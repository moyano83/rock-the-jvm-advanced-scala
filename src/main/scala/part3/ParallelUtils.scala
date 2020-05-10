package part3

import java.util.concurrent.ForkJoinPool
import java.util.concurrent.atomic.AtomicReference

import scala.collection.parallel.ForkJoinTaskSupport

object ParallelUtils extends App {

  // 1 - Parallel collections
  val  parList = List(1,2,3,4,5).par // Operations on this list are handled by multiple threads at the same time
    // this performs better for large collections, care should be taken for reduce and fold operations

  // If we want deeper control of the type of parallelization (number of threads and so on), we do it with task support
  // And we pass the thread manager we want to use
  parList.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(2))

  // Alternatives to ForkJoinTaskSupport
  // - ThreadPoolTaskSupport -> deprecated
  // - ExecutionContextTaskSupport
  // - customTaskSupport defined by you

  // 2 - Atomic operations and references
  val atomic = new AtomicReference[Int](2)

  val currentValue = atomic.get() // thread safe read
  atomic.set(5) // thread safe write

  // other operations
  atomic.getAndSet(4) // does a get an then a set in a thread safe way
  atomic.compareAndSet(38, 56) // if value = 38 (reference equality) then set value = 56 otherwise do nothing,

  atomic.updateAndGet(_ + 1) // updates the value with the function passed and then gets the result
  atomic.getAndUpdate( _+ 3) // gets the value and updates it with the result of the function

  // This receives an argument and a two parameter function,
  atomic.accumulateAndGet(12, _ + _ ) // gets the value, uses the function to add 12 to it, and then sets the value with
  // the result
  atomic.getAndAccumulate(12,  _ + _) // oposite order than the previous function

}
