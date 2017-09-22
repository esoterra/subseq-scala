package com.subseq.core

import com.subseq.split.Spliterator

/**
 * A companion object which allows users to more easily create Parsers
 *
 * @author Kyle Brown
 */
object Parses {
  /**
   * A straightforward parser whose behavior is defined by a function argument
   *
   * @param func the function which defines this Parsers behavior
   * @tparam T the type of tokens parsed
   * @tparam V the result type
   */
  class SimpleParser[T,V](func: (Spliterator[T]) => AfterParse[T, V]) extends Parses[T, V] {
    def parse(input: Spliterator[T]): AfterParse[T, V] = func(input)
  }

  /**
   * A utility method for constructing a [[SimpleParser]]
   */
  def apply[T, V](func: (Spliterator[T]) => AfterParse[T, V]): Parses[T, V] = new SimpleParser[T, V](func)
}

/**
 * Parses is the Core Trait of the SubSeq framework
 *
 * It defines parsing as the action of taking a Spliterator of some type T and producing a
 * value of type V and a Spliterator which contains the remaining unparsed tokens of type T
 *
 * @author Kyle Brown
 * @tparam T the type of tokens parsed
 * @tparam V the result type
 */
trait Parses[T, V] {
  /**
   * The parse method takes a Spliterator[T] and returns a value of Type V and a Spliterator[T].
   * These can be thought of as the parsed value and the remaining tokens
   *
   * See [[AfterParse]]
   *
   * @param input a Spliterator of tokens
   * @return the produced value and remaining tokens
   */
  def parse(input: Spliterator[T]): AfterParse[T, V]
}