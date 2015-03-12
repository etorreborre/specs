

# How to use Data Tables #

Suppose you want to specify that a directory path and a file name should be combined to provide a proper full path. One way to specify this is to provide examples of different possible combinations and results (taken from the xmlRunnerUnit specification):
```
import org.specs.util.DataTables

object xmlRunnerSpec extends Specification with DataTables { // dont forget to mix-in the DataTables trait!
...
    "create an xml file in the specified output directory, handling file separators" in {
       "output dir" | 	"spec name" | 	"file path"  		|>
       ""           ! 	"spec1"     !	"./spec1.xml"		|  
       "result"     !	"spec1"     !	"./result/spec1.xml" 	|  
       "result/"    !	"spec1"     !	"./result/spec1.xml" 	|  
       "result\\"   !	"spec1"     !	"./result/spec1.xml" 	|  
       "/result"    !	"spec1"     !	"/result/spec1.xml" 	|
       "\\result"   !	"spec1"     !	"/result/spec1.xml" 	|
       "result/xml" ! 	"spec1"     !	"./result/xml/spec1.xml"| { (dir, spec, result) =>
           xmlRunner.outputDir = dir
           spec1.name = spec
           xmlRunner.execute
           xmlRunner.files must haveKey(result)
       }
    }
...
```

In the example above, you have a DataTable with:
  * a header describing the content of the columns
  * rows being valid combinations
  * a function which applied to each row, specifies the expected behavior

The resulting output in case of a failure would be:
```
 |"output dir" | "spec name" | "file path"           |
x|"wrong"      | "spec1"     | "./spec1.xml"         | Map(./bad/spec1.xml -> <...>) doesn't have key './spec1.xml'  
 |"result"     | "spec1"     | "./result/spec1.xml"  |  
```

Please note the small `>` on the border of the table. This is what makes the table being actually executed in the specification. Think about it a the "play" command (it can be placed on any row).

## DataTable contexts ##

When it is necessary to do some initialization on each row of a DataTable, a [`Context` http://code.google.com/p/specs/wiki/DeclareSpecifications#Shared_contexts] object can be used:

```
  var counter = 0
  val context = beforeContext(counter = 0)

    context|
    "a"    | "b" | "sum" |>
     1     !  2  !   3   |
     2     !  2  !   4   | { (a, b, c) => 
      counter must_== 0
      counter = a + b + c
    }
```

In the example above, the `beforeActions` of the context will be called before each row execution. In the case of a more general context, for each row:

  * `beforeActions` are called
  * then the function, with the row data is executed inside the `aroundExpectationActions`
  * and finally the `afterActions` are called

# How to add syntactic sugar to your specifications #

When you import `org.specs.Sugar._` you get some syntactic sugar that you can add to enhance your specifications:

  * tuples can behave as lists: `(1, 2, 3).tail must_== List(2, 3)`
  * you can use a `times` method to iterate on a block of code:
```
3.times {println _}

var j = 0
3 times {j += _}
j must_== 6
```
  * println any object with `myObject.println` or `myObject.pln`. If you want the myObject to be printed _and_ returned you can use `myObject.pp` ("print and pass")
  * you can use the constants `ok` and `ko` as equivalents of `true` and `false`
  * you can wait for a certain amount of time mixing the `WaitFor` trait and using the `waitFor` method: `waitFor(10.ms)`