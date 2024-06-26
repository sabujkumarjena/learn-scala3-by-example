package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt14_TypeclassDerivation
//import com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances.*
//import com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances.GivenInstances.Ord
//import com.allevite.scala3Reference.part3_ContextualAbstractions.chapt9_GivenInstances.given
import scala.collection.AbstractIterable
import scala.compiletime.{erasedValue, error, summonInline}
import scala.deriving.Mirror

// ? is wildcard argument. It is used in place of _

object TypeClassDerivation:

  trait TC[T]

  inline def derived[T](using Mirror.Of[T]): TC[T] = ???

  /**
   * Note that derived methods may have context Mirror parameters indirectly
   * (e.g. by having a context argument which in turn has a context Mirror parameter, or not at all
   * (e.g. they might use some completely different user-provided mechanism, for instance using Scala 3 macros or runtime reflection).
   * We expect that (direct or indirect) Mirror based implementations will be the most common and that is what this document emphasises.

   * Type class authors will most likely use higher-level derivation or
   * generic programming libraries to implement derived methods.
   * An example of how a derived method might be implemented using only
   * the low-level facilities described above and Scala 3's general metaprogramming features is provided below.
   * It is not anticipated that type class authors would normally implement a derived method in this way,
   * however this walkthrough can be taken as a guide for authors of the higher-level derivation libraries
   * that we expect typical type class authors will use (for a fully worked out example of such a library, see Shapeless 3).
   */
  //How to write a type class derived method using low-level mechanisms

  /**
   * The low-level technique we will use to implement a type class derived method in this example exploits three new
   * type-level constructs in Scala 3: inline methods, inline matches, and implicit searches via summonInline or summonFrom
   *
   */

  trait Eq[T]:
    def eqv(x: T, y: T): Boolean

  /**
   * 
   * we need to implement a method Eq.derived on the companion object of Eq that produces a given instance for
   * Eq[T] given a Mirror[T]
   */
  
  object Eq: 
    inline def derived[T](using m: Mirror.Of[T]): Eq[T] =
      lazy val elemInstances: List[Eq[?]] = summonAll[T, m.MirroredElemTypes]
      inline m match
        case s: Mirror.SumOf[T] => deriveSum(s, elemInstances)
        case p: Mirror.ProductOf[T] => deriveProduct(p, elemInstances)
      
    
    inline def summonAll[T, Elems <: Tuple] : List[Eq[?]] =
      inline erasedValue[Elems] match
        case _ : EmptyTuple => Nil
        case _ : (elem *: elems) => deriveOrSummon[T, elem] :: summonAll[T,elems]

    inline def deriveOrSummon[T, Elem]: Eq[Elem] =
      inline erasedValue[Elem] match
        case _: T => deriveRec[T, Elem]
        case _  => summonInline[Eq[Elem]]


    inline def deriveRec[T, Elem]: Eq[Elem] =
      inline erasedValue[T] match
        case _: Elem => error(" infinite recursive derivation")
        case _ => Eq.derived[Elem](using summonInline[Mirror.Of[Elem]]) // recursive derivation

    def check(x: Any, y: Any, elem: Eq[?]): Boolean =
      elem.asInstanceOf[Eq[Any]].eqv(x, y)


    inline def deriveSum[T](s: Mirror.SumOf[T], list: => List[Eq[?]]): Eq[T] =
      new Eq[T]:
        def eqv(x: T, y: T): Boolean =
          val ordx = s.ordinal(x)
          (s.ordinal(y) == ordx) && check(x, y, list(ordx))

    def iterable[T](p: T): Iterable[Any] = new AbstractIterable[Any]:
      def iterator: Iterator[Any] = p.asInstanceOf[Product].productIterator

    inline def deriveProduct[T](p: Mirror.ProductOf[T], list: => List[Eq[?]]): Eq[T] =
      new Eq[T]:
        def eqv(x: T, y: T): Boolean =
          iterable(x).lazyZip(iterable(y)).lazyZip(list).forall(check)

    given Eq[Int] with
      def eqv(x: Int, y: Int) = x == y


  enum Lst[+T] derives Eq:
    case Cns(t: T, ts: Lst[T])
    case Nl

  extension [T](t: T) def ::(ts: Lst[T]): Lst[T] = Lst.Cns(t, ts)

  @main def demp(): Unit=
    import Lst.*
    val eqoi = summon[Eq[Lst[Int]]]
    assert(eqoi.eqv(23 :: 47 :: Nl, 23 :: 47 :: Nl))
    assert(!eqoi.eqv(23 :: Nl, 7 :: Nl))
    assert(!eqoi.eqv(23 :: Nl, Nl))

/**
 * In this case the code that is generated by the inline expansion for the derived Eq instance for Lst looks like the following, after a little polishing,
 *
 * given derived$Eq[T](using eqT: Eq[T]): Eq[Lst[T]] =
 * eqSum(summon[Mirror.Of[Lst[T]]], {/* cached lazily */
 * List(
 * eqProduct(summon[Mirror.Of[Cns[T]]], {/* cached lazily */
 * List(summon[Eq[T]], summon[Eq[Lst[T]]])
 * }),
 * eqProduct(summon[Mirror.Of[Nl.type]], {/* cached lazily */
 * Nil
 * })
 * )
 * })
 * The lazy modifier on elemInstances is necessary for preventing infinite recursion in the derived instance for recursive types such as Lst.
 *
 * Alternative approaches can be taken to the way that derived methods can be defined. For example, more aggressively inlined variants using Scala 3 macros, whilst being more involved for type class authors to write than the example above, can produce code for type classes like Eq which eliminate all the abstraction artefacts (eg. the Lists of child instances in the above) and generate code which is indistinguishable from what a programmer might write by hand. As a third example, using a higher-level library such as Shapeless, the type class author could define an equivalent derived method as,
 *
 * given eqSum[A](using inst: => K0.CoproductInstances[Eq, A]): Eq[A] with
 * def eqv(x: A, y: A): Boolean = inst.fold2(x, y)(false)(
 * [t] => (eqt: Eq[t], t0: t, t1: t) => eqt.eqv(t0, t1)
 * )
 *
 * given eqProduct[A](using inst: => K0.ProductInstances[Eq, A]): Eq[A] with
 * def eqv(x: A, y: A): Boolean = inst.foldLeft2(x, y)(true: Boolean)(
 * [t] => (acc: Boolean, eqt: Eq[t], t0: t, t1: t) =>
 * Complete(!eqt.eqv(t0, t1))(false)(true)
 * )
 *
 * inline def derived[A](using gen: K0.Generic[A]): Eq[A] =
 * gen.derive(eqProduct, eqSum)
 * The framework described here enables all three of these approaches without mandating any of them.
 *
 */
