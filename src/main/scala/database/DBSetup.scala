package cs345.database

import cs345.scheduler.datastructures._
 
import biweekly.Biweekly
import biweekly.component.VEvent
import biweekly.property.Attendee
import biweekly.property.Comment
import slick.driver.H2Driver.api._
// import slick.driver.MySQLDriver.api._

import java.util.Calendar
import java.sql.Date
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.Instant

trait DBObject {
  
}

object Employee {
  val BITS_NEED_FOR_HOURLY_SCHEDULE = 8760/8 //Temporary division
  val NAME_DEFAULT_VALUE = ""
  val RANK_DEFAULT_VALUE = -1
  val PAY_DEFAULT_VALUE = 0.0
  val SCHEDULE_DEFAULT:Array[Byte] = (new ScheduleMap()).toByteArray()
}

object Client {
  val NAME_DEFAULT_VALUE = ""
  val ADDDATE_DEFAULT_VALUE: Date = new Date(1480906361)
  val BALANCE_DEFAULT = 0.0
}

object Meeting {
  val NAME_DEFAULT_VALUE = ""
  val CLIENT_DEFAULT = 1
  val START_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val END_DEFAULT_VALUE: Timestamp = new Timestamp(0)
}

object Project {
  val NAME_DEFAULT_VALUE = ""
  val CLIENT_DEFAULT = 1
  val END_DEFAULT_VALUE: Date = new Date(0)
}

object Payment {
  val CLIENT_DEFAULT = 1
  val RECEIVED_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val EMPLOYEE_DEFAULT = -1
  val AMOUNT_DEFAULT = 0.0
}

object Purchase {
  val CLIENT_DEFAULT = 1
  val PURCHASE_DATE_VALUE: Timestamp = new Timestamp(0)
  val EMPLOYEE_DEFAULT = -1
  val INVENTORY_DEFAULT = -1
  val QUANTITY_DEFAULT_VALUE = 0
  val TOTAL_COST_DEFAULT = 0.0
}

object Shipment {
  val RECEIVED_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val EMPLOYEE_DEFAULT = -1
  val INVENTORY_DEFAULT = -1
  val QUANTITY_DEFAULT_VALUE = 0
  val TOTAL_COST_DEFAULT = 0.0
}

object Inventory {
  val NAME_DEFAULT_VALUE = ""
  val QUANTITY_DEFAULT_VALUE = 0
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

  def getAttendeeCard(): Attendee = {
    var attendee: Attendee = new Attendee(name, name.replaceAll("\\s+","").toLowerCase() + "@company.com")
    attendee.setRsvp(false)
    return attendee
  }

  override def toString: String = {
    val minimum = 28
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name.substring(0, minimum.min(name.length))
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + rank
    while( resultString.length < 52 ) resultString += " "
    resultString += "| " + pay
    return resultString
  }
}

class Client(var id: Int, var name: String, var addDate: Date, var balance: Double) extends DBObject {
  def this(id: Int) {
    this(id, Client.NAME_DEFAULT_VALUE, new Date(Calendar.getInstance().getTimeInMillis()), Client.BALANCE_DEFAULT);
  }

  def this(data: (Int, String, Date, Double)) {
    this(data._1, data._2, data._3, data._4);
  }

  def getComment(): Comment = {
    val comment = new Comment("Contact: " + name);
    return comment
  }

  override def toString: String = {
    val minimum = 28
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name.substring(0, minimum.min(name.length))
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + addDate
    while( resultString.length < 62 ) resultString += " "
    resultString += "| " + balance
    return resultString
  }
}

class Meeting(var id: Int, var client_id: Int, var name: String, var start: Timestamp, var durationMinutes: Int) 
      extends DBObject {
  def this(id: Int) {
    this(id, Meeting.CLIENT_DEFAULT, Meeting.NAME_DEFAULT_VALUE, Meeting.START_DEFAULT_VALUE, 0);
  }

  def this(data: (Int, Int, String, Timestamp, Int)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  def changeDuration(durationMins: Int) {
    this.durationMinutes = durationMins
    // TODO: Need to recheck schedules.
  }

  def getEnd(): Timestamp = {
    return new Timestamp( start.getTime + durationMinutes * 60000 ) 
  }

  def getStartTime(): LocalDateTime = {
    val instant = Instant.now(); //can be LocalDateTime
    val systemZone = ZoneId.systemDefault(); // my timezone
    val currentOffsetForMyZone = systemZone.getRules().getOffset(instant); // DUMB Offset stuff
    var epochSecond = start.getTime() / 1000
    return LocalDateTime.ofEpochSecond(epochSecond, 0, currentOffsetForMyZone)
  }

  def getEndTime(): LocalDateTime = {
    val instant = Instant.now(); //can be LocalDateTime
    val systemZone = ZoneId.systemDefault(); // my timezone
    val currentOffsetForMyZone = systemZone.getRules().getOffset(instant); // DUMB Offset stuff
    var epochSecond = getEnd().getTime() / 1000
    return LocalDateTime.ofEpochSecond(epochSecond, 0, currentOffsetForMyZone)
  }

  def setStart(time: LocalDateTime) {
    var zoneId = ZoneId.systemDefault()
    var epoch = time.atZone(zoneId).toEpochSecond()
    this.start = new Timestamp(epoch * 1000)
  }
 
  def getCalEvent(): VEvent = {
    var event = new VEvent()
    event.setDateStart(this.start)
    event.setDateEnd(this.getEnd())
    event.setSummary(this.name)
    val employees: Seq[Employee] = DBService.GetEmployeesForMeeting(id)
    for (employee <- employees) {
      event.addAttendee(employee.getAttendeeCard())
    }
    event.addComment(DBService.GetClient(client_id).getComment())
    return event
  }

  override def toString: String = {
    val minimum = 28
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name.substring(0, minimum.min(name.length))
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + start
    while( resultString.length < 72 ) resultString += " "
    resultString += "| " + getEnd()
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
    val minimum = 28
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name.substring(0, minimum.min(name.length))
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + end
    return resultString
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
    val minimum = 28
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
  }
}

class Purchase(var id: Int, var client_id: Int, var emp_id: Int, var inv_id: Int, var quantity: Int, var total_cost: Double, var purchase_date: Timestamp)
  extends DBObject {
  def this(id: Int) {
    this(id, Purchase.CLIENT_DEFAULT, Purchase.EMPLOYEE_DEFAULT, Purchase.INVENTORY_DEFAULT, Purchase.QUANTITY_DEFAULT_VALUE, Purchase.TOTAL_COST_DEFAULT, Purchase.PURCHASE_DATE_VALUE);
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
    resultString += "| " + quantity
    while( resultString.length < 54 ) resultString += " "
    resultString += "| " + total_cost
    while( resultString.length < 65 ) resultString += " "
    resultString += "| " + purchase_date
    return resultString
  }
}

class Shipment(var id: Int, var emp_id: Int, var inv_id: Int, var quantity: Int, var total_cost: Double, var received: Timestamp)
  extends DBObject {
  def this(id: Int) {
    this(id, Shipment.EMPLOYEE_DEFAULT, Shipment.INVENTORY_DEFAULT, Shipment.QUANTITY_DEFAULT_VALUE, Shipment.TOTAL_COST_DEFAULT, Shipment.RECEIVED_DEFAULT_VALUE);
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
    resultString += "| " + quantity
    while( resultString.length < 43 ) resultString += " "
    resultString += "| " + total_cost
    while( resultString.length < 54 ) resultString += " "
    resultString += "| " + received
    return resultString
  }
}

class Inventory(var id: Int, var name: String, var quantity: Int, var total_cost: Double, var total_earning: Double)
  extends DBObject {
  def this(id: Int) {
    this(id, Inventory.NAME_DEFAULT_VALUE, Inventory.QUANTITY_DEFAULT_VALUE, Inventory.TOTAL_COST_DEFAULT, Inventory.TOTAL_EARNING_DEFAULT);
  }

  def this(data: (Int, String, Int, Double, Double)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  override def toString: String = {
    val minimum = 28
    var resultString = " " + id
    while( resultString.length < 10 ) resultString += " "
    resultString += "| " + name.substring(0, minimum.min(name.length))
    while( resultString.length < 41 ) resultString += " "
    resultString += "| " + quantity
    while( resultString.length < 52 ) resultString += " "
    resultString += "| " + total_cost
    while( resultString.length < 63 ) resultString += " "
    resultString += "| " + total_earning
    return resultString
    //return "id: " + id + ", Name: " + name + ", quantity: " + quantity + ", Total Cost: " + total_cost + ", Total Earning: " + total_earning
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
  class Meetings(tag: Tag) extends Table[(Int, Int, String, Timestamp, Int)](tag, "MEETINGS") {
    def id = column[Int]("MEETING_ID", O.PrimaryKey, O.AutoInc)
    def client_id = column[Int]("CLIENT_ID")
    def name = column[String]("MEETING_DESCRIPTION")
    def start = column[Timestamp] ("START_TIME")
    def duration = column[Int]("DURATION")
    def client = foreignKey("MEET_CLIENT_FK", client_id, clients)(_.id)
    def * = (id, client_id, name, start, duration)
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
    def quantity = column[Int]("QUANTITY")
    def total_cost = column[Double]("TOTAL_COST")
    def purchase_date = column[Timestamp]("PURCHASE_DATE")
    def * = (id, client_id, emp_id, inv_id, quantity, total_cost, purchase_date)
  }
  val purchases = TableQuery[Purchases]

  //add foreign keys
  class Shipments(tag: Tag) extends Table[(Int, Int, Int, Int, Double, Timestamp)](tag, "" +
    "SHIPMENTS") {
    def id = column[Int]("PURCHASE_ID", O.PrimaryKey, O.AutoInc)
    def emp_id = column[Int]("EMP_ID")
    def inv_id = column[Int]("INV_ID")
    def quantity = column[Int]("QUANTITY")
    def total_cost = column[Double]("TOTAL_COST")
    def received = column[Timestamp]("RECEIVED")
    def * = (id, emp_id, inv_id, quantity, total_cost, received)
  }
  val shipments = TableQuery[Shipments]

  //add foreign keys
  class Inventorys(tag: Tag) extends Table[(Int, String, Int, Double, Double)](tag, "" +
    "INVENTORYS") {
    def id = column[Int]("PURCHASE_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def quantity = column[Int]("QUANTITY")
    def total_cost = column[Double]("TOTAL_COST")
    def total_earning = column[Double]("TOTAL_EARNING")
    def * = (id, name, quantity, total_cost, total_earning)
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