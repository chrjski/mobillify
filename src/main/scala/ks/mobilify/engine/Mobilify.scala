package ks.mobilify.engine

import java.util.Date

/**
  * Created by k-dev on 5/9/2018.
  */
object Mobilify extends App {

  abstract class Transaction(val amount: Float,
                             val category: String,
                             val description: String,
                             val transactionDate: Date,
                             val iddate: Long)(val accountName: String)

  case class Income(override val amount: Float,
                    override val category: String,
                    override val description: String,
                    override val transactionDate: Date,
                    override val iddate: Long = new Date().getTime)(override val accountName: String)
    extends Transaction(amount, category, description, transactionDate, iddate = new Date().getTime)(accountName)

  case class Expense(expense: Float,
                     override val category: String,
                     override val description: String,
                     override val transactionDate: Date,
                     override val iddate: Long = new Date().getTime)(override val accountName: String)
    extends Transaction(amount = -expense, category, description, transactionDate, iddate = new Date().getTime)(accountName)

  case class Account(name: String, var transactions: List[Transaction])

  def balance(transactions: Iterable[Transaction]): Float =
    transactions.foldLeft(0f)((acc, tr) => acc + tr.amount)

  def categorize(transactions: Iterable[Transaction]): Map[String, Iterable[Transaction]] = transactions.groupBy(_.category)

  case class Balance(accountName: String, balance: Float)
}
