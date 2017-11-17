package processor

import model.Tag
import org.apache.spark.sql.Row
import org.scalatest.FunSuite

class TagsProcessTest extends FunSuite {

    test("test rowMap from Row to SeqOfString") {
      val row = Row("String,Int")
      val expectValue = Seq("String", "Int")
      val actualValue = TagsProcess.mapRow(row)
      assert(expectValue.equals(actualValue))
    }

    test("test rowMap with only one string") {
      val row = Row("String")
      val expectValue = Seq("String", "")
      val actualValue = TagsProcess.mapRow(row)
      assert(expectValue.equals(actualValue))
    }

    test("test tag pair generation") {
      val tags = Seq("Int", "Float" , "Double")
      val expected = Seq((Tag("Int"), Tag("Float")),(Tag("Int"), Tag("Double")), (Tag("Float"), Tag("Double")) )
      val actual = TagsProcess.createCombinationOfTags(tags)
      assert(expected.equals(actual))
    }

    test("load tags into dataset") {
      val inputFile = getClass.getResource("/tags.csv")
      val actualPairTags = TagsProcess.loadTagPairsIntoDataFrame(inputFile.getFile).collect()
      assert(actualPairTags.size.equals(7))
      assert(actualPairTags.contains((Tag("A"), Tag("B"))))
    }

  test("test get most relevant tag pair first test") {
    val inputFile = getClass.getResource("/tags.csv")
    val actualRelevantTags = TagsProcess.getRelevantTags(inputFile.getFile, 1).collect()
    assert(actualRelevantTags.size.equals(5))
    assert(actualRelevantTags.contains((Tag("A"), Tag("C"))))
  }

    test("test get most relevant tag pair second test") {
      val inputFile = getClass.getResource("/tags2.csv")
      val actualRelevantTags = TagsProcess.getRelevantTags(inputFile.getFile, 1).collect()
      assert(actualRelevantTags.size.equals(9))
      assert(actualRelevantTags.contains((Tag("apple"), Tag("fruit"))))
    }

}
