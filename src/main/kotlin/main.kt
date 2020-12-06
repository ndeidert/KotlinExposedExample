import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Database.Companion.connect
import org.jetbrains.exposed.sql.transactions.transaction


object Entries : Table() {
    val entryId = integer("id").autoIncrement() //Unique Primary-Key?
    val name = varchar("name", 50)
    val message = varchar("message", 500)
    override val primaryKey = PrimaryKey(entryId)
}

fun main() {

    val config = HikariConfig().apply {
        jdbcUrl = "jdbc:mysql://localhost/test?serverTimezone=UTC" //Server spezifizieren
        driverClassName = "com.mysql.cj.jdbc.Driver"                       //Treiber spezifizieren
        username = "root"
        password = "root"
        maximumPoolSize = 10                                               //Maximale Verbindungen mit der Datenbank (Hikari)
    }
    val dataSourceMySQL = HikariDataSource(config)                              //Die Konfiguration einer bestimmten dataSource hinzufügen
    val mySQLDB = connect(dataSourceMySQL)

    transaction(mySQLDB) {
        // print sql to std-out
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Entries)

        //Insert in die Entries-Tabelle
        Entries.insert {
            it[entryId] = 1
            it[name] = "Max Mustermann"
            it[message] = "Hello World! I am message #1!"
        }
        Entries.insert {
            it[entryId] = 2
            it[name] = "Michelle Musterfrau"
            it[message] = "Hello Max! I am message #2"
        }
        Entries.insert {
            it[entryId] = 3
            it[name] = "Alexander Musterjunge"
            it[message] = "Hello boys and girls! I am message #3"
        }
        Entries.insert {
            it[entryId] = 4
            it[name] = "Michel Mustermaedchen"
            it[message] = "Hello everyone! I am message #4"
        }

        println("queryAll folgt:")
        val queryAll = Entries.selectAll()                                              //Nimmt alle Einträge aus Entries und speichert sie in queryAll

        //Updated in Entries die Zeile, wo entryId == 4 true ergibt
        println("Entries.update folgt:")
        Entries.update({ Entries.entryId eq 4 }) {
            it[Entries.name] = "Michelle Mustermaedchen"                                //Updated den Eintrag von Michel Mustermaedchen auf Michelle Mustermaedchen
        }

        println("querySpecific folgt:")
        val querySpecific = Entries.select { Entries.name eq "Michelle Mustermaedchen" }

        println("querySlice folgt:")
        val querySlice = Entries.slice(Entries.entryId, Entries.name).selectAll()
        val querySliceMap = querySlice.map { it[Entries.entryId] to it[Entries.name] }    //Man kann diese Queries auch mappen in eine Liste.

        //Entries.delete folgt:
        Entries.deleteWhere { Entries.entryId eq 4 }
        println("queryDeleted folgt:")
        val queryDeleted = Entries.selectAll()



        println("Ausgabe von queryAll folgt:")
        queryAll.forEach() {
            println("ID: ${it[Entries.entryId]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}")
        }

        println("Ausgabe von querySpecific folgt:")
        querySpecific.forEach {
            println("ID: ${it[Entries.entryId]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}")
        }

        println("Ausgabe von querySlice folgt:")
        //Man sieht, dass querySlice direkt eine Liste aus Pairs ist.
        querySlice.forEach {
            println("ID: ${it[Entries.entryId]}, Name: ${it[Entries.name]}")
        }

        println("Ausgabe von querySliceMap folgt:")
        querySliceMap.forEach {
            println("ID: ${it.first}, Name: ${it.second}")
        }

        println("Ausgabe von queryDeleted folgt:")
        queryDeleted.forEach() {
            println("ID: ${it[Entries.entryId]}, Name: ${it[Entries.name]}, Message: ${it[Entries.message]}")
        }

        SchemaUtils.drop(Entries)
    }


}