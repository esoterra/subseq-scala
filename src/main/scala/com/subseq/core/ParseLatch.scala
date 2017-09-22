package com.subseq.core

import com.subseq.split.Spliterator

/**
 *
 * @tparam T the type of tokens parsed
 * @tparam V the result type
 */
class ParseLatch[T,V] extends ParsesOptionally[T,V]{
  private var parser: ParsesOptionally[T,V] = ParsesOptionally(in=>(None,in))

  def set(newVal: ParsesOptionally[T,V]): ParsesOptionally[T,V] = {
    parser = newVal
    this
  }

  override def parse(input: Spliterator[T]): AfterParse[T, Option[V]] = {
    parser.parse(input)
  }

  def canFail = false
}
