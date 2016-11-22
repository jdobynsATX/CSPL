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
    /*CREATE NEW EMPLOYEE WITH NAME AS "Bob Smith" WITH RANK AS 2;
    CREATE NEW EMPLOYEE WITH NAME AS "Bob Jones" WITH RANK AS 3;
    CREATE NEW EMPLOYEE WITH NAME AS "Bill Smith" WITH RANK AS 2;
    CREATE NEW EMPLOYEE WITH NAME AS "Bill Jones" WITH RANK AS 4;

    UPDATE ALL EMPLOYEE WHERE RANK LESSTHANEQUAL 3 WHERE PAY EQUAL 0 MODIFY PAY TO 10;*/

    IMPORT FROM "file.csv" TO EMPLOYEE

    PRINT ALL;
    /*CREATE NEW EMPLOYEE WITH NAME AS "Bob Smith" WITH RANK AS 2;

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

    CREATE NEW EVENT WITH NAME AS "Meeting with Morgan Smith";

    UPDATE EVENT 1 MODIFY START TO 999923438 MODIFY END TO 999923438;

    CREATE NEW EVENT WITH NAME AS "Meeting with Taylor Jones" WITH START AS 999923438;

    PRINT ALL EVENT;

    REMOVE EVENT 2;

    PRINT ALL EVENT;

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

    PRINT ALL;*/

    dbService.Stop();
  }
}
