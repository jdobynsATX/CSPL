package cs345.database

import slick.driver.H2Driver.api._

class Employee {
  var id = -1
  var name = ""
  var rank = -1
  var pay = 0.0

  override def toString: String = {
    return "id: " + id + " name: " + name + " rank: " + rank + " pay: " + pay
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

  // Definition of the SUPPLIERS table
  class Suppliers(tag: Tag) extends Table[(Int, String, String, String, String, String)](tag, "SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey) // This is the primary key column
    def name = column[String]("SUP_NAME")
    def street = column[String]("STREET")
    def city = column[String]("CITY")
    def state = column[String]("STATE")
    def zip = column[String]("ZIP")
    // Every table needs a * projection with the same type as the table's type parameter
    def * = (id, name, street, city, state, zip)
  }
  val suppliers = TableQuery[Suppliers]

  // Definition of the COFFEES table
  class Coffees(tag: Tag) extends Table[(String, Int, Double, Int, Int)](tag, "COFFEES") {
    def name = column[String]("COF_NAME", O.PrimaryKey)
    def supID = column[Int]("SUP_ID")
    def price = column[Double]("PRICE")
    def sales = column[Int]("SALES")
    def total = column[Int]("TOTAL")
    def * = (name, supID, price, sales, total)
    // A reified foreign key relation that can be navigated to create a join
    def supplier = foreignKey("SUP_FK", supID, suppliers)(_.id)
  }
  val coffees = TableQuery[Coffees]

  val setupSequence = DBIO.seq(
    // Create the tables, including primary and foreign keys
    (suppliers.schema ++ coffees.schema ++ employees.schema).create,

    employees += (0, "Existing One", 5, 75.5),
    employees += (0, "Existing Two", 3, 78.95),

    // Insert some suppliers
    suppliers += (101, "Acme, Inc.",      "99 Market Street", "Groundsville", "CA", "95199"),
    suppliers += ( 49, "Superior Coffee", "1 Party Place",    "Mendocino",    "CA", "95460"),
    suppliers += (150, "The High Ground", "100 Coffee Lane",  "Meadows",      "CA", "93966"),
    // Equivalent SQL code:
    // insert into SUPPLIERS(SUP_ID, SUP_NAME, STREET, CITY, STATE, ZIP) values (?,?,?,?,?,?)

    // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
    coffees ++= Seq(
      ("Colombian",         101, 7.99, 0, 0),
      ("French_Roast",       49, 8.99, 0, 0),
      ("Espresso",          150, 9.99, 0, 0),
      ("Colombian_Decaf",   101, 8.99, 0, 0),
      ("French_Roast_Decaf", 49, 9.99, 0, 0)
    )
    // Equivalent SQL code:
    // insert into COFFEES(COF_NAME, SUP_ID, PRICE, SALES, TOTAL) values (?,?,?,?,?)
  )
}