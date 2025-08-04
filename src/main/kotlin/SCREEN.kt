class SCREEN {
    private val ram = ScreenRAM(64)

    fun writeAt(char: Char, row: Int, column: Int) {

        ram.writeAt(column + (row * 8), char)
    }

    fun printScreen() {
        print("__________\n|")

        for (i in 0..63) {
            if (i % 8 == 0 && i > 0)
                print("|\n|")

            print(ram.getCharAt(i))
        }

        println("|\n----------")
    }
}
