# coding: utf8

"""

Evolutionary algorithm, optimizing a bit string.

"""

import random;

iteration = 0;

"""
Creates an individual with given size
"""
def create_new_individual(size):
    output = "";
    
    for _ in range(size):
        output = output + str(random.randint(0,1));
    return output;

"""
Generate Score for the individual
"""
def F(individual):
    score = 0;
    
    for character in individual:
        score += 1 if character == "1" else 0;
        
    return score;

"""
Returns a mutated variant of the invididual with mutation probability p
"""
def getMutant(individual, p):
    individual_chars = list(individual); #Strings are immuteable and always recreating strings is expensive
    
    for i in range(len (individual_chars)):
        rand = 1.0 - random.random(); #random.random() returns number in [0,1), but we want a number in (0,1], so invert it
        assert rand >= 0 and rand <= 1; #for checking
            
                                                                   
        if (rand <= p):                                
            
            char = individual_chars[i];
            individual_chars[i] = "0" if  char == "1" else "1"; #swap the bit
    
    return "".join(individual_chars);

def selectbest(population):
    assert len(population) > 0;
    
    best = population[0]; #select the first
    bestscore = F(best);
    
    #Search for best individual
    for individual in population:
        
        score = F(individual);
        
        if score > bestscore:
            best = individual;
            bestscore = score;
            
    return best;
        
"""
Generates population and runs evolution on it

parent - Inital parent
descendants - λ
mutation_rate - p

"""
def evolution(parent, descendants, mutation_rate):
    
    global iteration;
    global bestindividual;
    global population;
    
    population = [parent]; #only the parent exists
    iteration = 0; #set iteration to zero
    
    bestindividual = parent; #save it here for termination   
    
    while F(bestindividual) != 40 and iteration < 10000:        
       
        iteration += 1; #count iteration
       
        
        print "iteration " + str(iteration);
        
        #Look for best indidual
        bestindividual = selectbest(population);
        
        print "best: " + bestindividual;
       
        
        #Create a new population with only individual and it's descendants in it
        population = [bestindividual];
        
        
        for _ in range(descendants):
            population.append(getMutant(bestindividual, mutation_rate));
            
        #Go on until terminated.
        
    #terminated!
    #return the best individual
    
    return bestindividual;

def main_mutationtest():
    
    global mutationtest_iterationresult; 
    global mutationtest_fresult;
    mutationtest_iterationresult = [];
    mutationtest_fresult = [];
    
    mutationtest_testranges = [x * 0.01 for x in range(0,11) ];
    mutationtest_testranges.extend( [x * 0.05 for x in range(3,21) ] );
    
    for mutation_rate in mutationtest_testranges:
        
        print "**** TESTING WITH RATE " + str(mutation_rate);
    
        parent = create_new_individual(40); #parameter: n
        descendants = 400; # = λ       
    
        best = evolution(parent, descendants, mutation_rate); #returns best individual
        
        mutationtest_iterationresult.append(iteration);   
        mutationtest_fresult.append(F(best));
        
    print "RESULT (iter):" + str( mutationtest_iterationresult);     
    print "RESULT (best):" + str( mutationtest_fresult);   
  
"""
Main method
"""
def main():
    
    parent = create_new_individual(40); #parameter: n
    descendants = 100; # = λ
    mutation_rate = 0.05; # = p ; Mutation rate should be selected that only 1-2 chars will mutate
    
    best = evolution(parent, descendants, mutation_rate); #returns best individual
    
    print "Best individual in " + str(iteration) + " iterations: " + best;
    
#main();
main_mutationtest();
