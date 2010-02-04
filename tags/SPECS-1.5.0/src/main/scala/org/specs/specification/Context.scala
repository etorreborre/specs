/**
 * Copyright (c) 2007-2009 Eric Torreborre <etorreborre@yahoo.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of
 * the Software. Neither the name of specs nor the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written permission.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF
 * CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS INTHE SOFTWARE.
 */
package org.specs
import org.specs.util._
import org.specs.util.ExtendedString._
import scala.xml._
import org.specs.matcher._
import scala.collection.mutable._
import org.specs.runner._
import org.specs.matcher.MatcherUtils._
import org.specs.SpecUtils._
import org.specs.specification._
import org.specs.ExtendedThrowable._
import scala.reflect.Manifest
import org.specs.execute._

/**
 * This traits adds before / after capabilities to specifications, so that a context can be defined for
 * each system under test being specified.
 */
trait BeforeAfter extends BaseSpecification { outer =>
  /** 
   * adds a "before" function to the last sus being defined 
   */
  private def usingBefore(actions: () => Any) = currentSus.before = stackActions(actions, currentSus.before)
  
  /** @return a function with actions being executed after the previous actions. */
  private def stackActions(actions: () => Any, previousActions: Option[() => Any]) = {
    Some(() => {
      previousActions.map(a => a())
      actions()
    })
  }
  /** @return a function with actions being executed after the previous actions. */
  private def reverseStackActions(actions: () => Any, previousActions: Option[() => Any]) = {
    Some(() => {
      actions()
      previousActions.map(a => a())
    })
  }

  /** adds a "before" function to the last sus being defined */
  def doBefore(actions: =>Any) = usingBefore(() => actions)

  /** adds a "firstActions" function to the last sus being defined */
  def doFirst(actions: =>Any) = currentSus.firstActions = stackActions(() => actions, currentSus.firstActions)

  /** adds a "lastActions" function to the last sus being defined */
  def doLast(actions: =>Any) = currentSus.lastActions = reverseStackActions(() => actions, currentSus.lastActions)

  /** adds a "beforeSpec" function to the current specification */
  def doBeforeSpec(actions: =>Any) = beforeSpec = stackActions(() => actions, beforeSpec)

  /** adds a "afterSpec" function to the current specification */
  def doAfterSpec(actions: =>Any) = afterSpec = reverseStackActions(() => actions, afterSpec)

  /** 
   * adds an "after" function to the last sus being defined 
   */
  private def usingAfter(actions: () => Any) = currentSus.after = reverseStackActions(actions, currentSus.after)

  /** 
   * adds an "after" function to the last sus being defined 
   */
  def doAfter(actions: =>Any) = usingAfter(() => actions)

  /** 
   * repeats examples according to a predicate 
   */
  def until(predicate: =>Boolean) = {
    currentSus.untilPredicate = Some(() => {
      predicate || currentSus.untilPredicate.map(p => p()).getOrElse(false)
    })
  }

  /** 
   * Syntactic sugar for before/after actions.<p>
   * Usage: <code>"a system" should { createObjects.before
   *  ...
   * </code>
   */
  implicit def toShortActions(actions: =>Unit) = new ShortActions(actions)

  /** 
   * Syntactic sugar for before/after actions.<p>
   * Usage: <code>"a system" should { createObjects.before
   *  ...
   * </code>
   */
  class ShortActions(actions: =>Unit) {
    def before = doBefore(actions)
    def after = doAfter(actions)
    def doFirst: Unit = outer.doFirst(actions)
    def doLast: Unit = outer.doLast(actions)
    def beforeSpec = outer.doBeforeSpec(actions)
    def afterSpec = outer.doAfterSpec(actions)
  }
}
trait Contexts extends BeforeAfter {
  /** Factory method to create a context with beforeAll only actions */
  def contextFirst(actions: => Any) = new Context { first(actions) }

  /** Factory method to create a context with before only actions */
  def beforeContext(actions: => Any) = new Context { before(actions) }

  /** Factory method to create a context with before only actions and an until predicate */
  def beforeContext(actions: => Any, predicate: =>Boolean) = new Context { before(actions); until(predicate()) }

  /** Factory method to create a context with after only actions */
  def afterContext(actions: => Any) = new Context { after(actions) }

  /** Factory method to create a context with afterAll actions */
  def contextLast(actions: => Any) = new Context { last(actions) }

  /** Factory method to create a context with after only actions and an until predicate */
  def afterContext(actions: => Any, predicate: =>Boolean) = new Context { after(actions); until(predicate()) }

  /** Factory method to create a context with after only actions */
  def context(b: => Any, a: =>Any) = new Context { before(b); after(a) }

  /** Factory method to create a context with before/after actions */
  def globalContext(b: => Any, a: =>Any) = new Context { first(b); last(a) }

  /** Factory method to create a context with before/after actions and an until predicate */
  def context(b: => Any, a: =>Any, predicate: =>Boolean) = new Context { before(b); after(a); until(predicate()) }

  /** 
   * Syntactic sugar to create pass a new context before creating a sus.<p>
   * Usage: <code>"a system" ->(context) should { 
   *  ..
   * </code>
   * In that case before/after actions defined in the context will be set on the defined sus.
   */
  implicit def whenInContext(s: String) = ToContext(s) 
  /** 
   * Syntactic sugar to create pass a new context before creating a sus.<p>
   * Usage: <code>"a system" ->(context) should { 
   *  ...
   * </code>
   * In that case before/after actions defined in the context will be set on the defined sus.
   */
  case class ToContext(desc: String) {
    def ->-[S](context: Context): Sus = {
      if (context == null) throw new NullPointerException("the context is null")
      specifySus(context, desc)
    } 
  }
  private def specifySus(context: Context, desc: String): Sus = {
    if (context == null) throw new NullPointerException("the context is null")
    val sus = specify(desc)
    doFirst(context.firstActions())
    doBefore(context.beforeActions())
    doAfter(context.afterActions())
    doLast(context.lastActions())
    until(context.predicate())
    sus
  }
}
/** 
 * Case class holding before and after functions to be set on a system under test.<p>
 * Context objects are usually created using the factory methods of the Contexts trait:<pre>
 * 
 * // this method returns a context object which can be passed to a System under test (with "a system" ->(context) should {... )
 * // so that initSystem is done before each example and so that each example is repeated until enoughTestsAreExecuted is true 
 * beforeContext(initSystem).until(enoughTestsAreExecuted)
 * </pre>
 */
abstract class SystemContext[S] extends Context with java.lang.Cloneable {
  var systemOption: Option[S] = None
  def init = systemOption = Some(newSystem)
  def system: S = systemOption match {
    case None => throw new FailureException("There is no system set on the context")
    case Some(s) => s
  }
  def newSystem: S
  def before(s: S) = {}
  def newInstance: SystemContext[S] = this.clone.asInstanceOf[SystemContext[S]]
}
trait SystemContexts extends Contexts {
  class SystemContextCaller(s: String) {
    def withSome[T](context: SystemContext[T])(f: T => Any): Example = into(f)(context)
    def withAn[T](context: SystemContext[T])(f: T => Any): Example = into(f)(context)
    def withA[T](context: SystemContext[T])(f: T => Any): Example = into(f)(context)
    def into[T](f: T => Any)(implicit context: SystemContext[T]): Example = {
      forExample(s).in(f(context.newSystem))
    }
  }
  implicit def forExampleWithSystemContext(s: String) = new SystemContextCaller(s)
  def systemContext[T](t: =>T) = new SystemContext[T] {
    def newSystem = t
  }
  implicit def whenInSystemContext(s: String) = ToSystemContext(s) 

  case class ToSystemContext(desc: String) {
    def definedAs[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def isAn[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def isA[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def whenIn[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def whenIs[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def whenHas[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def whenHaving[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
    def when[S](context: SystemContext[S]): Sus = specifySusWithContext(context, desc)
  }
  
  private def specifySusWithContext[S](context: SystemContext[S], desc: String): Sus = {
    if (context == null) throw new NullPointerException("the context is null")
    val sus = specify(context, desc)
    doFirst(context.firstActions())
    doLast(context.lastActions())
    until(context.predicate())
    sus
  }

}

object SystemContext {
  def apply[S](s: => S) = new SystemContext[S] {
    def newSystem = s
  }
}
case class Context() {
  var firstActions: () => Any = () => () 
  var lastActions: () => Any = () => ()
  var beforeActions: () => Any = () => () 
  var afterActions: () => Any = () => ()
  var predicate: () => Boolean = () => true
  def before(actions: =>Any) = { beforeActions = () => actions; this }
  def after(actions: =>Any) = { afterActions = () => actions; this }
  def first(actions: =>Any) = { firstActions = () => actions; this }
  def last(actions: =>Any) = { lastActions = () => actions; this }
  def until(predicate: =>Boolean) = { this.predicate = () => predicate; this }
}

