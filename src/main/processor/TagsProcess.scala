package processor

import model.Tag
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Dataset, Row}
import util.SparkContext


trait TagsProcess extends Serializable {

  val spark = SparkContext.sparkSession

  import spark.implicits._

  def getRelevantTags(inputFile: String, maximumTagsPerTag: Int): RDD[(Tag, Tag)] = {
    val tagPairs = loadTagPairsIntoDataFrame(inputFile)
    val tagPairsWithCount = tagPairs.rdd.map(tagPair => (tagPair, 1L)).reduceByKey(_ + _)
    val tagPairWithCountMapped = tagPairsWithCount.flatMap(
      tagsWithCount => Seq(
        (tagsWithCount._1._1, Seq((tagsWithCount._1._2, tagsWithCount._2))),
        (tagsWithCount._1._2, Seq((tagsWithCount._1._1, tagsWithCount._2)))
      )
    )

    val tagsWithRelevantTags = tagPairWithCountMapped.reduceByKey(
      (listOfTags1, listOfTags2) => (listOfTags1 ++ listOfTags2).sortBy(_._2).takeRight(maximumTagsPerTag)
    )
    tagsWithRelevantTags.flatMap(tagWithRelevantTags => tagWithRelevantTags._2.reverse.map(
      relevantTagWithCount => (tagWithRelevantTags._1, relevantTagWithCount._1 )
    ))
  }

  def loadTagPairsIntoDataFrame(file: String): Dataset[(Tag, Tag)] = {

    val sqlContext = SparkContext.sqlContext
    val df = sqlContext.read.format("csv")
      .option("header", "true")
      .option("inferSchema", "true")
      .option("delimiter", "\t")
      .load(file)
    val tagsStringList = df.map(
      row => mapRow(row)
    )
    val pairTags = tagsStringList.flatMap(
      tagsOnDoc => createCombinationOfTags(tagsOnDoc)
    )
    print(pairTags.first()._1.value)
    pairTags
  }

  def mapRow(row: Row): Seq[String] = {
    val tagsOnDoc = row.mkString("").split(",").toSeq
    if (tagsOnDoc.size < 2) {
      tagsOnDoc ++ Seq("")
    } else {
      tagsOnDoc
    }

  }

  def createCombinationOfTags(tagsOnDoc: Seq[String]): Seq[(Tag, Tag)] = {
    tagsOnDoc.combinations(2).map(
      tagsPair => (Tag(tagsPair.head), Tag(tagsPair.tail.head))
    ).toSeq
  }
}

object TagsProcess extends TagsProcess
