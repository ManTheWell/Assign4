import java.io.File
import kotlin.system.exitProcess

fun main() {
//    print("Type in the path to the ROM file: ")
//    val filepath = readLine() ?: exitProcess(-1)
    val filepath = "src/roms/test.d5700"

    try {
        val rom = ROM(File(filepath).readText())
        val cpu = CPU()

        cpu.run(rom)
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

}

