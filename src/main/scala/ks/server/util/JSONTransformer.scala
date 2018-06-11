package ks.server.util

import org.json4s.DefaultFormats
import org.json4s.native.Serialization.write
import spark.ResponseTransformer

object JSONTransformer extends ResponseTransformer {
  override def render(model: scala.Any): String = toJson(model)

  def toJson(model: Any): String = write(model)(DefaultFormats)
}
