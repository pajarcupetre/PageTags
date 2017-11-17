package runner

import processor.TagsProcess
import utils.TagWriter

object TagsRunner extends App {

  val inputFile = getClass.getResource("/tags.csv")
  val tagsPairs = TagsProcess.getRelevantTags(inputFile.getFile, 3)
  TagWriter.writePairOfTags(tagsPairs, "/home/petre/Downloads/spark-interview-offline/result.csv")
}