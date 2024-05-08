package com.allevite.scala3Reference.chapt1_IntersectionTypes

object IntersectionTypes:
  trait Horse:
    def name: String  = "default horse"
    def canRun: Boolean
    def children: List[Horse]

  trait Donkey[T]:
    def name: String = "default donkey"
    def carry(load: T): Unit = println(s"I am carrying : $load")
    def children: List[Donkey[T]]

  class Mule[T] extends  Horse, Donkey[T]:
    override def name: String = "Default Mule"
    override def canRun: Boolean = true
    override def children: List[Donkey[T] & Horse] = Nil

  val myMule: Mule[String]=  Mule[String]

  val myMule2: Horse & Donkey[String] =  Mule[String]

  @main def test(): Unit =
    myMule.carry("sabuj")
    println(myMule.name)

/**
```
T <: A    T <: B
 ----------------
 T <: A & B

 A <: T
 ----------------
 A & B <: T

 B <: T
 ----------------
 A & B <: T
```

 From the rules above, we can show that `&` is _commutative_: `A & B <: B & A` for any type `A` and `B`.

```
   B <: B           A <: A
 ----------       -----------
 A & B <: B       A & B <: A
 ---------------------------
 A & B  <:  B & A
```

In another word, `A & B` is the same type as `B & A`, in the sense that the two types
 have the same values and are subtypes of each other.

 If `C` is a co- or contravariant type constructor, then `C[A] & C[B]` can be simplified using the following rules:

 - If `C` is covariant, `C[A] & C[B] ~> C[A & B]`
- If `C` is contravariant, `C[A] & C[B] ~> C[A | B]`

When `C` is covariant, `C[A & B] <: C[A] & C[B]` can be derived:

```
    A <: A                  B <: B
 ----------               ---------
 A & B <: A               A & B <: B
 ---------------         -----------------
 C[A & B] <: C[A]          C[A & B] <: C[B]
 ------------------------------------------
 C[A & B] <: C[A] & C[B]
```

When `C` is contravariant, `C[A | B] <: C[A] & C[B]` can be derived:

```
    A <: A                        B <: B
 ----------                     ---------
 A <: A | B                     B <: A | B
 -------------------           ----------------
 C[A | B] <: C[A]              C[A | B] <: C[B]
 --------------------------------------------------
 C[A | B] <: C[A] & C[B]
```

## Erasure

 The erased type for `S & T` is the erased _glb_ (greatest lower bound) of the
 erased type of `S` and `T`. The rules for erasure of intersection types are given
 below in pseudocode:

```
|S & T| = glb(|S|, |T|)

 glb(JArray(A), JArray(B)) = JArray(glb(A, B))
 glb(JArray(T), _)         = JArray(T)
 glb(_, JArray(T))         = JArray(T)
 glb(A, B)                 = A                     if A extends B
 glb(A, B)                 = B                     if B extends A
 glb(A, _)                 = A                     if A is not a trait
 glb(_, B)                 = B                     if B is not a trait
 glb(A, _)                 = A                     // use first
```

In the above, `|T|` means the erased type of `T`, `JArray` refers to
 the type of Java Array.

*/