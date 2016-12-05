package cs345.bdsl

import cs345.database._

import java.text.SimpleDateFormat

import cs345.scheduler._

import java.sql.Date
import java.sql.Timestamp
import java.time.LocalDateTime
import scala.language.postfixOps
import scala.collection.mutable.Map

/**
  * Created by Sean on 11/9/16.
  */

class Bdsl {
  object CREATE {

    def NEW(keyword: EmployeeKeyword) = {
      val emp = DBService.NewEmployee()
      println("Created new employee with ID " + emp.id)
      new CreateEmployee(emp)
    }

    def NEW(keyword: ClientKeyword) = {
      val cli = DBService.NewClient()
      println("Created new client with ID " + cli.id)
      new CreateClient(cli)
    }

    def NEW(keyword: MeetingKeyword) = {
      val env = DBService.NewMeeting()
      println("Created new meeting with ID " + env.id)
      new CreateMeeting(env)
    }

    def NEW(keyword: ProjectKeyword) = {
      val pro = DBService.NewProject()
      println("Created new project with ID " + pro.id)
      new CreateProject(pro)
    }

    def NEW(keyword: InventoryKeyword) = {
      val inv = DBService.NewInventory()
      println("Created new inventory with ID " + inv.id)
      new CreateInventory(inv)
    }

    def NEW(keyword: PurchaseKeyword) = {
      val pur = DBService.NewPurchase()
      println("Created new purchase with ID " + pur.id)
      new CreatePurchase(pur, 0, 0)
    }

    def NEW(keyword: PaymentKeyword) = {
      val pay = DBService.NewPayment()
      println("Created new payment with ID " + pay.id)
      new CreatePayment(pay, 0)
    }


    def NEW(keyword: ShipmentKeyword) = {
      val ship = DBService.NewShipment()
      println("Created new shipment with ID " + ship.id)
      new CreateShipment(ship, 0)
    }

    class CreateEmployee(emp: Employee) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue(keyword)
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int) = {
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
          }
          DBService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }

        def AS(str: String) = {
          emp.name = str
          DBService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }

        def AS(dou: Double) = {
          emp.pay = dou
          DBService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }
      }

    }

    class CreateClient(cli: Client) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue(keyword)
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int) = {
          cli.id = num
          DBService.UpdateClient(cli)
          new CreateClient(cli)
        }

        def AS(str: String) = {
          keyword match {
            case NAME => cli.name = str
            case DATE => val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(str)
              cli.addDate = new java.sql.Date(temp.getTime())
          }
          DBService.UpdateClient(cli)
          new CreateClient(cli)
        }

        def AS(dou: Double) = {
          cli.balance = dou
          DBService.UpdateClient(cli)
          new CreateClient(cli)
        }
      }

    }

    class CreateMeeting(env: Meeting) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue(keyword)
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int) = {
          keyword match {
            case ID => env.id = num
            case DURATION => env.durationMinutes = num
          }
          DBService.UpdateMeeting(env)
          new CreateMeeting(env)
        }

        def AS(str: String) = {
          keyword match {
            case NAME => env.name = str
            case START => val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(str)
              env.start = new Timestamp(temp.getTime())
          }
          DBService.UpdateMeeting(env)
          new CreateMeeting(env)
        }
      }

    }

    class CreateProject(pro: Project) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue(keyword)
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int) = {
          keyword match {
            case ID => pro.id = num
            case CLIENT_ID => pro.client_id = num
          }
          DBService.UpdateProject(pro)
          new CreateProject(pro)
        }

        def AS(str: String) = {
          keyword match {
            case NAME => pro.name = str
            case END => val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(str)
              pro.end = new Date(temp.getTime())
          }
          DBService.UpdateProject(pro)
          new CreateProject(pro)
        }
      }

    }

    class CreateInventory(inv: Inventory) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue(keyword)
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int) = {
          keyword match {
            case ID => inv.id = num
            case QUANTITY => inv.quantity = num
          }
          DBService.UpdateInventory(inv)
          new CreateInventory(inv)
        }

        def AS(dou: Double) = {
          keyword match {
            case TOTAL_COST => inv.total_cost = dou
            case TOTAL_EARNING => inv.total_earning = dou
          }
          DBService.UpdateInventory(inv)
          new CreateInventory(inv)
        }

        def AS(str: String) = {
          inv.name = str
          DBService.UpdateInventory(inv)
          new CreateInventory(inv)
        }
      }

    }


    class CreatePurchase(pur: Purchase, id: Int, id2: Int) {

      def FOR_CLIENT(num: Int) = {
        pur.client_id = num
        DBService.UpdatePurchase(pur)
        new CreatePurchase(pur, num, 0)
      }

      def FOR_AMOUNT(num: Int) = {
        pur.quantity = num
        DBService.UpdatePurchase(pur)
        val item = DBService.GetInventory(id2)
        val temp = item.quantity
        item.quantity = temp - num
        DBService.UpdateInventory(item)
        new CreatePurchase(pur, id, id2)
      }

      def FOR_COST(dou: Double) = {
        pur.total_cost = dou
        DBService.UpdatePurchase(pur)
        val client = DBService.GetClient(id)
        val temp = client.balance
        client.balance = temp + dou
        DBService.UpdateClient(client)
        val item = DBService.GetInventory(id2)
        val temp2 = item.total_earning
        item.total_earning = temp2 + dou
        DBService.UpdateInventory(item)
        new CreatePurchase(pur, id, id2)
      }

      def OF_ITEM(num: Int) = {
        pur.inv_id = num
        DBService.UpdatePurchase(pur)
        new CreatePurchase(pur, id, num)
      }


      def REVIEWED_BY(num: Int) = {
        pur.emp_id = num
        DBService.UpdatePurchase(pur)
      }
    }

    class CreatePayment(pay: Payment, id: Int) {

      def FOR_CLIENT(num: Int) = {
        pay.client_id = num
        DBService.UpdatePayment(pay)
        new CreatePayment(pay, num)
      }

      def FOR_AMOUNT(dou: Double) = {
        pay.amount = dou
        DBService.UpdatePayment(pay)
        val client = DBService.GetClient(id)
        val temp = client.balance
        client.balance = temp - dou
        DBService.UpdateClient(client)
        new CreatePayment(pay, id)
      }

      def REVIEWED_BY(num: Int) = {
        pay.emp_id = num
        DBService.UpdatePayment(pay)
      }
    }

    class CreateShipment(ship: Shipment, id: Int) {

      def OF_ITEM(num: Int) = {
        ship.inv_id = num
        DBService.UpdateShipment(ship)
        new CreateShipment(ship, num)
      }

      def FOR_COST(dou: Double) = {
        ship.total_cost = dou
        DBService.UpdateShipment(ship)
        val inv = DBService.GetInventory(id)
        val temp = inv.total_cost
        inv.total_cost = temp + dou
        DBService.UpdateInventory(inv)
        new CreateShipment(ship, id)
      }

      def FOR_AMOUNT(num: Int) = {
        ship.quantity = num
        DBService.UpdateShipment(ship)
        val inv = DBService.GetInventory(id)
        val temp = inv.quantity
        inv.quantity = temp + num
        DBService.UpdateInventory(inv)
        new CreateShipment(ship, id)
      }

      def RECEIVED_BY(num: Int) = {
        ship.emp_id = num
        DBService.UpdateShipment(ship)
        new CreateShipment(ship, id)
      }
    }

  }

  object BATCH {

    def ALL( keyword: EmployeeKeyword ) = {
      val emps = DBService.GetAllEmployees()
      new EmployeeQuery( emps )
    }

    def ALL( keyword: ClientKeyword ) = {
      val cli = DBService.GetAllClients()
      new ClientQuery( cli )
    }

    def ALL( keyword: MeetingKeyword ) = {
      val mtng = DBService.GetAllMeetings()
      new MeetingQuery( mtng )
    }

    def ALL( keyword: ProjectKeyword ) = {
      val proj = DBService.GetAllProjects()
      new ProjectQuery( proj )
    }

    class EmployeeQuery( emps: Array[Employee] ) {
      def WHERE( keyword: AttributeKeyword ) = {
        new WhereContinue( keyword )
      }

      class WhereContinue( keyword: AttributeKeyword ) {
        def EQUAL( num: Any ) = {
          keyword match {
            case PAY => new EmployeeQuery( emps.filter( _.pay == num.asInstanceOf[Double] ) )
            case RANK => new EmployeeQuery( emps.filter( _.rank == num.asInstanceOf[Int] ) )
            case NAME => new EmployeeQuery( emps.filter( _.name == num.asInstanceOf[String] ) )
          }
        }

        def LESSTHAN( num: Any ) = {
          keyword match {
            case PAY => new EmployeeQuery( emps.filter( _.pay < num.asInstanceOf[Double] ) )
            case RANK => new EmployeeQuery( emps.filter( _.rank < num.asInstanceOf[Int] ) )
            case NAME => new EmployeeQuery( emps.filter( _.name < num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHAN( num: Any ) = {
          keyword match {
            case PAY => new EmployeeQuery( emps.filter( _.pay > num.asInstanceOf[Double] ) )
            case RANK => new EmployeeQuery( emps.filter( _.rank > num.asInstanceOf[Int] ) )
            case NAME => new EmployeeQuery( emps.filter( _.name > num.asInstanceOf[String] ) )
          }
        }

        def LESSTHANEQUAL( num: Any ) = {
          keyword match {
            case PAY => new EmployeeQuery( emps.filter( _.pay <= num.asInstanceOf[Double] ) )
            case RANK => new EmployeeQuery( emps.filter( _.rank <= num.asInstanceOf[Int] ) )
            case NAME => new EmployeeQuery( emps.filter( _.name <= num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHANEQUAL( num: Any ) = {
          keyword match {
            case PAY => new EmployeeQuery( emps.filter( _.pay >= num.asInstanceOf[Double] ) )
            case RANK => new EmployeeQuery( emps.filter( _.rank >= num.asInstanceOf[Int] ) )
            case NAME => new EmployeeQuery( emps.filter( _.name >= num.asInstanceOf[String] ) )
          }
        }
      }

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int) = {
          emps.foreach(_.rank = num)
          emps.foreach( DBService.UpdateEmployee(_) )
          new EmployeeQuery(emps)
        }

        def TO(num: Double) = {
          emps.foreach(_.pay = num)
          emps.foreach( DBService.UpdateEmployee(_) )
          new EmployeeQuery(emps)
        }

        def TO(str: String) = {
          emps.foreach(_.name = str)
          emps.foreach( DBService.UpdateEmployee(_) )
          new EmployeeQuery(emps)
        }
      }

      def PRINT = {
        println("Employees:")
        println("    ID    |             NAME             |   RANK   |   PAY    ")
        emps.foreach( println(_) )
      }

      def ASSIGN(keyword: EventKeyword) = {
        new AssignEmployee(emps)
      }

      class AssignEmployee(emps: Array[Employee]) {

          def MEETING(id: Int) = {
            println("Adding EMPLOYEES to MEETING ")
            emps.foreach(DBService.AddEmployeeToMeeting(_, id))
          }

          def MEETING(name: String) = {
            println("Adding EMPLOYEES to MEETING ")
            val id = DBService.GetMeeting(name).id
            emps.foreach(DBService.AddEmployeeToMeeting(_, id))
          }

          // def PROJECT(id: Int) = {
          //   println("Adding EMPLOYEE to PROJECT " + DBService.AssignEmployeeProject(emp.id, id))
          // }

          // def PROJECT(name: String) = {
          //   val id = DBService.GetProject(name).id
          //   println("Adding EMPLOYEE to PROJECT " + DBService.AssignEmployeeProject(emp.id, id))
          // }

      }

      def REMOVE = {
        println("REMOVING BATCH OF EMPLOYEES " + emps.foreach(DBService.DeleteEmployee(_)))
      }
    }

    class ClientQuery( cli: Array[Client] ) {
      def WHERE( keyword: AttributeKeyword ) = {
        new WhereContinue( keyword )
      }

      class WhereContinue( keyword: AttributeKeyword ) {
        def EQUAL( num: Any ) = {
          keyword match {
            case DATE =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
			  new ClientQuery(cli.filter(_.addDate.compareTo(new Date(temp.getTime())) == 0))
            case BALANCE => new ClientQuery( cli.filter( _.balance == num.asInstanceOf[Double] ) )
            case NAME => new ClientQuery( cli.filter( _.name == num.asInstanceOf[String] ) )
          }
        }

        def LESSTHAN( num: Any ) = {
          keyword match {
            case DATE =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
			  new ClientQuery(cli.filter(_.addDate.compareTo(new Date(temp.getTime())) < 0))
            case BALANCE => new ClientQuery( cli.filter( _.balance < num.asInstanceOf[Double] ) )
            case NAME => new ClientQuery( cli.filter( _.name < num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHAN( num: Any ) = {
          keyword match {
            case DATE =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
			  new ClientQuery(cli.filter(_.addDate.compareTo(new Date(temp.getTime())) > 0))
            case BALANCE => new ClientQuery( cli.filter( _.balance > num.asInstanceOf[Double] ) )
            case NAME => new ClientQuery( cli.filter( _.name > num.asInstanceOf[String] ) )
          }
        }

        def LESSTHANEQUAL( num: Any ) = {
          keyword match {
            case DATE =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
			  new ClientQuery(cli.filter(_.addDate.compareTo(new Date(temp.getTime())) <= 0))
            case BALANCE => new ClientQuery( cli.filter( _.balance <= num.asInstanceOf[Double] ) )
            case NAME => new ClientQuery( cli.filter( _.name <= num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHANEQUAL( num: Any ) = {
          keyword match {
            case DATE =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
			  new ClientQuery(cli.filter(_.addDate.compareTo(new Date(temp.getTime())) >= 0))
            case BALANCE => new ClientQuery( cli.filter( _.balance >= num.asInstanceOf[Double] ) )
            case NAME => new ClientQuery( cli.filter( _.name >= num.asInstanceOf[String] ) )
          }
        }
      }

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }
 class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Double) = {
          cli.foreach(_.balance = num)
          cli.foreach( DBService.UpdateClient(_) )
          new ClientQuery(cli)
        }

        def TO(str: String) = {
          keyword match {
            case NAME => cli.foreach(_.name = str)
            case DATE => val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(str)
              cli.foreach(_.addDate = new java.sql.Date(temp.getTime()))
          }
          cli.foreach( DBService.UpdateClient(_) )
          new ClientQuery(cli)
        }
      }

      def PRINT = {
        println("Clients:")
        println("    ID    |             NAME             |        DATE        | BALANCE  ")
        cli.foreach( println(_) )
      }

      def REMOVE = {
        println("REMOVING BATCH OF CLIENTS " + cli.foreach(DBService.DeleteClient(_)))
      }
    }

    class MeetingQuery( mtng: Array[Meeting] ) {
      def WHERE( keyword: AttributeKeyword ) = {
        new WhereContinue( keyword )
      }

      class WhereContinue( keyword: AttributeKeyword ) {
        def EQUAL( num: Any ) = {
          keyword match {
            case CLIENT_ID => new MeetingQuery( mtng.filter( _.client_id == num.asInstanceOf[Int] ) )
            case START =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new MeetingQuery(mtng.filter(_.start.compareTo(new Timestamp(temp.getTime())) == 0))
            case DURATION => new MeetingQuery( mtng.filter( _.durationMinutes == num.asInstanceOf[Int] ) )
            case NAME => new MeetingQuery( mtng.filter( _.name == num.asInstanceOf[String] ) )
          }
        }

        def LESSTHAN( num: Any ) = {
          keyword match {
            case CLIENT_ID => new MeetingQuery( mtng.filter( _.client_id < num.asInstanceOf[Int] ) )
            case START =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new MeetingQuery(mtng.filter(_.start.compareTo(new Timestamp(temp.getTime())) < 0))
            case DURATION => new MeetingQuery( mtng.filter( _.durationMinutes < num.asInstanceOf[Int] ) )
            case NAME => new MeetingQuery( mtng.filter( _.name < num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHAN( num: Any ) = {
          keyword match {
            case CLIENT_ID => new MeetingQuery( mtng.filter( _.client_id > num.asInstanceOf[Int] ) )
            case START => new MeetingQuery( mtng.filter( _.start.compareTo(new Timestamp(num.asInstanceOf[Int]))>0 ) )
            case DURATION => new MeetingQuery( mtng.filter( _.durationMinutes > num.asInstanceOf[Int] ) )
            case NAME => new MeetingQuery( mtng.filter( _.name > num.asInstanceOf[String] ) )
          }
        }

        def LESSTHANEQUAL( num: Any ) = {
          keyword match {
            case CLIENT_ID => new MeetingQuery( mtng.filter( _.client_id <= num.asInstanceOf[Int] ) )
            case START =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new MeetingQuery(mtng.filter(_.start.compareTo(new Timestamp(temp.getTime())) <= 0))
            case DURATION => new MeetingQuery( mtng.filter( _.durationMinutes <= num.asInstanceOf[Int] ) )
            case NAME => new MeetingQuery( mtng.filter( _.name <= num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHANEQUAL( num: Any ) = {
          keyword match {
            case CLIENT_ID => new MeetingQuery( mtng.filter( _.client_id >= num.asInstanceOf[Int] ) )
            case START =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new MeetingQuery(mtng.filter(_.start.compareTo(new Timestamp(temp.getTime())) >= 0))
            case DURATION => new MeetingQuery( mtng.filter( _.durationMinutes >= num.asInstanceOf[Int] ) )
            case NAME => new MeetingQuery( mtng.filter( _.name >= num.asInstanceOf[String] ) )
          }
        }
      }

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {

        def TO(num: Int) = {
          keyword match {
            case CLIENT_ID => mtng.foreach(_.client_id = num)
            case DURATION => mtng.foreach(_.changeDuration(num) )
          }
          mtng.foreach( DBService.UpdateMeeting(_) )
          new MeetingQuery(mtng)
        }

        def TO(str: String) = {
          keyword match {
            case NAME => mtng.foreach(_.name = str)
            case START => val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(str)
              mtng.foreach(_.start = new java.sql.Timestamp(temp.getTime()))
          }
          mtng.foreach( DBService.UpdateMeeting(_) )
          new MeetingQuery(mtng)
        }
      }

      def PRINT = {
        println("Meetings:")
        println("    ID    |             NAME             |       START        |   END")
        mtng.foreach( println(_) )
      }

      def REMOVE = {
        println("REMOVING BATCH OF MEETINGS " + mtng.foreach(DBService.DeleteMeeting(_)))
      }
    }

    class ProjectQuery( proj: Array[Project] ) {
      def WHERE( keyword: AttributeKeyword ) = {
        new WhereContinue( keyword )
      }

      class WhereContinue( keyword: AttributeKeyword ) {
        def EQUAL( num: Any ) = {
          keyword match {
            case CLIENT_ID => new ProjectQuery( proj.filter( _.client_id == num.asInstanceOf[Int] ) )
       		case END =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new ProjectQuery(proj.filter(_.end.compareTo(new Date(temp.getTime())) == 0))
            case NAME => new ProjectQuery( proj.filter( _.name == num.asInstanceOf[String] ) )
          }
        }

        def LESSTHAN( num: Any ) = {
          keyword match {
            case CLIENT_ID => new ProjectQuery( proj.filter( _.client_id < num.asInstanceOf[Int] ) )
    	    case END =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new ProjectQuery(proj.filter(_.end.compareTo(new Date(temp.getTime())) < 0))
	        case NAME => new ProjectQuery( proj.filter( _.name < num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHAN( num: Any ) = {
          keyword match {
            case CLIENT_ID => new ProjectQuery( proj.filter( _.client_id > num.asInstanceOf[Int] ) )
  		  	case END =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new ProjectQuery(proj.filter(_.end.compareTo(new Date(temp.getTime())) > 0))
            case NAME => new ProjectQuery( proj.filter( _.name > num.asInstanceOf[String] ) )
          }
        }

        def LESSTHANEQUAL( num: Any ) = {
          keyword match {
            case CLIENT_ID => new ProjectQuery( proj.filter( _.client_id <= num.asInstanceOf[Int] ) )
    	    case END =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new ProjectQuery(proj.filter(_.end.compareTo(new Date(temp.getTime())) <= 0))
 	        case NAME => new ProjectQuery( proj.filter( _.name <= num.asInstanceOf[String] ) )
          }
        }

        def GREATERTHANEQUAL( num: Any ) = {
          keyword match {
            case CLIENT_ID => new ProjectQuery( proj.filter( _.client_id >= num.asInstanceOf[Int] ) )
	        case END =>
              val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(num.asInstanceOf[String])
              new ProjectQuery(proj.filter(_.end.compareTo(new Date(temp.getTime())) >= 0))
            case NAME => new ProjectQuery( proj.filter( _.name >= num.asInstanceOf[String] ) )
          }
        }
      }

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int) = {
          proj.foreach(_.client_id = num)
          proj.foreach( DBService.UpdateProject(_) )
          new ProjectQuery(proj)
        }

        def TO(str: String) = {
          keyword match {
            case NAME =>  proj.foreach(_.name = str)
            case END => val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(str)
              proj.foreach(_.end = new java.sql.Date(temp.getTime()))
          }
          proj.foreach( DBService.UpdateProject(_) )
          new ProjectQuery(proj)
        }
      }

      def PRINT = {
        println("Projects:")
        println("    ID    |             NAME             |   END")
        proj.foreach( println(_) )
      }

      def REMOVE = {
        println("REMOVING BATCH OF PROJECTS " + proj.foreach(DBService.DeleteProject(_)))
      }
    }
  }

  object UPDATE {

    def EMPLOYEE(id: Int) = {
      println("Updating EMPLOYEE " + id)
      new ModifyEmployee(DBService.GetEmployee(id))
    }

    def EMPLOYEE(name: String) = {
      println("Updating EMPLOYEE " + name)
      new ModifyEmployee(DBService.GetEmployee(name))
    }

    def CLIENT(id: Int) = {
      println("Updating CLIENT " + id)
      new ModifyClient(DBService.GetClient(id))
    }

    def CLIENT(name: String) = {
      println("Updating CLIENT " + name)
      new ModifyClient(DBService.GetClient(name))
    }

    def MEETING(id: Int) = {
      println("Updating MEETING " + id)
      new ModifyMeeting(DBService.GetMeeting(id))
    }

    def MEETING(name: String) = {
      println("Updating MEETING " + name)
      new ModifyMeeting(DBService.GetMeeting(name))
    }

    def PROJECT(id: Int) = {
      println("Updating PROJECT " + id)
      new ModifyProject(DBService.GetProject(id))
    }

    def PROJECT(name: String) = {
      println("Updating PROJECT " + name)
      new ModifyProject(DBService.GetProject(name))
    }

    def INVENTORY(id: Int) = {
      println("Updating INVENTORY " + id)
      new ModifyInventory(DBService.GetInventory(id))
    }

    def INVENTORY(name: String) = {
      println("Updating INVENTORY " + name)
      new ModifyInventory(DBService.GetInventory(name))
    }

    def SHIPMENT(id: Int) = {
      println("Updating SHIPMENT " + id)
      new ModifyShipment(DBService.GetShipment(id))
    }

    def PURCHASE(id: Int) = {
      println("Updating PURCHASE " + id)
      new ModifyPurchase(DBService.GetPurchase(id))
    }

    def PAYMENT(id: Int) = {
      println("Updating PAYMENT " + id)
      new ModifyPayment(DBService.GetPayment(id))
    }

    class ModifyEmployee(emp: Employee) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
          }
          DBService.UpdateEmployee(emp)
          new ModifyEmployee(emp)
        }

        def TO(dou: Double) = {
          emp.pay = dou
          DBService.UpdateEmployee(emp)
          new ModifyEmployee(emp)
        }
      }

    }

    class ModifyClient(cli: Client) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          cli.id = num
          DBService.UpdateClient(cli)
          new ModifyClient(cli)
        }

        def TO(str: String) = {
          keyword match {
            case NAME => cli.name = str
            case DATE => val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(str)
              cli.addDate = new java.sql.Date(temp.getTime())
          }
          DBService.UpdateClient(cli)
          new ModifyClient(cli)
        }

        def TO(dou: Double) = {
          cli.balance = dou
          DBService.UpdateClient(cli)
          new ModifyClient(cli)
        }
      }

    }

    class ModifyMeeting(env: Meeting) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case ID => env.id = num
            case CLIENT_ID => env.client_id = num
            case DURATION => env.changeDuration(num)
          }
          DBService.UpdateMeeting(env)
          new ModifyMeeting(env)
        }

        def TO(str: String) = {
          keyword match {
            case NAME => env.name = str
            case START => val dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm a")
              val temp = dateFormat.parse(str)
              env.start = new java.sql.Timestamp(temp.getTime())
          }
          DBService.UpdateMeeting(env)
          new ModifyMeeting(env)
        }
      }

    }

    class ModifyProject(pro: Project) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case ID => pro.id = num
            case CLIENT_ID => pro.client_id = num
          }
          DBService.UpdateProject(pro)
          new ModifyProject(pro)
        }

        def TO(str: String) = {
			keyword match {
            case NAME => pro.name = str
            case END => val dateFormat = new SimpleDateFormat("MM/dd/yyyy")
              val temp = dateFormat.parse(str)
              pro.end = new java.sql.Date(temp.getTime())
          }
          DBService.UpdateProject(pro)
          new ModifyProject(pro)
        }
      }

    }

    class ModifyInventory(inv: Inventory) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case ID => inv.id = num
            case QUANTITY => inv.quantity = num
          }
          DBService.UpdateInventory(inv)
          new ModifyInventory(inv)
        }

        def TO(dou: Double) = {
          keyword match {
            case TOTAL_COST => inv.total_cost = dou
            case TOTAL_EARNING => inv.total_earning = dou
          }
          DBService.UpdateInventory(inv)
          new ModifyInventory(inv)
        }

        def TO(str: String) = {
          inv.name = str
          DBService.UpdateInventory(inv)
          new ModifyInventory(inv)
        }
      }

    }

    class ModifyShipment(ship: Shipment) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case EMP_ID => ship.emp_id = num
            case INV_ID =>
              val oldid = ship.inv_id
              val oldinv = DBService.GetInventory(oldid)
              val temp = oldinv.quantity
              val temp2 = oldinv.total_cost
              oldinv.quantity = temp - ship.quantity
              oldinv.total_cost = temp2 - ship.total_cost
              DBService.UpdateInventory(oldinv)
              ship.inv_id = num
              val inv = DBService.GetInventory(num)
              val newtemp = inv.quantity
              val newtemp2 = inv.total_cost
              inv.quantity = newtemp - ship.quantity
              inv.total_cost = newtemp2 - ship.total_cost
              DBService.UpdateInventory(inv)
            case QUANTITY =>
              val tempcount = ship.quantity
              ship.quantity = num
              val diff = num - tempcount
              val inv = DBService.GetInventory(ship.inv_id)
              val temp = inv.quantity
              inv.quantity = temp + diff
              DBService.UpdateInventory(inv)
          }
          DBService.UpdateShipment(ship)
          new ModifyShipment(ship)
        }

        def TO(dou: Double) = {
          val tempcost = ship.total_cost
          ship.total_cost = dou
          val diff = dou - tempcost
          val inv = DBService.GetInventory(ship.inv_id)
          val temp = inv.total_cost
          inv.total_cost = temp + diff
          DBService.UpdateShipment(ship)
          new ModifyShipment(ship)
        }
      }

    }

    class ModifyPurchase(pur: Purchase) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case EMP_ID => pur.emp_id = num
            case INV_ID =>
              val oldid = pur.inv_id
              val oldinv = DBService.GetInventory(oldid)
              val temp = oldinv.quantity
              val temp2 = oldinv.total_cost
              oldinv.quantity = temp - pur.quantity
              oldinv.total_cost = temp2 - pur.total_cost
              DBService.UpdateInventory(oldinv)
              pur.inv_id = num
              val inv = DBService.GetInventory(num)
              val newtemp = inv.quantity
              val newtemp2 = inv.total_cost
              inv.quantity = newtemp + pur.quantity
              inv.total_cost = newtemp2 + pur.total_cost
              DBService.UpdateInventory(inv)
            case QUANTITY =>
              val tempcount = pur.quantity
              pur.quantity = num
              val diff = num - tempcount
              val inv = DBService.GetInventory(pur.inv_id)
              val temp = inv.quantity
              inv.quantity = temp + diff
              DBService.UpdateInventory(inv)
            case CLIENT_ID =>
              val oldid = pur.client_id
              val oldcli = DBService.GetClient(oldid)
              val temp = oldcli.balance
              oldcli.balance = temp - pur.total_cost
              DBService.UpdateClient(oldcli)
              pur.client_id = num
              val cli = DBService.GetClient(num)
              val newtemp = cli.balance
              cli.balance = newtemp + pur.total_cost
              DBService.UpdateClient(cli)
          }
          DBService.UpdatePurchase(pur)
          new ModifyPurchase(pur)
        }

        def TO(dou: Double) = {
          val tempcost = pur.total_cost
          pur.total_cost = dou
          val diff = dou - tempcost
          val inv = DBService.GetInventory(pur.inv_id)
          val temp = inv.total_earning
          inv.total_earning = temp + diff
          DBService.UpdateInventory(inv)
          val cli = DBService.GetClient(pur.client_id)
          val temp2 = cli.balance
          cli.balance = temp2 + diff
          DBService.UpdateClient(cli)
          DBService.UpdatePurchase(pur)
          new ModifyPurchase(pur)
        }
      }

    }

    class ModifyPayment(pay: Payment) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue(keyword)
      }

      class UpdateContinue(keyword: AttributeKeyword) {
        def TO(num: Int) = {
          keyword match {
            case EMP_ID => pay.emp_id = num
            case CLIENT_ID =>
              val oldid = pay.client_id
              val oldcli = DBService.GetClient(oldid)
              val temp = oldcli.balance
              oldcli.balance = temp + pay.amount
              DBService.UpdateClient(oldcli)
              pay.client_id = num
              val cli = DBService.GetClient(num)
              val newtemp = cli.balance
              cli.balance = newtemp - pay.amount
              DBService.UpdateClient(cli)
          }
          DBService.UpdatePayment(pay)
          new ModifyPayment(pay)
        }

        def TO(dou: Double) = {
          val tempamount = pay.amount
          pay.amount = dou
          val diff = dou - tempamount
          val cli = DBService.GetClient(pay.client_id)
          val temp2 = cli.balance
          cli.balance = temp2 - diff
          DBService.UpdateClient(cli)
          DBService.UpdatePayment(pay)
          new ModifyPayment(pay)
        }
      }

    }

  }

  object REMOVE {

    def EMPLOYEE(id: Int) = {
      println("Removing EMPLOYEE " + DBService.DeleteEmployee(id))
    }

    def EMPLOYEE(name: String) = {
      val id = DBService.GetEmployee(name).id
      println("Removing EMPLOYEE " + DBService.DeleteEmployee(id))
    }

    def CLIENT(id: Int) = {
      println("Removing CLIENT " + DBService.DeleteClient(id))
    }

    def CLIENT(name: String) = {
      val id = DBService.GetClient(name).id
      println("Removing CLIENT " + DBService.DeleteClient(id))
    }

    def MEETING(id: Int) = {
      println("Removing MEETING " + DBService.DeleteMeeting(id))
    }

    def MEETING(name: String) = {
      val id = DBService.GetMeeting(name).id
      println("Removing MEETING " + DBService.DeleteMeeting(id))
    }

    def PROJECT(id: Int) = {
      println("Removing PROJECT " + DBService.DeleteProject(id))
    }

    def PROJECT(name: String) = {
      val id = DBService.GetProject(name).id
      println("Removing PROJECT " + DBService.DeleteProject(id))
    }

    def INVENTORY(id: Int) = {
      println("Removing INVENTORY " + DBService.DeleteInventory(id))
    }

    def INVENTORY(name: String) = {
      val id = DBService.GetInventory(name).id
      println("Removing INVENTORY " + DBService.DeleteInventory(id))
    }

    def SHIPMENT(id: Int) = {
      val ship = DBService.GetShipment(id)
      val inv = DBService.GetInventory(ship.inv_id)
      val temp = inv.total_cost
      val temp2 = inv.quantity
      inv.total_cost = temp - ship.total_cost
      inv.quantity = temp2 - ship.quantity
      DBService.UpdateInventory(inv)
      println("Removing SHIPMENT " + DBService.DeleteShipment(id))
    }

    def PURCHASE(id: Int) = {
      val pur = DBService.GetPurchase(id)
      val inv = DBService.GetInventory(pur.inv_id)
      val temp = inv.total_earning
      val temp2 = inv.quantity
      inv.total_earning = temp - pur.total_cost
      inv.quantity = temp2 + pur.quantity
      DBService.UpdateInventory(inv)
      val cli = DBService.GetClient(pur.client_id)
      val tempb = cli.balance
      cli.balance = tempb - pur.total_cost
      DBService.UpdateClient(cli)
      println("Removing PURCHASE " + DBService.DeletePurchase(id))
    }

    def PAYMENT(id: Int) = {
      val pay = DBService.GetPayment(id)
      val cli = DBService.GetClient(pay.client_id)
      val temp = cli.balance
      cli.balance = temp - pay.amount
      DBService.UpdateClient(cli)
      println("Removing PAYMENT " + DBService.DeletePayment(id))
    }
  }

  object ASSIGN {
    def EMPLOYEE(id: Int) = {
      new AssignEmployee(DBService.GetEmployee(id))
    }

    def EMPLOYEE(name: String) = {
      new AssignEmployee(DBService.GetEmployee(name))
    }

    class AssignEmployee(emp: Employee) {
      def TO(keyword: EventKeyword) = {
        new Assignment(keyword)
      }

      class Assignment(keyword: EventKeyword) {
        def MEETING(id: Int) = {
          println("Adding EMPLOYEE to MEETING ")
          DBService.AddEmployeeToMeeting(emp, id)
        }

        def MEETING(name: String) = {
          println("Adding EMPLOYEE to MEETING ")
          val id = DBService.GetMeeting(name).id
          DBService.AddEmployeeToMeeting(emp, id)
        }

        def PROJECT(id: Int) = {
          println("Adding EMPLOYEE to PROJECT " + DBService.AssignEmployeeProject(emp.id, id))
        }

        def PROJECT(name: String) = {
          val id = DBService.GetProject(name).id
          println("Adding EMPLOYEE to PROJECT " + DBService.AssignEmployeeProject(emp.id, id))
        }

      }

    }

  }

  object PRINT {
    def ALL(keyword: ObjectKeyword) = {
      keyword match {
        case EMPLOYEE => DBService.ListAllEmployees()
        case CLIENT => DBService.ListAllClients()
        case MEETING => DBService.ListAllMeetings()
        case PROJECT => DBService.ListAllProjects()
        case INVENTORY => DBService.ListAllInventorys()
        case PAYMENT => DBService.ListAllPayments()
        case PURCHASE => DBService.ListAllPurchases()
        case SHIPMENT => DBService.ListAllShipments()
        case MEETING_ASSIGNMENTS => DBService.ListAllMeetingAssignments()
        case PROJECT_ASSIGNMENTS => DBService.ListAllProjectAssignments()
      }
    }

    def ALL = {
      DBService.ListAllEmployees()
      DBService.ListAllClients()
      DBService.ListAllMeetings()
      DBService.ListAllProjects()
      DBService.ListAllInventorys()
      DBService.ListAllPayments()
      DBService.ListAllPurchases()
      DBService.ListAllShipments()
    }

  }

  object IMPORT {
    def FROM(file: String) = {
      new ImportTo(file)
    }

    class ImportTo(file: String) {
      def TO(keyword: EmployeeKeyword) {
        val bufferedSource = io.Source.fromFile(file)
        for (line <- bufferedSource.getLines) {
          val cols = line.split(",").map(_.trim)
          val emp = DBService.NewEmployee()
          emp.name = cols(0)
          emp.rank = cols(1).toInt
          emp.pay = cols(2).toDouble
          DBService.UpdateEmployee(emp)
        }
        bufferedSource.close
      }

      def TO(keyword: ClientKeyword) {
        val bufferedSource = io.Source.fromFile(file)
        for (line <- bufferedSource.getLines) {
          val cols = line.split(",").map(_.trim)
          val cli = DBService.NewClient()
          cli.name = cols(0)
          cli.balance = cols(1).toDouble
          DBService.UpdateClient(cli)
        }
        bufferedSource.close
      }
    }
  }

  object EXPORT {
    def EMPLOYEE( id: Int ) = {
      new EmployeeExport(id)
    }

    def TO(file: String) {
      Calendar.ExportCompanySchedule(file)
    }

    class EmployeeExport(id: Int) {
      def TO(file: String) {
        Calendar.ExportEmployeeSchedule(id, file)
      }
    }

  }

  def CLOSE = {
    val now = java.util.Calendar.getInstance().getTime()
    val projects = DBService.GetAllProjects()
    val meetings = DBService.GetAllMeetings()

    for (meeting <- meetings) {
      if( meeting.getEnd().compareTo(now) < 0 )
      {
        println("Closing MEETING " + DBService.DeleteMeeting(meeting.id))
      }
    }

    for (project <- projects) {
      if( project.end.compareTo(now) < 0 )
      {
        println("Closing PROJECT " + DBService.DeleteProject(project.id))
      }
    }
  }
}