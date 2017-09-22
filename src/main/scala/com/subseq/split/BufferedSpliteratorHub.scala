package com.subseq.split

import scala.collection.mutable.ListBuffer

/**
 *
 * @param data
 * @tparam T
 * @author Kyle Brown
 */
class BufferedSpliteratorHub[T](data: Iterator[T]) {
  private[split] val splits = ListBuffer[BufferedSpliterator[T]]()

  private[split] var lowerBound = 0
  private[split] val buffer = new ListBuffer[T]

  def getBase: BufferedSpliterator[T] = {
    if(splits.isEmpty) {
      val newBase = new BufferedSpliterator[T](this, lowerBound)
      splits += newBase
      newBase
    } else {
      splits.head
    }
  }

  private[split] def duplicate(first: BufferedSpliterator[T]): BufferedSpliterator[T] = {
    val second = new BufferedSpliterator[T](this, first.nextIndex)
    splits.insert(splits.indexOf(first), second)
    second
  }

  def destroy(split: BufferedSpliterator[T]): Unit = {
    splits -= split
    cleanBuffer()
  }

  def hasNext(split: BufferedSpliterator[T]): Boolean = {
    if(!splits.contains(split)) false
    else split.nextIndex >= lowerBound && (split.nextIndex < (lowerBound + buffer.size) || data.hasNext)
  }

  def nextFor(split: BufferedSpliterator[T]): T = {
    val startRank = splits.indexOf(split)
    if(startRank == -1) throw new Exception("next called on destroyed Spliterator")

    //Re-rank split in the splits ListBuffer
    for(currentRank <- startRank to splits.size-2
       if splits(currentRank+1).nextIndex < split.nextIndex + 1) {
      splits(currentRank) = splits(currentRank+1)
      splits(currentRank+1) = split
    }

    //Retrieve the required element
    if(split.nextIndex == lowerBound + buffer.size) {
      if(!data.hasNext) throw new Exception("Spliterator ")
      val res = data.next()
      buffer.append(res)
      res
    } else {
      buffer(split.nextIndex-lowerBound)
    }
  }

  private[split] def cleanBuffer(): Unit = {
    val newLower =
      if(splits.nonEmpty) splits.head.nextIndex
      else lowerBound + buffer.size

    buffer.remove(0, newLower-lowerBound)
    lowerBound = newLower
  }
}

/**
 *
 * @param hub
 * @param init
 * @tparam T
 * @author Kyle Brown
 */
class BufferedSpliterator[T](hub: BufferedSpliteratorHub[T], init: Int) extends Spliterator[T] {
//  println("Spliterator Created: " + init)
  private[split] var nextIndex = init

  def split: Spliterator[T] = {
    hub.duplicate(this)
  }
  def destroy(): Unit = hub.destroy(this)

  override def hasNext: Boolean = hub.hasNext(this)
  override def next(): T = {
    val cache = hub.nextFor(this)
    nextIndex += 1
    hub.cleanBuffer()
    cache
  }
}