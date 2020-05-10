package part3

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

object ThreadCommunication extends App {

  /**
    * The producer consumer problem:
    * producer tries to set a value in a container object
    * the consumer tries to read the object in the container object
    *
    * The problem is to force threads to run in a guaranteed order (first produce then consume)
    */

  class SimpleContainer{
    private var value :Int = 0

    def isEmpty:Boolean = value == 0
    def get():Int = {
      val res = value
      value = 0
      res
    }

    def set(newValue:Int) = value = newValue
  }

  def naiveConsumer():Unit= {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("Consumer started to wait")
      while (container.isEmpty) {
        Thread.sleep(50)
        println("Consumer waiting")
      }
      println("I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("Producer starting to produce")
      Thread.sleep(100)
      val value = Random.nextInt()
      println("I have produced "+ value)
      container.set(value)
    })

    consumer.start()
    producer.start()
  }

  //naiveConsumer()
  // synchronized methods are available on AnyRef types (not in AnyVal types)
  //wait and notify
  // wait suspends the current thread indefinitely and releases the lock in the synchronized object
  // notify/notifyAll signals one/all sleeping thread to continue (you don't know which Thread will get the lock)

  def smartProducerConsumer() = {
    val container = new SimpleContainer

    val consumer = new Thread(() => {
      println("Consumer started to wait")
      container.synchronized {
        container.wait()
      }
      println("I have consumed " + container.get)
    })

    val producer = new Thread(() => {
      println("Producer starting to produce")
      Thread.sleep(100)
      val value = Random.nextInt()
      println("I have produced "+ value)
      container.synchronized {
        container.set(value)
        container.notify()
      }
    })

    consumer.start()
    producer.start()
  }

  //smartProducerConsumer()

  class ComplexContainer{
    private var value :List[Int] = Nil

    def isEmpty:Boolean = value.isEmpty
    def isFull:Boolean = value.size >=3

    def get():Int = {
      val res = value.head
      value = value.tail
      res
    }

    def set(newValue:Int) = value = newValue :: value
  }

  def multiProducerConsumer() = {
    val container = new ComplexContainer

    val consumer = new Thread(() => {
      while(true) {
        container.synchronized {
          println("Consumer started to wait")
          if(container.isEmpty) {
            println("Consumer wait for empty buffer")
            container.wait()
          }
          println("I have consumed " + container.get)
          container.notify()
        }
        println("Consumer to sleep")
        Thread.sleep(Random.nextInt(500).abs)
      }
    })

    val producer = new Thread(() => {
      while(true) {
        container.synchronized {
          println("Producer starting to produce")
          if (container.isFull) {
            println("Producer wait for full buffer")
            container.wait()
          }
          val value = Random.nextInt()
          println("I have produced " + value)
          container.set(value)
          container.notify()
        }
        println("Producer to sleep")
        Thread.sleep(Random.nextInt(500 ).abs)
      }
    })

    consumer.start()
    producer.start()
  }
  multiProducerConsumer()
}
