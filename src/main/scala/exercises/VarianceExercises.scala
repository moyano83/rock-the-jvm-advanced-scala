package exercises

object VarianceExercises extends App{

  class Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle

  //Implement an invariant covariant and contravariant version of parking
  // And Create Monad of parking with flatMap
  class IParking[T](vehicles:List[T]){
    def park[T](vehicle:T):IParking[T]= ???
    def impound(vehicles:List[T]):IParking[T] = ???
    def checkVehicles(conditions:String):List[T] = ???
    def flatMap[S](f: T=> IParking[S] ):IParking[S] = ???
  }

  class CoParking[+T](vehicles:List[T]){
    def park[B>:T](vehicle:B):CoParking[B]= ???
    def impound[B>:T](vehicles:List[B]):CoParking[B] = ???
    def checkVehicles(conditions:String):List[T] = ???
    def flatMap[S](f: T=> CoParking[S] ):CoParking[S] = ???
  }

  class ContraParking[-T](vehicles:List[T]){
    def park(vehicle:T):ContraParking[T]= ???
    def impound[B<:T](vehicles:List[B]):ContraParking[B] = ???
    def checkVehicles[B<:T](conditions:String):List[B] = ???
    def flatMap[B<:T, S](f: B=> ContraParking[S] ):ContraParking[S] = ???
  }

  /* Rule of tumbs
   * - Use covariance if you use it as a covariance of things
   * - If it is a collection of actions you want to perform on your types, use contravariance
   */

}
