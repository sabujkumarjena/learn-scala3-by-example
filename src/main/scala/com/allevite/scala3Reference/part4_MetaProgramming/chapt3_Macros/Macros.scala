package com.allevite.scala3Reference.part4_MetaProgramming.chapt3_Macros

import scala.quoted.*

object Macros:

    //Multi-Staging

    //Quoted expressions

  /**
   * Multi-stage programming in Scala 3 uses quotes '{..} to delay, i.e., stage, execution of code
   * and splices ${..} to evaluate and insert code into quotes. Quoted expressions are typed as
   * Expr[T] with a covariant type parameter T. It is easy to write statically safe code generators
   * with these two concepts.
   */
  def unrolledPowerCode(x: Expr[Double], n: Int)(using Quotes): Expr[Double] =
    if n == 0 then '{1.0}
    else if n == 1 then x
    else '{$x * ${ unrolledPowerCode(x, n-1)}}

//  '{
//    val x = 2.03 + 1.05
//    ${unrolledPowerCode('{x}, 3)}
//  }
  /**
   * Quotes and splices are duals of each other. For an arbitrary expression x of type T we have
   * ${'{x}} = x and for an arbitrary expression e of type Expr[T] we have '{${e}} = e
   */


  @main def demo(): Unit =
    ()

