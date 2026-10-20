package task09

// private/internal нельзя использовать в public inline-функции; @PublishedApi разрешает internal
@PublishedApi
internal var debugEnabled = false

// лямбда: сообщение строится только если debugEnabled == true
inline fun debug(message: () -> String) {
    if (debugEnabled) println("[DEBUG] ${message()}")
}

// обычная функция (шаг 4): аргумент вычисляется ДО вызова, всегда
fun debugEager(message: String) {
    if (debugEnabled) println("[DEBUG] $message")
}

fun buildReport(): String {
    println("(building the report...)")
    return "report with 1000 rows"
}

fun main() {
    print("Enable debug logging? (yes/no): ")
    debugEnabled = readln().trim().equals("yes", ignoreCase = true)
    println("-- lazy (lambda) --")
    debug { buildReport() }
    println("-- eager (String) --")
    debugEager(buildReport())
    println("Program finished")
}
