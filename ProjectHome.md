# Welcome to specs! #

# **specs is now deprecated, please use [specs2](http://specs2.org) instead!** #

|06/03/2011 | **specs2** 1.0 | [specs2](http://specs2.org) is released! - Maintenance will go on with specs but new features are only provided with specs2: acceptance testing, concurrent execution,... So please, **start any new project with specs2!**|
|:----------|:---------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
|09/4/2011 | ![http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_mozilla.png/20px-Nuvola_apps_mozilla.png](http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_mozilla.png/20px-Nuvola_apps_mozilla.png) **specs** 1.6.9 | Maintenance release for Scala 2.9.1([enhancements and fixes](http://code.google.com/p/specs/wiki/ChangeLog)) |
|11/10/2010 | **specs** 1.6.6 | Scala 2.8.1 release! ([enhancements and fixes](http://code.google.com/p/specs/wiki/ChangeLog)) |
|02/04/2010 | **specs** 1.6.2 | **NEW**: Added a test-interface runner to execute specifications with [sbt](http://code.google.com/p/simple-build-tool/). Added [Eventually matchers](http://code.google.com/p/specs/wiki/MatchersGuide#Eventually_matchers) to retry matchers evaluation (Thanks to Robey Pointer). [Other improvements and fixes](http://code.google.com/p/specs/wiki/ChangeLog)|
|11/05/2009 | **specs** 1.6.1 | **NEW**: Added ["around" actions](http://code.google.com/p/specs/wiki/DeclareSpecifications#Execute_the_Example_expectations_inside_a_specific_context) that can be executed around the expectations of an example. Added [SpecContexts](http://code.google.com/p/specs/wiki/DeclareSpecifications#Specification_context). Added a [plan option](http://code.google.com/p/specs/wiki/RunningSpecs#Specification_plan) to display the plan of a specification without executing the examples. [Other improvements and fixes](http://code.google.com/p/specs/wiki/ChangeLog)|
|09/08/2009 |  **specs** 1.6.0 | **NEW**: new [execution model](http://code.google.com/p/specs/wiki/DeclareSpecifications?DeclareSpecifications#Execution_model) with automated clean-up of local variables and first-class subexamples. Added an [EasyMock trait](UsingEasyMock.md).  [Other improvements and fixes](http://code.google.com/p/specs/wiki/ChangeLog)|
|05/07/2009 | **specs** 1.5.0 | **NEW**: Easier syntax with [be/have + matcher](http://code.google.com/p/specs/wiki/MatchersGuide#Be/Have_matchers). [Alpha version of literate specifications](http://code.google.com/p/specs/wiki/LiterateSpecifications) and [Forms](http://code.google.com/p/specs/wiki/Forms). [Other improvements and fixes](http://code.google.com/p/specs/wiki/ChangeLog): run options, configuration, pending examples. |

**specs** is a [Behaviour-Driven-Design](http://behaviour-driven.org/) framework which provides:

  * a simple and typed language to create specifications ([your first specification in 5 minutes](QuickStart.md))
```
class helloWorld extends Specification {
  "'hello world' has 11 characters" in {
     "hello world".size must be equalTo(11)
  }
  "'hello world' matches 'h.* w.*'" in {
     "hello world" must be matching("h.* w.*")
  }
}
```
  * [lots of matchers](http://code.google.com/p/specs/wiki/MatchersGuide) to specify code properties:
```
 "myString" must be matching("Str.*")

 // or to specify xml pieces using XPath-like operators: 
 <a><b><c><d></d></c></b></a> must \\("c").\("d")
```
  * an integration with [JUnit](http://farm2.static.flickr.com/1098/1433239972_5f3a83e40c.jpg?v=0) to so that you can run and report your tests using your existing infrastructure (you can also run the tests in the console or generate an xml file with the results)
  * an easy way to specify combinatorial properties through an integration with [Scalacheck](http://code.google.com/p/scalacheck/):
```
// generates 500 different mail addresses
mailAddresses must pass { address =>
  address must beMatching(companyPattern)
}
```
  * an [integration](http://code.google.com/p/specs/wiki/UsingMockito) with [Mockito](http://code.google.com/p/mockito) ([JMock](http://code.google.com/p/specs/wiki/UsingJMock) and [EasyMock](http://code.google.com/p/specs/wiki/UsingEasyMock) are also available):
```
class SendSpec extends Specification with Mockito {
  "A Send service" should {
    "publish data" in {
      val mock = mock[Mailer]
      // use the mock
      val sendService = new SendService(mock)
      sendService.publishData

      // check that the mock was called
      there was one(mock).send(any[Mail])
    }
  }
}
```
  * lots of possibilities to [structure and compose specifications](http://code.google.com/p/specs/wiki/DeclareSpecifications)
```
class compositeSpec extends Specification {
  "A composite spec" isSpecifiedBy (okSpec, koSpec)
}
```
  * the possibility to reuse examples across specs (see the [stackSpec](http://specs.googlecode.com/svn/branches/SPECS-2.8.0/src/test/scala/org/specs/samples/stackSpec.scala) example):
```
"A full stack"->-(fullStack) should {
  behave like "A non-empty stack below full capacity"
  "throw an exception when sent #push" in {
    stack.push(11) must throwAn[Error]
  }
}
```
  * [data tables](http://code.google.com/p/specs/wiki/AdvancedSpecifications) to group several data examples at once:
```
import org.specs.util.DataTables

class addOperationSpec extends Specification withDataTables {
  "provide an add operation" in {
     "a" | "b" | "result" |>
      1  !  2  !   3      |  
      5  !  2  !   7      |  
      3  !  0  !   3      | { (a, b, c) => { a + b must_== c } }
  }
}
```
  * a [FIT-like](http://fit.c2.com/) library for [business specifications](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.samples.formSampleSpec.html): [Forms](http://code.google.com/p/specs/wiki/Forms)
# The first time #

You can download the current library distribution ([or get it with Maven](http://code.google.com/p/specs/wiki/UserGuide#Maven_dependency)) and execute:

java -cp [specs-1.6.7.jar;specs-1.6.7-tests.jar](http://scala-tools.org/repo-releases/org/scala-tools/testing/specs_2.8.1/1.6.7);[scalacheck-1.8.jar](http://scala-tools.org/repo-releases/org/scala-tools/testing/scalacheck_2.8.1/1.8);[scala-library-2.8.1.jar](http://scala-tools.org/repo-releases/org/scala-lang/scala-library/2.8.1);
[junit-4.7.jar](http://repo1.maven.org/maven2/junit/junit/4.7);
[scalatest-1.2.jar](http://scala-tools.org/repo-releases/org/scalatest/scalatest/1.2);
[cglib-2.1\_3.jar](http://repo1.maven.org/maven2/cglib/cglib/2.1_3);[asm-1.5.3.jar](http://repo1.maven.org/maven2/asm/asm/1.5.3);[objenesis-1.1.jar](http://repo1.maven.org/maven2/org/objenesis/objenesis/1.1);[hamcrest-all-1.1.jar](http://repo1.maven.org/maven2/org/hamcrest/hamcrest-all/1.1);[jmock-2.5.1.jar](http://repo1.maven.org/maven2/org/jmock/jmock/2.5.1);[jmock-legacy-2.5.1.jar](http://repo1.maven.org/maven2/org/jmock/jmock-legacy/2.5.1);[mockito-all-1.8.5.jar](http://repo1.maven.org/maven2/org/mockito/mockito-all/1.8.5);[easymock-2.5.1.jar](http://repo1.maven.org/maven2/org/easymock/easymock/2.5.1);[easymockclassextension-2.4.jar](http://repo1.maven.org/maven2/org/easymock/easymockclassextension/2.4);[wikitext-0.9.4.I20090220-1600-e3x.jar](http://http://scala-tools.org/repo-releases/org/eclipse/mylyn/wikitext/wikitext/0.9.4.I20090220-1600-e3x);[wikitext.textile-0.9.4.I20090220-1600-e3x.jar](http://scala-tools.org/repo-releases/org/eclipse/mylyn/wikitext/wikitext.textile/0.9.4.I20090220-1600-e3x); -Xmx512m org.specs.allRunner

Then you should see the whole specification for the **specs** project, ending with:
```
Total for specification "The specs and unit tests for the specs project":
Finished in 36 seconds, 706 ms
1262 examples, 7167 expectations, 0 failure, 0 error
```

_tested with Scala 2.8.1.final_

**Happy specs!**

&lt;wiki:gadget url="http://www.ohloh.net/p/10595/widgets/project\_users\_logo.xml" height="43"  border="0" /&gt;