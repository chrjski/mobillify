package ks.mobilify.engine

import ks.mobilify.engine.Mobilify._

object DataStore {

  var accounts: List[Account] = List(
    Account("Test acccount 1"),
    Account("Test acccount 2"),
    Account("Test acccount 3")
  )
//    (0 to 10).foldLeft(List[Account]())((acc, el) => {
//      Account("Undefined:" + 1) :: acc
//    })

}
