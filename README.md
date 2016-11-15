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
