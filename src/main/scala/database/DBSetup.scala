package cs345.database

import slick.driver.H2Driver.api._
import java.sql.Date
import java.sql.Timestamp

trait DBObject {
  
}

class Employee(var id: Int, var name: String, var rank: Int, var pay: Double) extends DBObject {
  def this(id: Int) {
    this(id, Employee.NAME_DEFAULT_VALUE, Employee.RANK_DEFAULT_VALUE, Employee.PAY_DEFAULT_VALUE);
  }

  def this(data: (Int, String, Int, Double)) {
    this(data._1, data._2, data._3, data._4);
  }

  override def toString: String = {
    return "id: " + id + " name: " + name + " rank: " + rank + " pay: " + pay
  }
}

object Employee {
  val NAME_DEFAULT_VALUE = ""
  val RANK_DEFAULT_VALUE = -1
  val PAY_DEFAULT_VALUE = 0.0
}

class Client(var id: Int, var name: String, var addDate: Date) extends DBObject {
  def this(id: Int) {
    this(id, Client.NAME_DEFAULT_VALUE, Client.ADDDATE_DEFAULT_VALUE);
  }

  def this(data: (Int, String, Date)) {
    this(data._1, data._2, data._3);
  }

  override def toString: String = {
    return "id: " + id + ", name: " + name + ", dateAdded: " + addDate
  }
}

object Client {
  val NAME_DEFAULT_VALUE = ""
  val ADDDATE_DEFAULT_VALUE: Date = new Date(0)
}

class Event {
  var id = -1
  var name = ""
  var start : Timestamp = new Timestamp(0)
  var end : Timestamp = new Timestamp(0)

  override def toString: String = {
    return "id: " + id + ", name: " + name + ", Start Time: " + start + ", End Time: " + end
  }
}

object DBSetup {

  // Definition of the EMPLOYEES table
  class Employees(tag: Tag) extends Table[(Int, String, Int, Double)](tag, "EMPLOYEES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
    def name = column[String]("NAME")
    def rank = column[Int]("RANK")
    def pay = column[Double]("PAY")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, rank, pay)
  }
  val employees = TableQuery[Employees]

  class Clients(tag: Tag) extends Table[(Int, String, Date)](tag, "CLIENTS") {
    def id = column[Int]("CLIENT_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CLIENT_NAME")
    def addDate = column[Date]("DATE_ADDED")
    //ISSUE: Insert 1-* for Meetings here + foreign
    def * = (id, name, addDate)
  }
  val clients = TableQuery[Clients]

  class Events(tag: Tag) extends Table[(Int, String, Timestamp, Timestamp)](tag, "EVENTS") {
    def id = column[Int]("CLIENT_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("EVENT_NAME")
    def start = column[Timestamp]("EVENT_DATE")
    def end = column[Timestamp]("EVENT_DURATION")
    //ISSUE: Insert *-* relationships for Employees and Clients
    def * = (id, name, start, end)
  }
  val events = TableQuery[Events]

  // Definition of the COFFEES table
  // class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
  //   def name = column[String]("COF_NAME", O.PrimaryKey)
  //   def supID = column[Int]("SUP_ID")
  //   def price = column[Double]("PRICE")
  //   def sales = column[Int]("SALES")
  //   def total = column[Int]("TOTAL")
  //   def * = (name, supID, price, sales, total)
  //   // A reified foreign key relation that can be navigated to create a join
  //   def supplier = foreignKey("SUP_FK", supID, suppliers)(_.id)
  // }
  // val coffees = TableQuery[Coffees]

  val setupSequence = DBIO.seq(
    // Create the tables, including primary and foreign keys
    (employees.schema ++ clients.schema ++ events.schema).create,

    employees += (0, "Existing One", 5, 75.5),
    employees += (0, "Existing Two", 3, 78.95)

  )
}