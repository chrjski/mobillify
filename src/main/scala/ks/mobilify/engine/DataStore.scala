package ks.mobilify.engine

import ks.mobilify.engine.Mobilify._

object DataStore {

  private val account = Account("Test acccount 1")
  private val transactions = List(
    Income(1, "c1", "desc1"),
    Income(1, "c1", "desc1"),
    Expense(4, "c2", "desc1"),
    Income(10, "c1", "desc1")
  )

  account.transactions = AccountTransactions(Expense(4, "c2", "desc1") :: transactions)

  var accounts: List[Account] = List(
    account,
    Account("Test acccount 2"),
    Account("Test acccount 3")
  )
}
