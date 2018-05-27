package ks.server.util

/**
  * Created by k-dev on 5/26/2018.
  */

import java.util

import org.apache.velocity.app.VelocityEngine
import spark.Request
import spark.template.velocity.VelocityTemplateEngine
object ViewUtil {

  import spark.ModelAndView
  // Renders a template given a model and a request// Renders a template given a model and a request

  // The request is needed to check the user session for language settings
  // and to see if the user is logged in
  def render(request: Request, model: Map[String, AnyRef], templatePath: String): String = {

    val nmap = new util.LinkedHashMap[String, AnyRef]()

    model.foldLeft(nmap)((acc, el) => {
      acc.put(el._1, el._2)
      acc
    })

    val view = new ModelAndView(nmap, templatePath)
    strictVelocityEngine.render(view)
  }

  private def strictVelocityEngine = {
    val configuredEngine = new VelocityEngine
    configuredEngine.setProperty("runtime.references.strict", true)
    configuredEngine.setProperty("resource.loader", "class")
    configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader")
    new VelocityTemplateEngine(configuredEngine)
  }
}
