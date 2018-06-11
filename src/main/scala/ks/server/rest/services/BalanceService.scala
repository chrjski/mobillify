package ks.server.rest.services

import ks.mobilify.engine.Mobilify.Balance
import ks.mobilify.engine.{DataStore, Mobilify}
import org.slf4j.{Logger, LoggerFactory}

class BalanceService extends RestService[Balance] {

  val log: Logger = LoggerFactory.getLogger(toString)

  override def toString = "BalanceService"

  override def all: List[Balance] = DataStore.accounts.foldLeft(List[Balance]())((balances, account) => {
    Balance(account.name, Mobilify.balance(account.transactions)) :: balances
  })

  override def get(id: String): Option[Balance] = DataStore.accounts
    .find(_.name == id)
    .map(x => Balance(x.name, Mobilify.balance(x.transactions)))

  override def create(id: String, any: AnyRef): Balance = ???

  override def delete(id: String): Boolean = ???
}
