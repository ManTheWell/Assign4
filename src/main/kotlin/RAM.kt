class RAM (size: Int) {
    val ram = Array(size) {}

    fun getBitAt(address: Int) {
        return ram[address]
    }
}
