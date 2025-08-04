class ScreenRAM (size: Int) {
    private val ram = Array(size) { ' ' }

    fun getCharAt(address: Int): Char {
        return ram[address]
    }

    fun writeAt(address: Int, value: Char) {
        if (address > ram.size) println("Index out of bounds: $address > ${ram.size}")
        ram[address] = value
    }
}
