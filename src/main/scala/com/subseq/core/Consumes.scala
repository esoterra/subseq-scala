package com.subseq.core

import com.subseq.split.Spliterator

/**
 * A companion object which allows users to more easily create Consumers
 *
 * @author Kyle Brown
 */
object Consumes {

  /**
   * A Consumer which wraps the specified function
   *
   * @param func a function which defines the operation from Spliterator[T] => Spliterator[T]
   * @tparam T the type of tokens consumed
   */
  class SimpleConsumer[T](func: (Spliterator[T]) => Spliterator[T]) extends Consumes[T] {
    def consume(in: Spliterator[T]): Spliterator[T] = func(in)
  }

  /**
   * A Consumer that consumes tokens as long as the predicate is true for the next token
   *
   * @param predicate a predicate to apply to a token to determine whether or not it is consumed
   * @tparam T the type of tokens consumed
   */
  class PredicateConsumer[T](predicate: T => Boolean) extends Consumes[T] {
    def consume(in: Spliterator[T]): Spliterator[T] = {
      val checkpoint = in.split
      while (predicate(in.next())) checkpoint.next()
      in.destroy()
      checkpoint
    }
  }

  /**
   * A utility method which constructs a SimpleConsumer
   *
   * @param func a function which defines the operation from Spliterator[T] => Spliterator[T]
   * @tparam T the type of tokens consumed
   * @return the new SimpleSpliterator created from the function
   */
  def apply[T](func: (Spliterator[T]) => Spliterator[T]): Consumes[T] = new SimpleConsumer[T](func)
}

/**
 * Consumers are a subset of Parsers who produce no value and therefore have value type Null
 *
 * As a result the only effect Consumers can have is to move the Spliterator forward before returning it,
 * effectively skipping or "consuming" tokens
 *
 * @tparam T the type of tokens consumed
 * @author Kyle Brown
 */
trait Consumes[T] extends Parses[T, Null] {
  /**
   * @param input the series of tokens to be parsed
   * @return a new series of tokens
   */
  def consume(input: Spliterator[T]): Spliterator[T]

  /**
   * Parses to null and applies consume to the input tokens
   * @param input the series of tokens to be parsed
   * @return the null value and the remaining tokens
   */
  def parse(input: Spliterator[T]): AfterParse[T, Null] = (null, this.consume(input))

  /**
   * Creates a new Consumer whose consume method applies this consumer then runs the other consumer
   *
   * @param other a different Consumer
   * @return the new combined Consumer
   */
  def thenDo(other: Consumes[T]): Consumes[T] = Consumes((in: Spliterator[T]) => other.consume(this.consume(in)))
  /** See documentation for [[thenDo(Consumes[T])]]*/
  def >>(other: Consumes[T]): Consumes[T] = thenDo(other)

  /**
   * Creates a new Assertion that applies the Consumer then runs the Assertion
   *
   * @param assertion a different Consumer
   * @return the new combined Assertion
   */
  def thenDo(assertion: Asserts[T]): Asserts[T] = Asserts(in => assertion.parse(this.consume(in)))
  /** See documentation for [[thenDo(Asserts[T])]]*/
  def >>(assertion: Asserts[T]): Asserts[T] = thenDo(assertion)

  /**
   * Creates a new Optional Parser that applies the Consumer then runs the Optional Parser
   *
   * @param parser an Optional Parser
   * @tparam V the optional return type of the parser
   * @return the new combined Optional Parser
   */
  def thenDo[V](parser: ParsesOptionally[T,V]): ParsesOptionally[T,V] = ParsesOptionally(in => parser.parse(this.consume(in)))
  /** See documentation for [[thenDo(ParsesOptionally[T, V])]]*/
  def >>[V](parser: ParsesOptionally[T, V]): ParsesOptionally[T, V] = thenDo(parser)

  /**
   * Creates a new Parser that applies the Consumer then runs the Parser
   *
   * @param parser a Parser
   * @tparam V the return type of the parser
   * @return the new combined Parser
   */
  def thenDo[V](parser: Parses[T,V]): Parses[T,V] = Parses(in => parser.parse(this.consume(in)))
  /** See documentation for [[thenDo(Parses[T, V])]]*/
  def >>[V](parser: Parses[T, V]): Parses[T, V] = thenDo(parser)
}
