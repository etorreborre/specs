![http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png](http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png) **Alpha version!**



# Why literate specifications? #

The literate specifications support in **specs** is intended to provide a way to develop Acceptance specifications in a [Fitnesse](http://fitnesse.org/FrontPage) style.

The basic idea is to define Systems under specification as some informal text, possibly with images. Parts of this text are considered as examples and they are backed up by some Scala code executed against the system being specified.

Furthermore, **Forms** add a very convenient way to declare and display expected structured information, like business objects (customers, orders,... the usual ones).

## A short example ##

Here is a short example of such a `LiterateSpecification`:
```

class HelloWorldSpecification extends HtmlSpecification with Textile {
 
  "The greeting application" is <t>

h3. Presentation

This new application should say "hello" in different languages.

For example,<ex>by default, saying hello by default should use English</ex> 
{ greet must_== "hello" }
 
Then, other languages, like <ex>French and German should be supported too</ex> 
{ eg {
    greet("French") must_== "bonjour"
    greet("German") must_== "hallo"
  } 
}

<ex>Japanese should be supported also</ex> { notImplemented }

 </t>
}
```

The example above shows the following features:

  * the `HelloWorldSpecification` is an `HtmlSpecification` which is actually a `LiterateSpecification` mixing-in the `Html` trait to report the execution with an Html runner

  * the name of the first System under specification is "The greeting application" and is defined by an xml Element `<t></t>`

  * a title is declared with the `h3.` markup tag, using the [Textile](http://en.wikipedia.org/wiki/Textile_(markup_language)) markup language as specified by the mixed-in trait `Textile`. The other supported markup language is [Markdown](http://en.wikipedia.org/wiki/Markdown).

  * the first example description is enclosed in the `<ex></ex>` element.

  * it is **immediately** followed by an expectation which is some Scala code enclosed in `{}` curly braces.

  * when there are several expectations, they must be enclosed in a `eg` function to declare that they belong to the same example (otherwise the first expectation would be attached to the example description, the next ones would be anonymous examples)

When you run the example above, the result should be the following [html](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.samples.helloWorld.html)


# Helper functions #

The first helper function seen above is the `notImplemented` function. This will just create an example which body throws a `SkippedException` .

There are other available helper functions:

  * `shh`. To "silence" the result of an expression. If you want to execute an action in the middle of the specification, you can silence it to avoid its result to be printed:
```
  This some text { doSomething().shh }
  or This some text { doSomething() <| }
```

  * `consoleOutput(messages: Seq[String])`. This displays the messages with a "prompt", mimicking the Scala interpreter (see [Scala interpreter](http://code.google.com/p/specs/LiterateSpecifications#Scala_interpreter))

  * `linkTo(description, otherSpecification)`. Links to sub-specifications can be included with the `linkTo` function. This will include the other specification in the current one (if not already present) and create a Html link to its results.

  * `linkTo(description, xml node)`. creates a specification on the fly for the `xml node` and link to it.

  * `myText.collapsible(title)` or `myText.collapsible(title, headerNumber)`. This creates a collapsible section with a title (and optionally with a different header style than the default `<h5/>`).

## Display code ##

Sometimes code is not always properly displayed on multiple lines with either Textile or Markdown. The workaround for this is to mixin the Textile or Markdown trait and use the following functions which will also add css coloring:
```
  This is some code to display properly: {
    """
    println("Hello world")
    """ >@
    // or "println("Hello world")".code without a cabalistic sign
  }
```

## Simple Properties ##

Sometimes, in order to stay [DRY](http://en.wikipedia.org/wiki/Don't_repeat_yourself), you may want to extract part of a description as a property to reuse it in the implementation of the example. Here is an example from the xmlRunnerSpec specification
```

It is possible to indicate the output directory of the runner, for example: {"specresults" as runnerOutputDir}
      In that case, {"the xml file should be created in the output directory with path: " +
                 "./specresults/org.specs.runner.sp1.xml".as(path) in checkOutputDirectory}

```

In the situation above, the `runnerOutputDir` property will hold the "specresults" values which will also be displayed in the literate specification text.

For your convenience, there are several traits with predefined properties that you can mix-in to your specification to avoid creating Property variables:

  * `StringProperties`: a, b, c, d, e, f
  * `IntProperties`: i, j, k, l, m, n
  * `DoubleProperties`: u, v, w, x, y, z
  * `BooleanProperties`: o, p, q, r, s, t
  * `XmlProperties`: xml, xml2, xml3, xml4, xml5, xml6
  * `CurrentProperty`: it
  * `AllProperties`: all the traits above

All those properties offer an additional shortcut to set the property value:
```
... with AllProperties {

  "the age is " + 10.i " + years  // set the property i and display "the age is 10 years"
  "c:/temp".a                     // set the property "a"
  <ex>result<ex>.xml              // set the property "xml"
}
```

# Data tables #

[DataTables](http://code.google.com/p/specs/wiki/AdvancedSpecifications#How_to_use_Data_Tables) can also be used inside a `LiterateSpecification` with the following syntax:
```

 All those small examples should be ok: {
   "examples are ok" inTable 
   "a" | "b" | "sum" |
    1  !  1  ! 2     |
    1  !  2  ! 3     |
    1  !  3  ! 4     | { (a: Int, b: Int, sum: Int) =>
     a + b must be equalTo(sum)
   }
 }

```

# Snippets #

Snippets of Scala code can be inserted in the specification by adding the `LiterateSnippets` trait to the `LiterateSpecification`:
```
  This is creating a piece of Scala code. The "println("hello")" code is stored in a variable called "it" (stored in the org.specs.specification.SnipIt trait). 
  It is also displayed in the Html report with a code style:
  { "println(\"hello\")" snip it }
  
  This code can then be executed with:
  { execute(it) }

  You can check that the output indeed contains the expected messages with: {
    executeIs("hello") // equivalent to execute(it) must include("hello")
    
    // same thing but adds a small prompt sign before the output text
    >("hello")
    // 
  }

```

Note that each call to `snip it` will actually clear the `it` variable. If you want to have some code being kept in the `it` variable you can use the `prelude` method:
```
  { """import scala.collection.immutable._
       import scala.xml._
    """ prelude it }

  The prelude method can be called several times
  { """import org.specs.specification._
       import org.specs.util._
    """ prelude it }
```

As you can see, this is very useful when declaring shared imports.

For a complete example, please have a look at [the Mockito specification](http://code.google.com/p/specs/source/browse/trunk/src/test/scala/org/specs/mock/mockitoSpec.scala) and its [Html report](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.mock.mockitoSpec.html)

# Forms #

Forms are the preferred way in specs to create examples in a table format, so that they are readable by business users. You can have a look at some examples [here](http://specs.googlecode.com/svn/samples/LiterateSpecifications).

See the [Forms](http://code.google.com/p/specs/wiki/Forms) page for more information.