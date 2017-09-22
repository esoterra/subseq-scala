package temp

import org.scalatest.FlatSpec
import com.subseq.chars._
import com.subseq.core._
import com.subseq.split.Spliterator

import scala.util.Random

class SpliteratorTest extends FlatSpec {
  val random = new Random(1)
  val bigString: String = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".combinations(4).mkString("")

  "A BufferedSpliterator" should "contain a sequence of values" in {
    val left: Spliterator[Char] = bigString
    val right = left.split

    var lCount = 0
    var rCount = 0
    while(lCount < bigString.length && rCount < bigString.length) {
      if(random.nextBoolean()) {
        assert(left.next() == bigString.charAt(lCount))
        lCount += 1
      } else {
        assert(right.next() == bigString.charAt(rCount))
        rCount += 1
      }
    }

    while(lCount < bigString.length) {
      assert(left.next() == bigString.charAt(lCount))
      lCount += 1
    }

    while(rCount < bigString.length) {
      assert(right.next() == bigString.charAt(rCount))
      rCount += 1
    }
  }
}
