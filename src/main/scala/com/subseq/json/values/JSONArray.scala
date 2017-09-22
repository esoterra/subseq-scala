package com.subseq.json.values

object JSONArray {
//  def apply(contents: Seq[JSON]): JSONArray = new JSONArray(contents)
  def apply(contents: JSON*): JSONArray = new JSONArray(contents)
}

class JSONArray(contents: Seq[JSON]) extends JSON {
  def as[T](someType: Type[T]): Option[T] = {
    someType match {
      case SeqType(v) =>
        val query = contents.map(_.as(v))
        if(query.exists(_.isEmpty)) {
          None
        } else {
          Some(query.map(_.get).asInstanceOf[T])
        }
      case JSONType() => Some(this.asInstanceOf[T])
      case _ => None
    }
  }
}
