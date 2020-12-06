import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.transactions.transaction


object Entries : IntIdTable() {
    val name = varchar("name", 50)
    val message = varchar("message", 500)
}

class Entry(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Entry>(Entries)

    var name by Entries.name
    var message by Entries.message
}

val activateDSL = true
val activateDAO = true

fun main() {

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://localhost/test?serverTimezone=UTC"          // Server spezifizieren
        driverClassName = "com.mysql.cj.jdbc.Driver"                        // Treiber spezifizieren
        username = "root"
        password = "root"
        maximumPoolSize = 10                                                // Maximale Verbindungen mit der Datenbank (Hikari)
    }
    val dataSourceMySQL = HikariDataSource(config)                          // Die Konfiguration einer bestimmten dataSource hinzufügen
    val mySQLDB = connect(dataSourceMySQL)


    if (activateDSL) {
        println("------------------------------------------- Start von DSL - Abschnitt -------------------------------------------")
        //DSL - Method!
        transaction(mySQLDB) {
            //addLogger(StdOutSqlLogger) // Logger gibt SQL - Code in das Ausgabe-Fenster aus
            SchemaUtils.create(Entries) // Tabelle Entries erstellen

            // Create - Zeilen in die Entries-Tabelle einfügen
            Entries.insert {
                it[name] = "Max Mustermann"
                it[message] = "Hello World! I am message #1!"
            }
            Entries.insert {
                it[name] = "Michelle Musterfrau"
                it[message] = "Hello Max! I am message #2"
            }
            Entries.insert {
                it[name] = "Alexander Musterjunge"
                it[message] = "Hello boys and girls! I am message #3"
            }
            Entries.insert {
                it[name] = "Michel Mustermaedchen"
                it[message] = "Hello everyone! I am message #4"
            }

            // Read - Zeilen aus Tabelle Entries auslesen
            // Alle Zeilen auslesen
            val queryAll = Entries.selectAll()       //Nimmt alle Einträge aus Entries und speichert sie in queryAll

            println("Ausgabe von queryAll folgt:")
            queryAll.forEach() { println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}") }

            // Spezifische Zeile(n) auslesen - hier nach Name (Möglichkeiten unter https://github.com/JetBrains/Exposed/wiki/DSL zu finden)
            val querySpecific = Entries.select { Entries.name eq "Michelle Mustermaedchen" }

            println("Ausgabe von querySpecific folgt:")
            querySpecific.forEach { println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}") }

            // Update - Werte von bestimmten Feldern verändern.
            Entries.update({ Entries.id eq 4 }) { it[name] = "Michelle Mustermaedchen" } //Updated den Eintrag von Michel Mustermaedchen auf Michelle Mustermaedchen
            val queryUpdate = Entries.select { Entries.id eq 4}
            println("Ausgabe von queryUpdate folgt:")
            queryUpdate.forEach { println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}") }

            // Man kann auch spezifische Spalten aus einer Zeile auslesen (auch mehrere auf einmal!)
            val querySlice = Entries.slice(Entries.id, Entries.name).selectAll()
            println("Ausgabe von querySlice folgt:")
            querySlice.forEach { println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}") }

            // Man kann diese Queries auch mappen in eine Liste aus Paaren (in diesem Fall)
            val querySliceMap = querySlice.map { it[Entries.id] to it[Entries.name] }
            println("Ausgabe von querySliceMap folgt:")
            querySliceMap.forEach { println("ID: ${it.first}, Name: ${it.second}") }

            // Delete - Spezifische Zeilen löschen (hier nach ID der Zeile)
            Entries.deleteWhere { Entries.id eq 4 }
            val queryDeleted = Entries.selectAll()

            println("Ausgabe von queryDeleted folgt:")
            queryDeleted.forEach { println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}") }

            SchemaUtils.drop(Entries)               //Table nach Benutzung löschen
        }
    }
    if (activateDAO) {
        println("------------------------------------------- Start von DAO - Abschnitt -------------------------------------------")
        //DAO - Method
        transaction(mySQLDB) {
            //addLogger(StdOutSqlLogger)  // Logger gibt SQL - Code in das Ausgabefenster aus
            SchemaUtils.create(Entries) // Tabelle Entries erstellen

            // Create - Neue Zeilen in "Entries" Tabelle erstellen
            Entry.new {
                name = "Max Mustermann"
                message = "Hello World! I am message #1"
            }
            Entry.new {
                name = "Michelle Musterfrau"
                message = "Hello Max! I am message #2"
            }
            Entry.new {
                name = "Alexander Musterjunge"
                message = "Hello boys and girls! I am message #3"
            }
            Entry.new {
                name = "Michel Mustermädchen"
                message = "Hello everyone! I am message #4"
            }

            // Read - Zeilen auslesen aus Entries-Tabelle
            // Alle Zeilen auslesen
            val queryAll = Entry.all()

            println("Ausgabe von queryAll folgt:")
            queryAll.forEach() { println("ID: ${it.id}, Name: ${it.name}, Message: ${it.message}") }

            // Spezifische auslesen (hier nach Name)
            val queryName = Entry.find { Entries.name eq "Max Mustermann" }

            println("Ausgabe von queryName folgt:")
            queryName.forEach { println("ID: ${it.id}, Name: ${it.name}, Message: ${it.message}") }

            // Nach ID auslesen
            val queryID = Entry.findById(4)

            println("Ausgabe von queryID folgt:")
            // Null - Check erforderlich, da queryID vom Typ Entry? ist.
            if (queryID != null) {
                println("ID: ${queryID.id}, Name: ${queryID.name}, Message: ${queryID.message}")
            }

            // Update - Spezifische Inhalte von einzelnen Zeilen verändern
            queryID?.name = "Michelle Mustermädchen"                               // name von entryFour verändern
            val queryUpdate = Entry.find { Entries.name eq "Michelle Mustermädchen" } // Die ge-Updatete Zeile auslesen

            println("Ausgabe von queryUpdate folgt:")
            queryUpdate.forEach { println("ID: ${it.id}, Name: ${it.name}, Message: ${it.message}") }

            // Delete - Spezifische Zeilen löschen
            queryID?.delete()              // Zeile die zu entryFour gehört löschen
            val queryDelete = Entry.all()   // Alle auslesen --> sollten jetzt nur noch Zeilen 1 bis 3 vorhanden sein.

            println("Ausgabe von queryDelete folgt:")
            queryDelete.forEach { println("ID: ${it.id}, Name: ${it.name}, Message: ${it.message}") }

            // Bestimmte Spalte von einer Zeile auslesen:
            val querySpecific = Entry.findById(1)?.message
            println("Ausgabe von querySpecific folgt: $querySpecific")

            SchemaUtils.drop(Entries)           //Table nach Benutzung löschen
        }
    }
}
