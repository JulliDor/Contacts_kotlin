package contacts
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

private const val NAME = "Enter the name: "
private const val SURNAME = "Enter the surname: "
private const val BIRTH = "Enter the birth day: "
private const val GENDER = "Enter the gender (M, F): "
private const val PHONE = "Enter the phone number: "
private const val ORGANIZATION = "Enter the organization name: "
private const val ADDRESS = "Enter the address: "

object ContaktsApps {
    private val phoneBook = mutableListOf<Contacts>()
    fun run() {
        while (true) {
            print("\n[menu] Enter action (add, list, search, count, exit): ")
            when (readln()) {
                "exit" -> return
                "add" -> when (print("Enter the type (person, organization): ").run { readln() }) {
                    "person" -> {
                        val newPerson = Person()
                        phoneBook.add(newPerson).also { newPerson.phone = input(PHONE)}.also{ newPerson.dateCreate = dateTime() }.also { println("The record added.") }
                    }
                    "organization" -> {
                        val newOrganization = Organization()
                        phoneBook.add(newOrganization).also{newOrganization.phone = input(PHONE)}.also{newOrganization.dateCreate = dateTime() }.also { println("The record added.") }
                    }
                }
                "count" -> println("The Phone Book has ${phoneBook.size} records.")
                "list" -> {
                    printContakts()
                    while(true) {
                        val num = print("\n[list] Enter action ([number], back): ").run { readln() }
                        when {
                            num == "back" -> break
                            num.matches(Regex("""\d+""")) -> { record(num.toInt()-1); break }
                        }
                    }
                }
                "edit" -> {
                    printContakts()
                    val num = print("Select a record: ").run { readln().toInt() }
                    phoneBook[num - 1].edit()
                }
                "search" -> {
                    while(true) {
                        val searchList = mutableListOf<String>()
                        val word = print("Enter search query: ").run { readln() }
                        phoneBook.forEach { if (it.getProperty().lowercase().contains(word.lowercase())) searchList.add(it.toString()) }
                        println("Found ${searchList.size} results:").also { if (searchList.isNotEmpty()) searchList.forEachIndexed { i, s -> println("${i + 1}. $s") } }
                        val input = print("\n[search] Enter action ([number], back, again): ").run { readln() }
                        when {
                            input == "back" -> break
                            input == "again" -> continue
                            input.matches(Regex("""\d+""")) -> {
                                var num = 1
                                phoneBook.forEachIndexed {i, it -> if (it.toString() == searchList[input.toInt() - 1]) num = i }
                                record(num)
                                break
                            }
                        }
                    }
                }
            }
        }
    }
    private fun printContakts() {
        if (phoneBook.isEmpty()) println("No records to list!") else phoneBook.forEachIndexed { i, it -> println("${i + 1}. $it") }
    }
    private fun record(num:Int) {
        phoneBook[num].getInfo()
        while (true) {
            when (print("\n[record] Enter action (edit, delete, menu): ").run { readln() }) {
                "edit" -> phoneBook[num].edit()
                "delete" -> phoneBook.removeAt(num).also { println("The record removed!") }
                "menu" -> return
            }
        }
    }
}
fun dateTime():String = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
fun input(text: String) = print(text).run { readln() }

abstract class Contacts(var dateCreate:String = "", var dateEdit:String = "Not edit") {
    var phone = ""
        set(value){ field = checkPhoneNumber(value) }
    private fun checkPhoneNumber(number:String) = if (number.matches(Regex("""\+?((\(\w+\)([ -]\w{2,})?)|(\w+([ -]\(\w{2,}\))?))([ -]\w{2,})*"""))) number else "[no number]".also{ println("Wrong number format!")}
    open fun getInfo() {
        println("Number: $phone\nTime created: $dateCreate\nTime last edit: $dateEdit")
    }
    abstract fun edit()
    abstract fun getProperty():String
}

class Person:Contacts() {
    private var name:String = input(NAME)
    private var surname:String = input(SURNAME)
    private var birth:String = input(BIRTH).run { checkBirth(this) }
        set(value) {field = checkBirth(value)}
    private var gender:String = input(GENDER).run { checkGender(this) }
        set(value) { field = checkGender(value) }
    private fun checkGender(g:String):String {
        return if (!(g == "M" || g == "F")) "[no data]".also { println("Bad gender!") } else g
    }
    private fun checkBirth(day:String):String{
        return day.ifEmpty { "[no data]".also { println("Bad birth date!") } }
    }
    override fun toString(): String {
        return "$name $surname"
    }
    override fun getInfo(){
        print("Name: $name\nSurname: $surname\nBirth date: $birth\nGender: $gender\n")
        super.getInfo()
    }
    override fun getProperty():String {
        return "$name $surname $birth $gender $phone"
    }
    override fun edit() {
        val field = print("Select a field (name, surname, birth, gender, number): ").run{ readln() }
        when(field){
            "name" -> name = input(NAME)
            "surname" -> surname = input(SURNAME)
            "birth" -> birth = input(BIRTH)
            "gender" -> gender = input(GENDER)
            "number" -> phone = input(PHONE)
            else -> println("Wrong input!")
        }
        dateEdit = dateTime().also{ println("Saved") }
        getInfo()
    }
}

class Organization:Contacts() {
    private var name:String = input(ORGANIZATION)
    private var address:String = input(ADDRESS)
    override fun toString(): String {
        return name
    }
    override fun getInfo(){
        print("Organization name: $name\nAddress: $address\n")
        super.getInfo()
    }
    override fun getProperty(): String {
        return "$name $address $phone"
    }
    override fun edit() {
        val field = print("Select a field (name, address, number):").run { readln() }
        when(field) {
            "name" -> name = input(ORGANIZATION)
            "address" -> address = input(ADDRESS)
            "phone" -> phone = input(PHONE)
        }
        dateEdit = dateTime().also{println("Saved")}
        getInfo()
    }
}

fun main() {
    ContaktsApps.run()
}
