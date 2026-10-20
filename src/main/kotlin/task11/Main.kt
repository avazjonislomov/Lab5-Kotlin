package task11

typealias Validator = (String) -> Boolean
typealias Predicate<T> = (T) -> Boolean
typealias Rule = Predicate<String>

val notBlank: Validator = { it.isNotBlank() }
val isEmail: Validator = { '@' in it && '.' in it.substringAfter('@') }
val isStrongPassword: Validator = { it.length >= 8 && it.any(Char::isDigit) }
val startsWithCode: Rule = { it.startsWith("+998") }

fun check(label: String, input: String, validator: Validator) {
    println("$label: ${if (validator(input)) "OK" else "INVALID"}")
}

fun <T> countMatching(items: List<T>, rule: Predicate<T>): Int = items.count(rule)

typealias Meters = Double
typealias Feet = Double

fun toFeet(length: Meters): Feet = length * 3.28084

fun describeRoom(width: Meters) {
    println("Room width: $width m")
}

fun roomDemo() {
    print("Room width in meters: ")
    val width: Meters = readln().trim().toDouble()
    val feet: Feet = toFeet(width)
    println("That is $feet feet")
    describeRoom(feet) // компилируется: Meters и Feet — это один и тот же тип Double
}

fun main() {
    print("E-mail: ")
    val email = readln().trim()
    print("Password: ")
    val password = readln()
    check("E-mail", email, isEmail)
    check("Password", password, isStrongPassword)
    check("Not blank", email) { it.isNotBlank() } // обычная лямбда -> параметр типа Validator
    val plain: (String) -> Boolean = isEmail      // Validator -> обычный тип функции
    println("Used as a plain function type: ${plain(email)}")
    println("Valid e-mails in the list: ${countMatching(listOf(email, "x@y.uz", "bad"), isEmail)}")

    val phones = List(3) { i ->
        print("Phone ${i + 1}: ")
        readln().trim()
    }
    println("Phones with +998: ${countMatching(phones, startsWithCode)}")

    roomDemo()
}
