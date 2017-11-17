package runner

import java.io.File

import processor.TagsProcess
import utils.TagWriter

import scala.util.Try

object TagsRunner extends App {

  val (maximumTagsPerTag, inputFile, outputFile) = getArgumentsOrDefault(args)
  val tagsPairs = TagsProcess.getRelevantTags(inputFile, maximumTagsPerTag)
  TagWriter.writePairOfTags(tagsPairs, outputFile)

  def getArgumentsOrDefault(args: Array[String]):(Int, String, String) = {

    val maximumTagsPerTag =
      if (args.size > 0 && Try(args(0).toInt).isSuccess) { args(0).toInt } else { 1 }

    val inputFile = if (args.size > 1) { args(1) } else { getClass.getResource("/tags.csv").getFile }
    val outFile = if (args.size > 2) { args(2) } else { new File(inputFile).getParent + "/result.csv" }

    (maximumTagsPerTag, inputFile, outFile)
  }
}