

# Dependencies #

The following libraries are necessary to run specifications.

![http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png](http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png) **Please check carefully the version numbers!**

  * [specs-1.6.7.jar](http://scala-tools.org/repo-releases/org/scala-tools/testing/specs_2.8.1/1.6.7)
  * [scala-library-2.8.1.jar](http://scala-tools.org/repo-releases/org/scala-lang/scala-library/2.8.1)
  * [junit-4.7.jar](http://repo1.maven.org/maven2/junit/junit/4.7)

The junit library also allows you to run the specifications as JUnit suites by mixing-in the `org.specs.runner.JUnit` trait. The junit library is a requirement for specs because the `@RunWith` annotations on the JUnit is unfortunately not inherited by the `Specification` class.


If you use `ScalaCheck` you must add

  * [scalacheck-1.8.jar](http://scala-tools.org/repo-releases/org/scala-tools/testing/scalacheck_2.8.1/1.8)

If you use mock objects with jMock, add

  * [jmock-2.5.1.jar](http://repo1.maven.org/maven2/org/jmock/jmock/2.5.1)
  * [jmock-legacy-2.5.1.jar](http://repo1.maven.org/maven2/org/jmock/jmock-legacy/2.5.1)
  * [hamcrest-all-1.1.jar](http://repo1.maven.org/maven2/org/hamcrest/hamcrest-all/1.1)

And if you mock classes or traits with attributes you will need

  * [cglib-2.1\_3.jar](http://repo1.maven.org/maven2/cglib/cglib/2.1_3)
  * [asm-1.5.3.jar](http://repo1.maven.org/maven2/asm/asm/1.5.3)
  * [objenesis-1.1.jar](http://repo1.maven.org/maven2/org/objenesis/objenesis/1.1)

If you use mock objects with Mockito, add

  * [mockito-all-1.8.5.jar](http://repo1.maven.org/maven2/org/mockito/mockito-all/1.8.5)

If you use mock objects with `EasyMock`, add

  * [easymock-2.5.1.jar](http://repo1.maven.org/maven2/org/easymock/easymock/2.5.1)
  * [easymockclassextension-2.4.jar](http://repo1.maven.org/maven2/org/easymock/easymockclassextension/2.4)

If you want to run the specifications as `ScalaTest` suites, use

  * [scalatest-1.2.jar](http://scala-tools.org/repo-releases/org/scalatest/scalatest/1.2)

If you want to use Textile markup in Literate Specifications, you need to add

  * [wikitext-0.9.4.I20090220-1600-e3x.jar](http://scala-tools.org/repo-releases/org/eclipse/mylyn/wikitext/wikitext/0.9.4.I20090220-1600-e3x/)
  * [wikitext.textile-0.9.4.I20090220-1600-e3x.jar](http://scala-tools.org/repo-releases/org/eclipse/mylyn/wikitext/wikitext.textile/0.9.4.I20090220-1600-e3x)

# Run your specification in the Console #

You can run your specifications in the console just by invoking scala on the Specification class:
```
package org.hw
import org.specs._

object helloWorld extends Specification { ... }
```

`java -cp ... org.hw.helloWorld`

## Getting some help ##

You can display a help message with all available options by passing the `-h` or `--help` options on the command line.

## Removing stacktraces from the report ##

If you have several exceptions being thrown and if stacktraces are obscuring the Console output you can turn them off by passing the `-ns` or `--nostacktrace` option.

## Showing only failures and errors ##

You can also choose to display only the failed and error examples by using either the -xonly or the --failedonly option ("x" like the symbol showing a failure or an error in the console).

## Don't show statistics ##

For big specifications, if you want to remove the display of statistics, you can use the -nostats or --nostatistics flag to prevent statistics to be displayed.

## Filter systems and examples ##

The following flags can be used to run only some systems or some examples:

-sus or --system regexp runs only the systems which description matches the regular expression

-ex or --example regexp runs only the examples which description matches the regular expression

## Report with colors ##

If your console supports it, you can display success, failures and skipped examples in color with the -c or --color flag.

## Run a Specification class ##

If a Specification is declared as an `object` you can run it directly on the command line. However, if you declare the Specification as a class (to make it a JUnit test for example), you will have to use the `run` class to execute the Specification on the command line:
```
  java -cp <classpath> run org.specs.samples.mySpec
```

If the class can't be instantiated a stacktrace will be printed.

## Run several Specification classes ##

You can also run many specifications at once with the -k flag (or --classes):
```
  java -cp <classpath> run -k org.specs.samples.mySpec,org.specs.samples.mySpec2
```

And the package names can be factored with -p (or --packages):
```
  java -cp <classpath> run -p org.specs.samples -k mySpec,mySpec2
```

Additionally you can display instantiations issues with the -v flag.

## Display the help ##

The help flag: -h or --help can be used to display the available flags and their description:
```
usage java classpath package.mySpecification [-h|--help]
                                             [-ns|--nostacktrace]
                                             [-finalstats|--finalstatistics]
                                             [-nostats|--nostatistics]
                                             [-xonly | -failedonly]
                                             [[-acc | --accept] tag1,tag2,...] [[-rej | --reject] tag1,tag2,...]
                                             [-sus | --system]
                                             [-ex | --example]
                                             [-plan | --planonly]
                                             [-c | --color]

-h, --help                     print this message and doesn't execute the specification
-config, --configuration        class name of an object extending the org.specs.util.Configuration trait
-ns, --nostacktrace            remove the stacktraces from the reporting
-nostats, --nostatistics       remove the statistics from the reporting
-finalstats, --finalstatistics print the final statistics only
-xonly, --failedonly           report only failures and errors
-acc, --accept tags            accept only the specified tags (comma-separated names)
-rej, --reject tags            reject the specified tags (comma-separated names)
-sus, --system                 only the systems under specifications matching this regular expression will be executed
-ex, --example                 only the examples matching this regular expression will be executed
-plan, --planOnly              only display the sus and first level descriptions without executing the examples
-c, --color                    report with colors

```

Note that the specification is not executed in that case.

## Override specs default behavior ##

You can override specs behavior by providing a configuration (see below). This configuration file allows you to set default reporting options for:

  * `ns, xonly, finalstats, c`

But it also allows to control the behavior of specs for:

  * Examples without expectations. The default is to throw a `SkippedException` but you can override it
  * (_more to come later_)

This how you declare a **specs** configuration:

  * you can provide an object named `configuration`, extending the `org.specs.util.Configuration` trait, and overriding the methods of that trait:

```

  /** this value controls if the errors stacktrace should be printed. */
  def stacktrace = true
  /** this value controls if ok examples should be printed. */
  def failedAndErrorsOnly = false
  /** this value controls if the statistics should be printed. */
  def statistics = true
  /** this value controls if the final statistics should be printed. */
  def finalStatisticsOnly = false
  /** this value controls if the ANSI color sequences should be used to colorize output */
  def colorize = false
  /** this value controls if examples without expectations should be marked as PENDING examples */
  def examplesWithoutExpectationsMustBePending = true
```

  * you can provide a class named `mypackage.configuration`, extending the `org.specs.util.Configuration` trait and pass this full name to the -config option

  * you can provide a properties file named `configuration.properties`, having as keys some of the method names of the Configuration trait and as values `true` or `false` (or actually anything starting with `y`/`Y` or `n`/`N`).

  * you can provide a properties file as above but with a specific path and specify its path to the `-config` option.


## Find specifications in a directory path ##

You can use the `SpecsFinder` class to find specifications in a given path:
```
object displaySpecifications extends SpecsFinder with Application {
  // print all specifications contained in subdirectories of the project directory
  // whose names are matching "all.*"
  specificationNames("project/**/*.scala", "all.*") foreach { println(_) }
}
```

To be more specific, the name of the specification will be retrieved if the scala file contains:
```
\\s*object\\s*(" + pattern + ")\\s*extends\\s*.*Spec.*\\s*\\{"
```

## Execute specifications in a directory path ##

You simply use the `SpecsFileRunner`, with the parameters required for the `SpecsFinder`:
```
import org.specs.runner.SpecsFileRunner

object allSpecsRunner extends SpecsFileRunner("project/**/*.scala", "all.*")
```

The `SpecsFileRunner` will find possible specification names with the `SpecsFinder` and will try to instantiate them, keeping them ottp://code.google.com
Content-Tn instance of `Specification`.

## How to redirect the results on a different output ##

If you want to redirect the result of the execution to a file for example, you have to create a new trait extending the `Output` trait:
```
trait FileOutput extends Output {
  def println(m: Any) = {...}
  def printf(format: String, args: Any*) = {...}
  def flush() = {...}
}
```

And then you "mix" it to your specification:
```
object mySpec extends Specification with FileOutput { ... }
```

On the command line, you can then invoke: `java -cp ... run mySpec`

# Run your specification with JUnit4 #

To execute your specification with JUnit, you can extend the `SpecificationWithJUnit` class:

```
class mySpecTest extends SpecificationWithJUnit { ... }
```

**Note 1**: we need to declare the specification as a class and not as an object, otherwise Ant and Maven test tasks won't be able to instantiate the class properly. Moreover, following the convention of having 'Test' at the end of the name should be make it being picked up by default. See [below](http://code.google.com/p/specs/wiki/RunningSpecs#Run_your_specifications_with_JUnit4_and_Maven) how to configure Maven to pick up another naming convention.

**Note 2**: on the command line, you can still run your suite by executing: `java -cp ... run mySpecTest`

**Note 2**: before version 1.5.1, you could directly mix in a JUnit trait to your Specification:
```
  import org.specs.runner.JUnit
  class mySpec extends Specification with JUnit { ... }
```

However, starting from version 1.5.1, this feature has been replaced with a direct inheritance of the SpecificationWithJUnit class:
```
  class mySpec extends SpecificationWithJUnit { ... }
```

This was necessary in order to remove a dependency between the Specification class and the junit libraries when JUnit is not required.

## Alternate method of declaring a specification runnable with JUnit ##

To execute your specification with JUnit, you can also use the `JUnit4` class:
```
  import org.specs.runner.JUnit4

  class mySpecTest extends JUnit4(mySpec)
  object mySpec extends Specification { ... }
```

The name of the test class will be <package name>.mySpecTest

# Run your specification with JUnit4 in Eclipse #

In order to be able to select your JUnit4 classes, you need to add the output directory of your project to your build path:
  1. Select your project
  1. Go to "Build Path / Configure Build Path"
  1. In the "Libraries" tab, "Add Class Folder"
  1. In the "Class Folder Selection" dialog, do "Create New Folder"
  1. In the "New Folder" dialog, select "Advanced" and "Link to folder in the file system"
  1. Select the output folder of your project
  1. In the "Order and Export" tab, make sure that the newly created alias is placed before the main/scala/test directory
  1. Refresh your project (F5 on the project's folder)

You should now be able to select the JUnit4 classes of your Scala project.

_Note_: for undetermined reasons the trick above might not even work! In that case your best chance is to put explicitly the following annotation on your specification class:
```
  import org.junit.runner.RunWith
  import org.specs.runner.JUnitSuiteRunner

  @RunWith(classOf[JUnitSuiteRunner])
  class MySpec extends SpecificationWithJUnit

```
# Run your specifications with JUnit4 and Maven #

By default, you need to respect the surefire plugin naming convention:
```
// all JUnit4 tests must end with "Test"
// it must be a class, not an object, otherwise the class name would be mySpecTest$
class mySpecTest extends SpecificationWithJUnit
```

Then you run the usual `mvn test` to compile main code, test code and execute the tests.

If you want another naming convention to be used with Maven, you can add this section to your pom file:
```
      <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-surefire-plugin</artifactId>
        <version>2.4.3</version>
        <configuration>
          <useSystemClassLoader>false</useSystemClassLoader>
          <argLine>-Xmx512m</argLine>
          <includes>
            <include>**/*Unit.java</include>
            <include>**/*Spec.java</include>
          </includes>
        </configuration>
      </plugin>
      <plugin>
```

This section above will execute all specification classes whose name end with Unit or Spec (note the `.java` at the end of the include name).

# Run your specification with JUnit4 and Ant #

You can use the following Ant build file as a starter to compile and execute your specs as JUnit tests:
```
<project name="MyFirstSpecsProject" default="test" basedir=".">
  <description>sample build file</description>
  <!-- 1. Define common properties. Change the paths according to your installation -->
  <property name="src.dir" value="src/main/scala" />
  <property name="src.test.dir" value="src/test/scala" />
  <property name="build.dir" value="target/classes" />
  <property name="lib.dir" value="lib" />
  <property name="repository.home" value="c:/local_repository" />
  <property name="scala-compiler.jar"
  value="${repository.home}/org/scala-lang/scala-compiler/2.7.7/scala-compiler-2.7.7.jar" />
  <property name="scala-library.jar"
  value="${repository.home}/org/scala-lang/scala-library/2.7.7/scala-library-2.7.7.jar" />
  <!-- 2. Define Scala CLASSPATH. -->
  <path id="scala.classpath">
    <pathelement location="${scala-compiler.jar}" />
    <pathelement location="${scala-library.jar}" />
  </path>
  <!-- 3. Define project CLASSPATH. -->
  <path id="project.classpath">
    <path refid="scala.classpath" />
    <pathelement location="${build.dir}" />
    <pathelement location="${repository.home}/junit/junit/4.4/junit-4.4.jar" />
    <pathelement location="${repository.home}/org/scala-tools/testing/specs/1.6.2.1/specs-1.6.2.1.jar" />
    <pathelement location="http://scala-tools.org/repo-snapshots/org/scala-tools/testing/scalacheck/1.6/scalacheck-1.6.jar" />
  </path>
  <!-- 4. Define scala compiler command. -->
  <taskdef resource="scala/tools/ant/antlib.xml">
    <classpath refid="scala.classpath" />
  </taskdef>
  <!-- 5. Compiles sources by using "scalac" command. -->
  <target name="compile">
    <mkdir dir="${build.dir}" />
    <scalac srcdir="${src.dir}" destdir="${build.dir}">
      <include name="**/*.scala" />
    </scalac>
  </target>
  <target name="test-compile">
    <mkdir dir="${build.dir}" />
    <scalac srcdir="${src.test.dir}" destdir="${build.dir}" classpathref="project.classpath" force="changed">
      <include name="**/*.scala" />
    </scalac>
  </target>

  <!-- 6. Execute the specs as junit tests. -->
  <target name="test" description="execute the tests">
    <junit haltonfailure="true" showoutput="true">
      <classpath refid="project.classpath" />
      <formatter type="brief" usefile="false" />
      <batchtest fork="yes">
        <fileset dir="${build.dir}">
          <include name="**/*Test.class" />
          <exclude name="**/All*Test.class" />
        </fileset>
      </batchtest>
    </junit>
  </target>
  <taskdef name="junit" classname="org.apache.tools.ant.taskdefs.optional.junit.JUnitTask" />
</project>
```
(the initial compile script comes from [this blog post](http://scriptlandia.blogspot.com/2007/04/how-to-compile-and-run-scala-program.html))

# Run your specifications with sbt #

[sbt](http://code.google.com/p/simple-build-tool) is able to run any specification class or object inheriting from the Specification class. One thing you may want to specify in your sbt project though is the name of the class to execute:
```
  // this restrict the executed classes names to end with either "Spec" or "Unit"
  override def includeTest(s: String) = { s.endsWith("Spec") || s.endsWith("Unit") }
```

## Pass command line arguments ##

It is possible to pass command line arguments to some of the sbt commands like `test-only` and `test-quick`
```

// -ns: don't print the stacktrace
// -acc tag1 accept examples tagged with tag1
test-only mySpec -- -ns -acc tag1
```

# Run your specifications and get the results as an XML file #

You can use the `XmlRunner` class to export the results of your specifications as an xml file:
```
object specResults extends XmlRunner(extendedThrowableUnit)
```

This will create a file named extendedThrowableUnit.xml in the current directory:
```
<spec errors="0" description="extendedThrowableUnit" failures="0" assertions="1" name="extendedThrowableUnit">
  <sut errors="0" description="an extended Throwable" failures="0" assertions="1">
    <example errors="0" description="provide a location method extracting the name of the file and the line from an exception" failures="0" assertions="1"></example>
  </sut>
</spec>
```

and display:
```
Specification "extendedThrowableUnit"
  an extended Throwable should
  + provide a location method extracting the name of the file and the line from an exception

Total for specification "extendedThrowableUnit":
Finished in 0 second, 16 ms
1 example, 1 assertion, 0 failure, 0 error
```

It is also possible to specify the output directory with:
```
object specResults extends XmlRunner(extendedThrowableUnit, "./target/reports/specs")
```

# Run your specification with Scala Test #

Declare a runner extending the `ScalaTestSuite` runner:

```
import org.specs.runner._
class mySpecSuite extends ScalaTestSuite(mySpec)
```

Then use the ScalaTest gui runner: `java -cp ... org.scalatest.Runner -g -s mySpecSuite`

# Run your specification with Scala Test in `IDEA IntelliJ` #

Just add the `ScalaTest` trait to your specification (declared as a class, see below) and you're done!

# Run your specification with either the Console, JUnit or `ScalaTest` #

The following specification:
```
package org.specs.samples
import org.specs._
import org.specs.runner._

class helloWorldTest extends Specification with JUnit with ScalaTest { ... }
```

  * can be run in the Console with `java -cp <classpath> run org.specs.samples.helloWorldTest`. This is the default behavior provided by the Specification class. Note that the main class is the `run` class which takes as first argument a string corresponding to the Specification class to be executed. The `run` class instantiates the Specification class and invokes its main method to execute the it (the one which would be called if the specification was declared as an object instead of a class).

  * can be run using Eclipse or Maven because of the added JUnit trait. Note that the Specification name has to end with `Test` by default to be included in the test suite by the Maven Surefire plugin.

  * can be run using `ScalaTest` with `java -cp ... org.scalatest.Runner -g -s helloWorldTest`. This functionality is provided through the additional `ScalaTest` trait.

# Run your specification with Team City #

You can use the TeamCityRunner to get an output parsable by [TeamCity4](http://www.jetbrains.net/confluence/display/TCD4/Build+Script+Interaction+with+TeamCity):

```
import org.specs._
import org.specs.runner._

object mySpec extends Specification { ... }
object tcRunner extends TeamCityRunner(mySpec)
```

(see some screenshots: [here](http://screencast.com/t/OpSg3PkcqCZ) and [there](http://screencast.com/t/6it9oqcXJ))

# Declare several spec runners at once #

You may want to execute a specification with several runners. In that case you can declare:

```
import org.specs.runner._
class mySpecRunner extends Runner(mySpec) with JUnit with ScalaTest with Console

// or alternately if you also require an xml output
class mySpecRunner extends Runner(mySpec) with JUnit with ScalaTest with Xml
```

The execution of each runner can be done via:
```
JUnit4 -> java -cp ... org.junit.runner.JUnitCore mySpecRunner
ScalaTest -> java -cp ... org.scalatest.Runner -g -s mySpecRunner
XmlRunner -> scala -cp ... -e "(new mySpecRunner).reportSpecs"
ConsoleRunner -> scala -cp ... -e "(new mySpecRunner).reportSpecs"
```

# Skip examples #

You can skip several examples by using the skip method:
```
object mySkippedSpecification extends Specification {
  "These examples" should {
    skip("those examples don't pass yet")
    "be skipped" in {...}
    "be skipped2" in {...}
  }
}
```
You can also skip an individual example if a matcher is not satisfied:
```
object mySpecification extends Specification {
  "my web framework" should {
    "work with DB2" in {
      // skip the example if the DB2 connection is not available locally
      DB2Connection.start must not(throwA(ConnectionException(""))).orSkipExample // alias orSkip
      // use DB2Connection
    }
    ...
  }
}
```

Skipped examples will appear in the Console runner with a small 'o'
```
my web framework should
  o work with DB2
    skipped because DB2 cannot connect was thrown
```

Otherwise, the skipped examples will be reported in the JUnit reports if using a JUnit4 runner to execute the specs.

# Include or exclude examples #


Most of the time examples are naturally classified and grouped according to the Specification they belong to. However, sometimes you may want to group examples differently in order to execute them separately for instance. One very common use of this is when you want to run one example only inside a Specification containing a lot of other examples. Instead of commenting out the other examples you can:

  * tag the example you want to run
```
  "this example is tagged" in {
    // assertions
  } tag("only this")
```

  * accept only the tag you defined on the Specification
```
  // other examples will be marked as skipped
  mySpec.accept("only this")
```

It is possible also to reject tags in order to exclude some examples:
```
  // returns the Specification object so that
  // class SpecTest extends JUnit4(mySpec.accept("tag1")) is valid
  mySpec.accept("only this").reject("still failing") 
```

Tags are also applicable to whole systems under specification so that all included examples will also be tagged:
```
  "this system" should {
    // lots of examples
  } tag("basic functionalities")
```

From the command line, the tags can be accepted or rejected with the following options:

  * `--accept` or `-acc` followed by a comma-separated list of tags
  * `--reject` or `-rej` followed by a comma-separated list of tags

For example: `scala mySpecification --accept tag1,tag2 --reject tag3`

# Specification plan #

A Specification can be executed so as to display its systems and first-level examples descriptions without executing them.

In order to activate this functionality, you need to:

  * pass the `-plan` option to the command-line arguments
  * use the `-Dplan` system property when executing the specifications with JUnit

Then sus and examples descriptions are displayed like this:
```
  - This system should
    - do this
    - do that
```