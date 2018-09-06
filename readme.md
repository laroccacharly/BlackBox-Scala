# Distributed BlackBox Optimisation in Scala 

The goal of BlackBox optimization is to find the minimum (or the maximum) of a function
for which the analytic form is not known (which is almost always the case).

The idea is to sample the function (to try different input values) until you find one that is good enough. 

The easiest way to so : 
```scala
while(progress > stoppingCriteria) { // Iterate until you progressed enough 
  val sample = makeRandomSampleInDomain() // Make a random choice 
  val result = evaluateSample(sample) // Check how good you did
  if(result < currentBestResult) // If you did better than before 
    progress = abs(currentBestSample - sample) // update progress and your current best choice 
    currentBestSample = sample
    currentBestResult = result
}
```

Note that this algorithm might stop before being anywhere close to the minimum of the function. 

```scala
progress = abs(currentBestSample - sample)
```
`progress` could be very small if you happen to sample very close to the `currentBestSample` 
(the while loop will stop if `progress` is smaller than `stoppingCriteria`).

To avoid this let's put some dampening so that `progress` does not change so fast. 
```scala
progress = (abs(currentBestSample - sample) + progress) / 2
```

## EpsilonGreedy 
Instead of sampling randomly in the domain, why not sample greedily? 
That means sampling close to the `currentBestSample`. 
We can use an EpsilonGreedy sampling strategy that takes a greedy action `epsilon` percent of the time and non-greedy action `1-epsilon` percent of the time. For example: 

```scala
def isGreedy = nextRandom() < epsilon // if epsilon is 0.3 => a greedy sample will be taken 30% of the time. 

def sample = {
    if (isGreedy) sampleInGreedyDomain
    else sampleInNonGreedyDomain
}
```

With an EpsilonGreedy strategy, the sampling distribution can look like this : 
![alt text](plotGreedySampler.png)

`greedyDomainSize` could be an hyperparameter just like `epsilon`.  
In our case, the following formula is used  `greedyDomainSize = const * progress` because, as the algorithm converges
 to the minimum, the `greedyDomainSize` should get smaller. 

## A Distributed approach 

Is there a way to improve the basic algorithm even more? 
Why not use parallelism? The function doesn't have to be evaluated in a specific order. 
Instead, it can be sampled by multiple workers at the same time. Observations made by the workers 
are processed by a Master that updates the `currentBestObservation` until the `stoppingCriteria` is reached. 

Here is a possible architecture : 

![alt text](architecture.png)

A User gives a `configName` that a `ConfigFactory` uses to create the configuration object of the experiment. 
It basically contains hyperparameters such as `epsilon` and `stoppingCriteria` that are used to initialize all the different actors. 

- 1 : `Start` is sent to master. It triggers the experiment. 
- 2 : `Master` then sends a `StartExperiment` to the `Store` so it can keep track of when the experiment started. 
- 3 :  `Master` sends `Work` to each `Worker`. 
- 4 : `Worker` samples the function and collects `nbIter` observations. 
It returns the best observation to `Master`. 
- 5 : `Scheduler` sends `UpdateBestObservation` at regular intervals to `Master`. 
The update of the `currentBestObservation` is not done inline (when an observation is received)
because of raise conditions that can occur when two `Workers` send an observation at the same time. 
- 6 : We loop over 3, 4 and 5 until `stoppingCriteria` is reached. 
- 7 : When the experiment is done, `Master` gathers data and sends `EndExperiment` to the `Store`. 
- 8 : `Store` forwards the data to the `DataBase`. 

## API 

To run the experiment `square` 10 times. 
```
run -e square -n 10
```
Each experiment will be stored in a MongoDB collection. 
To view the results : 
```
run view -e square -a executionTime 
```
That will plot an histogram of the execution times 
for the `square` experiment. 


## Results (WIP)


