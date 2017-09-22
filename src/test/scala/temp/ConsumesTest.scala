package temp

import com.subseq.core._
import com.subseq.chars._
import org.scalatest.FlatSpec

class ConsumesTest extends FlatSpec {
  "IgnoreLW" should "consume only leading whitespace tokens" in {
    assert(IgnoreLW.parse("\n\n\t\r    ABC").remaining.mkString("") == "ABC")
    assert((IgnoreLW >> DigitParser).parse(" 2").value.getOrElse(0) == 2)
    assert((IgnoreLW >> 'A').parse("\t\n A").value)
  }
}
