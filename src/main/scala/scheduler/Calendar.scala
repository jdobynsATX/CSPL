package cs345.scheduler

import cs345.database._

import java.io._

import biweekly.ICalendar
import biweekly.Biweekly

object Calendar {
	def ExportEmployeeSchedule(empID: Int, filename: String) {
    val meetings: Seq[Meeting] = DBService.GetMeetingsForEmployee(empID)
    ExportMeetings(meetings, filename)
  }

  def ExportCompanySchedule(filename: String) {
    val meetings: Seq[Meeting] = DBService.GetAllMeetings()
    ExportMeetings(meetings, filename)
  }

  def ExportMeetings(meetings: Seq[Meeting], filename: String) {
    var ical = new ICalendar()
    for (meeting <- meetings) {
      ical.addEvent(meeting.getCalEvent())
    }
    val fileContent = Biweekly.write(ical).go()
    val pw = new PrintWriter(new File(filename + ".ics" ))
    pw.write(fileContent)
    pw.close
  }
}