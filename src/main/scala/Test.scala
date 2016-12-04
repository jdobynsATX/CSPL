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
    CREATE NEW EMPLOYEE WITH NAME AS "Bob Jones" WITH RANK AS 3;
    CREATE NEW EMPLOYEE WITH NAME AS "Bill Smith" WITH RANK AS 2;
    CREATE NEW EMPLOYEE WITH NAME AS "Bill Jones" WITH RANK AS 4;

    BATCH ALL EMPLOYEE WHERE RANK LESSTHANEQUAL 3 WHERE PAY EQUAL 0.0 MODIFY PAY TO 10;

    CREATE NEW EMPLOYEE WITH NAME AS "12lul";
    CREATE NEW INVENTORY WITH NAME AS "item2" WITH QUANTITY AS 12 WITH TOTAL_COST AS 323.3;
    CREATE NEW CLIENT WITH NAME AS "testing" WITH BALANCE AS 23.12;
    CREATE NEW MEETING WITH NAME AS "Meeting with Morgan Smith";
    CREATE NEW PROJECT WITH NAME AS "Chalupa Extravaganza";
    ASSIGN EMPLOYEE 1 TO EVENT MEETING 1;
    ASSIGN EMPLOYEE 1 TO EVENT PROJECT 1;
    PRINT ALL PROJECT_ASSIGNMENTS;

    UPDATE MEETING 1 MODIFY NAME TO "Meeting with Morgan Freeman" MODIFY CLIENT_ID TO 2
    UPDATE PROJECT 1 MODIFY NAME TO "Taco Night" MODIFY CLIENT_ID TO 2
    //CREATE NEW PURCHASE FOR_CLIENT 2 OF_ITEM 1 FOR_COST 22.1 FOR_AMOUNT 5 REVIEWED_BY 3;
    //CREATE NEW PAYMENT FOR_CLIENT 2 FOR_AMOUNT 12.2 REVIEWED_BY 3;
    //CREATE NEW SHIPMENT OF_ITEM 1 FOR_AMOUNT 23 FOR_COST 232.2;

    //IMPORT FROM "file.csv" TO EMPLOYEE

    PRINT ALL;
    /*CREATE NEW EMPLOYEE WITH NAME AS "Bob Smith" WITH RANK AS 2;
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

    PRINT ALL;*/

    dbService.Stop();
  }
}
