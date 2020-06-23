package part3

import java.util.concurrent.Executors

object L1_Intro extends App {


  val aThread = new Thread(() => println("Inside the thread"))

  aThread.start()
  aThread.join // Blocks until aThread finish running

  val pool = Executors.newFixedThreadPool(10)
  pool.execute(() => println("Something in the Thread pool"))

  pool.execute(() => Thread.sleep(100))

  pool.execute(() => {
    Thread.sleep(100)
    println("Done after 0.1s")
  })
  pool.execute(() => {
    Thread.sleep(100)
    println("Almost Done")
    Thread.sleep(2000)
    println("Done after 2s")
  })

  pool.shutdown() //waits until the threads are done, but no more runnables can be submitted
  //pool.shutdownNow() // running threads would throw exceptions

  def runInParallel = {
    var x = 0

    val t1 = new Thread(()=> x = 1)

    val t2 = new Thread(()=> x = 2)

    t1.start()
    t2.start()
    println(x)

  }
  for (_ <- 1 to 100) runInParallel //race condition

  class BankAccount(var amount:Int){
    override def toString = s"The account has:$amount"
  }

  def buy(account:BankAccount, thing:String, money:Int) = {
    account.amount -= money
    println(s"I've bought $thing")
    println(account)
  }

  for (_ <- 1 to 10){
    val account = new BankAccount(50000)
    val t1 = new Thread(()=> buy(account, "shoes", 3000))
    val t2 = new Thread(()=> buy(account, "phone", 4000))

    t1.start()
    t2.start()
    Thread.sleep(100)
    if(account.amount != 43000) println("Found race condition!" + account.toString)
  } // race condition as the assign operation is not atomic, but instead it does amount = amount - money

  //How to solve race conditions

  // Option 1:
  def buySafe(account:BankAccount, thing:String, money:Int) ={
    account.synchronized{
      account.amount -= money
    }
    println(s"I've bought $thing")
    println(account)
  }

  // Option 2: Add @Volatile
  // Volatile means that all access to the variable marked with it are synchronized
  class BankAccountVolatile(@volatile var amount:Int){
    override def toString = s"The account has:$amount"
  }

  //Exercise 1: Construct 50 inception threads, everythread should print "Hello id" and print it in reverse order


  class InceptionThread(val id:Int) extends Runnable{
    override def run(): Unit = {
      if(id -1 >= 0) {
        val t = new Thread(new InceptionThread(id -1))
        t.start()
        t.join()
        println(s"Hello this is  ${id}")
      }
    }
  }

  new Thread(new InceptionThread(50)).start()

  // Exercise 2:
}
