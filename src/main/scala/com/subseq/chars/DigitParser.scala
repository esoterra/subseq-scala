package com.subseq.chars

import com.subseq.core._
import com.subseq.split.Spliterator

object DigitParser extends ParsesOptionally[Char, Int] {
  override def parse(input: Spliterator[Char]): AfterParse[Char, Option[Int]] = {
    if(!input.hasNext) (None, input)
    else {
      val checkpoint = input.split
      val char = input.next()
      if(char.isDigit) {
        checkpoint.destroy()
        (Some(char.asDigit), input)
      } else {
        input.destroy()
        (None, checkpoint)
      }
    }
  }
  def canFail = true
}
