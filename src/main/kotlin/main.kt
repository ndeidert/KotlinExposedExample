import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction


object Students : Table(){
    val id = integer ("id")
    val name = varchar ("name",50)
}
fun main(){


    val config = HikariConfig().apply {
        jdbcUrl         = "jdbc:mysql://localhost/test?serverTimezone=UTC"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        username        = "root"
        password        = "root"
        maximumPoolSize = 10
    }
    val dataSource = HikariDataSource(config)
    Database.connect(dataSource)

    transaction {
        // print sql to std-out
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(Students)
    }
}