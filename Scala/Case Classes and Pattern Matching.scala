/*abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String,left: Expr, right: Expr) extends Expr

def simplifyTop(expr: Expr): Expr = expr match {
  case UnOp("",  UnOp("",  e)) => e // Double negation
  case BinOp("+", e, Number(0)) => e // Adding zero
  case BinOp("*", e, Number(1)) => e // Multiplying by one
  case _ => expr
}

simplifyTop(UnOp("", UnOp("", Var("x"))))
*/

/*
//Example
import math.{E, Pi}
E match {
  case pi => "strange math? Pi = "+ pi
  case _ => "OK"
}
val pi = math.Pi
*/

/*
//Example
sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String,
                 left: Expr, right: Expr) extends Expr

def describe(e: Expr): String = (e: @unchecked) match {
  case Number(_) => "a number"
  case Var(_) => "a variable"
}

describe(new Number(1.5))
*/

val second: PartialFunction[List[Int],Int] = {
  case x :: y :: _ => y
}
