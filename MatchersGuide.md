

# Matchers presentation #

There are different kind of matchers which are used to assert that some properties must be verified. Generally they are used like this:

```
"This example presents a matcher" in {
  // verifyThisProperty is a Matcher
  myObject must verifyThisProperty(parameter)
}
```

### Be/Have matchers ###

Most of the matchers presented below are used with `myObject must matcher`. However some matchers are starting with the words 'be' or 'have'. Those matchers also have alternate forms allowing them to be used with 'be' or 'have' as separated words:

```

 // this is equivalent
 "hello" must beMatching("h.*")
 "hello" must be matching("h.*")

 // this is equivalent
 "hello" must not(beMatching("z.*"))
 "hello" must not be matching("z.*")

 // this is equivalent
 List("hello") must not(haveSize(2))
 List("hello") must not have size(2)

 // articles are also allowed
 Map("hello" -> "world") must have the key("hello")

 // be and have matchers can be combined with logical operators too
 "hello" must be matching("h.*") and not be matching("z.*")
```

### Create your own Matcher ###

Creating a new Matcher is easy. You extend the `Matcher` class, using a case class and implement the `apply` method:

```
  import org.specs.matcher.Matcher

  "A matcher" can {
    "be created as a case class" in {
      case class matchHello(a: String) extends Matcher[String]() {
        def apply(v: => String) = (v == a, "okMessage", "koMessage")
      }
      "hello" must matchHello("hello")
    }
  }
```

You must return a tuple containing:

  * a boolean value indicating a success or a failure when applied to the object which `must` match
  * a message which can be displayed in case of a success
  * a message which can be displayed in case of a failure

A matcher can also be created through as a val:

```
  import org.specs.matcher.Matcher

  "A matcher" can {
    "be created as a val" in {
      val beEven = new Matcher[Int] {
        def apply(number: => Int) = {
          val b = number
         (b % 2 == 0, b + " is even", b + " is odd")
        }
      }
      2 must beEven
    }
  }
```

Or through a method:
```
  import org.specs.matcher.Matcher

  "A matcher" can {
    "be created as a method" in {
      def divide(a: Int) = new Matcher[Int] {
        def apply(number: => Int) = {
          val b = number
          (a % b == 0, b + " divides " + a, b + " doesn't divide " + a)
        }
      }
      10 must divide(100)
      3 must not(divide(100))
    }
  }
```

_Implementation note: in the above examples, the lazy parameter to the apply method is passed to a local val. This avoids unnecessary evaluations when the parameter is also reused to create the result messages._

### Create the negation of a matcher ###

Simply use the `not` method on a matcher:

```
 2 must beEven // beEven is a user-made matcher checking if a number is even
 val beOdd = beEven.not
 3 must beOdd
```

In that case, the ok message of the first matcher is used as a ko message for the second matcher.

### Create a Be/Have matcher ###

When you create a matcher like `beEven`, you may want it to be also usable with `be` as a separated words. In order to do so, you need to add an implicit conversion from `org.specs.specification.Result[T]` (which is the result value of the expression `a must be`) and add the expected matcher as a method:

```
  implicit def toOddEvenMatcherResult(result: Result[T]) = new OddEvenMatcherResult(result)
  class OddEvenMatcherResult(result: Result[T]) {
    def even = result.matchWithMatcher(beEven)
    def odd = result.matchWithMatcher(beEven.not)
  }
```

### Combine matchers ###

You can combine matchers with logical operators: `and`, `or`, `xor`, `verifyAll`, `verifyAny`

```
  "ab" must (beMatching("a") and beMatching("b"))
  "ab" must (beMatching("a") or beMatching("c"))
  "ab" must (beMatching("a") xor beMatching("c"))
  "ab" must verifyAll(beMatching("a"), beMatching("b"))
  "ab" must verifyAny(beMatching("a"), beMatching("b"))
```

`verifyAll` must be ok for all matchers and `verifyAny` must be ok for at least one matcher.

A matcher for a single element can be transformed to a matcher for an iterable can be created with `toIterable`:

```
  List(1, 2, 3) must beLessThan(5).toIterable // is ok!
  List(1, 6, 3) must beLessThan(5).toIterable // is ko!
```

### Limit the applicability of a matcher ###

In some cases, you may want to specify that a matcher is only applicable depending on some conditions:
```
 "abc" must beMatching(s).when(s == ".")
 "abc" must beMatching(s).unless(s.isEmpty)
```

### Eventually matchers ###

Some actions done on the specified system may require some time to take effect, such as creating a file. In that case, it is handy to be able to retry a matcher automatically a number of times:
```
  val iterator = List(1, 2, 3).iterator // simulation of a value which may not be quite right on first attempts
  iterator.next() must eventually(be(3)) // can be also written be(3).eventually
```

The number of retries and sleeping time between tries are of course configurable:
```
  val iterator = List(1, 2, 3).iterator
  // @see the org.specs.util.Time class for Duration implicit conversions
  // the default is 40 retries, 100.millis
  iterator.next() must eventually(10, 1.second)(be(3)) 
```

# Matchers applicable to any object #

  * `a must be_==(b)` is ok if `a == b` (alias: `a must ==(b)`, `a must_== b` or `a mustEqual b`)
  * `a must be_!=(b)` is ok if `a != b` (alias: `a must_!= b`)

  * `a must beEqualTo(b)` is ok if `a == b` and constrains a and b to have the same type at compilation time.

  * `a must beDifferent(b)` is ok if `a != b` and constrains a and b to have the same type at compilation time

  * `a must be(b)` is ok if `a eq b` (alias: `a mustBe b` or `a mustEq b`)
  * `a must notBe(b)` is ok if `!(a eq b)` (alias: `a must notEq(b)` or `a mustNotBe(b)`)

Note that the `eq` operator can be very tricky, so be careful to use it on purpose. See [issue 40](https://code.google.com/p/specs/issues/detail?id=40) for more details.

  * `a must beIn(iterable)` is ok if `iterable.exists(_ == a)`
  * `a must notBeIn(iterable)` is ok if `!iterable.exists(_ == a)`

variable arguments can also be used with beOneOf:
  * `1 must beOneOf(1, 2, 3)`
  * `4 must notBeOneOf(1, 2, 3)`

  * `a must beEmpty` is ok if `a` defines a `isEmpty` method and `a.isEmpty`
  * `a must notBeEmpty` is ok if `a` defines a `isEmpty` method and `!a.isEmpty`

  * `a must verify(f)` is ok if `f(a) == true` (alias: `a mustVerify f` or `a verifies b`)

  * `a must beLike { case p => aBooleanFunction }` is ok if a matches the pattern `p` and the function `aBooleanFunction` returns true:

```
List(1, 2) must beLike { case x::y::Nil => true }
```

  * `a must beNull` is ok if a is null
  * `a must notBeNull` is ok if a is not null

  * `a must beAsNullAs(b)` is a shortcut for 2 expectations:
```
a must beNull.when(b == null)
b must beNull.when(a == null)
```

  * `a must haveClass[c]` is ok if a.getClass == c
  * `a must notHaveClass[c]` is ok if a.getClass != c
  * `a must haveSuperClass[c]` is ok if c.isAssignableFrom a.getClass
  * `a must notHaveSuperClass[c]` is ok if !(c.isAssignableFrom a.getClass)
  * `c1 must beAssignableFrom[c2]` is ok if c1 isAssignableFrom c2
  * `c1 must notBeAssignableFrom[c2]` is ok if !(c1 isAssignableFrom c2)

You can also introduce a failure with the `fail` function:

  * `fail("not implemented yet")`

### If you want to verify that an exception is thrown ###

  * `a must throwA[SpecialException]` is ok if evaluating `a` throws an Exception with the type `SpecialException` (alias: `throwAn[...]`)

Or we can check the exception type **and** message:

  * `a must throwA(MyUserException("message"))` is ok if evaluating `a` throws an exception e of type `MyUserException` and `e.getMessage == "message"` (alias `throwAn`, `throwThis`)

Or we can use pattern matching for more specific checks:

  * `a must throwA(new Exception).like {case Exception(m) => m.startsWith("bad")} ` is ok if evaluating `a` throws an Exception and the message starts with "bad"


# Matchers applicable to Strings #

  * `a must equalIgnoreCase(b)` is ok if `a equalsIgnoreCase(b)` (alias: `a must_==/ b`)
  * `a must notEqualIgnoreCase(b)` is ok if `!(a equalsIgnoreCase(b))` (alias: `a must_!=/ b`)
  * `a must equalIgnoreSpace(b)` is ok if `a.trim == b.trim`
  * `a must notEqualIgnoreSpace(b)` is ok if `a.trim != b.trim`
  * `a must beMatching(b)` is ok if a matches the regexp b (alias: `a mustMatch b`)
  * `a must notBeMatching(b)` is ok if a doesn't match the regexp b (alias: `a mustNotMatch b`)
  * `a must find(regexp)` is ok if the regular expression finds some groups in a (using `java.util.regex.Pattern.compile(a).matcher(b).find()`). For example: `"lallbl" must find("l(.*?)l")`
  * `a must find(regexp).withGroups(g1, g2)` is ok if the regular expression finds the groups g1 and g2 in a. For example: `"lallbl" must find("l(.*?)l").withGroups("a", "b")`

  * `a must include(b)` is ok if `a.indexOf(b) >= 0`
  * `a must notInclude(b)` is ok if `a.indexOf(b) < 0`

  * `a must startWith(b)` is ok if `a startsWith b`
  * `a must notStartWith(b)` is ok if `!(a startsWith b)`

  * `a must endWith(b)` is ok if `a endsWith b`
  * `a must notEndWith(b)` is ok if `!(a endsWith b)`

## Smart differences ##

The default failure message for string comparisons is using "smart differences", that is, if the two strings to compare are short (less than 30 characters) the strings are shown as they are. If the strings are longer, the "detailed diffs" message described below is enabled with 30 as a triggering size for shortening and 20 as the shortening size.

## Detailed differences ##

You can get more detailed failure messages for String comparisons with the `detailedDiffs` method:
```
  object SpecificationWithDetailedFailures extends Specification {
    detailedDiffs() // enable detailed differences
    "abc" must_== "ab"    // 'ab[c]' is not equal to 'ab'
    "abcd" must_== "abd"  // 'ab[c]d' is not equal to 'abd'
    "acd" must_== "abd"   // 'a[c]d' is not equal to 'abd'

    // 'the kitt[en] is pret[ty]' is not equal to 'the [s]kitt[y] is pret[en]'
    "the kitten is pretty" must_== "the skitty is preten"   
  }
```

Note that the pair of separators used to highlight the differences can be changed with:
```
  detailedDiffs("()")
  // or detailedDiffs("<<>>") to separate with << and >>
```

You can also specify that the detailed diffs should only be triggered when the strings are more than 10 characters long:

```
  detailedDiffs("()", 10)
```

And finally, it is possible to declare that the strings should be shortened between the differences in order to be able to spot them more rapidly:

```
  detailedDiffs("()", 10, 5) // 5 is the shorten size

  '...aaaaa[bb]aa...aa[cc]aaaaa...' is not equal to '...aaaaa[xx]aa...aa[yy]aaaaa...'
  "aaaaaaabbaaaaaaaaaaaccaaaaaaaa" must_== "aaaaaaaxxaaaaaaaaaaayyaaaaaaaa"   

```

_Performances optimizations_

When doing a detailed difference on large strings (typically from a file), the memory and execution performances of the edit distance algorithm used to report differences can be pretty bad (see [issue 110](https://code.google.com/p/specs/issues/detail?id=110)). This is why the detailed differences are only reported by comparing lines without linefeeds.

In any case, it is possible to deactivate the detailed differences with a call to `noDetailedDiffs()`.

# Matchers applicable to Iterables #

  * `a must contain(b)` is ok if `a.exists(_ == b)` (alias: `mustContain`)
  * `a must notContain(b)` is ok if `!a.exists(_ == b)` (alias: `mustNotContain`)

  * `a must containAll(b)` is ok if `b.forall(x => a.exists(_ == x))`
  * `a must notContainAll(b)` is an alias for `containAll(b).not`
  * `a must containInOrder(b1, b2)` is ok if `a` contains each `b` and the order is respected


  * `a must exist(f)` is ok if a contains an element verifying the function f (alias: `mustExist`)
  * `a must notExist(f)` is ok if a doesn't contain an element verifying the function f (alias: `mustNotExist`)

  * `a must containMatch(b)` is ok if a contains a String matching the pattern `b` (alias: `haveMatch`, `mustHaveMatch`, `existMatch` (deprecated), `mustExistMatch` (deprecated))
  * `a must notContainMatch(b)` is ok if a doesn't contain a String matching the pattern `b` (alias: `mustNotContainMatch`, `notHaveMatch`, `mustNotHaveMatch`, `notExistMatch` (deprecated), `mustNotExistMatch` (deprecated))

  * `a must containMatchOnlyOnce(b)` is ok if a contains exactly one String matching the pattern `b`

  * `a must haveTheSameElementsAs(b)` is ok if a contains the same elements as the iterable b. This verification doesn't check the order of the elements but does a recursive verification is a and b contain iterables:
```
List(1, List(2, 3, List(4)), 5) must haveTheSameElementsAs(List(5, List(List(4), 2, 3), 1))
```
  * `a must haveSize(3)` matches if there are 3 elements in a

# Matchers applicable to Maps #

  * `map must haveKey(k)` is ok if `map.exists(p => p._1 == k)`
  * `map must notHaveKey(k)` is ok if `!map.exists(p => p._1 == k)`

  * `map must haveValue(v)` is ok if `map.exists(p => p._2 == v)`
  * `map must notHaveValue(v)` is ok if `!map.exists(p => p._2 == v)`

  * `map must havePair(p)` is ok if `map.exists(p == _)`
  * `map must notHavePair(p)` is ok if `!map.exists(p == _)`

  * `map must havePairs(pairs)` is ok if `pairs.forall(pair => map.exists(pair == _))`
  * `map must notHavePair(pairs)` is ok if `!pairs.forall(pair => map.exists(pair == _))`


# Matchers applicable to numerical values #

  * `a must beGreaterThan(b)` is ok if `a > b` (alias `be_>`)
  * `a must beGreaterThanOrEqualTo(b)` is ok if `a >= b` (alias `be_>=`)

  * `a must beLessThan(b)` is ok if `a < b` (alias `be_<`)
  * `a must beLessThanOrEqualTo(b)` is ok if `a <= b` (alias `be_<=`)

  * `a must beCloseTo(b, delta)` is ok if `b - delta <= a <= b + delta`

  * `a must be closeTo(b +/- delta)` is ok if `b - delta <= a <= b + delta`

# Matchers applicable to Options #

  * `a must beNone` is ok if a is None
  * `a must beAsNoneAs(b)` is ok if a is None when b is None or a is not None when b is None

  * `a must beSome[Type]` is ok if a is Some(t: Type)
  * `a must beSomething` is ok if a is Some(a: Any)
  * `a must beSome(value)` is ok if a is Some(value)

  * an Option matcher can be extended with a `which` condition

```
  Some(x) must beSome[String].which(_.startWith("abc"))
```

# Matchers applicable to `ScalaCheck` properties #

Let's say you want to implement a function which returns all the prefixes of a given list:

```
prefixes(List(1, 2, 3)) // => List(List(1), List(1, 2), List(1, 2, 3))
```

You can use [ScalaCheck](http://code.google.com/p/scalacheck) generators to generate lists of random size:
```
  // list is an arbitrary list (with at least one element)
  // prefix is a random prefix of list
  // testData contains all the prefixes of list and a random prefix
  val testData = for (list <- listOf1(elements(1, 2, 3, 4));
                               n <- choose(1, list.size-1);
                               val prefix = list.take(n))
                            yield (prefixes(list), prefix)
```

Then, mixing the `org.specs.ScalaCheck` trait to your specification, you can check that the prefix property passes all generated data:
```
  // the generated data must pass the following property
  testData must pass { t: (List[List[Int]], Seq[Int]) => val (prefixes, prefix) = t
    prefixes must contain(prefix)
  }
```

  * You can also use a simple boolean function `prefixes.exist(_ == prefix)` instead of
> > the above matcher. The failure message will be however less precise

  * You can express things the other way around:

`function must pass(generated_data)` instead of `generated_data must pass(function)`

  * You can simply use the `pass` matcher to verify a ScalaCheck property:

```
  import org.scalacheck.Prop

  // this property will alway be true
  val prop = Prop.forAll((a:Int) => true)
  prop must pass
```

_Note_: the `property` function is deprecated from `ScalaCheck` 1.5. You should use Prop.forAll instead.

  * Partial functions

The `testData must pass(function)` syntax can be improved by using a partial function where variables don't have to be explicitly typed. However there are some [overloading limitations](http://groups.google.com/group/specs-users/t/4d76a276e044a47b) in Scala which require the use of a new method:

```
  // the general form is `generator must validate(partial function)`
  testData must validate { case (prefixes, prefix) => prefixes must contain(prefix) }
```

A shorthand is also provided so that the previous expression is reduced to:

```
  // the general form is `generator validates (partial function)`
  testData validates { case (prefixes, prefix) => prefixes must contain(prefix) }
```


### ScalaCheck parameters ###

  * You can set ScalaCheck properties with `set` (or `display` to additionally see the results on the Console):

```
data must pass {
...
}(set(minSize -> 10, maxSize -> 20, maxDiscarded -> 30, minTestsOk -> 5))
```

where:

  * `minSize` is the minimum size for generated data (like lists)
  * `maxSize` is the maximum size for generated data (like lists)
  * `maxDiscarded` is the maximum number of tests which should be inconclusive in order for the property to pass
  * `minTestsOk` is the minimum number of tests which should be ok in order for the property to pass
  * `workers` is the number of working threads
  * `wrkSize` is the number of tests per thread

  * the default is: `minTestsOk->100, maxDiscarded->500, minSize->0, maxSize->100, workers->1, wrkSize->20`

You can also set the properties and display ScalaCheck messages with `display` instead of  `set`:

```
data must pass {
...
}(display(minSize -> 10, maxSize -> 20, maxDiscarded -> 30, minTestsOk -> 5))
```


### One liners ###

It is possible to specify properties directly with the `verifies` operator:
```
object StringSpecification extends Specification("String") with Scalacheck {
   "startsWith" verifies { (a: String, b: String) => (a + b).startsWith(a) }
   "endsWith" verifies { (a: String, b: String) => (a + b).endsWith(b) }
}
```

This will create examples which names are "startsWith" and "endsWith".

Note that you can still use `ScalaCheck` parameters to control the test generation:
```
"startsWith" verifies ((a: String, b: String) => (a + b).startsWith(a)).set(minTestsOk->250)
```

### Expectations number ###

By default, the statement `property must pass` will count each evaluation as one expectation. You can deactivate this behavior by declaring `dontExpectProperties` at the beginning of your specification and get finer control of what's counted as an expectation with `isExpectation`:

```
  dontExpectProperties()
  Prop.forAll((b: Boolean) => (b == true).isExpectation)
  // or
  Prop.forAll((b: Boolean) => isExpectation(b == true))
```

Of course, you can globally re-enable expectations counting with `expectProperties` for the rest of your specification:

```
  object spec extends Specification with Scalacheck {
    // no expectations count when evaluating properties
    dontExpectProperties()
    Prop.forAll((b: Boolean) => b == true)

    // one expectation will be counted on each property evaluation
    expectProperties()
    Prop.forAll((b: Boolean) => b == true)
  }
```

# Matchers applicable to XML #

  * `<a><b/></a> must equalIgnoreSpace(<a> <b/></a>)` is ok if the nodes are same but ignoring space (and inner nodes order). You can use `==/` as an alias

  * You can use the XPath-like `\` matcher to match nodes inside an xml node:
```
<a><b/></a> must \("b") // or <a><b/></a> must \(<b/>)
```

  * You can use the XPath-like `\\` matcher to match nodes deeply nested inside an xml node: `<a><s><c></c></s></a> must \\("c")`

  * `\` and `\\` can be composed to assert more complex statements:
```
<a><b><c><d></d></c></b></a> must \\("c").\("d")
```

  * `\` and `\\` can also check for attribute names:
```
<a><b name="value" name2="value"></b></a> must \("b", ("name2", "name")) // with the Sugar object implicitly transforming tuples to Lists 
```

  * `\` and `\\` can also check for inclusion of attribute names and values:
```
// just checks the presence of "name" -> "value"
<a><b name="value" name2="value2"></b></a> must \("b", "name"->"value")
```

```
val actual = <a><b name="value" name2="value2"></b></a> 

// using 'have' + a pair of attribute/value (it is a varargs parameter)
actual must have \("b", "name"->"value") 
```

It must be noted that you don't need to specify all the expected attributes and attributes values, but only the ones relevant to your example. If however you want the match to be exact (no additional attributes for instance), you would need to add `exactly` to the matcher:
```
val actual = <a><b name="value" name2="value2"></b></a> 

// this will fail because "name2" shouldn't be an attribute of "actual"
actual must \("b", "name"->"value").exactly
```

  * `\` and `\\` can also check for the presence of a whole Node:
```
<a><b name="value"></b></a> must \(<b name="value"/>)
```

  * `==/` check if 2 nodes are equals, ignoring space (alias: `equalIgnoreSpace`)
```
<a>
  <b name="value">
  </b>
</a> must ==/(<a><b name="value"></b></a>)
```

This matcher doesn't check the nodes order, if you want to check the node order, add `ordered` to the matcher:
```
<a>
  <b/>
  <c/>
</a> must ==/(<a><b/></c></a>).ordered
```

# Matchers applicable to Paths and Files #

Most of the time, you should try to minimize the interactions with the File System. However, sometimes this is precisely what you want to specify! The following matchers will help you specify paths and files properties.

For paths (as strings):

  * `a must beEqualIgnoringSep(b)` matches if the path for a is the same than the path for b ignoring the system specific separators
  * `a must beAnExistingPath` matches if a is the path to an existing file or directory
  * `a must beAReadablePath` matches if a is the path can be read
  * `a must beAWritablePath` matches if a is the path can be written
  * `a must beAHiddenPath` matches if a is the path starts with "."
  * `a must beAnAbsolutePath` matches if a is absolute (starting with "/" under Unix/Linux)
  * `a must beACanonicalPath` matches if a is canonical (with no relative part like "../..")
  * `a must beAFilePath` matches if a points to a File
  * `a must beADirectoryPath` matches if a points to a Directory
  * `a must havePathName(name)` matches if a has "name" as a name
  * `a must haveAsAbsolutePath(path)` matches if a has "path" has path as a absolute path. For example, "./tmp" will have "c:/tmp" absolute path if the spec is executed under c:.
  * `a must haveAsCanonicalPath(path)` matches if a has "path" has path as a canonical path. For example, "c:/tmp/dir/.." has "c:/tmp" as a canonical path
  * `a must haveParentPath(path)` matches if a has "path" as a parent. For example, "c:/tmp/dir" has "c:/tmp" as a parent
  * `a must listPaths(path1, path2)` matches if a is a directory and has path1 and path2 as children

For files:

  * `file must exist` matches if file.exists
  * `file must beReadable` matches if file.canRead
  * `file must beWritable` matches if file.canWrite
  * `file must beAbsolute` matches if file.isAbsolute
  * `file must beHidden` matches if file.isHidden
  * `file must beFile` matches if file.isFile
  * `file must beDirectory` matches if file.isDirectory
  * `file must haveName(n)` matches if file.getName == n
  * `file must haveAbsolutePath(p)` matches if file.getAbsolutePath == p
  * `file must haveCanonicalPath(p)` matches if file.getCanonicalPath == p
  * `file must haveParent(p)` matches if file.getParent == p
  * `file must haveList(p1, p2)` matches if file.list == List(p1, p2)

Note: for the above matchers, when paths are compared, separators are always ignored.

# Matchers adaptation #

A matcher can be created from another one just by adapting the kind of input it checks. Let's take an example:
```
  def beTrimmedEqual  = be_==(_:String) ^^ ((_:String).trim)
  
  // the actual value is trimmed before comparison
  " s " must beTrimmedEqual("s")
```

It is also possible to adapt both the actual and expected values with the `^^^` operator:
```
  // note that type inference may not be as good in that case
  def beTrimmedEqual = ((s: String) => be_==(s)) ^^^ ((_:String).trim)
  
  // both the actual and expected values are trimmed before comparison
  " s " must beTrimmedEqual("  s  ")
```


## Matchers composition for object graphs ##

You may want to combine several existing matchers to be able to match an entire object graph. For example, given the following class definitions:


```
trait ObjectGraph { 
  import scala.collection.mutable 

  case class Foo(val name:String) { 
    var singlebar: Bar = _ 
    val bars = mutable.Set[Bar]() 
  } 
  
  case class Bar(val id: Long)
}
```

You will define the following matchers for the classes Foo and Bar:
```
import org.specs.matcher._

trait ObjectGraphMatchers extends ObjectGraph with Matchers {
  case class matchFoo(foo: Foo) extends Matcher[Foo] {
    def apply(other: => Foo) = {
      ((beEqualTo(_:String)) ^^^ ((_:Foo).name) and
       (matchOptionalBar(_)) ^^^ ((_:Foo).singlebar) and
       (matchBar(_)).toSet ^^^ ((_:Foo).bars))(foo)(other)
    }
  }
  case class matchOptionalBar(bar: Bar) extends Matcher[Bar] {
    def apply(other: => Bar) = {
      if (bar != null && other != null)
        (matchBar(_:Bar))(bar)(other)
      else
        (beAsNullAs(_:Bar))(bar)(other)
    }
  }
  case class matchBar(bar: Bar) extends Matcher[Bar] {
    def apply(other: => Bar) = {
      ((beEqualTo(_:Long)) ^^^ ((_: Bar).id))(bar)(other)
    }
  }
}
```

In the code above:

  * `matchBar(_)` is a function returning a matcher for Bar objects. It is composed (using the `^^^` operator) with a function extracting the `singlebar` from Foo objects in order to return a new matcher for Foo object which will also extract the `singlebar` field from the object to match (`other`).

  * This could also be written using the `^^` operator as: `(((matchBar(_)) ^^ ((_:Foo).singlebar))(foo) ^^ ((_:Foo).singlebar))(other)`. In that expression, the first `^^` operator allows to select the `singlebar` field from foo to create a new Matcher of bar objects. And the second `^^` operator takes the resulting matcher and allow it to be applied to Foo objects which will have their `singlebar` field extracted

  * The `matchBar(_)` matcher is transformed to a matcher for sets of Bar objects using the toSet function. Similarly we could have applied a `toSeq` method to check for a Sequence of Bar objects

# Precise failures #

Most of the failure messages display an expected value and an actual value. But an actual value on what precisely?
```
  // will fail with "List(ticket1, ticket2) doesn't have size 3" for example
  machine.tickets must haveSize(3) // machine is a user-defined object
```

If you wish to get a more precise failure message on what's being tested you can set an alias with the "aka" method ("also known as"):

```
  // will fail with "the created tickets 'List(ticket1, ticket2)' doesn't have size 3"
  machine.tickets aka "the created tickets" must haveSize(3)
```


# xUnit assertions #

**specs** provides xUnit-like assertions to allow a smooth transition path from Test-Driven Development to Behavior-Driven Development:
```
import org.specs.matcher._

object jUnitTest extends Specification with xUnit {
  "provide the 'assertTrue' jUnit assertion" {
    assertTrue(1 == 1 + 1)
  }
}
```

  * `assertTrue`        <=> `must beTrue`
  * `assertFalse`       <=> `must beFalse`
  * `assertEquals`      <=> `must beEqual`
  * `assertSame`        <=> `mustBe`
  * `assertNotSame`     <=> `mustNotBe`
  * `assertNull`        <=> `must beNull`
  * `assertNotNull`     <=> `must notBeNull`
  * `assertArrayEquals` <=> `must ((beEqual(_:T)).toSeq`

# Use specs matchers alone #

You can use the specs matchers in the context of a JUnit test, without having to extends from a `Specification`:

```
import org.specs._
import org.junit.Test

class MyTest extends SpecsMatchers { 
  @Test 
  def mytest = { 
     10 must be_== (11) 
  } 
}
```