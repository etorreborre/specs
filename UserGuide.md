### Introduction ###
**specs** is designed as an alternative for JUnit when specifying and testing Java or Scala projects. Yet, it is able to reuse the existing JUnit infrastructure for execution and reporting since the specifications can be executed as JUnit4 test suites.

**specs** also integrates an advanced testing and specification library, the [ScalaCheck](http://code.google.com/p/scalacheck/) project in order to be able to generate a numerous cases testing a given property.

**specs** takes its inspiration from existing frameworks:

  * [rspec](http://rspec.rubyforge.org/) for trying to have a literate structure to specifications and assertions and also for the possibility to share examples between related specifications (as in the Stack example).

  * [jmock](http://www.jmock.org) for having reusable matchers and expressive assertions.

### User Guide ###
This User Guide is divided in 7 parts:

  * [5 minutes to your first specification](QuickStart.md)
  * [Declare your specifications](DeclareSpecifications.md)
  * [The full guide to specs matchers](MatchersGuide.md)
  * [How to run your specifications](RunningSpecs.md)
  * [How use mock objects with jMock](UsingJMock.md)
  * [How use mock objects with Mockito](UsingMockito.md)
  * [Use DataTables and syntactic sugar in your specifications](AdvancedSpecifications.md)
> ![http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png](http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png) **Alpha version!**
  * [How write literate specifications](LiterateSpecifications.md)

For more examples about what you can do with the library, please:

  * download the API
  * take a look at the specification for the library itself:
    * [all specifications](http://specs.googlecode.com/svn/trunk/src/test/scala/org/specs/specs.scala)
    * [matchers applicable to any object](http://specs.googlecode.com/svn/trunk/src/test/scala/org/specs/matcher/objectMatchersSpec.scala)

### Maven dependency ###

If you're a Maven user, you need to add the following dependency to your pom file:
```
    <dependency>
      <groupId>org.scala-tools.testing</groupId>
      <artifactId>specs</artifactId>
      <version>1.4.4</version> <!-- specify the version here -->
    </dependency>
```

_Note_: specs versions older than 1.4.4 are using the org.specs groupId



