package cs345.bdsl

import scala.language.postfixOps
import scala.collection.mutable.Map


/**
  * Created by Sean on 11/9/16.
  */

class Bdsl {
  class Employee {
    var id = -1
    var name = ""
    var rank = -1
    var pay = 0.0

    override def toString: String = {
      return "id: " + id + " name: " + name + " rank: " + rank + " pay: " + pay
    }
  }
  
  object CREATE {

    def NEW(keyword: ObjectKeyword) = {
      val emp = new Employee
      new CreateEmployee(emp)
    }

    class CreateEmployee(emp: Employee) {

      def WITH(keyword: AttributeKeyword) = {
        new AsContinue( keyword )
      }

      class AsContinue(keyword: AttributeKeyword) {
        def AS(num: Int)= {
          val attribute = GetAttribute(keyword)
          println("Inserting (" + attribute + ", " + num + ") into emp record")
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
          }

          println(emp)
          new CreateEmployee(emp)
        }

        def AS(str: String) = {
          val attribute = GetAttribute(keyword)
          println("Inserting (" + attribute + ", " + str + ") into emp record")
          emp.name = str
          println(emp)
          new CreateEmployee(emp)
        }
      }
    }

    def GetAttribute(keyword: AttributeKeyword): String = keyword match {
      case ID => "id"
      case NAME => "name"
      case RANK => "rank"
      case _ => "UNKNOWN"
    }
  }

  object UPDATE {

    def EMPLOYEE( id: Int ) = {
      println( "Obtaining EMPLOYEE")
      val emp = new Employee
      new ModifyEmployee(emp)
    }

    class ModifyEmployee( emp: Employee ) {

      def MODIFY(keyword: AttributeKeyword) = {
        new UpdateContinue( keyword )
      }

      class UpdateContinue( keyword: AttributeKeyword ) {
        def TO(num: Int) = {
          val attribute = GetAttribute(keyword)
          println("Updating (" + attribute + ", " + num + ") into emp record")
          keyword match {
            case ID => emp.id = num
            case RANK => emp.rank = num
          }

          println(emp)
          new ModifyEmployee(emp)
        }

        def TO(str: String) = {
          val attribute = GetAttribute(keyword)
          println("Updating (" + attribute + ", " + str + ") into emp record")
          emp.name = str
          println(emp)
          new ModifyEmployee(emp)
        }
      }
    }

    def GetAttribute(keyword: AttributeKeyword): String = keyword match {
      case ID => "id"
      case NAME => "name"
      case RANK => "rank"
      case _ => "UNKNOWN"
    }
  }
}