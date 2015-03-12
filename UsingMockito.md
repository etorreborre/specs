

# Introduction #

[Mockito](http://mockito.googlecode.com) is a mocking library which "lets you write beautiful tests with clean & simple API".

**specs** integrates Mockito expectations and add some syntactic sugar for an even easier experience of Mockito.

# Usage #

In order to use Mockito mocks in your specification, you need to mix in the Mockito trait.
```
  import org.specs.Specification
  import org.specs.mock.Mockito
  import org.mockito.Matchers._  // to use matchers like anyInt()

  object spec extends Specification with Mockito {
    
    val m = mock[java.util.List[String]] // a concrete class would be mocked with: mock(new java.util.LinkedList[String])
    
    // stub a method call with a return value
    m.get(0) returns "one"
  
    // call the method
    m.get(0)
  
    // verify that the call happened, this is an expectation which will throw a FailureException if that is not the case
    there was one(m).get(0)
    
    // we can also check that another call did not occur
    there was no(m).get(1)
  }

```

# Stubbing #

Stubbing values is as simple as calling a method on the mock and declaring what should be returned or thrown:
```
  m.get(1) returns "one"
  m.get(2) throws new Exception("forbidden")

```

You can specify different consecutive returned values by appending `thenReturns` or `thenThrows`:
```
  m.get(1) returns "one" thenReturns "two"
  m.get(2) throws new Exception("forbidden") thenReturns "999"

```
## Smart mocks ##

Mocks can be made to return "smart" null values (look at [setting smart return values](http://code.google.com/p/mockito/wiki/ReleaseNotes) for more details):
```
  trait Hello {
    def get(i: Int) = "hello"
  }
  val m = smartMock[Hello]
  m.get(0) must ==("") // instead of null
```

_Note_: there is an internal Mockito bug when doing the same thing on java.util.List[String](String.md)

## Argument matchers ##

The built-in Mockito argument matchers can be used to specify the method arguments for stubbing:
```
   mockedList.get(anyInt()) returns "element"
   mockedList.get(999) must_== "element"
```

Hamcrest matchers can also be used, allowing to create your own Hamcrest matchers:
```
   // stubbing using hamcrest (let's say IsNull returns your own hamcrest matcher):
   mockedList.contains(argThat(new IsNull)) returns true
```

While Mockito matchers are pretty exhaustive, for convenience a `any[T]` matcher is available:
```
   // the any matcher calls the org.mockito.Matchers.isA(classOf[T]) method
   mockedList.contains(any[String]) was called
```

You can even pass specs matchers directly as arguments where the implicit conversion would work:
```
   mockedList.get(==(123)) returns "one" // ==(_) is an alias for beEqualTo(_)

   // note that the implicit conversion transforming a specs Matcher to a Hamcrest matcher would not work here as the expected type for the contains method is Object
   mockedList.contains(isNull) returns true

```

## Callbacks ##

In some rare cases, it is necessary to have the return value depend on the parameters passed to the mocked method:
```
   val mockedList = mock[List[String]]
   
   mockedList.get(anyInt) answers { i => "The parameter is " + i.toString } 
 }
```

The function passed to `answers` will be called with each parameter passed to the stubbed method:
```
  // returns The parameter is 0
  s.mockedList.get(0)

  // The second call returns a different value: The parameter is 1
  s.mockedList.get(1) 
```

### Parameters for the answers function ###

Because of the use of reflection the function passed to `answers` will receive only instances of the java.lang.Object type.

More precisely, it will:

  * pass the mock object if both the method has no parameters and the function has one parameter: `mock.size answers { mock => mock.hashCode }`
  * pass the parameter if both the method and the function have one parameter: `mock.get(0) answers ( i => i.toString )`
  * pass the parameter and the mock object if the method has 1 parameter and the function has 2: `mock.get(0) answers { (i, mock) => i.toString + " for mock " + mock.toString }`
  * In any other cases, if f is a function of 1 parameter, the array of the method parameters will be passed and if the function has 2 parameters, the second one will be the mock.


# Verification #

By default Mockito doesn't expect any method to be called. However if your writing interaction-based specifications you want to specify that some methods are indeed called:
```
  mockedList.get(0)

  mockedList.get(0) was called
  mockedList.get(1) wasnt called
  mockedList.get(2) was notCalled

```

If anything fails a new `FailureException` will be thrown creating a failure for the current example.

## Constraints on call expectations ##

You can be more precise when specifying the number of calls on a mock:
```
 there was one(mockedList).get(0)          // one call only to get(0)
 there was no(mockedList).get(0)           // no calls to get(0)
 
 // were can also be used
 there were two(mockedList).get(0)          // 2 calls exactly to get(0)
 there were three(mockedList).get(0)        // 3 calls exactly to get(0)
 there were 4.times(mockedList).get(0)      // 4 calls exactly to get(0)
 
 there was atLeastOne(mockedList).get(0)   // at least one call to get(0)
 there was atLeastTwo(mockedList).get(0)   // at least two calls to get(0)
 there was atLeastThree(mockedList).get(0) // at least three calls to get(0)
 there was atLeast(4)(mockedList).get(0)   // at least four calls to get(0)

 there was atMostOne(mockedList).get(0)   // at most one call to get(0)
 there was atMostTwo(mockedList).get(0)   // at most two calls to get(0)
 there was atMostThree(mockedList).get(0) // at most three calls to get(0)
 there was atMost(4)(mockedList).get(0)   // at most four calls to get(0)

```

It is also possible to add all verifications inside a block, when several mocks are involved:
```
  got {
    one(mockedList1).get(0)
    two(mockedList2).get(1)
  }
```

## Order of calls ##

The order of method calls can be checked by creating calls and chaining them with `then`:

```
  val m1 = mock[List[String]]
  val m2 = mock[List[String]]

  m1.get(0)
  m1.get(0)
  m2.get(0)

  there was one(m1).get(0) then one(m1).get(1)

  // when several mocks are involved, the expected order must be given
  // whereas it is not necessary if the methods of one mock only are to
  // be checked in order
  there was one(m2).get(0) then one(m1).get(2) orderedBy (m1, m2)
```

# Spies #

Spies can also be used in order to do some "partial mocking" of real objects:
```

   val spiedList = spy(new LinkedList[String])

   // methods can be stubbed on a spy
   spiedList.size returns 100

   // other methods can also be used
   spiedList.add("one")
   spiedList.add("two") 
   
   // and verification can happen on a spy
   there was one(spiedList).add("one")
```

However, working with spies can be tricky:
```
  // if the list is empty, this will throws an IndexOutOfBoundsException
  spiedList.get(0) returns "one"
```

As advised in the Mockito documentation, `doReturn` must be used in that case:
```
  doReturn("one").when(spiedList).get(0)
```

# Using Mockito with ScalaTest #

You can use the `Mockito` trait with ScalaTest specifications but for this to work you also need to mix in the `DefaultExamplesExpectations` trait like this:
```
package test

import org.specs.mock.Mockito
import org.scalatest.WordSpec
import org.specs.specification._

/**
 * DefaultExampleExpectationsListener provides default behavior for specs creation of
 * examples and expectations.
 */
class ScalaTestAndMockitoSpec extends WordSpec with Mockito with DefaultExampleExpectationsListener {
  "it" should {
    "be possible to use the Mockito trait from specs" in {
      val m = mock[java.util.List[String]]
      m.get(0) returns "one"
      m.get(0)
      there was one(m).get(0)
    }
  }
}
```


# Deprecations #

![http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png](http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png) The library still contains deprecated methods for the previous Mockito API (which was working with Mockito < 1.8.4). They will be removed in a subsequent release.