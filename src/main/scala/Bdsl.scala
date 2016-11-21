package cs345.bdsl

import cs345.database._

import scala.language.postfixOps
import scala.collection.mutable.Map


/**
  * Created by Sean on 11/9/16.
  */

class Bdsl {
  val dbService = new DBService
  object CREATE {

    def NEW(keyword: ObjectKeyword) = {
      val emp = dbService.NewEmployee()
      println( "Created new employee with ID " + emp.id )
      new CreateEmployee(emp)
    }

    class CreateEmployee(emp: Employee) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          val attribute = GetAttribute(keyword)
          //println("Inserting (" + attribute + ", " + num + ") into emp record")
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
            case PAY => emp.pay = num
          }

          dbService.UpdateEmployee(emp)

          //println(emp)
          new CreateEmployee(emp)
        }

        def AS(str: String) = {
          val attribute = GetAttribute(keyword)
          //println("Inserting (" + attribute + ", " + str + ") into emp record")
          emp.name = str

          dbService.UpdateEmployee(emp)
          //println(emp)
          new CreateEmployee(emp)
        }
      }
    }

    def GetAttribute(keyword: AttributeKeyword): String = keyword match {
      case ID => "id"
      case NAME => "name"
      case RANK => "rank"
      case PAY => "pay"
      case _ => "UNKNOWN"
    }
  }

  object UPDATE {

    def EMPLOYEE( id: Int ) = {
      println( "Updating EMPLOYEE " + id)
      val emp = dbService.GetEmployee(id)
      new ModifyEmployee(emp)
    }

    class ModifyEmployee( emp: Employee ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int) = {
          val attribute = GetAttribute(keyword)
          //println("Updating (" + attribute + ", " + num + ") into emp record")
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
            case PAY => emp.pay = num
          }

          dbService.UpdateEmployee(emp)

          //println(emp)
          new ModifyEmployee(emp)
        }

        def TO(str: String) = {
          val attribute = GetAttribute(keyword)
          //println("Updating (" + attribute + ", " + str + ") into emp record")
          emp.name = str
          dbService.UpdateEmployee(emp)
          //println(emp)
          new ModifyEmployee(emp)
        }
      }
    }

    def GetAttribute(keyword: AttributeKeyword): String = keyword match {
      case ID => "id"
      case NAME => "name"
      case RANK => "rank"
      case PAY => "pay"
      case _ => "UNKNOWN"
    }
  }

  object REMOVE {

    def EMPLOYEE( id: Int ) = {
      println( "Removing EMPLOYEE " + dbService.DeleteEmployee(id) )
    }
  }

  object PRINT {
    def ALL( keyword: EmployeeKeyword ) = { 
      dbService.GetAllEmployees()
    }
  }
}