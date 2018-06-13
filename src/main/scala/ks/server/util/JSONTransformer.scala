package ks.server.util

import ks.mobilify.engine.Mobilify.{Account, Balance, Expense, Income}
import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
import org.json4s.native.Serialization.write
import spark.ResponseTransformer

object JSONTransformer extends ResponseTransformer {
  override def render(model: scala.Any): String = toJson(model)
  implicit val formats = Serialization.formats(ShortTypeHints(
    List(
      classOf[Account],
      classOf[Balance],
      classOf[Income],
      classOf[Expense]
    )))

  def toJson(model: Any): String = write(model)
}
