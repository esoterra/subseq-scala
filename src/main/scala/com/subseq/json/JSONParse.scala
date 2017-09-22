package com.subseq.json

import com.subseq.chars._
import com.subseq.core._
import com.subseq.json.values._
import com.subseq.multi._
import com.subseq.split.Spliterator

object JSONParse extends ParsesOptionally[Char, JSON] {
  val jsonParser = new ParseLatch[Char,JSON]

  //Object Parsing
  val pairParser = ((IgnoreLW >> StringParser) &> (IgnoreLW >> ':'), IgnoreLW >> jsonParser)
  val mapParser = '{' &> MapOf(pairParser, IgnoreLW(',')) &> IgnoreLW('}')

  val seqParser = '[' &> SeqOf(IgnoreLW(jsonParser), IgnoreLW(',')) &> IgnoreLW(']')

  val combined: ParsesOptionally[Char, JSON] = IgnoreLW >> AnyOf(
    StringParser.transform(new JSONString(_)),
    NumberParser.transform(new JSONNumber(_)),
    mapParser.transform(new JSONObject(_)),
    seqParser.transform(new JSONArray(_)),
    "true" -> JSONBoolean(true),
    "false" -> JSONBoolean(false),
    "null" -> JSONNull
  )

  jsonParser.set(combined)

  override def parse(input: Spliterator[Char]): AfterParse[Char, Option[JSON]] = {
    jsonParser.parse(input)
  }

  def canFail = true
}