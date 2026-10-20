package task07

inline fun <reified T> List<Any>.onlyOfType(): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (item is T) result.add(item)
    }
    return result
}

fun parseToken(token: String): Any =
    token.toIntOrNull() ?: token.toDoubleOrNull() ?: token.toBooleanStrictOrNull() ?: token

fun main() {
    print("Items separated by spaces (for example: 10 apples 2.5 7): ")
    val items: List<Any> = readln().trim().split(" ")
        .filter { it.isNotEmpty() }
        .map(::parseToken)
    println("All:      $items")
    println("Ints:     ${items.onlyOfType<Int>()}")
    println("Doubles:  ${items.onlyOfType<Double>()}")
    println("Words:    ${items.onlyOfType<String>()}")
    println("Booleans: ${items.onlyOfType<Boolean>()}")
    println("filterIsInstance<Int>: ${items.filterIsInstance<Int>()}")
}
