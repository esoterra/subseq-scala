package chartests

import com.subseq.chars._
import com.subseq.core._
import org.scalatest.FlatSpec

class StringParserTest extends FlatSpec {
  "A string literal" should "parse to a string" in {
    StringParser.parse("\"string literal\"").value match {
      case Some(v) => assert(v == "string literal")
      case None => fail()
    }
  }

//  val dictionary = ('a' to 'z') ++ ('A' to 'Z') ++ Seq('\"', '\\', '/', 'b', 'f', 'n', 'r', 't').map('\\'+_)
}