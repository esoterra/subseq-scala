package com.subseq.split

/**
 *
 * @tparam T
 * @author Kyle Brown
 */
trait Spliterator[T] extends Iterator[T] {
  def split: Spliterator[T]
  def destroy(): Unit
}