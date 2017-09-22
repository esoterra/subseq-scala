package com.subseq.core

import com.subseq.split.Spliterator

/**
 * A companion object which allows users to more easily create Assertions
 *
 * @author Kyle Brown
 */
object Asserts {

  /**
   * An Assertion which uses the specified function for its parsing method
   *
   * @param func the parsing function
   * @tparam T the type of the input tokens which are checked by this Assertion
   */
  class SimpleAssertion[T](func: (Spliterator[T]) => AfterParse[T, Boolean]) extends Asserts[T] {
    def parse(in: Spliterator[T]): AfterParse[T, Boolean] = func(in)
  }

  /**
   * A utility method for constructing Assertions
   *
   * @param func the parsing function
   * @tparam T the type of the input tokens which are checked by this Assertion
   * @return the new Assertion
   */
  def apply[T](func: (Spliterator[T]) => AfterParse[T, Boolean]): Asserts[T] = new SimpleAssertion[T](func)
}

/**
 * A subset of Parsers who produce a boolean value as their output and can be thought of as asserting some condition
 * with respect to the input tokens.
 *
 * @tparam T the type of the input tokens which are checked by this Assertion
 * @author Kyle Brown
 */
trait Asserts[T] extends Parses[T, Boolean] {
  /**
   * An Assertion andThen another Assertion is a compositional operation which creates a new Assertion that applies
   * the first Assertion and if it succeeds the second Assertion. The new Assertion only succeeds if both of the
   * Assertions succeed.
   *
   * @param other the other Assertion that should be checked after this one
   * @return a new Assertion which represents the combination of the two Assertions
   */
  def andThen(other: Asserts[T]): Asserts[T] = {
    Asserts(in => {
      val (flag, remaining) = this.parse(in)
      if(flag) {
        other.parse(remaining)
      } else {
        (false, remaining)
      }
    })
  }
  /** See documentation for [[andThen()]]*/
  def &>(other: Asserts[T]): Asserts[T] = andThen(other)

  /**
   * An Assertion andThen an Optional Parser is a compositional operation which creates a new Optional Parser which
   * applies the Assertion and then if it succeeds applies the Optional Parser. If the Assertion fails the new Parser
   * returnsf None.
   *
   * @param optionalParser the Optional Parser which should be evaluated if the Assertion succeeds
   * @tparam V the type of the Optional output of the specified Optional Parser
   * @return a new Optional Parser which behaves like optionalParser except that it is applied after and depends on
   *         this Assertion
   */
  def andThen[V](optionalParser: ParsesOptionally[T, V]): ParsesOptionally[T, V] = {
    ParsesOptionally(in => {
      val (flag, remaining) = this.parse(in)
      if(flag) optionalParser.parse(remaining)
      else (None, remaining)
    })
  }
  /** See documentation for [[andThen[V](ParsesOptionally[T,V])]]*/
  def &>[V](other: ParsesOptionally[T, V]): ParsesOptionally[T, V] = andThen(other)

  /**
   * "An Assertion orUse another Assertion" is a compositional operation which creates a new Assertion that applies
   * the first Assertion and if it fails tries again with the second Assertion. The new Assertion succeeds if
   * either of the two Assertions succeeds.
   *
   * @param other the other Assertion that should be checked if this one fails
   * @return a new Assertion which represents the logical OR of the two Assertions
   */
  def orUse(other: Asserts[T]): Asserts[T] = {
    Asserts(in => {
      val (flag, remaining) = this.parse(in)
      if(flag) {
        (true, remaining)
      } else {
        other.parse(remaining)
      }
    })
  }
  /** See documentation for [[orUse()]]*/
  def ||(other: Asserts[T]): Asserts[T] = orUse(other)

  /**
   * "An Assertion yields a value" is a compositional operation which creates a new Optional Parser which returns
   * the specified value if the Assertion succeeds and returns None otherwise
   *
   * @param value the resulting value
   * @tparam W the type of the given value
   * @return a new Optional Parser which represents a parser which succeeds the specified output if this Assertion
   *         succeeds
   */
  def yields[W](value: W): ParsesOptionally[T, W] = {
    ParsesOptionally(in => {
      val (flag, remaining) = this.parse(in)
      if(flag) (Some(value), remaining)
      else (None, remaining)
    })
  }
  /** See documentation for [[yields()]]*/
  def ->[W](value: W): ParsesOptionally[T, W] = yields(value)

  def yields[W](whenTrue: W, whenFalse: W): ParsesOptionally[T, W] = {
    ParsesOptionally(in => {
      val checkpoint = in.split
      val (flag, remaining) = this.parse(in)
      if(flag) {
        checkpoint.destroy()
        (Some(whenTrue), remaining)
      } else {
        remaining.destroy()
        (Some(whenFalse), checkpoint)
      }
    }, value = false)
  }
}
