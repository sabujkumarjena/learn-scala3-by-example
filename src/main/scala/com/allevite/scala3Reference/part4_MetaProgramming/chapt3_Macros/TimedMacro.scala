package com.allevite.scala3Reference.part4_MetaProgramming.chapt3_Macros

import scala.quoted.{Expr, Quotes, Type}

object TimedMacro :
  inline def timed[T](inline expr: T): T = ${ timedImpl('{ expr }) }

  private def timedImpl[T: Type](expr: Expr[T])(using Quotes): Expr[T] =
    '{
      val start = System.currentTimeMillis()
      try $expr
      finally
        val end = System.currentTimeMillis()
        // val exprAsString = ${ Expr(expr.show) }
        val exprAsString = ${ Expr(exprAsCompactString(expr)) }.replaceAll("\\s+", "").trim()
        println(s"Evaluating $exprAsString took: ${end - start}")
    }

  private def exprAsCompactString[T: Type](expr: Expr[T])(using ctx: Quotes): String =
    import ctx.reflect.*
    expr.asTerm match
      case Inlined(_, _, Apply(method, params)) => s"${method.symbol.name}(${params.map(_.show).mkString("")})"
      case _ => expr.show
