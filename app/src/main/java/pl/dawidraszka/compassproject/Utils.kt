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