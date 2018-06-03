package ks.mobilify.engine

import java.text.SimpleDateFormat
import java.util.Date

import ks.mobilify.engine.Mobilify._

object DataStore {

  private val account = Account("Test acccount 2")
  private val transactions = List(
    new Income(1, "c1", "desc1", new Date) {
      override val transactionDate: Date = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-10")
      override val iddate: Long = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-10").getTime
    },
    new Income(1, "c1", "desc1", new Date){
      override val transactionDate: Date = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-15")
      override val iddate: Long = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-13").getTime
    },
    new Expense(4, "c2", "desc1", new Date){
      override val transactionDate: Date = new SimpleDateFormat("yyyy-MM-dd").parse("2018-04-15")
      override val iddate: Long = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-14").getTime
    },
    new Income(10, "c1", "desc1", new Date){
      override val iddate: Long = new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-15").getTime
    }
  )

  account.transactions = AccountTransactions(transactions)

  var accounts: List[Account] = List(
    Account("Test acccount 1"),
    account,
    Account("Test acccount 3")
  )
}
