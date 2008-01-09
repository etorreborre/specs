package org.specs.runner

import org.specs._
import org.specs.runner._
import org.specs.util._
import scala.collection.mutable._
import org.specs.io.mock.MockOutput
import org.specs.Sugar._
import org.specs.matcher.MatcherUtils._

object consoleReporterRunner extends ConsoleRunner(consoleReporterSpec)
class consoleReporterTest extends JUnit3(consoleReporterSpec)
object consoleReporterSpec extends Specification {

  "A console reporter" should {
    skip("the whole stuff")
    "report the name of the specification: 'A specification should'" in {
      specWithOneExample(that.isOk) must containMatch("A specification should")
    }
    "report the specification examples: '-have example 1 ok'" in { 
      specWithOneExample(that.isOk) must containMatch("have example 1 ok")
    }
    "display '0 failure' if there is no assertion" in { 
      specWithOneExample(that.isOk) must existMatch("0 failure")
    } 
    "display '1 failure' if one example isKo" in { 
      specWithOneExample(that.isKo) must existMatch("1 failure") 
    } 
    "indicate the line and class where the failure occurred" in { 
      specWithOneExample(that.isKo) must existMatch("(consoleReporterSpec.scala:\\d)") 
    } 
    "display the first failure of an example having several ones" in { 
      specWithOneExample(that.isKo, that.isKo) must existMatch("first failure") 
      specWithOneExample(that.isKo, that.isKo) must notExistMatch("second failure")
    } 
    "display '1 error' if one example throws an exception" in { 
      specWithOneExample(that.throwsAnException) must existMatch("1 error") 
    } 
    "display '1 skipped' if one example is skipped" in { 
      specWithOneExample(that.isSkipped) must existMatch("1 skipped") 
    } 
    "report a pluralized message if there are several examples failing" in { 
      specWithTwoExamples(that.isKo) must existMatch("2 examples")
      specWithTwoExamples(that.isKo) must existMatch("2 failures")
    } 
    "report the number of assertions: '2 assertions'" in { 
      specWithOneExample(that.isOk) must existMatch("1 assertion")
      specWithTwoExamples(that.isKo) must existMatch("2 assertions")
    } 
    "display the failure message next to the corresponding example" in { 
      specWithTwoExamples(that.isKo, that.isOk) verifies(messages =>
            messages.findIndexOf(matches("first failure")) ==
            messages.findIndexOf(matches("example 2.1 ok")) + 1)
    } 
    "report the elapsed time" in { 
      specWithOneExample(that.isOk) mustExistMatch "Finished in"
    }
    "report failures created with the 'fail' method" in {
      specWithOneExample(that.isKoWithTheFailMethod) mustExistMatch "1 failure" 
    }
    "report skipped examples created with the 'skip' method with a small circle" in {
      specWithOneExample(that.isSkipped) mustExistMatch "o " 
    }
    "report skipped examples created with the 'orSkipExample' on a faulty matcher with a small circle" in {
      specWithOneExample(that.isSkippedBecauseOfAFaultyMatcher) mustExistMatch "o " 
    }
    "report the literal description of a sut if it is set"  in {
      new SpecWithLiteralDescription(that.isOk).run mustExistMatch "Some text with embedded assertions"
    }
    "report the reason for a skipped example" in {
      specWithOneExample(that.isSkipped) mustExistMatch "irrelevant" 
    }
    "indicate the line and class where the skipping occurred" in { 
      specWithOneExample(that.isSkipped) must existMatch("(consoleReporterSpec.scala:\\d)") 
    } 

  }

  def specWithOneExample(assertions: (that.Value)*) = new SpecWithOneExample(assertions.toList).run
  def specWithTwoExamples(assertions: (that.Value)*) = new SpecWithTwoExamples(assertions.toList).run
}

abstract class TestSpec extends Specification with ConsoleReporter with MockOutput {
  val success = () => true mustBe true
  val isSkipped = () => skip("irrelevant")
  val isSkippedBecauseOfAFaultyMatcher = () => 1 must be(0).orSkipExample
  val failure1 = () => "ok" mustBe "first failure"
  val failure2 = () => "ok" mustBe "second failure"
  val failMethod = () => fail("failure with the fail method")
  val exception = () => throw new Error("new Error")
  def assertions(behaviours: List[that.Value]) = behaviours map { 
                                    case that.isOk => success
                                    case that.isSkipped => isSkipped
                                    case that.isSkippedBecauseOfAFaultyMatcher => isSkippedBecauseOfAFaultyMatcher
                                    case that.isKo => failure1
                                    case that.isKoTwice => () => {failure1(); failure2()} 
                                    case that.isKoWithTheFailMethod => failMethod 
                                    case that.throwsAnException => exception }
}

class SpecWithOneExample(behaviours: List[(that.Value)]) extends TestSpec {
  def run = {
    "A specification" should {
       "have example 1 ok" in {
        assertions(behaviours) foreach {_.apply}
      }
    }
    reportSpec(this)
    messages
  }   
}

class SpecWithTwoExamples(behaviours: List[(that.Value)]) extends TestSpec {
  def run = {
    "A specification" should {
      "have example 2.1 ok" in { assertions(behaviours).head.apply}
      "have example 2.2 ok" in { assertions(behaviours).last.apply }
    }
    reportSpec(this)
    messages
  }   
}
class SpecWithLiteralDescription(behaviours: List[(that.Value)]) extends TestSpec {
  def run = {
    "The specification" is <p> 
      Some text with {"embedded assertions" in {assertions(behaviours) foreach {_.apply}}}
    </p>
    reportSpec(this)
    messages
  }   
}
object that extends Enumeration {
  val isKo, isOk, isKoTwice, isKoWithTheFailMethod, throwsAnException, isSkipped, isSkippedBecauseOfAFaultyMatcher = Value
}
