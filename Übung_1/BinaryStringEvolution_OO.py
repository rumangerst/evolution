# coding: utf8

"""

Evolutionary algorithm, optimizing a bit string.

"""

import random;

global iteration;
iteration = 0;


class Individual:
    data =""; #the individual itself
    score=0; #the score of the individual
    
    """
    generates random individual with data and scores it
    
    """
    def __init__(self, data):
        
        self.data = data;
            
        #score the individual
        self.scoreIndividual(); 
            
    """
    scores the individual    
    """        
    def scoreIndividual(self):                            
        self.score = 0;                                
                                              
        for character in self.data:              
            self.score += 1 if character == "1" else 0;
            
    """
    Returns mutated variant of this individual
    """
    def getMutatedVariant(self, p):
        individual_chars = list(self.data); #Strings are immuteable and always recreating strings is expensive
                                                                                                               
        for i in range(len (individual_chars)):                                                                
            if (random.random() <= p):                                                                         
                                                                                                               
                char = individual_chars[i];                                                                    
                individual_chars[i] = "0" if  char == "1" else "1"; #swap the bit                              
                                                                                                               
        mutated = "".join(individual_chars);

        return Individual(mutated);
    
    """
    Creates a new random individual
    """
    @staticmethod
    def createRandomIndividual(size):
        data = "";                                    
                                                        
        for _ in range(size):                           
            data = data + str(random.randint(0,1)); 
       
        return Individual(data);
        
        
def selectbest(population):
    assert len(population) > 0;
    
    best = population[0]; #select the first
    bestscore = best.score;
    
    #Search for best individual
    for individual in population:
        
        score = individual.score;
        
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
  
    global bestindividual;
    global population;
    global iteration;
    
    population = [parent]; #only the parent exists
    iteration = 0; #set iteration to zero
    
    bestindividual = parent; #save it here for termination   
    
    while bestindividual.score != 40 and iteration < 10000:        
       
        iteration += 1; #count iteration
       
        
        print "iteration " + str(iteration);
        
        #Look for best indidual
        bestindividual = selectbest(population);
        
        print "best: " + bestindividual.data;
       
        
        #Create a new population with only individual and it's descendants in it
        population = [bestindividual];
        
        
        for _ in range(descendants):
            population.append(bestindividual.getMutatedVariant(mutation_rate));
            
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
    
        parent = Individual.createRandomIndividual(40); #parameter: n
        descendants = 400; # = λ       
    
        best = evolution(parent, descendants, mutation_rate); #returns best individual
        
        mutationtest_iterationresult.append(iteration);   
        mutationtest_fresult.append(best.score);
        
    print "RESULT (iter):" + str( mutationtest_iterationresult);     
    print "RESULT (best):" + str( mutationtest_fresult);   
  
"""
Main method
"""
def main():
    
    parent = Individual.createRandomIndividual(40); #parameter: n
    descendants = 400; # = λ
    mutation_rate = 0.05; # = p ; Mutation rate should be selected that only 1-2 chars will mutate
    
    best = evolution(parent, descendants, mutation_rate); #returns best individual
    
    print "Best individual in " + str(iteration) + " iterations: " + best.data;
    
#main();
main_mutationtest();
