package com.subseq.chars

import com.subseq.core._
import com.subseq.split.Spliterator

object StringParser extends ParsesOptionally[Char, String] {
  val escaped = Seq('\"', '\\', '/', 'b', 'f', 'n', 'r', 't')

  override def parse(input: Spliterator[Char]): AfterParse[Char, Option[String]] = {
    val checkpoint = input.split
    val buf = new StringBuilder

    if(input.next() != '"') {
      input.destroy()
      (None, checkpoint)
    } else {
      var flag = true
      var c = input.next()

      while(flag) {
        if(c == '\\') {
          buf += c
          c = input.next()

          if(escaped.contains(c)) {
            buf += c
          } else if(c.isUnicodeIdentifierStart) {
            buf += c
            for(_ <- 1 to 4) {
              c = input.next()

              if(!c.isUnicodeIdentifierPart && input.hasNext) {
                buf += c
              } else {
                flag = false
              }
            }
          } else {
            flag = false
          }

        } else if(c == '"') {
          checkpoint.destroy()
          return (Some(buf.mkString), input)
        } else {
          buf += c
        }

        c = input.next()
      }
      input.destroy()
      (None, checkpoint)
    }
  }

  def canFail = false
}
