package task06

inline fun <reified T> readValue(prompt: String): T? {
    print(prompt)
    val text = readln().trim()
    return when (T::class) {
        Int::class -> text.toIntOrNull() as T?
        Long::class -> text.toLongOrNull() as T?
        Double::class -> text.toDoubleOrNull() as T?
        Boolean::class -> text.toBooleanStrictOrNull() as T?
        String::class -> text as T?
        else -> error("Unsupported type ${T::class.simpleName}")
    }
}

fun main() {
    val age = readValue<Int>("Age: ")
    val height = readValue<Double>("Height in meters: ")
    val subscribed = readValue<Boolean>("Newsletter (true/false): ")
    val city = readValue<String>("City: ")
    val phone = readValue<Long>("Phone: ")
    println("age=$age, height=$height, subscribed=$subscribed, city=$city, phone=$phone")
}
