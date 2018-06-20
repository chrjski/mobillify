package ks.mobilify.engine

import java.text.SimpleDateFormat

import ks.mobilify.engine.Mobilify._

object DataStore {

  val loremipsum3 = "facilisis viverra Quisque tempor vehicula neque"
    .replaceAll(",", "")
    .replaceAll("\\.", "")
    .replaceAll("\n", "")
    .toLowerCase()
    .split(" ")

  val loremipsum2 = "Donec sodales venenatis tortor. Cras viverra"
    .replaceAll(",", "")
    .replaceAll("\\.", "")
    .replaceAll("\n", "")
    .toLowerCase()
    .split(" ")

  val loremipsum = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin rutrum diam a nunc elementum, in dapibus quam placerat. Cras imperdiet mauris at sem convallis facilisis. Donec ullamcorper porttitor mauris vitae consectetur. Etiam a fermentum lorem, condimentum dignissim diam. Sed nec odio non erat gravida ultricies et id risus. Pellentesque condimentum sem et justo sagittis semper. Praesent feugiat nibh tincidunt libero maximus, id malesuada sem pretium. Vivamus felis magna, sagittis auctor libero a, egestas sagittis tellus. Maecenas justo diam, finibus sit amet bibendum ut, dignissim et magna.\n\nVestibulum ultricies leo eu ligula faucibus accumsan. Nam pulvinar mi felis, ac interdum ante vestibulum et. Sed ac vulputate metus. Quisque quis nisl ultrices, placerat eros a, convallis felis. Phasellus laoreet lectus sed ligula fermentum, nec varius libero consectetur. Phasellus euismod, ipsum vel facilisis tristique, augue lectus molestie lectus, ut porttitor metus neque eu nibh. Integer semper fermentum odio. Etiam vulputate nunc ligula. Donec non sapien tristique, placerat felis et, ultrices enim. Integer quis imperdiet diam, a consequat turpis. Sed vitae lorem enim. Aliquam commodo purus varius tellus commodo placerat. Sed auctor velit vitae ipsum maximus, ac vehicula felis convallis. Aliquam erat volutpat. Quisque faucibus est odio, id commodo arcu aliquet ut. Donec ultrices erat at lacus tincidunt, ut ornare diam fermentum.\n\nAenean facilisis lacus id mi efficitur, id egestas dolor ornare. Vestibulum nec nisl est. Vestibulum ut orci posuere, volutpat ex nec, venenatis mauris. Aenean sagittis varius justo quis porttitor. Proin nec ex sit amet nulla dapibus pulvinar sed sit amet orci. Nullam et tortor nec erat consequat auctor non viverra nisl. Praesent tempus at nunc ut pulvinar. In ut sem quis diam egestas lobortis viverra at nisl. Nunc sagittis elementum lorem, ut dignissim turpis porttitor non. Suspendisse aliquam orci sed velit tincidunt fringilla.\n\nDuis in neque elit. Donec sit amet ligula nec tellus aliquam posuere at quis massa. Nam mattis accumsan ex ac gravida. Aenean convallis purus tortor, in tempor nunc aliquam nec. Quisque aliquam enim urna, nec vehicula metus gravida ut. Interdum et malesuada fames ac ante ipsum primis in faucibus. Aliquam lacinia lacinia tortor, quis convallis nulla pretium sed. Duis varius, leo non pellentesque consequat, erat velit posuere orci, ac interdum lorem risus nec dolor. Donec ut commodo libero, ut consectetur turpis. Nullam mauris risus, pulvinar ac diam tincidunt, condimentum fringilla lectus. Fusce erat velit, pharetra sed interdum id, laoreet at neque.\n\nAliquam ut ipsum nec libero elementum posuere. Nulla eu feugiat dui. Nam eros enim, dapibus id ex nec, semper mollis ipsum. Vestibulum eu dui pharetra, malesuada est quis, luctus quam. Sed facilisis purus non lectus fringilla iaculis. Mauris tempor, sapien at sollicitudin porta, velit metus luctus nibh, ac dignissim odio nisi vel nibh. Integer commodo urna lectus, sit amet semper enim rutrum ac. Cras feugiat est sit amet nunc pulvinar ullamcorper. Morbi a justo convallis, bibendum ante at, tincidunt enim. Praesent tincidunt libero tortor, nec ullamcorper purus sodales fermentum."
    .replaceAll(",", "")
    .replaceAll("\\.", "")
    .replaceAll("\n", "")
    .toLowerCase()
    .split(" ")

  //  private
  object DSGenerator {
    def randInc = {
      Income(
        randIncome,
        randCat(incCat),
        randDesc,
        randDate,
        randAcc
      )
    }

    def randExp = {
      Expense(
        randExpense,
        randCat(expCat),
        randDesc,
        randDate,
        randAcc
      )
    }

    def randTrans(p: Double) = p match {
      case x if x < 0.9 => randExp
      case _ => randInc
    }

    def randIncome = (Math.random() * 1000).toInt

    def randExpense = (Math.random() * 100).toInt

    private def randAcc = {
      val ix = Math.random() * 3 match {
        case x if x < 0.8 => 0
        case x if x > 0.8 && x < 0.9 => 1
        case x if x > 0.9 => 2
      }
      val accounts = Map(
        0 -> "Konto",
        1 -> "Portfel 1",
        2 -> "Portfel 2"
      )

      accounts(ix)
    }

    private def randDate = {
      new SimpleDateFormat("yyyy-MM-dd").parse(s"2018-${(Math.random() * 12 + 1).toInt}-${(Math.random() * 27 + 1).toInt}")
    }

    private def randDesc = {

      val description = for {
        lenght <- 1 to (Math.random() * 5 + 2).toInt
      } yield loremipsum((Math.random() * loremipsum.length).toInt)
      description mkString " "
    }

    def randCat(m: Map[Int, String]) = m((Math.random() * m.size).toInt)

    val emojis = (for (emoji <- 0 until 100) yield emoji -> (s"&#${9800 + emoji}")).toMap

    val incCat = (for (ix <- 0 until loremipsum2.length) yield ix -> loremipsum2(ix)).toMap
    val expCat = (for (ix <- 0 until loremipsum3.length) yield ix -> loremipsum3(ix)).toMap

    val cat2emoji =
      incCat.map(x => x._2 -> emojis(x._1)) ++
        expCat.map(x => x._2 -> emojis(x._1 + incCat.size))
  }
    //  private val income1 = Income(2, "c1", "desc1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-10"), "Test acccount 2")
    //  Thread.sleep(2000) // testing only
    //  private val income2 = Income(2, "c1", "desc1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-02-15"), "Test acccount 2")
    //  Thread.sleep(5)
    //  private val expense1 = Expense(4, "c2", "desc1", new SimpleDateFormat("yyyy-MM-dd").parse("2018-04-15"), "Test acccount 2")
    //  Thread.sleep(5)
    //  private val income3 = Income(5, "c1", "desc1", new Date, "Test acccount 2")
    //  private val transactions = List(income1, income2, expense1, income3)
    //
    //  private val account = Account("Test acccount 2", transactions)

    //  var accounts: List[Account] = List(
    //    Account("Test acccount 1", List()),
    //    account,
    //    Account("Test acccount 3", List())
    //  )

  println(DSGenerator.cat2emoji)
  def getEmoji(category: String) = DSGenerator.cat2emoji(category)
  var accounts = (for (times <- 1 to 100) yield DSGenerator.randTrans(Math.random())).toList
    .groupBy(_.accountName)
    .map(x => Account(x._1, x._2))
    .toList
  }