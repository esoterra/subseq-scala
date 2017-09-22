package com.subseq.json.values

trait JSON {
  def as[T](someType: Type[T]): Option[T]
}