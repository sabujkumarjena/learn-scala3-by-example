package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances

import java.util.concurrent.Executors
import scala.concurrent.ExecutionContext

object GivenInstances:
  trait Ord[T]:
    def compare(x: T, y: T): Int
    extension (x:T)
      def < (y: T) = compare(x, y) < 0
      def > (y: T) = compare(x,y) > 0

//  given intOrd : Ord[Int] with
//    override def compare(x: Int, y: Int): Int =
//      if x < y then -1 else if x > y then 1 else 0
//
//  given listOrd[T](using ord: Ord[T]): Ord[List[T]] with
//    override def compare(xs: List[T], ys: List[T]): Int =  (xs, ys) match
//      case (Nil, Nil) => 0
//      case (Nil, _) => -1
//      case (_, Nil) => +1
//      case (x :: xst, y :: yst) =>
//        val result = ord.compare(x, y)
//        if result != 0 then result else compare(xst, yst)

  //Anonymous Given
  given  Ord[Int] with
    override def compare(x: Int, y: Int): Int =
      if x < y then -1 else if x > y then 1 else 0

  given [T](using ord: Ord[T]): Ord[List[T]] with
    override def compare(xs: List[T], ys: List[T]): Int =  (xs, ys) match
      case (Nil, Nil) => 0
      case (Nil, _) => -1
      case (_, Nil) => +1
      case (x :: xst, y :: yst) =>
        val result = ord.compare(x, y)
        if result != 0 then result else compare(xst, yst)
  
  given Ord[String] with
    override def compare(x: String, y: String): Int = x.compareTo(y)
    
  case class Person(name: String, age: Int) 
  
  given (using ord: Ord[String]):Ord[Person] with
    override def compare(x: Person, y: Person): Int = ord.compare(x.name, y.name)
  // Alias Givens

  given global: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(5))