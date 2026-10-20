package task02

val pending = mutableListOf<() -> Unit>()

// reminder сохраняется в список, поэтому он должен быть настоящим объектом -> noinline
inline fun addTask(save: () -> Unit, noinline reminder: () -> Unit) {
    save()
    pending.add(reminder)
    // pending.add(save) // ошибка: Illegal usage of inline-parameter 'save'
}

fun main() {
    print("Task name: ")
    val name = readln().trim()
    addTask(
        save = { println("Saved: $name") },
        reminder = { println("Reminder: finish '$name'") },
    )
    println("Pending reminders: ${pending.size}")
    pending.forEach { it() }
}
