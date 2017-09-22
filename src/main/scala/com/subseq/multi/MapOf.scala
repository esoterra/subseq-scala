package com.subseq.multi

import com.subseq.core._
import com.subseq.split.Spliterator

/**
 * @author Kyle Brown
 */
object MapOf {
  def apply[T, V, W](pair: ParsesOptionally[T, (V, W)], delimeter: Asserts[T]): ParsesOptionally[T, Map[V, W]] = {
    new MapOf(pair,delimeter)
  }
}

/**
 *
 * @param pair
 * @param delimeter
 * @tparam T the type of tokens parsed
 * @tparam V the result type
 * @tparam W
 * @author Kyle Brown
 */
class MapOf[T, V, W](pair: ParsesOptionally[T, (V, W)], delimeter: Asserts[T]) extends ParsesOptionally[T, Map[V, W]] {
  val parser: ParsesOptionally[T, Map[V, W]] = SeqOf(pair, delimeter).transform(_.toMap)

  override def parse(input: Spliterator[T]): AfterParse[T, Option[Map[V, W]]] = {
    parser.parse(input)
  }

  def canFail = true
}
