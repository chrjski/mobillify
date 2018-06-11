package ks.mobilify.engine

import java.text.SimpleDateFormat
import java.util.Date

import ks.mobilify.engine.Mobilify._

object DataStore {
  private val income1 = Income(2, "c1", "desc1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-10"))("Test acccount 2")
  private val income2 =  Income(2, "c1", "desc1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-15"))("Test acccount 2")
  private val expense1 = Expense(4, "c2", "desc1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-04-15"))("Test acccount 2")
  private val income3 =  Income(5, "c1", "desc1", new Date)("Test acccount 2")
  private val transactions = List(income1, income2, expense1, income3)

  private val account = Account("Test acccount 2", transactions)

  var accounts: List[Account] = List(
    Account("Test acccount 1", List()),
    account,
    Account("Test acccount 3", List())
  )
}