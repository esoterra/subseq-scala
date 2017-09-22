package com.subseq.chars

import com.subseq.core._
import com.subseq.split.Spliterator

//Drop Leading Whitespace
object IgnoreLW extends Consumes[Char] {
  def consume(input: Spliterator[Char]): Spliterator[Char] = {
    val checkpoint = input.split
    while (input.next().isWhitespace) checkpoint.next()
    input.destroy()
    checkpoint
  }

  def apply(consumer: Consumes[Char]): Consumes[Char] = IgnoreLW >> consumer
  def apply(assertion: Asserts[Char]): Asserts[Char] = IgnoreLW >> assertion
  def apply[V](parser: ParsesOptionally[Char, V]): ParsesOptionally[Char, V] = IgnoreLW >> parser
}