package com.subseq

import com.subseq.split.{BufferedSpliteratorHub, SeqSpliterator, Spliterator}

/**
 * @author Kyle Brown
 */
package object core {
  type AfterParse[T, V] = (V, Spliterator[T])
  case class AfterParseClass[T, V](value: V, remaining: Spliterator[T])

  implicit def tupleToCaseClass[T, V](in: AfterParse[T, V]): AfterParseClass[T, V] = {
    val (value, remaining) = in
    AfterParseClass(value, remaining)
  }

  implicit def caseClassToTuple[T, V](in: AfterParseClass[T, V]): AfterParse[T, V] = {
    (in.value, in.remaining)
  }

  implicit def seqToSpliterator[T](data: Seq[T]): Spliterator[T] =
    new SeqSpliterator[T](data,0)

  implicit def toOptionParser[T, V](parser: Parses[T, V]): ParsesOptionally[T, V] = {
    ParsesOptionally[T, V](in => {
      val (value, remaining) = parser.parse(in)
      (Some(value), remaining)
    }, value = false)
  }

  /**
    * A function which converts a Tuple2 of ParsesOptionally into a single ParsesOptionally to a tuple
   *
    * @param tuple a Tuple2 of Optional Parsers
    * @tparam T The type of the tokens being parsed
    * @tparam A The type of the optional output of the first Parser
    * @tparam B The type of the optional output of the second Parser
    * @return A new Parser which will parse to a Tuple2[A,B]
    */
  implicit def tuple2Converter[T, A, B]
      (tuple: (ParsesOptionally[T, A], ParsesOptionally[T, B])):
      ParsesOptionally[T, (A, B)] = {

    val (parseA, parseB) = tuple

    ParsesOptionally((in: Spliterator[T]) => {
      val (value1, remaining1) = parseA.parse(in)
      value1 match {
        case Some(v1) =>
          val (value2, remaining2) = parseB.parse(remaining1)
          value2 match {
            case Some(v2) => (Some(v1,v2), remaining2)
            case None => (None, remaining2)
          }
        case None => (None, remaining1)
      }
    }, value = parseA.canFail || parseB.canFail)
  }

  implicit def tuple3Converter[T, A, B, C]
      (tuple: (ParsesOptionally[T,A],ParsesOptionally[T,B],ParsesOptionally[T,C])):
      ParsesOptionally[T,(A,B,C)] = {

    val (parseA, parseB, parseC) = tuple
    val prior = tuple2Converter(parseA, parseB)

    ParsesOptionally((in: Spliterator[T]) => {
      val (valueAB, postB) = prior.parse(in)
      valueAB match {
        case Some((vA,vB)) =>
          val (valueC, postC) = parseC.parse(postB)
          valueC match {
            case Some(vC) => (Some(vA, vB, vC), postC)
            case None => (None, postC)
          }
        case None => (None, postB)
      }
    }, value = prior.canFail || parseC.canFail)
  }

  implicit def tuple4Converter[T, A, B, C, D]
      (tuple: (ParsesOptionally[T, A],ParsesOptionally[T, B],ParsesOptionally[T, C],ParsesOptionally[T, D])):
      ParsesOptionally[T,(A,B,C,D)] = {

    val (parseA, parseB, parseC, parseD) = tuple
    val prior = tuple3Converter(parseA, parseB, parseC)

    ParsesOptionally((in: Spliterator[T]) => {
      val (valueABC, postC) = prior.parse(in)
      valueABC match {
        case Some((vA,vB,vC)) =>
          val (valueD, postD) = parseD.parse(postC)
          valueD match {
            case Some(vD) => (Some((vA, vB, vC, vD)), postD)
            case None => (None, postD)
          }
        case None => (None, postC)
      }
    }, value = prior.canFail || parseD.canFail)
  }

  def c[T](token: T): Consumes[T] = {
    Consumes[T](in =>
      if(!in.hasNext) in
      else {
        val checkpoint = in.split
        if(in.next() == token) {
          checkpoint.destroy()
          in
        } else {
          in.destroy()
          checkpoint
        }
      }
    )
  }
}
