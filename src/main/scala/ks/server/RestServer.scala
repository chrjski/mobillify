package ks.server

import ks.server.rest.services._
import org.slf4j.LoggerFactory
import spark.Spark._

object RestServer extends App {
  port(45)
  path("/api", () => {
    val log = LoggerFactory.getLogger("API Logger")

    before("/*", (req, res) => log.info("Received api call", req.pathInfo()))
    before("/*", (req, res) => res.`type`("application/json"))
    //    before("/*", (req, res) =>
    //      if (!req.pathInfo().endsWith("/")) {
    //        res.redirect(req.pathInfo() + "/");
    //      })

    notFound(error("Page not found"))
    internalServerError(error("Internal Server Error"))

    val accountsService = new AccountsService()
    val transactionsService = new TransactionsService()

    path("/accounts", () => {
      get("/", (req, res) => {
        log.info("All accounts " + accountsService.all)
        success(json(accountsService.all))
        success(json(accountsService.all))
      })
      get("/:account", (req, res) => {
        val accountName = req.params(":account")
        log.info(s"Requesting account $accountName")

        val account = accountsService.get(accountName)

        if (account.isDefined) success(json(account.get))
        else error(s"Could not find account by name $accountName")
      })
    })

    path("/transactions", () => {
        get("/", (req, res) => {
          log.info(s"transactionsService.all ${transactionsService.all}")
          success(json(transactionsService.all))
        })

    })

    //  post("/accounts", ???)
    //  get("/accounts/:account", ???)
    //  put("/accounts/:account", ???)
    //  delete("/accounts/:account", ???)

  });

  private def json(any: Any) = {
    import org.json4s.DefaultFormats
    import org.json4s.native.Serialization.write
    write(any)(DefaultFormats)
  }


  import ks.server.util.Statuses._
  import ks.server.util.{StandardResponse, StatusResponse}

  def error(message: String) = status(ERROR, message)

  def success(message: String) = status(SUCCESS, message)

  def status(status: StatusResponse, message: String) = json(new StandardResponse(status, message))

}
