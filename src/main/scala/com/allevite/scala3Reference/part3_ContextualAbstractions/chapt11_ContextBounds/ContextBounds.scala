package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt11_ContextBounds

import com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances.GivenInstances.*

//importing given instances
import com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances.GivenInstances.given

object ContextBounds:

  //A context bound is a shorthand
  // for expressing the common pattern of a context parameter that depends on a type parameter.
//  def max[T](x: T, y: T)(using ord: Ord[T]): T =
  def max1[T: Ord](x: T, y: T): T =
    if x < y then y else x

  // Anonymous Context Parameters
//  def maximum[T](list: List[T])(using Ord[T]): T =
  def maximum1[T: Ord](list: List[T]): T =
    list.reduceLeft(max1)

  //def f[T: C1 : C2, U: C3](x: T)(using y: U, z: V): R

  // will expand to
//  def f[T, U](x: T)(using _: C1[T], _: C2[T], _: C3[U], y: U, z: V): R

//Context bounds can be combined with subtype bounds.
// If both are present, subtype bounds come first, e.g.

//def g[T <: B : C](x: T): R = ...
  @main def MAIN(): Unit =
    println(maximum1(List(List(1, 2, 3), List(2,4,6,8,9))))
    println(maximum1(List(Person("sabuj", 43), Person("aditya", 47))))
