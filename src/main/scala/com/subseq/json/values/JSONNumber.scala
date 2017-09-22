package com.subseq.json.values

object JSONNumber {
  def apply(value: Double): JSONNumber = new JSONNumber(value)
}

class JSONNumber(value: Double) extends JSON {
  def as[T](someType: Type[T]): Option[T] = {
    someType match {
      case DoubleType() => Some(value.asInstanceOf[T])
      case IntType() => if(value.isValidInt) Some(value.toInt.asInstanceOf[T]) else None
      case JSONType() => Some(this.asInstanceOf[T])
      case _ => None
    }
  }
}