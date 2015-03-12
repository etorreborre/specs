This page lists tips on errors you may encounter while using **specs**.



# NoSuchMethodError #

You may have dependencies issues. Please check the version numbers of all the libraries you're using: [dependencies](http://code.google.com/p/specs/wiki/RunningSpecs#Dependencies)

# Strange compilation error #

The following spec would raise a compilation error:
```
"My Thing" should {
  "do something" {
     ...possibly long setup...
     something must ... other // matcher
  }
```
This would generate:
```
> "message without in" { "value" must beEqual("anotherValue") }
> error: type mismatch;
> found   : org.specs.specification.Result[java.lang.String]
> required: Int
>       "message without in" { "value" must beEqual("anotherValue") }
```

This is because there is a missing "in" after the example description, so everything inside the accolade is expected to be an Int used to access one character of "do something" (see [issue 22](https://code.google.com/p/specs/issues/detail?id=22))

# Skipped example #

This example would appear as "skipped" in the reports:
```
  "my example" in {
    doSomething()     // appears as skipped
  }
```

The example shows as 'skipped' because it is a warning that it is not specifying any expectations. We're taking the view here that expecting a piece of code _not to throw an exception_ is a sign that your example is incomplete as a specification of the intended behavior. However if that's really what you mean, you can declare that by using `isExpectation`:
```
  "my example" in {
    doSomething().isExpectation     // appears as passed if no exception is thrown
  }
```

If you don't like this default behavior you can override it by changing your specs configuration: http://code.google.com/p/specs/wiki/RunningSpecs#Override_specs_default_behavior

# The objects to compare have the same toString value #

For example you're comparing `List("1")` to `List(1)` using `must_==`. After the fix for [issue 141](https://code.google.com/p/specs/issues/detail?id=141) you will get a warning saying `Values have the same string representation but possibly different types like List[Int] and List[String]`.

# My specification is leaking some resources #

This may be a case like [issue 150](https://code.google.com/p/specs/issues/detail?id=150) where specs is executing examples in isolation by cloning the specification but not always calling doAfter to clean the resources. You can read more [here](http://code.google.com/p/specs/wiki/DeclareSpecifications?ts=1277341088&updated=DeclareSpecifications#Warning!) and [there](http://code.google.com/p/specs/wiki/DeclareSpecifications?ts=1277341088&updated=DeclareSpecifications#With_nested_examples).