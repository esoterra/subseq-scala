package temp

import org.scalatest.FlatSpec
import com.subseq.chars._
import com.subseq.core._
import com.subseq.multi._

class TupleTest extends FlatSpec {
  "(T=>Option[A], T=>Option[B])" should "implicitly convert to T=>Option[(A,B)]" in {
    val tupleParser = '(' &> (DigitParser, ',' &> DigitParser) &> ')'

    tupleParser.parse("(1,2)").value match {
      case Some(a) =>
        assert(a == (1,2))
      case None =>
        fail()
    }
  }

  "SeqOf and tuple parsing" should "combine effectively" in {
    val tupleParser = '(' &> (DigitParser, ',' &> DigitParser) &> ')'
    val tupleSeqParser = '[' &> SeqOf(tupleParser, ',') &> ']'

    tupleSeqParser.parse("[(1,2),(3,4),(5,6)]").value match {
      case Some(a) =>
        assert(a==Seq((1,2),(3,4),(5,6)))
      case None =>
        fail()
    }
  }
}
