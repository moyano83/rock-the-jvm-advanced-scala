package part5

object L6_SelfTypes extends App{
  // Self types are a way to requiring a type to be mixed in
  trait Instrumentalist{
    def play()
  }

  // Imagine we want a singer that requires to know how to play an instrument. We can do this like the example below
  trait Singer{self:Instrumentalist => // Whomever implements Singer needs to implement Instrumentalist
    def sing()
  }

  // The compiler requires us to put Instrumentalist or won't compile
  class LeadSinger extends Singer with Instrumentalist {
    def play() = println("Playing")
    def sing() = println("I am singing")
  }

  // Anonymous classes must adhere as well to the constraint impose by Singer
  val jamesHetfield = new Singer with Instrumentalist {
    override def sing(): Unit = println("Master of Puppets")
    override def play() = println("Ñi Ñi Ñi")
  }

  // The order is not important, for example
  class Guitarist extends Instrumentalist{
    def play() = println("Playing guitar")
  }

  val EricClapton = new Guitarist with Singer{
    override def sing(): Unit = println("Tears in Heaven")
  }

  /// Self types are compared with Inheritance:
  class A
  class B extends A //B IS AN A

  trait C
  trait D{self:C=>} //D REQUIRES a C

  // Self types are normaly used in CAKE PATTERN which is the scala equivalent to dependency injection
  // Example: In Java the Dependency injection would be like
  class Component{
    //Some API
  }
  class ComponentA extends Component
  class ComponentB extends Component
  class DependentComponent(val component: Component)
  // The component is injected on runtime and the actual implementation depends on the type injected

  // The equivalent to the above in Scala would be something like
  trait ScalaComponent {
    def action(x:Int):String
  }
  trait ScalaDependentComponent {self:ScalaComponent=>
    // This is possible because we know whomever implements ScalaDependentComponent must implement ScalaComponent
    def dependentAction(x:Int):String = action(x) + "Scala rocks"
  }

  trait ScalaApp{self:ScalaDependentComponent =>}

  //layer 1 - Small Components
  trait Picture extends ScalaComponent
  trait Stats extends ScalaComponent

  //layer 2 - compose
  // At this component you can choose which version of the small components you want to mix in your implementation
  trait Profile extends ScalaDependentComponent with Picture
  trait Analytics extends ScalaDependentComponent with Stats

  //layer 3 - App
  trait AnalyticApp extends ScalaApp with Analytics

  // It is important to note that with the cake pattern this dependencies that we are mixing in are check at COMPILE TIME
  // The self types allow us to define semenly cyclical dependencies, but this cyclic dependency is only apparent.
  trait X{self:Y =>}
  trait Y{self:X =>}
}
