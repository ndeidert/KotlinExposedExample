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

val DSLOrDAO: Int = 0

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


    if (DSLOrDAO == 0){
        //DSL - Method!
        transaction(mySQLDB) {
            // print sql to std-out
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Entries)                 // Table erstellen

            //Insert in die Entries-Tabelle
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

            println("queryAll folgt:")
            val queryAll = Entries.selectAll()                                              //Nimmt alle Einträge aus Entries und speichert sie in queryAll

            //Updated in Entries die Zeile, wo entryId == 4 true ergibt
            println("Entries.update folgt:")
            Entries.update({ Entries.id eq 4 }) {
                it[name] = "Michelle Mustermaedchen"                                        //Updated den Eintrag von Michel Mustermaedchen auf Michelle Mustermaedchen
            }

            println("querySpecific folgt:")
            val querySpecific = Entries.select { Entries.name eq "Michelle Mustermaedchen" }

            println("querySlice folgt:")
            val querySlice = Entries.slice(Entries.id, Entries.name).selectAll()
            val querySliceMap = querySlice.map { it[Entries.id] to it[Entries.name] }       //Man kann diese Queries auch mappen in eine Liste.

            //Entries.delete folgt:
            Entries.deleteWhere { Entries.id eq 4 }
            println("queryDeleted folgt:")
            val queryDeleted = Entries.selectAll()



            println("Ausgabe von queryAll folgt:")
            queryAll.forEach() {
                println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}")
            }

            println("Ausgabe von querySpecific folgt:")
            querySpecific.forEach {
                println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}")
            }

            println("Ausgabe von querySlice folgt:")
            //Man sieht, dass querySlice direkt eine Liste aus Pairs ist.
            querySlice.forEach {
                println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}")
            }

            println("Ausgabe von querySliceMap folgt:")
            querySliceMap.forEach {
                println("ID: ${it.first}, Name: ${it.second}")
            }

            println("Ausgabe von queryDeleted folgt:")
            queryDeleted.forEach() {
                println("ID: ${it[Entries.id]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}")
            }

            SchemaUtils.drop(Entries)               //Table nach Benutzung löschen
        }
    }else {
        //DAO - Method
        transaction(mySQLDB) {
            addLogger(StdOutSqlLogger)
            SchemaUtils.create(Entries)             //Table erstellen

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
                name = "Michel Mustermaedchen"
                message = "Hello everyone! I am message #4"
            }





            SchemaUtils.drop(Entries)           //Table nach Benutzung löschen
        }
    }
}
