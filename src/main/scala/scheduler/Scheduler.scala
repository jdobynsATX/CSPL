package cs345.scheduler

import cs345.scheduler.datastructures._
import cs345.database._

import java.time.LocalDate
import java.time.LocalDateTime

object Scheduler {
  def firstAvailableTimeFromNow(employees: Seq[Employee]): LocalDateTime = {
    var curTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1)
    for (curIter <- 1 to 250){
      // println( "Value of curTime: " + curTime )
      var timeWorks = true
      for (emp <- employees){
        if (!emp.schedule.isFree(curTime))
          timeWorks = false
      }
      if (timeWorks)
        return curTime

      curTime = curTime.plusMinutes(30)
    }
    return LocalDateTime.now()
  }

  def firstAvailableTimeFromNow(employees: Seq[Employee], atLeast: Int): LocalDateTime = {
    var curTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1)
    for (curIter <- 1 to 250){
      var timeWorks = 0
      for (emp <- employees){
        if (emp.schedule.isFree(curTime))
          timeWorks = timeWorks + 1
      }
      if (timeWorks >= atLeast)
        return curTime

      curTime = curTime.plusMinutes(30)
    }
    return LocalDateTime.now()
  }

  // def firstAvailableTimeFromNow(employees: Seq[ScheduleMap]): LocalDateTime = {
  //   var curTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1)
  //   for (curIter <- 1 to 250){
  //     // println( "Value of curTime: " + curTime )
  //     var timeWorks = true
  //     for (emp <- employees){
  //       if (!emp.isFree(curTime))
  //         timeWorks = false
  //     }
  //     if (timeWorks)
  //       return curTime

  //     curTime = curTime.plusMinutes(30)
  //   }
  //   return LocalDateTime.now()
  // }
}

