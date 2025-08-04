import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class CPU {
    private lateinit var memory : MEMORY

    private val registers = arrayOf(
        REGISTER("r0"),
        REGISTER("r1"),
        REGISTER("r2"),
        REGISTER("r3"),
        REGISTER("r4"),
        REGISTER("r5"),
        REGISTER("r6"),
        REGISTER("r7")
    )

    private val P = REGISTER("P", true)
    private val T = REGISTER("T")
    private val A = REGISTER("A", true)
    private var M = false

    private val screen = SCREEN()

    private val ram = RAM(4000)

    fun run(memory: MEMORY) {
        this.memory = memory
        P.clear()

        val executor = Executors.newSingleThreadScheduledExecutor()

        val instructionRunnable = Runnable {
            val instruction = memory.read4At(P.read())

            if (instruction == "0000" || instruction == "") {
                println("\nProgram complete!")
                exitProcess(0)
            }

            execute(instruction)
        }

        val scheduledInstructions = executor.scheduleAtFixedRate(
            instructionRunnable,
            0,
            2,
            TimeUnit.MILLISECONDS
        )

        val scheduledTimerDecrease = Runnable {
            if (T.read() > 0)
                T.write(T.read() - 1)
        }

        val scheduledDecreaseTimer = executor.scheduleAtFixedRate(
            scheduledTimerDecrease,
            0,
            16,
            TimeUnit.MILLISECONDS
        )
    }

    private fun execute(instruction: String) {
        if (instruction.length != 4) {
            println("\nIncorrect instructions length: $instruction")
            exitProcess(-1)
        }

        when (instruction[0].uppercaseChar()) {
            '0' -> store(instruction)
            '1' -> add(instruction)
            '2' -> sub(instruction)
            '3' -> read(instruction)
            '4' -> write(instruction)
            '5' -> jump(instruction)
            '6' -> readKeyboard(instruction)
            '7' -> switchMemory()
            '8' -> skipEqual(instruction)
            '9' -> skipNotEqual(instruction)
            'A' -> setA(instruction)
            'B' -> setT(instruction)
            'C' -> readT(instruction)
            'D' -> convertToBase10(instruction)
            'E' -> convertByteToAscii(instruction)
            'F' -> draw(instruction)
            else -> {
                println("\nUnknown instruction: ${instruction[0].uppercaseChar()}")
                exitProcess(-1)
            }
        }

        screen.printScreen()
     }

    private fun incrementProgramCounter() {
        P.write(P.read() + 4)
    }

    private fun store(instruction: String) {
        registers[instruction[1].digitToInt()].write(instruction.substring(2, 4).toInt(16))

        incrementProgramCounter()
    }

    private fun add(instruction: String) {
        val a = registers[instruction[1].digitToInt()].read()
        val b = registers[instruction[2].digitToInt()].read()

        registers[instruction[3].digitToInt()].write(a + b)

        incrementProgramCounter()
    }

    private fun sub(instruction: String) {
        val a = registers[instruction[1].digitToInt()].read()
        val b = registers[instruction[2].digitToInt()].read()

        registers[instruction[3].digitToInt()].write(a - b)

        incrementProgramCounter()
    }

    private fun read(instruction: String) {
        registers[instruction[1].digitToInt()].write(ram.readAt(A.read()))

        incrementProgramCounter()
    }

    private fun write(instruction: String) {
        if (M)
            memory.writeAt(A.read(), registers[instruction[1].digitToInt()].read())
        else
            ram.writeAt(A.read(), registers[instruction[1].digitToInt()].read())

        incrementProgramCounter()
    }

    private fun jump(instruction: String) {
        val location = instruction.substring(1, 4).toInt(16)

        if (location % 2 != 0) {
            println("Program location is not divisible by 2: ${P.read()}")
            exitProcess(-1)
        }

        P.write(location)
    }

    private fun readKeyboard(instruction: String) {
        print("Enter hex input (0–F): ")
        val input = readLine()?.take(2)?.uppercase() ?: ""

        val value = input.toIntOrNull(16) ?: 0

        registers[instruction[1].digitToInt()].write(value)

        incrementProgramCounter()
    }

    private fun switchMemory() {
        M = !M

        incrementProgramCounter()
    }

    private fun skipEqual(instruction: String) {
        val rX = instruction[1].digitToInt()
        val rY = instruction[2].digitToInt()

        if (registers[rX].read() == registers[rY].read()) {
            incrementProgramCounter()
            // increase twice to skip the next instruction set
            incrementProgramCounter()
        }

        incrementProgramCounter()
    }

    private fun skipNotEqual(instruction: String) {
        val rX = instruction[1].digitToInt()
        val rY = instruction[2].digitToInt()

        if (registers[rX].read() != registers[rY].read()) {
            incrementProgramCounter()
            // increase twice to skip the next instruction set
            incrementProgramCounter()
        }

        incrementProgramCounter()
    }

    private fun setA(instruction: String) {
        A.write(instruction.substring(1, 4).toInt(16))

        incrementProgramCounter()
    }

    private fun setT(instruction: String) {
        T.write(instruction.substring(1, 3).toInt(16))

        incrementProgramCounter()
    }

    private fun readT(instruction: String) {
        val rX = instruction[1].digitToInt()
        registers[rX].write(T.read())

        incrementProgramCounter()
    }

    private fun convertToBase10(instruction: String) {
        val value = registers[instruction[1].digitToInt()].read()

        val hundreds = (value / 100) % 10
        val tens = (value / 10) % 10
        val ones = value % 10

        if (M) {
            memory.writeAt(A.read(), hundreds)
            memory.writeAt(A.read() + 1, tens)
            memory.writeAt(A.read() + 2, ones)
        }
        else {
            ram.writeAt(A.read(), hundreds)
            ram.writeAt(A.read() + 1, tens)
            ram.writeAt(A.read() + 2, ones)
        }

        incrementProgramCounter()
    }

    private fun convertByteToAscii(instruction: String) {
        val rX = instruction[1].digitToInt()
        val rY = instruction[2].digitToInt()

        val value = registers[rX].read()
        if (value > 16) {
            println("Value larger than 16 (F): $value")
            exitProcess(-1)
        }

        val ascii = if (value < 10) '0' + value else 'A' + (value - 10)

        registers[rY].write(ascii.code)

        incrementProgramCounter()
    }

    private fun draw(instruction: String) {
        val rX = instruction[1].digitToInt()
        val row = instruction[2].digitToInt()
        val col = instruction[3].digitToInt()

        val charValue = registers[rX].read()
        if (charValue > 127) {
            println("Char value $charValue too large (> 127)")
            exitProcess(-1)
        }

        screen.writeAt(charValue.toChar(), row, col)

        incrementProgramCounter()
    }

    // MAKE THINGS PUBLIC FOR TESTING PURPOSES:

    fun test(instruction: String): Array<REGISTER> {
        execute(instruction)
        return registers
    }

    fun getP(): REGISTER {
        return P
    }

    fun getM() :Boolean {
        return M
    }

    fun getRam(): RAM {
        return ram
    }
}
