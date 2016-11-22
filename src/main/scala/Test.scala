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
    //CREATE NEW EMPLOYEE
    //CREATE NEW EMPLOYEE
    CREATE NEW CLIENT
    //CREATE NEW MEETING WITH NAME AS "Meeting with Morgan Smith";
    UPDATE CLIENT 1 MODIFY NAME TO "Morgan Smith"

    UPDATE EMPLOYEE 3 MODIFY PAY TO 10 MODIFY NAME TO "Bob Jones";

    // val dbService = new DBService
    
    PRINT ALL EMPLOYEE;

    REMOVE EMPLOYEE 3;

    PRINT ALL EMPLOYEE;

    CREATE NEW CLIENT WITH NAME AS "Morgan Smith" WITH DATE AS 999923438;

    CREATE NEW CLIENT WITH NAME AS "Taylor Jones";

    PRINT ALL CLIENT;

    REMOVE CLIENT 2;

    PRINT ALL CLIENT;

    /*
    var cli = dbService.NewClient()
    cli.name = "Client Name"
    cli.addDate = new Date(999923438);
    dbService.UpdateClient(cli);
    dbService.NewClient()
    println(dbService.GetClient(1));
    dbService.ListAllClients();
    dbService.DeleteClient(2);
    dbService.ListAllClients();
    */

    CREATE NEW MEETING WITH NAME AS "Meeting with Morgan Smith";

    UPDATE MEETING 1 MODIFY START TO 999923438 MODIFY END TO 999923438;

    CREATE NEW MEETING WITH NAME AS "Meeting with Taylor Jones" WITH START AS 999923438;

    PRINT ALL MEETING;

    REMOVE MEETING 2;

    PRINT ALL MEETING;

    /*
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
    */
    
    /*var emp = dbService.NewEmployee();
    println(emp);

    emp.name = "TEST";
    println(dbService.UpdateEmployee(emp));
    dbService.GetAllEmployees();

    dbService.DeleteEmployee(emp);
    dbService.GetAllEmployees();
*/

    PRINT ALL;

    dbService.Stop();
  }
}
