package task01

// inline: тело ask и тело лямбды копируются в main — нет объекта лямбды и нет Ref.IntRef
inline fun <T> ask(prompt: String, parse: (String) -> T?): T {
    while (true) {
        print(prompt)
        val value = parse(readln().trim())
        if (value != null) return value
        println("Invalid input, try again.")
    }
}

fun main() {
    var attempts = 0
    val age = ask("Your age (0-120): ") { text ->
        attempts++
        text.toIntOrNull()?.takeIf { it in 0..120 }
    }
    println("Age accepted: $age after $attempts attempt(s)")
}
