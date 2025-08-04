import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CPUTest {
    private lateinit var cpu: CPU

    @BeforeEach
    fun setUp() {
        cpu = CPU()
    }

    @Test
    fun `test store instruction`() {
        cpu.test("00FF") // store 0xFF in r0
        assertEquals(0xFF, cpu.test("0700")[0].read())
    }

    @Test
    fun `test add instruction`() {
        cpu.test("0105") // r1 = 0x05
        cpu.test("0203") // r2 = 0x03
        cpu.test("1120") // r1 + r2 -> r0
        assertEquals(8, cpu.test("0700")[0].read())
    }

    @Test
    fun `test sub instruction`() {
        cpu.test("0308") // r3 = 0x08
        cpu.test("0403") // r4 = 0x03
        cpu.test("2345") // r3 - r4 -> r5
        assertEquals(0x05, cpu.test("0700")[5].read())
    }

    @Test
    fun `test jump instruction`() {
        cpu.test("A010") // set A to 0x010
        cpu.test("5010") // jump to 0x010
        assertEquals(0x010, cpu.getP().read())
    }

    @Test
    fun `test memory switch`() {
        val before = cpu.getM()
        cpu.test("7000")
        assertNotEquals(before, cpu.getM())
    }

    @Test
    fun `test base 10 conversion`() {
        cpu.test("010A") // r1 = 0x0A = 10
        cpu.test("A010") // A = 0x010
        cpu.test("D100") // convert r1 to base 10 -> 010: 0, 011: 1, 012: 0
        assertEquals(0, cpu.getRam().readAt(0x010))
        assertEquals(1, cpu.getRam().readAt(0x011))
        assertEquals(0, cpu.getRam().readAt(0x012))
    }

    @Test
    fun `test convertByteToAscii for digit`() {
        cpu.test("0105") // r1 = 5
        cpu.test("E120") // r1 to ascii -> r2
        assertEquals('5'.code, cpu.test("0000")[2].read())
    }

    @Test
    fun `test convertByteToAscii for hex letter`() {
        cpu.test("010F") // r1 = 15
        cpu.test("E120") // r1 to ascii -> r2
        assertEquals('F'.code, cpu.test("0000")[2].read())
    }

    @Test
    fun `test read and write`() {
        cpu.test("010C") // r1 = 0x0C
        cpu.test("A020") // A = 0x020
        cpu.test("4010") // write r1 to RAM at A
        cpu.test("3010") // read RAM[A] into r1
        assertEquals(0x0C, cpu.test("0000")[1].read())
    }

    @Test
    fun `test skipEqual`() {
        cpu.test("010A") // r1 = 0x0A
        cpu.test("020A") // r2 = 0x0A
        val before = cpu.getP().read()
        cpu.test("8120") // skip next if r1 == r2
        assertEquals(before + 12, cpu.getP().read())
    }

    @Test
    fun `test skipNotEqual`() {
        cpu.test("010A") // r1 = 0x0A
        cpu.test("020B") // r2 = 0x0B
        val before = cpu.getP().read()
        cpu.test("9120") // skip next if r1 != r2
        assertEquals(before + 12, cpu.getP().read())
    }

    @Test
    fun `test setT and readT`() {
        cpu.test("B01F") // T = 0x01
        cpu.test("C10F") // read T into r1
        assertEquals(0x01, cpu.test("0000")[1].read())
    }

    @Test
    fun `test draw`() {
        cpu.test("0102") // r1 = 0x02 ('\u0002')
        cpu.test("F112") // draw r1 at row 1 col 2
        // visually confirm screen or use screen buffer if exposed
    }

    @Test
    fun `SET_T should correctly assign value to T register`() {
        cpu.test("B0A0") // Set T = 0x0A

        val registers = cpu.test("C000") // Read T into r0
        assertEquals(0x0A, registers[0].read())
    }

    @Test
    fun `SET_T should handle max hex value`() {
        cpu.test("BFF0") // Set T = 0xFF

        val registers = cpu.test("C100") // Read T into r1
        assertEquals(0xFF, registers[1].read())
    }

    @Test
    fun `READ_T should allow writing T to any register`() {
        cpu.test("B1C0") // Set T = 0x1C

        repeat(8) { index ->
            val instruction = "C${index.toString(16).uppercase()}00" // CrX00
            val result = cpu.test(instruction)
            assertEquals(0x1C, result[index].read())
        }
    }
}