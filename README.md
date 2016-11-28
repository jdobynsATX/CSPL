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

####Printing objects
```
PRINT ALL objectType
PRINT ALL EMPLOYEE
```

####Restocking
```
CREATE NEW SHIPMENT OF_ITEM -id- FOR_AMOUNT -count- FOR_COST -total_cost-
```

####Orders
```
CREATE NEW PURCHASE FOR_CLIENT -client_id- OF_ITEM -inv_id- FOR_AMOUNT -count- FOR_COST -cost- REVIEWED_BY -emp_id-
```

####Payments
```
CREATE NEW PAYMENT FOR_CLIENT -client_id- FOR_AMOUNT -amount- REVIEWED_BY -emp_id-
```
