

# Mocks presentation #

**specs** offers a lightweight framework to create mocks and stubs (see [this](http://www.mockobjects.com/) reference and [that one](http://martinfowler.com/articles/mocksArentStubs.html) for a discussion on mocks and stubs).

Using mocks follows a 4 steps process:

  1. extend the `Mocker` trait
```
  object mySpec extends Specification with scala.specs.mock.Mocker {...}
```
  1. override the methods in the class or trait you want to mock and implement them with the `record` method
```
  class MockedService extends Service {
    override def executeService = record
  }
```
  1. add expectations in your specification example
```
  object mySpec extends Specification with Mocker {
    "my system" should {
      "use mocks" in {
         val mock = new MockedService
         expect {
           mock.executeService
         }
         ...
      }
    }
  }
```
  1. use the mock in the rest of your example, expectations will be automatically checked at the end of the example

### A complete example ###

Here is a complete example of the use of mocks to specify the interactions between a `Button` and a `Light`:
```
trait ButtonAndLightMock extends ButtonAndLight with Mocker {
  val mock = new Light { 
    override def on = record
    override def off = record
  }
  val button = Button(mock)
}
trait ButtonAndLight {
  case class Button(light: Light) {
    var lightOn = false
    def push = {
      if (lightOn) light.off else light.on 
      lightOn = !lightOn
    }
  }
  case class Light() {
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
    "not fail if the mock receives the expected messages" in {
      expect {mock.on; mock.off}
      button.push
      button.push  // if the button is pressed twice, then the light will go on and off
    }
  }
}
```

# Mock protocols #

By default, the mock expectations order is not important and any excess message will be ignored. This default decision is motivated by the need to make the specification more robust in case of a minor implementation changes (such as the order of calls).

However you can specify that the calls must happen in sequence:
```
  expect(inSequence) { mock.on; mock.off }
```

You can also specify that unexpected calls must be reported as failures:
```
  expect(exclusively) { mock.on; mock.off }
```

### Repeated calls ###

You may want to be even more precise when specifying mock expectations by using the following `ProtocolTypes`:

  * anyOf: any of the expected calls
```
 expect(anyOf) {mock.on; mock.off}
```

  * oneOf: the expected calls must be received 1 time only
```
 expect(oneOf) {mock.on; mock.off}
 3.times {i => button.push} // will fail
```

  * n.Of: the expected calls must be received n times only
```
 expect(5.Of) {mock.on}
```

  * atLeastOneOf: the expected calls must be received at least once
  * n.atLeastOf: the expected calls must be received at least n times

  * atMostOneOf: the expected calls must be received at most once
  * n.atMostOf: the expected calls must be received at most n times

### Nested expectations ###

You can also nest expectations to build more sophisticated expectations. In that case, you can even avoid the `expect` function:
```
 expect(exclusively) {
   1.of {mock.on; mock.off; mock.on} 
   1.of {mock.off} 
 }
```

# Return values and expected parameter values #

### How to stub return values ###

You may want your mock to return specific values sometimes (using it effectively as a stub). In that case, you can use the `recordAndReturn` method to return a specific value:
```
trait MockedRandomGenerator extends RandomGenerator with Mocker {
  // will always return 0.5
  override def getRandomNumber: Double = recordAndReturn(0.5)
}
```

If the return value depends on the mocked method parameters, you can use a function to compute the returned value, using the method parameters (see [here](http://specs.googlecode.com/svn/trunk/src/test/scala/scala/specs/mock/mockParameters.scala) for an example)

### How to define expected parameters ###

You may want to check that the parameters passed to the mocked method verify some specific constraints. In order to do that, you need to build your mock object accordingly. Here's n example:
```
  // define a mock with a function which will check the passed parameters
  def buildMock(f: (Int, Movie) => Boolean) = {
    new MovieRater {
      override def okForAge(a: Int, m: Movie) =  recordAndReturn(f(a, m))
    }
  }
  // create a new mock object checking that the age is > 0
  val mock = buildMock((a: Int, m: Movie) => {a must beGreaterThan(0); true})
```

### How to avoid to create complex parameters in expectations ###

Some methods can only be called with complex objects as parameters. Thus, it can be tedious when defining expected calls because such objects need to be build first. In order to avoid this, you can use the `any` function:
```
 expect {
   mock.methodWithComplexParameter(any[ComplexType])
 }
```