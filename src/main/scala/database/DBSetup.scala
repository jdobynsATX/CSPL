package cs345.database

import cs345.scheduler.datastructures._

import biweekly.Biweekly
import biweekly.component.VEvent
import slick.driver.H2Driver.api._
// import slick.driver.MySQLDriver.api._
import java.sql.Date
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob
import java.time.LocalDateTime
import java.time.ZoneId
//ISSUE: Blob is a placeholder for a user-defined type
trait DBObject {
  
}

object Employee {
  val BITS_NEED_FOR_HOURLY_SCHEDULE = 8760/8 //Temporary division
  val NAME_DEFAULT_VALUE = ""
  val RANK_DEFAULT_VALUE = -1
  val PAY_DEFAULT_VALUE = 0.0
  // val BITSET_DEFAULT: Blob = new SerialBlob(new Array[Byte](0))
  val SCHEDULE_DEFAULT:Array[Byte] = (new ScheduleMap()).toByteArray()
}

object Client {
  val NAME_DEFAULT_VALUE = ""
  val ADDDATE_DEFAULT_VALUE: Date = new Date(0)
  val BALANCE_DEFAULT = 0.0
}

object Meeting {
  val NAME_DEFAULT_VALUE = ""
  val CLIENT_DEFAULT = 1
  val START_DEFAULT_VALUE:Timestamp = new Timestamp(0)
  val END_DEFAULT_VALUE:Timestamp = new Timestamp(0)
}

object Project {
  val NAME_DEFAULT_VALUE = ""
  val CLIENT_DEFAULT = 1
  val END_DEFAULT_VALUE: Date = new Date(0)
}

object Payment {
  val CLIENT_DEFAULT = -1
  val RECEIVED_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val EMPLOYEE_DEFAULT = -1
  val AMOUNT_DEFAULT = 0.0
}

object Purchase {
  val CLIENT_DEFAULT = -1
  val PURCHASE_DATE_VALUE: Timestamp = new Timestamp(0)
  val EMPLOYEE_DEFAULT = -1
  val INVENTORY_DEFAULT = -1
  val COUNT_DEFAULT = 0
  val TOTAL_COST_DEFAULT = 0.0
}

object Shipment {
  val RECEIVED_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val EMPLOYEE_DEFAULT = -1
  val INVENTORY_DEFAULT = -1
  val COUNT_DEFAULT_VALUE = 0
  val TOTAL_COST_DEFAULT = 0.0
}

object Inventory {
  val NAME_DEFAULT_VALUE = ""
  val COUNT_DEFAULT_VALUE = 0
  val TOTAL_COST_DEFAULT = 0.0
  val TOTAL_EARNING_DEFAULT = 0.0
}


class Employee(var id: Int, var name: String, var rank: Int, var pay: Double, var schedule: ScheduleMap) extends DBObject {
  def this(id: Int) {
    this(id, Employee.NAME_DEFAULT_VALUE, Employee.RANK_DEFAULT_VALUE, Employee.PAY_DEFAULT_VALUE, 
      new ScheduleMap());
  }

  def this(data: (Int, String, Int, Double, Array[Byte])) {
    this(data._1, data._2, data._3, data._4, new ScheduleMap(data._5));
  }

  // def this(data: (Int, String, Int, Double, ScheduleMap)) {
  //   this(data._1, data._2, data._3, data._4, data._5);
  // }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + rank
    while( resultString.length < 52 ) resultString += " "
    resultString += "| " + pay
    return resultString
    //return "id: " + id + " name: " + name + " rank: " + rank + " pay: " + pay //+ " Schedule: " + schedule
  }
}

class Client(var id: Int, var name: String, var addDate: Date, var balance: Double) extends DBObject {
  def this(id: Int) {
    this(id, Client.NAME_DEFAULT_VALUE, Client.ADDDATE_DEFAULT_VALUE, Client.BALANCE_DEFAULT);
  }

  def this(data: (Int, String, Date, Double)) {
    this(data._1, data._2, data._3, data._4);
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + addDate
    while( resultString.length < 62 ) resultString += " "
    resultString += "| " + balance
    return resultString
    //return "id: " + id + ", name: " + name + ", dateAdded: " + addDate + ", Balance: " + balance
  }
}

class Meeting(var id: Int, var client_id: Int, var name: String, var start: Timestamp, var end: Timestamp) 
      extends DBObject {
  def this(id: Int) {
    this(id, Meeting.CLIENT_DEFAULT, Meeting.NAME_DEFAULT_VALUE, Meeting.START_DEFAULT_VALUE, Meeting.END_DEFAULT_VALUE);
  }

  def this(data: (Int, Int, String, Timestamp, Timestamp)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  def setStart(time: LocalDateTime) {
    var zoneId = ZoneId.systemDefault(); 
    var epoch = time.atZone(zoneId).toEpochSecond();
    this.start = new Timestamp(epoch * 1000);
  }

  def setEnd(time: LocalDateTime) {
    var zoneId = ZoneId.systemDefault(); 
    var epoch = time.atZone(zoneId).toEpochSecond();
    this.end = new Timestamp(epoch * 1000);
  }

  def getCalEvent(): VEvent = {
    var event = new VEvent()
    event.setDateStart(this.start)
    event.setDateEnd(this.end)
    event.setSummary(this.name)
    // event.setLocation("NONE")
    return event
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + start
    while( resultString.length < 62 ) resultString += " "
    resultString += "| " + end
    return resultString
    //return "id: " + id + ", name: " + name + ", Start Time: " + start + ", End Time: " + end
  }
}

class Project(var id: Int, var client_id: Int, var name: String, var end: Date)
  extends DBObject {
  def this(id: Int) {
    this(id, Project.CLIENT_DEFAULT, Project.NAME_DEFAULT_VALUE, Project.END_DEFAULT_VALUE);
  }

  def this(data: (Int, Int, String, Date)) {
    this(data._1, data._2, data._3, data._4);
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + end
    return resultString
    //return "id: " + id + ", name: " + name + ", End Time: " + end
  }
}

class Payment(var id: Int, var client_id: Int, var emp_id: Int, var amount: Double, var received: Timestamp)
  extends DBObject {
  def this(id: Int) {
    this(id, Payment.CLIENT_DEFAULT, Payment.EMPLOYEE_DEFAULT, Payment.AMOUNT_DEFAULT, Payment.RECEIVED_DEFAULT_VALUE);
  }

  def this(data: (Int, Int, Int, Double, Timestamp)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + client_id
    while( resultString.length < 21 ) resultString += " "
    resultString += "| " + emp_id
    while( resultString.length < 32 ) resultString += " "
    resultString += "| " + amount
    while( resultString.length < 43 ) resultString += " "
    resultString += "| " + received
    return resultString
    //return "id: " + id + ", Client: " + client_id + ", Employee: " + emp_id + ", Amount: " + amount + ", Received: " + received
  }
}

class Purchase(var id: Int, var client_id: Int, var emp_id: Int, var inv_id: Int, var count: Int, var total_cost: Double, var purchase_date: Timestamp)
  extends DBObject {
  def this(id: Int) {
    this(id, Purchase.CLIENT_DEFAULT, Purchase.EMPLOYEE_DEFAULT, Purchase.INVENTORY_DEFAULT, Purchase.COUNT_DEFAULT, Purchase.TOTAL_COST_DEFAULT, Purchase.PURCHASE_DATE_VALUE);
  }

  def this(data: (Int, Int, Int, Int, Int, Double, Timestamp)) {
    this(data._1, data._2, data._3, data._4, data._5, data._6, data._7);
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + client_id
    while( resultString.length < 21 ) resultString += " "
    resultString += "| " + emp_id
    while( resultString.length < 32 ) resultString += " "
    resultString += "| " + inv_id
    while( resultString.length < 43 ) resultString += " "
    resultString += "| " + count
    while( resultString.length < 54 ) resultString += " "
    resultString += "| " + total_cost
    while( resultString.length < 65 ) resultString += " "
    resultString += "| " + purchase_date
    return resultString
    //return "id: " + id + ", Client: " + client_id + ", Employee: " + emp_id + ", Inventory: " + inv_id + ", Count: " + count + ", Total Cost: " + total_cost + ", Purchase Date: " + purchase_date
  }
}

class Shipment(var id: Int, var emp_id: Int, var inv_id: Int, var count: Int, var total_cost: Double, var received: Timestamp)
  extends DBObject {
  def this(id: Int) {
    this(id, Shipment.EMPLOYEE_DEFAULT, Shipment.INVENTORY_DEFAULT, Shipment.COUNT_DEFAULT_VALUE, Shipment.TOTAL_COST_DEFAULT, Shipment.RECEIVED_DEFAULT_VALUE);
  }

  def this(data: (Int, Int, Int, Int, Double, Timestamp)) {
    this(data._1, data._2, data._3, data._4, data._5, data._6);
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + emp_id
    while( resultString.length < 21 ) resultString += " "
    resultString += "| " + inv_id
    while( resultString.length < 32 ) resultString += " "
    resultString += "| " + count
    while( resultString.length < 43 ) resultString += " "
    resultString += "| " + total_cost
    while( resultString.length < 54 ) resultString += " "
    resultString += "| " + received
    return resultString
    //return "id: " + id + ", Employee: " + emp_id + ", Inventory: " + inv_id + ", Count: " + count + ", Total Cost: " + total_cost + ", Received: " + received
  }
}

class Inventory(var id: Int, var name: String, var count: Int, var total_cost: Double, var total_earning: Double)
  extends DBObject {
  def this(id: Int) {
    this(id, Inventory.NAME_DEFAULT_VALUE, Inventory.COUNT_DEFAULT_VALUE, Inventory.TOTAL_COST_DEFAULT, Inventory.TOTAL_EARNING_DEFAULT);
  }

  def this(data: (Int, String, Int, Double, Double)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  override def toString: String = {
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + count
    while( resultString.length < 52 ) resultString += " "
    resultString += "| " + total_cost
    while( resultString.length < 63 ) resultString += " "
    resultString += "| " + total_earning
    return resultString
    //return "id: " + id + ", Name: " + name + ", Count: " + count + ", Total Cost: " + total_cost + ", Total Earning: " + total_earning
  }
}

object DBSetup {

  class Employees(tag: Tag) extends Table[(Int, String, Int, Double, Array[Byte])](tag, "EMPLOYEES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def rank = column[Int]("RANK")
    def pay = column[Double]("PAY")
    def schedule = column[Array[Byte]]("SCHEDULE")
    def * = (id, name, rank, pay, schedule)
  }
  val employees = TableQuery[Employees]

  class Clients(tag: Tag) extends Table[(Int, String, Date, Double)](tag, "CLIENTS") {
    def id = column[Int]("CLIENT_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CLIENT_NAME")
    def addDate = column[Date]("DATE_ADDED")
    def balance = column[Double]("BALANCE")
    def * = (id, name, addDate, balance)
  }
  val clients = TableQuery[Clients]

  class Projects(tag: Tag) extends Table[(Int, Int, String, Date)](tag, "PROJECTS") {
    def id = column[Int]("PROJECT_ID", O.PrimaryKey, O.AutoInc)
    def client_id = column[Int]("CLIENT_ID")
    def name = column[String]("PROJECT_DESCRIPTION")
    def end = column[Date]("COMPLETION_DATE")
    def client = foreignKey("PROJ_CLIENT_FK", client_id, clients)(_.id)
    def * = (id, client_id, name, end)
  }
  val projects = TableQuery[Projects]

  //ISSUE: Foreign key shenanigans
  class Meetings(tag: Tag) extends Table[(Int, Int, String, Timestamp, Timestamp)](tag, "MEETINGS") {
    def id = column[Int]("MEETING_ID", O.PrimaryKey, O.AutoInc)
    def client_id = column[Int]("CLIENT_ID")
    def name = column[String]("MEETING_DESCRIPTION")
    def start = column[Timestamp] ("START_TIME")
    def end = column[Timestamp]("END_TIME")
    def client = foreignKey("MEET_CLIENT_FK", client_id, clients)(_.id)
    def * = (id, client_id, name, start, end)
  }
  val meetings = TableQuery[Meetings]

  //add foreign keys
  class Payments(tag: Tag) extends Table[(Int, Int, Int, Double, Timestamp)](tag, "PAYMENTS") {
    def id = column[Int]("PAYMENT_ID", O.PrimaryKey, O.AutoInc)
    def client_id = column[Int]("CLIENT_ID")
    def emp_id = column[Int]("EMP_ID")
    def amount = column[Double]("AMOUNT")
    def received = column[Timestamp]("RECEIVED")
    def * = (id, client_id, emp_id, amount, received)
  }
  val payments = TableQuery[Payments]

  //add foreign keys
  class Purchases(tag: Tag) extends Table[(Int, Int, Int, Int, Int, Double, Timestamp)](tag, "PURCHASES") {
    def id = column[Int]("PURCHASE_ID", O.PrimaryKey, O.AutoInc)
    def client_id = column[Int]("CLIENT_ID")
    def emp_id = column[Int]("EMP_ID")
    def inv_id = column[Int]("INV_ID")
    def count = column[Int]("COUNT")
    def total_cost = column[Double]("TOTAL_COST")
    def purchase_date = column[Timestamp]("PURCHASE_DATE")
    def * = (id, client_id, emp_id, inv_id, count, total_cost, purchase_date)
  }
  val purchases = TableQuery[Purchases]

  //add foreign keys
  class Shipments(tag: Tag) extends Table[(Int, Int, Int, Int, Double, Timestamp)](tag, "" +
    "SHIPMENTS") {
    def id = column[Int]("PURCHASE_ID", O.PrimaryKey, O.AutoInc)
    def emp_id = column[Int]("EMP_ID")
    def inv_id = column[Int]("INV_ID")
    def count = column[Int]("COUNT")
    def total_cost = column[Double]("TOTAL_COST")
    def received = column[Timestamp]("RECEIVED")
    def * = (id, emp_id, inv_id, count, total_cost, received)
  }
  val shipments = TableQuery[Shipments]

  //add foreign keys
  class Inventorys(tag: Tag) extends Table[(Int, String, Int, Double, Double)](tag, "" +
    "INVENTORYS") {
    def id = column[Int]("PURCHASE_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def count = column[Int]("COUNT")
    def total_cost = column[Double]("TOTAL_COST")
    def total_earning = column[Double]("TOTAL_EARNING")
    def * = (id, name, count, total_cost, total_earning)
  }
  val inventorys = TableQuery[Inventorys]


  class MeetingJoin(tag: Tag) extends Table[(Int, Int)](tag, "MEET_JOIN") {
    def meeting_id = column[Int]("MEETING_ID")
    def emp_id = column[Int]("EMP_ID")
    foreignKey("MEETING_FK", meeting_id, meetings)(_.id)
    foreignKey("EMP_FK", emp_id, employees)(_.id)
    def * = (meeting_id, emp_id)
  }
  val meetingJoinTable = TableQuery[MeetingJoin]
  
  class ProjectJoin(tag: Tag) extends Table[(Int, Int)](tag, "PROJECT_JOIN") {
    def project_id = column[Int]("PROJECT_ID")
    def emp_id = column[Int]("EMP_ID")
    foreignKey("PROJECT_FK", project_id, meetings)(_.id)
    foreignKey("EMP_FK", emp_id, employees)(_.id)
    def * = (project_id, emp_id)
  }
  val projectJoinTable = TableQuery[ProjectJoin]

  val default_blob : Blob = new SerialBlob(Array[Byte](0))
  val setupSequence = DBIO.seq(
    // Create the tables, including primary and foreign keys

    (employees.schema ++ clients.schema ++ meetings.schema
      ++ projects.schema ++ inventorys.schema ++ shipments.schema ++
      payments.schema ++ purchases.schema ++ meetingJoinTable.schema ++ projectJoinTable.schema).create,
    //employees += (0, "Existing One", 5, 75.5, default_blob),
    //employees += (0, "Existing Two", 3, 78.95, default_blob),
    clients += (1, "SELF", Client.ADDDATE_DEFAULT_VALUE, 0)
  )
   // meetings += (0, 1, "M0", Meeting.START_DEFAULT_VALUE, Meeting.END_DEFAULT_VALUE),
 //   inventorys +=(0, "item1", 4, 342.2, 0)
}