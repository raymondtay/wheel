package wheel

import scala.language.experimental.macros
import scala.reflect.macros.Context
import scala.annotation._

/**
 * Note: macro annotations cannot be applied to case classes, it seems
 */
@compileTimeOnly("enable macro paradise to use macro annotations")
class Infuse extends StaticAnnotation {
  def macroTransform(annottees: Any*) = macro Infuser.impl
}

object Infuser {
  def impl(c: Context)(annottees: c.Expr[Any]*) : c.Expr[Any] = {
    import c.universe._
    println(annottees)
    val cName =
      annottees.map(_.tree) match {
        //case List(q"class $containerName") => containerName
        case List(q"object $containerName") => containerName
        case _ => c.abort(c.enclosingPosition, "This annotation can only be used with objects.")
      } 
    val varName = q"i"
    val varTypeName = tq"Int"
    val params = q"$varName : $varTypeName" 
    val imports = q"""import io.circe._""" :: q"""import io.circe.generic.semiauto._""" :: Nil

    c.Expr(q"""
      ..$imports
      object $cName {
        case class X(..$params)
        implicit val xDecoder : Decoder[X] = deriveDecoder[X]
        implicit val xEncoder : Encoder[X] = deriveEncoder[X]
      }
    """)
  }
}
