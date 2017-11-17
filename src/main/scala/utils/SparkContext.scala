package util

import org.apache.spark.sql.SparkSession
import utils.ConfigManager

object SparkContext {

  lazy val confing = ConfigManager.config
  lazy val master =  confing.getString("spark.master")
  lazy val sparkSession = SparkSession
    .builder()
    .master(master)
    .appName(Constants.AppName)
    .getOrCreate()
  lazy val sqlContext = sparkSession.sqlContext
}
