package utils

import com.typesafe.config.ConfigFactory

object ConfigManager {

    lazy val config = ConfigFactory.load("application.conf")

}