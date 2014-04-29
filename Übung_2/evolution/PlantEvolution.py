# -*- coding: utf-8 -*-

'''
Created on 24.04.2014

HINWEIS: Mit Python Version 3.4 (kompatibel zu 3.3 wegen ohne ENUM) geschrieben!
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

class EvolutionStrategy:
    PLUS = 0;
    COMMA = 1;
    
class EvolutionRecombStrategy:
    DISCRETE = 0;
    INTERMEDIATE = 1;

class Individual:
    
    _mass_leaf = 0;
    _mass_bloom = 0;
    
    T = 0;
    
    #Object Parameters
    _growth_bloom = [];
    
    #Strategy Parameters
    _sigma = 0;
    
    #===========================================================================
    # Generate a random individual (if not provided) with age 0 and predefined bloom and leaf mass
    #=========================================================================== 
    def __init__(self, L_zero, R_zero, T, u_r = None, sigma = None):      
        self.T = T;
        self._mass_leaf = [float(L_zero)];
        self._mass_bloom = [float(R_zero)];  
        
        
        if u_r == None:
            print("Created new random individual!");
        
    
        self._growth_bloom = u_r if u_r != None else  [random.uniform(0,1) for _ in range(0,T)]; 
        self._sigma = sigma if sigma != None else random.uniform(1,5);     
        
        
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
    # Creates a new individual with all object- and strategy parameters of this object
    #===========================================================================
    def copy(self):
        return Individual(self.L(0), self.R(0), self.T, self._growth_bloom, self._sigma);
       
    #===========================================================================
    # Sets strategy parameters of individual to recombined strategy parameters of parents
    #===========================================================================    
    def recombStrategyParam(self, parents):
        # recombination by using mean
        
        mean =  numpy.mean([x._sigma for x in parents]);
        self._sigma = mean;
        
    #===========================================================================
    # Sets object parameters of individual to recombined object parameters of parents    #===========================================================================
   
    def recombObjectParam(self, parents,strategy):
        
        if strategy == EvolutionRecombStrategy.INTERMEDIATE:
            
            #recombination using mean of each u_r(t) for each time
            mean_u_r = [numpy.mean([x.growth_bloom(t) for x in parents]) for t in range(self.T)];
            self._growth_bloom = mean_u_r;
            
        else:
            
            #recombination by baking together random explicit values of u_r
            baked_u_r = [random.choice(parents).growth_bloom(t) for t in range(self.T)];
            self._growth_bloom = baked_u_r;
    
    #===========================================================================
    # Mutates strategy params
    #===========================================================================
    def mutateStrategyParam(self):
        tau = 1.0 / numpy.sqrt(self.T); # 1 / SQRT(N) mit Suchraum R^N
        exp = random.gauss(0, tau);
        
        self._sigma = self._sigma * numpy.power(numpy.e, exp);        
    
    #===========================================================================
    # Mutates this individual's object parameter
    #===========================================================================
    def mutateObjectParam(self):        
      
        def y_(y):            
                        
            z = random.gauss(0,self._sigma);
            y_ = y + z;
            
            return clamp(y_, 0, 1); #shame on you, NUMPY; no NORMAL CLIP/CLAMP!!!!
      
        
        self._growth_bloom = [ y_(x) for x in self._growth_bloom]; 
    
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
    evolution_strategy = EvolutionStrategy.PLUS;   
    evolution_recomb_strategy = EvolutionRecombStrategy.DISCRETE;
     
    population_size = 20;
    rho = 1;
    descendants = 40;
    r = 0.1;
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
        
        result.evolution_strategy = data["evolution_strategy"];
        result.evolution_recomb_strategy = data["evolution_recomb_strategy"];
        
        result.population_size = data["population_size"];
        result.descendants = data["descendants"];
        result.r = data["r"];
        result.rho = data["rho"];
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
        
        data["evolution_strategy"] = self.evolution_strategy;
        data["evolution_recomb_strategy"] = self.evolution_recomb_strategy;
        
        data["population_size"] = self.population_size;
        data["rho"] = self.rho;
        data["descendants"] = self.descendants;
        data["r"] = self.r;
        data["L_zero"] = self.L_zero;
        data["R_zero"] = self.R_zero;
        data["T"] = self.T;
        data["generations"] = self.generations;
        
        pickle.dump(data, file);

        file.close();
        
   
    #===========================================================================
    # Plot the data, stored in this class
    #===========================================================================
    def plotdata(self, filename=None):                                          
                                                                                                                                                   
        
        plt.figure(figsize = (20,14), dpi = 50);
                                                                                                                                                   
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
        plt.xlabel("Generation ($n$)");                                                                                                            
        plt.ylabel("$R(T)$");           
        
        recomb_strat = ("Diskret" if self.evolution_recomb_strategy == EvolutionRecombStrategy.DISCRETE else "Intermediär") if self.rho > 1 else "Keine Rekombination";                                                                                                           
        
                                                                                                                                                   
        if self.evolution_strategy == EvolutionStrategy.PLUS:                                                                                                              
            plt.title("$R(T)$-Entwicklung über die Generation $({0}/{1}+{2})$ {4}  ({3} Durchläufe)"                        
                  .format(self.population_size,self.rho, self.descendants,self.TEST_RUNS,recomb_strat));                                                                                          
        else:                                                                                                                                      
            plt.title("$R(T)$-Entwicklung über die Generation $({0}/{1},{2})$ {4}  ({3} Durchläufe)"                         
                  .format(self.population_size,self.rho, self.descendants,self.TEST_RUNS,recomb_strat));                                                                                          
                                                                                                                                                   
                                                                                                                                                   
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
        
        if filename != None:
         
            
            plt.savefig(filename, dpi=400);
            print ("Plot saved to file.");
                                                                                                                                       
        plt.show();    
  
class EvolutionTask:   
    
    evolution_strategy = EvolutionStrategy.PLUS;
    evolution_recomb_strategy = EvolutionRecombStrategy.DISCRETE;
    
    population_size = 20;
    rho = 1;
    descendants = 40;
    r = 0.1;
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
        best_individuals = [];                                                                                                        
                                                                                                                                      
        #initialize start population                                                                                                  
        population = [Individual(self.L_zero, self.R_zero, self.T) for _ in range(self.population_size)];                                                                                 
                                                                                                                                      
        #grow population (evaluation)                                                                                                 
        for individual in population:                                                                                                 
            individual.grow(self.r,self.T);                                                                                                     
                                                                                                                                      
        for generation in range(self.generations):                                                                                         
                                                                                                                                      
            print("++++ Generation " + str(generation + 1))                                                                           
                                                                                                                                      
                                                                                                                                      
                                                                                                                                      
            #select best individuals of current population a parents                                                                   
            population.sort(key= lambda x: x.R(self.T), reverse=True);                                                                            
            best_individuals.append(population[0]); #add it to output list for later use        
            
            parents = population[0:self.rho];                                             
                                                                                                                                      
            #create descendants                                                                                                       
            next_generation = [];                                                                                                     
                                                                                                                                      
            for _ in range(self.descendants):
                
                #Erzeuge neues Kind = copy(parent) wenn nur ein einziger Elter verfügbar ist
                #Erzeuge mit RECOMB-Methoden das Kind über Rekombination, wenn es mehrere Eltern gibt
                individual = parents[0].copy() if len(parents) == 1 else Individual(self.L_zero, self.R_zero, self.T, [0 for _ in range(self.T)], -1);
                           
                              
                #Rekombination
                if(len(parents) > 1):
                    individual.recombStrategyParam(parents);
                    individual.recombObjectParam(parents, self.evolution_recomb_strategy);
                    
                #Mutation
                individual.mutateStrategyParam();
                individual.mutateObjectParam();
                
                next_generation.append(individual);
                                                                                                                                      
            #grow children (evaluate)                                                                                                 
            for individual in next_generation:                                                                                        
                individual.grow(self.r,self.T);                                                                                                 
                                                                                                                                      
                                                                                                                                      
            #Create a new population (using the set strategy)                                                                         
            if self.evolution_strategy == EvolutionStrategy.PLUS:                                                                                             
                #take best individuals of parents or children as new population (plus strategy)                                       
                population = next_generation + parents;                                                                              
            else:                                                                                                                     
                #take population_size best individuals as new population (comma strategy)                                             
                population = next_generation;                                                                                         
                                                                                                                                      
            #environment selection                                                                                                    
            population.sort(key= lambda x: x.R(self.T), reverse=True);                                                                     
            population = population[0:self.population_size];                                                                               
                                                                                                                                      
        #Finished! grow, sort and return                                                                                              
        print("******** Finished ********");                                               
                                                                                                                                      
        best_individuals.append(max(population, key = lambda x: x.R(self.T)));                                                             
                                                                                                                                      
        return best_individuals;                                                                                                      

    #===========================================================================
    # Run a test for this evolutionary algorithm
    #===========================================================================
    def evolutiontest(self, TEST_RUNS = 25):                                                                              
                           
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
       
        result.evolution_strategy = self.evolution_strategy;
        result.evolution_recomb_strategy = self.evolution_recomb_strategy;
        result.population_size = self.population_size;
        result.rho = self.rho;
        result.descendants = self.descendants;
        result.r = self.r;
        result.generations = self.generations;
        result.L_zero = self.L_zero;
        result.R_zero = self.R_zero;
        result.T = self.T;             
        
        result.TEST_DATA = TEST_DATA;
        result.TEST_DATA_leaf = TEST_DATA_leaf;
        result.TEST_DATA_u_r = TEST_DATA_u_r;
        result.TEST_RUNS = TEST_RUNS;
        
        return result;                             
        
                                                                                                                           


#===============================================================================
# Default run procedure
#===============================================================================
def run():
    test_plus = EvolutionTask();
    test_plus.evolution_strategy = EvolutionStrategy.PLUS;
    test_plus.evolution_recomb_strategy = EvolutionRecombStrategy.DISCRETE;
    test_plus.rho = 2;
    individual = test_plus.evolve();  
    
    individual.pop().printData();
    
    print([x._sigma for x in individual]);
    print([x.R(20) for x in individual]);


#===============================================================================
# Run a test task and saves it to a file
#===============================================================================
def run_test(task,test_runs, filename):
    result = task.evolutiontest(test_runs);
    result.write(filename);
    
    return result;
    
#===============================================================================
# Tests without recombination (RHO = 1); tests all evo. strategies
#===============================================================================
def run_tests():
    
    tests = [];   
    
    for strategy in [EvolutionStrategy.PLUS, EvolutionStrategy.COMMA]:
      
        case = EvolutionTask();
        case.name = "evolution_test_NORECOMB_" + str(strategy);      
        case.evolution_strategy = strategy;
            
        tests.append(case);
            
    #Run the tests
    results = [run_test(x,25,x.name) for x in tests];
    
    #Plot data
    for x in results: 
        x.plotdata();    

#===============================================================================
# Tests with recombination; tests all recombination strategies
#===============================================================================
def run_tests_recomb():
    
    tests = [];
    
    rho = 2;
    test_runs = 50;
    
    for strategy in [EvolutionStrategy.PLUS, EvolutionStrategy.COMMA]:
        for recomb_strategy in [EvolutionRecombStrategy.DISCRETE, EvolutionRecombStrategy.INTERMEDIATE]:
            
            case = EvolutionTask();   
            case.name = "evolution_test_" + str(strategy) + str(recomb_strategy);
            case.rho = rho;
            case.evolution_strategy = strategy;
            case.evolution_recomb_strategy = recomb_strategy;
            
            tests.append(case);
            
    #Run the tests
    results = [run_test(x,test_runs,x.name) for x in tests];
    
    #Plot data
    for x in results: 
        x.plotdata();
    
#===============================================================================
# Loads a plot from filename, load it and save it to a svg
#===============================================================================
def show_plot(filename):
    result = EvolutionTestResult.load(filename);
    result.plotdata(filename + ".svg");

    

#run_tests();
#run_tests_recomb();
run();
# show_plot("evolution_test_EvolutionStrategy.COMMAEvolutionRecombStrategy.INTERMEDIATE");
# show_plot("evolution_test_EvolutionStrategy.COMMAEvolutionRecombStrategy.DISCRETE");
# show_plot("evolution_test_EvolutionStrategy.PLUSEvolutionRecombStrategy.INTERMEDIATE");
# show_plot("evolution_test_EvolutionStrategy.PLUSEvolutionRecombStrategy.DISCRETE");
# show_plot("evolution_test_NORECOMB_EvolutionStrategy.COMMA");
# show_plot("evolution_test_NORECOMB_EvolutionStrategy.PLUS");