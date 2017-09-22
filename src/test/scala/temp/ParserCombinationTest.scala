package temp

import org.scalatest.FlatSpec
import com.subseq.core._
import com.subseq.chars._

class ParserCombinationTest extends FlatSpec {
  "An assertion and then an optional parser" should "only yield a value when that assertion is true" in {
    val parser = 'A' &> DigitParser
    assert(parser.parse("A2").value.getOrElse(0) == 2)
  }

  "An optional parser and then an assertion" should "only yield a value when that assertion is true" in {
    val parser = DigitParser &> 'A'
    assert(parser.parse("2A").value.getOrElse(0) == 2)
  }

  "An optional parser with prefix and postfix assertions" should "be dependent on both assertions" in {
    val positive = "<2>"
    val negative1 = ">2>"
    val negative2 = "<2<"

    val parser = '<' &> DigitParser &> '>'

    assert(parser.parse(positive).value.getOrElse(0) == 2)
    assert(parser.parse(negative1).value.isEmpty)
    assert(parser.parse(negative2).value.isEmpty)
  }

  "A series of assertions" should "require all to be true" in {
    val test = "ABCDEF"
    val parser = 'A' &> 'B' &> 'C' &> 'D' &> 'E' &> 'F'
    assert(parser.parse(test).value)
  }

  "A assertion or an assertion" should "be true in either situation" in {
    val testL = "A"
    val testR = "B"
    val testF = "C"

    val left: Asserts[Char] = 'A'
    val right: Asserts[Char] = 'B'
    val parser: Asserts[Char] = left || right

    assert(left.parse(testL).value)
    assert(right.parse(testR).value)

    assert(parser.parse(testL).value)
    assert(parser.parse(testR).value)
    assert(!parser.parse(testF).value)
  }
}
