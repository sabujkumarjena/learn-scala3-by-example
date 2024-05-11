package com.allevite.scala3Reference.part2_Enums.chapt7_Enumerations

object Enumerations:
  //An enumeration is used to define a type consisting of a set of named values.
  enum Color:
    case Red, Green, blue

  //This defines a new sealed class, Color,
// with three values, Color.Red, Color.Green, Color.Blue.
// The color values are members of Colors companion object.

  //Parameterized enums
