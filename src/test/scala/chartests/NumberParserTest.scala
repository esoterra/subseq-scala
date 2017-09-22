package chartests

import com.subseq.core._
import com.subseq.chars._

import org.scalatest.FlatSpec

class NumberParserTest extends FlatSpec {
  def testParseEquivalence(s: String, target: Double): Unit = {
    NumberParser.parse(s).value match {
      case Some(num) => assert(num == target)
      case None => fail()
    }
  }

  "Strings of digits" should "parse to numbers" in {
    testParseEquivalence("123", 123)
    testParseEquivalence("4253", 4253)
    testParseEquivalence("162897043", 162897043)
    testParseEquivalence("0", 0)
  }

  "Strings of digits prepended by '-'" should "parse to negative numbers" in {
    testParseEquivalence("-123", -123)
    testParseEquivalence("-4253", -4253)
    testParseEquivalence("-162897043", -162897043)
    testParseEquivalence("-0", 0)
  }

  "Strings of digits with a decimal point" should "parse to rational numbers" in {
    //Positives
    testParseEquivalence("12.3", 12.3)
    testParseEquivalence("4.253", 4.253)
    testParseEquivalence("1628.97043", 1628.97043)
    testParseEquivalence("0.0", 0)
    //Negatives
    testParseEquivalence("-12.3", -12.3)
    testParseEquivalence("-4.253", -4.253)
    testParseEquivalence("-1628.97043", -1628.97043)
    testParseEquivalence("-0.0", 0)
  }

  "Decimal or whole number strings followed by 'e' or 'E' and a number" should "parse to rational numbers" in {
    testParseEquivalence("12e3", 12000)
    testParseEquivalence("-1.2E3", -1200)
    testParseEquivalence("21.2e-3", 0.0212)
    testParseEquivalence("12E+2", 1200)
    testParseEquivalence("-123456e-1", -12345.6)
  }
}
