package task15

import kotlin.system.exitProcess

fun fail(message: String): Nothing = throw IllegalStateException(message)

fun discountFor(code: String): Int = when (code.uppercase()) {
    "STUDENT" -> 20
    "FRIEND" -> 10
    else -> fail("Unknown coupon '$code'")
}

fun runCalculator(): Nothing {
    var total = 0
    while (true) {
        print("> ")
        val parts = readln().trim().split(" ")
        val number = parts.getOrNull(1)?.toIntOrNull()
        when (parts[0]) {
            "add" -> total += number ?: 0
            "sub" -> total -= number ?: 0
            "mul" -> total *= number ?: 1
            "reset" -> total = 0
            "show" -> println("Total = $total")
            "exit" -> {
                println("Bye, final total = $total")
                exitProcess(0)
            }
            else -> println("Unknown command '${parts[0]}'")
        }
    }
}

fun main() {
    val none: List<Nothing> = emptyList()
    val names: List<String> = none
    val ages: List<Int> = none
    println("${names.size} names, ${ages.size} ages")

    print("Coupon: ")
    println("Discount: ${discountFor(readln().trim())}%")

    println("Commands: add N, sub N, mul N, reset, show, exit")
    runCalculator()
    println("This line is never reached")
}
