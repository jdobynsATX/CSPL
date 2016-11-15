import cs345.bdsl._

import scala.language.postfixOps
/**
  * Created by Sean on 11/9/16.
  */
object Test extends Bdsl {

  def main(args: Array[String]): Unit = {
    CREATE NEW EMPLOYEE WITH ID AS 6 WITH NAME AS "Bob Smith" WITH RANK AS 2;

    UPDATE EMPLOYEE 496 MODIFY RANK TO 10 MODIFY NAME TO "Bob Jones";
  }
}
