package com.allevite.scala3Reference.part2_Enums.chapt8_AlgebraicDataTypes

object AlgebraicDataType:
  enum Option[+T]:
    case Some(x:T)
    case None

    def isDefined: Boolean = this match
      case None => false
      case _ => true
  object Option:
    def apply[T](x: T): Option[T] =
      if x == null then None else Some(x)

  enum View[-T]:
    case Refl[T1](f: T1 => T1)  extends View[T1]