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

  val myMule: Mule[String]=  new Mule[String]{}

  val myMule2: Horse & Donkey[String] =  new Mule[String]{}

  @main def test(): Unit =
    myMule.carry("sabuj")
    println(myMule.name)

