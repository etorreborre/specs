package org.specs.matcher
import org.specs._
import org.specs.runner._
import org.specs.Sugar._
import org.specs.matcher.ScalacheckParameters._
import scalacheck._
import org.specs.mock._
import org.specs.io._
import org.specs.specification._

object scalacheckMatchersSuite extends JUnit3(scalacheckMatchersUnit)
object scalacheckMatchersUnit extends MatchersSpecification with ScalacheckMock {
  "The ScalacheckParameters object" should {
    "provide a 'display' value which is verbose" in {
       display.verbose mustBe true
    }
    "provide a 'display' value which has default values for Scalacheck parameters" in {
      defaultValues foreach {display(_) mustNotBe null}
    }
    "provide a 'display' case class which can take parameters overriding the default values" in {
      display(minTestsOk->10)(minTestsOk) mustBe 10
      display(minTestsOk->10)(maxDiscarded) mustBe defaultValues(maxDiscarded)
    }
    "provide a 'display' case class which throws an exception in case of null values" in {
      display(null) must throwA(new RuntimeException)
    }
    "provide a 'display' case class which is resilient to a value with a null key" in {
      val s: Symbol = null
      display(s -> 10) must throwA(new RuntimeException)
    }
    "provide a 'set' case class which can take parameters overriding the default values" in {
      set(minTestsOk->10)(minTestsOk) mustBe 10
      set(minTestsOk->10)(maxDiscarded) mustBe defaultValues(maxDiscarded)
    }
    "provide a 'set' case class which is not verbose" in {
      set(minTestsOk->10).verbose mustBe false
    }
  }
  "The checkFunction method" should {
    "call the forAll function of scalacheck to create a property that will be checked for all generated values" in {
      expect { matcher.forAll(any[Gen[Boolean]])(x => Prop.proved) }
      matcher.checkFunction(Gen.value(true))(x => true)(set(minTestsOk->1))
    }
  }
  "The checkScalacheckProperty method" should {
    "call the printf function of Output to print the results if verbose=true" in {
      expect { matcher.printf(any[String], any[String]) }
      matcher.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, true)
    }
    "call the check function of scalacheck to check the property" in {
      expect { matcher.check(Test.defaultParams, Prop.proved, (r, t, d) => ()) }
      matcher.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, false)
    }
    "return a true status if the check function return a succeeded result" in {
      expect { matcher.check(any[Test.Params], any[Prop], (r, t, d) => ()) }
      matcher.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, false)._1 mustBe true
    }
    "return a false status if the check function return a failure" in {
      matcherWithFailure.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, false).success mustBe false
    }
    "return a false status if the check function return a property exception" in {
      matcherWithPropertyException.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, false).success mustBe false
    }
    "return a false status if the check function return an generation exception" in {
      matcherWithGenerationException.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, false).success mustBe false
    }
    "return a false status if the check function return an exhausted status" in {
      matcherWithExhaustedGeneration.checkScalacheckProperty(forAll(Gen.value(true))(x => true))(Test.defaultParams, false).success mustBe false
    }
  }
}
trait ScalacheckMock extends Mocker {
  trait ScalacheckFunctionsMock extends ScalacheckFunctions {
    def result = Test.Stats(Test.Passed, 2, 3)
    override def check(params: Test.Params, prop: Prop, printResult: (Option[Prop.Result], Int, Int) => Unit) = { 
      recordAndReturn(result)
    }
    override def forAll[A,P](g: Gen[A])(f: A => Prop): Prop = recordAndReturn(Prop.proved)
  } 
  trait ConsoleOutputMock extends Output {
    override def println(s: Any) = record
    override def printf(format: String, args: Any*) = record
  } 
  val matcher = new ScalacheckMatchers with ConsoleOutputMock with ScalacheckFunctionsMock  
  val matcherWithFailure = new ScalacheckMatchers with ConsoleOutputMock with ScalacheckFunctionsMock {
    override def result = Test.Stats(Test.Failed(List(("", 1))), 1, 2)
  }  
  val matcherWithPropertyException = new ScalacheckMatchers with ConsoleOutputMock with ScalacheckFunctionsMock {
    override def result = Test.Stats(Test.PropException(List(("", 2)), FailureException("")), 1, 2)
  }  
  val matcherWithGenerationException = new ScalacheckMatchers with ConsoleOutputMock with ScalacheckFunctionsMock {
    override def result = Test.Stats(Test.GenException(new Exception), 1, 2)
  }  
  val matcherWithExhaustedGeneration = new ScalacheckMatchers with ConsoleOutputMock with ScalacheckFunctionsMock {
    override def result = Test.Stats(Test.Exhausted, 1, 2)
  }  
}