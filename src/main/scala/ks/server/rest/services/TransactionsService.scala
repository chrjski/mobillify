package ks.server.rest.services

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.{Expense, Income, Transaction}
import org.json4s.native.{JsonMethods, Serialization}
import org.json4s.{Formats, ShortTypeHints}
import org.slf4j.{Logger, LoggerFactory}

class TransactionsService extends RestService[Transaction] {

  val log: Logger = LoggerFactory.getLogger(toString)

  override def toString = "TransactionsService"

  override def all: List[Transaction] =
    DataStore.accounts.foldLeft(List[Transaction]())((trans, account) => {
      account.transactions ++ trans
    })

  override def get(id: String): Option[Transaction] = {
    all.find(_.iddate == id.toLong)
  }

  override def create(id: String, any: AnyRef): Transaction = {
    log.info(s"$id for $any")

    implicit val formats: AnyRef with Formats = Serialization.formats(ShortTypeHints(List(classOf[Income], classOf[Expense])))
    val transaction = id match {
      case "income" => JsonMethods.parse(any.asInstanceOf[String]).extract[Income]
      case "expense" => JsonMethods.parse(any.asInstanceOf[String]).extract[Expense]
    }

    val mAccount = new AccountsService().get(transaction.accountName)

    if (mAccount.isDefined) {
      val account = mAccount.get
      account.transactions = transaction :: account.transactions
    } else {
      throw new UnsupportedOperationException(s"cant add transaction to account that does not exists ${transaction.accountName}")
    }

    log.info(s"Added $transaction")

    transaction
  }

  override def delete(id: String): Boolean = {
    val maybeTransaction = get(id)
    log.info(s"$maybeTransaction")
    if (maybeTransaction.isDefined) {
      val toremove = maybeTransaction.get
      val account = new AccountsService().get(toremove.accountName).get
      account.transactions = account.transactions.filter(_.iddate != toremove.iddate)
      true
    } else false
  }
}
