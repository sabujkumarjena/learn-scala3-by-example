package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt19_ByNameContextParameters

object ByNameContextParameters:

  //By-Name Context Parameters
  // Context parameters can be declared by-name to avoid a divergent inferred expansion.

  trait Codec[T]:
    def write(x: T): Unit

  given intCodec: Codec[Int] = ???

  given optionCodec[T](using ev: => Codec[T]): Codec[Option[T]] with
    override def write(xo: Option[T]): Unit = xo match
      case Some(x) => ev.write(x)
      case None =>

  val optCodec = summon[Codec[Option[Int]]]

  optCodec.write(Some(33))
  optCodec.write(None)

  /**
   * As is the case for a normal by-name parameter, the argument for the context parameter ev is evaluated on demand.
   * In the example above, if the option value x is None, it is not evaluated at all.
  */