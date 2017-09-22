package com.subseq.split

/**
 *
 * @param seq
 * @param init
 * @tparam T
 * @author Kyle Brown
 */
class SeqSpliterator[T](seq: Seq[T], init: Int) extends Spliterator[T] {
  private[split] var nextIndex = init

  def split: Spliterator[T] = new SeqSpliterator[T](seq, nextIndex)
  def destroy(): Unit = {}

  override def hasNext: Boolean = nextIndex < seq.size
  override def next(): T = {
    nextIndex += 1
    seq(nextIndex-1)
  }
}
