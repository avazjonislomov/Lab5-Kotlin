package task04

// action вызывается внутри объекта Runnable (другой контекст) -> crossinline
inline fun command(crossinline action: () -> Unit): Runnable =
    Runnable { action() }

fun main() {
    val commands = mapOf(
        "1" to command { println("Hello!") },
        "2" to command { println("Goodbye!") },
        "3" to command { println("Time to rest.") },
        "4" to command { println("Settings opened") },
    )
    print("Choose 1, 2, 3 or 4: ")
    val chosen = commands[readln().trim()]
    if (chosen == null) println("Unknown command") else chosen.run()
}
