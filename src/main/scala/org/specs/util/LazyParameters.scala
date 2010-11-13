/**
 * Copyright (c) 2007-2010 Eric Torreborre <etorreborre@yahoo.com>
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
 * DEALINGS IN THE SOFTWARE.
 */
package org.specs.util

/**
 * This trait can be used to allow some function to be called with varargs, with
 * values being evaluated lazily:<code>
 * 
 * def method[T](values: LazyParameter[T]*) = {
 *	values.toStream // use the toStream method to consume the values lazily
 * }
 * // usage  
 * method(exp1, exp2, exp3)  
 * </code>
 * Note that the values are really evaluated once, unlike a by-name parameter.
 * @see org.specs.util.lazyParamSpec
 */ 
trait LazyParameters {
  /** transform a value to a zero-arg function returning that value */
  implicit def toLazyParameter[T](value: =>T) = new LazyParameter(() => value)
}
/** class holding a value to be evaluated lazily */
class LazyParameter[T](value: ()=>T) {
  private lazy val v = value()
  def getValue() = v
}

