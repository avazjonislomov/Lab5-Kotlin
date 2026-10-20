# Лабораторная 5 — Kotlin Special Keywords: ответы

Код: `src/main/kotlin/taskNN/Main.kt` (финальное состояние после всех шагов).
Весь код скомпилирован компилятором Kotlin 2.1.21 и запущен с входными данными из задания;
сообщения об ошибках ниже — реальный вывод компилятора (в IDE формулировки могут немного отличаться).

> Task 5 требует Kotlin 2.2. В Kotlin 2.1.x добавьте аргумент компилятора `-Xnon-local-break-continue`
> (File → Settings → Build → Compiler → Kotlin Compiler → Additional command line parameters).

---

## Part A — Inline functions

### Task 1 — inline
**Вывод** (abc, 200, 35):
```
Your age (0-120): Invalid input, try again.
Your age (0-120): Invalid input, try again.
Your age (0-120): Age accepted: 35 after 3 attempt(s)
```
1. **3 попытки.** Для `abc` — `toIntOrNull()` возвращает `null` (не число); для `200` — число есть, но `takeIf { it in 0..120 }` возвращает `null`, т.к. 200 вне диапазона.
2. **С `inline`** в декомпилированном `main` **нет** вызова `ask(...)` и **нет** `Ref.IntRef`: тело `ask` и тело лямбды скопированы прямо в `main`, `attempts` — обычная локальная `int`.
3. **Без `inline`** в `main` появился вызов `ask("...", лямбда)`, объект лямбды (класс `Function1` / invokedynamic) и `Ref.IntRef attempts = new Ref.IntRef()`. Лямбда теперь — отдельный объект, который живёт вне `main`, а захватить и *изменять* локальную переменную Java может только через объект-обёртку на куче — это и есть `Ref.IntRef` (closure).
4. **Ответ другу:** inline выгоден для маленьких функций, принимающих лямбды — исчезают объект лямбды, замыкание и лишний вызов. Для 200-строчной функции выигрыша нет, а тело копируется в каждое место вызова — растёт размер байткода (code bloat).

### Task 2 — noinline
1. Ошибка: `Illegal usage of inline parameter 'reminder: () -> Unit'. Add 'noinline' modifier to the parameter declaration.` — называет параметр `reminder` и предлагает `noinline`.
2. Исправление: `noinline reminder: () -> Unit`. Вывод для `Write report`:
   ```
   Saved: Write report
   Pending reminders: 1
   Reminder: finish 'Write report'
   ```
   Сначала `save` выполняется сразу внутри `addTask`, потом печатается количество, и только потом вызывается сохранённый `reminder`.
3. `pending.add(save)` → `Illegal usage of inline parameter 'save'...`. Inline-лямбда **не существует как объект** — её код просто вставлен в место вызова. Вызвать её можно (код подставится), а положить в список нечего.
4. Код `reminder` живёт **в объекте** (класс лямбды), он не скопирован в `main`. Если бы он был заинлайнен, не было бы объекта, который можно хранить в списке и вызвать позже, после возврата из `addTask`.

### Task 3 — non-local return
1. `ali`: `findFirst` печатает одну строку (`First match: Ali Karimov`), потому что `return` — **non-local**, он выходит из всей `findFirst`. `findAll` печатает три строки (Ali Karimov, Aliyah Smith, Bobur Aliev), т.к. `return@forEach` выходит только из текущего вызова лямбды. `xyz`: `No contact contains 'xyz'`, `findAll` ничего не печатает.
2. `return@forEach` — **локальный возврат** из лямбды: пропускает текущий элемент и переходит к следующему. Это аналог `continue` в обычном `for`.
3. После совпадения никогда не печатается `No contact contains '...'` — `return` завершил `findFirst`. `Search finished` всё равно печатается, потому что `return` выходит только из `findFirst`, а не из `main`.
4. Ошибка: `'return' is prohibited here.` Наша `each` — **не inline**: лямбда становится объектом и может быть вызвана когда угодно, поэтому выйти из `findWithEach` она не может. Стандартная `forEach` объявлена как `inline`.
5. **Правило:** голый `return` разрешён в лямбде, когда эта лямбда передана в **inline-функцию** (как inline-параметр, т.е. не `noinline`/`crossinline`).

### Task 4 — crossinline
1. Ошибка: `Cannot inline 'action: () -> Unit' here: it might contain non-local returns. Add 'crossinline' modifier to parameter declaration 'action: () -> Unit'.` — параметр `action`, модификатор `crossinline`.
2. `2` → `Goodbye!`, `9` → `Unknown command`.
3. `command { println("Hello!"); return }` → `'return' is prohibited here.` В Task 3 лямбда выполнялась **сразу**, внутри функции, из которой выходит `return`. Здесь лямбда вызывается внутри `Runnable` — потом, когда `main` может уже давно закончиться (или в другом потоке), поэтому выйти из `main` невозможно. `crossinline` — это обещание: «non-local return не будет».
4. Добавлена команда `"4" to command { println("Settings opened") }` → `Settings opened`.

### Task 5 — break / continue в лямбдах
1. Ввод `10, abc, 25.5, done` → `'abc' is not a number, skipped`, `Items: 2, total: 35.5`.
   Ввод `10, abc, 20000, 5` → `'abc' ... skipped`, `Price 20000.0 is too large, stopping`, `Items: 1, total: 10.0`. **5 не читается** — цикл уже остановлен `break`.
2. `continue` внутри лямбды `run { ... }`, `break` внутри лямбды `let { ... }`. Оба `run` и `let` — inline. Оба прыжка относятся к циклу `while (true)`: `continue` переходит к следующей итерации, `break` выходит из него. (`break` в строке `if (line == "done") break` — не в лямбде.)
3. С не-inline `orSkip`: `'break' or 'continue' crosses a function or class boundary.` `run` — inline, `orSkip` — обычная функция: её лямбда — объект, отдельная функция, и из неё нельзя прыгнуть в цикл вызывающего кода.
4. Да, с `inline` ошибка исчезает. Лекция: *break и continue можно использовать в лямбде, переданной в inline-функцию, если вызов находится внутри цикла* (так же, как non-local return).

---

## Part B — reified, inline-свойства, @PublishedApi

### Task 6 — reified
1. `25, 1.75, true, Samarkand` → `age=25, height=1.75, subscribed=true, city=Samarkand`.
   `x, 1,75, yes, ""` → `age=null, height=null, subscribed=null, city=` — `x` не Int, `1,75` не Double (нужна точка), `yes` не строгий Boolean (`toBooleanStrictOrNull` принимает только `true`/`false`). `city` — пустая строка, но **не null**: для `String` ничего не парсится, `text` возвращается как есть.
2. Без `reified`: `Cannot use 'T' as reified type parameter. Use a class instead.` — в двух местах: `T::class` в `when (T::class)` и `T::class.simpleName` в `error(...)`. (Касты `as T?` — только unchecked-предупреждения.)
3. `readValue<Char>` **компилируется**. Проблема возникает **во время выполнения**: `IllegalStateException: Unsupported type Char`, сообщение создаёт строка `else -> error("Unsupported type ${T::class.simpleName}")`.
4. Добавлено `Long::class -> text.toLongOrNull() as T?`; `998901234567` → `phone=998901234567` (в Int не поместилось бы).

### Task 7 — reified: фильтр по типу
1. `10 apples 2.5 7 pears 0.75` → Ints `[10, 7]`, Doubles `[2.5, 0.75]`, Words `[apples, pears]`. `7` — Int, потому что `parseToken` **сначала** пробует `toIntOrNull()`, и только если это `null`, переходит к `toDoubleOrNull()`.
2. Без `reified`: `Cannot check for instance of erased type 'T'`. **Erased type** — на JVM обобщённые типы стираются при компиляции: во время выполнения `List<T>` — просто `List`, и проверить `item is T` невозможно, т.к. неизвестно, что такое `T`. `reified` + `inline` подставляет реальный тип в каждое место вызова.
3. `filterIsInstance<Int>()` даёт тот же результат `[10, 7]`. Модификаторы: **`inline`** и **`reified`** (`public inline fun <reified R> Iterable<*>.filterIsInstance(): List<R>`).
4. `parseToken` дополнен `?: token.toBooleanStrictOrNull()`. `true 5 false x` → Booleans `[true, false]`, Ints `[5]`, Words `[x]`.

### Task 8 — inline-свойства
1. `100` и `the quick  brown fox` → `Price with VAT: 112.00` (в русской локали `112,00`), `Words: 4` (пустая строка между двумя пробелами отбрасывается `isNotBlank`).
2. `withVat` помечено `inline` целиком (`inline val`), у `wordCount` — только getter (`inline get()`). Работают одинаково: у `val` есть только getter, так что в обоих случаях заинлайнен getter.
3. `inline val taxRate: Double = 0.12` → `Inline property cannot have a backing field.` Inline-аксессор копируется в место вызова; поле же — это хранилище в памяти, его нельзя «скопировать». Inline может быть только свойство, значение которого вычисляется кодом.
4. `inline val Int.kilobytes: Int get() = this * 1024` → `5 KB = 5120 bytes`.

### Task 9 — @PublishedApi
1. С `private`: `Public-API inline function cannot access non-public-API property.`
2. С `internal` — **та же ошибка** (internal — тоже не публичный API: другой модуль его не видит).
3. С `@PublishedApi internal` — компилируется. `yes` → печатается `(building the report...)` и `[DEBUG] report with 1000 rows`. `no` → ничего, т.к. лямбда `message` вызывается только внутри `if (debugEnabled)`; при `no` она не вызывается и отчёт не строится.
4. Обычная функция `debug(message: String)` при `no` всё равно печатает `(building the report...)`. **Объяснение:** аргумент-строка вычисляется *до* вызова функции, поэтому `buildReport()` выполняется всегда, даже когда сообщение не нужно. С лямбдой вычисление откладывается и происходит только если логирование включено — а `inline` делает это бесплатным (без объекта лямбды).
   (В финальном коде обе версии: `debug { }` и `debugEager(...)`.)

---

## Part C — typealias, Any, Nothing

### Task 10 — typealias
1. Отклонены `bad` (`parts.size != 2`) и `Bobur x` (`grade == null`, т.к. `"x".toIntOrNull()` = null). Итог: `Ali: 85, 70 average = 77.5`, `Dilnoza: 90 average = 90.0`.
2. IDE показывает `Gradebook`, а развёрнутый тип: `MutableMap<String, MutableList<Int>>`.
3. С `MutableMap<String, MutableList<Int>>.addGrade` программа работает так же: **alias — не новый тип**, а другое имя того же типа, они полностью взаимозаменяемы.
4. Добавлены `typealias Grades = MutableList<Int>`, `typealias Gradebook = MutableMap<String, Grades>`, `fun Grades.best(): Int? = maxOrNull()` → `best = 85`, `best = 90`.

### Task 11 — typealias для функций
1. `a@b.uz / secret123` → все OK, `Valid e-mails in the list: 2`. `abc / short` → E-mail INVALID, Password INVALID, Not blank OK, list: 1.
2. Обычная лямбда → `Validator`: `check("Not blank", email) { it.isNotBlank() }`. `Validator` → обычный тип: `val plain: (String) -> Boolean = isEmail`. Обе компилируются, потому что компилятор заменяет `Validator` на `(String) -> Boolean` — это **один и тот же тип**.
3. `roomDemo` с `4`: `That is 13.12336 feet`, затем `Room width: 13.12336 m` — неверно! Компилятор молчит, потому что `Meters` и `Feet` — это оба просто `Double`. Защитить может **value class**: `@JvmInline value class Meters(val value: Double)` — это настоящий отдельный тип (без накладных расходов во время выполнения), и передать `Feet` вместо `Meters` уже не скомпилируется.
4. `typealias Rule = Predicate<String>`, `val startsWithCode: Rule = { it.startsWith("+998") }`, `countMatching(phones, startsWithCode)` — для `+998901, +7999, +998777` → `2`.

### Task 12 — Any: equals / hashCode / toString
1. С исходным классом `println(a)` печатает что-то вроде `task12.Contact@1b6d3586` — это `toString()` из класса **`Any`** (имя класса + `@` + hashCode в hex).
2. `a == b` → `false`: `==` вызывает `a.equals(b)`, а `equals` из `Any` сравнивает **ссылки** — это два разных объекта. `===` всегда сравнивает ссылки (тот же ли это объект). `book contains b` → тоже `false`.
3. С переопределёнными методами (ввод тот же):
   - `println(a)` → `Ali (991234567)` (наш `toString`)
   - `a == b` → `true` (сравнение по телефону)
   - `a === b` → `false` (без изменений — это всё ещё разные объекты)
   - `book contains b` → `true` (одинаковый `hashCode` → та же корзина в HashSet, затем `equals` = true)
4. Без `hashCode`: `b in book` почти всегда `false` (иногда случайно `true`). `HashSet` сначала ищет по `hashCode` корзину и только в ней вызывает `equals`; у `a` и `b` разные хеши (по умолчанию — от адреса), поэтому до `equals` дело не доходит. **Контракт:** если `a == b`, то `a.hashCode() == b.hashCode()`.
5. `equals` объявлен в `Any` как `equals(other: Any?)` — переопределение должно иметь ту же сигнатуру; сравнить можно с чем угодно, в т.ч. с `null`. `other is Contact` при `other == null` даёт **`false`** (и smart-cast к `Contact` после проверки).

### Task 13 — is, as, as?
1. `ratio` (1.5, Double) в исходном коде обрабатывается веткой **`else`** → `an unknown type: Double`.
2. Последняя строка `value as String` (исходный код):
   | ключ | результат |
   |---|---|
   | volume | `ClassCastException: class java.lang.Integer cannot be cast to class java.lang.String` |
   | darkMode | `ClassCastException: ... java.lang.Boolean cannot be cast to ... java.lang.String` |
   | username | без проблем, `Dilnoza` |
   | theme | `NullPointerException: null cannot be cast to non-null type kotlin.String` |
   | ratio | `ClassCastException: ... java.lang.Double cannot be cast to ... java.lang.String` |
   | несуществующий | `No such setting`, до каста не доходит |
3. `as? String`: volume → `null`, theme → `null` (без исключений). `as String?`: theme → `null` (null разрешён в nullable-типе), volume → всё равно `ClassCastException` (Integer — не String). Итог: `as?` — безопасный каст (null при неудаче), `as T?` — небезопасный каст к nullable-типу.
4. `Any` — корневой тип всех **ненулевых** типов Kotlin, поэтому расширение `Any` доступно у любого значения. `5.typeName()` → `Int`, `"x".typeName()` → `String`.
5. Добавлена ветка `is Double -> "a decimal, half of it is ${value / 2}"` → `ratio is a decimal, half of it is 0.75`.

### Task 14 — Nothing
1. `20` → `You will be 21 next year`; `abc` → `Input error: Age must be a whole number`; `-5` → `Input error: Age cannot be negative`. Исключение бросается в `fail`, а ловится в `main` в блоке `catch (e: IllegalArgumentException)`.
2. Тип `age` — **`Int`**. Левая часть `?:` — `Int?`, правая — `Nothing`. Общий тип `Int` и `Nothing` — `Int` (Nothing — подтип всех типов), а `null` из левой части уже обработан `?:`.
3. С `Unit`: тип `age` становится **`Any`** (общий супертип `Int` и `Unit`), отсюда ошибки: `Unresolved reference 'compareTo'` на `age < 0` и `Return type mismatch: expected 'Int', actual 'Any'` на `return age`.
4. После `fail("always")` строка `println("after fail")` подсвечивается серым как **unreachable code**. Вызов функции с типом `Nothing` никогда не возвращает управление — компилятор это знает и использует для анализа потока.
5. `TODO()` и `error()` возвращают **`Nothing`**.

### Task 15 — Nothing: бесконечный цикл
1. Ввод `add 5, sub 2, show, foo, exit` → `Total = 3`, `Unknown command 'foo'`, `Bye, final total = 3`. `runCalculator` может иметь тип `Nothing`, потому что она **никогда не возвращает управление нормально**: `while (true)` без `break`, а единственный выход — `exitProcess(0)`, у которого тоже тип `Nothing`.
2. Никогда не выполняется `println("This line is never reached")` — IDE подсвечивает её серым с предупреждением **Unreachable code**.
3. `student` → `Discount: 20%`, `friend` → `Discount: 10%`, `zzz` → программа падает: `IllegalStateException: Unknown coupon 'zzz'`. Ветки `Int, Int, Nothing` → тип всего `when` — **`Int`**.
4. `List<Nothing>` можно присвоить и `List<String>`, и `List<Int>`: `List<out E>` ковариантен, а `Nothing` — подтип любого типа, значит `List<Nothing>` — подтип `List<String>` и `List<Int>`. К тому же в пустом списке нет ни одного элемента, который мог бы иметь «неправильный» тип. Вывод: `0 names, 0 ages`.
5. Добавлены команды `reset` (`total = 0`) и `mul N` (`total *= number ?: 1`). Проверка: `add 5, sub 2, mul 3, show` → `Total = 9`; `reset, show` → `Total = 0`.
