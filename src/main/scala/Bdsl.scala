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

    def NEW(keyword: EventKeyword) = {
      val env = dbService.NewEvent()
      println( "Created new event with ID " + env.id )
      new CreateEvent(env)
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
            case PAY => emp.pay = num
          }
          dbService.UpdateEmployee(emp)
          new CreateEmployee(emp)
        }

        def AS(str: String) = {
          emp.name = str
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
      }
    }

    class CreateEvent(env: Event) {

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
          dbService.UpdateEvent(env)
          new CreateEvent(env)
        }

        def AS(str: String) = {
          env.name = str
          dbService.UpdateEvent(env)
          new CreateEvent(env)
        }
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

      def PRINT = {
        println("printing")
        for( emp <- emps ) {
          println( emp )
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

    def EVENT( id: Int ) = {
      println( "Updating EVENT " + id)
      new ModifyEvent(dbService.GetEvent(id))
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
            case PAY => emp.pay = num
          }
          dbService.UpdateEmployee(emp)
          new ModifyEmployee(emp)
        }

        def TO(str: String) = {
          emp.name = str
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
        def To(num: Int)= {
          keyword match {
            case ID => cli.id = num
            case DATE => cli.addDate = new Date( num )
          }
          dbService.UpdateClient(cli)
          new ModifyClient(cli)
        }

        def To(str: String) = {
          cli.name = str
          dbService.UpdateClient(cli)
          new ModifyClient(cli)
        }
      }
    }

    class ModifyEvent( env: Event ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int)= {
          keyword match {
            case ID => env.id = num
            case START => env.start = new Timestamp(num)
            case END => env.end = new Timestamp(num)
          }
          dbService.UpdateEvent(env)
          new ModifyEvent(env)
        }

        def TO(str: String) = {
          env.name = str
          dbService.UpdateEvent(env)
          new ModifyEvent(env)
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

    def EVENT( id: Int ) = {
      println( "Removing EVENT " + dbService.DeleteEvent(id) )
    }
  }

  object PRINT {
    def ALL( keyword: ObjectKeyword ) = { 
      keyword match {
        case EMPLOYEE => dbService.ListAllEmployees()
        case CLIENT => dbService.ListAllClients()
        case EVENT => dbService.ListAllEvents()
      }
    }

    def ALL = {
      dbService.ListAllEmployees()
      dbService.ListAllClients()
      dbService.ListAllEvents()
    }
  }
}