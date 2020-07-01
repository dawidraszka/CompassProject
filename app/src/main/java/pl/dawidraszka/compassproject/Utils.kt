package pl.dawidraszka.compassproject

const val ALPHA = 0.25f

fun lowPassFilter(
    input: FloatArray,
    previousValue: FloatArray
): FloatArray {
    val output = FloatArray(input.size)
    for (i in input.indices) {
        output[i] = previousValue[i] + ALPHA * (input[i] - previousValue[i])
    }
    return output
}

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