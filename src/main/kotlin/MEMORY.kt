interface MEMORY {
    fun read4At(address: Int) : String
    fun writeAt(address: Int, value: Int) : Boolean
}