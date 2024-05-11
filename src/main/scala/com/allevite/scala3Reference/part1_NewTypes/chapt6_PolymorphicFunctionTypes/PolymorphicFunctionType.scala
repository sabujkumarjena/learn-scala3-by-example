package com.allevite.scala3Reference.part1_NewTypes.chapt6_PolymorphicFunctionTypes

object PolymorphicFunctionType :
  //A polymorphic method
  def foo[A](xs: List[A]): List[A] = xs.reverse

  //A polymorphic function
  val bar: [A] => List[A] => List[A] =
    [A] => (xs: List[A]) => xs.reverse

  val toMap: [K,V] => K => V => Map[K,V] =
  [K,V] => (key: K) => (value: V) => Map(key -> value)

  @main def MAIN(): Unit = ()
    println(bar(List(1,2,3)))
    println(toMap("price")(35))
