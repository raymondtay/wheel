package wheel.codec

/**
  * Leveraging the power of Shapeless's lenses to extract case class fields in
  * a pleasant monadic fashion. To use this, simply import all or
  * selective-imports work as well.
  */
object Codec {

  import cats._, data._, implicits._
  import io.circe.optics.JsonPath._

  def getTitleFromSchema : Reader[io.circe.Json, String] =
    Reader{ (json: io.circe.Json) ⇒ root.title.string.getOption(json).getOrElse("No top-level title")}

  def getTypeFromSchema : Reader[io.circe.Json, String] =
    Reader{ (json: io.circe.Json) ⇒ root.`type`.string.getOption(json).getOrElse("No top-level type")}

  def getPropertiesFromSchema : Reader[io.circe.Json, Option[Vector[io.circe.Json]]] =
    Reader{ (json: io.circe.Json) ⇒ root.properties.arr.getOption(json)}

  def getDefinitionsFromSchema : Reader[io.circe.Json, Option[Vector[io.circe.Json]]] =
    Reader{ (json: io.circe.Json) ⇒ root.definitions.arr.getOption(json)}

}

