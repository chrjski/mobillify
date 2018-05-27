package ks.server.util

import ks.browser.account.{AccountDao, NAAccount, ValidAccount}
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
  }

  object Templates {

    val Body = "templates/body.vm"
    val Dashboard = "templates/dashboard.vm"
    val Expense = "templates/expense.vm"
    val Income = "templates/income.vm"
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
          (Link.Accounts, "Accounts"))
          .toArray
      ), Templates.Dashboard)

    def Expense(): Route = (req: Request, res: Response) => {
      log.info("Adding an expense")
      ViewUtil.render(req, Map(
        "accounts" -> accdao.all()
      ), Templates.Expense)
    }

    def ExpensePost(): Route = (req: Request, res: Response) => {
      log.info("Expense added "+req.queryMap())
      accdao.expense(req.queryMap())
      res.redirect("/expense")
      ""
    }

    def Income(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(), Templates.Income)

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
  }

}
