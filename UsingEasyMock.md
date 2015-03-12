

# Introduction #

[EasyMock](http://easymock.org) is a mocking library:

EasyMock provides Mock Objects for interfaces (and objects through the class extension) by generating them on the fly using Java's proxy mechanism. Due to EasyMock's unique style of recording expectations, most refactorings will not affect the Mock Objects. So EasyMock is a perfect fit for Test-Driven Development.

**specs** integrates EasyMock expectations and add some syntactic sugar for an even simpler experience of EasyMock.

# Usage #

In order to use EasyMock mocks in your specification, you need to mix in the EasyMock trait. Here is an example of EasyMock usage:
```
  import org.specs.Specification
  import org.specs.mock.EasyMock
  import org.easymock.EasyMock._  // to use matchers like anyInt()

  object spec extends Specification with EasyMock {
    
    class ToMock {
      def voidMethod = ()
  
      @throws(classOf[Exception])
      def size = 1
      def get(i: Int) = i.toString
    }

    val m = mock[ToMock] // this creates a "strict" mock
    
    "This is the canonical way of using EasyMock" in {
        m.voidMethod    // declare expected mock calls
        replay(m)       // the mock needs to be replayed
        m.voidMethod    // use the mock
        verify(m)       // verify expectations
    }
```

# Mocks creation #

EasyMock provides different methods to create mocks:

  * `createMock`. This method is available as `mock` in specs. Note that the class extension of EasyMock is used to create mocks so you need the corresponding jar in your classpath.

  * `createNiceMock`. This method is available as `niceMock` in specs.

  * `createStrictMock`. This method is available as `strictMock` in specs.

# Mock methods #

There are many methods directly applicable to mocks instead of calling the corresponding `EasyMock.(...)` method:

  * `mock.toNice`
  * `mock.toDefault`
  * `mock.toStrict`
  * `mock.reset`
  * `mock.replay`
  * `mock.verify`
  * `mock.checkOrder`
  * `mock.dontCheckOrder`
  * `mock.checkIsUsedInOneThread`
  * `mock.dontCheckIsUsedInOneThread`
  * `mock.makeThreadSafe`
  * `mock.dontMakeThreadSafe`

# Block replay #

It is possible to replay all mocks:
```
   expect { 
     m1.method1
     m2.method2
   } // m1 and m2 are automatically replayed. This is equivalent to replay(m1, m2)
```

# Verification #

The `verify` method is used as you would do in EasyMock:
```
  verify(m1, m2, m3)
```

In that case, there is no "block verify" method available because this wouldn't save much from a call to verify with several arguments.

# Returned values #

Returned values are specified using `returns`:
```
  m.get(1) returns "one" 
```

EasyMock makes a strong difference between mocks and stubs, stub calls not being checked by `verify`. If you just want method calls to be stubbed you can use `stubReturn`:

```
  m.get(1) stubReturns "one" 
```


# Thrown exceptions #

Thrown exceptions are specified using `throws`:
```
  m.size throws new Exception("bad call") 
```

# Consecutive calls #

You can specify different consecutive returned values by appending `thenReturns` or `thenThrows`:
```
  m.get(1) returns "one" thenReturns "two"
  m.get(2) throws new Exception("forbidden") thenReturns "999"
```

# Callbacks #

In some rare cases, it is necessary to have the return value depend on the parameters passed to the mocked method:
```
   val mockedList = mock[List[String]]
   
   mockedList.get(anyInt) answers { i => "The parameter is " + i.toString } 
```

In this case, the `answers` method can be used with either:

  * a no parameter function if the mocked method takes no parameters
  * a one parameter function, with a parameter of type Any, if the mocked method takes one parameter
  * a one parameter function, with one parameter of type `Array[Any]` if the mocked method takes more than one parameter

# Delegates #

Calls can also be delegated to another object implementing the same interface as the mocked object. Use the `delegatesTo` method to that purpose:
```
  m.get(1) delegatesTo new ToMock
  replay(m)
  m.get(1) must_== 1 
```

Note that `stubDelegates` is also available when you just need to delegate returned values with no mock verification.

# Repeated calls #

The syntax used to specify that repeated calls will occur on a mock object is the following:

  * `atLeastOnce`: the mock is called at least once
  * `times(i)`: the mock is called `i` times
  * `anyTimes`: the mock will be called any number of times