package ks.mobilify.engine

import java.util.Date

/**
  * Created by k-dev on 5/9/2018.
  */
object Mobilify extends App {

  trait Transaction {
    val iddate: Date = new Date()

    var amount: Float
    var transactionDate: Date = new Date()
    var category: String
    var description: String
  }

  case class Income(inAmount: Float,
                    override val category: String,
                    override val description: String) extends Transaction {
    override val amount = inAmount
  }

  case class Expense(exAmount: Float,
                     override val category: String,
                     override val description: String) extends Transaction {
    override val amount = -exAmount
  }

  case class Account(name: String) {
    var transactions: AccountTransactions = AccountTransactions(List[Transaction]())
  }

  case class AccountTransactions(var transactions: List[Transaction])

  def balance(account: AccountTransactions): Float = balance(account.transactions)

  def balance(transactions: Iterable[Transaction]): Float =
    transactions.foldLeft(0f)((acc, tr) => acc + tr.amount)

  def categorize(account: AccountTransactions): Map[String, Iterable[Transaction]] = account.transactions.groupBy(_.category)

  override def main(args: Array[String]): Unit = {
    val account = AccountTransactions(List(
            Income(1, "c1", "desc1"),
            Income(1, "c1", "desc1"),
            Expense(4, "c2", "desc1"),
            Income(10, "c1", "desc1"),
            Expense(4, "c2", "desc1")
          ))
    println(
      balance(account.transactions),
//      categorize(account),
//      categorize(account).head._2,
      categorize(account).map(c => (c._1, balance(c._2)))
    )
  }
}
