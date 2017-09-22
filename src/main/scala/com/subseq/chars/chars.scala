package com.subseq

import com.subseq.core._
import com.subseq.split._

import scala.collection.immutable.StringOps

package object chars {
  implicit def stringToSpliterator(s: String): Spliterator[Char] = seqToSpliterator((s:StringOps).toSeq)

  implicit def charToAssertion(c: Char): Asserts[Char] = {
    Asserts[Char](in => {
      if(!in.hasNext) {
        (false, in)
      } else {
        val checkpoint = in.split
        val nextChar = in.next

        if (nextChar == c) {
          checkpoint.destroy()
          (true, in)
        } else {
          in.destroy()
          (false, checkpoint)
        }
      }
    })
  }

  implicit def stringToAssertion(s: String): Asserts[Char] = {
    val cIter = (s:StringOps).toIterator
    Asserts[Char](in => {
      val checkpoint = in.split

      while(in.hasNext && cIter.hasNext && (in.next() == cIter.next())) {}

      if (cIter.hasNext) {
        in.destroy()
        (true, checkpoint)
      } else {
        checkpoint.destroy()
        (false, in)
      }
    })
  }
}
