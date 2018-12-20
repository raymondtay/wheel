package wheel

/* for parsing json */
import wheel.codec._

/* scala annotations */
import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.experimental.macros
import scala.reflect.api.Universe
import scala.reflect.macros.Context

/* circe */
import io.circe._, parser._, syntax._

/* cats */
import cats._, data._, implicits._

// Materializes the json codecs by discovering all known subtypes
// and restriction we want to have is to make sure they are all 
// case classes
object Materalize {

  def materializeCodec[T]: Any = macro impl[T]

  def impl[T: c.WeakTypeTag](c: Context): c.Tree = {
    import c.universe._
    import definitions._
    import Flag._

    val sym = c.weakTypeOf[T].typeSymbol
    if (!sym.asClass.isSealed || !sym.asClass.isTrait) c.abort(c.enclosingPosition, s"$sym needs to be a sealed trait.")
    val fields = sym.typeSignature.declarations.toList.collect{ case x: TermSymbol if x.isVal && x.isCaseAccessor ⇒ x }

    val jsonModels = sym.asClass.knownDirectSubclasses

    q""
  }

}

@compileTimeOnly("Enable macro paradise to use macro annotations.")
class CrankIt(jsonStringToBeParsed: String,
             pathToJsonSchema: Option[String] = None) extends StaticAnnotation {
  def macroTransform(annottees: Any*) : Any = macro Macros.impl
}

class Macros(val c: Context) {
  import c.universe._
  private val modelBuilder = new ModelBuilder[c.universe.type](c.universe)
  private val codecBuilder = new CodecBuilder[c.universe.type](c.universe)

  private def extractAnnotationArgs(c: Context)(args: c.universe.Tree) = {
    val q"new $className(..$jsonAST)" = args
    jsonAST.toString
  }

  def impl(annottees: c.Expr[Any]*) : c.Tree = {
    val jsonToBeParsed = extractAnnotationArgs(c)(c.prefix.tree)
    annottees.map(_.tree) match {
      case List(q"""object $toplevelScopeName""") ⇒ {
        val topLevelImports   = q"""import io.circe._""" :: q"""import io.circe.generic.semiauto._""" :: Nil
        val schemaDefinitions: List[Macros.this.modelBuilder.u.Tree] = modelBuilder.fromJson(jsonToBeParsed)
        val codecDefinitions : List[Macros.this.codecBuilder.u.Tree] = codecBuilder.buildJsonCodec(jsonToBeParsed)
        q"""
          ..$topLevelImports
          object $toplevelScopeName {

            ..$schemaDefinitions

            ..$codecDefinitions
          }
        """
      }
      case _ ⇒ c.abort(c.enclosingPosition, "This annotation can only be used with objects.")
    } 
  }
}

class ModelBuilder[U <: Universe](val u: U) {
  import u._
  import Flag._

  def fromJson(json : String) = {
    val j : Json = parse(json).getOrElse(Json.Null)
    import Codec._
    val (title, schemaType, defns, properties) =
      (getTitleFromSchema(j), getTypeFromSchema(j), getDefinitionsFromSchema(j), getPropertiesFromSchema(j))
    defns.get.map(defn ⇒  genDef(defn.asObject.get.toMap)).toList
  }

  private def genDef(map: Map[String, io.circe.Json]) = {
    val cName = map.keys.head
    val cBody = extractParams(map(cName)).toList
    buildModel(cName)(cBody)
  }

  private def extractParams(body : io.circe.Json) = {
    for {
      (k,v) <- body.asObject.get.toMap if k != "type"
    } yield {
      val tpeName = lookup(v.asObject.get.toMap("type"))
      q"val ${TermName(k)} : $tpeName"
    }
  }

  private def lookup(typeName: io.circe.Json) = {
    typeName.asString match {
      case Some("[string]") ⇒ AppliedTypeTree(Ident(TypeName("List")), List(Ident(TypeName("String"))))
      case Some("[integer]") ⇒ AppliedTypeTree(Ident(TypeName("List")), List(Ident(TypeName("Int"))))
      case Some("[bool]") ⇒ AppliedTypeTree(Ident(TypeName("List")), List(Ident(TypeName("Boolean"))))
      case Some("string") ⇒ tq"String"
      case Some("integer") ⇒ tq"Int"
      case Some("bool") ⇒ tq"Boolean"
      case _ ⇒ tq"Any"
    }
  }

  private def buildModel(className: String)(params: List[Tree]) : Tree = {
    q"""
      case class ${TypeName(className)}(..$params)
    """
  }

}

class CodecBuilder[U <: Universe](val u: U) {
  import u._
  import Flag._

  def buildJsonCodec(json: String) : List[ValDef] = {
    val j : Json = parse(json).getOrElse(Json.Null)
    import Codec._
    val defns = getDefinitionsFromSchema(j)

    defns.get.map{defn ⇒ 
      val className = defn.asObject.get.toMap.keys.head
      val mods = Modifiers(IMPLICIT)
      val decoderName = TermName(s"${className}Decoder")
      val encoderName = TermName(s"${className}Encoder")
      q"""
      $mods val $decoderName : io.circe.Decoder[${TypeName(className)}] = deriveDecoder[${TypeName{className}}]
      """ :: q"""
      $mods val $encoderName : io.circe.Encoder[${TypeName(className)}] = deriveEncoder[${TypeName{className}}]
      """ :: Nil
    }.toList.flatten
  }

}

