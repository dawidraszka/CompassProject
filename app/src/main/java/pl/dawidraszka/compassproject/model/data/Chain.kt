package pl.dawidraszka.compassproject.model.data

class Chain(size: Int) {
    private val elements = IntArray(size)
    private var counter = 0

    fun addElement(element: Int) {
        if (counter == elements.size)
            counter = 0

        elements[counter] = element
        counter++
    }

    fun average(): Int {
        var sum = 0f
        elements.forEach { sum += it }
        return (sum / elements.size).toInt()
    }
}