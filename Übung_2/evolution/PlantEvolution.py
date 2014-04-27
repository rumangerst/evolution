# -*- coding: utf-8 -*-

'''
Created on 24.04.2014

HINWEIS: Mit Python Version 3.4 geschrieben!
Anmerkung: Windows ist sehr SCHLECHT zum Entwickeln mit Python -.-

@author: Ruman
'''
import random;
import numpy;
import matplotlib.pyplot as plt;
import pickle;

def clamp(x,_min,_max):
    #return x;
    return min(max(x, _min), _max);

class Individual:
    
    #===========================================================================
    # Generate a random individual (if not provided) with age 0 and predefined bloom and leaf mass
    #=========================================================================== 
    def __init__(self, L_zero, R_zero, T, u_r = None):      
        self._mass_leaf = [float(L_zero)];
        self._mass_bloom = [float(R_zero)];      
        
        if u_r == None:
            print("Created new random individual!");
        
                          
        #self._growth_bloom = u_r if u_r != None else  [1 for _ in range(0,T)]; #nur zum testen      
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
    # Leaf mass
    #===========================================================================
    def L(self, t):
        return self._mass_leaf[t];
    
    #===========================================================================
    # Bloom mass
    #===========================================================================
    def R(self, t):
        return self._mass_bloom[t];
    
    #===========================================================================
    # Grow the plant and return R(T)
    #===========================================================================
    def grow(self,r,T):        
     
        for t in range(T):            
            #Grow leaves, because days are discrete (integral <=> sum) + dt = 1 => sum up growth for each day
            self._mass_leaf.append(self.L(t) + r * self.growth_leaf(t) * self.L(t)); #self.mass_leaf IS L(t)
            self._mass_bloom.append(self.R(t) + r * self.growth_bloom(t) * self.L(t)); #^^^
            
        #After growth, L(T) is mass_leaf, R(T) is mass_bloom
        
        #print(str(self._mass_leaf));
        #print(str(self._mass_bloom));
        
        return self.R(T);
    
    #===========================================================================
    # Returns a mutated variant of this individual
    #===========================================================================
    def mutate(self, L_zero, R_zero, T):
        
        sigma = numpy.std(self._growth_bloom);
        
        if sigma == 0:
            sigma = 1.0;
        
        def y_(y):            
                        
            z = random.gauss(0,1) * sigma;
            y_ = y + z;
            
            return clamp(y_, 0, 1);
      
        
        u_r = [ y_(x) for x in self._growth_bloom]; 
                
        return Individual(L_zero, R_zero, T, u_r)
    
    #===========================================================================
    # Print some data about the individual
    #===========================================================================
    def printData(self):
        
        print("*-- Individual --*");
        print("u_r(t): " + str(self._growth_bloom)[1:-1]);
        print("R(t) = " + str(self._mass_bloom));       
        print("R(T) = " + str(self._mass_bloom[-1]));        

class EvolutionTestResult:
    #===========================================================================
    # Results
    #===========================================================================
    TEST_RUNS = 25;
    TEST_DATA = [];                                                                                 
    TEST_DATA_leaf = [];                                                                            
    TEST_DATA_u_r = [];         
    
    #===========================================================================
    # Test parameters
    #===========================================================================
    EVOLUTION_STRATEGY = "+";    
    population_size = 20;
    descendants = 40;
    energy_production_factor = 0.1;
    L_zero = 0.01;
    R_zero = 0;
    T = 20;
    generations = 200;
    
    @staticmethod
    def load(filename):
        file = open(filename, "rb");
        data = pickle.load(file);
        file.close();
        
        result = EvolutionTestResult();
        
        result.TEST_RUNS = data["TEST_RUNS"];
        result.TEST_DATA = data["TEST_DATA"];
        result.TEST_DATA_leaf = data["TEST_DATA_leaf"];
        result.TEST_DATA_u_r = data["TEST_DATA_u_r"];
        
        result.EVOLUTION_STRATEGY = data["EVOLUTION_STRATEGY"];
        result.population_size = data["population_size"];
        result.descendants = data["descendants"];
        result.energy_production_factor = data["energy_production_factor"];
        result.L_zero = data["L_zero"];
        result.R_zero = data["R_zero"];
        result.T = data["T"];
        result.generations = data["generations"];
        
        return result;
    
    def write(self, filename):
        
        file = open(filename, "wb");
        
        data = {};
        
        data["TEST_RUNS"] = self.TEST_RUNS;
        data["TEST_DATA"] = self.TEST_DATA;
        data["TEST_DATA_leaf"] = self.TEST_DATA_leaf;
        data["TEST_DATA_u_r"] = self.TEST_DATA_u_r;
        
        data["EVOLUTION_STRATEGY"] = self.EVOLUTION_STRATEGY;
        data["population_size"] = self.population_size;
        data["descendants"] = self.descendants;
        data["energy_production_factor"] = self.energy_production_factor;
        data["L_zero"] = self.L_zero;
        data["R_zero"] = self.R_zero;
        data["T"] = self.T;
        data["generations"] = self.generations;
        
        pickle.dump(data, file);

        file.close();
        
    #===========================================================================
    # Plot the data, stored in this class
    #===========================================================================
    def plotdata(self):                                          
                                                                                                                                                   
                                                                                                                                                   
        data_min = [min(x) for x in self.TEST_DATA];                                                                                                    
        data_max = [max(x) for x in self.TEST_DATA];                                                                                                    
        data_avg = [numpy.mean(x) for x in self.TEST_DATA];                                                                                             
        data_leaf_avg = [numpy.mean(x) for x in self.TEST_DATA_leaf];                                                                                   
                                                                                                                                                   
        x_axis = range(self.generations + 1);                                                                                                           
                                                                                                                                                   
        ############### Create main plot                                                                                                           
        fig_fitness = plt.subplot(2,1,1);                                                                                                          
                                                                                                                                                   
        plt.plot(x_axis, data_min, color='black', linestyle='-', label='Minimum R(T)');                                                            
        plt.plot(x_axis, data_avg, color='red', linestyle='-', label='Average R(T)');                                                              
        plt.plot(x_axis, data_max, color='black', linestyle='-', label='Maximum R(T)');                                                            
                                                                                                                                                   
        plt.plot(x_axis, data_leaf_avg, color='green', linestyle=':', label='Average L(T)');                                                       
                                                                                                                                                   
        #plt.axis([0,generation])                                                                                                                  
        plt.xlabel("Generation ($t$)");                                                                                                            
        plt.ylabel("$R(T)$");                                                                                                                      
                                                                                                                                                   
        if self.EVOLUTION_STRATEGY == "+":                                                                                                              
            plt.title("$R(T)$-Entwicklung über die Generation $(\mu + \lambda)\ \mu = {0},\ \lambda = {1}$ (25 Durchläufe)"                        
                  .format(self.population_size, self.descendants));                                                                                          
        else:                                                                                                                                      
            plt.title("$R(T)$-Entwicklung über die Generation $(\mu, \lambda)\ \mu = {0},\ \lambda = {1}$ (25 Durchläufe)"                         
                  .format(self.population_size, self.descendants));                                                                                          
                                                                                                                                                   
                                                                                                                                                   
        #draw error bars                                                                                                                           
        data_error = [numpy.std(x) / numpy.sqrt(len(x)) for x in self.TEST_DATA];                                                                       
        plt.errorbar(x_axis, data_avg, yerr= data_error);                                                                                          
                                                                                                                                                   
        #draw legend                                                                                                                               
        fig_fitness.legend(loc = "lower right");                                                                                                   
                                                                                                                                                   
        #===========================================================================                                                               
        # Create u_r plot                                                                                                                          
        #===========================================================================                                                               
        x_axis = range(1, self.T + 1);                                                                                                                  
                                                                                                                                                   
        fig_u_r = plt.subplot(2,1,2);                                                                                                              
                                                                                                                                                   
        plt.xlabel("Tage ($t$)");                                                                                                                  
        plt.ylabel("$u_r$");                                                                                                                       
        plt.title("Entwicklung der $u_r$ über die Tage");                                                                                          
                                                                                                                                                   
        plt.axis([1,20,0,1]);                                                                                                                      
                                                                                                                                                   
        for generation in range(self.generations + 1):                                                                                                  
                                                                                                                                                   
            _u_r_source_data = self.TEST_DATA_u_r[generation]; #get array of u_r(t) for all 25 runs                                                     
            data_u_r_avg = [numpy.mean([ x[day] for x in _u_r_source_data ]) for day in range(self.T)];                                                 
                                                                                                                                                   
            ### Python ist cool!!!!                                                                                                                
            _color, _linestyle, _label = ["red", "-", "Average $u_r$"] if generation == self.generations else ["gray", ":", ""];                        
                                                                                                                                                   
            plt.plot(x_axis,data_u_r_avg, color=_color, linestyle=_linestyle, label = _label);                                                     
                                                                                                                                                   
        #print latest avg u_r                                                                                                                      
        print("Latest AVG u_r:")                                                                                                                   
        print(str([numpy.mean([ x[day] for x in self.TEST_DATA_u_r[self.generations] ]) for day in range(self.T)]));                                              
                                                                                                                                                   
        #draw legend                                                                                                                               
        fig_u_r.legend(loc = "lower right");                                                                                                       
                                                                                                                                                   
        #show                                                                                                                                      
        plt.show();    
    
        
        
    
class EvolutionTask:   
    
    EVOLUTION_STRATEGY = "+";
    
    population_size = 20;
    descendants = 40;
    energy_production_factor = 0.1;
    L_zero = 0.01;
    R_zero = 0;
    T = 20;
    generations = 200;
    
    def __init__(self):
        print("Created a new evolution task!");

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
    #will use comma or plus strategy                                                             
    #===============================================================================                                                  
    def evolve(self):         
        
        #Load all variables from class to make everything easier
        population_size = self.population_size;  
        descendants = self.descendants;
        r = self.energy_production_factor;
        L_zero = self.L_zero;
        R_zero = self.R_zero;
        generations = self.generations;
        T = self.T;        
        EVOLUTION_STRATEGY = self.EVOLUTION_STRATEGY;                                   
                                                                                                                                      
        best_individuals = [];                                                                                                        
                                                                                                                                      
        #initialize start population                                                                                                  
        population = [Individual(L_zero, R_zero, T)];                                                                                 
                                                                                                                                      
        #grow population (evaluation)                                                                                                 
        for individual in population:                                                                                                 
            individual.grow(r,T);                                                                                                     
                                                                                                                                      
        for generation in range(generations):                                                                                         
                                                                                                                                      
            print("++++ Generation " + str(generation + 1))                                                                           
                                                                                                                                      
                                                                                                                                      
                                                                                                                                      
            #select best individual of current population a parent                                                                    
            parent = max(population, key = lambda x: x.R(T));                                                                         
            best_individuals.append(parent); #add it to output list for later use                                                     
                                                                                                                                      
            #create descendants                                                                                                       
            next_generation = [];                                                                                                     
                                                                                                                                      
            for _ in range(descendants - ( 1 if EVOLUTION_STRATEGY == "+" else 0 )): #create lamda desc. or lamda - 1 (if plus strat.)
                next_generation.append(parent.mutate(L_zero, R_zero, T));                                                             
                                                                                                                                      
            #grow children (evaluate)                                                                                                 
            for individual in next_generation:                                                                                        
                individual.grow(r,T);                                                                                                 
                                                                                                                                      
                                                                                                                                      
            #Create a new population (using the set strategy)                                                                         
            if EVOLUTION_STRATEGY == "+":                                                                                             
                #take best individuals of parents or children as new population (plus strategy)                                       
                population = next_generation + [parent];                                                                              
            else:                                                                                                                     
                #take population_size best individuals as new population (comma strategy)                                             
                population = next_generation;                                                                                         
                                                                                                                                      
            #environment selection                                                                                                    
            population.sort(key= lambda x: x.R(T), reverse=True);                                                                     
            population = population[0:population_size];                                                                               
                                                                                                                                      
        #Finished! grow, sort and return                                                                                              
        print("******** Finished ********");                                                                                          
        for individual in population:                                                                                                 
            individual.grow(r,T);                                                                                                     
                                                                                                                                      
        best_individuals.append(max(population, key = lambda x: x.R(T)));                                                             
                                                                                                                                      
        return best_individuals;                                                                                                      

    #===========================================================================
    # Run a test for this evolutionary algorithm
    #===========================================================================
    def evolutiontest(self):
        TEST_RUNS = 25;                                                                                                                  
                           
        TEST_DATA = [[] for _ in range(self.generations + 1)];                                                                                 
        TEST_DATA_leaf = [[] for _ in range(self.generations + 1)];                                                                            
        TEST_DATA_u_r = [[] for _ in range(self.generations + 1)];                                                                             
                                                                                                                                          
        for run in range(TEST_RUNS):                                                                                                      
            print("------------- * Test Run " + str(run));                                                                                
                                                                                                                                          
            best_individuals = self.evolve();            
                                                                                                                                          
            for generation in range(self.generations + 1):                                                                                     
                TEST_DATA[generation].append( best_individuals[generation].R(self.T) );                                                        
                TEST_DATA_leaf[generation].append( best_individuals[generation].L(self.T) );                                                   
                TEST_DATA_u_r[generation].append( best_individuals[generation]._growth_bloom ); 
                
                
        #generate a result
        result = EvolutionTestResult();
        
        result.EVOLUTION_STRATEGY = self.EVOLUTION_STRATEGY;
        result.population_size = self.population_size;
        result.descendants = self.descendants;
        result.energy_production_factor = self.energy_production_factor;
        result.generations = self.generations;
        result.L_zero = self.L_zero;
        result.R_zero = self.R_zero;
        result.T = self.T;             
        
        result.TEST_DATA = TEST_DATA;
        result.TEST_DATA_leaf = TEST_DATA_leaf;
        result.TEST_DATA_u_r = TEST_DATA_u_r;
        result.TEST_RUNS = TEST_RUNS;
        
        return result;                             
                                                                                                                                          
    
    
def run_test():
    
    test_plus = EvolutionTask();
    test_plus.EVOLUTION_STRATEGY = "+";
    result_plus = test_plus.evolutiontest();   
    result_plus.write("TEST_result_plus"); 
    result_plus.plotdata();
    
    test_comma = EvolutionTask();
    test_comma.EVOLUTION_STRATEGY = ",";
    result_comma = test_comma.evolutiontest();   
    result_comma.write("TEST_result_comma"); 
    result_comma.plotdata();
    
def show_plot(filename):
    result = EvolutionTestResult.load(filename);
    result.plotdata();

    
 
#run_test();
show_plot("TEST_result_plus");
