package com.allevite.scala3Reference.part1_NewTypes.chapt4_MatchTypes

object MatchTypes:
  type LastElem[X] = X match
    case BigInt => Int
    case String => Char
    case Array[t] => t
    case Iterable[t] => t

  val aDigit: LastElem[BigInt] = 2
  def lastElemOf[T](value: T): LastElem[T] = value match
    case n: BigInt => (n % 10).toInt
    case s: String => s.charAt(s.length - 1)
    case a: Array[_] => a.last
    case i: Iterable[_] => i.last

  // Match types can form part of recursive type definitions

  type FisrtElem[X] = X match
    case String => Char
    case Array[t] => FisrtElem[t]
    case Iterable[t] => FisrtElem[t]
    case AnyVal => X

  def firstElemOf[T](value: T): FisrtElem[T] = value match
    case s: String => firstElemOf(s.charAt(0))
    case a: Array[_] => firstElemOf(a(0))
    case i: Iterable[_] => firstElemOf(i.head)
    case v: AnyVal => v


  @main def MAIN(): Unit =
    println(lastElemOf(List(1,2,3,4)))
    println(lastElemOf("Sabuj"))
    println(firstElemOf(List(List("sabuj", "Deepak"),List(1,2,3,4))))