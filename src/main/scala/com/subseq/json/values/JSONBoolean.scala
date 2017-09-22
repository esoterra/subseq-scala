package com.subseq.json.values

object JSONBoolean {
  def apply(v: Boolean): JSONBoolean = new JSONBoolean(v)
}

class JSONBoolean(v: Boolean) extends JSON {
//  def as(someType: BooleanType): Option[Boolean] = Some(v)
//  def as(someType: Type[JSON]): Option[JSON] = Some(this)
//  def as[T](someType: Type[T]): Option[T] = None

  def as[T](someType: Type[T]): Option[T] = {
    someType match {
      case BooleanType() => Some(v.asInstanceOf[T])
      case JSONType() => Some(this.asInstanceOf[T])
      case _ => None
    }
  }
}