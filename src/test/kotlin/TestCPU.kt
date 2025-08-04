import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TestCPU {
    @Test
    fun store() {
        val cpu = CPU()

        var registers = cpu.test("0001")
        assertEquals(1, registers[0].read())

        registers = cpu.test("0110")
        assertEquals(16, registers[1].read())

        registers = cpu.test("07FF")
        assertEquals(255, registers[7].read())
    }

    @Test
    fun add() {
        val cpu = CPU()

        cpu.test("0001")
        cpu.test("0101")

        val registers = cpu.test("1012")
        assertEquals(2, registers[2].read())
    }

    @Test
    fun sub() {
        val cpu = CPU()

        cpu.test("0002")
        cpu.test("0101")

        val registers = cpu.test("2012")
        assertEquals(1, registers[2].read())
    }
}
