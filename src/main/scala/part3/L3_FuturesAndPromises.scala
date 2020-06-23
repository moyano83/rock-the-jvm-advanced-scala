package part3

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Random, Success}
import scala.concurrent.duration._

object L3_FuturesAndPromises extends App {

  def meaningOfLife:Int = {
    Thread.sleep(1000)
    42
  }

  val  aFuture = Future{
    meaningOfLife //calculates the meaning of life on another thread
  }

  aFuture.onComplete(t=> t match {
    case Success(x) => println(x)
    case Failure(exception) => exception.printStackTrace
  })

  // Mini social network
  case class Profile(id:String, name:String){
    def poke(anotherProfile:Profile) = {
      println(s"$this poking $anotherProfile")
    }


    override def toString: String = s"{ id=[${id}], name=[${name}] }"
  }

  object SocialNetwork{
    //database
    val names = Map("id1"->"Mark",
      "id2"-> "Bill",
      "id3" -> "Dummy")

    val friends = Map("id1" -> "id2")

    val randomNumberGen = new Random()

    def fetchProfile(id:String):Future[Profile] = Future{
      Thread.sleep(randomNumberGen.nextInt(300))
      Profile(id, names(id))
    }

    def fetchBestFriend(profile: Profile):Future[Profile]=Future{
      Thread.sleep(randomNumberGen.nextInt(400))
      val bfId = friends(profile.id)
      Profile(bfId, names(bfId))
    }
  }

  // Mark  poke bill
  val opMark = SocialNetwork.fetchProfile("id1")
  opMark.onComplete{ res => res match{
    case Success(mark) => {
      SocialNetwork.fetchBestFriend(mark).onComplete{ res2 => res2 match {
        case Success(bill) => mark.poke(bill)
        case Failure(ex) => ex.printStackTrace()
      }
      }
    }
    case Failure(x) => x.printStackTrace()
    }
  }

  Thread.sleep(500)


  //Functional composition of futures with map, flatMap and filter
  val markFutureName = opMark.map(profile=> profile.name)
  val markBestFriend = opMark.flatMap(profile=> SocialNetwork.fetchBestFriend(profile))
  val markRestrictedBestFriend = markBestFriend.filter(p=> p.name.startsWith("Ana"))

  //We can write for comprehensions
  for{
    mark<-SocialNetwork.fetchProfile("id1")
    bill<- SocialNetwork.fetchBestFriend(mark)
  } mark.poke(bill) //Much cleaner code than the above


  //Fallbacks
  // SocialNetwork.fetchProfile("notExistingId") // We want to recover from this case by passing a dummy profile
  SocialNetwork.fetchProfile("notExistingId").recover{
    case e:Throwable => Profile("id0", "dummy") // This is executed on failure
  }

  val aFetchProfile = SocialNetwork.fetchProfile("notExistingId").recoverWith{
    case e:Throwable => SocialNetwork.fetchProfile("id3") // This is executed on failure in case we want to try anotheroperation
  }

  // If the op is success returns the first op result, otherwise the second op is executed, if succeeded then it returns
  // that result, otherwise it returns the first op throwable
  val fallBackResult = SocialNetwork.fetchProfile("notExistingId").fallbackTo{
    SocialNetwork.fetchProfile("id3")
  }

  // PROMISES AND BLOCKING FUTURES
  case class User(name:String)
  case class Transaction(sender:String, receiver: String, amount:Double, status:String)

  object BankingApp{
    val name="Banking"

    def fetchUser(name:String):Future[User] =Future{
      Thread.sleep(500)
      User(name)
    }

    def createTransaction(user:User, merchantName:String, amount:Double):Future[Transaction] = Future{
      Thread.sleep(1000) // Simultaion of process
      Transaction(user.name, merchantName, amount, "SUCCESS")
    }

    def purchase(user:String, item:String, merchant:String, cost:Double):String = {
      //fetch the user from the DB
      //create the transaction from user to merchant
      // wait for transaction to finish

      val transactionStatusFuture = for{
        user<- fetchUser(user)
        transaction<-createTransaction(user, merchant, cost)
      } yield transaction.status

      Await.result(transactionStatusFuture, 2.seconds) // implicits technique named pimp my library
    }
  }

  println(BankingApp.purchase("user", "iphone", "apple", 2000))

  val promise = Promise[Int]()//Controller over a future
  val future = promise.future //the future is under the control of the promise

  // thread 1 - consumer knows how to handle the future completion
  future.onComplete{
    case Success(x) => println(s"I've received ${x}")
  }


  val producer = new Thread(() =>{
    println("producer crunching numbers")
    Thread.sleep(1000)
    promise.success(42) // Producer is "fullfilling" the promise
    println("Producer done")
  })

  // The pattern is promise creates a future for the consumer that is fullfilled by another thread with a value
  producer.start()
  Thread.sleep(1500)
}
