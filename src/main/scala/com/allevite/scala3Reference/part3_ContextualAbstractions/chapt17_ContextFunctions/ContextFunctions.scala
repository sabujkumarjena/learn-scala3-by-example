package com.allevite.scala3Reference.part3_ContextualAbstractions.chapt17_ContextFunctions

object ContextFunctions :
  /**
   * Context functions are functions with (only) context parameters. Their types are context function types.
   */

  import scala.concurrent.ExecutionContext
  type Executable[T] = ExecutionContext ?=> T

//  given ec: ExecutionContext = ???
//
//  def f(x: Int): ExecutionContext ?=> Int = ???
//
//  val ans: Int = f(2) //argument is inferred // f(2)(using ec) explicit argument
//
//  def g(arg: ExecutionContext ?=> Int) = ???
//
//  g(22) // expanded into g((ev: ExecutionContext) ?=> 22)
//  g(f(2)) // is expanded to g((ev: executionContext) ?=> f(2)(using ev))
//  g((ctx: ExecutionContext) ?=> f(3)) // expanded to g((ctx: ExecutionContext) ?=> f(3)(using ctx))

  //Example: Builder Pattern

  import scala.collection.mutable.ArrayBuffer

  class Table:
    val rows = new ArrayBuffer[Row]

    def add(r: Row): Unit = rows += r

    override def toString = rows.mkString("Table(", ", ", ")")

  class Row:
    val cells = new ArrayBuffer[Cell]

    def add(c: Cell): Unit = cells += c

    override def toString = cells.mkString("Row(", ", ", ")")

  case class Cell(elem: String)

  def table(init: Table ?=> Unit): Table =
    given t: Table = Table()
    init
    t

  def row(init: Row ?=> Unit)(using t: Table): Unit =
    given r: Row = Row()
    init
    t.add(r)

  def cell(str: String)(using r: Row) =
    r.add(new Cell(str))

  @main def demo():Unit =
    val myTable =
      table {
        row {
          cell("TL")
          cell("TR")
        }
        row {
          cell("BL")
          cell("BR")
        }
      }
    println(myTable)


/**
 * With that setup, the table construction code above compiles and expands to:
 *
 * table { ($t: Table) ?=>
 *
 * row { ($r: Row) ?=>
 * cell("top left")(using $r)
 * cell("top right")(using $r)
 * }(using $t)
 *
 * row { ($r: Row) ?=>
 * cell("bottom left")(using $r)
 * cell("bottom right")(using $r)
 * }(using $t)
 * }
*/