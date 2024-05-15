package com.allevite.scala3Reference.part4_MetaProgramming.chapt2_CompileTimeOperations

import com.allevite.scala3Reference.part4_MetaProgramming.chapt1_Inline.InlineDemo.{Nat, Succ, Zero}

import scala.collection.immutable.{HashSet, TreeSet}
import scala.compiletime.{constValue, erasedValue, error, summonFrom}
import scala.compiletime.ops.int.S
import scala.util.hashing.Hashing

object CompileTimeOperations:

  // The scala.compiletime package contains helper definitions
  // that provide support for compile-time operations over values

  //"constValue" and "constValueOpt"

  /**
   * constValue is a function that produces the constant value represented by a type,
   * or a compile time error if the type is not a constant type.
  */



  transparent inline def toIntC[N]: Int =
    inline constValue[N] match
      case 0 => 0
      case _: S[n1] => 1 + toIntC[n1]

  inline val ctwo: 2 = toIntC[2]

  /**
   constValueOpt is the same as constValue, however returning an Option[T] enabling us to handle situations where
   a value is not present. Note that S is the type of the successor of some singleton type. For example the
   type S[1] is the singleton type 2.

   Since tuples are not constant types, even if their constituents are, there is constValueTuple, which given a
   tuple type (X1, ..., Xn), returns a tuple value (constValue[X1], ..., constValue[Xn]).
   */

  // erasedValue

  // def erasedValue[T]: T

  transparent inline def defaultValue[T] =
    inline erasedValue[T] match
      case _: Byte => Some(0: Byte)
      case _: Char => Some(0: Char)
      case _: Short => Some(0: Short)
      case _: Int => Some(0)
      case _: Long => Some(0L)
      case _: Float => Some(0.0f)
      case _: Double => Some(0.0d)
      case _: Boolean => Some(false)
      case _: Unit => Some(())
      case _ => None

  transparent inline def toIntT[N <: Nat]: Int =
    inline scala.compiletime.erasedValue[N] match
      case _: Zero.type => 0
      case _: Succ[n] => toIntT[n] + 1

  inline val two = toIntT[Succ[Succ[Zero.type]]]

  /**
   * erasedValue is an erased method so it cannot be used and has no runtime behavior.
   * Since toIntT performs static checks over the static type of N we can safely use it to
   * scrutinize its return type (S[S[Z]] in this case).
   */

   // error

   // The error method is used to produce user-defined compile errors during inline expansion.
   // It has the following signature:

   // inline def error(inline msg: String): Nothing

  inline def fail() =
    error("failed for a reason")

  //fail() // error: failed for a reason

  // Summoning Givens Selectively

  inline def setFor[T]: Set[T] = summonFrom {
    case ord: Ordering[T] => new TreeSet[T]()(using ord)
    case _ => new HashSet[T]
  }

  /**
   * A summonFrom call takes a pattern matching closure as argument. All patterns in the closure are
   * type ascriptions of the form identifier : Type.
   *
   * Patterns are tried in sequence. The first case with a pattern x: T such that a contextual value of
   * type T can be summoned is chosen.
   *
   * Alternatively, one can also use a pattern-bound given instance, which avoids the explicit using clause.
   * For instance, setFor could also be formulated as follows:
   */

  inline def setForV2[T]: Set[T] = summonFrom {
    case given Ordering[T] => new TreeSet[T]
    case _ => new HashSet[T]
  }

  summon[Ordering[String]]

  // "summonInline"

  /**
   * The shorthand summonInline provides a simple way to write a summon that is delayed until the call is inlined.
   * Unlike summonFrom, summonInline also yields the implicit-not-found error,
   * if a given instance of the summoned type is not found.
   */

  import scala.compiletime.summonInline
  import scala.annotation.implicitNotFound

  @implicitNotFound("Missing One")
  trait Missing1

  @implicitNotFound("Missing Two")
  trait Missing2

  trait NotMissing
  given NotMissing = new NotMissing { }

  transparent inline def summonInlineCheck[T <: Int](inline t : T) : Any =
    inline t match
      case 1 => summonInline[Missing1]
      case 2 => summonInline[Missing2]
      case _ => summonInline[NotMissing]

  //val missing1 = summonInlineCheck(1) // error: Missing One
  //val missing2 = summonInlineCheck(2) // error: Missing Two
  val notMissing : NotMissing = summonInlineCheck(3)

  @main def demo(): Unit =
    println(defaultValue[Boolean])
    println(two)
    println(setFor[String].getClass)