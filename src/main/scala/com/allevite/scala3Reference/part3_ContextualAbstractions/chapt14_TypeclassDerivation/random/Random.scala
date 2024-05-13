package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt14_TypeclassDerivation.random

//import scala.deprecatedName.*
import scala.compiletime.{erasedValue, summonInline} // erasedValue[A] is a construct that enables us to match on types, instead of values
import scala.deriving.Mirror
import scala.util.Random as SRandom

trait Random[A]:
  def generate():A



inline def summonAll[A <: Tuple]: List[Random[?]] =  // inline perform computation at compile time
  inline erasedValue[A] match
    case _: EmptyTuple => Nil
    case _: (t *: ts)  => summonInline[Random[t]] :: summonAll[ts]

def toTuple(xs: List[_], acc: Tuple): Tuple =
  xs match
    case Nil => acc
    case h :: t => h *: toTuple(t, acc)

object Random:

  inline given derived[A](using m: Mirror.Of[A]): Random[A] =
    lazy val instances = summonAll[m.MirroredElemTypes]
    inline m match
      case s: Mirror.SumOf[A] => deriveSum(s, instances)
      case p: Mirror.ProductOf[A] => deriveProduct(p, instances)

  private def deriveSum[A](s: Mirror.SumOf[A], instances: => List[Random[?]]): Random[A] =
    new Random[A]:
      override def generate(): A =
        instances(scala.util.Random.nextInt(instances.size))
          .asInstanceOf[Random[A]]
          .generate()

  private def deriveProduct[A](p: Mirror.ProductOf[A], instances: => List[Random[?]]): Random[A] =
    new Random[A]:
      def generate(): A =
        p.fromProduct(toTuple(instances.map(_.generate()), EmptyTuple))

enum User:
  case RegisteredUser(id: Long, email: String, isAdmin: Boolean)
  case AnonymousUser(session: String)

given Random[String] with
  override def generate(): String = SRandom.alphanumeric.take(SRandom.between(5,10)).mkString

given Random[Long] with
  override def generate(): Long = SRandom.nextLong(1000000)

given Random[Boolean] with
  override def generate(): Boolean = SRandom.nextBoolean()

@main def randDemo(): Unit =
  println(summon[Random[User]].generate())
  println(summon[Random[User]].generate())
  println(summon[Random[User]].generate())
  println(summon[Random[User]].generate())

