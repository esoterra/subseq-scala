package com.subseq.core

import com.subseq.split.Spliterator

/**
 * A companion object which allows users to more easily create Optional Parsers
 *
 * @author Kyle Brown
 */
object ParsesOptionally {
  /**
   *
   *
   * @param func
   * @param value
   * @tparam T the type of tokens parsed
   * @tparam V the result type
   */
  class SimpleOptionalParser[T, V]
      (func: (Spliterator[T]) => AfterParse[T, Option[V]], value: Boolean = true)
      extends ParsesOptionally[T, V] {

    def parse(in: Spliterator[T]): AfterParse[T, Option[V]] = func(in)
    def canFail: Boolean = value
  }

  def apply[T, V]
      (func: (Spliterator[T]) => AfterParse[T, Option[V]], value: Boolean = true):
      ParsesOptionally[T, V] = new SimpleOptionalParser[T, V](func)
}

/**
 * A subset of parsers who are not guaranteed to produce an output at all and may return None in the event that the
 * input does not represent a valid sequence of tokens
 *
 * @tparam T the type of tokens parsed
 * @tparam V the result type
 * @author Kyle Brown
 */
trait ParsesOptionally[T, V] extends Parses[T, Option[V]] {
  /**
   *
   * @param other another Optional Parser
   * @return
   */
  def orUse(other: ParsesOptionally[T, V]): ParsesOptionally[T, V] = {
    ParsesOptionally[T, V](in => {
      val checkpoint = in.split
      val (value, remaining) = this.parse(in)
      value match {
        case Some(result) =>
          checkpoint.destroy()
          (Some(result), remaining)
        case None =>
          in.destroy()
          other.parse(checkpoint)
      }
    }, value = this.canFail && other.canFail)
  }
  /** See documentation for */
  def ||(other: ParsesOptionally[T, V]): ParsesOptionally[T, V] = orUse(other)

  def ||(default: V): ParsesOptionally[T, V] = orUse(default)
  def orUse(default: V): ParsesOptionally[T, V] = {
    ParsesOptionally[T, V](in => {
      val checkpoint = in.split
      val (value, remaining) = this.parse(in)
      value match {
        case Some(result) =>
          checkpoint.destroy()
          (Some(result), remaining)
        case None =>
          in.destroy()
          (Some(default), checkpoint)
      }
    }, value = false)
  }

  def >>(consumer: Consumes[T]): ParsesOptionally[T, V] = thenDo(consumer)
  def thenDo(consumer: Consumes[T]): ParsesOptionally[T, V] = {
    ParsesOptionally(in => {
      val checkpoint = in.split
      val (value, remaining) = this.parse(in)

      if(value.isDefined) {
        checkpoint.destroy()
        (value, consumer.consume(remaining))
      } else {
        in.destroy()
        (None, checkpoint)
      }
    })
  }

  def &>(assertion: Asserts[T]): ParsesOptionally[T, V] = andThen(assertion)
  def andThen(assertion: Asserts[T]): ParsesOptionally[T, V] = {
    ParsesOptionally(in => {
      val (value, remaining1) = this.parse(in)

      if(value.isDefined) {
        val checkpoint = remaining1.split
        val (flag, remaining2) = assertion.parse(remaining1)
        if (flag) {
          checkpoint.destroy()
          (value, remaining2)
        } else {
          remaining2.destroy()
          (None, checkpoint)
        }
      } else {
        in.destroy()
        (None, remaining1)
      }
    })
  }

  def transform[W](func: V => W): ParsesOptionally[T,W] = {
    ParsesOptionally(in => {
      val (result, remaining) = parse(in)
      result match {
        case Some(v) => (Some(func(v)), remaining)
        case None => (None, remaining)
      }
    })
  }

  def canFail: Boolean
}