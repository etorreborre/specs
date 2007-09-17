package scala.specs

object Sugar extends Sugar

/**
 * Synctactic sugar for specifications. Since it makes heavy uses of implicit definitions,
 * the name reminds that it must be used with caution
 */
trait Sugar {
  
  /** alias for the value true. Allows to write <code> myObject.status mustBe ok </code>*/
  val ok = true
  
  /** alias for the value false. Allows to write <code> myObject.status mustBe ko </code>*/
  val ko = false

  /**
    This allows the following declarations:
    val list0 = ()
    val list1 = ("a")
    val list2 = ("a", "b")
    Produced with the following ruby code:
    (1..19).each do |i|
      puts "implicit def productToList#{i}[T](t: Product#{i}[#{(1..i).map{|x|"T"}.join(", ")}]) = List(#{(1..i).map {|j| "t._" + j.to_s}.join(", ")})"
     end
  */
  implicit def unitToList(a: Unit) = List()
  implicit def anyToList[T](a: T) = List[T](a)
  implicit def productToList1[T](t: Product1[T]) = List(t._1)
  implicit def productToList2[T](t: Product2[T, T]) = List[T](t._1, t._2)
  implicit def productToList3[T](t: Product3[T, T, T]) = List(t._1, t._2, t._3)
  implicit def productToList4[T](t: Product4[T, T, T, T]) = List(t._1, t._2, t._3, t._4)
  implicit def productToList5[T](t: Product5[T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5)
  implicit def productToList6[T](t: Product6[T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6)
  implicit def productToList7[T](t: Product7[T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7)
  implicit def productToList8[T](t: Product8[T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8)
  implicit def productToList9[T](t: Product9[T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9)
  implicit def productToList10[T](t: Product10[T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10)
  implicit def productToList11[T](t: Product11[T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11)
  implicit def productToList12[T](t: Product12[T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12)
  implicit def productToList13[T](t: Product13[T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13)
  implicit def productToList14[T](t: Product14[T, T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14)
  implicit def productToList15[T](t: Product15[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15)
  implicit def productToList16[T](t: Product16[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16)
  implicit def productToList17[T](t: Product17[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17)
  implicit def productToList18[T](t: Product18[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18)
  implicit def productToList19[T](t: Product19[T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T, T]) = List(t._1, t._2, t._3, t._4, t._5, t._6, t._7, t._8, t._9, t._10, t._11, t._12, t._13, t._14, t._15, t._16, t._17, t._18, t._19)

  /** 
   * This implicit definition allows to write short loops, ruby-style:
   * <code> 3.times { i => doThis() } </code>. 
   * Warning: an integer variable i must be declared otherwise there will be a runtime exception
   */
  implicit def integerToRange(n: Int): RangeInt = new RangeInt(n)
  case class RangeInt(n: Int) { 
    def times[T](f: (Int) => T)  = for (i <- 1 to n) f(i) 
  }
  
  /** 
   * This implicit definition allows to print any object to the console with:
   * <code> myObject.pln </code> or <code> myObject.println </code>  
   */
  implicit def anyPrintable[T](a: T) = {
    new Object { 
      def println = Console.println(a)
      def pln = Console.println(a) 
    }
  }

}

