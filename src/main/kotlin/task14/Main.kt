package task14

fun fail(message: String): Nothing = throw IllegalArgumentException(message)

fun readAge(): Int {
    print("Age: ")
    val age = readln().trim().toIntOrNull() ?: fail("Age must be a whole number") // тип age: Int
    if (age < 0) fail("Age cannot be negative")
    return age
}

fun main() {
    try {
        val age = readAge()
        println("You will be ${age + 1} next year")
    } catch (e: IllegalArgumentException) {
        println("Input error: ${e.message}")
    }
}
