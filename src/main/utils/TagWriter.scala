package utils

import java.io.File
import java.nio.file.Files

import model.Tag
import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs._
import org.apache.spark.rdd.RDD

trait TagWriter {

  def writePairOfTags(tags: RDD[(Tag, Tag)], outputFile: String): Unit = {
    val outFileSplitted = outputFile + "_split"
    val tagsCSVFormat = tags.map(pairTags => s"${pairTags._1.value},${pairTags._2.value}")
    tagsCSVFormat.saveAsTextFile(outFileSplitted)
    merge(outFileSplitted, outputFile)
  }

  private def merge(srcPath: String, dstPath: String): Unit =  {
    val hadoopConfig = new Configuration()
    val hdfs = FileSystem.get(hadoopConfig)
    FileUtil.copyMerge(hdfs, new Path(srcPath), hdfs, new Path(dstPath), true, hadoopConfig, null)
  }
}

object TagWriter extends TagWriter
