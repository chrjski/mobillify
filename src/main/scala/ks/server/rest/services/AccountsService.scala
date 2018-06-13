package ks.server.rest.services

import ks.mobilify.engine.DataStore
import ks.mobilify.engine.Mobilify.Account
import org.json4s.native.Serialization
import org.json4s.{Formats, ShortTypeHints}
import org.slf4j.{Logger, LoggerFactory}

class AccountsService extends RestService[Account] {
  val log: Logger = LoggerFactory.getLogger(toString)

  override def toString = "AccountsService"

  override def get(name: String): Option[Account] = DataStore.accounts.find(_.name.equals(name))

  override def all: List[Account] = DataStore.accounts

  override def create(id: String, any: AnyRef): Account = {
    implicit val formats: AnyRef with Formats = Serialization.formats(ShortTypeHints(List(classOf[Account])))
    val account = Serialization.read[Account](any.asInstanceOf[String])
    DataStore.accounts = account :: DataStore.accounts
    account
  }

  override def delete(id: String): Boolean = ???

  override def update(id: String, str: String): Unit = ???
}
