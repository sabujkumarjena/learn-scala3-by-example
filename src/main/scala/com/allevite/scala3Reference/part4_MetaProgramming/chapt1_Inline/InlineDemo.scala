package com.allevite.scala3Reference.part4_MetaProgramming.chapt1_Inline

object InlineDemo:
  object Config:
    inline val logging = true

  object Logger:
    private var indent = 0
    inline def log[T](msg: String, indentMargin: => Int)(op : => T): T =
      if Config.logging then
        println(s"${" " * indent}start $msg")
        indent += indentMargin
        val result = op
        indent -= indentMargin
        println(s"${" " * indent}$msg = $result")
        result
      else
        op
  /**
   * The Config object contains a definition of the inline value logging.
   * This means that logging is treated as a constant value,
   * equivalent to its right-hand side false.
   * The right-hand side of such an inline val must itself be a constant expression.
  */

  var indentSetting = 2

  def factorial(n: BigInt): BigInt =
    Logger.log(s"factorial($n)", indentSetting) {
      if n == 0 then 1
      else n * factorial(n - 1)
    }


  /**
   * If Config.logging == false, this will be rewritten (simplified) to:
   *
   * def factorial(n: BigInt): BigInt =
   *  if n == 0 then 1
   *  else n * factorial(n - 1)
   *
   * If Config.logging == true, this will be rewritten (simplified) to:
   *
   * def factorial(n: BigInt): BigInt =
   *  val msg = s"factorial($n)"
   *  println(s"${"  " * indent}start $msg")
   *  Logger.inline$indent_=(indent.+(indentSetting))
   *  val result =
   *  if n == 0 then 1
   *  else n * factorial(n - 1)
   *  Logger.inline$indent_=(indent.-(indentSetting))
   *  println(s"${"  " * indent}$msg = $result")
   *  result
   *
   *
    */

  // Recursive Inline Methods

  /**
   *  Inline methods can be recursive. For instance, when called with a constant exponent n,
   *  the following method for power will be implemented by straight inline code without any loop or recursion.
   *  It is worth noting that the number of successive inlines is limited to 32 and
   *  can be modified by the compiler setting -Xmax-inlines.
   */

  inline def power(x: Double, n: Int): Double =
    if n == 0 then 1.0
    else if n == 1 then x
    else
      val y = power(x, n / 2)
      if n % 2 == 0 then y * y else y * y * x

 // power(expr, 10)
// translates to
//
//   val x = expr
//   val y1 = x * x   // ^2
//   val y2 = y1 * y1 // ^4
//   val y3 = y2 * x  // ^5
//   y3 * y3          // ^10

  /**
   * Parameters of inline methods can have an inline modifier as well. This means that actual arguments to these
   * parameters will be inlined in the body of the inline def. inline parameters have call semantics equivalent
   * to by-name parameters but allow for duplication of the code in the argument. It is usually useful when constant
   * values need to be propagated to allow further optimizations/reductions.
   *
   * The following example shows the difference in translation between by-value, by-name and inline parameters:
   */
  inline def funkyAssertEquals(actual: Double, expected: =>Double, inline delta: Double): Unit =
    if (actual - expected).abs > delta then
      throw new AssertionError(s"difference between ${expected} and ${actual} was larger than ${delta}")

//funkyAssertEquals(computeActual(), computeExpected(), computeDelta())
// translates to
//
//   val actual = computeActual()
//   def expected = computeExpected()
//   if (actual - expected).abs > computeDelta() then
//     throw new AssertionError(s"difference between ${expected} and ${actual} was larger than ${computeDelta()}")


  //Rules for Overriding
  // Inline methods can override other non-inline methods. The rules are as follows:

  /**
   *  1)  If an inline method f implements or overrides another, non-inline method, the inline method can also be
   *  invoked at runtime. For instance, consider the scenario:
   */

  abstract class A:
    def f: Int
    def g: Int = f

  class B extends A:
    inline def f = 22
    override inline def g = f + 11

  val b = new B
  val a: A = b
  // inlined invocatons
  assert(b.f == 22)
  assert(b.g == 33)
  // dynamic invocations
  assert(a.f == 22)
  assert(a.g == 33)
  // The inlined invocations and the dynamically dispatched invocations give the same results.

  // 2) Inline methods are effectively final

  // 3) Inline methods can also be abstract. An abstract inline method can be implemented only by other inline methods.
  //    It cannot be invoked directly :

  abstract class AA:
    inline def f: Int

  object BB extends AA:
    inline def f: Int = 22

  BB.f // OK
  val aa: AA = BB
  // aa.f // error: cannot inline f in AA.

  //Transparent Inline Methods

//  Inline methods can additionally be declared transparent. This means that the return type of the inline method
  //  can be specialized to a more precise type upon expansion.

  class C
  class D extends C:
    def m = true

  transparent inline def choose(b: Boolean): C =
    if b then new C else new D

  val obj1: C = choose(true)    // static type is C
  val obj2: D = choose(false)   // static type is D

  // obj1.m  // compile time error : m is not defined on C
  obj2.m //Ok

  /**
   * Here, the inline method choose returns an instance of either of the two types C or D.
   * If choose had not been declared to be transparent, the result of its expansion would always be of type C,
   * even though the computed value might be of the subtype D. The inline method is a "blackbox" in the sense
   * that details of its implementation do not leak out. But if a transparent modifier is given,
   * the expansion is the type of the expanded body. If the argument b is true, that type is C, otherwise it is D.
   * Consequently, calling m on obj2 type-checks since obj2 has the same type as the expansion of choose(false),
   * which is D. Transparent inline methods are "whitebox" in the sense that the type of an application of such a
   * method can be more specialized than its declared return type, depending on how the method expands.
   *
   * In the following example, we see how the return type of zero is specialized to the singleton type 0 permitting
   * the addition to be ascribed with the correct type 1.
   */

  transparent inline def zero: Int = 0
  val one: 1 = zero + 1

  //Transparent vs. non-transparent inline

  /**
   * As we already discussed, transparent inline methods may influence type checking at call site.
   * Technically this implies that transparent inline methods must be expanded during type checking of the program.
   * Other inline methods are inlined later after the program is fully typed.
   *
   * For example, the following two functions will be typed the same way but will be inlined at different times.
   */
//

  //Inline Conditionals

  /**
   * An if-then-else expression whose condition is a constant expression can be simplified to the selected branch.
   * Prefixing an if-then-else expression with inline enforces that the condition has to be a constant expression,
   * and thus guarantees that the conditional will always simplify.
   */
  inline def update(delta: Int) =
    inline if delta >= 0 then "positive"
    else "negative"

  // Inline Matches

  /**
   * A match expression in the body of an inline method definition may be prefixed by the inline modifier.
   * If there is enough type information at compile time to select a branch, the expression is reduced to
   * that branch and the type of the expression is the type of the right-hand side of that result. If not,
   * a compile-time error is raised that reports that the match cannot be reduced.
   *
   * The example below defines an inline method with a single inline match expression that picks a case based on its static type:
   */
  transparent inline def g(x: Any): Any =
    inline x match
      case x: String => (x, x) // Tuple2[String, String](x, x)
      case x: Double => x

  val g1: Double = g(1.0d) // Has type 1.0d which is a subtype of Double
  val g2: (String, String) = g("test") // Has type (String, String)

  trait Nat

  case object Zero extends Nat

  case class Succ[N <: Nat](n: N) extends Nat

  transparent inline def toInt(n: Nat): Int =
    inline n match
      case Zero => 0
      case Succ(n1) => toInt(n1) + 1

  inline val natTwo = toInt(Succ(Succ(Zero)))
  val intTwo : 2 = natTwo


  @main def demo(): Unit =
    println(factorial(5))
    println(power(2,10))
//    println(update(scala.util.Random.nextInt(10))) // compile time error can't inline, because the condition is not constant value
    println(update(10)) //ok