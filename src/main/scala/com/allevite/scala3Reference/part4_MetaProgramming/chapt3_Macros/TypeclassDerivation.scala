package com.allevite.scala3Reference.part4_MetaProgramming.chapt3_Macros

object TypeclassDerivation extends App:
  trait SafeShow[T]:
    def show(t: T): String

  object SafeShow:
    ???

  case class Test1(f1: String, f2: Int)
  case class Test2(token: String, tx: Long)
  case class Test3(x: Int, t2: Test2)
