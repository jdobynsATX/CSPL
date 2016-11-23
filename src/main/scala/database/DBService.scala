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
  val projects = DBSetup.projects

  def Stop() = {
    db.close
  }

  def ListAllEmployees() = {
    // Read all coffees and print them to the console
    println("Employees:")
    val employees: Seq[Employee] = GetAllEmployees()
    for (emp <- employees) {
      println(emp)
    }
  }

  def GetAllEmployees(): Array[Employee] = {
    var result: Array[Employee] = new Array[Employee](0)
    val f: Future[Seq[(Int, String, Int, Double, Blob)]] = db.run(employees.result)
    val queryResult: Seq[(Int, String, Int, Double, Blob)] = Await.result(f, Duration.Inf)
    for (empData <- queryResult) {
      result = result :+ (new Employee(empData))
    }
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
    val clients: Seq[Client] = GetAllClients()
    for (client <- clients) {
      println(client)
    }
  }

  def GetAllClients(): Array[Client] = {
    var result: Array[Client] = new Array[Client](0)
    val f: Future[Seq[(Int, String, Date, Double)]] = db.run(clients.result)
    val queryResult: Seq[(Int, String, Date, Double)] = Await.result(f, Duration.Inf)
    for (data <- queryResult) {
      result = result :+ (new Client(data))
    }
    return result
  }

  def GetClient(id: Int): Client = {
    val query = for {
      client <- clients if client.id === id
    } yield client
    val action = query.result.head
    val f: Future[(Int, String, Date, Double)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retClient = new Client(result)
    return retClient
  }

  def NewClient(): Client = {
    val insert = (clients returning clients.map(_.id)) += (-1, "", new Date(0), 0.0)
    val insertSeq: Future[Int] = db.run(insert)

    val clientId = Await.result(insertSeq, Duration.Inf)
    var result = new Client(clientId)
    return result
  }

  def UpdateClient(client: Client): Client = {
    val updated = clients.insertOrUpdate(client.id, client.name, client.addDate, client.balance)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find client")
    return client
  }

  def DeleteClient(client: Client): Client = {
    val query = clients.filter(_.id === client.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find client")
    return client
  }

  def DeleteClient(id: Int): Int = {
    val query = clients.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find client")
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

  def ListAllProjects() = {
    println("Projects:")
    db.run(projects.result).map(_.foreach {
      case (id, client_id, name, end) =>
        println("  " + id + "\t" + client_id + "\t" + name + "\t" + end + "\t")
    })
  }

  def GetProject(id: Int): Project = {
    val query = for {
      project <- projects if project.id === id
    } yield project
    val action = query.result.head
    val f: Future[(Int, Int, String, Date)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retProject = new Project(result)
    return retProject
  }


  def NewProject(): Project = {
    val insert = (projects returning projects.map(_.id)) += (-1, -1, "", new Date(0))
    val insertSeq: Future[Int] = db.run(insert)

    val projectId = Await.result(insertSeq, Duration.Inf)
    var result = new Project(projectId)
    return result
  }

  def UpdateProject(project: Project): Project = {
    val updated = projects.insertOrUpdate(project.id, project.client_id, project.name, project.end)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project")
    return project
  }

  def DeleteProject(project: Project): Project = {
    val query = projects.filter(_.id === project.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project")
    return project
  }

  def DeleteProject(id: Int): Int = {
    val query = projects.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project")
    return id
  }


}


