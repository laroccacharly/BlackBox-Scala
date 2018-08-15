package algorithm

import breeze.linalg._
import breeze.stats._
import challenge.Challenge

abstract class Algorithm(challenge: Challenge) {
  def time[R](block: => R): Double = {
    val t0 = System.nanoTime()
    val result = block    // call-by-name
    val t1 = System.nanoTime()
    val total_time = (t1 - t0) / 1e6 // returns milliseconds
    total_time
  }

  val nb_iter: Int = 5

  def meanAndErrScores: (Double, Double) = {
    val scores = DenseVector.range(1, nb_iter).map(_ => score)
    (mean(scores), stddev(scores))
  }

  def score: Double
  //def run: (Double, Double) // (value of score, error of score)
}

class SimpleAlgorithm(challenge: Challenge, batch_size: Int) extends Algorithm(challenge) {
  override def score: Double = {
    time {
      while (!challenge.done) {
        (1 until batch_size).foreach(_ => challenge.step)
      }
    }
  }
}