package com.subseq.json.values

object JSONObject {
  def apply(contents: Map[String, JSON]): JSONObject = new JSONObject(contents)
}

class JSONObject(contents: Map[String, JSON]) extends JSON {
  def as[T](someType: Type[T]): Option[T] = {
    someType match {
      case MapType(StringType(), vType) =>
        val query = contents.mapValues(_.as(vType))
        if(query.values.exists(_.isEmpty)) {
          None
        } else {
          Some(query.mapValues(_.get).asInstanceOf[T])
        }
      case JSONType() => Some(this.asInstanceOf[T])
      case _ => None
    }
  }
}