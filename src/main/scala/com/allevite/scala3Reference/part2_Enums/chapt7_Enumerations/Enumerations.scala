package com.allevite.scala3Reference.part2_Enums.chapt7_Enumerations

import com.allevite.scala3Reference.part2_Enums.chapt7_Enumerations.Enumerations.Color.Red
import com.allevite.scala3Reference.part2_Enums.chapt7_Enumerations.Enumerations.Planet.Mercury

object Enumerations:
  //An enumeration is used to define a type consisting of a set of named values.
  enum ColorNonParameterized:
    case Red, Green, blue

  //This defines a new sealed class, Color,
// with three values, Color.Red, Color.Green, Color.Blue.
// The color values are members of Colors companion object.

  //Parameterized enums

  enum Color(val rgb: Int):
    case Red extends Color(0xFF0000)
    case Green extends Color(0x00FF00)
    case Blue extends Color(0x0000FF)

//  The values of an enum correspond to unique integers.
//  The integer associated with an enum value is returned by its ordinal method:

  val red = Color.Red

  val redInt = red.ordinal //0

  //The companion object of an enum also defines three utility methods
  // valueOf, values, fromOrdinal
  val redColor: Color = Color.valueOf("Red")
  val allColors: Array[Color] = Color.values
  val blueColor: Color = Color.fromOrdinal(2)

  //user defined members of enum
  enum Planet(mass: Double, radius: Double):
    private val G = 6.67300E-11
    private  val _radius = radius

    def surfaceGravity = G * mass / (radius * radius)

    def surfaceWeight(otherMass: Double) = otherMass * surfaceGravity

    case Mercury extends Planet(3.303e+23, 2.4397e6)
    case Venus extends Planet(4.869e+24, 6.0518e6)
    case Earth extends Planet(5.976e+24, 6.37814e6)
    case Mars extends Planet(6.421e+23, 3.3972e6)
    case Jupiter extends Planet(1.9e+27, 7.1492e7)
    case Saturn extends Planet(5.688e+26, 6.0268e7)
    case Uranus extends Planet(8.686e+25, 2.5559e7)
    case Neptune extends Planet(1.024e+26, 2.4746e7)

  object Planet:
    def surfaceArea(p: Planet):Double = Math.PI * p._radius * p._radius

  @main def MAIn(): Unit =
    println(redInt) //0
    println(redColor)
    println(allColors(2))
    println(blueColor)
    println(Planet.surfaceArea(Mercury))
    println(Mercury.surfaceGravity)
