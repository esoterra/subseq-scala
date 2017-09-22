package com.subseq.json.values

class JSONString(value: String) extends JSON {
//  def as(someType: StringType): Option[String] = Some(value)
//  def as(someType: Type[JSON]): Option[JSON] = Some(this)
//  def as[T](someType: Type[T]): Option[T] = None

  def as[T](someType: Type[T]): Option[T] = {
    someType match {
      case StringType() => Some(value.asInstanceOf[T])
      case JSONType() => Some(this.asInstanceOf[T])
      case _ => None
    }
  }
}
