package cs345.bdsl

import cs345.database._

import java.sql.Date
import java.sql.Timestamp
import scala.language.postfixOps
import scala.collection.mutable.Map

/**
  * Created by Sean on 11/9/16.
  */

class Bdsl {
  val dbService = new DBService
  object CREATE {

    def NEW(keyword: EmployeeKeyword) = {
      val emp = dbService.NewEmployee()
      println( "Created new employee with ID " + emp.id )
      new CreateEmployee(emp)
    }

    def NEW(keyword: ClientKeyword) = {
      val cli = dbService.NewClient()
      println( "Created new client with ID " + cli.id )
      new CreateClient(cli)
    }

    def NEW(keyword: MeetingKeyword) = {
      val env = dbService.NewMeeting()
      println( "Created new meeting with ID " + env.id )
      new CreateMeeting(env)
    }

    def NEW(keyword: ProjectKeyword) = {
      val pro = dbService.NewProject()
      println( "Created new project with ID " + pro.id )
      new CreateProject(pro)
    }

    def NEW(keyword: InventoryKeyword) = {
      val inv = dbService.NewInventory()
      println( "Created new inventory with ID " + inv.id )
      new CreateInventory(inv)
    }

    def NEW(keyword: PurchaseKeyword) = {
      val pur = dbService.NewPurchase()
      println( "Created new purchase with ID " + pur.id )
      new CreatePurchase(pur,0,0)
    }

    def NEW(keyword: PaymentKeyword) = {
      val pay = dbService.NewPayment()
      println( "Created new payment with ID " + pay.id )
      new CreatePayment(pay,0)
    }


    def NEW(keyword: ShipmentKeyword) = {
      val ship = dbService.NewShipment()
      println( "Created new shipment with ID " + ship.id )
      new CreateShipment(ship,0)
    }

    class CreateEmployee(emp: Employee) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
          }
          dbService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }

        def AS(str: String) = {
          emp.name = str
          dbService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }

        def AS(dou: Double) = {
          emp.pay = dou
          dbService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }
      }
    }

    class CreateClient(cli: Client) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          keyword match {
            case ID => cli.id = num
            case DATE => cli.addDate = new Date( num )
          }
          dbService.UpdateClient(cli)
          new CreateClient(cli)
        }

        def AS(str: String) = {
          cli.name = str
          dbService.UpdateClient(cli)
          new CreateClient(cli)
        }

        def AS(dou: Double) = {
          cli.balance = dou
          dbService.UpdateClient(cli)
          new CreateClient(cli)
        }
      }
    }

    class CreateMeeting(env: Meeting) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          keyword match {
            case ID => env.id = num
            case START => env.start = new Timestamp(num)
            case END => env.end = new Timestamp(num)
          }
          dbService.UpdateMeeting(env)
          new CreateMeeting(env)
        }

        def AS(str: String) = {
          env.name = str
          dbService.UpdateMeeting(env)
          new CreateMeeting(env)
        }
      }
    }

    class CreateProject(pro: Project) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          keyword match {
            case ID => pro.id = num
            case CLIENT_ID => pro.client_id = num
            case START => pro.end = new Date(num)
          }
          dbService.UpdateProject(pro)
          new CreateProject(pro)
        }

        def AS(str: String) = {
          pro.name = str
          dbService.UpdateProject(pro)
          new CreateProject(pro)
        }
      }
    }

    class CreateInventory(inv: Inventory) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          keyword match {
            case ID => inv.id = num
            case COUNT => inv.count = num
          }
          dbService.UpdateInventory(inv)
          new CreateInventory(inv)
        }

        def AS(dou: Double)= {
          keyword match {
            case TOTAL_COST => inv.total_cost = dou
            case TOTAL_EARNING => inv.total_earning = dou
          }
          dbService.UpdateInventory(inv)
          new CreateInventory(inv)
        }

        def AS(str: String) = {
          inv.name = str
          dbService.UpdateInventory(inv)
          new CreateInventory(inv)
        }
      }
    }


    class CreatePurchase(pur:Purchase, id: Int, id2:Int){

      def FOR_CLIENT(num : Int) = {
        pur.client_id = num
        dbService.UpdatePurchase(pur)
        new CreatePurchase(pur, num, 0)
      }
      def FOR_AMOUNT(num:Int)={
        pur.count=num
        dbService.UpdatePurchase(pur)
        val item = dbService.GetInventory(id2)
        val temp = item.count
        item.count = temp - num
        dbService.UpdateInventory(item)
        new CreatePurchase(pur, id, id2)
      }
      def FOR_COST(dou:Double)={
        pur.total_cost = dou
        dbService.UpdatePurchase(pur)
        val client = dbService.GetClient(id)
        val temp = client.balance
        client.balance = temp + dou
        dbService.UpdateClient(client)
        val item = dbService.GetInventory(id2)
        val temp2 = item.total_earning
        item.total_earning = temp2 + dou
        dbService.UpdateInventory(item)
        new CreatePurchase(pur, id, id2)
      }

      def OF_ITEM(num:Int)={
        pur.inv_id=num
        dbService.UpdatePurchase(pur)
        new CreatePurchase(pur, id, num)
      }


      def REVIEWED_BY(num:Int)={
        pur.emp_id=num
        dbService.UpdatePurchase(pur)
      }
    }

    class CreatePayment(pay:Payment, id: Int){

      def FOR_CLIENT(num : Int) = {
        pay.client_id = num
        dbService.UpdatePayment(pay)
        new CreatePayment(pay, num)
      }

      def FOR_AMOUNT(dou:Double)={
        pay.amount = dou
        dbService.UpdatePayment(pay)
        val client = dbService.GetClient(id)
        val temp = client.balance
        client.balance = temp - dou
        dbService.UpdateClient(client)
        new CreatePayment(pay, id)
      }

      def REVIEWED_BY(num:Int)={
        pay.emp_id=num
        dbService.UpdatePayment(pay)
      }
    }

    class CreateShipment(ship:Shipment, id: Int){

      def OF_ITEM(num : Int) = {
        ship.inv_id = num
        dbService.UpdateShipment(ship)
        new CreateShipment(ship, num)
      }

      def FOR_COST(dou:Double)={
        ship.total_cost = dou
        dbService.UpdateShipment(ship)
        val inv = dbService.GetInventory(id)
        val temp = inv.total_cost
        inv.total_cost = temp + dou
        dbService.UpdateInventory(inv)
        new CreateShipment(ship, id)
      }

      def FOR_AMOUNT(num:Int)={
        ship.count = num
        dbService.UpdateShipment(ship)
        val inv = dbService.GetInventory(id)
        val temp = inv.count
        inv.count = temp + num
        dbService.UpdateInventory(inv)
        new CreateShipment(ship, id)
      }
    }
  }

  object UPDATE {

    def ALL( keyword: EmployeeKeyword ) = {
      val emps = dbService.GetAllEmployees()
      new QueryResults( emps )
    }

    class QueryResults( emps: Array[Employee] ) {
      def WHERE( keyword: AttributeKeyword ) = {
        new WhereContinue( keyword )
      }

      class WhereContinue( keyword: AttributeKeyword ) {
        def EQUAL( num: Int ) = {
          keyword match {
            case ID => new QueryResults( emps.filter( _.id == num ) )
            case PAY => new QueryResults( emps.filter( _.pay == num ) )
            case RANK => new QueryResults( emps.filter( _.rank == num ) )
          }
        }

        def LESSTHAN( num: Int ) = {
          keyword match {
            case ID => new QueryResults( emps.filter( _.id < num ) )
            case PAY => new QueryResults( emps.filter( _.pay < num ) )
            case RANK => new QueryResults( emps.filter( _.rank < num ) )
          }
        }

        def GREATERTHAN( num: Int ) = {
          keyword match {
            case ID => new QueryResults( emps.filter( _.id > num ) )
            case PAY => new QueryResults( emps.filter( _.pay > num ) )
            case RANK => new QueryResults( emps.filter( _.rank > num ) )
          }
        }

        def LESSTHANEQUAL( num: Int ) = {
          keyword match {
            case ID => new QueryResults( emps.filter( _.id <= num ) )
            case PAY => new QueryResults( emps.filter( _.pay <= num ) )
            case RANK => new QueryResults( emps.filter( _.rank <= num ) )
          }
        }

        def GREATERTHANEQUAL( num: Int ) = {
          keyword match {
            case ID => new QueryResults( emps.filter( _.id >= num ) )
            case PAY => new QueryResults( emps.filter( _.pay >= num ) )
            case RANK => new QueryResults( emps.filter( _.rank >= num ) )
          }
        }
      }

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int) = {
          keyword match {
            case ID => emps.foreach(_.id = num)
            case RANK => emps.foreach(_.rank = num)
            case PAY => emps.foreach(_.pay = num)
          }
          emps.foreach( dbService.UpdateEmployee(_) )
          new QueryResults(emps)
        }

        def TO(str: String) = {
          emps.foreach(_.name = str)
          emps.foreach( dbService.UpdateEmployee(_) )
          new QueryResults(emps)
        }
      }
    }

    def EMPLOYEE( id: Int ) = {
      println( "Updating EMPLOYEE " + id)
      new ModifyEmployee(dbService.GetEmployee(id))
    }

    def CLIENT( id: Int ) = {
      println( "Updating CLIENT " + id)
      new ModifyClient(dbService.GetClient(id))
    }

    def MEETING( id: Int ) = {
      println( "Updating MEETING " + id)
      new ModifyMeeting(dbService.GetMeeting(id))
    }

    def PROJECT( id: Int ) = {
      println( "Updating PROJECT " + id)
      new ModifyProject(dbService.GetProject(id))
    }

    def INVENTORY( id: Int ) = {
      println( "Updating INVENTORY " + id)
      new ModifyInventory(dbService.GetInventory(id))
    }

    class ModifyEmployee( emp: Employee ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int) = {
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
          }
          dbService.UpdateEmployee(emp) 
          new ModifyEmployee(emp)
        }

        def TO(dou: Double) = {
          emp.pay = dou
          dbService.UpdateEmployee(emp) 
          new ModifyEmployee(emp)
        }
      }
    }

    class ModifyClient( cli: Client ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int)= {
          keyword match {
            case ID => cli.id = num
            case DATE => cli.addDate = new Date( num )
          }
          dbService.UpdateClient(cli)
          new ModifyClient(cli)
        }

        def TO(str: String) = {
          cli.name = str
          dbService.UpdateClient(cli)
          new ModifyClient(cli)
        }

        def TO(dou: Double) = {
          cli.balance = dou
          dbService.UpdateClient(cli)
          new ModifyClient(cli)
        }
      }
    }

    class ModifyMeeting(env: Meeting ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int)= {
          keyword match {
            case ID => env.id = num
            case CLIENT_ID => env.client_id = num
            case START => env.start = new Timestamp(num)
            case END => env.end = new Timestamp(num)
          }
          dbService.UpdateMeeting(env)
          new ModifyMeeting(env)
        }

        def TO(str: String) = {
          env.name = str
          dbService.UpdateMeeting(env)
          new ModifyMeeting(env)
        }
      }
    }

    class ModifyProject(pro: Project ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int)= {
          keyword match {
            case ID => pro.id = num
            case CLIENT_ID => pro.client_id = num
            case END => pro.end = new Date(num)
          }
          dbService.UpdateProject(pro)
          new ModifyProject(pro)
        }

        def TO(str: String) = {
          pro.name = str
          dbService.UpdateProject(pro)
          new ModifyProject(pro)
        }
      }
    }

    class ModifyInventory(inv: Inventory ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int)= {
          keyword match {
            case ID => inv.id = num
            case COUNT => inv.count = num
          }
          dbService.UpdateInventory(inv)
          new ModifyInventory(inv)
        }

        def TO(dou: Double)= {
          keyword match {
            case TOTAL_COST => inv.total_cost = dou
            case TOTAL_EARNING => inv.total_earning = dou
          }
          dbService.UpdateInventory(inv)
          new ModifyInventory(inv)
        }

        def TO(str: String) = {
          inv.name = str
          dbService.UpdateInventory(inv)
          new ModifyInventory(inv)
        }
      }
    }

  }

  object REMOVE {

    def EMPLOYEE( id: Int ) = {
      println( "Removing EMPLOYEE " + dbService.DeleteEmployee(id) )
    }

    def CLIENT( id: Int ) = {
      println( "Removing CLIENT " + dbService.DeleteClient(id) )
    }

    def MEETING( id: Int ) = {
      println( "Removing MEETING " + dbService.DeleteMeeting(id) )
    }

    def PROJECT( id: Int ) = {
      println( "Removing PROJECT " + dbService.DeleteProject(id) )
    }

    def INVENTORY( id: Int ) = {
      println( "Removing INVENTORY " + dbService.DeleteInventory(id) )
    }
  }

  object PRINT {
    def ALL( keyword: ObjectKeyword ) = { 
      keyword match {
        case EMPLOYEE => dbService.ListAllEmployees()
        case CLIENT => dbService.ListAllClients()
        case MEETING => dbService.ListAllMeetings()
        case PROJECT => dbService.ListAllProjects()
        case INVENTORY => dbService.ListAllInventorys()
        case PAYMENT => dbService.ListAllPayments()
        case PURCHASE => dbService.ListAllPurchases()
        case SHIPMENT => dbService.ListAllShipments()
      }
    }

    def ALL = {
      dbService.ListAllEmployees()
      dbService.ListAllClients()
      dbService.ListAllMeetings()
      dbService.ListAllProjects()
      dbService.ListAllInventorys()
      dbService.ListAllPayments()
      dbService.ListAllPurchases()
      dbService.ListAllShipments()
    }
  }

  object IMPORT {
    def FROM( file: String ) = {
      new ImportTo( file )
    }

    class ImportTo( file: String ) {
      def TO( keyword: ObjectKeyword )
      {
        val bufferedSource = io.Source.fromFile(file)
        for (line <- bufferedSource.getLines) {
          val cols = line.split(",").map(_.trim)
          val emp = dbService.NewEmployee()
          emp.name = cols(0)
          emp.rank = cols(1).toInt
          emp.pay = cols(2).toDouble
          dbService.UpdateEmployee(emp)
        }
        bufferedSource.close
      }
    }
  }
}