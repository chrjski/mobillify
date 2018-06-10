package ks.server.util

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

    def Dashboard(): Route = (req: Request, res: Response) =>
      ViewUtil.render(req, Map(
        "links" -> List((Link.Income, "Income"),
          (Link.Expense, "Expense"),
          (Link.Transfer, "Transfer"),
          (Link.Accounts, "Accounts"),
          (Link.Categories, "Categories")
        ).toArray
      ), Templates.Dashboard)
  }
}
