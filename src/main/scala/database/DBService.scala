package cs345.database


import cs345.scheduler.datastructures._

import scala.concurrent.{Future, Await}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

// Use H2Driver to connect to an H2 database
import slick.driver.H2Driver.api._
import java.sql.Date
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

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


  def AssignEmployeeMeeting(emp_id: Int, meet_id: Int)= {
    val insert = meetingJoinTable += (meet_id, emp_id)
    val insertSeq: Future[Int] = db.run(insert)
    val insertComplete = Await.result(insertSeq, Duration.Inf)
    //DEBUG/ISSUE IMPORTANT: Schedule the employee here
  }
 
  def AssignEmployeeProject(emp_id: Int, pro_id: Int)= {
    val insert = projectJoinTable += (pro_id, emp_id)
    val insertSeq: Future[Int] = db.run(insert)
    val insertComplete = Await.result(insertSeq, Duration.Inf)
    //DEBUG/ISSUE IMPORTANT: Schedule the employee here
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
    println("    ID    |             NAME             |       START        |   END")
    db.run(meetings.result).map(_.foreach {
      case (id, client_id, name, start, end) =>
        println("  " + id + "\t" + client_id + "\t" + name + "\t" + start + "\t" + end + "\t")
    })
  }

  def GetAllMeetings(): Array[Meeting] = {
    var result: Array[Meeting] = new Array[Meeting](0)
    val f: Future[Seq[(Int, Int, String, Timestamp, Timestamp)]] = db.run(meetings.result)
    val queryResult: Seq[(Int, Int, String, Timestamp, Timestamp)] = Await.result(f, Duration.Inf)
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
    val f: Future[(Int, Int, String, Timestamp, Timestamp)] = db.run(action)

    val result = Await.result(f, Duration.Inf)
    val retMeeting = new Meeting(result)
    return retMeeting
  }

  def NewMeeting(): Meeting = {
    val insert = (meetings returning meetings.map(_.id)) += (-1, 1 ,"", new Timestamp(0), new Timestamp(0))
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
    println("    ID    |             NAME             |   END")
    db.run(projects.result).map(_.foreach {
      case (id, client_id, name, end) =>
        println("  " + id + "\t" + client_id + "\t" + name + "\t" + end + "\t")
    })
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

  def ListAllPayments() = {
    println("Payments:")
    println("    ID    |  CLIENT  | EMPLOYEE |  AMOUNT  | RECIEVED")
    db.run(payments.result).map(_.foreach {
      case (id, client_id, emp_id, amount, received) =>
        println("  " + id + "\t" + client_id + "\t" + emp_id + "\t" + amount + "\t" +received + "\t")
    })
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
    val insert = (payments returning payments.map(_.id)) += (-1, -1, -1, 0.0, new Timestamp(0))
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
    db.run(purchases.result).map(_.foreach {
      case (id, client_id, emp_id, inv_id, quantity, total_cost, purchase_date) =>
        println("  " + id + "\t" + client_id + "\t" + emp_id + "\t" + inv_id + "\t" + quantity + "\t" + total_cost + "\t" + purchase_date + "\t")
    })
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
    val insert = (purchases returning purchases.map(_.id)) += (-1, -1, -1, -1, 0, 0.0, new Timestamp(0))
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
    db.run(shipments.result).map(_.foreach {
      case (id, emp_id, inv_id, quantity, total_cost, received) =>
        println("  " + id + "\t" + emp_id + "\t" + inv_id + "\t" + quantity + "\t" + total_cost + "\t" + received + "\t")
    })
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
    val insert = (shipments returning shipments.map(_.id)) += (-1, -1, -1, 0, 0.0, new Timestamp(0))
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
    db.run(inventorys.result).map(_.foreach {
      case (id, name, quantity, total_cost, total_earning) =>
        println("  " + id + "\t" + name + "\t" + quantity + "\t" + total_cost + "\t" + total_earning + "\t")
    })
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