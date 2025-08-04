import java.util.concurrent.Executors
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
    private val M = true

    fun run(memory: MEMORY) {
        this.memory = memory
        P.clear()
        next()

//        val executor = Executors.newSingleThreadScheduledExecutor()
//
//        val runnable = Runnable {
//            next()
//        }
//
//        val cpuFuture = executor.scheduleAtFixedRate(
//            runnable,
//            0,
//            1000L / 500L, // repeat frequency - every 2 ms
//            TimeUnit.MILLISECONDS
//        )
    }

    private fun next() {
        val instruction = memory.read4At(P.read())
        execute(instruction)
    }

    private fun execute(instruction: String) {
        if (instruction.length != 4)
            exitProcess(-1)

        when (instruction[0]) {
            '0' -> store(instruction)
            '1' -> add(instruction)
            '2' -> sub(instruction)
            '3' -> read(instruction)
        }
     }

    private fun store(instruction: String) {
        registers[instruction[1].digitToInt()].write(instruction.substring(2, 4).toInt(16))
    }

    private fun add(instruction: String) {
        val a = registers[instruction[1].digitToInt()].read()
        val b = registers[instruction[2].digitToInt()].read()

        registers[instruction[3].digitToInt()].write(a + b)
    }

    private fun sub(instruction: String) {
        val a = registers[instruction[1].digitToInt()].read()
        val b = registers[instruction[2].digitToInt()].read()

        registers[instruction[3].digitToInt()].write(a - b)
    }

    private fun read(instruction: String) {
        registers[instruction[3].digitToInt()].write(memory.read4At(A.read()).toInt(16))
    }

    private fun write(instruction: String) {
        if (!M) return

        memory.writeAt(A.read(), registers[instruction[1].digitToInt()].read())
    }

    private fun jump(instruction: String) {
        val location = instruction.substring(1, 4).toInt(16)

        if (location % 2 != 0) exitProcess(-1)

        P.write(location)
    }

    fun test(instruction: String): Array<REGISTER> {
        execute(instruction)
        return registers
    }
}
