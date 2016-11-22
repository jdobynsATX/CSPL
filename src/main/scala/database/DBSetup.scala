package cs345.database

import slick.driver.H2Driver.api._
import java.sql.Date
import java.sql.Timestamp
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob
//ISSUE: Blob is a placeholder for a user-defined type
trait DBObject {
  
}

object Employee {
  val BITS_NEED_FOR_HOURLY_SCHEDULE = 8760/8 //Temporary division
  val NAME_DEFAULT_VALUE = ""
  val RANK_DEFAULT_VALUE = -1
  val PAY_DEFAULT_VALUE = 0.0
  val BITSET_DEFAULT: Blob = new SerialBlob(new Array[Byte](0))
}

object Client {
  val NAME_DEFAULT_VALUE = ""
  val ADDDATE_DEFAULT_VALUE: Date = new Date(0)
}

object Meeting {
  val NAME_DEFAULT_VALUE = ""
  val CLIENT_DEFAULT = -1
  val START_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val END_DEFAULT_VALUE: Timestamp = new Timestamp(0)
}

object Project {
  val NAME_DEFAULT_VALUE = ""
  val CLIENT_DEFAULT = -1
  val END_DEFAULT_VALUE: Timestamp = new Timestamp(0)
}

object Event {
  val NAME_DEFAULT_VALUE = ""
  val START_DEFAULT_VALUE: Timestamp = new Timestamp(0)
  val END_DEFAULT_VALUE: Timestamp = new Timestamp(0)
}


class Employee(var id: Int, var name: String, var rank: Int, var pay: Double, var schedule: Blob) extends DBObject {
  def this(id: Int) {
    this(id, Employee.NAME_DEFAULT_VALUE, Employee.RANK_DEFAULT_VALUE, Employee.PAY_DEFAULT_VALUE, 
      Employee.BITSET_DEFAULT);
  }

  def this(data: (Int, String, Int, Double, Blob)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  override def toString: String = {
    return "id: " + id + " name: " + name + " rank: " + rank + " pay: " + pay
  }
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

class Meeting(var id: Int, var client_id: Int, var name: String, var start: Timestamp, var end: Timestamp) 
      extends DBObject {
  def this(id: Int) {
    this(id, Meeting.CLIENT_DEFAULT, Meeting.NAME_DEFAULT_VALUE, Meeting.START_DEFAULT_VALUE, Meeting.END_DEFAULT_VALUE);
  }

  def this(data: (Int, Int, String, Timestamp, Timestamp)) {
    this(data._1, data._2, data._3, data._4, data._5);
  }

  override def toString: String = {
    return "id: " + id + ", name: " + name + ", Start Time: " + start + ", End Time: " + end
  }
}

class Project(var id: Int, var client_id: Int, var name: String, var end: Timestamp) 
      extends DBObject {
  def this(id: Int, client_id: Int) {
    this(id, client_id, Project.NAME_DEFAULT_VALUE, Project.END_DEFAULT_VALUE);
  }

  def this(data: (Int, Int, String, Timestamp)) {
    this(data._1, data._2, data._3, data._4);
  }

  override def toString: String = {
    return "id: " + id + ", name: " + name + ", End Time: " + end
  }
}

object DBSetup {

  class Employees(tag: Tag) extends Table[(Int, String, Int, Double, Blob)](tag, "EMPLOYEES") {
    def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("NAME")
    def rank = column[Int]("RANK")
    def pay = column[Double]("PAY")
    def schedule = column[Blob]("SCHEDULE")
    def * = (id, name, rank, pay, schedule)
  }
  val employees = TableQuery[Employees]

  class Clients(tag: Tag) extends Table[(Int, String, Date)](tag, "CLIENTS") {
    def id = column[Int]("CLIENT_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("CLIENT_NAME")
    def addDate = column[Date]("DATE_ADDED")
    def * = (id, name, addDate)
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

  class Events(tag: Tag) extends Table[(Int, String, Timestamp, Timestamp)](tag, "EVENTS") {
    def id = column[Int]("CLIENT_ID", O.PrimaryKey, O.AutoInc)
    def name = column[String]("EVENT_NAME")
    def start = column[Timestamp]("EVENT_DATE")
    def end = column[Timestamp]("EVENT_DURATION")
    def * = (id, name, start, end)
  }
  val events = TableQuery[Events]

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
      ++ projects.schema ++ meetingJoinTable.schema ++ projectJoinTable.schema).create,
    employees += (0, "Existing One", 5, 75.5, default_blob),
    employees += (0, "Existing Two", 3, 78.95, default_blob),
    clients += (2, "C1", Client.ADDDATE_DEFAULT_VALUE),
    meetings += (0, 1, "M0", Meeting.START_DEFAULT_VALUE, Meeting.END_DEFAULT_VALUE)
  )
}