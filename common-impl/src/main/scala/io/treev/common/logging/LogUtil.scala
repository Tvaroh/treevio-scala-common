package io.treev.common.logging

object LogUtil {

  def build(message: String, properties: (String, Any)*): String =
    if (properties.isEmpty) {
      message
    } else {
      val propertiesString = properties.view.map(pair ⇒ pair._1 + "=" + pair._2.toString).mkString(", ")
      s"$message [$propertiesString]"
    }

  def serialize(args: Iterable[Any], limit: Int = 10): String = {
    val (limitedArgs, droppedArgs) = args.view.splitAt(limit)
    val effectiveArgs = if (droppedArgs.nonEmpty) limitedArgs ++ Seq("...") else args

    "[" + effectiveArgs.mkString(", ") + "]"
  }

  def serializeOpt(arg: Option[Iterable[Any]]): String =
    arg.fold("<none>")(serialize(_))

}
