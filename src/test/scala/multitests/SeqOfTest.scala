package multitests

import com.subseq.chars._
import com.subseq.multi._
import com.subseq.core._
import org.scalatest.FlatSpec

class SeqOfTest extends FlatSpec {
  "A delimited array of digits" should "parse to a Seq of integers" in {
    SeqOf(DigitParser, '|').parse("1|2|3|4~").value match {
      case Some(v) => assert(v == Seq(1, 2, 3, 4))
      case None => fail()
    }
  }

  "A delimited array of digits" should "parse to a Seq of integers matching prefix and postfix assertions" in {
    ('[' &> SeqOf(DigitParser, ',') &> ']').parse("[1,2,3]").value match {
      case Some(v) => assert(v == Seq(1, 2, 3))
      case None => fail()
    }
  }

  "A json array of digits" should "parse to a sequence of digits ignoring whitespace" in {
    val parser = IgnoreLW >> '[' &> SeqOf(IgnoreLW >> DigitParser, IgnoreLW >> ',') >> IgnoreLW &> ']'

    parser.parse(" [  1\n,\t2 , 4 ] ").value match {
      case Some(v) => assert(v == Seq(1, 2, 4))
      case None => fail()
    }
  }
}

