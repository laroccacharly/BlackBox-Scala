import breeze.linalg.DenseVector
import org.apache.commons.math3.distribution.TDistribution
import org.apache.commons.math3.exception.MathIllegalArgumentException
import org.apache.commons.math3.stat.descriptive.SummaryStatistics
import math.abs


object Car {
  def size = 10
}
Car.size

def sqrtNewton(x: Double) = {
  def isGoodEnough(guess: Double): Boolean =
    abs(guess * guess - x) < 0.001 * x

  def improve(guess: Double)=
    (guess + x / guess) / 2

  def abs(x: Double) = if (x < 0) -x else x

  def sqrtIter(guess: Double): Double =
    if (isGoodEnough(guess)) guess
    else sqrtIter(improve(guess))


  sqrtIter(1)
}

def fixedPoint(f: Double => Double)(firstGuess: Double): Double = {

  val tolerance = 0.001
  def isGood =
    (x: Double, y: Double) => abs((x-y)/x) < x * tolerance

  def iter(guess: Double): Double = {
    val next = f(guess)
    if (isGood(guess, next)) next
    else iter(next)

  }

  iter(firstGuess)
}

def averageDamp =
  (f: Double => Double) => (x: Double) => (x + f(x))/2


def sqrt(x: Double): Double =
  fixedPoint(averageDamp(y => x / y))(1)

sqrt(2)


def sumv2(f: Int => Int): (Int, Int) => Int = {
  def sumF(a: Int, b: Int): Int =
    if (a > b) 0 else f(a) + sumF(a + 1, b)
  sumF
}

def sum(f: Int => Int)(a: Int, b: Int): Int =
  if (a > b) 0 else f(a) + sum(f)(a + 1, b)

import breeze.stats._
val a = DenseVector(1.0, 2.0)
mean(a)
stddev(a)
