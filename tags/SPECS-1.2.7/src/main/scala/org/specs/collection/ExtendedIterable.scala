package org.specs.collection
import org.specs.collection.ExtendedList._

/**
 * The ExtendedIterable object offers utility methods applicable to iterables like:<ul>
 * <li><code>toStream</code> 
 * <li><code>toDeepString</code>: calls toString recursively on the iterable elements
 * <li><code>sameElementsAs</code>: compares 2 iterables recursively
 * </ul>
 */
object ExtendedIterable {
  /**
   * implicit definition to transform an iterable to an ExtendedIterable
   */
  implicit def iterableToExtended[A](xs : Iterable[A]) = new ExtendedIterable(xs)

  /**
   * See the description of the ExtendedIterable object
   */
  class ExtendedIterable[A](xs:Iterable[A]) {
    /**
     * @return a Stream created from the iterable
     */
    def toStream = Stream.fromIterator(xs.elements)

    /**
     * alias for any type of Iterable
     */
    type anyIterable = Iterable[T] forSome {type T} 
    
    /**
     * @return the representation of the elements of the iterable using the method toString recursively
     */
    def toDeepString: String = {
      "[" + xs.toList.map { x =>
        if (x.isInstanceOf[anyIterable]) x.asInstanceOf[anyIterable].toDeepString else x.toString
      }.mkString(", ") + "]" 
    }
    
    /**
     * @return true if the 2 iterables contain the same elements according to a function f 
     */
    def isSimilar[B >: A](that: Iterable[B], f: Function2[A, B, Boolean]): Boolean = {
      val ita = xs.elements
      val itb = that.elements
      var res = true
      while (res && ita.hasNext && itb.hasNext) {
        res = f(ita.next, itb.next)
      }
      !ita.hasNext && !itb.hasNext && res
    }
    
    /**
     * @return true if the 2 iterables contain the same elements recursively, in the same order 
     */
    def sameElementsAs(that: Iterable[A]): Boolean = sameElementsAs(that, (x, y) => x == y)
    def sameElementsAs(that: Iterable[A], f: (A, A) => Boolean): Boolean = {
      val ita = xs.elements.toList
      val itb = that.elements.toList
      var res = true
      (ita, itb) match {
        case (Nil, Nil) => true
        case (a: anyIterable, b: anyIterable) => {
          if (a.firstOption.isDefined && b.firstOption.isDefined) {
            val (x, y, resta, restb) = (a.head, b.head, a.drop(1), b.drop(1))
            ((f(x, y)) && (resta sameElementsAs restb)) || 
            ((resta.exists(f(_, y))  && restb.exists(f(_, x))) && (resta.removeFirst(f(_, y)) sameElementsAs restb.removeFirst(f(_, x))))
          }
          else
            false
        }
        case _ => ita == itb  
      } 
    }

    /**
     * adds the sameElementsAs method to any object in order to do that comparison recursively 
     */
    implicit def anyToSameElements(x: Any) = new AnyWithSameElements(x)

    /**
     * Class adding the <code>sameElementsAs</code> method to any object. The default implementation uses standard equality (==) 
     */
    class AnyWithSameElements(x: Any) { 
       def sameElementsAs(that: Any): Boolean = x == that 
    }
  }
}
