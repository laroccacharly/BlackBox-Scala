
import util._
import math._
import scala.concurrent._
import ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.collection.mutable.ArraySeq

def findMin(fmin: Double => Double, interval: (Double, Double), nb_samples: Int = 1000) = {
  def next_sample =
    () => Random.nextDouble() * (interval._2 - interval._1) + interval._1

  def eval(x: Any) = {
    val sample = next_sample()
    (sample, fmin(sample))
  }
  List.range(1, nb_samples).map(eval).minBy(p => p._2)
}


val f = Future {
  findMin(x => cos(x) , (-1, 1))
}


f onComplete {
  case Success(value) => println("yay" + value)
  case Failure(t) => println("ewrrrr")

}

Await.ready(f, 200 milliseconds)