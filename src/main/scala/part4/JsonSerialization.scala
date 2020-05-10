package part4

import java.util.Date

import part4.JsonSerialization.JSONInt

object JsonSerialization extends App {

  /**
    * We have a social network that has Users, posts feeds and we want to serialize them
    */
  case class User(name: String, age: Int, email: String)

  case class Post(content: String, createdAt: Date)

  case class Feed(user: User, posts: List[Post])

  /* Steps to serialize the above
   * 1- Create intermediate types for Int,String,List,Date...
   * 2- Create type classes for conversion from case classes to intermediate data types
   * 3- Serialize the intermediate data types to JSON
   */

  //1- Create intermediate types
  sealed trait JSONValue {
    def stringify: String
  }

  final case class JSONString(value: String) extends JSONValue {
    override def stringify: String = "\"" + value + "\""
  }

  final case class JSONInt(value: Int) extends JSONValue {
    override def stringify: String = value.toString
  }

  final case class JSONList(value: List[JSONValue]) extends JSONValue {
    override def stringify: String =value.map(_.stringify).mkString("[",",","]")
  }

  final case class JSONObject(values: Map[String, JSONValue]) extends JSONValue {
    override def stringify: String = values.map {
      case (k,v) => "\"" + k + "\":" + v.stringify
    }.mkString("{",",","}")
  }

  val data = JSONObject(Map(
    "user" -> JSONString("Jorge"),
    "Post" -> JSONList(List(
      JSONString("Scala Course"),
      JSONInt(10)
    ))
  ))
  println(data.stringify)

  trait JSONConverter[T]{
    def convert(t:T):JSONValue
  }

  implicit object StringConverter extends JSONConverter[String]{
    override def convert(t: String): JSONValue = JSONString(t)
  }

  implicit object IntConverter extends JSONConverter[Int]{
    override def convert(t: Int): JSONValue = JSONInt(t)
  }

  implicit object ListConverter extends JSONConverter[List[JSONValue]]{
    override def convert(t: List[JSONValue]): JSONValue = JSONList(t)
  }

  implicit class JSONOps[T](value:T){
    def toJson(implicit converter:JSONConverter[T]):JSONValue = converter.convert(value)
  }

  implicit object JSONConverterUser extends JSONConverter[User]{
    override def convert(t: User): JSONValue = JSONObject(Map(
      "name" -> JSONString(t.name),
      "age" -> JSONInt(t.age),
      "email" -> JSONString(t.email)
    ))
  }

  implicit object JSONConverterPost extends JSONConverter[Post]{
    override def convert(t: Post): JSONValue = JSONObject(Map(
      "content" -> JSONString(t.content),
      "createdAt" -> JSONString(t.createdAt.toString)
    ))
  }

  implicit object JSONConverterFeed extends JSONConverter[Feed]{
    override def convert(t: Feed): JSONValue = JSONObject(Map(
      "user"-> t.user.toJson,
      "posts"-> JSONList(t.posts.map(_.toJson))
    ))
  }

  val now = new Date(System.currentTimeMillis())
  val john = User("john", 34, "john@test.com")
  val feed = Feed(john, List(
    Post("Hello", now),
    Post("Bye", now)
  ))

  println(feed.toJson.stringify)

}
