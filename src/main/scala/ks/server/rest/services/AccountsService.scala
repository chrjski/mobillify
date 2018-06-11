package ks.server.rest.services

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.Account
import org.slf4j.{Logger, LoggerFactory}

class AccountsService extends RestService[Account] {
  val log: Logger = LoggerFactory.getLogger(toString)

  override def toString = "AccountsService"

  override def get(name: String): Option[Account] = DataStore.accounts.find(_.name.equals(name))

  override def all: List[Account] = DataStore.accounts

  override def create(any: AnyRef): Account = ???

  override def delete(id: String): Boolean = ???
}
