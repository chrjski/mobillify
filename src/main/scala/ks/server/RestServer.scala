package ks.server

import ks.server.RestServer.ApiPaths.dictionary
import ks.server.rest.services._
import org.slf4j.LoggerFactory
import spark.Spark._
import spark.{Request, Response, Route}

object RestServer extends App {

  object ApiPaths {
    val api = "/api"

    object dictionary {
      val accounts = "accounts"
      val account = ":account"

      val transactions = "transactions"
      val transaction = ":transaction"

      val balance = "balance"
    }

    val allAccounts = s"$api/${dictionary.accounts}"
    val accountByName = s"$allAccounts/:${dictionary.account}"

    val allTransactions = s"$api/${dictionary.transactions}"
    val transactionByName = s"$allTransactions/:${dictionary.transaction}"
  }

  port(45)
  path("/api", () => {
    val log = LoggerFactory.getLogger("API Logger")

    before("/*", (req, res) => log.info("Received api call", req.pathInfo()))
    before("/*", (req, res) => res.`type`("application/json"))
    before("/*", (req, res) => appendTrailingSlash(req, res))

    notFound(error("Page not found"))
    internalServerError(error("Internal Server Error"))

    get("/", (req, res) => {
      res.`type`("html/text")
      s"<body>" +
        s"<a href='${ApiPaths.allAccounts}'>All accounts</a><br />" +
        s"<a href='${ApiPaths.accountByName}'>Account by name</a><br />" +
        s"<a href='${ApiPaths.allTransactions}'>All transactions</a><br />" +
        s"<a href='${ApiPaths.transactionByName}'>Transaction by name</a><br />" +
        "</body>"
    })

    def getFromService[A <: Any](id: String, service: RestService[A]) = {
      log.info(s"Requesting data $id from $service")
      val transaction = service.get(id)

      if (transaction.isDefined) success(transaction.get)
      else error(s"Could not find by id $id in $service")
    }

    def accountsBalanceRoute(): Route = (req, res) => {
      val id = req.params(s"${dictionary.account}")
      getFromService(id, new BalanceService)
    }

    restPath(dictionary.accounts, dictionary.account, new AccountsService,
      List(
        (s"/${dictionary.account}/${dictionary.balance}/", accountsBalanceRoute))
    )

    restPath(dictionary.transactions, dictionary.transaction, new TransactionsService, List())

    restPath(dictionary.balance, dictionary.account, new BalanceService, List())

    def restPath[A <: Any](
                            all: String,
                            getById: String,
                            service: RestService[A],
                            customs: List[(String, Route)]
                          ) = {
      path(s"/$all/", () => {
        get("/", (req, res) => {
          log.info(s"all ${service.all}")
          success(service.all)
        })

        get(s"/$getById/", (req, res) => {
          val id = req.params(s"$getById")
          getFromService(id, service)
        })

        customs.foreach(customRoute => {
          log.info(s"Setting up custom pages ${customRoute._1}")
          get(customRoute._1, customRoute._2)
        })
      })
    }


    //  post("/accounts", ???)
    //  get("/accounts/:account", ???)
    //  put("/accounts/:account", ???)
    //  delete("/accounts/:account", ???)

  })

  private def appendTrailingSlash(req: Request, res: Response): Unit = {
    if (!req.pathInfo().endsWith("/")) {
      res.redirect(req.pathInfo() + "/")
    }
  }

  private def json(any: Any) = {
    import org.json4s.DefaultFormats
    import org.json4s.native.Serialization.write
    write(any)(DefaultFormats)
  }

  import ks.server.util.Statuses._
  import ks.server.util.{StandardResponse, StatusResponse}

  def error(message: Any) = status(ERROR, message)

  def success(message: Any) = status(SUCCESS, message)

  def status(status: StatusResponse, message: Any) = json(StandardResponse(status, message))

}
