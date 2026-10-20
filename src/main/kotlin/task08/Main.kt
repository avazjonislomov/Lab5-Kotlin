package task08

const val VAT_RATE = 0.12

// inline val taxRate: Double = 0.12 // ошибка: у inline-свойства не может быть backing field

inline val Double.withVat: Double
    get() = this * (1 + VAT_RATE)

val String.wordCount: Int
    inline get() = split(" ").count { it.isNotBlank() }

inline val Int.kilobytes: Int
    get() = this * 1024

fun main() {
    print("Price without VAT: ")
    val price = readln().trim().toDoubleOrNull()
    if (price == null) {
        println("Not a number")
        return
    }
    println("Price with VAT: ${"%.2f".format(price.withVat)}")
    print("Write a sentence: ")
    val sentence = readln()
    println("Words: ${sentence.wordCount}")
    print("Size in KB: ")
    val kb = readln().trim().toIntOrNull() ?: 0
    println("$kb KB = ${kb.kilobytes} bytes")
}
