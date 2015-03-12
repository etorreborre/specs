(for more information, take a look at the [User Guide](UserGuide.md))

  1. declare a Specification class
```
import org.specs._

class helloWorld extends Specification
```
  1. a Specification is just a list of examples:
```
import org.specs._

class helloWorld extends Specification {
  "'hello world' has 11 characters" in {}
  "'hello world' matches 'h.* w.*'" in {}
}
```
  1. back-up your examples with expectations:
```
import org.specs._

class helloWorld extends Specification {
  "'hello world' has 11 characters" in {
     "hello world".size must_== 11
  }
  "'hello world' matches 'h.* w.*'" in {
     "hello world" must be matching("h.* w.*")
  }
}
```
  1. compile and run the class named **helloWorld**: `scala -cp specs-<version>.jar run helloWorld`:
```
Specification "helloWorld"
  specifies 
  + 'hello world' has 11 characters
  + 'hello world' matches 'h.* w.*'

Total for specification "helloWorld":
Finished in 0 second, 63 ms
2 examples, 2 assertions, 0 failure, 0 error
```
  1. now, you can add more structure to your specification, by grouping your examples:
```
import org.specs._

class helloWorld extends Specification {
  "hello world" should {
    "have 11 characters" in {
      "hello world".size must_== 11
    }
    "match 'h.* w.*'" in {
      "hello world" must be matching("h.* w.*")
    }
  }
  "Good bye cruel world" should {...}
}
```
  1. You can also extends the `SpecificationWithJUnit` class to run your specification inside [Eclipse](http://farm2.static.flickr.com/1098/1433239972_5f3a83e40c.jpg?v=0) for example (using the JUnit 4.7 library):
```
class helloWorldTest extends SpecificationWithJUnit { ... }
```

**Note**: `helloWorldTest` is declared as a class and not an object to be instantiated properly by JUnit runners.










