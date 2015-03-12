![http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png](http://upload.wikimedia.org/wikipedia/commons/thumb/0/01/Nuvola_apps_important.png/20px-Nuvola_apps_important.png) **Alpha version!**



# Forms #

Forms are the preferred way in specs to create examples in a table format, so that they are readable by business users. You can have a look at some examples [here](http://specs.googlecode.com/svn/samples/LiterateSpecifications). You can also read the [blog post](http://etorreborre.blogspot.com/2009/04/fit-like-library-for-scala.html) about it.

## Fields ##

A form is a set of Fields or Properties. Here is a simple example of a Form:
```
class Person extends Form {
  val firstName = field("First name", "Eric")
  val lastName = field("Last name", "Torreborre")

  tr(firstName)
  tr(lastName)
}
```

This form declares 2 fields attached to the Person form (with the `field` method). They have a distinct label and an initial value. They are displayed on 2 separate rows with the tr method.

The following [html](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.samples.fieldsFormSpec.html) is produced with this literate specification:
```
class fieldsFormSpec extends LiterateSpecification with Html {
 class Person extends Form {
   val firstName = field("First name", "Eric")
   val lastName = field("Last name", "Torreborre")
   tr(firstName)
   tr(lastName)
 }
 "A form with fields" is <textile>
   { new Person().toHtml }  
  </textile>
}
```

Clearly this Form is not doing much, this is just a way to create a table with input data in the `LiterateSpecification` and to be able to get its values afterwards.

## Properties ##

Now if we want to be able to set expectations on data we need to declare properties. A property is declared like a field, but with the `prop` method (inside a Form object):
```
  // "Name" is the property label, "Eric" is its actual value
  val name = prop("Name", "Eric")
```

Then it is possible to set the expected value of this property with its `apply` method (see the complete example [below](http://code.google.com/p/specs/wiki/LiterateSpecifications#Nested_forms)):
```
  name("Bob") // the enclosing Form object will check that actual value == expected value
```

### Property matcher ###

The default matcher used to check if the actual value is ok according to the expected value is `beEqual(_)`. However, you can change this matcher to another one. For example:

```
  val name = prop("Name", "Eric").matchesWith(equalToIgnoringCase(_))
  name("eric").execute.isOk // true
```


### Value formatters ###

The default formatter for Double values is `new DecimalFormat("#.###############")`, however if you want to have a specific display for some of your values, you can declare a new value formatter on a property. For example, you may want to display Iterable properties (created with the `propIterable` method) with `\` as a separator:
```
  p = PropIterable("", List("1.2", "3.4"))
  p.formatIterableWith((list:Option[Iterable[Double]]) => list match { 
     case None => "x/x/x" // this is for the case where there are no values
     case Some(l) => l.map(p.formatValue(_)).mkString("/") // using the default formatter for Doubles, then separating with "/"
  })
```

The available formatting methods are:

  * `formatWith` to format values _and_ missing values on properties
  * `formatterIs` to format existing values (an empty string is used for a missing value)
  * `formatIterableWith` to format iterable properties (for both existing _and_ missing values)
  * `iterableFormatterIs` to format only existing values on iterable properties

### Label and Value decorators ###

On any Field, Prop or Form you can set decorators for labels or values:
```
  // this will add bold tags around the formatted value
  myField.decorateValueWith((s:String) => <b>{s}</b>)

  // this will add italic tags around the formatted label
  myField.decorateLabelWith((s:String) => <i>{s}</i>)

  // and there are convenient shortcuts for italics, bold and strike
  myField.italicValue    // the value is italic
  myField.strikeLabel    // the label is strike
  myField.bold           // all is bold
  myField.boldLabel.strikeValue    // the label is bold, the value is strike

  // there are other shortcuts to set the style attributes on cells too
  myField.successValue  // the value has a success style (green)
```


## Nested forms ##

Let's have a look at a complete example, which will also demonstrate that forms can also be nested to create complex business objects:

First of all, let's define some application-level objects modeling a Person and his address:
```
trait PersonBusinessEntities {
  case class Person(firstName: String, lastName: String, address: Address, friends: List[String]) {
    def initials = firstName(0).toString + lastName(0)
  }
  case class Address(number: Int, street: String)
}
```

Then let's create a `PersonForm` which can be instantiated from an actual Person object (retrieved from a database for example):
```
trait PersonForms extends HtmlSpecification with PersonBusinessEntities {

  case class PersonForm(t: String, p: Person) extends Form(t) {
    def this(p: Person) = this("Customer", p)
    val firstName = prop("First Name", p.firstName)
    val lastName = prop("Last Name", p.lastName)
    val initials = prop("Initials", p.initials).matchesWith(beEqualToIgnoringCase(_))
    val friends =  propIterable("Friends", p.friends)
    val address = form(AddressForm("Home", p.address))

    tr(firstName, address)
    tr(lastName, initials)
    tr(friends)
  }
  case class AddressForm(t: String, address: Address) extends Form(t) {
    def this(a: Address) = this("Home", a)
    val number = prop("Number", address.number)
    val street = prop("Street", address.street)
    tr(number)
    tr(street)
  }
}
```

In the 2 forms above, we declare:

  * a title for the form `extends Form(t)`. This will replace the title by default which is  created by uncamelcasing the class name

  * some properties like `firstName`. Those properties have their actual value set from a `p` object passed as a parameter

  * an iterable property which will display its value a bit differently by separating all values with commas

  * a nested form, declared with the `form` method. Nested forms can also be declared and added to the parent form at the same time with the `.formTr` method. This is especially useful with `DataTableForms`.

  * a layout for those properties and forms on 3 different rows with the `tr` method

Finally, we can create a literate specification with this form, like this:
```
class formSampleSpec extends PersonForms with Html {
  "Forms can be used in a Literate specificatins" is <textile>

This is a Person form, checking that the initials are set properly on a Person object. { 
  val address = Address(37, "Nando-cho")
  val person = Person("Eric", "Torreborre", address, List("Jerome", "Olivier"))

  "Initials are automatically populated" inForm
   new PersonForm(person) {
    firstName("Eric")       
    initials("et")
    friends("Jerome", "Olivier")
    address.set { a =>
                  a.number(37)
                  a.street("Nando-cho") }
    lastName("Torreborre")
   }
}

  </textile>
}

```

When we use the form, we bind it with an actual person object and for each property, we declare what is the expected value. For example, we expect the `initials` property to be properly populated with the initials of the first name and the last name of the Person.

You can also notice the `set` method on a form which provides a way to set a nested form properties in the same block.

When the specification is executed, the result will look like [this](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.samples.formSampleSpec.html).

## Forms execution ##

There are 2 way to relate the form successes/failures to its specification:

  * either you use the `inForm` method as seen above. This will create a new example and one failure if the form fails (but still all the form issues will be displayed)

  * or you can just declare the form and call the `report` method on it. This will execute the form and attach each property cell as a separate example on the enclosing specification:
```
This is a Person form, checking that the initials are set properly on a Person object. { 
  val address = Address(37, "Nando-cho")
  val person = Person("Eric", "Torreborre", address, List("Jerome", "Olivier"))

   new PersonForm(person) {
     firstName("Eric")       
     initials("et")
     friends("Jerome", "Olivier")
     address.set { a =>
                   a.number(37)
                   a.street("Nando-cho")}
     lastName("Torreborre")
   }.report
}
```



## Forms layout ##

### Predefined layouts ###
Some special methods can be used to help the layout of Forms (see the scaladoc for the precise methods description):

  * **p(properties/forms)**: adds all the properties/forms on a new table row, with an empty row above as a separator
  * **th1(titles)**: adds a small box with a caption inside a row
  * **th2(titles)**: adds a bold center header on a new row
  * **th3(titles)**: adds a bold left-aligned header on a new row
  * **th3(title or list of titles, status)**: adds a bold left-aligned header on a new row, colored with a
  * **tabs** You can display 2 forms on 2 different tabs with the `tabs` and `tab` classes:

```
class tabsSpec extends HtmlSpecification("Tabs sample") with JUnit {
 class ClubMember extends Form {
   new tabs() {
     new tab("Contact details") {
       tr(field("First name", "Eric"))
       tr(field("Last name", "Torreborre"))
     }
     new tab("Sports") {
       th2("Sport", "Years of practice")
       tr(field("Squash", 10))
       tr(field("Tennis", 5))
       tr(field("Windsurf", 2))
     }
   }
 }
 "A form with tabs" is <textile>
   { new ClubMember().toHtml }  
  </textile>
}
```

#### Tabs helper methods ####

  * `tabs(title, forms: Form*)` create tabs with an overall title and the title of each form on each tab.
  * `toTabs(title, forms: (String, Form)*)` create tabs with a specific title for each tab

See [here](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.samples.tabsSpec.html) for the corresponding Html report.

### Override an existing layout ###

When working with an existing form you may wish to:

  * remove some properties from the layout to simplify it. In this case you can use the `include/exclude` methods to include only some properties or exclude some properties from the display:
```
  // the customer form class provides a default layout
  new CustomerForm {
    // exclude the age property
    exclude(age)
  }
```

  * reset the whole existing layout with `resetLayout()` and provide a new one.

  * override the `layout(formRows: List[Seq[LabeledXhtml]])` method and provide a different layout:
```
  override def layoutRows(formRows: List[Seq[LabeledXhtml]]) = {
    th1(field("Customer id", customer.getId))
    trs(new Form {
        new tabs {
          new tab("Orders") {
            trs(formRows)
          }
          new tab("Customer details") {
            customerDetails.foreach((p: Pair[String, Boolean]) => tr(field(p._1, p._2)))
          }
       }
    }.rows)
  }
```

# Special forms #

## `DataTable` form ##

`DataTables` can also be used in a Form. Here is an example:
```
  new TradePrice {
    
    "Value date"    | "NPV_PAY_NOTIONAL"    | "NPV_REC_NOTIONAL"    |
    "6/1/2007"      ! -1732.34              ! 0.0                   |
    "4/30/2008"     ! -580332.88            ! 0.0                   | { (valueDate: String, pay: Double, rec: Double) =>

      tr(valueDate, prop(pricePay(valueDate))(pay), prop(priceRec(valueDate))(rec))
    }
  }.report
```

In this example:

  * the `DataTable` is not directly executed (there is no `|>` sign on the right border of the table) but it is reported to the enclosing Specification with `report`.

  * the function executed on each row of the `DataTable` is actually building the Form rows (with the `tr` method)

  * there is an implicit conversion converting a value of type T to a `Field[T]`, so the valueDate can directly be added to the form row `tr(valueDate,....)`

  * we want to check that the expected values declared on the 2nd and 3rd columns are correct, so we create properties on the fly where the actual value is the computed price and the expected value is the value declared inside the table

  * no labels are declared for the fields and properties, they're automatically taken from the `DataTable` header

## Table forms ##

Actually a `DataTableForm` is just a special case of a `TableForm`. A `TableForm` is a form containing only `LineForms`. And a `LineForm` is just a set of properties which should be displayed on a row, without their labels (the labels will be used to create the table header).

The `DataTableForm` example above could then be rewritten as:
```
  class PayLine(vDate: String, p: Double, r: Double) extends LineForm {
    val valueDate = field("Value date", vDate)
    val pay = prop("NPV_PAY_NOTIONAL", pricePay(valueDate))(p)
    val rec = prop("NPV_REC_NOTIONAL", priceRec(valueDate))(r)
  }
  new TableForm {
    tr(PayLine("6/1/2007", -1732.34, 0.0))
    tr(PayLine("4/30/2008", -580332.88, 0.0))
  }.report
```

You can note in this example that there is also an implicit conversion between a Field and its value so you can write `pricePay(valueDate)` instead of `pricePay(valueDate.get)`

## `SeqForm` and `EntityLine` form ##

Those 2 forms are to be used when you want to declare a form where each row is a separate "Entity" (modeling a business object for example) and where the whole table declares a sorted sequence of expected entities. Upon execution, the missing or supplementary rows will be reported. Here is an example:
```
  case class CustomerLine(name: String, age: Int) extends EntityLineForm[Customer] {

    // the prop method accepts a function here, taking the proper attribute on the "Entity"
    prop("Name", (_:Customer).getName)(name)
    prop("Age", (_:Customer).getAge)(age)
  }
  class Customers(actualCustomers: Seq[Customer]) extends SeqForm(actualCustomers)

  // example usage
  new Customers(listFromTheDatabase) {
    tr(CustomerLine("Eric", 36))
    tr(CustomerLine("Bob", 27))
  }
```

## `BagForm` and `EntityLine` form ##

The `BagForm` is similar to a `SeqForm` but it doesn't expect a sequence of expected rows to match a sequence of actual entities. It tries instead to find the best match between the set of expected rows and the set of actual entities based on the number of failing properties for each row.

_Note however that the underlying algorithm for finding the best match is a bit naive and may not find the best combination based on the expected and actual rows order. This will evolve in subsequent releases._

You can see an example of it [here](http://specs.googlecode.com/svn/samples/LiterateSpecifications/org.specs.samples.bagFormSpec.html)

### Missing actual rows ###

By default, a `BagForm` will report missing actual rows and a failure if some actual rows can't be matched against expected raws.

You can switch this behavior off with `isIncomplete`:
```
  // there are 2 actual persons to match but one only is ok
  val form = new DataTableBagForm("Persons", actual) {
    "Name" | "Age" |
    "Bob"  ! 40    | { (name:String, age:Int) =>
      tr(PersonLine(name, age))
    }
  }
  form.isIncomplete.execute.isOk must be(true)
```