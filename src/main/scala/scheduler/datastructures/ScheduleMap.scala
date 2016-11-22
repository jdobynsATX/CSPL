package cs345.scheduler.datastructures

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.BitSet

class ScheduleMap(var startDate: LocalDate, val intervalMins: Int, val numDays: Int) {
  def this() {
    this(LocalDate.now(), 30, 365)
  }

  val INTERVALS_PER_DAY: Int = (1440/intervalMins)
  val INTERVALS_PER_HOUR: Int = (60/intervalMins)

  var bitmap: BitSet = new BitSet(INTERVALS_PER_DAY * numDays)

  def SetFree(dateTime: LocalDateTime) = {
    bitmap.set(ConvertToOffset(dateTime), false)
  }

  def SetBusy(dateTime: LocalDateTime) = {
    bitmap.set(ConvertToOffset(dateTime), true)
  }

  def SetFree(startTime: LocalDateTime, endTime: LocalDateTime) = {
    bitmap.set(ConvertToOffset(startTime), ConvertToOffset(endTime) + 1, false)
  }

  def SetBusy(startTime: LocalDateTime, endTime: LocalDateTime) = {
    bitmap.set(ConvertToOffset(startTime), ConvertToOffset(endTime) + 1, true)
  }

  def IsFree(dateTime: LocalDateTime): Boolean = {
    return bitmap.get(ConvertToOffset(dateTime))
  }

  def ConvertToOffset(dateTime: LocalDateTime): Int = {
    
    val day = dateTime.getDayOfYear()
    val hour = dateTime.getHour()
    val minute = dateTime.getMinute()
    return day * INTERVALS_PER_DAY + hour * INTERVALS_PER_HOUR + (minute/intervalMins)
  }

  override def toString(): String = {
    return bitmap.toString()
  }
}