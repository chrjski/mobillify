package ks.mobillis.engine

import java.util.Date

/**
  * Created by k-dev on 5/9/2018.
  */
object Mobilify extends App {

  trait Transaction {
    val amout: Int
    val date: Date = new Date()
    val category: String
    val description: String
  }

  case class Income(inAmount: Int,
                    override val category: String,
                    override val description: String) extends Transaction {
    override val amout: Int = inAmount
  }

  case class Expense(exAmount: Int,
                     override val category: String,
                     override val description: String) extends Transaction {
    override val amout: Int = -exAmount
  }

  case class Account(transactions: Iterable[Transaction])

  def balance(account: Account): Int = balance(account.transactions)

  def balance(transactions: Iterable[Transaction]): Int = transactions.foldLeft(0)((acc, tr) => acc + tr.amout)

  def categorize(account: Account): Map[String, Iterable[Transaction]] = account.transactions.groupBy(_.category)

  override def main(args: Array[String]): Unit = {
    val account = Account(
      List(
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
