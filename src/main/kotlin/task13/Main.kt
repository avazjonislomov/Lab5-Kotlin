package task13

fun Any.typeName(): String = this::class.simpleName ?: "anonymous"

fun describe(value: Any?): String = when (value) {
    null -> "no value"
    is Int -> "a number, doubled it is ${value * 2}"
    is Double -> "a decimal, half of it is ${value / 2}"
    is Boolean -> if (value) "switched ON" else "switched OFF"
    is String -> "a text of ${value.length} characters"
    else -> "an unknown type: ${value.typeName()}"
}

fun main() {
    println(5.typeName())
    println("x".typeName())
    val settings: Map<String, Any?> = mapOf(
        "volume" to 7,
        "darkMode" to true,
        "username" to "Dilnoza",
        "theme" to null,
        "ratio" to 1.5,
    )
    println("Settings: ${settings.keys}")
    print("Setting name: ")
    val key = readln().trim()
    if (key !in settings) {
        println("No such setting")
        return
    }
    val value = settings[key]
    println("$key is ${describe(value)}")

    val asNumber = value as? Int
    println("as? Int gives: $asNumber")
    // value as String  -> ClassCastException для volume/darkMode/ratio, NullPointerException для theme
    // value as String? -> null для theme, ClassCastException для volume
    val asText = value as? String // безопасный каст: null вместо исключения
    println("as? String gives: $asText")
}
