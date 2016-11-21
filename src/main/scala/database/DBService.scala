package cs345.database

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import java.sql.Date
import java.sql.Timestamp

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

  def ListAllEmployees() = {
    // Read all coffees and print them to the console
    println("Employees:")
    db.run(employees.result).map(_.foreach {
      case (id, name, rank, pay) =>
        println("  " + id + "\t" + name + "\t" + rank + "\t" + pay + "\t")
    })
  }

  def GetAllEmployees(): Array[Employee] = {
    val result: Array[Employee] = new Array[Employee](0)
    db.run(employees.result).map(_.foreach {
      case data =>
        result :+ (new Employee(data))
    })
    return result
  }

  def GetEmployee(id: Int): Employee = {
    val query = for {
      emp <- employees if emp.id === id
    } yield emp
    val action = query.result.head
    val f: Future[(Int, String, Int, Double)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val e = new Employee(result)
    return e
  }

  def NewEmployee(): Employee = {
    val insert = (employees returning employees.map(_.id)) += (-1, "", -1, -1)
    val insertSeq: Future[Int] = db.run(insert)

    val empId = Await.result(insertSeq, Duration.Inf)
    var result = new Employee(empId)
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

  def DeleteEmployee(id: Int): Int = {
    val query = employees.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find emp")
    return id
  }

  def ListAllClients() = {
    println("Clients:")
    db.run(clients.result).map(_.foreach {
      case (id, name, dateAdded) =>
        println("  " + id + "\t" + name + "\t" + dateAdded + "\t")
    })
  }

  def GetClient(id: Int): Client = {
    val query = for {
      client <- clients if client.id === id
    } yield client
    val action = query.result.head
    val f: Future[(Int, String, Date)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retClient = new Client(result)
    return retClient
  }

  def NewClient(): Client = {
    val insert = (clients returning clients.map(_.id)) += (-1, "", new Date(0))
    val insertSeq: Future[Int] = db.run(insert)

    val clientId = Await.result(insertSeq, Duration.Inf)
    var result = new Client(clientId)
    return result
  }

  def UpdateClient(client: Client): Client = {
    val updated = clients.insertOrUpdate(client.id, client.name, client.addDate)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find emp")
    return client
  }

  def DeleteClient(client: Client): Client = {
    val query = clients.filter(_.id === client.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find emp")
    return client
  }

  def DeleteClient(id: Int): Int = {
    val query = clients.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find emp")
    return id
  }

  def ListAllEvents() = {
    println("Events:")
    db.run(events.result).map(_.foreach {
      case (id, name, start, end) =>
        println("  " + id + "\t" + name + "\t" + start + "\t" + end + "\t")
    })
  }

  def GetEvent(id: Int): Event = {
    val query = for {
      event <- events if event.id === id
    } yield event
    val action = query.result.head
    val f: Future[(Int, String, Timestamp, Timestamp)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retEvent = new Event(result)
    return retEvent
  }

  def NewEvent(): Event = {
    val insert = (events returning events.map(_.id)) += (-1, "", new Timestamp(0), new Timestamp(0))
    val insertSeq: Future[Int] = db.run(insert)

    val eventId = Await.result(insertSeq, Duration.Inf)
    var result = new Event(eventId)
    return result
  }

  def UpdateEvent(event: Event): Event = {
    val updated = events.insertOrUpdate(event.id, event.name, event.start, event.end)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find event")
    return event
  }

  def DeleteEvent(event: Event): Event = {
    val query = events.filter(_.id === event.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find event")
    return event
  }

  def DeleteEvent(id: Int): Int = {
    val query = events.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find event")
    return id
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


