package cs345.scheduler.datastructures

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.BitSet
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

object DEFAULT {
  val START_DATE: LocalDate = LocalDate.of(2016,11,1)
}

class ScheduleMap(var startDate: LocalDate, val intervalMins: Int, val numDays: Int) {
  def this() {
    this(DEFAULT.START_DATE, 30, 365)
    var date: LocalDate = startDate
    for (day <- 1 to 365) {
      if (date.getDayOfWeek().getValue() <= 5) {
        blockWorkingDay(date)
      } else {
        blockDay(date)
      }
      date = date.plusDays(1)
    }
  }

  def this(data: Blob) {
    this(DEFAULT.START_DATE, 30, 365)
    bitmap = BitSet.valueOf(data.getBytes(1, (NUM_INTERVALS/8)-1))
  }

  def this(data: Array[Byte]) {
    this(DEFAULT.START_DATE, 30, 365)
    bitmap = BitSet.valueOf(data)
  }

  val INTERVALS_PER_DAY: Int = (1440/intervalMins)
  val INTERVALS_PER_HOUR: Int = (60/intervalMins)
  val NUM_INTERVALS: Int = numDays * INTERVALS_PER_DAY

  var bitmap: BitSet = new BitSet(INTERVALS_PER_DAY * numDays)

  def setFree(dateTime: LocalDateTime) = {
    bitmap.set(convertToOffset(dateTime), false)
  }

  def setBusy(dateTime: LocalDateTime) = {
    bitmap.set(convertToOffset(dateTime), true)
  }

  def setFree(startTime: LocalDateTime, endTime: LocalDateTime) = {
    bitmap.set(convertToOffset(startTime), convertToOffset(endTime.minusMinutes(1)) + 1, false)
  }

  def setBusy(startTime: LocalDateTime, endTime: LocalDateTime) = {
    // HACKY: To get not inclusive of one next block of time on interval changes
    bitmap.set(convertToOffset(startTime), convertToOffset(endTime.minusMinutes(1)) + 1, true)
  }

  def blockWorkingDay(day: LocalDate) = {
    setBusy(day.atTime(0,0), day.atTime(7,59))
    setBusy(day.atTime(12,0), day.atTime(12,59))
    setBusy(day.atTime(17,0), day.atTime(23,59))
  }

  def blockDay(day: LocalDate) = {
    setBusy(day.atTime(0,0), day.atTime(23,59))
  }

  def isFree(dateTime: LocalDateTime): Boolean = {
    return !bitmap.get(convertToOffset(dateTime))
  }

  def isFree(startTime: LocalDateTime, endTime: LocalDateTime): Boolean = {
    return bitmap.get(convertToOffset(startTime), convertToOffset(endTime.minusMinutes(1)) + 1).isEmpty()
  }

  def getStartDate(): LocalDate = {
    return startDate
  }

  def toByteArray(): Array[Byte] = {
    return bitmap.toByteArray()
  }

  def toSerialBlob(): SerialBlob = {
    return new SerialBlob(bitmap.toByteArray())
  }

  def convertToOffset(dateTime: LocalDateTime): Int = {
    
    val day = dateTime.getDayOfYear()
    val hour = dateTime.getHour()
    val minute = dateTime.getMinute()
    return day * INTERVALS_PER_DAY + hour * INTERVALS_PER_HOUR + (minute/intervalMins)
  }

  override def toString(): String = {
    return bitmap.cardinality() + ""
    // return bitmap.toString()
  }
}