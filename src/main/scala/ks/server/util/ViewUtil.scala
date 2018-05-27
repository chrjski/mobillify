package ks.server.util

/**
  * Created by k-dev on 5/26/2018.
  */

import org.apache.velocity.app.VelocityEngine
import spark.Request
import spark.template.velocity.VelocityTemplateEngine

import scala.collection.JavaConverters
object ViewUtil {

  import spark.ModelAndView
  // Renders a template given a model and a request// Renders a template given a model and a request

  // The request is needed to check the user session for language settings
  // and to see if the user is logged in
  def render(request: Request, model: Map[String, AnyRef], templatePath: String): String = {
//    model.put("msg", new Nothing(getSessionLocale(request)))
//    model.put("currentUser", getSessionCurrentUser(request))
//    model.put("WebPath", classOf[Nothing]) // Access application URLs from templates

    strictVelocityEngine.render(new ModelAndView(JavaConverters.mapAsJavaMap(model), templatePath))
  }

  private def strictVelocityEngine = {
    val configuredEngine = new VelocityEngine
    configuredEngine.setProperty("runtime.references.strict", true)
    configuredEngine.setProperty("resource.loader", "class")
    configuredEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader")
    new VelocityTemplateEngine(configuredEngine)
  }
}
