package cs345.scheduler

import cs345.scheduler.datastructures._
import cs345.database._

import java.time.LocalDate
import java.time.LocalDateTime

object Scheduler {
  // TODO: Add functionailty to export an employees meetings or all company meetings.

  // TODO: ADD START AND END TIME;
  def firstAvailableTimeFromNow(employees: Seq[Employee]): LocalDateTime = {
    var curTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1)
    for (curIter <- 1 to 3000) {
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
    return LocalDateTime.of(0,0,0,0,0)
  }

  def firstAvailableTimeFromNow(employees: Seq[Employee], atLeast: Int): LocalDateTime = {
    if (employees.size < atLeast) {
      return LocalDateTime.of(0,0,0,0,0)
    }
    var curTime = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).plusHours(1)
    for (curIter <- 1 to 3000) {
      var timeWorks = 0
      for (emp <- employees){
        if (emp.schedule.isFree(curTime))
          timeWorks = timeWorks + 1
      }
      if (timeWorks >= atLeast)
        return curTime

      curTime = curTime.plusMinutes(30)
    }
    return LocalDateTime.of(0,0,0,0,0)
  }

  // def nextAvailableTime(employees: Seq[Employee], fromTime: LocalDateTime): LocalDateTime = {
  //   var curTime = fromTime.withMinute(0).withSecond(0).withNano(0)
  //   for (curIter <- 1 to 3000){
  //     var timeWorks = true
  //     for (emp <- employees){
  //       if (!emp.schedule.isFree(curTime))
  //         timeWorks = false
  //     }
  //     if (timeWorks)
  //       return curTime

  //     curTime = curTime.plusMinutes(30)
  //   }
  //   return LocalDateTime.of(0,0,0,0,0)
  // }

  // def nextAvailableTime(employees: Seq[Employee], atLeast: Int, fromTime: LocalDateTime): LocalDateTime = {
  //   if (employees.size < atLeast) {
  //     return LocalDateTime.of(0,0,0,0,0)
  //   }
  //   var curTime = fromTime.withMinute(0).withSecond(0).withNano(0).plusHours(1)
  //   for (curIter <- 1 to 3000){
  //     var timeWorks = 0
  //     for (emp <- employees){
  //       if (emp.schedule.isFree(curTime))
  //         timeWorks = timeWorks + 1
  //     }
  //     if (timeWorks >= atLeast)
  //       return curTime

  //     curTime = curTime.plusMinutes(30)
  //   }
  //   return LocalDateTime.of(0,0,0,0,0)
  // }
}

