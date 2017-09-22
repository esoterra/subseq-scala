package com.subseq.chars

import com.subseq.core._
import com.subseq.multi.{AnyOf, SeqOf}
import com.subseq.split.Spliterator

/**
 * This parser should parse all valid number strings and reject all invalid strings according to the json
 * standard on json.org; with the exception of leading zeros which it will allow, in contrast with the standard.
 *
 * @author Kyle Brown
 */
object NumberParser extends ParsesOptionally[Char, Double] {
  val intParser = SeqOf(DigitParser).transform(_.fold(0)((a, b)=>a*10+b))
  val decParser = SeqOf(DigitParser).transform(_.foldRight(0.0)((a, b)=>(a+b)/10.0))

  val expParser: ParsesOptionally[Char, Double] =
    ('e' || 'E') &> AnyOf(
      '-' &> intParser.transform(a => Math.pow(10.0, -1*a)),
      c('+') >> intParser.transform(a => Math.pow(10.0, a))
    )

  val numParser: ParsesOptionally[Char,Double] =
    (('-' -> -1) || 1, intParser,  ('.' &> decParser) || 0, expParser || 1)
      .transform(a => a._1*(a._2+a._3)*a._4)

  override def parse(input: Spliterator[Char]): AfterParse[Char, Option[Double]] = numParser.parse(input)

  def canFail = true
}