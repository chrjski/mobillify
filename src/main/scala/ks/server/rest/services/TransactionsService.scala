package ks.server.rest.services

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.Transaction

class TransactionsService extends RestService[Transaction] {
  override def all: List[Transaction] =
    DataStore.accounts.foldLeft(List[Transaction]())((trans, account) => {
      account.transactions.transactions ++ trans
    })

  override def get(id: String): Option[Transaction] = ???

  override def create(any: AnyRef): Transaction = ???

  override def delete(id: String): Boolean = ???
}
