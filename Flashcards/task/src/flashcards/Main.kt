package flashcards

import java.io.File

val log = arrayListOf<String>()
const val fileName = "/Users/caique/Documents/Flashcards/Flashcards/task/src/flashcards/file_cards.txt"
val file = File(fileName)

fun main(args: Array<String>) {


    if (args.isEmpty()) {

        file.writeText("")
    } else {

        if (args.contains("-import")) {

            val importFileName = File(args[args.indexOf("-import") + 1])
            file.writeText(importFileName.readText())
        }
    }

    while (true) {

        printAndLog("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):")
        when (readLineAndLog()) {

            "add" -> { addCard() }
            "remove" -> { remove() }
            "import" -> { import() }
            "export" -> { export() }
            "ask" -> { ask() }
            "log" -> { log() }
            "hardest card" -> { hardestCard() }
            "reset stats" -> { resetStats() }
            "exit" -> {

                if (args.contains("-export")) {

                    val exportFileName = File(args[args.indexOf("-export") + 1])
                    file.copyTo(exportFileName)
                }
                printAndLog("Bye bye!")
                break
            }
        }
    }
}


fun addCard() {


    val lines = getLine()

    printAndLog("The card:")
    val card = readLineAndLog()
    if (lines.contains(card)) {

        printAndLog("The card \"$card\" already exists.\n")
        return
    }

    printAndLog("The definition of the card:")
    val definition = readLineAndLog()

    if (lines.contains(definition)) {

        printAndLog("The definition \"$definition\" already exists.\n")
    } else {

        file.appendText("$card\n$definition\n0\n")
        printAndLog("The pair (\"$card\":\"$definition\") has been added.\n")
    }

}

fun remove() {


    val lines = getLine()

    printAndLog("Which card?")
    val card = readLineAndLog()

    if (lines.contains(card) && lines.indexOf(card) % 3 == 0) {

        val index = lines.indexOf(card)

        repeat(3) { lines.removeAt(index) }
        printAndLog("The card has been removed.")

    } else {

        printAndLog("Can't remove \"$card\": there is no such card.")
    }

    file.writeText("")
    lines.forEach { file.appendText("$it\n") }
}


fun import() {


    val lines = getLine()

    printAndLog("File name:")
    val fileNameToImport = readLineAndLog()
    if (!File(fileNameToImport).exists()) {

        printAndLog("File not found.")
        return
    }
    val fileToImport = File(fileNameToImport)
    val linesToImport = arrayListOf<String>()
    fileToImport.forEachLine { linesToImport.add(it) }

    if (linesToImport.isEmpty()) return

    for (i in 0 until linesToImport.size step 3) {

        if (!lines.contains(linesToImport[i])) {

            lines.add(linesToImport[i])
            lines.add(linesToImport[i+1])
            lines.add(linesToImport[i+2])
        } else {

            val index = lines.indexOf(linesToImport[i])
            lines[index+1] = linesToImport[i+1]
            lines[index+2] = linesToImport[i+2]
        }
    }

    file.writeText("")
    lines.forEach { file.appendText("${it}\n") }
    printAndLog("${linesToImport.size / 3} cards have been loaded.")
}

fun export() {


    printAndLog("File name:")
    val nameToExport = readLineAndLog()
    val fileToExport = File(nameToExport)

    fileToExport.writeText("")
    file.forEachLine { fileToExport.appendText("$it\n") }

    printAndLog("${file.readLines().size / 3} cards have been saved.")

}

fun ask() {

    printAndLog("How many times to ask?")
    val n = readLineAndLog().toInt()

    flashCards(n)

}

fun log() {

    val logs = "/Users/caique/Documents/Flashcards/Flashcards/task/src/flashcards/log.txt"
    printAndLog("File name:")
    val nameToSave = readLineAndLog()
    val fileToSave = File(nameToSave)
    val file = File(logs)

    log.forEach { fileToSave.appendText("$it\n")
        file.appendText("$it\n") }

    printAndLog("The log has been saved.")
}

fun hardestCard() {


    val lines = arrayListOf<String>()
    var biggest = 0
    val hardestCard = arrayListOf<String>()
    file.forEachLine { lines.add(it) }


    lines.forEach {

        if (it.matches(Regex("^\\d+\$"))) {

            if (it.toInt() > biggest) biggest = it.toInt()
        }
    }

    if (biggest == 0) {

        printAndLog("There are no cards with errors.")

    } else {

        for (i in lines.indices) {

            if (lines[i].matches(Regex("^\\d+\$"))) {

                if (lines[i].toInt() == biggest) {

                    hardestCard.add(lines[i - 2])
                }
            }
        }

        var termsHardest = ""
        for (i in 0 until hardestCard.size - 1) {

            termsHardest += "\"${hardestCard[i]}\","
        }

        termsHardest += "\"${hardestCard.last()}\"."

        if (hardestCard.size == 1) {

            printAndLog("The hardest card is $termsHardest You have $biggest errors answering it.")
        } else {


            printAndLog("The hardest cards are $termsHardest You have $biggest errors answering them.")
        }
    }
}

fun resetStats() {


    val lines = getLine()
    for (i in 2 until lines.size) {

        lines[i] = "0"
    }

    file.writeText("")
    lines.forEach { file.appendText("$it\n") }

    printAndLog("Card statistics have been reset.")
}

fun printAndLog(string: String) {

    log.add(string)
    println(string)
}

fun readLineAndLog(): String {

    val input = readLine()!!
    log.add(input)
    return input
}

fun flashCards(times: Int) {


    val lines = getLine()
    for (i in 0 until times * 3 step 3) {

        printAndLog("Print the definition of \"${lines[i]}\":")
        val answer = readLineAndLog()

        if (answer == lines[i+1]) {

            printAndLog("Correct!")
        } else {

            lines[i+2] = "${lines[i+2].toInt() + 1}"
            if (lines.contains(answer)) {

                printAndLog("Wrong. The right answer is \"${lines[i+1]}\"," +
                        " but your definition is correct for \"${lines[lines.indexOf(answer)-1]}\".")
            } else {


                printAndLog("Wrong. The right answer is \"${lines[i+1]}\".")
            }
        }
    }

    file.writeText("")
    lines.forEach { file.appendText("${it}\n") }
}

fun getLine(): ArrayList<String> {

    val lines = arrayListOf<String>()
    file.forEachLine { lines.add(it) }
    
    return lines
}