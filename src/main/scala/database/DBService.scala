package cs345.database

import cs345.scheduler.datastructures._
import cs345.scheduler._

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import java.sql.Date
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob
import java.time.LocalDateTime

object DBService {
  val db = Database.forConfig("h2mem1")

  val setupFuture = db.run(DBSetup.setupSequence)
  Await.result(setupFuture, Duration.Inf)

  val meetings = DBSetup.meetings
  val clients = DBSetup.clients
  val employees = DBSetup.employees
  val projects = DBSetup.projects
  val payments = DBSetup.payments
  val purchases = DBSetup.purchases
  val shipments = DBSetup.shipments
  val inventorys = DBSetup.inventorys


  val meetingJoinTable = DBSetup.meetingJoinTable
  val projectJoinTable = DBSetup.projectJoinTable

  def Stop() = {
    db.close
  }

  def ListAllEmployees() = {
    // Read all coffees and print them to the console
    println("Employees:")
    println("    ID    |             NAME             |   RANK   |   PAY    ")
    val employees: Seq[Employee] = GetAllEmployees()
    for (emp <- employees) {
      println(emp)
    }
  }

  def GetAllEmployees(): Array[Employee] = {
    var result: Array[Employee] = new Array[Employee](0)
    val f: Future[Seq[(Int, String, Int, Double,  Array[Byte])]] = db.run(employees.result)
    val queryResult: Seq[(Int, String, Int, Double,  Array[Byte])] = Await.result(f, Duration.Inf)
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
    val f: Future[(Int, String, Int, Double,  Array[Byte])] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val e = new Employee(result)
    return e
  }

  def GetEmployee(name: String): Employee = {
    val query = for {
      emp <- employees if emp.name === name
    } yield emp
    val action = query.result.head
    val f: Future[(Int, String, Int, Double,  Array[Byte])] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val e = new Employee(result)
    return e
  }

  def NewEmployee(): Employee = {
    val default_blob = (new ScheduleMap()).toByteArray()
    val insert = (employees returning employees.map(_.id)) += (-1, "", -1, -1, default_blob)
    val insertSeq: Future[Int] = db.run(insert)

    val empId = Await.result(insertSeq, Duration.Inf)
    var result = new Employee(empId)
    return result
  }

  def UpdateEmployee(emp: Employee): Employee = {
    val updated = employees.insertOrUpdate(emp.id, emp.name, emp.rank, emp.pay, emp.schedule.toByteArray())
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

  def AssignEmployeeMeeting(emp_id: Int, meet_id: Int) : Int= {
    val insert = meetingJoinTable += (meet_id, emp_id)
    val insertSeq: Future[Int] = db.run(insert)
    val insertComplete = Await.result(insertSeq, Duration.Inf)
    return meet_id
  }

  def AddEmployeeToMeeting(emp: Employee, id: Int) {
    var meeting = DBService.GetMeeting(id)
    // Remove previous assignment of meeting, if any.
    val prevEmpList = DBService.GetEmployeesForMeeting(id)
    for (emp <- prevEmpList) {
      emp.schedule.setFree(meeting.getStartTime(), meeting.getEndTime())
      DBService.UpdateEmployee(emp)
    }

    // Assign employee to meeting.
    DBService.AssignEmployeeMeeting(emp.id, id)

    var newStartTime: LocalDateTime = LocalDateTime.now()
    // Use scheduling algorithm based on if meeting already has time or not.
    var empList: Seq[Employee] = DBService.GetEmployeesForMeeting(id)
    if (meeting.start.getTime() != 0) {
      // println("Has PREV start time: " + meeting.getStartTime())
      newStartTime = Scheduler.firstAvailableTimeFromTime(meeting, empList, meeting.getStartTime())
    } else {
      newStartTime = Scheduler.firstAvailableTimeFromNow(meeting, empList)
    }
    
    // Set the new time, then update all employees and meetings.
    meeting.setStart(newStartTime)
    for (emp <- empList) {
      emp.schedule.setBusy(meeting.getStartTime(), meeting.getEndTime())
      DBService.UpdateEmployee(emp)
    }
    DBService.UpdateMeeting(meeting)
  }
 
  def AssignEmployeeProject(emp_id: Int, pro_id: Int) : Int = {
    val insert = projectJoinTable += (pro_id, emp_id)
    val insertSeq: Future[Int] = db.run(insert)
    val insertComplete = Await.result(insertSeq, Duration.Inf)
    return pro_id
  }
 
   def ListAllMeetingAssignments() = {
    println("Meeting Assignments:")
    db.run(meetingJoinTable.result).map(_.foreach {
      case (meet_id, emp_id) =>
        println("  " + meet_id + "\t" + emp_id + "\t")
    })
  }
 
  def ListAllProjectAssignments() = {
    println("Project Assignments:")
    db.run(projectJoinTable.result).map(_.foreach {
      case (meet_id, emp_id) =>
        println("  " + meet_id + "\t" + emp_id + "\t")
    })
  }

  def ListAllClients() = {
    println("Clients:")
    println("    ID    |             NAME             |        DATE        | BALANCE  ")
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

  def GetClient(name: String): Client = {
    val query = for {
      client <- clients if client.name === name
    } yield client
    val action = query.result.head
    val f: Future[(Int, String, Date, Double)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retClient = new Client(result)
    return retClient
  }

  def NewClient(): Client = {
    val insert = (clients returning clients.map(_.id)) += (-1, "", new Date(System.currentTimeMillis()), 0.0)
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
    println("    ID    |             NAME             |            START             |   END")
    val meetings: Seq[Meeting] = GetAllMeetings()
    for (meeting <- meetings) {
      println(meeting)
    }
  }

  def GetAllMeetings(): Array[Meeting] = {
    var result: Array[Meeting] = new Array[Meeting](0)
    val f: Future[Seq[(Int, Int, String, Timestamp, Int)]] = db.run(meetings.result)
    val queryResult: Seq[(Int, Int, String, Timestamp, Int)] = Await.result(f, Duration.Inf)
    for (mtngData <- queryResult) {
      result = result :+ (new Meeting(mtngData))
    }
    return result
  }

  def GetMeeting(id: Int): Meeting = {
    val query = for {
      meeting <- meetings if meeting.id === id
    } yield meeting
    val action = query.result.head
    val f: Future[(Int, Int, String, Timestamp, Int)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retMeeting = new Meeting(result)
    return retMeeting
  }

  def GetMeeting(name: String): Meeting = {
    val query = for {
      meeting <- meetings if meeting.name === name
    } yield meeting
    val action = query.result.head
    val f: Future[(Int, Int, String, Timestamp, Int)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retMeeting = new Meeting(result)
    return retMeeting
  }

  def GetMeetingsForEmployee(id: Int): Array[Meeting] = {
    var result: Array[Meeting] = new Array[Meeting](0)
    val meetingList = for {
      mid <- meetingJoinTable if mid.emp_id === id
      m <- meetings if m.id === mid.meeting_id
    } yield m
    val f: Future[Seq[(Int, Int, String, Timestamp, Int)]] = db.run(meetingList.result)
    val queryResult: Seq[(Int, Int, String, Timestamp, Int)] = Await.result(f, Duration.Inf)
    for(meetData <- queryResult) {
      result = result :+ (new Meeting(meetData))
    }
    return result
  }

  def GetEmployeesForMeeting(id: Int): Array[Employee] = {
    var result: Array[Employee] = new Array[Employee](0)
    val empList = for {
      eid <- meetingJoinTable if eid.meeting_id === id
      e <- employees if e.id === eid.emp_id
    } yield e
    val f: Future[Seq[(Int, String, Int, Double, Array[Byte])]] = db.run(empList.result)
    val queryResult: Seq[(Int, String, Int, Double, Array[Byte])] = Await.result(f, Duration.Inf)
    for(empData <- queryResult) {
      result = result :+ (new Employee(empData))
    }
    return result
  }

  def NewMeeting(): Meeting = {
    val insert = (meetings returning meetings.map(_.id)) += (-1, 1 ,"", new Timestamp(0), 0)
    val insertSeq: Future[Int] = db.run(insert)

    val meetingId = Await.result(insertSeq, Duration.Inf)
    var result = new Meeting(meetingId)
    return result
  }

  def UpdateMeeting(meeting: Meeting): Meeting = {
    val updated = meetings.insertOrUpdate(meeting.id, meeting.client_id, meeting.name, meeting.start, meeting.durationMinutes)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting")
    return meeting
  }

  def DeleteMeeting(meeting: Meeting): Meeting = {
    val meetID = meeting.id
    val query = meetings.filter(_.id === meetID)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting")

    val query2 = meetingJoinTable.filter(_.meeting_id === meetID)
    val action2 = query2.delete
    val affectedRowsCount2 = db.run(action2)

    if (Await.result(affectedRowsCount2, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting/employee relation")

    return meeting
  }

  def DeleteMeeting(id: Int): Int = {
    val query = meetings.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting")

    val query2 = meetingJoinTable.filter(_.meeting_id === id)
    val action2 = query2.delete
    val affectedRowsCount2 = db.run(action2)

    if (Await.result(affectedRowsCount2, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find meeting/employee relation")

    return id
  }

  def ListAllProjects() = {
    println("Projects:")
    println("    ID    |             NAME             |   END")
    /*db.run(projects.result).map(_.foreach {
      case (id, client_id, name, end) =>
        println("  " + id + "\t" + client_id + "\t" + name + "\t" + end + "\t")
    })*/
    val projects: Seq[Project] = GetAllProjects()
    for (project <- projects) {
      println(project)
    }
  }

  def GetAllProjects(): Array[Project] = {
    var result: Array[Project] = new Array[Project](0)
    val f: Future[Seq[(Int, Int, String, Date)]] = db.run(projects.result)
    val queryResult: Seq[(Int, Int, String, Date)] = Await.result(f, Duration.Inf)
    for (projData <- queryResult) {
      result = result :+ (new Project(projData))
    }
    return result
  }

  def GetProjectsForEmployee(id: Int): Array[Project] = {
    var result: Array[Project] = new Array[Project](0)
    val projectList = for {
      pid <- projectJoinTable if pid.emp_id === id
      p <- projects if p.id === pid.project_id
    } yield p
    val f: Future[Seq[(Int, Int, String, Date)]] = db.run(projectList.result)
    val queryResult: Seq[(Int, Int, String, Date)] = Await.result(f, Duration.Inf)
    for(projData <- queryResult) {
      result = result :+ (new Project(projData))
    }
    return result
  }

  def GetEmployeesForProject(id: Int): Array[Employee] = {
    var result: Array[Employee] = new Array[Employee](0)
    val empList = for {
      eid <- projectJoinTable if eid.project_id === id
      e <- employees if e.id === eid.emp_id
    } yield e
    val f: Future[Seq[(Int, String, Int, Double, Array[Byte])]] = db.run(empList.result)
    val queryResult: Seq[(Int, String, Int, Double, Array[Byte])] = Await.result(f, Duration.Inf)
    for(empData <- queryResult) {
      result = result :+ (new Employee(empData))
    }
    return result
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

  def GetProject(name: String): Project = {
    val query = for {
      project <- projects if project.name === name
    } yield project
    val action = query.result.head
    val f: Future[(Int, Int, String, Date)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retProject = new Project(result)
    return retProject
  }


  def NewProject(): Project = {
    val insert = (projects returning projects.map(_.id)) += (-1, 1, "", new Date(0))
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
    val projID = project.id
    val query = projects.filter(_.id === projID)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project")

    val query2 = projectJoinTable.filter(_.project_id === projID)
    val action2 = query2.delete
    val affectedRowsCount2 = db.run(action2)

    if (Await.result(affectedRowsCount2, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project/employee relation")

    return project
  }

  def DeleteProject(id: Int): Int = {
    val query = projects.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project")

    val query2 = projectJoinTable.filter(_.project_id === id)
    val action2 = query2.delete
    val affectedRowsCount2 = db.run(action2)

    if (Await.result(affectedRowsCount2, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find project/employee relation")

    return id
  }

  def ListAllPayments() = {
    println("Payments:")
    println("    ID    |  CLIENT  | EMPLOYEE |  AMOUNT  | RECIEVED")
    val payments: Seq[Payment] = GetAllPayments()
    for (payment <- payments) {
      println(payment)
    }
  }

  def GetAllPayments(): Array[Payment] = {
    var result: Array[Payment] = new Array[Payment](0)
    val f: Future[Seq[(Int, Int, Int, Double, Timestamp)]] = db.run(payments.result)
    val queryResult: Seq[(Int, Int, Int, Double, Timestamp)] = Await.result(f, Duration.Inf)
    for (paymentData <- queryResult) {
      result = result :+ (new Payment(paymentData))
    }
    return result
  }

  def GetPayment(id: Int): Payment = {
    val query = for {
      payment <- payments if payment.id === id
    } yield payment
    val action = query.result.head
    val f: Future[(Int, Int, Int, Double, Timestamp)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retPayment = new Payment(result)
    return retPayment
  }


  def NewPayment(): Payment = {
    val insert = (payments returning payments.map(_.id)) += (-1, -1, -1, 0.0, new Timestamp(System.currentTimeMillis()))
    val insertSeq: Future[Int] = db.run(insert)

    val paymentId = Await.result(insertSeq, Duration.Inf)
    var result = new Payment(paymentId)
    return result
  }

  def UpdatePayment(payment: Payment): Payment = {
    val updated = payments.insertOrUpdate(payment.id, payment.client_id, payment.emp_id, payment.amount, payment.received)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find payment")
    return payment
  }

  def DeletePayment(payment: Payment): Payment = {
    val query = payments.filter(_.id === payment.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find payment")
    return payment
  }

  def DeletePayment(id: Int): Int = {
    val query = payments.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find payment")
    return id
  }

  def ListAllPurchases() = {
    println("Purchases:")
    println("    ID    |  CLIENT  | EMPLOYEE | INVENTORY|  COUNT   |   COST   | PURCHASED")
    val purchases: Seq[Purchase] = GetAllPurchases()
    for (purchase <- purchases) {
      println(purchase)
    }
  }

  def GetAllPurchases(): Array[Purchase] = {
    var result: Array[Purchase] = new Array[Purchase](0)
    val f: Future[Seq[(Int, Int, Int, Int, Int, Double, Timestamp)]] = db.run(purchases.result)
    val queryResult: Seq[(Int, Int, Int, Int, Int, Double, Timestamp)] = Await.result(f, Duration.Inf)
    for (purchaseData <- queryResult) {
      result = result :+ (new Purchase(purchaseData))
    }
    return result
  }

  def GetPurchase(id: Int): Purchase = {
    val query = for {
      purchase <- purchases if purchase.id === id
    } yield purchase
    val action = query.result.head
    val f: Future[(Int, Int, Int, Int, Int, Double, Timestamp)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retPurchase = new Purchase(result)
    return retPurchase
  }


  def NewPurchase(): Purchase = {
    val insert = (purchases returning purchases.map(_.id)) += (-1, -1, -1, -1, 0, 0.0, new Timestamp(System.currentTimeMillis()))
    val insertSeq: Future[Int] = db.run(insert)

    val purchaseId = Await.result(insertSeq, Duration.Inf)
    var result = new Purchase(purchaseId)
    return result
  }

  def UpdatePurchase(purchase: Purchase): Purchase = {
    val updated = purchases.insertOrUpdate(purchase.id, purchase.client_id, purchase.emp_id, purchase.inv_id, purchase.quantity, purchase.total_cost, purchase.purchase_date)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find purchase")
    return purchase
  }

  def DeletePurchase(purchase: Purchase): Purchase = {
    val query = purchases.filter(_.id === purchase.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find purchase")
    return purchase
  }

  def DeletePurchase(id: Int): Int = {
    val query = purchases.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find purchase")
    return id
  }

  def ListAllShipments() = {
    println("Shipments:")
    println("    ID    | EMPLOYEE | INVENTORY|  COUNT   |   COST   | RECIEVED")
    val shipments: Seq[Shipment] = GetAllShipments()
    for (shipment <- shipments) {
      println(shipment)
    }
  }

  def GetAllShipments(): Array[Shipment] = {
    var result: Array[Shipment] = new Array[Shipment](0)
    val f: Future[Seq[(Int, Int, Int, Int, Double, Timestamp)]] = db.run(shipments.result)
    val queryResult: Seq[(Int, Int, Int, Int, Double, Timestamp)] = Await.result(f, Duration.Inf)
    for (shipmentData <- queryResult) {
      result = result :+ (new Shipment(shipmentData))
    }
    return result
  }

  def GetShipment(id: Int): Shipment = {
    val query = for {
      shipment <- shipments if shipment.id === id
    } yield shipment
    val action = query.result.head
    val f: Future[(Int, Int, Int, Int, Double, Timestamp)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retShipment = new Shipment(result)
    return retShipment
  }

  def NewShipment(): Shipment = {
    val insert = (shipments returning shipments.map(_.id)) += (-1, -1, -1, 0, 0.0, new Timestamp(System.currentTimeMillis()))
    val insertSeq: Future[Int] = db.run(insert)

    val shipmentId = Await.result(insertSeq, Duration.Inf)
    var result = new Shipment(shipmentId)
    return result
  }

  def UpdateShipment(shipment: Shipment): Shipment = {
    val updated = shipments.insertOrUpdate(shipment.id, shipment.emp_id, shipment.inv_id, shipment.quantity,  shipment.total_cost, shipment.received)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find shipment")
    return shipment
  }

  def DeleteShipment(shipment: Shipment): Shipment = {
    val query = shipments.filter(_.id === shipment.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find shipment")
    return shipment
  }

  def DeleteShipment(id: Int): Int = {
    val query = shipments.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find shipment")
    return id
  }

  def ListAllInventorys() = {
    println("Inventories:")
    println("    ID    |             NAME             |  COUNT   |   COST   | EARNING")
    val inventories: Seq[Inventory] = GetAllInventorys()
    for (inventory <- inventories) {
      println(inventory)
    }
  }

  def GetAllInventorys(): Array[Inventory] = {
    var result: Array[Inventory] = new Array[Inventory](0)
    val f: Future[Seq[(Int, String, Int, Double, Double)]] = db.run(inventorys.result)
    val queryResult: Seq[(Int, String, Int, Double, Double)] = Await.result(f, Duration.Inf)
    for (invData <- queryResult) {
      result = result :+ (new Inventory(invData))
    }
    return result
  }

  def GetInventory(id: Int): Inventory = {
    val query = for {
      inventory <- inventorys if inventory.id === id
    } yield inventory
    val action = query.result.head
    val f: Future[(Int, String, Int, Double, Double)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retInventory = new Inventory(result)
    return retInventory
  }

  def GetInventory(name: String): Inventory = {
    val query = for {
      inventory <- inventorys if inventory.name === name
    } yield inventory
    val action = query.result.head
    val f: Future[(Int, String, Int, Double, Double)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retInventory = new Inventory(result)
    return retInventory
  }


  def NewInventory(): Inventory = {
    val insert = (inventorys returning inventorys.map(_.id)) += (-1, "", 0, 0.0, 0.0)
    val insertSeq: Future[Int] = db.run(insert)

    val inventoryId = Await.result(insertSeq, Duration.Inf)
    var result = new Inventory(inventoryId)
    return result
  }

  def UpdateInventory(inventory: Inventory): Inventory = {
    val updated = inventorys.insertOrUpdate(inventory.id, inventory.name, inventory.quantity, inventory.total_cost, inventory.total_earning)
    val updateSeq: Future[Int] = db.run(updated)

    if (Await.result(updateSeq, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find inventory")
    return inventory
  }

  def DeleteInventory(inventory: Inventory): Inventory = {
    val query = inventorys.filter(_.id === inventory.id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find inventory")
    return inventory
  }

  def DeleteInventory(id: Int): Int = {
    val query = inventorys.filter(_.id === id)
    val action = query.delete
    val affectedRowsCount: Future[Int] = db.run(action)

    if (Await.result(affectedRowsCount, Duration.Inf) <= 0)
      throw new RuntimeException("Did not find inventory")
    return id
  }

}