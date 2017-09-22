package jsontests

import com.subseq.json.values._
import org.scalatest.FlatSpec

class JSONRepresentationTest extends FlatSpec {
  "A number" should "be convertable into a Double" in {
    val a = JSONNumber(1)
    a.as(DoubleType()) match {
      case Some(1) => println("matched")
      case _ => fail()
    }
  }

  "A JSONObject" should "be able to convert to a Map[String,Double]" in {
    val source = JSONObject(Map(
      "A" -> JSONNumber(1),
      "B" -> JSONNumber(2),
      "C" -> JSONNumber(3)
    ))

    source.as(MapType(StringType(), DoubleType())) match {
      case Some(v) => assert(v == Map("A"->1, "B"->2, "C"->3))
      case _ => fail()
    }
  }

  "A complex compositional type" should "evaluate correctly" in {
    val source = JSONObject(Map(
      "A" -> JSONArray(JSONNumber(1), JSONNumber(2), JSONNumber(3)),
      "B" -> JSONArray(JSONNumber(4), JSONNumber(5)),
      "C" -> JSONArray(JSONNumber(6))
    ))

    source.as(MapType(StringType(), SeqType(DoubleType()))) match {
      case Some(v) => assert(v == Map("A"->Seq(1.0,2.0,3.0), "B"->Seq(4.0,5.0), "C"->Seq(6.0)))
      case _ => fail()
    }
  }

  "A complex compositional type" should "successfully evaluate partially" in {
    val source = JSONObject(Map(
      "A" -> JSONArray(JSONNumber(1), JSONNumber(2), JSONNumber(3)),
      "B" -> JSONArray(JSONNumber(4), JSONNumber(5)),
      "C" -> JSONArray(JSONNumber(6))
    ))

    source.as(MapType(StringType(), JSONType())) match {
      case Some(v) => assert(v.mapValues(_.as(SeqType(DoubleType()))) == Map(
        "A"->Some(Seq(1.0,2.0,3.0)),
        "B"->Some(Seq(4.0,5.0)),
        "C"->Some(Seq(6.0))))
      case _ => fail()
    }
  }
}
