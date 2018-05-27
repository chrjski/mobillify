package ks.server.util

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.Account
import org.slf4j.LoggerFactory
import spark.{Request, Response, Route}

object Paths {

  val log = LoggerFactory.getLogger(Paths.getClass)

  object Link {
    val Dashboard = "/"
    val Income = "/income"
    val Expense = "/expense"
    val Transfer = "/transfer"
    val Accounts = "/accounts"
    val AccountDetails = "/accounts/details/:accName"
  }

  object Templates {

    val Dashboard = "templates/dashboard.vm"
    val Expense = "templates/expense.vm"
    val Income = "templates/income.vm"
    val Transfer = "templates/transfer.vm"
    val Accounts = "templates/accounts.vm"
    val AccountDetail = "templates/accounts/accountDetail.vm"
  }

  object Routes {
    def Dashboard(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(
        "links" -> List((Link.Income, "Income"),
          (Link.Expense, "Expense"),
          (Link.Transfer, "Transfer"),
          (Link.Accounts, "Accounts"))
          .toArray
      ), Templates.Dashboard)

    def Expense(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(), Templates.Expense)

    def Income(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(), Templates.Income)

    def Transfer(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(), Templates.Transfer)

    def Accounts(): Route = (req: Request, res: Response) => {
      log.info("" + DataStore.accounts)
      ViewUtil.render(
        req,
        Map(
          "accounts" -> DataStore.accounts.toArray
        ),
        Templates.Accounts
      )
    }

    def AccountPosted(): Route = (req: Request, res: Response) => {
      val accountName: String = req.queryParams("account")

      val accounts = DataStore.accounts.filter(_.name.equals(accountName))
      if (accounts.nonEmpty) {
        log.info("Duplicated name " + accountName)
        ViewUtil.render(
          req,
          Map(
            "accounts" -> DataStore.accounts.toArray,
            "duplicated" -> accountName
          ),
          Templates.Accounts
        )
      } else {
        log.info("Creating an account by name: " + accountName)
        DataStore.accounts = new Account(accountName) :: DataStore.accounts
        res.redirect(Paths.Link.Accounts)
        "redirect to accounts"
      }
    }

    def AccountDetail(): Route = (req: Request, res: Response) => {
      val accName = req.params(":accName")
      val accounts = DataStore.accounts.filter(_.name.equals(accName))
      log.info("Filtered accounts for " + accName + " is " + accounts)
      if (accounts.isEmpty) {
        res.redirect("404", 404)
        "You drunk, go home"
      }
      else
        ViewUtil.render(
          req,
          Map(
            "account" -> accounts.head
          ),
          Templates.AccountDetail
        )
    }
  }

}
