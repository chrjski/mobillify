package ks.server

import ks.browser.HTMLServer
import ks.browser.HTMLServer.render
import ks.mobilify.engine.Mobilify.Transaction
import ks.server.RestServer.ApiPaths.dictionary
import ks.server.rest.services._
import ks.server.util.JSONTransformer
import ks.server.util.JSONTransformer.toJson
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

  path("/app", () => {
    staticFiles.location("/")
    val log = LoggerFactory.getLogger("API Logger")

    before("/*", (req, res) => log.info(s"Received app call ${req.pathInfo()}"))
    before("/*", (req, res) => appendTrailingSlash(req, res))

    notFound(toJson(error("Page not found")))
    internalServerError(toJson(error("Internal Server Error")))

    get("/", (req, res) => {
      render("templates/dashboard.vm", Map()
      )
    })

    get("/transactions/", (req, res) => {
      val all = new TransactionsService().all
      log.info(s"trans ${all}")
      HTMLServer.renderPart("templates/transactions.vm", Map(
        "transactions" -> all.foldLeft(new java.util.ArrayList[Transaction]())((acc, tr) => {
          acc.add(tr)
          acc
        })
      )
      )
    })

    def getFromService[A <: Any](id: String, service: RestService[A]) = {
      log.info(s"Requesting data $id from $service")
      val transaction = service.get(id)

      if (transaction.isDefined) success(transaction.get)
      else error(s"Could not find by id '$id' in $service")
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
        }, JSONTransformer)

        get(s"/$getById/", (req, res) => {
          log.info(s"get $getById")
          val id = req.params(s"$getById")
          log.info(s"get $id from $service")
          getFromService(id, service)
        }, JSONTransformer)

        post(s"/$getById/", (req, res) => {
          log.info(s"post $getById")
          val id = req.params(s"$getById")
          log.info(s"post $id from $service")
          val created = service.create(id, req.body())
          log.info(s"posted  $created")

          success(created)
        }, JSONTransformer)

        delete(s"/$getById/", (req, res) => {
          log.info(s"delete $getById")
          val id = req.params(s"$getById")
          log.info(s"delete $id from $service")
          val deleted = service.delete(id)
          log.info(s"deleted  $deleted")

          success(deleted)
        }, JSONTransformer)

        put(s"/$getById/", (req, res) => {
          log.info(s"put $getById")
          val id = req.params(s"$getById")
          log.info(s"put $id from $service")
          val created = service.update(id, req.body())
          log.info(s"put  $created")

          success(created)
        }, JSONTransformer)

        customs.foreach(customRoute => {
          log.info(s"Setting up custom pages ${customRoute._1}")
          get(customRoute._1, customRoute._2, JSONTransformer)
        })
      })
    }
  })



  path("/api", () => {
    val log = LoggerFactory.getLogger("API Logger")

    before("/*", (req, res) => log.info(s"Received api call ${req.pathInfo()}"))
    before("/*", (req, res) => res.`type`("application/json"))
    before("/*", (req, res) => appendTrailingSlash(req, res))

    notFound(toJson(error("Page not found")))
    internalServerError(toJson(error("Internal Server Error")))

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
      else error(s"Could not find by id '$id' in $service")
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
        }, JSONTransformer)

        get(s"/$getById/", (req, res) => {
          log.info(s"get $getById")
          val id = req.params(s"$getById")
          log.info(s"get $id from $service")
          getFromService(id, service)
        }, JSONTransformer)

        post(s"/$getById/", (req, res) => {
          log.info(s"post $getById")
          val id = req.params(s"$getById")
          log.info(s"post $id from $service")
          val created = service.create(id, req.body())
          log.info(s"posted  $created")

          success(created)
        }, JSONTransformer)

        delete(s"/$getById/", (req, res) => {
          log.info(s"delete $getById")
          val id = req.params(s"$getById")
          log.info(s"delete $id from $service")
          val deleted = service.delete(id)
          log.info(s"deleted  $deleted")

          success(deleted)
        }, JSONTransformer)

        put(s"/$getById/", (req, res) => {
          log.info(s"put $getById")
          val id = req.params(s"$getById")
          log.info(s"put $id from $service")
          val created = service.update(id, req.body())
          log.info(s"put  $created")

          success(created)
        }, JSONTransformer)

        customs.foreach(customRoute => {
          log.info(s"Setting up custom pages ${customRoute._1}")
          get(customRoute._1, customRoute._2, JSONTransformer)
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

  import ks.server.util.Statuses._
  import ks.server.util.{StandardResponse, StatusResponse}

  def error(message: Any) = status(ERROR, message)

  def success(message: Any) = status(SUCCESS, message)

  def status(status: StatusResponse, message: Any) = StandardResponse(status, message)

}
