class ROM (private val memory: String) : MEMORY {
    override fun read4At(address: Int) : String {
        if (address > memory.length || address + 4 > memory.length) return ""
        return memory.substring(address, address + 4)
    }

    override fun writeAt(address: Int, value: Int): Boolean {
        return false
    }
}