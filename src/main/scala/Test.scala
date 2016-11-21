import cs345.bdsl._
import cs345.database._

import scala.language.postfixOps
import java.sql.Date
import java.sql.Timestamp

/**
  * Created by Sean on 11/9/16.
  */
object Test extends Bdsl {

  def main(args: Array[String]): Unit = {
    CREATE NEW EMPLOYEE WITH NAME AS "Bob Smith" WITH RANK AS 2;

    UPDATE EMPLOYEE 3 MODIFY PAY TO 10 MODIFY NAME TO "Bob Jones";

    // val dbService = new DBService
    
    PRINT ALL EMPLOYEE;

    REMOVE EMPLOYEE 3;

    PRINT ALL EMPLOYEE;

    var cli = dbService.NewClient()
    cli.name = "Client Name"
    cli.addDate = new Date(999923438);
    dbService.UpdateClient(cli);
    dbService.NewClient()
    println(dbService.GetClient(1));
    dbService.ListAllClients();
    dbService.DeleteClient(2);
    dbService.ListAllClients();

    var event = dbService.NewEvent()
    event.name = "EVENT Name"
    event.start = new Timestamp(999923438);
    event.end = new Timestamp(999923438);
    dbService.UpdateEvent(event);
    dbService.NewEvent()
    println(dbService.GetEvent(1));
    dbService.ListAllEvents();
    dbService.DeleteEvent(2);
    dbService.ListAllEvents();
    
    /*var emp = dbService.NewEmployee();
    println(emp);

    emp.name = "TEST";
    println(dbService.UpdateEmployee(emp));
    dbService.GetAllEmployees();

    dbService.DeleteEmployee(emp);
    dbService.GetAllEmployees();
*/
    dbService.Stop();
  }
}
