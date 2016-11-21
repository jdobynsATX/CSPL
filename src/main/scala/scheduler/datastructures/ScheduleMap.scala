package bdsl.scheduler.datastructures

import java.time.LocalDate
import java.time.LocalDateTime

class ScheduleMap(var startDate: LocalDate, val intervalMins: Int, val numDays: Int) {
  def this() {
    this(LocalDate.now(), 30, 365)
  }

  val INTERVALS_PER_DAY: Int = (1440/intervalMins)
  var bitmap: Array[Boolean] = new Array[Boolean](INTERVALS_PER_DAY * numDays)

  def IsFree(dateTime: LocalDateTime): Boolean = {
    return false
  }
}