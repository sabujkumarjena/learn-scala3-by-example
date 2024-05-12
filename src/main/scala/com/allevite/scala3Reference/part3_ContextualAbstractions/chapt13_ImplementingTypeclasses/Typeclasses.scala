package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt13_ImplementingTypeclasses

object Typeclasses:

  //SemiGroups and Monoids
  trait SemiGroup[T]:
    extension (x: T) def combine(y: T): T

  trait Monoid[T] extends SemiGroup[T]:
    def unit: T

  given Monoid[String] with
    extension (x: String) def combine(y: String): String = x.concat(y)
      def unit: String = ""

  given Monoid[Int] with
    extension (x: Int) def combine(y: Int): Int = x + y
      def unit: Int = 0

//  def combineAll[T: Monoid](xs: List[T]): T =
//    xs.foldLeft(summon[Monoid[T]].unit)(_.combine(_))

  object Monoid:
    def apply[T](using m: Monoid[T]) = m

  def combineAll[T: Monoid](xs: List[T]): T =
    xs.foldLeft(Monoid[T].unit)(_.combine(_))

  //Functors
  trait Functor[F[_]]:
    extension [A](x: F[A])
      def map[B](f: A => B): F[B]

  given Functor[List] with
    extension [A](xs: List[A])
      def map[B](f: A => B): List[B] =
        xs.map(f) // List already has a `map` method

  //Monads
  trait Monad[F[_]] extends Functor[F]:

    /** The unit value for a monad */
    extension [A](x: A)
      def pure: F[A]

    extension [A](x: F[A])
      /** The fundamental composition operation */
      def flatMap[B](f: A => F[B]): F[B]

      /** The `map` operation can now be defined in terms of `flatMap` */
      def map[B](f: A => B) = x.flatMap(f.andThen(pure))

  end Monad

  //list
  given listMonad: Monad[List] with
    extension [A](x: A)
      def pure: List[A] =
        List(x)

    extension [A](xs: List[A])
      def flatMap[B](f: A => List[B]): List[B] =
        xs.flatMap(f) // rely on the existing `flatMap` method of `List`
  //Option
  given optionMonad: Monad[Option] with
    extension [A](x: A)
      def pure: Option[A] =
        Option(x)

    extension [A](xo: Option[A])
      def flatMap[B](f: A => Option[B]): Option[B] = xo match
        case Some(x) => f(x)
        case None => None
  //Reader Monad

  trait Config

  // ...
  def compute(i: Int)(config: Config): String = ???

  def show(str: String)(config: Config): Unit = ???

  /**
   * We may want to combine compute and show into a single function, accepting a Config as parameter, and showing the result of the computation, and we'd like to use a monad to avoid passing the parameter explicitly multiple times. So postulating the right flatMap operation, we could write:
   *
   * def computeAndShow(i: Int): Config => Unit = compute(i).flatMap(show)
   * instead of
   *
   * show(compute(i)(config))(config)
   */
//  type ConfigDependent[Result] = Config => Result
//
//  given configDependentMonad: Monad[ConfigDependent] with
//    extension [A](x: A)
//      def pure: ConfigDependent[A] = conf => x
//    extension [A](fa: ConfigDependent[A])
//      def flatMap[B](f: A => ConfigDependent[B]): ConfigDependent[B] =
//        conf => f(fa(conf))(conf)
  // The type ConfigDependent can be written using type lambdas:

  type ConfigDependent = [Result] =>> Config => Result

  given configDependentMonad: Monad[[Result] =>> Config => Result] with
    extension [A](x: A)
      def pure: Config => A = conf => x
    extension [A](fa: Config => A)
      def flatMap[B](f: A => Config => B): Config => B =
        conf => f(fa(conf))(conf)

  given readerMonad[Ctx]: Monad[[X] =>> Ctx => X] with //monad which acts on functions
    extension [A](x: A)
      def pure: Ctx => A = ctx => x
    extension  [A](fa: Ctx => A)
      def flatMap[B](f: A => Ctx => B): Ctx => B =
        ctx => f(fa(ctx))(ctx)
