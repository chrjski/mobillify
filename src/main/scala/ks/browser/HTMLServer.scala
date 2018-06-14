package ks.browser

import spark.ModelAndView
import spark.Spark.{get, port}
import spark.template.velocity.VelocityTemplateEngine

object HTMLServer extends App {

  port(44)
  get("/", (req, res) => {

    res.redirect("localhost:45/api/accounts/")

    render("templates/dashboard.vm", Map())
  })

  def render(template: String, model: Map[String, Any]): String = {
    renderPart("templates/body.vm", model + ("body" -> template))
  }

  def renderPart(template: String, model: Map[String, Any]): String = {
    val nmap = new java.util.LinkedHashMap[String, Any]()

    model.foldLeft(nmap)((acc, el) => {
      acc.put(el._1, el._2)
      acc
    })

    new VelocityTemplateEngine().render(new ModelAndView(nmap, template))
  }
}
