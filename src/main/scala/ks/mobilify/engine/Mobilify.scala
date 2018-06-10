package ks.mobilify.engine

import java.util.Date

/**
  * Created by k-dev on 5/9/2018.
  */
object Mobilify extends App {

  trait Transaction {
    val iddate: Long = new Date().getTime

    val accountName: String
    val amount: Float
    val transactionDate: Date
    val category: String
    val description: String
  }

  case class Income(inAmount: Float,
                    override val category: String,
                    override val description: String,
                    override val transactionDate: Date)(override val accountName: String) extends Transaction {
    def this(transaction: Transaction) = this(transaction.amount, transaction.category, transaction.description, transaction.transactionDate)(transaction.accountName)
//    def this(inAmount: Float,
//             category: String,
//             description: String,
//             transactionDate: Date)(account: Account) = this(amount, category, description, transactionDate)(account.name)
    override val amount = inAmount
  }

  case class Expense(exAmount: Float,
                     override val category: String,
                     override val description: String,
                     override val transactionDate: Date)(override val accountName: String) extends Transaction {
    def this(transaction: Transaction) = this(transaction.amount, transaction.category, transaction.description, transaction.transactionDate)(transaction.accountName)
    override val amount = -exAmount
  }

  case class Account(name: String, var test: List[Transaction]) {
//    test = AccountTransactions(List[Transaction]())
    var transactions: AccountTransactions = AccountTransactions(List[Transaction]())
    def getTransactions() : AccountTransactions = transactions
  }

  case class AccountTransactions(var transactions: List[Transaction])

  def balance(account: AccountTransactions): Float = balance(account.transactions)

  def balance(transactions: Iterable[Transaction]): Float =
    transactions.foldLeft(0f)((acc, tr) => acc + tr.amount)

  def categorize(account: AccountTransactions): Map[String, Iterable[Transaction]] = account.transactions.groupBy(_.category)

  override def main(args: Array[String]): Unit = {
    val account = AccountTransactions(List())
    println(
      balance(account.transactions),
//      categorize(account),
//      categorize(account).head._2,
      categorize(account).map(c => (c._1, balance(c._2)))
    )
  }
}
