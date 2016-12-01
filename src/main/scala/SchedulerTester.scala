import cs345.scheduler.datastructures._
import cs345.scheduler._
import cs345.database._

import java.time.LocalDateTime
import java.sql.Timestamp
import java.time.ZoneId


object SchedulerTester {
  def main(args: Array[String]): Unit = {
    var emp1 = new Employee(1)
    emp1.name = "Bobby Jones"
    var emp2 = new Employee(2)
    emp2.name = "George Doe"
    var emp3 = new Employee(3)
    emp3.name = "Jane Smith"

    var emps1: Array[Employee] = new Array[Employee](0)
    emps1 = emps1 :+ (emp1)
    emps1 = emps1 :+ (emp2)
    emps1 = emps1 :+ (emp3)
    for (emp <- emps1) {
        println(emp)
    }

    var emps2: Array[Employee] = new Array[Employee](0)
    emps2 = emps2 :+ (emp2)
    emps2 = emps2 :+ (emp3)

    var emps3: Array[Employee] = new Array[Employee](0)
    emps3 = emps3 :+ (emp1)

    var meeting1 = new Meeting(1)
    meeting1.name = "Test meeting 1"

    // SCHEDULE 90 Meeting with 3 employees in list emps1
    val meeting1StartTime = Scheduler.firstAvailableTimeFromNow(90, emps1)
    // SET start and end times, using provided function.
    meeting1.setStart(meeting1StartTime)
    meeting1.setEnd(meeting1StartTime.plusMinutes(90))
    // SET busy for all employees in list emps1
    for (emp <- emps1) {
        emp.schedule.setBusy(meeting1StartTime, meeting1StartTime.plusMinutes(90))
    }
    println(meeting1)

    var meeting2 = new Meeting(2)
    meeting2.name = "Test meeting 2"

    val meeting2StartTime = Scheduler.firstAvailableTimeFromNow(90, emps2)
    meeting2.setStart(meeting2StartTime)
    meeting2.setEnd(meeting2StartTime.plusMinutes(90))
    
    for (emp <- emps2) {
        emp.schedule.setBusy(meeting2StartTime, meeting2StartTime.plusMinutes(90))
    }
    println(meeting2)

    var meeting3 = new Meeting(3)
    meeting3.name = "Test meeting 3"

    val meeting3StartTime = Scheduler.firstAvailableTimeFromNow(90, emps1)
    meeting3.setStart(meeting3StartTime)
    meeting3.setEnd(meeting3StartTime.plusMinutes(90))
    
    for (emp <- emps1) {
        emp.schedule.setBusy(meeting3StartTime, meeting3StartTime.plusMinutes(90))
    }
    println(meeting3)

    var meeting4 = new Meeting(4)
    meeting4.name = "Test meeting 4"

    val meeting4StartTime = Scheduler.firstAvailableTimeFromNow(60, emps3)
    meeting4.setStart(meeting4StartTime)
    meeting4.setEnd(meeting4StartTime.plusMinutes(60))
    
    for (emp <- emps3) {
        emp.schedule.setBusy(meeting4StartTime, meeting4StartTime.plusMinutes(60))
    }
    println(meeting4)

    val meeting5StartTime = Scheduler.firstAvailableTimeFromNow(30, emps1)
    println(meeting5StartTime)

    // var schedule1 = new ScheduleMap()
    // var schedule2 = new ScheduleMap()
    // var schedule3 = new ScheduleMap()

    // schedule1.setBusy(LocalDateTime.now(), LocalDateTime.now().plusHours(5))

    // var schedules: Array[ScheduleMap] = new Array[ScheduleMap](0)
    // schedules = schedules :+ (schedule1)
    // schedules = schedules :+ (schedule2)
    // schedules = schedules :+ (schedule3)

    // println(Scheduler.firstAvailableTimeFromNow(schedules))

    // print("Created new: " + schedule1)
    // schedule1.setBusy(LocalDateTime.now())
    // print("Updated: " + schedule1)
  }
}
