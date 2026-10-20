package task05

// без inline: "'continue' is not allowed here" — лямбда стала бы объектом
inline fun orSkip(text: String?, onNull: () -> Double): Double = text?.toDoubleOrNull() ?: onNull()

fun main() {
    val prices = mutableListOf<Double>()
    println("Enter prices, one per line. Type 'done' to finish.")
    while (true) {
        val line = readln().trim()
        if (line == "done") break
        val price = orSkip(line) {
            println("'$line' is not a number, skipped")
            continue
        }
        price.takeIf { it > 10_000 }?.let {
            println("Price $it is too large, stopping")
            break
        }
        prices.add(price)
    }
    println("Items: ${prices.size}, total: ${prices.sum()}")
}
