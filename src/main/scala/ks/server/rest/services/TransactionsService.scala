package ks.server.rest.services

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.Transaction
import org.slf4j.{Logger, LoggerFactory}

class TransactionsService extends RestService[Transaction] {

  val log: Logger = LoggerFactory.getLogger(toString)

  override def toString = "TransactionsService"

  override def all: List[Transaction] =
    DataStore.accounts.foldLeft(List[Transaction]())((trans, account) => {
      account.transactions ++ trans
    })

  override def get(id: String): Option[Transaction] = all.find(_.iddate == id.toLong)

  override def create(any: AnyRef): Transaction = ???

  override def delete(id: String): Boolean = ???
}
