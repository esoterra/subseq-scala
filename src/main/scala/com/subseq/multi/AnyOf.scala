package com.subseq.multi

import com.subseq.core._

/**
 * @author Kyle Brown
 */
object AnyOf {
  def apply[T, V](parse1: ParsesOptionally[T, V], parsers: ParsesOptionally[T, V]*):
    ParsesOptionally[T, V] = parsers.fold(parse1)((a,b)=> a || b)
}
