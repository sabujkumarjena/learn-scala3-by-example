package com.allevite.scala3Reference.part6_Experimental.chapt1_CanThrowCapabilities

import language.experimental.saferExceptions
import scala.annotation.experimental
//import unsafeExceptions.canThrowAny


@experimental object CanThrowCapabilities:
  class CanThrow[-E <: Exception ]

  infix type $throws[R, +E <: Exception] = CanThrow[E] ?=> R

  def m[T, E <: Exception, U](x: T)(using CanThrow[E]): U = ???
  def m[T, E <: Exception, U](x: T): U throws E = ??? //same as above

  def m3[T, E1 <: Exception, E2 <: Exception, U](x: T): U throws E1 | E2 = ???

  def m2[T, E1 <: Exception, E2 <: Exception, U](x: T): U throws E1 throws E2 = ???

  def m[T, E1 <: Exception, E2 <: Exception, U](x: T)(using CanThrow[E1], CanThrow[E2]): U = ???

  def m2[T, E1 <: Exception, E2 <: Exception, U](x: T)(using CanThrow[E1])(using CanThrow[E2]): U = ???

  def m2[T, E1 <: Exception, E2 <: Exception, U](x: T)(using CanThrow[E1]): U throws E2 = ???

  val limit = 10e9

  class LimitExceeded extends Exception

  def f(x: Double): Double throws LimitExceeded =
    if x < limit then x * x else throw LimitExceeded()

//  @main def test(xs: Double*) =
//    try println(xs.map(f).sum)
//    catch case ex: LimitExceeded => println("too large")

  // compiler-generated code
//  @main def test(xs: Double*) =
//    try
//      erased given ctl: CanThrow[LimitExceeded] = compiletime.erasedValue
//      println(xs.map(x => f(x)(using ctl)).sum)
//    catch case ex: LimitExceeded => println("too large")