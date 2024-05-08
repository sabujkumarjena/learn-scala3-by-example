package com.allevite.scala3Reference.chapt3_TypeLambdas

object TypeLamdas:
/**
  Kinds = types of types
    - Int, String, Person = level-0 kind (value level kind)
    - List, Option, Future = level-1 kind (generics )
    - Monad, Functor = level-2 kind (generics of generics)
*/
  type MyList[A] = List[A]
  type MyListV2 = [A] =>> List[A] //type lamda
  val aList: List[Int] = List(1,2,3)
  val aListV2:MyListV2[Int] = List(1,2,3) // same
  type MyMap = [K,V] =>> Map[K,V]

  class Functor[F[_]]
  val functorOption = Functor[Option]  // new Functor[Option]
  type MyFunctor[F[_]] = Functor[F]
  type MyFunctorV2 = [F[_]] =>> Functor[F] //same as MyFunctor

  //example: ZIO
  class ZIO[R,E, A]

  trait Monad[F[_]]:
    def flatMap[A,B](fa: F[A])(f: A => F[B]): F[B]

  class ZIOMonad[R,E] extends Monad[[A] =>> ZIO[R, E, A]]:
    override def flatMap[A, B](fa: ZIO[R, E, A])(f: A => ZIO[R, E, B]): ZIO[R, E, B] = new ZIO

/**
 * Exercise: implement a "monad" data type for Either
 */

  class EitherMonad[E] extends Monad[[A] =>> Either[E,A]]:
    override def flatMap[A, B](fa: Either[E, A])(f: A => Either[E, B]): Either[E, B] = fa match
      case Left(e) => Left(e)
      case Right(v) => f(v)

