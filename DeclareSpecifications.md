

# Declare a specification #
You can declare a new specification by extending the Specification class:
```
import org.specs._

object newSpecification extends Specification {
}
```

A specification is composed of:

  * simple examples as in the [QuickStart example](http://code.google.com/p/specs/wiki/QuickStart)
  * Systems Under Specification (SUS), containing examples. A SUS is used to group several examples sharing the same context
  * examples can themselves contain sub-examples in order to refine the behavior being specified
  * other specifications (see [Compose a specification](http://code.google.com/p/specs/wiki/DeclareSpecifications#Compose_specifications))

## Specification name ##

The name of the specification is the name of the specification object by default but you can use a more meaningful name like this:
```
import org.specs._
object newSpecification extends Specification("My new Specification") {
}
```

## Change the specification description ##

By default the description of a specification, which is displayed in case of a composed specification, is the class name of the specification object (without any `$`).
If you want to set a more meaningful name, you need to override the description attribute:
```
object basicFunctionalitiesSpecification extends Specification {
  var description = "the system basic functionalities are"
}
```

# Systems under specification #

You specify systems by simply adding a description followed by the method `should`:
```
import org.specs._

object newSpecification extends Specification {
 "my system" should {...}
}
```

Then, when you will execute the specification, you will see displayed:
```
my system should
...rest of the specification
```

Note that you can not nest SUS inside SUS or examples so that SUS are always top-level in a Specification.

### Alias for the should keyword ###
You can declare several system in a specification. In that case you can, for instance, specify the main functionalities of your system using a `should` method and add another section using <sub>can</sub> for advanced features:
```
"For power users, my system" can {...}
```

### Extend the should verb ###

At times, some words can be repeated at the beginning of each example:
```
  "The project helpers" should {
    "provide this function" in { ... }
    "provide that function" in { ... }
    "provide this other function" in { ... }
  }
```

You can factor out the "provide" word by adding a new function:
```
  def provide = addToSusVerb("provide")

  "The project helpers" should provide {
    "this function" in { ... }
    "that function" in { ... }
    "this other function" in { ... }
  }
```

### Without implicit defs ###
If you don't want to use Scala's implicit defs for the String class, you can also use:

```
declare("my system") should {...}
```

# Specify an example #

You specify an example of what your system is supposed to do by adding a description inside a specified system followed by the method `in`:
```
"my System" should {
  "be wonderful" in {...}
  "be elegant" in {...}
}
```

### Alias for the in keyword ###
If you don't like the `in` keyword, you can replace it with the meaningless operator <sub>>></sub>:
```
"my System" should {
  "be wonderful" >> {...}
}
```

### Sub examples ###
It also is possible to nest examples where it makes sense, to refine the behavior being specified. For instance:
```
"The Scala language" should {
  "provide a && operator" >> {
    "returning true for true && true" >> { true && true must beTrue } 
    "returning false for true && false" >> { true && false must beFalse } 
    "returning false for false && true" >> { true && false must beFalse } 
    "returning false for false && false" >> { false && false must beFalse } 
  }
}
```

### Without implicit defs ###
You can also specify an example without using implicits defs:
```
declare("my System") should {
  forExample("be wonderful") in {...}
}
```

## Anonymous examples ##

Example descriptions are not even necessary to create a specification:
```
import org.specs.mock.JMocker

object expectationsOnly extends Specification("Hello world") with JMocker {
  "hello world".size must_==11
  3 must_== { "abc".size }
  classOf[java.io.OutputStream].expectsOne(_.flush) in { _.flush }
}
```

The 3 assertions above will create new examples: example, example 2, example 3

## Pending examples ##

Depending on your development process you may want to:

  1. define all your examples first, then write all the code progressively (example body + implementation)
  1. define all your examples first including the example body, but not the implementation

Let's see how specs supports those 2 styles of development:

### Pending example body ###

That's specs default mode for case #1.

If you leave the body of an example empty (or more precisely without any expectations) the example will be marked as `PENDING` (note that this behavior [can be overriden](http://code.google.com/p/specs/wiki/RunningSpecs#Override_specs_default_behavior)).

### Pending implementation ###

In case #2 you would have 2 options:

  * either you add a `skip(msg)` statement at the beginning of each example.
> > Examples won't be executed and be marked as `PENDING` until you remove the `skip`.

  * or you can extend the `org.specs.specification.PendingUntilFixed` trait and use the `pendingUntilFixed` method to enclose your example code:

```
  object s extends Specification with PendingUntilFixed { 
    "ex" in {       
      pendingUntilFixed { 1 must_== 2 }
    }
  } 
```


> This will mark the example as `PENDING` until someone fixes it (maybe accidentally!). In that case the example status will display a failure prompting you to consider removing the `pendingUntilFixed` call.

Another way of getting the same behavior is to use the `pendingUntilFixed` method on the example or the sus:
```
  object s extends Specification with PendingUntilFixed { 
    "ex" in { 1 must_== 2 } pendingUntilFixed 
  } 
  object s2 extends Specification with PendingUntilFixed { 
    // all the examples are marked as 'pendingUntilFixed'
    "sus" should {
      "ex" in { 1 must_== 2 } 
      "ex2" in { 1 must_== 2 }
    } pendingUntilFixed 
  } 
```

# Compose specifications #

Big specifications can be cut in several sub-specifications:
```
object bigSpec extends Specification {
  "this big specification".isSpecifiedBy(
                  basicFunctionalitiesSpec,
                  advancedFunctionalitiesSpec,
                  extensionsSpec)
}
```

Alternatively, when it makes sense, you can also use `areSpecifiedBy` instead of `isSpecifiedBy`.

When executed, a composed specification will display its results with each sub-specification results being indented:
```
  this big specification isSpecifiedBy
    the system basic functionalities are
       the system should
         + say hello
         + say hello world
    the system advanced functionalities are
      ...
```


# Share examples #

If you want to share examples between systems specifications, you can:

  * define a method returning examples:
```
def sharedFunctionalities = {
  "provide functionality 1" in {...}   
  "provide functionality 2" in {...}   
}
```

  * use this method inside another example:
```
"my system" should {
  "provide common functionalities" in { sharedFunctionalities }
}
```

You can also reuse all the examples of another system under specification with the `behave like` declaration:
```
"A full stack" ->-(fullStack) should { 
  // you can reference the other system under spec by name
  behave like "A non-empty stack below full capacity" 

  // or directly if you saved it in a val
  behave like nonEmptyStackBelowCapacity 
  ...
}
```

# Execution model #

The execution model of Specifications is as follows:

  1. when instantiating a Specification object, the SUS are first created with their description, but the examples they hold are not yet evaluated
  1. once a SUS is queried for its examples, the examples are created but not executed
  1. when an example is queried for its possible failures, it is executed

By default an example is executed in "isolation" from the other examples. Each variable declared inside a SUS, which may have been changed by another example is set to its initial value:
```
object spec extends Specification {
  "this system" should {
    var x = 0
    "execute the first example" in {
      x = 1
      x must_== 1 // success
    }
    "execute the second example in isolation" in {
      x must_== 0 // success
    }
  }
}
```

This simplifies the set-up of examples because [fixtures](http://en.wikipedia.org/wiki/Test_fixture) can be created without having to use [before/after](http://code.google.com/p/specs/DeclareSpecifications#Call_a_function_before_or_after_each_example) declarations to clean-up the mutated variables/objects or a special fixture-passing mechanism to re-initialize fixture state.

## Warning! ##

The scheme described above works by cloning the original specification for each example that needs to be executed in isolation. However there may be issues if the creation of your specification has some side-effects. Here is an example of what can go wrong.

[Issue 150](https://code.google.com/p/specs/issues/detail?id=150) shows a Specification starting an actor as a local variable and quitting it in the doAfter method.

Unfortunately doBefore and doAfter methods are only triggered for "leaf" examples (i.e. not containing nested examples), so during the evaluation of non-leaf examples, an actor will be started but never quitted. The remedy to this issue is to have symmetric doBefore and doAfter methods where doBefore starts the actor and doAfter quits it.

## What if I want to share state? ##

It is still possible to locally declare that you want to share the variables state by using `shareVariables()`:

```
object spec extends Specification {
  "this system" should {
    shareVariables()
    var x = 0
    "execute the first example" in {
      x = 1
      x must_== 1 // success
    }
    "execute the second example reusing x value" in {
      x must_== 1 // success
    }
  }
}
```

### Sequential execution ###

By default, the execution mode of examples of a Specification is "on demand". The examples are only executed when a Runner requests the number of failures or errors.

However, if you need to execute samples as soon as they are declared(as in a [literate specification](http://code.google.com/p/specs/wiki/LiterateSpecifications)), you can use the method `setSequential`:

```
object mySpec extends Specification {
  "The first functionality" should { 
    setSequential() // ensures that each example is executed as soon as defined

    "have example 1 execute first" in {...}
    // example 2 uses the results of example 1
    "have example 2 execute in second" in {...}
  }
}
```

(**next release**)

The sequential mode of execution can lead to issues if variables are not shared (see [this  discussion](http://groups.google.com/group/specs-users/browse_thread/thread/bbcc4ccfc05957f2)). So now when `setSequential()` is used `shareVariables()` is automatically set.

## Call a function before or after each example ##

Given the "isolated" execution model in specs, you usually don't need before/after functions to set or reset state before and after each example. However, when mutating external resources (database, files) or static objects you may needto use `doBefore` and `doAfter` functions to reset your system state:
```
"my system" should { doBefore { resetTheSystem() /** user-defined reset function */ }
  "mess up the system" in {...}
  "and again" in {...}
}
```

_Note_: you can also use syntactic sugar and write:
```
"my system" should { resetTheSystem.before // equivalent as the example before, using an implicit definition.
...
```

### With nested examples ###

When there are nested examples the doBefore/doAfter clause are only executed for "leaf" examples to avoid spending resources on the execution of non-leaf examples.

## Execute the Example expectations inside a specific context ##

When running your examples you may need to specify that all expectations occur inside a specific context:
```
  val dynamicVar = new DynamicVariable[String]
  "my example uses a dynamic variable" in {
    myVar.doWith("this session") { 
      // expectations here
    }
  }
  "my other example uses the same dynamic variable" in {
    myVar.doWith("this session") { 
      // other expectations here
    }
  }
```

You can remove that boilerplate code by specifying an "around" action:
```
  val dynamicVar = new DynamicVariable[String]
  def withDynamicVar(a: =>Any) = myVar.doWith("this session") { a }
  doAroundExpectations(withDynamicVar(_))

  "my example uses a dynamic variable" in {
    // these expectations will be executed "inside" the around method
  }
  "my other example uses the same dynamic variable" in {
    // those too
  }
```

Note that the function passed to the `doAroundExpectations` method must have a call-by-name parameter (`a: =>Any`).

## Call a function before or after each system or specification ##

In addition to the capacity to call methods before and after each example, it is also possible to call functions before and after the whole system with `doFirst` and `doLast`:

```
"my system" should { 
  doFirst { prepareTheSystem() /** user-defined setup function */ }  
  // can also be written: prepareTheSystem().doFirst
  
  "execute this" in {...}
  "and that" in {...}

  doLast { cleanUp() /** user-defined cleanup function */ } 
  // can also be written: cleanUp().doLast
}
```

And more globally, before and after executing the specification for several systems, it is also possible to setup/teardown the environment:

```
object mySpec extends Specification { 
  doBeforeSpec { prepareTheWholeEnvironment() /** user-defined function */ } 
  // can also be written: prepareTheWholeEnvironment().beforeSpec
  
  "my system" should { 
    doFirst { prepareTheSystem() }
    "execute this" in {...}
    "and that" in {...}
    doLast { cleanUp() }
  }

  doAfterSpec { cleanupTheWholeEnvironment() /** user-defined function */ } 
  // can also be written: cleanUpTheWholeEnvironment().afterSpec

```

_Note_: doBefore/doAfter or doFirst/doLast declarations can be placed anywhere inside a system, similarly doBeforeSpec and doAfterSpec can be placed anywhere in the spec declaration.

# Shared contexts #

You may want sometimes to share the set-up of systems and give it a meaningful name. To do this, you need to create `Context` objects:
```
object StackSpecification extends Specification {
  val empty = beforeContext(stack.clear)
  val full = beforeContext(createStack(stack.capacity))
  val nonEmpty = beforeContext(createStack(3))
  val belowCapacity = beforeContext(createStack(3))
...
}
```

The `beforeContext`, `afterContext`, `context` methods are factory methods to create `Context` objects:
```
def context(b: =>Any, a: =>Any) = new Context { before(b); after(a) }
```

Once you've defined a context, you can reuse it with the "threading" operator `->-`:
```
  // I'm "threading" the context within each example of the system under specification
  "A full stack" ->-(full) should { 
  ...
  }
  "An empty stack" ->-(empty) should { 
  ...
  }
```

_There are alias methods for the cryptic `->-` operator (the only difference being that they're not call-by-name)_
```
  "A full stack" definedAs full should { ... }
  "A stack" when empty should { ... }
```

More global context setup and teardown contexts can also be created with the following methods:
```
  val stackContext  = globalContext(createStack, deleteStack)
  val stackContext2 = new Context { first(createStack) }
  val stackContext3 = new Context { last(deleteStack) }
```

The whole list of methods available on a Context object is:

  * before (alias: beforeExample): actions to execute before each example
  * after  (alias: afterExample ): actions to execute after each example
  * first  (alias: beforeSus    ): actions to execute before each sus
  * last   (alias: afterSus     ): actions to execute after each sus
  * until: predicate which must be true otherwise the example execution is repeated

## Repeated examples ##

If you want to repeat the same examples with different data until a condition is true, you can use the `until` method:
```
  "This system" should {
    shareVariables()
    var counter = 1
    doAfter(counter += 1)
    until(counter == 3)

    "pass the example with an int value" in { counter must be_>(0) }
  }
```

This method is also available on `Context` objects, so you can write:
```
  val stack = new Stack(1) // stack size is 1
  val stacks = afterContext(stack.size += 1).until(stack.size == 10)
  "A non-empty stack" ->-(stacks) should { 
  ...
  }
```


## Specification context ##

Some contexts are applicable to all the systems of a given specification. In that case, instead of having to thread the context every time, you can define a `SpecContext` which will be automatically applied to all the systems of the Specification:
```
  object RepositorySpecification extends Specification {
    new SpecContext {
      // clean the DB
      beforeExample(deleteUsersTable)
      // make sure that each example expectations run inside a database session
      aroundExpectations(inDatabaseSession(_)) 
    }

    "A Users repository" can {
      "create a user" in {
        val eric = User("Eric", "password")
        Users.mergeAndFlush(eric)
        Users.find(classOf[User], "Eric") must_== Some(eric)
      }
    }
    "A Users repository" should {
      "throw and exception if a user name has a length < 5" in { ... }
      ...
    }
  }
```

A `SpecContext` is a subclass of the `Context` class with the additional methods:

  * beforeSpec: actions to execute before the specification
  * afterSpec:  actions to execute after the specification

### Share a specification context ###

A specification context can be created and reused across different specifications with the `apply(spec: Specification)` method:
```
  object DBContext extends Specification {
    val setup = new SpecContext {
      // clean the DB
      beforeExample(deleteUsersTable)
      // make sure that each example expectations run inside a database session
      aroundExpectations(inDatabaseSession(_)) 
    }
  }
  object RepositorySpecification extends Specification {
    DBContext.setup(this) // use the DBContext specification context in this specification
    "A Users repository" can {
      ...
    }
  }

```

# "Boosted" Specification #

If you don't mind getting lots of implicit definitions you can use the following spex.Specification class: {
package org.spex
import org.specs.mock.Mockito
import org.specs.Sugar
import org.specs.runner.

class Specification extends org.specs.Specification with Mockito with Sugar with JUnit with ScalaTest with DataTables with ScalaCheck
}

With this only class, you will be able to:

  * use Mocks (with the Mockito library)
  * use [tuples as lists](http://code.google.com/p/specs/wiki/AdvancedSpecifications#How_to_add_syntactic_sugar_to_your_specifications)
  * execute the specification as JUnit and `ScalaTest`
  * use [ScalaCheck](http://code.google.com/p/specs/wiki/MatchersGuide#Matchers_applicable_to_ScalaCheck_properties)
  * use [DataTables](http://code.google.com/p/specs/wiki/AdvancedSpecifications)