# Business DSL
### CS 345 Fall 2016
This is a business oriented domain specific language designed to easily perform operations on a business system with simple English syntax. It  has operations to create and modify data, perform batch modifications on a query of objects, and import data from files. One of the main benefits of this DSL is the ability to assign employees and clients to meetings and automatically reschedule meeting times to fit individual schedules, and to export these schedules to iCalendar (.ics) files.

#### Group Members
- Nathan Bain
- Jonathan Dobyns
- Sean Wang
- Rosie Wu

## Setup

SBT is used to build and run. Make sure you [install sbt](http://www.scala-sbt.org/release/docs/Setup.html) first. (Note: The make file just calls `sbt` commands.)

Mac:
``` 
brew install sbt 
```


Then you need the files to run which can be obtained from this github repository.

```
git clone https://github.com/rxwu/CSPL.git
```

## Testing

To test the code and see output we just need to run the code. You can do this by using `make test` which is mapped to `sbt run` command which just runs Test.scala.

```
make test
```

##Console

To use the dsl on in the console

```
$ sbt console
scala> val bdsl = new cs345.bdsl.Bdsl
scala> import bdsl._
scala> import cs345.bdsl._
```
and then type commands
```
scala> CREATE NEW EMPLOYEE WITH NAME AS "Taylor Jones"
```

##Functionality
####Adding new objects
```
CREATE NEW objectType [ WITH attribute AS value ]

Ex) CREATE NEW EMPLOYEE WITH NAME AS "Morgan Smith" WITH RANK AS 4
```
####Updating objects
```
UPDATE objectType id [ MODIFY attribute TO value ]
UPDATE objectType "name" [ MODIFY attribute TO value]

Ex) UPDATE EMPLOYEE 3 MODIFY RANK TO 6
Ex) UPDATE EMPLOYEE "Morgan Freeman" MODIFY RANK TO 6 MODIFY PAY TO 24.50
```
####Removing objects
```
REMOVE objectType id
REMOVE objectType "name"

Ex) REMOVE EMPLOYEE 3
Ex) REMOVE "Morgan Freeman"
```
####Assignment to meetings and projects
```
ASSIGN EMPLOYEE id TO EVENT MEETING id
ASSIGN EMPLOYEE "name" TO EVENT MEETING "name"
ASSIGN EMPLOYEE "name" TO EVENT PROJECT "name"


Ex) ASSIGN EMPLOYEE 5 TO EVENT MEETING 3
Ex) ASSIGN EMPLOYEE "Jeffrey Kramer" TO EVENT PROJECT "Burrito Night" 
```
####Printing objects
```
PRINT ALL objectType
PRINT ALL

Ex) PRINT ALL EMPLOYEE
```
####Batch Operations
```
BATCH ALL objectType [ WHERE attribute comparator value ] [ MODIFY attribute TO value ] [ ASSIGN TO EVENT MEETING id ][ PRINT ] [ REMOVE ]

Ex) BATCH ALL EMPLOYEE WHERE RANK LESSTHANEQUAL 11 MODIFY RANK TO 11 PRINT
```
This works for employees, clients, meetings, and projects. Employees can also be batch assigned to meetings.
####Importing from files
```
IMPORT FROM "filename.csv" TO objectType

Ex) IMPORT FROM "employees.csv" TO EMPLOYEE
```
This works for csv files for employees and clients.

####Exporting Schedule to Calendar
```
EXPORT EMPLOYEE id TO "calendar_file_name"
EXPORT EMPLOYEE "name" TO "calendar_file_name"
EXPORT COMPANY TO "calender_file_name"

Ex) EXPORT EMPLOYEE 4 TO "Calendar1"
Ex) EXPORT EMPLOYEE "Ryan Reynolds" TO "Calendar2"
Ex) EXPORT COMPANY TO "Calendar3"
```
This works for csv files for employees and clients.
####Restocking
```
CREATE NEW SHIPMENT OF_ITEM id FOR_AMOUNT quantity FOR_COST cost

Ex) CREATE NEW SHIPMENT OF_ITEM 4 FOR_AMOUNT 12 FOR_COST 12.99
```
####Orders
```
CREATE NEW PURCHASE FOR_CLIENT id OF_ITEM id FOR_AMOUNT quantity FOR_COST cost REVIEWED_BY id

Ex) CREATE NEW PURCHASE FOR_CLIENT 15 OF_ITEM 2 FOR_AMOUNT 12 FOR_COST 12.99 REVIEWED_BY 1
```
####Payments
```
CREATE NEW PAYMENT FOR_CLIENT id FOR_AMOUNT amount REVIEWED_BY id

Ex) CREATE NEW PAYMENT FOR_CLIENT 4 FOR_AMOUNT 36.75 REVIEWED_BY 4
```
