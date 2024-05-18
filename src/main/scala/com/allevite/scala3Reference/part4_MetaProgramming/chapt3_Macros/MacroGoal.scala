package com.allevite.scala3Reference.part4_MetaProgramming.chapt3_Macros

import scala.quoted.{Expr, Quotes, Type}

object MacroGoal extends App:
  def f(n: Int): Int =
    Thread.sleep(500L * n)
    20

  val start = System.currentTimeMillis()

  println {
    try f(10)
    finally
      val end = System.currentTimeMillis()
      println(s" Evaluating f(10) took: ${end - start} ms")
  }

  //goal : timed(f(4))

  import TimedMacro.timed
  
  timed(f(2))
