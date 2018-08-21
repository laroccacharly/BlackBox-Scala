# BlackBox Optimisation in Scala 

## First of all, why BlackBox Optimization? 
Instead of starting with a general definition, I will give a few analogies. 

BlackBox optimisation is like : 
- Finding the path between two points that minimizes the gas needed to travel between those points. 
- Finding the configuration of tables in a restaurant that maximizes its capacity. 
- Finding the configuration of a neural network that minimizes its error on the data. (the regression problem)
- Finding the mate that will make us happy. 


From that, blackBox optimisation can be simply defined as a general way to reach a goal. 
In fact, blackbox optimisation is very general 
because it can solve problems for which the analytic form is not known (which is almost always the case).

''''
Note : The first example I gave can be described analytically. 
Therefore, there are very efficient ways to solve it that doesn't involve blackbox optimization. 
''''

## How it works


When no analytic form is known, the only way to find a satisfying state is to try things out. 
Often, the only way to : 
- find a short path, is to try different paths. 
- find a profitable configuration of tables, is to try different configurations. 
- find an accurate neural network, is to try different network's architectures. 
- find a satisfying mate, is to try different mate. 

Note that I am not using the word "best" because optimality is impractical. 
It would take an infinite amount of time to find the actual shortest path or the perfect mate. 
Instead, we are interested in finding a state that is not too far away from a goal
within a reasonable amount of time/energy. 

For example, after trying a few different table configurations in your restaurant, you will probably stop exploring. 
Why? Because it is not reasonable to spend time/energy trying different configurations indefinitely. 
The expected gain from trying a new configuration decreases as you keep exploring. 

Formally, this concept is called a "stopping criteria". It tells us when the solution is "good enough". 
This is interesting because it is not clear what it should be in general. In fact, it is safe to say that we will 
always need human input to define it. 

/*
val (currentBestResult, currentBestSample, progress) = initialize()
while(progress > stoppingCriteria) {
  val sample = makeRandomSampleInDomain()
  val result = evaluateSample(sample)
  if(result < currentBestResult)
    progress = abs(currentBestSample - sample)
    currentBestSample = sample
    currentBestResult = result
}

 */

## Algorithms KPI

Blackbox algorithms are often characterised by the amount of time they need to reach that good enough solution. 
Sometimes, time isn't a concern and the bottle neck is the number of observations. 





 
## Why Scala? 

Honestly, I choose Scala because I wanted to learn it. 
Simply put, It offers a great balance between ease of use, flexibility and performance. 
Ease of use because its syntax is similar to Ruby's. 
Flexibility because it offers pure functional and OO approaches.
Performance because it runs on a well optimized virtual machine and has great support for currency. 