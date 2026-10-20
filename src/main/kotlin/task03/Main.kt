package task03

val contacts = listOf("Ali Karimov", "Dilnoza Yusupova", "Aliyah Smith", "Bobur Aliev")

fun findFirst(query: String) {
    contacts.forEach {
        if (it.contains(query, ignoreCase = true)) {
            println("First match: $it")
            return // non-local return: выходит из всей findFirst
        }
    }
    println("No contact contains '$query'")
}

fun findAll(query: String) {
    contacts.forEach {
        if (!it.contains(query, ignoreCase = true)) return@forEach // local return = continue
        println("Match: $it")
    }
}

// без inline здесь была бы ошибка "'return' is not allowed here"
inline fun each(items: List<String>, action: (String) -> Unit) {
    for (item in items) action(item)
}

fun findWithEach(query: String) {
    each(contacts) {
        if (it.contains(query, ignoreCase = true)) {
            println("First match (each): $it")
            return
        }
    }
}

fun main() {
    print("Search: ")
    val query = readln().trim()
    findFirst(query)
    findAll(query)
    findWithEach(query)
    println("Search finished")
}
