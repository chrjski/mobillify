package ks.server.rest.services

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify._
import org.json4s.ShortTypeHints
import org.json4s.native.Serialization
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

    val transaction: Transaction =
      any match {
        case x: Transaction => x
        case _ => jsonToTransaction(id, any)
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

  private def jsonToTransaction(id: String, any: AnyRef) = {
    // TODO use field serializer
    //    val incomeSerializer =  FieldSerializer[Income]()

    implicit val formats = Serialization.formats(ShortTypeHints(
      List(
        classOf[Income],
        classOf[Expense]
      )))
    val json = any.asInstanceOf[String]

    val transaction = id match {
      case "income" => Serialization.read[Income](json)
      case "expense" => Serialization.read[Expense](json)
    }
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

  override def update(id: String, json: String): Unit = {
    log.info(s"$id for $json")
    val maybeTransaction = get(id)

    if (maybeTransaction.isDefined) {
      log.info(s"test ${maybeTransaction.get} + \n $json")
      val replaceBy = maybeTransaction.get match {
        case Income(_, _, _, _, _, _) => jsonToTransaction("income", json)
        case Expense(_, _, _, _, _, _) => jsonToTransaction("expense", json)
      }

      val mAccount = new AccountsService().get(replaceBy.accountName)

      if (mAccount.isDefined) {
        val account = mAccount.get
        val toReplace = maybeTransaction.get

        if (account.name == replaceBy.accountName) {
          account.transactions = account.transactions.map({
            case x if x.iddate == toReplace.iddate => replaceBy
            case x => x
          })
        } else {
          throw new UnsupportedOperationException(s"Cant update transaction in differnt accounts ${replaceBy.accountName} and ${toReplace.accountName}")
        }
      } else {
        throw new UnsupportedOperationException(s"cant add replaceBy to account that does not exists ${replaceBy.accountName}")
      }

      log.info(s"Added $replaceBy")
      replaceBy
    }
  }
}
