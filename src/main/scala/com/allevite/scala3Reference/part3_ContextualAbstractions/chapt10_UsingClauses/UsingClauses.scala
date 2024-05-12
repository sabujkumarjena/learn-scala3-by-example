package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt10_UsingClauses

import  com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances.GivenInstances.*
object UsingClauses:
  def max[T](x:T, y:T)(using ord: Ord[T]): T =
    if x < y then y else x
  // Anonymous Context Parameters
  def maximum[T](list: List[T])(using Ord[T]): T =
    list.reduceLeft(max)

  // Summoning Instances
  summon[Ord[List[Int]]]

  @main def MAIN(): Unit = println(maximum(List(1,2,3)))