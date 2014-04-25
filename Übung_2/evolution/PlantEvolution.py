# -*- coding: utf-8 -*-

'''
Created on 24.04.2014

@author: Ruman
'''
import random;
import statistics;
import numpy;
import matplotlib.pyplot as plt;

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
        
    #===========================================================================
    # run after grow()!
    #===========================================================================
    def fitness(self):
        return self.mass_bloom;
    
            
    
        
    

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
  
    best_individuals = [];
    population = [Individual(L_zero, R_zero, T) for _ in range(population_size)];
    
    for generation in range(generations):
        
        print("++++ Generation " + str(generation + 1))
        
        #grow population
        for individual in population:
            individual.grow(r,T);
        
        #select lamda = descendants individuals to mutate        
        population.sort(key = lambda x: x.mass_bloom, reverse = True);
        parents = population[0:population_size-1];
        
        #print("Best individual: ");
        #parents[0].printData();
        
        #add to list
        best_individuals.append(parents[0]);
        
        #create new population of descendants by creating mutants
        population = [];
        
        for x in parents:
            population.extend([x.mutate(L_zero, R_zero, T) for _ in range(descendants)]);
            
        ##print("Population size: " + str(len(population)));
        
    #Finished! grow, sort and return   
    print("******** Finished ********");
    for individual in population:
        individual.grow(r,T);
    population.sort(key = lambda x: x.mass_bloom, reverse = True);
    best_individuals.append(population[0]);

    return best_individuals;
        
def plotdata(TEST_DATA, TEST_DATA_u_r, generations, population_size, descendants):
    
    
    data_min = [min(x) for x in TEST_DATA];
    data_max = [max(x) for x in TEST_DATA];
    data_avg = [numpy.mean(x) for x in TEST_DATA];
    data_u_r_avg = [numpy.mean(x) for x in TEST_DATA_u_r];
    x_axis = range(generations + 1);
    
    ############### Create main plot
    plt.subplot(2,1,1);
    
    plt.plot(x_axis, data_min, color='black', linestyle='-', label='Minimum');
    plt.plot(x_axis, data_avg, color='red', linestyle='-', label='Average');
    plt.plot(x_axis, data_max, color='black', linestyle='-', label='Maximum');   
        
    #plt.axis([0,generation])
    plt.xlabel("Generation ($t$)");
    plt.ylabel("$R(T)$");
    plt.title("$R(T)$ über die Generationen $(\mu, \lambda)\ \mu = {0},\ \lambda = {1}$"
              .format(population_size, descendants));
    
    ############### Create u_r plot
    plt.subplot(2,1,2);
    
    plt.xlabel("Generation ($t$)");
    plt.ylabel("$u_r$");
    plt.title("$u_r$ über die Generation");
    
    plt.plot(x_axis, data_u_r_avg, color='red', linestyle=':', label='Average');
    
    
    #show
    plt.show(); 
    
def main_readandplot():
    
    file = open("testdata_params", "r");    
    data_params = file.read();
    data_params = eval(data_params);    
    file.close();
    
    file = open("testdata", "r");    
    data = file.read();
    data = eval(data);    
    file.close();
    
    file = open("testdata_u_r", "r");    
    data_u_r = file.read();
    data_u_r = eval(data_u_r);
    file.close();
    
    plotdata(data, data_u_r, data_params[6], data_params[0], data_params[1]);
    
    
def main():
    population_size = 10;
    descendants = 5;
    energy_production_factor = 0.1;
    L_zero = 0.01;
    R_zero = 0;
    T = 20;
    generations = 200;
    
    best = evolve(population_size, descendants, energy_production_factor, L_zero, R_zero, T, generations);
    best = best[generations]; #generations are 0 - <generations> (parents are counting as gen 0)
    best.printData();
    
def main_plottest():
    population_size = 10;
    descendants = 3;
    energy_production_factor = 0.1;
    L_zero = 0.01;
    R_zero = 0;
    T = 20;
    generations = 200;
    
    TEST_RUNS = 25;
    TEST_DATA_PARAMS = [population_size, descendants, energy_production_factor, L_zero, R_zero, T, generations];
    TEST_DATA = [[] for _ in range(generations + 1)];
    TEST_DATA_u_r = [[] for _ in range(generations + 1)];
    
    for run in range(TEST_RUNS):
        print("------------- * Test Run " + str(run));
        
        best_individuals = evolve(population_size, descendants, energy_production_factor, L_zero, R_zero, T, generations);
                
        for generation in range(generations + 1):
            TEST_DATA[generation].append( best_individuals[generation].mass_bloom );
            TEST_DATA_u_r[generation].append( best_individuals[generation].growth_bloom(T - 1) );
        
    #first, write testdata to file for eventual later use
    file = open("testdata", "w");
    file.write(str(TEST_DATA));
    file.close();
    
    file = open("testdata_u_r", "w");
    file.write(str(TEST_DATA_u_r));
    file.close();
    
    file = open("testdata_params", "w");
    file.write(str(TEST_DATA_PARAMS));
    file.close();
    
    #ok, plot testdata
    plotdata(TEST_DATA, TEST_DATA_u_r, generations, population_size, descendants);
    
    
    
#main_plottest();
main_readandplot();
