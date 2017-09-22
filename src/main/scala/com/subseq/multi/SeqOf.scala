package com.subseq.multi

import com.subseq.core._
import com.subseq.split.Spliterator

import scala.annotation.tailrec

/**
 * @author Kyle Brown
 */
object SeqOf {
  def apply[T, V](parser: ParsesOptionally[T, V]): ParsesOptionally[T, Seq[V]] = {
    new SimpleSeqOf[T, V](parser)
  }

  def apply[T, V](parser: ParsesOptionally[T, V], assertion: Asserts[T]): ParsesOptionally[T, Seq[V]] = {
    new DelimitedSeqOf[T, V](parser, assertion)
  }
}

/**
 *
 * @param parser
 * @tparam T the type of tokens parsed
 * @tparam V the result type
 * @author Kyle Brown
 */
class SimpleSeqOf[T, V](parser: ParsesOptionally[T, V]) extends ParsesOptionally[T, Seq[V]] {
  def parse(input: Spliterator[T]): AfterParse[T, Option[Seq[V]]] = {
    val checkpoint = input.split
    val (firstVal, remaining) = parser.parse(input)

    firstVal match {
      case Some(value) =>
        checkpoint.destroy()
        rParse(Seq[V](value), remaining)
      case None =>
        remaining.destroy()
        (None, checkpoint)
    }
  }

  @tailrec
  final def rParse(value: Seq[V], remaining: Spliterator[T]): AfterParse[T, Option[Seq[V]]] = {
    val (result, afterParse) = parser.parse(remaining)

    result match {
      case Some(contents) =>
        rParse(value :+ contents, afterParse)
      case None =>
        (Some(value), afterParse)
    }
  }

  def canFail = true
}

/**
 *
 * @param parser
 * @param assertion
 * @tparam T the type of tokens parsed
 * @tparam V the result type
 * @author Kyle Brown
 */
class DelimitedSeqOf[T,V](parser: ParsesOptionally[T, V], assertion: Asserts[T]) extends ParsesOptionally[T, Seq[V]] {
  def parse(input: Spliterator[T]): AfterParse[T, Option[Seq[V]]] = {
    val checkpoint = input.split
    val (firstVal, remaining) = parser.parse(input)

    firstVal match {
      case Some(value) =>
        val result = rParse(Seq[V](value), remaining)

        if(result.value.isDefined) {
          checkpoint.destroy()
          (result.value, result.remaining)
        } else {
          remaining.destroy()
          (None, checkpoint)
        }
      case None =>
        remaining.destroy()
        (Some(Seq[V]()), checkpoint)
    }
  }

  @tailrec
  final def rParse(value: Seq[V], remaining: Spliterator[T]): AfterParse[T, Option[Seq[V]]] = {
    val afterAssertion = assertion.parse(remaining)
    if(afterAssertion.value) {
      val (result, afterParse) = parser.parse(afterAssertion.remaining)

      result match {
        case Some(contents) =>
          rParse(value :+ contents, afterParse)
        case None =>
          (None, afterParse)
      }
    } else {
      (Some(value), afterAssertion.remaining)
    }
  }

  def canFail = false
}

