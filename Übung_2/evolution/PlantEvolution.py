'''
Created on 24.04.2014

@author: Ruman
'''
import random;
import statistics;
import itertools;

def clamp(x,_min,_max):
    return min(max(x, _min), _max);

class Individual:
    
    #===========================================================================
    # Generate a random individual (if not provided) with age 0 and predefined bloom and leaf mass
    #=========================================================================== 
    def __init__(self, L_zero, R_zero, T, u_r = None):      
        self.mass_leaf = L_zero;
        self.mass_bloom = R_zero;                
        self._growth_bloom = u_r if u_r != None else  [random.uniform(0,1) for _ in range(0,T)];      
        
        
    #===========================================================================
    # returns u_r(t)
    #===========================================================================
    def growth_bloom(self,t):
        return self._growth_bloom[t];
    
    #===========================================================================
    # returns 1 - u_r(t)
    #===========================================================================
    def growth_leaf(self,t):
        return 1 - self.growth_bloom(t);
    
    #===========================================================================
    # Grow the plant and return R(T)
    #===========================================================================
    def grow(self,r,T):        
     
        for t in range(T):            
            #Grow leaves, because days are discrete (integral <=> sum) + dt = 1 => sum up growth for each day
            self.mass_leaf += r * self.growth_leaf(t) * self.mass_leaf; #self.mass_leaf IS L(t)
            self.mass_bloom += r * self.growth_bloom(t) * self.mass_leaf; #^^^
            
        #After growth, L(T) is mass_leaf, R(T) is mass_bloom
        
        return self.mass_bloom;
    
    #===========================================================================
    # Returns a mutated variant of this individual
    #===========================================================================
    def mutate(self, L_zero, R_zero, T):
        
        #New value is x + z; z = SIGMA * ( N(0,1), ..., N(0,1))
        #SIGMA is standard deviation (mutation strength
        
        strength = statistics.stdev(self._growth_bloom); #calculate strength
        u_r = [ clamp((strength * random.gauss(0,1)) + x, 0, 1) for x in self._growth_bloom];  #ein bisschen funktional ist sehr praktisch :)
        
        return Individual(L_zero, R_zero, T, u_r)
    
    #===========================================================================
    # Print some data about the individual
    #===========================================================================
    def printData(self):
        
        print("*-- Individual --*");
        print("u_r(t): " + str(self._growth_bloom)[1:-1]);
        print("R(T) = " + str(self.mass_bloom));
    
            
    
        
    

#===============================================================================
# Evolves a start population and creates <descendants> children
#r ... energy production factor (constant)*
#L_zero ... initial bio mass of leafs
#R_zero ... initial bio mass of blooms
#T ... lifetime
#
#
#Algorithm evolves discrete in days (max lifetime T of each individual)
#
#will use comma strategy (plants can only live 20 days, then dead) 
#===============================================================================
def evolve(population_size, descendants, r, L_zero, R_zero, T, generations):   
  
    population = [Individual(L_zero, R_zero, T) for _ in range(population_size)];
    
    for generation in range(generations):
        
        print("++++ Generation " + str(generation + 1))
        
        #grow population
        for individual in population:
            individual.grow(r,T);
        
        #select lamda = descendants individuals to mutate        
        population.sort(key = lambda x: x.mass_bloom, reverse = True);
        parents = population[0:population_size-1];
        
        print("Best individual: ");
        parents[0].printData();
        
        #create new population of descendants by creating mutants
        population = [];
        
        for x in parents:
            population.extend([x.mutate(L_zero, R_zero, T) for _ in range(descendants)]);
            
        print("Population size: " + str(len(population)));
        
    #Finished! grow, sort and return   
    print("******** Finished ********");
    for individual in population:
        individual.grow(r,T);
    population.sort(key = lambda x: x.mass_bloom, reverse = True);
    return population[0];
        
        
        
    
def main():
    population_size = 10;
    descendants = 5;
    energy_production_factor = 0.1;
    L_zero = 0.01;
    R_zero = 0;
    T = 20;
    generations = 100;
    
    best = evolve(population_size, descendants, energy_production_factor, L_zero, R_zero, T, generations);
    best.printData();
    
    
main();
