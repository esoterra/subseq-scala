package com.subseq.json.values

trait Type[+T]

case class JSONType() extends Type[JSON]

case class StringType() extends Type[String]
case class DoubleType() extends Type[Double]
case class IntType() extends Type[Int]
case class MapType[K,V](keyType: Type[K], valueType: Type[V]) extends Type[Map[K,V]]
case class SeqType[T](elementType: Type[T]) extends Type[Seq[T]]
case class BooleanType() extends Type[Boolean]
case class NullType() extends Type[Null]