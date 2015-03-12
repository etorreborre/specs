

# Introduction #

[jMock2](http://www.jmock.org/) allows to define mocks and expectations on mocks very  easily.

This page shows how to use jMock with specs. Please refer to the jMock site for more instructions on jMock itself. You can also have a look at the [spec for the jMock integration](http://specs.googlecode.com/svn/trunk/src/test/scala/org/specs/mock/jmockSpec.scala) for more examples.

# Libraries #

In order to use jMock2 with specs, you need to add the following dependencies to your project:
[jmock-2.4.0.jar](http://repo1.maven.org/maven2/org/jmock/jmock/2.4.0),
[hamcrest-1.1.jar](http://repo1.maven.org/maven2/org/hamcrest/hamcrest-all/1.1)
and
[cglib-2.1\_3.jar](http://repo1.maven.org/maven2/cglib/cglib/2.1_3),
[asm-1.5.3.jar](http://repo1.maven.org/maven2/asm/asm/1.5.3),
[objenesis-1.1.jar](http://repo1.maven.org/maven2/org/objenesis/objenesis/1.1) if you want to mock classes.

Here is a sample maven snippet you can add to your pom.xml file:
```
    <dependency>
      <groupId>org.jmock</groupId>
      <artifactId>jmock</artifactId>
      <version>2.4.0</version>
    </dependency>
    <!-- Those are only needed if you want to mock classes -->
    <dependency>
      <groupId>cglib</groupId>
      <artifactId>cglib</artifactId>
      <version>2.1_3</version>
    </dependency>
    <dependency>
      <groupId>org.objenesis</groupId>
      <artifactId>objenesis</artifactId>
      <version>1.0</version>
    </dependency>
```

# A simple mock example with specs and jMock #

Using mocks follows a 4 steps process:

  1. extend the JMocker trait and add ClassMocker if you want to mock classes as well as interfaces:
```
import scala.specs.mock._
object mySpec extends Specification with JMocker with ClassMocker {...}
```
**It is especially important that JMocker is mixed-in with your Specification, otherwise the expectations won't be checked**

> 2. create mock objects using the `mock` method
```
object mySpec extends Specification with JMocker with ClassMocker {
  "my system" should {
    "use mocks" in {
      val mockedService = mock[Service]
    }
  }
}
```

**Important note:** the mock declarations should be done either inside your example or in a trait inherited by your specification. Otherwise you will have expectation errors because the expectations are reset before and after each example. One way to do that is to use a `doBefore` clause:
```
  "a statistics component" should {
    doBefore {
      blogger = mock[Blogger]
      stats = new Statistics(blogger)
    }
    "return the number of posts for today" in {...}
  }
```

> 3. add expectations in your specification example
```
object mySpec extends Specification with JMocker with ClassMocker {
  "my system" should {
    "use mocks" in {
      val mockedService = mock[Service]
      expect {
        one(mockedService).executeService() // this will be called one time exactly
      }
      ...
    }
  }
}
```
> 4. use the mock in the rest of your example, expectations will be automatically checked at the end of the example
```
object mySpec extends Specification with JMocker with ClassMocker {
  "my system" should {
    "use mocks" in {
      val mockedService = mock[Service]
      expect {
        one(mockedService).executeService() // this will be called one time exactly
      }
      val whatIWantToTest = new WhatIWantToTest(mockedService)
      whatIWantToTest.executeMethod // must call once mockedService.executeService or the example will fail
    }
  }
}
```

# A complete example #

Here is a complete example of the use of mocks to specify the interactions between a `Button` and a `Light` object:
```
trait ButtonAndLightMock extends ButtonAndLight with JMocker with ClassMocker {
  var mock: Light = _
  var button: Button = _
  def init = {
    mock = mock[Light]
    button = Button(mock)
  }
}
trait ButtonAndLight {
  case class Button(light: Light) {
    var lightOn = false
    def push = {
      if (lightOn) light.off else light.on 
      lightOn = !lightOn
    }
  }
  case class Light {
    var state: LightState = Off
    def on = state = On
    def off = state = Off
    def isOn = state == On
  }
  abstract sealed class LightState(s: String)
  object On extends LightState("on")
  object Off extends LightState("off")
}

object mockExample extends Specification with ButtonAndLightMock {
  "A button and light mock example" should {
    doBefore { init }
    "not fail if the mock receives the expected messages" in {
      expect {
        one(mock).on
        one(mock).off
      }
      button.push
      button.push  // if the button is pressed twice, then the light will go on and off
    }
  }
}
```

# Use JMock without a Specification #

You may want to use JMock without necessarily creating a specification (if you use `ScalaTest` only for example). In that case, you can just import the JMocker object and use its functionalities directly:
```
package org.specs.mock

import org.specs.mock.JMocker._
import org.specs.mock.JMocker.{expect => expecting}
import org.scalatest.Suite
 
/**
 * This sample class shows how to use ScalaTest with JMocker and how to avoid naming conflicts with the <code>expect</code> method
 */
class jMockerWithScalaTestSuite extends Suite {
  def testMockExpectations {
    val list: java.util.List[Object] = mock[java.util.List[Object]]
    expecting { one(list).size }
    list.size
    checkContext
  }
}
```


# Methods expectations #

## Methods counters ##
All the standard jMock method expectations are available with specs. However, a little bit of syntactic sugar has been added:
```
  expect {
    exactly(2).of(mock).on // is equivalent to 
    2.of(mock).on

    atLeast(2).of(mock).on // is equivalent to 
    2.atLeastOf(mock).on

    (2 to 4).of(mock).on // between 2 and 4 calls to list.size
  }
```

## Allowing or ignoring methods ##

Some shortcuts are available to allow or ignore some method calls:
```
  expect {
    allowingMatch("on") // allow any method matching "on"
    allowingMatch(mock, "on") // allow any method matching "on" on the object "mock"
    ignoringMatch("on") // ignore any method matching "on"
    ignoringMatch(mock, "on") // ignore any method matching "on" on the object "mock"
  }
```

## Returned values ##

Scala allows a chain method call expectations with the specification of the returned values with `will`, `willReturn` and `willReturnIterable`:
```
expect { 
  one(List("hey")).take(anyInt) will returnValue(equal(List("hey"))) // `will` accepts a jMock action, like returnValue
  one(List("hey")).take(anyInt) willReturn List("hey") // `willReturn` specifies a returned value
  one(List("hey")).take(anyInt) willReturnIterable("a", "b") // `willReturnIterable` returns an Iterable with specified values
}
```

## Returned values on consecutive calls ##

specs offers the `willReturnEach` method to specify that consecutive calls to a given method will return different values:
```
  // returns "a" the first time the "get" method is called, "b" the second time and "c" the third time
  1.atLeastOf(list).get(anyInt) willReturnEach ("a", "b", "c") 
```

## Nested returned values and mocks ##

There is a frequent situation when interacting with object graphs. You need to mock an object, like a Connection, which is supposed to give you access to a service, that you also want to mock and so on. For example, testing some code accessing the Eclipse platform can be very difficult for that reason.

Using specs you can use blocks to specify nested expectations:
```
  // A workspace gives access to a project and a project to a module
  case class Module(name: String)
  case class Project(module: Module, name: String)
  case class Workspace(project: Project)
  val workspace = mock[Workspace]
  
  expect { 
    one(workspace).project.willReturn[Project] {p: Project => 
        // nested expectations on project
        one(p).name willReturn "hi"
        one(p).module.willReturn[Module] {m: Module => 
          // nested expectation on module
          one(m).name willReturn "module"}
    }
  }

```

or

```
  // a workspace is a list of projects
  case class Project(name: String)
  case class Workspace(projects: List[Project])
  val workspace = mock[Workspace]
  expect { 
    // the workspace will return project mocks with different expectations
    one(workspace).projects willReturnIterable[Project]( 
           {p: Project => one(p).name willReturn "p1" },
           {p: Project => one(p).name willReturn "p2" })
  }

```

## Capturing parameters for returned values ##

Sometimes, you may want a mocked method to return the value of one of the passed parameter. You can do that using `CapturingParameters`:
```
val s = capturingParam[String]
classOf[ClassToMock].expects(one(_).method(s.capture) willReturn s) in {
  _.method("a") must_== "a" // the String parameter of the method will be captured and returned
}
```

Note that if the method has several parameters you need to indicate the index of the captured parameter during the capture:
```
val s = capturingParam[String]
classOf[ClassToMock].expects(one(_).method2(anyString, s.capture(1)) willReturn s) in {
  _.method2("a", "b") must_== "b" // "b" is returned as the second parameter (this is a 0 based index hence the 'capture(1)')
}
```

It is also possible to apply matchers on capturing parameters, like this:
```
val s = capturingParam[String]
classOf[ClassToMock].expects {
  one(_).method(s.must(beMatching("h.*")).capture willReturn s
} in {
  _.method("hello") must_== "hello"
}
```

And if necessary, you can transform the captured value using `map` to return a value of a different type:
```
val s = capturingParam[String]
classOf[ClassToMock].expects(one(_).method3(s.capture) willReturn s.map(_.size)) in {
  _.method3("a") must_== 1 
}
```

## Thrown exceptions ##

You can declare that a method call will throw an exception with `willThrow`:
```
expect { 
  one(list).get(will(beEqual(0))) willThrow new java.lang.Exception("ouch") 
}
```

## Method parameters ##

Shortcuts are provided for the most common types of parameters:
```
  // accepts any value, but can't be mixed with other matchers in a method call
  anyInt, anyLong, anyShort, anyByte, anyDouble, anyFloat, anyChar, anyString
  any(<anything>) 
 
  // checks the class of the parameter
  a[Type]
  
  // returns always true, can be mixed with other matchers in a method call
  aNull[Type]
  aNonNull[Type]
  equal(value)
  same(value)
```

**Known issue**: as of now, jMock parameter matching doesn't seem to work with lazy parameters

**Known issue**: working with repeated parameters is tricky. However the following works:
```
  case class Param(name: String)
  trait ToMock { def method(p: Param*) = () }
  val mocked = mock[ToMock]
  expect { 1.of(mocked).method(any[Param]) }
  mocked.method(Param("hello"))
```

## Matchers adaptation ##

A lot of jMock methods are expecting Hamcrest matchers:
```
  expect { 
    one(list).get(`with`(same(0))) // same(0) == new IsSame(0). Note the `backticks` on "with" which is a Scala reserved keyword
  }
```

Thanks to an implicit conversion from specs matchers to Hamcrest matchers, you can use specs matchers instead, using the `will` method:
```
  expect { 
    one(list).get(will(beEqual(0))) // beEqual(0) is a specs matchers
  }
```

**Note**: the be_== matcher can't be used here as it is expecting a value of type Any. This is why you should use beEqual[T](T.md)(a: T)._

## Sequence expectations ##

You can constrain calls to occur in sequence, with the `then` method:
```
expect { 
  one(list).size then 
  one(list).get(anyInt) then
  one(list).isEmpty
}
```

## State expectations ##

You can model expectations on some abstracts states, using the `when` and `set` methods
```
  val readiness = state("readiness")
  readiness.startsAs("not ready")
  expect { 
    one(list).size set readiness.is("ready") 
    allowing(list).get(anyInt) when readiness.is("ready")
  }
```

## Counting expectations ##

You can say that some expectations should be counted as such in the statistics:
```
  expect { 
    one(list).get(anyInt) will(returnValue("hey")) isExpectation
  }
  list.get(0) must_== "hey"
```

This will report 2 expectations (one for the mock expectation, the other one for the equality).

## One liners ##

If you want to express only one expectation in your example, you can use this short form:
```
  // example 1
  classOf[Channel].expects(one(_).isOpen willReturn false) in { _.isOpen must beFalse}

  // example 2
  classOf[OutputStream].expectsOne(_.flush) in { _.flush }

  // example 3
  classOf[OutputStream].neverExpects(_.close) in { stream: OutputStream) => 
    // use the stream mock object
  }
```

One-liners are also very effective at creating mocks for objects with just one expectation or which will be ignored:
```
  val sender = classOf[MailSender].expectsOne(_.send).mock // returns a MailSender mock with one expectation
  val output = classOf[OutputStream].isIgnored.mock // returns a mock which ignores everything
  publisher(sender, output).publish
```