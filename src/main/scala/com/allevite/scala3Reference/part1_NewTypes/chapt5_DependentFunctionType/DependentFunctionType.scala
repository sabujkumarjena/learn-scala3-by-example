package com.allevite.scala3Reference.part1_NewTypes.chapt5_DependentFunctionType

object DependentFunctionType:
  // A dependent function type is a function type whose result depends on function's parameters

  trait Entry:
    type Key
    def key: Key

  def extractKey(e: Entry): e.Key = e.key   // a dependent method

  val df: (e: Entry) => e.Key = extractKey // a dependent function type

  class MyEntry extends Entry:
    override type Key = String
    override def key: String = "secrete key"

  @main def MAIN(): Unit = println(df(new MyEntry()))