# Business DSL
### CS 345 Fall 2016

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
CREATE NEW EMPLOYEE WITH NAME AS "Morgan Smith" WITH RANK AS 4
```
####Updating objects
```
UPDATE objectType id [ MODIFY attribute TO value ]
UPDATE EMPLOYEE 3 MODIFY RANK TO 6
```
####Removing objects
```
REMOVE objectType id
REMOVE EMPLOYEE 3
```
####Assignment to meetings
```
ASSIGN EMPLOYEE id TO EVENT MEETING id
ASSIGN EMPLOYEE 5 TO EVENT MEETING 3
```
####Printing objects
```
PRINT ALL objectType
PRINT ALL EMPLOYEE
PRINT ALL
```
####Batch Operations
```
BATCH ALL objectType [ WHERE attribute comparator value ] [ MODIFY attribute TO value ] [ ASSIGN TO EVENT MEETING id ][ PRINT ] [ REMOVE ]
BATCH ALL EMPLOYEE WHERE RANK LESSTHANEQUAL 11 MODIFY RANK TO 11 PRINT
```
This works for employees, clients, meetings, and projects. Employees can also be batch assigned to meetings.
####Importing from files
```
IMPORT FROM "filename.csv" TO objectType
IMPORT FROM "employees.csv" TO EMPLOYEE
```
This works for csv files for employees and clients.
####Restocking
```
CREATE NEW SHIPMENT OF_ITEM id FOR_AMOUNT quantity FOR_COST cost
```
####Orders
```
CREATE NEW PURCHASE FOR_CLIENT id OF_ITEM id FOR_AMOUNT quantity FOR_COST cost REVIEWED_BY id
```
####Payments
```
CREATE NEW PAYMENT FOR_CLIENT id FOR_AMOUNT amount REVIEWED_BY id
```
