package ks.mobilify.engine

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by k-dev on 5/9/2018.
  */
object Mobilify extends App {

  abstract class Transaction(val value: Float,
                             val category: String,
                             val description: String,
                             val transactionDate: Date,
                             val iddate: Long)(val accountName: String)

  case class Income(override val value: Float,
                    override val category: String,
                    override val description: String,
                    override val transactionDate: Date,
                    override val accountName: String,
                    override val iddate: Long = new Date().getTime
                   )
    extends Transaction(value, category, description, transactionDate, iddate = new Date().getTime)(accountName) {
    def this(amount: Float,
             category: String,
             description: String,
             transactionDate: String,
             accountName: String) = this(amount, category, description, new SimpleDateFormat("yyyy-MM-dd").parse(transactionDate), accountName, new Date().getTime)
  }

  case class Expense(amount: Float,
                     override val category: String,
                     override val description: String,
                     override val transactionDate: Date,
                     override val accountName: String,
                     override val iddate: Long = new Date().getTime
                    )
    extends Transaction(value = -amount, category, description, transactionDate, iddate = new Date().getTime)(accountName) {
    def this(amount: Float,
             category: String,
             description: String,
             transactionDate: String,
             accountName: String) = this(amount, category, description, new SimpleDateFormat("yyyy-MM-dd").parse(transactionDate), accountName, new Date().getTime)

  }

  case class Account(name: String, var transactions: List[Transaction])

  def balance(transactions: Iterable[Transaction]): Float =
    transactions.foldLeft(0f)((acc, tr) => acc + tr.value)

  def categorize(transactions: Iterable[Transaction]): Map[String, Iterable[Transaction]] = transactions.groupBy(_.category)

  case class Balance(accountName: String, balance: Float)
}
