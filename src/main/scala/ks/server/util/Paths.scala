package ks.server.util

import java.text.SimpleDateFormat

import ks.browser.account._
import org.slf4j.{Logger, LoggerFactory}
import spark.{Request, Response, Route}

object Paths {

  val log: Logger = LoggerFactory.getLogger(Paths.getClass)

  object Link {
    val Dashboard = "/"
    val Income = "/income"
    val Expense = "/expense"
    val Transfer = "/transfer"
    val Accounts = "/accounts"
    val AccountDetails = "/accounts/details/:accName"
    val AccountDetailsEditTransaction = "/accounts/details/:accName/edit/transaction/:id"

    val Categories = "/categories"
    val CategoryDetail = "/categories/details/:category"
  }

  object Templates {

    val Body = "templates/body.vm"
    val Dashboard = "templates/dashboard.vm"
    val Transaction = "templates/transactions/transaction.vm"
    val Transfer = "templates/transfer.vm"
    val Accounts = "templates/accounts.vm"
    val AccountDetail = "templates/accounts/accountDetail.vm"
  }

  object Routes {
    val accdao: AccountDao = new AccountDao()

    def Dashboard(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(
        "links" -> List((Link.Income, "Income"),
          (Link.Expense, "Expense"),
          (Link.Transfer, "Transfer"),
          (Link.Accounts, "Accounts"),
          (Link.Categories, "Categories")
        ).toArray
      ), Templates.Dashboard)

    def Expense(): Route = (req: Request, res: Response) => {
      log.info("Adding an expense")
      ViewUtil.render(req, Map(
        "accounts" -> accdao.all(),
        "transactionType" -> "expense",

        "accName" -> "",
        "amount" -> "",
        "description" -> "",
        "category" -> "",
        "date" -> ""
      ), Templates.Transaction)
    }

    def Income(): Route = (req: Request, res: Response) => {
      log.info("Adding an income")
      ViewUtil.render(req, Map(
        "accounts" -> accdao.all(),
        "transactionType" -> "income",

        "accName" -> "",
        "amount" -> "",
        "description" -> "",
        "category" -> "",
        "date" -> ""
      ), Templates.Transaction)
    }

    def TransactionPosted(): Route = (req: Request, res: Response) => {
      log.info("Transaction added")
      accdao.parseTransaction(req.queryMap())
      res.redirect("#")
      "income added"
    }

    def Transfer(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(), Templates.Transfer)

    def Accounts(): Route = (req: Request, res: Response) => {
      log.info("" + accdao.all)
      ViewUtil.render(
        req,
        Map(
          "accounts" -> accdao.all
        ),
        Templates.Accounts
      )
    }

    def AccountPosted(): Route = (req: Request, res: Response) => {
      val accountName: String = req.queryParams("account")

      val account = accdao.get(accountName)

      account match {
        case NAAccount =>
          log.info("Creating an account by name: " + accountName)
          accdao.add(accountName)
          res.redirect(Paths.Link.Accounts)
          "redirect to accounts"

        case ValidAccount(_) =>
          log.info("Duplicated name " + accountName + " " + account)
          ViewUtil.render(
            req,
            Map(
              "accounts" -> accdao.all,
              "duplicated" -> accountName
            ),
            Templates.Accounts
          )

      }
    }

    def AccountDetail(): Route = (req: Request, res: Response) => {
      val accName = req.params(":accName")
      val account = accdao.get(accName)
      account match {
        case NAAccount =>
          res.redirect("404", 404)
          "You drunk, go home"

        case ValidAccount(_) =>
          log.info("Filtered accounts for " + accName + " is " + account)
          ViewUtil.render(
            req,
            Map(
              "account" -> account
            ),
            Templates.AccountDetail)
      }
    }

    def AccountDetailsEditTransaction: Route = (req: Request, res: Response) => {
      // TODO : apply edits
      accdao.parseTransaction(req.queryMap())

      log.info("AccountDetailsEditTransaction")
      val accName = req.params(":accName")
      val id = req.params(":id").toLong

      val transaction = accdao.get(accName, id)

      ViewUtil.render(
        req,
        Map(
          "accounts" -> accdao.all,
          "accName" -> accName,
          "amount" -> transaction.getAmount().toString,
          "description" -> transaction.getDescription(),
          "category" -> transaction.getCategory(),
          "date" -> new SimpleDateFormat("yyyy-MM-dd").format(transaction.getDate),
          "transactionType" -> transaction.getType(),
          "isEdit" -> true.toString
        ),
        Templates.Transaction
      )
    }

    def Categories(): Route = (req: Request, res: Response) => {

      ""
    }
  }
}
