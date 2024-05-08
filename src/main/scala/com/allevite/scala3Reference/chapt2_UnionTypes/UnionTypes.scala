package com.allevite.scala3Reference.chapt2_UnionTypes

object UnionTypes:

  case class UserName(name: String)
  case class Password(hash: String)

  def lookUpName(name: String): Boolean = true
  def lookupPassword(hash: String): Boolean = true

  def help(id: UserName | Password): Boolean = id match
    case UserName(n) => lookUpName(n)
    case Password(h) => lookupPassword(h)

/**
 ### Interaction with pattern matching syntax
`|` is also used in pattern matching to separate pattern alternatives and has
 lower precedence than `:` as used in typed patterns, this means that:

```scala
case _: A | B => ...
```

is still equivalent to:

```scala
case (_: A) | B => ...
```

and not to:

```scala
case _: (A | B) => ...
```

## Subtyping Rules

 - `A` is always a subtype of `A | B` for all `A`, `B`.
 - If `A <: C` and `B <: C` then `A | B <: C`
- Like `&`, `|` is commutative and associative:

  ```scala
  A | B =:= B | A
 A | (B | C) =:= (A | B) | C
  ```

- `&` is distributive over `|`:

  ```scala
  A & (B | C) =:= A & B | A & C
  ```

From these rules it follows that the _least upper bound_ (LUB) of a set of types
 is the union of these types.

 ## Join of a union type

 In some situation described below, a union type might need to be widened to
 a non-union type, for this purpose we define the _join_ of a union type `T1 |
... | Tn` as the smallest intersection type of base class instances of
`T1`,...,`Tn`. Note that union types might still appear as type arguments in the
 resulting type, this guarantees that the join is always finite.

 The _visible join_ of a union type is its join where all operands of the intersection that
 are instances of [transparent](../other-new-features/transparent-traits.md) traits or classes are removed.


 ### Example

 Given

```scala
trait C[+T]
 trait D
 trait E
 transparent trait X
 class A extends C[A], D, X
 class B extends C[B], D, E, X
```

The join of `A | B` is `C[A | B] & D & X` and the visible join of `A | B` is `C[A | B] & D`.

 ## Hard and Soft Union Types

 We distinguish between hard and soft union types. A _hard_ union type is a union type that's explicitly
 written in the source. For instance, in
```scala
val x: Int | String = ...
```
`Int | String` would be a hard union type. A _soft_ union type is a type that arises from type checking
 an alternative of expressions. For instance, the type of the expression
```scala
val x = 1
 val y = "abc"
 if cond then x else y
```
is the soft unon type `Int | String`. Similarly for match expressions. The type of
```scala
x match
 case 1 => x
 case 2 => "abc"
 case 3 => List(1, 2, 3)
```
is the soft union type `Int | "abc" | List[Int]`.


 ## Type inference

 When inferring the result type of a definition (`val`, `var`, or `def`) and the
 type we are about to infer is a soft union type, then we replace it by its visible join,
 provided it is not empty.
 Similarly, when instantiating a type argument, if the corresponding type
 parameter is not upper-bounded by a union type and the type we are about to
 instantiate is a soft union type, we replace it by its visible join, provided it is not empty.
 This mirrors the
 treatment of singleton types which are also widened to their underlying type
 unless explicitly specified. The motivation is the same: inferring types
 which are "too precise" can lead to unintuitive typechecking issues later on.

 ### Example

```scala
import scala.collection.mutable.ListBuffer
 val x = ListBuffer(Right("foo"), Left(0))
 val y: ListBuffer[Either[Int, String]] = x
```

This code typechecks because the inferred type argument to `ListBuffer` in the
 right-hand side of `x` was `Left[Int, Nothing] | Right[Nothing, String]` which
 was widened to `Either[Int, String]`. If the compiler hadn't done this widening,
 the last line wouldn't typecheck because `ListBuffer` is invariant in its
 argument.


 ## Members

 The members of a union type are the members of its join.

 ### Example

 The following code does not typecheck, because method `hello` is not a member of
`AnyRef` which is the join of `A | B`.

```scala
trait A { def hello: String }
 trait B { def hello: String }

 def test(x: A | B) = x.hello // error: value `hello` is not a member of A | B
```

On the other hand, the following would be allowed

```scala
trait C { def hello: String }
 trait A extends C with D
 trait B extends C with E

 def test(x: A | B) = x.hello // ok as `hello` is a member of the join of A | B which is C
```

## Exhaustivity checking

 If the selector of a pattern match is a union type, the match is considered
 exhaustive if all parts of the union are covered.

 ## Erasure

 The erased type for `A | B` is the _erased least upper bound_ of the erased
 types of `A` and `B`. Quoting from the documentation of `TypeErasure#erasedLub`,
 the erased LUB is computed as follows:

 - if both argument are arrays of objects, an array of the erased LUB of the element types
 - if both arguments are arrays of same primitives, an array of this primitive
 - if one argument is array of primitives and the other is array of objects,
 [`Object`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html)
 - if one argument is an array, [`Object`](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/lang/Object.html)
 - otherwise a common superclass or trait S of the argument classes, with the
 following two properties:
 * S is minimal: no other common superclass or trait derives from S
 * S is last   : in the linearization of the first argument type `|A|`
                  there are no minimal common superclasses or traits that
 come after S.
 The reason to pick last is that we prefer classes over traits that way,
 which leads to more predictable bytecode and (?) faster dynamic dispatch.

*/
