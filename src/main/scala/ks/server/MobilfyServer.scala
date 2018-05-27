package ks.server

import ks.server.util.ViewUtil
import spark.Spark._

/**
  * Created by k-dev on 5/26/2018.
  */
object MobilfyServer extends App {

  get("/", (req, res) => {
    ViewUtil.render(req, Map("a" -> "b"), "templates/test.vm")
  })
}
