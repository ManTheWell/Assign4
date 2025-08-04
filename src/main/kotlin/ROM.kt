class ROM (private val memory: String) : MEMORY {
    override fun read4At(address: Int) : String {
        return memory.substring(address, address + 4)
    }

    override fun writeAt(address: Int): Boolean {
        return false
    }
}