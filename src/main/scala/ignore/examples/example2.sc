abstract class Stack[A] {
  def push(x: A): Stack[A] = new NonEmptyStack[A](x, this)
  def isEmpty: Boolean
  def top: A
  def pop: Stack[A]
}
class EmptyStack[A] extends Stack[A] {
  def isEmpty = true
  def top = throw new Error("EmptyStack.top")
  def pop = throw new Error("EmptyStack.pop")
}
class NonEmptyStack[A](elem: A, rest: Stack[A]) extends Stack[A] {
  def isEmpty = false
  def top = elem
  def pop = rest
}


val x = new EmptyStack[Int]
val y = x.push(1).push(2)
y.pop.top



abstract class Expr
case class Number(n: Int) extends Expr


def eval(e: Expr): Int = e match {
  case Number(n) => n * n
}



(1,2,3).getClass().getName()

val l = List(12,3)
val l2 = List(4, 4, 4)
l.zip(l)
l:::l2
l.reverse
l.map(x => 2*x)
l
l.filter(x => x==l.head)
s"The duration of c3 is"

