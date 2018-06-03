package ks.server

import ks.server.util.Paths._
import spark.Spark._

/**
  * Created by k-dev on 5/26/2018.
  */
object MobilfyServer extends App {
  port(44)
  notFound("<html><body><h1>Custom 404 handling</h1></body></html>")
  internalServerError("<a href='/'>Go to dashboard</a>")

  get(Link.Dashboard, Routes.Dashboard)

  get(Link.Expense, Routes.Expense)
  get(Link.Income, Routes.Income)
  post(Link.Income, Routes.TransactionPosted)
  post(Link.Expense, Routes.TransactionPosted)

  get(Link.Transfer, Routes.Transfer)

  get(Link.Accounts, Routes.Accounts)
  post(Link.Accounts, Routes.AccountPosted)
  get(Link.AccountDetails, Routes.AccountDetail)
  post(Link.AccountDetailsEditTransaction, Routes.AccountDetailsEditTransaction)

  get(Link.Categories, Routes.Categories)
}
