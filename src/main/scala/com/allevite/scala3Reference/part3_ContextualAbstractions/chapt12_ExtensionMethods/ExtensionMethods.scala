package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt12_ExtensionMethods

object ExtensionMethods:
  case class Circle(x: Double, y: Double, radius: Double)

  extension (c: Circle)
    def circumference: Double = c.radius * math.Pi * 2

  /**
   * Translation of Extension Methods
   * An extension method translates to a specially labelled method that takes the leading parameter section
   * as its first argument list. The label, expressed as <extension> here, is compiler-internal.
   * So, the definition of circumference above translates to the following method,
   * and can also be invoked as such:
   *
   * <extension> def circumference(c: Circle): Double = c.radius * math.Pi * 2
   *
   * assert(circle.circumference == circumference(circle))
  */

  //Collective Extensions
  extension (ss: Seq[String])

    def longestStrings: Seq[String] =
      val maxLength = ss.map(_.length).max
      ss.filter(_.length == maxLength)

    def longestString: String = longestStrings.head

  @main def MAIN(): Unit =
    println(List("sabuj", "Deepak", "Debadas").longestString)