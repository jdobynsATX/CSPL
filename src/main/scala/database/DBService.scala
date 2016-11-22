package cs345.database

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import java.sql.Date
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

class DBService() {
  val db = Database.forConfig("h2mem1")

  val setupFuture = db.run(DBSetup.setupSequence)
  Await.result(setupFuture, Duration.Inf)

  val meetings = DBSetup.meetings
  val clients = DBSetup.clients
  val employees = DBSetup.employees

  def Stop() = {
    db.close
  }

  def ListAllEmployees() = {
    // Read all coffees and print them to the console
    println("Employees:")
    db.run(employees.result).map(_.foreach {
      case (id, name, rank, pay, schedule) =>
        println("  " + id + "\t" + name + "\t" + rank + "\t" + pay + "\t" + schedule + "\t")
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
    val f: Future[(Int, String, Int, Double, Blob)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val e = new Employee(result)
    return e
  }

  def NewEmployee(): Employee = {
    val default_blob = new SerialBlob(Array[Byte](0))
    val insert = (employees returning employees.map(_.id)) += (-1, "", -1, -1, default_blob)
    val insertSeq: Future[Int] = db.run(insert)

    val empId = Await.result(insertSeq, Duration.Inf)
    var result = new Employee(empId)
    return result
  }

  def UpdateEmployee(emp: Employee): Employee = {
    val updated = employees.insertOrUpdate(emp.id, emp.name, emp.rank, emp.pay, emp.schedule)
    println( "DEBUG") //DEBUG
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

  def ListAllMeetings() = {
    println("Meetings:")
    db.run(meetings.result).map(_.foreach {
      case (id, client_id, name, start, end) =>
        println("  " + id + "\t" + client_id + "\t" + name + "\t" + start + "\t" + end + "\t")
    })
  }

  def GetMeeting(id: Int): Meeting = {
    val query = for {
      meeting <- meetings if meeting.id === id
    } yield meeting
    val action = query.result.head
    val f: Future[(Int, Int, String, Timestamp, Timestamp)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retMeeting = new Meeting(result)
    return retMeeting
  }

  def NewMeeting(): Meeting = {
    val insert = (meetings returning meetings.map(_.id)) += (-1, -1,"", new Timestamp(0), new Timestamp(0))
    val insertSeq: Future[Int] = db.run(insert)

    val meetingId = Await.result(insertSeq, Duration.Inf)
    var result = new Meeting(meetingId)
    return result
  }

  def UpdateMeeting(meeting: Meeting): Meeting = {
    val updated = meetings.insertOrUpdate(meeting.id, meeting.client_id, meeting.name, meeting.start, meeting.end)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting")
    return meeting
  }

  def DeleteMeeting(meeting: Meeting): Meeting = {
    val query = meetings.filter(_.id === meeting.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting")
    return meeting
  }

  def DeleteMeeting(id: Int): Int = {
    val query = meetings.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting")
    return id
  }

}


