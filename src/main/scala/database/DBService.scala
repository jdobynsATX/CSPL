package cs345.database

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._

class DBService() {
  val db = Database.forConfig("h2mem1")

  val setupFuture = db.run(DBSetup.setupSequence)
  Await.result(setupFuture, Duration.Inf)

  val events = DBSetup.events
  val clients = DBSetup.clients
  val employees = DBSetup.employees

  def Stop() = {
    db.close
  }

  def GetAllEmployees() = {
    // Read all coffees and print them to the console
    println("Emps:")
    db.run(employees.result).map(_.foreach {
      case (id, name, rank, pay) =>
        println("  " + id + "\t" + name + "\t" + rank + "\t" + pay + "\t")
    })
  }

  def NewEmployee(): Employee = {
    val insert = (employees returning employees.map(_.id)) += (-1, "", -1, -1)
    val insertSeq: Future[Int] = db.run(insert)

    val empId = Await.result(insertSeq, Duration.Inf)
    var result = new Employee()
    result.id = empId
    return result
  }

  def UpdateEmployee(emp: Employee): Employee = {
    val updated = employees.insertOrUpdate(emp.id, emp.name, emp.rank, emp.pay)
    // val update = (employees returning employees).insertOrUpdate(-1, emp.name, emp.rank, emp.pay)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find emp")
    return emp
  }

  def DeleteEmployee(emp: Employee): Employee = {
    val query = employees.filter(_.id === emp.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find emp")
    return emp
  }

  // Examples
  // def GetAllCoffee() = {

  //   // Read all coffees and print them to the console
  //   println("Coffees:")
  //   db.run(coffees.result).map(_.foreach {
  //     case (name, supID, price, sales, total) =>
  //       println("  " + name + "\t" + supID + "\t" + price + "\t" + sales + "\t" + total)
  //   })
  // }

  // def GetQ() = {
  //   // Perform a join to retrieve coffee names and supplier names for
  //   // all coffees costing less than $9.00
  //   val q2 = for {
  //     c <- coffees if c.price < 9.0
  //     s <- suppliers if s.id === c.supID
  //   } yield (c.name, s.name)
  //   // Equivalent SQL code:
  //   // select c.COF_NAME, s.SUP_NAME from COFFEES c, SUPPLIERS s where c.PRICE < 9.0 and s.SUP_ID = c.SUP_ID
  //   db.stream(q2.result).foreach(println)
  // }

}


