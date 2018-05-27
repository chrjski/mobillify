/*
 * Created by IntelliJ IDEA.
 * User: k-dev
 * Date: 5/27/2018
 * Time: 4:51 PM
 */
package ks.browser.account

import java.util
import java.util.Date

import ks.mobilify.engine.{DataStore, Mobilify}

class AccountDao() {
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

  def get(accName: String): Account = {
    val accounts = DataStore.accounts.filter(_.name.equals(accName))
    if (accounts.isEmpty) {
      NAAccount
    } else {
      new ValidAccount(accounts.head)
    }
  }

}

trait Account {
  def getName(): String

  def getTransactions(): util.List[Transaction]
}

case class ValidAccount(account: Mobilify.Account) extends Account {
  override def getName(): String = account.name

  override def getTransactions(): util.List[Transaction] = {
    account
      .transactions
      .transactions
      .foldLeft(new util.LinkedList[Transaction])((acc, el) => {
        val transaction = new Transaction {
          override def getAmount(): Int = el.amout

          override def getDate(): Date = el.date

          override def getCategory(): String = el.category

          override def getDescription(): String = el.description

          override def toString(): String = el.category + " " + el.amout + " " + el.date
        }
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
  def getAmount(): Int

  def getDate(): Date

  def getCategory(): String

  def getDescription(): String
}