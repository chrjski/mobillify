package ks.server

import java.text.SimpleDateFormat
import java.util.Date

import ks.browser.HTMLServer.render
import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.{Account, Expense, Income, Transaction}
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
    before("/*", (req, res) => {
//      Thread.sleep(2000)
    })


    notFound(toJson(error("Page not found")))
    internalServerError(toJson(error("Internal Server Error")))

    get("/", (req, res) => {
      render("templates/dashboard.vm", Map()
      )
    })

    get("/expensesByCategory/", (req, res) => {
      val all = new TransactionsService().all
      log.info("categorize expenses")
      render("templates/expensesByCategory.vm", Map(
        "categories" -> all
          //              .filter(_.transactionDate)
          .groupBy(_.category)
          .keys
          .toList,
        "expenses" ->
          all
            //              .filter(_.transactionDate)
            .groupBy(_.category)
            .foldLeft(new java.util.LinkedHashMap[String, List[Transaction]]())((acc, el) => {
              acc.put(el._1, el._2)
              acc
            })
      ))
    })

    post("/expense/", (req, res) => {
      log.info(s"post adding income")

      val json = "{" +
        "\"amount\": " + req.queryMap().get("amount").value() + "," +
        "\"category\": \"" + req.queryMap().get("category").value() + "\"," +
        "\"description\": \"" + req.queryMap().get("description").value() + "\"," +
        "\"transactionDate\": \"" + req.queryMap().get("date").value() + "\"," +
        "\"accountName\": \"" + req.queryMap().get("account").value() + "\"" +
        "}"
      new TransactionsService().create("expense", json)
      res.redirect("/app/transactions/")
      "redirect"
    })

    post("/income/", (req, res) => {
      log.info(s"post adding income")

      val json = "{" +
        "\"amount\": " + req.queryMap().get("amount").value() + "," +
        "\"category\": \"" + req.queryMap().get("category").value() + "\"," +
        "\"description\": \"" + req.queryMap().get("description").value() + "\"," +
        "\"transactionDate\": \"" + req.queryMap().get("date").value() + "\"," +
        "\"accountName\": \"" + req.queryMap().get("account").value() + "\"" +
        "}"
      new TransactionsService().create("income", json)
      res.redirect("/app/transactions/")
      "redirect"
    })

    get("/income/", (req, res) => {
      log.info("add income")
      render("templates/addIncome.vm", Map(
        "accounts" ->
          new AccountsService().all.foldLeft(new java.util.LinkedList[Account]())((acm, acc) => {
            acm.add(acc)
            acm
          }),
        "date" -> new SimpleDateFormat("yyyy-MM-dd").format(new Date),
        "categories" -> new TransactionsService().all.foldLeft(new java.util.HashSet[String]())((acc, tr) => {
          if (tr.isInstanceOf[Income]) acc.add(tr.category)
          acc
        })
      ))
    })

    get("/expense/", (req, res) => {
      log.info("add expense")
      render("templates/addExpense.vm", Map(
        "accounts" ->
          new AccountsService().all.foldLeft(new java.util.LinkedList[Account]())((acm, acc) => {
            acm.add(acc)
            acm
          }),
        "date" -> new SimpleDateFormat("yyyy-MM-dd").format(new Date),
        "categories" -> new TransactionsService().all.foldLeft(new java.util.HashSet[String]())((acc, tr) => {
          if (tr.isInstanceOf[Expense]) acc.add(tr.category)
          acc
        })
      ))
    })

    get("/transactions/", (req, res) => {
      val all = new TransactionsService().all
      log.info("get all transactions")
      render("templates/transactions.vm", Map(
        "transactions" -> all
          .groupBy(_.transactionDate)
          .foldLeft(
            new java.util.TreeMap[String, java.util.LinkedList[Transaction]](
              (o1: String, o2: String) => o2.compareTo(o1))
          )((acc, tr) => {
            val transactions = tr._2.foldLeft(new java.util.LinkedList[Transaction]())((acc1, el) => {
              acc1.add(el)
              acc1
            })
            acc.put(new SimpleDateFormat("yyyy-MM-dd").format(tr._1),
              transactions
            )
            acc
          })
        ,
        "emojis" -> all
          .map(tr => tr.category -> DataStore.getEmoji(tr.category))
          .toMap
          .foldLeft(new java.util.LinkedHashMap[String, String]())((acc, el) => {
            acc.put(el._1, el._2)
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
