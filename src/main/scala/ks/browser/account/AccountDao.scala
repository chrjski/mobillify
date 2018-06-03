/*
 * Created by IntelliJ IDEA.
 * User: k-dev
 * Date: 5/27/2018
 * Time: 4:51 PM
 */
package ks.browser.account

import java.text.SimpleDateFormat
import java.util
import java.util.Date

import ks.mobilify.engine.Mobilify.{Expense, Income}
import ks.mobilify.engine.{DataStore, Mobilify}
import org.slf4j.LoggerFactory
import spark.QueryParamsMap

class AccountDao() {
  def parseTransaction(map: QueryParamsMap) = {
    map.get("transactionType").value() match {
      case "income" => income(map)
      case "expense" => expense(map)
    }
  }


  def parseTransactionMap(map: QueryParamsMap): Mobilify.Transaction = {
    new Mobilify.Transaction {
      override val amount: Float = map.get("amount").floatValue
      override val transactionDate: Date = new SimpleDateFormat("yyyy-MM-dd").parse(map.get("date").value())
      override val category: String = map.get("category").value
      override val description: String = map.get("description").value
    }
  }


  def income(map: QueryParamsMap) = {
    val account = map.get("account").value()

    transaction(new Income(parseTransactionMap(map)))(account)
  }

  def transaction(transaction: Mobilify.Transaction)(account: String) = {
    val logger = LoggerFactory.getLogger(getClass)

    val value: Option[Mobilify.Account] = getMA(account)
    if (value.isDefined) {
      logger.info("Adding " + transaction)
      value.get.transactions.transactions = transaction :: value.get.transactions.transactions
    } else {
      logger.error("Wrong account " + account)
    }
  }

  def expense(map: QueryParamsMap) = {
    val account = map.get("account").value()

    transaction(new Expense(parseTransactionMap(map)))(account)
  }

  def add(accountName: String) = {
    DataStore.accounts = new Mobilify.Account(accountName) :: DataStore.accounts
  }

  def all(): java.util.LinkedList[Account] = {
    DataStore
      .accounts
      .foldLeft(new java.util.LinkedList[Account])((acc, el) => {
        acc.add(ValidAccount(el))
        acc
      })
  }

  private def getMA(accName: String): Option[Mobilify.Account] = DataStore.accounts.filter(_.name.equals(accName)).headOption

  def get(accName: String): Account = {
    val accounts: Option[Mobilify.Account] = getMA(accName)
    if (accounts.isDefined) {
      new ValidAccount(accounts.head)
    } else {
      NAAccount
    }
  }

  def get(accName: String, transactionId: Long): Transaction = {
    val maybeAccount = getMA(accName)
    if (maybeAccount.isDefined) {
      AccountUtil.convert(
      maybeAccount
        .get
        .transactions
        .transactions
        .filter(_.iddate == transactionId).head)
    } else {
      throw new IllegalArgumentException
    }
  }
}

object AccountUtil {
  def convert(el: Mobilify.Transaction) = {
    new Transaction {
      override def getAmount() = el.amount

      override def getDate() = el.transactionDate

      override def getCategory() = el.category

      override def getDescription() = el.description

      override def toString() = el.category + " " + el.amount + " " + el.transactionDate

      def getType(): String = el match {
        case Expense(_, _, _, _) => "expense"
        case Income(_, _, _, _) => "income"
      }

      override def getId(): Long = el.iddate

    }
  }
}

trait Account {
  def getName(): String

  def getTransactions(): util.List[Transaction]
}

case class ValidAccount(account: Mobilify.Account) extends Account {
  override def getName(): String = account.name

  def getBalance() = {
    Mobilify.balance(account.transactions)
  }

  override def getTransactions(): util.List[Transaction] = {
    account
      .transactions
      .transactions
      .foldLeft(new util.LinkedList[Transaction])((acc, el) => {
        val transaction = AccountUtil.convert(el)
        acc.add(transaction)
        acc
      })
  }
}

object NAAccount extends Account {
  override def getName(): String = "N/A"

  override def getTransactions(): util.List[Transaction] = java.util.Collections.emptyList()
}


trait AccountTransactions {
  def getTransactions(): java.util.List[Transaction]

  def isEmpty(): Boolean
}

trait Transaction {
  def getAmount(): Float

  def getDate(): Date

  def getId(): Long

  def getCategory(): String

  def getDescription(): String

  def getType(): String
}