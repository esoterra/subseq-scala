package com.subseq.json.values

object JSONNull extends JSON {
//  def as(someType: NullType): Option[Null] = Some(null)
//  def as(someType: Type[JSON]): Option[JSON] = Some(this)
//  def as[T](someType: Type[T]): Option[T] = None

  def as[T](someType: Type[T]): Option[T] = {
    someType match {
      case NullType() => Some(null.asInstanceOf[T])
      case JSONType() => Some(this.asInstanceOf[T])
      case _ => None
    }
  }
}