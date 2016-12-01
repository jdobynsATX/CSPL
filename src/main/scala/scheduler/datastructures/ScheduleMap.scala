package cs345.scheduler.datastructures

import java.time.LocalDate
import java.time.LocalDateTime
import java.util.BitSet
import java.sql.Blob
import javax.sql.rowset.serial.SerialBlob

class ScheduleMap(var startDate: LocalDate, val intervalMins: Int, val numDays: Int) {
  def this() {
    // TODO: CREATE DEFAULT SCHEDULE WITH TIMES BLOCKED OFF;
    this(LocalDate.of(2016,6,1), 30, 365)
    println("DEBUG BitSet size: " + bitmap.size())
  }

  def this(data: Blob) {
    this(LocalDate.of(2016,6,1), 30, 365)
    bitmap = BitSet.valueOf(data.getBytes(1, (NUM_INTERVALS/8)-1))
    println("DEBUG BitSet size: " + bitmap.size())
  }

  def this(data: Array[Byte]) {
    this(LocalDate.of(2016,6,1), 30, 365)
    bitmap = BitSet.valueOf(data)
    println("DEBUG BitSet size: " + bitmap.size())
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
    bitmap.set(convertToOffset(startTime), convertToOffset(endTime) + 1, false)
  }

  def setBusy(startTime: LocalDateTime, endTime: LocalDateTime) = {
    bitmap.set(convertToOffset(startTime), convertToOffset(endTime) + 1, true)
  }

  def isFree(dateTime: LocalDateTime): Boolean = {
    return !bitmap.get(convertToOffset(dateTime))
  }

  def isFree(startTime: LocalDateTime, endTime: LocalDateTime): Boolean = {
    return bitmap.get(convertToOffset(startTime), convertToOffset(endTime)).isEmpty()
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
    return bitmap.toString()
  }
}