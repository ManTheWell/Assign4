class RAM (size: Int) {
    private val ram = Array(size) { 0 }

    fun readAt(address: Int): Int {
        return ram[address]
    }

    fun writeAt(address: Int, value: Int) {
        ram[address] = value
    }
}