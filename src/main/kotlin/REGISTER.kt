class REGISTER (private val name: String, private val sixteen: Boolean) {
    constructor(name: String) : this(name, false)

    private var value = 0

    fun write(newVal: Int) {
        value = newVal
    }

    fun read() : Int {
        return value
    }

    fun clear() {
        value = 0
    }
}
