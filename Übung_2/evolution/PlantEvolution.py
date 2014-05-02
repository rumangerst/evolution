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
from symbol import parameters

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
    
    #Parameter of TAU, used for adaptive SIGMA; set 0 to disable adaptive sigma
    _sigma_tau_parameter = 0;
    
    #===========================================================================
    # Generate a random individual (if not provided) with age 0 and predefined bloom and leaf mass
    #=========================================================================== 
    def __init__(self, test_parameters, u_r = None):    
          
        self.test_parameters = test_parameters;
      
        self._mass_leaf = [float(test_parameters.L_zero)];
        self._mass_bloom = [float(test_parameters.R_zero)];  
        
        
        if u_r == None:
            print("Created new random individual!");
        
    
        self._growth_bloom = u_r if u_r != None else  [random.uniform(0,1) for _ in range(0,test_parameters.T)];
        self._sigma = test_parameters.sigma;
        self._sigma_tau_parameter = test_parameters.sigma_tau_parameter;
        
        
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
    def grow(self):        
     
        for t in range(self.test_parameters.T):            
            #Grow leaves, because days are discrete (integral <=> sum) + dt = 1 => sum up growth for each day
            self._mass_leaf.append(self.L(t) + self.test_parameters.r * self.growth_leaf(t) * self.L(t)); #self.mass_leaf IS L(t)
            self._mass_bloom.append(self.R(t) + self.test_parameters.r * self.growth_bloom(t) * self.L(t)); #^^^
            
        #After growth, L(T) is mass_leaf, R(T) is mass_bloom
        
        #print(str(self._mass_leaf));
        #print(str(self._mass_bloom));
        
        return self.R(self.test_parameters.T);
    
    #===========================================================================
    # Creates a new individual with all object- and strategy parameters of this object
    #===========================================================================
    def copy(self):
        indiv = Individual(self.test_parameters, self._growth_bloom);
        indiv._sigma = self._sigma;
        indiv._sigma_tau_parameter = self._sigma_tau_parameter;
        
        return indiv;
       
    #===========================================================================
    # Sets strategy parameters of individual to recombined strategy parameters of parents
    #===========================================================================    
    def recombStrategyParam(self, parents):
        # recombination by using mean (Intermediate; recommeded by Beyer)
        
        mean =  numpy.mean([x._sigma for x in parents]);
        self._sigma = mean;
        
    #===========================================================================
    # Sets object parameters of individual to recombined object parameters of parents    #===========================================================================
   
    def recombObjectParam(self, parents,strategy):
        
        if strategy == EvolutionRecombStrategy.INTERMEDIATE:
            
            #recombination using mean of each u_r(t) for each time
            mean_u_r = [numpy.mean([x.growth_bloom(t) for x in parents]) for t in range(self.test_parameters.T)];
            self._growth_bloom = mean_u_r;
            
        else:
            
            #recombination by baking together random explicit values of u_r
            baked_u_r = [random.choice(parents).growth_bloom(t) for t in range(self.test_parameters.T)];
            self._growth_bloom = baked_u_r;
    
    #===========================================================================
    # Mutates strategy params
    # Will use TAU adaptive strategy if _sigma_tau_parameter != 0
    # else will not mutate strategy parameter sigma
    #===========================================================================
    def mutateStrategyParam(self):
        
        if self._sigma_tau_parameter == 0: return;
        
        tau = 1.0 / numpy.sqrt(self._sigma_tau_parameter * self.test_parameters.T); # 1 / SQRT(N) mit Suchraum R^N
        exp = random.gauss(0, 1) * tau;
        
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
        
        
#===============================================================================
# Holds all data, used for a evolution test
#===============================================================================
class EvolutionTaskParameters:    
    
    sigma = 5;
    sigma_tau_parameter = 0;
 
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
    #===========================================================================
    # Load data from dictionary
    #===========================================================================
    def load(data):      
        result = EvolutionTaskParameters();
     
        result.evolution_strategy = data["evolution_strategy"];
        result.evolution_recomb_strategy = data["evolution_recomb_strategy"];
        
        result.sigma = data["sigma"];
        result.sigma_tau_parameter = data["sigma_tau_parameter"];
        
        result.population_size = data["population_size"];
        result.descendants = data["descendants"];
        result.r = data["r"];
        result.rho = data["rho"];
        result.L_zero = data["L_zero"];
        result.R_zero = data["R_zero"];
        result.T = data["T"];
        result.generations = data["generations"];
        
        return result;
    
    #===========================================================================
    # Write data to dictionary
    #===========================================================================
    def write(self):        
        
        data = {};        
      
        data["evolution_strategy"] = self.evolution_strategy;
        data["evolution_recomb_strategy"] = self.evolution_recomb_strategy;
        
        data["sigma"] = self.sigma;
        data["sigma_tau_parameter"] = self.sigma_tau_parameter;
        
        data["population_size"] = self.population_size;
        data["rho"] = self.rho;
        data["descendants"] = self.descendants;
        data["r"] = self.r;
        data["L_zero"] = self.L_zero;
        data["R_zero"] = self.R_zero;
        data["T"] = self.T;
        data["generations"] = self.generations;
        
        return data;
    
    def printdata(self):
        
        print("---* EVOLUTION PARAM *---");
        print("BEIGN >>>>");
        
        print("EVO-STRAT={0}, RECOMB-STRAT={1}".format(self.evolution_strategy, self.evolution_recomb_strategy));
        print("SIGMA={0}, SIGMA-TAU-PARAM={1}".format(self.sigma, self.sigma_tau_parameter));
        print("POP-SIZE={0}, RHO={1}, DESC={2}".format(self.population_size, self.rho, self.descendants));
        print("EGNERATIONS={0}".format(self.generations));
        print("L_ZERO={0}, R_ZERO={1}, T={2}, r={3}".format(self.L_zero, self.R_zero, self.T, self.r));
        
        print("<<<< DONE");

class EvolutionTestResult:
    
    #===========================================================================
    # All test parameters, used for this test
    #===========================================================================
    test_parameters = EvolutionTaskParameters();
    
    #===========================================================================
    # Results
    #===========================================================================
    TEST_RUNS = 25;
    TEST_DATA = [];                                                                                 
    TEST_DATA_leaf = [];                                                                            
    TEST_DATA_u_r = [];         
    
   
    
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
        
        result.test_parameters = EvolutionTaskParameters.load(data["test_parameters"]);
        
        return result;
    
    def write(self, filename):
        
        file = open(filename, "wb");
        
        data = {};
        
        data["TEST_RUNS"] = self.TEST_RUNS;
        data["TEST_DATA"] = self.TEST_DATA;
        data["TEST_DATA_leaf"] = self.TEST_DATA_leaf;
        data["TEST_DATA_u_r"] = self.TEST_DATA_u_r;
        
        data["test_parameters"] = self.test_parameters.write();
              
        pickle.dump(data, file);

        file.close();
        
   
    #===========================================================================
    # Plot the data, stored in this class
    #===========================================================================
    def plotdata(self, filename=None):      
        
        print("plotting:");
        self.test_parameters.printdata();                                            
                                                                                                                                                   
        
        plt.figure(figsize = (20,14), dpi = 50);
                                                                                                                                                   
        data_min = [min(x) for x in self.TEST_DATA];
        data_max = [max(x) for x in self.TEST_DATA];
        data_avg = [numpy.mean(x) for x in self.TEST_DATA];
        data_leaf_avg = [numpy.mean(x) for x in self.TEST_DATA_leaf];
                                                                                                                                                   
        x_axis = range(self.test_parameters.generations + 1);                                                                                                           
                                                                                                                                                   
        ############### Create main plot                                                                                                           
        fig_fitness = plt.subplot(2,1,1);                                                                                                          
                                                                                                                                                   
        plt.plot(x_axis, data_min, color='black', linestyle='-', label='Minimum R(T)');                                                            
        plt.plot(x_axis, data_avg, color='red', linestyle='-', label='Average R(T)');                                                              
        plt.plot(x_axis, data_max, color='black', linestyle='-', label='Maximum R(T)');                                                            
                                                                                                                                                   
        plt.plot(x_axis, data_leaf_avg, color='green', linestyle=':', label='Average L(T)');                                                       
                                                                                                                                                   
        #plt.axis([0,generation])                                                                                                                  
        plt.xlabel("Generation ($n$)");                                                                                                            
        plt.ylabel("$R(T)$");           
        
        recomb_strat = ("Diskret" if self.test_parameters.evolution_recomb_strategy == EvolutionRecombStrategy.DISCRETE else "Intermediär") if self.test_parameters.rho > 1 else "Keine Rekombination";                                                                                                           
        
                                                                                                                                                   
        if self.test_parameters.evolution_strategy == EvolutionStrategy.PLUS:                                                                                                              
            plt.title("$R(T)$-Entwicklung über die Generation $({0}/{1}+{2})$ {4}  ({3} Durchläufe)"                        
                  .format(self.test_parameters.population_size,self.test_parameters.rho, self.test_parameters.descendants,self.TEST_RUNS,recomb_strat));                                                                                          
        else:                                                                                                                                      
            plt.title("$R(T)$-Entwicklung über die Generation $({0}/{1},{2})$ {4}  ({3} Durchläufe)"                         
                  .format(self.test_parameters.population_size,self.test_parameters.rho, self.test_parameters.descendants,self.TEST_RUNS,recomb_strat));                                                                                          
                                                                                                                                                   
                                                                                                                                                   
        #draw error bars                                                                                                                           
        data_error = [numpy.std(x) / numpy.sqrt(len(x)) for x in self.TEST_DATA];                                                                       
        plt.errorbar(x_axis, data_avg, yerr= data_error);                                                                                          
                                                                                                                                                   
        #draw legend                                                                                                                               
        fig_fitness.legend(loc = "lower right");                                                                                                   
                                                                                                                                                   
        #===========================================================================                                                               
        # Create u_r plot                                                                                                                          
        #===========================================================================                                                               
        x_axis = range(1, self.test_parameters.T + 1);                                                                                                                  
                                                                                                                                                   
        fig_u_r = plt.subplot(2,1,2);                                                                                                              
                                                                                                                                                   
        plt.xlabel("Tage ($t$)");                                                                                                                  
        plt.ylabel("$u_r$");                                                                                                                       
        plt.title("Entwicklung der $u_r$ über die Tage");                                                                                          
                                                                                                                                                   
        plt.axis([1,20,0,1]);                                                                                                                      
                                                                                                                                                   
        for generation in range(self.test_parameters.generations + 1):                                                                                                  
                                                                                                                                                   
            _u_r_source_data = self.TEST_DATA_u_r[generation]; #get array of u_r(t) for all 25 runs                                                     
            data_u_r_avg = [numpy.mean([ x[day] for x in _u_r_source_data ]) for day in range(self.test_parameters.T)];                                                 
                                                                                                                                                   
            ### Python ist cool!!!!                                                                                                                
            _color, _linestyle, _label = ["red", "-", "Average $u_r$"] if generation == self.test_parameters.generations else ["gray", ":", ""];                        
                                                                                                                                                   
            plt.plot(x_axis,data_u_r_avg, color=_color, linestyle=_linestyle, label = _label);                                                     
                                                                                                                                                   
        #print latest avg u_r                                                                                                                      
        print("Latest AVG u_r:")                                                                                                                   
        print(str([numpy.mean([ x[day] for x in self.TEST_DATA_u_r[self.test_parameters.generations] ]) for day in range(self.test_parameters.T)]));                                              
                                                                                                                                                   
        #draw legend                                                                                                                               
        fig_u_r.legend(loc = "lower right");                                                                                                       
                                                                                                                                                   
        #show       
        
        if filename != None:
            
            plt.savefig(filename + ".svg");
            print ("Plot saved to file.");
        else:                                                                                                                              
            plt.show();    
        

    
  
class EvolutionTask:   
    
    test_parameters = EvolutionTaskParameters();
    
    def __init__(self):        
        print("Created a new evolution task!");
        
        
    #===========================================================================
    # Evolves using sphere model
    # Tests R^N with N = T
    #===========================================================================
    def spheremodel(self):
        #initialize start population                                                                                                  
        population = [ Individual(self.test_parameters, [ 1 for _ in range(self.test_parameters.T) ]) for _ in range(self.test_parameters.population_size)];
         
        
        #=======================================================================
        # Calculates fitness SUM (x*x) for each u_r
        #=======================================================================
        def fitness(individual):
            
            return [x*x for x in individual._growth_bloom];
        
        #calc fitness for start population
        for x in population:
            x.fitness = fitness(x);
        
        # holds best fitness values over generation
        fitness_values = [];
        sigma_values = [];
        
        for generation in range(self.test_parameters.generations):                                                                                         
                                                                                                                                      
            if generation % 50 == 0: print("++++ Sphere Generation " + str(generation + 1));
                                                                             
            #select best individuals of current population a parents                                                                   
            population.sort(key= lambda x: x.fitness, reverse=False);  #note: minimize fitness
                                                                                      
            fitness_values.append(population[0].fitness); #add it to output list for later use 
            sigma_values.append(population[0]._sigma);       
            
            parents = population[0:self.test_parameters.rho];                                             
                                                                                                                                      
            #create descendants                                                                                                       
            next_generation = [];                                                                                                     
                                                                                                                                      
            for _ in range(self.test_parameters.descendants):
                
                #Erzeuge neues Kind = copy(parent) wenn nur ein einziger Elter verfügbar ist
                #Erzeuge mit RECOMB-Methoden das Kind über Rekombination, wenn es mehrere Eltern gibt
                individual = parents[0].copy() if len(parents) == 1 else Individual(self.test_parameters, [0 for _ in range(self.test_parameters.T)]);
                           
                              
                #Rekombination
                if(len(parents) > 1):
                    individual.recombStrategyParam(parents);
                    individual.recombObjectParam(parents, self.test_parameters.evolution_recomb_strategy);
                    
                #Mutation
                individual.mutateStrategyParam();
                individual.mutateObjectParam();
                
                next_generation.append(individual);
                                                                                                                                      
            #evaluate children (fitness)                                                                                  
            for individual in next_generation:                                                                                        
                individual.fitness = fitness(individual);                                                                                     
                                                                                                                                      
                                                                                                                                      
            #Create a new population (using the set strategy)                                                                         
            if self.test_parameters.evolution_strategy == EvolutionStrategy.PLUS:                                                                                             
                #take best individuals of parents or children as new population (plus strategy)                                       
                population = next_generation + parents;                                                                              
            else:                                                                                                                     
                #take population_size best individuals as new population (comma strategy)                                             
                population = next_generation;                                                                                         
                                                                                                                                      
            #environment selection                                                                                                    
            population.sort(key= lambda x: x.fitness, reverse=False);                                                                     
            population = population[0:self.test_parameters.population_size];                                                                               
                                                                                                                                      
        #Finished! grow, sort and return                                                                                              
        print("******** Finished Sphere Test ********");                                               
                             
        bestindividual = min(population, key = lambda x: x.fitness);
                                                                                                                                      
        fitness_values.append(bestindividual.fitness);
        sigma_values.append(bestindividual._sigma);     
        
        
        print("Best u_r: " + str(bestindividual._growth_bloom));
        
        
        return { "fitness": fitness_values, "sigma": sigma_values, "bestdata": bestindividual._growth_bloom };
        

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
        population = [Individual(self.test_parameters) for _ in range(self.test_parameters.population_size)];                                                                                 
                                                                                                                                      
        #grow population (evaluation)                                                                                                 
        for individual in population:                                                                                                 
            individual.grow();                                                                                                     
                                                                                                                                      
        for generation in range(self.test_parameters.generations):                                                                                         
                                                                                                                                      
            if generation % 50 == 0: print("++++ Generation " + str(generation + 1));                                                                           
                                                                                                                                      
                                                                                                                                      
                                                                                                                                      
            #select best individuals of current population a parents                                                                   
            population.sort(key= lambda x: x.R(self.test_parameters.T), reverse=True);                                                                            
            best_individuals.append(population[0]); #add it to output list for later use        
            
            parents = population[0:self.test_parameters.rho];                                             
                                                                                                                                      
            #create descendants                                                                                                       
            next_generation = [];                                                                                                     
                                                                                                                                      
            for _ in range(self.test_parameters.descendants):
                
                #Erzeuge neues Kind = copy(parent) wenn nur ein einziger Elter verfügbar ist
                #Erzeuge mit RECOMB-Methoden das Kind über Rekombination, wenn es mehrere Eltern gibt
                individual = parents[0].copy() if len(parents) == 1 else Individual(self.test_parameters, [0 for _ in range(self.test_parameters.T)]);
                           
                              
                #Rekombination
                if(len(parents) > 1):
                    individual.recombStrategyParam(parents);
                    individual.recombObjectParam(parents, self.test_parameters.evolution_recomb_strategy);
                    
                #Mutation
                individual.mutateStrategyParam();
                individual.mutateObjectParam();
                
                next_generation.append(individual);
                                                                                                                                      
            #grow children (evaluate)                                                                                                 
            for individual in next_generation:                                                                                        
                individual.grow();                                                                                                 
                                                                                                                                      
                                                                                                                                      
            #Create a new population (using the set strategy)                                                                         
            if self.test_parameters.evolution_strategy == EvolutionStrategy.PLUS:                                                                                             
                #take best individuals of parents or children as new population (plus strategy)                                       
                population = next_generation + parents;                                                                              
            else:                                                                                                                     
                #take population_size best individuals as new population (comma strategy)                                             
                population = next_generation;                                                                                         
                                                                                                                                      
            #environment selection                                                                                                    
            population.sort(key= lambda x: x.R(self.test_parameters.T), reverse=True);                                                                     
            population = population[0:self.test_parameters.population_size];                                                                               
                                                                                                                                      
        #Finished! grow, sort and return                                                                                              
        print("******** Finished ********");                                               
                                                                                                                                      
        best_individuals.append(max(population, key = lambda x: x.R(self.test_parameters.T)));                                                             
                                                                                                                                      
        return best_individuals;                                                                                                      

    #===========================================================================
    # Run a test for this evolutionary algorithm
    #===========================================================================
    def evolutiontest(self, TEST_RUNS = 25):      
        
        print("Starting evolution test");
        self.test_parameters.printdata();                                                                        
                           
        TEST_DATA = [[] for _ in range(self.test_parameters.generations + 1)];                                                                                 
        TEST_DATA_leaf = [[] for _ in range(self.test_parameters.generations + 1)];                                                                            
        TEST_DATA_u_r = [[] for _ in range(self.test_parameters.generations + 1)];                                                                             
                                                                                                                                          
        for run in range(TEST_RUNS):                                                                                                      
            print("------------- * Test Run " + str(run));                                                                                
                                                                                                                                          
            best_individuals = self.evolve();            
                                                                                                                                          
            for generation in range(self.test_parameters.generations + 1):                                                                                     
                TEST_DATA[generation].append( best_individuals[generation].R(self.test_parameters.T) );                                                        
                TEST_DATA_leaf[generation].append( best_individuals[generation].L(self.test_parameters.T) );                                                   
                TEST_DATA_u_r[generation].append( best_individuals[generation]._growth_bloom ); 
                
                
        #generate a result
        result = EvolutionTestResult();        
       
        result.test_parameters = self.test_parameters;     
        
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
    test_plus.test_parameters.evolution_strategy = EvolutionStrategy.PLUS;
    test_plus.test_parameters.evolution_recomb_strategy = EvolutionRecombStrategy.DISCRETE;
    test_plus.test_parameters.rho = 2;
    individual = test_plus.evolve();  
    
    individual.pop().printData();
    
    print([x._sigma for x in individual]);
    print([x.R(20) for x in individual]);


#===============================================================================
# Compares two strategies by running a large test + sphere model test
#===============================================================================
def run_tests_ubung_4():
    
    #===============================================================================
    # Run a test task and saves it to a file
    #===============================================================================
    def run_test(task,test_runs, filename):
        result = task.evolutiontest(test_runs);
        result.write(filename);
    
        return result;  
    
    
    #===========================================================================
    # Define test cases
    #===========================================================================
    
    # Case: old rho = 1, sigma = 5, comma strategy
    case_unopt = EvolutionTask();
    case_unopt.name = "evolution_test_x2_UNOPT";   
    case_unopt.test_parameters = EvolutionTaskParameters();   
    case_unopt.test_parameters.evolution_strategy = EvolutionStrategy.COMMA;
    case_unopt.test_parameters.generations = 5000;        
    case_unopt.test_parameters.test_runs = 25;
    case_unopt.test_parameters.rho = 1;
    case_unopt.test_parameters.sigma = 5;
    case_unopt.test_parameters.sigma_tau_parameter = 1;
    
    #Case: optimized values: sigma=1, sigma_tau_param=2.3, rho=2, plus strategy
    case_opt = EvolutionTask();
    case_opt.test_parameters = EvolutionTaskParameters();   
    case_opt.name = "evolution_test_x2_--OPT--";      
    case_opt.test_parameters.evolution_strategy = EvolutionStrategy.PLUS;
    case_opt.test_parameters.evolution_recomb_strategy = EvolutionRecombStrategy.DISCRETE;
    case_opt.test_parameters.generations = 5000;        
    case_opt.test_parameters.test_runs = 25;
    case_opt.test_parameters.rho = 2;
    case_opt.test_parameters.sigma = 1;
    case_opt.test_parameters.sigma_tau_parameter = 2.3;
    
    #===========================================================================
    # Run the test
    #===========================================================================
    
    result_unopt = run_test(case_unopt, 25, case_unopt.name);
    result_opt = run_test(case_opt, 25, case_opt.name);
    
    result_unopt.plotdata(case_unopt.name);
    result_opt.plotdata(case_opt.name);
    
    run_tests_sphere(case_unopt.name + "SPHERE", case_unopt.test_parameters);
    run_tests_sphere(case_opt.name + "SPHERE", case_opt.test_parameters);
   
#     for strategy in [EvolutionStrategy.PLUS, EvolutionStrategy.COMMA]:
#       
#         case_unopt = EvolutionTask();
#         case_unopt.name = "evolution_test_NORECOMB_" + str(strategy);      
#         case_unopt.test_parameters.evolution_strategy = strategy;
#         case_unopt.test_parameters.generations = 200;        
#         case_unopt.test_parameters.test_runs = 25;
#         case_unopt.test_parameters.rho = 1;
#         case_unopt.test_parameters.sigma = 5;
#         case_unopt.test_parameters.sigma_tau_parameter = 1;
#             
#         result = run_test(case_unopt, 25, case_unopt.name);
#         result.plotdata(case_unopt.name);       
#             
#         #run spheremodel test
#         case_unopt.test_parameters.generations = 1000;        
#         run_tests_sphere(case_unopt.name + "SPHERE", case_unopt.test_parameters);

    
#===============================================================================
# Tests without recombination (RHO = 1); tests all evo. strategies and starts a spheremodel test
#===============================================================================
def run_tests():
    
    #===============================================================================
    # Run a test task and saves it to a file
    #===============================================================================
    def run_test(task,test_runs, filename):
        result = task.evolutiontest(test_runs);
        result.write(filename);
    
        return result;    
   
    for strategy in [EvolutionStrategy.PLUS, EvolutionStrategy.COMMA]:
      
        case = EvolutionTask();
        case.name = "evolution_test_NORECOMB_" + str(strategy);      
        case.test_parameters.evolution_strategy = strategy;
        case.test_parameters.generations = 200;        
        case.test_parameters.test_runs = 25;
        case.test_parameters.rho = 1;
        case.test_parameters.sigma = 5;
        case.test_parameters.sigma_tau_parameter = 1;
            
        result = run_test(case, 25, case.name);
        result.plotdata(case.name);       
            
        #run spheremodel test
        case.test_parameters.generations = 1000;        
        run_tests_sphere(case.name + "SPHERE", case.test_parameters);

#===============================================================================
# Tests with recombination; tests all recombination strategies
#===============================================================================
def run_tests_recomb():
    
    #===============================================================================
    # Run a test task and saves it to a file
    #===============================================================================
    def run_test(task,test_runs, filename):
        result = task.evolutiontest(test_runs);
        result.write(filename);
    
        return result;
    
    tests = [];
    
    rho = 2;
    test_runs = 50;
    
    for strategy in [EvolutionStrategy.PLUS, EvolutionStrategy.COMMA]:
        for recomb_strategy in [EvolutionRecombStrategy.DISCRETE, EvolutionRecombStrategy.INTERMEDIATE]:
            
            case = EvolutionTask();   
            case.name = "evolution_test_" + str(strategy) + str(recomb_strategy);
            
            case.test_parameters.rho = rho;
            case.test_parameters.evolution_strategy = strategy;
            case.test_parameters.evolution_recomb_strategy = recomb_strategy;
            
            tests.append(case);
            
    #Run the tests
    results = [run_test(x,test_runs,x.name) for x in tests];
    
    #Plot data
    print("Plotting data ...");
    for x in results: 
        x.plotdata();
        
#===============================================================================
# Test sphere model
#===============================================================================
def run_tests_sphere(name,testparameters):
    
    test_return_values = [];
        
    for _ in range(testparameters.test_runs):
        
        case = EvolutionTask();   
        case.name = "evolution_test_sphere";
        case.test_parameters = testparameters;
        
            
        test_return_values.append(case.spheremodel());
            
      
    #Calculate means
    test_fitness_values_means = [ numpy.mean( [ x["fitness"][generation] for x in test_return_values ] ) for generation in range(testparameters.generations) ];
    test_sigma_values_means = [ numpy.mean( [ x["sigma"][generation] for x in test_return_values ] ) for generation in range(testparameters.generations) ];
    
    test_u_r_mean = [ numpy.mean( [ x["bestdata"][generation] for x in test_return_values ] ) for generation in range(testparameters.T) ];
    
    print("Average fitness in last generation: {0}".format(test_fitness_values_means[-1]));
    print("Average sigma in last generation: {0}".format(test_sigma_values_means[-1]));
    print("Average u_r(t) in last generation: {0}".format(test_u_r_mean));
    
       
    #Plot the data
    def plot(parameters, data_fitness, data_sigma):
        
        plt.figure(figsize = (20,14), dpi = 50);                                                                                                                                                   
                                                                     
        x_axis = range(parameters.generations);                                                                                                           
                                                                                                                                                   
        ############### Create main plot                                                                                                           
        fig_fitness = plt.subplot(1,1,1);
        fig_fitness.set_yscale("log");                                                                                                          
                                                                                                                                                   
        plt.plot(x_axis, data_fitness, color='black', linestyle='-', label='Average Fitness');    
        #plt.plot(x_axis, data_sigma, color='red', linestyle='-', label='Average $\sigma$');                                                          
                                                
        #plt.axis([0,generation])                                                                                                                  
        plt.xlabel("Generation ($n$)");                                                                                                            
        plt.ylabel("$Fitness$");           
        
        plt.title("Kugelmodell {0} Generationen, $\sigma={1}$, {2}; {3}".format(
                                                                                                      parameters.generations,
                                                                                                      parameters.sigma,                                                                                                     
                                                                                                      ("$({0}/{1}+{2})$" if parameters.evolution_strategy == EvolutionStrategy.PLUS else "$({0}/{1},{2})$").format(parameters.population_size, parameters.rho, parameters.descendants),
                                                                                                      "Keine Anpassung" if parameters.sigma_tau_parameter == 0 else "log-normal $\\frac{{1}}{{\sqrt{{{0}\cdot N}}}}$".format(parameters.sigma_tau_parameter) ));                                                                                                                                                  
                                                                                                                                                   
                                                                                      
        #draw legend                                                                                                                               
        fig_fitness.legend(loc = "lower right");
        
        #plt.show();
        plt.savefig("sphere_model_plot_" + name + ".svg");
        
        
    plot(testparameters, test_fitness_values_means, test_sigma_values_means);
    return {"fitness_value_mean" : test_fitness_values_means, "sigma_mean": test_sigma_values_means, "bestfitness_mean": test_u_r_mean};

    
#===============================================================================
# Automatic function to test sphere model
#===============================================================================
def test_spheremodels():
    
    bestparams = None;
    bestfitness = float("Infinity");
    
    #prior tests
    #define tests
#     test_sigma = [1,2,5];    
#     test_sigma_tau = [0,1,1.5,2,3];
#     test_rho = [1,2,3,4,5];
#     test_strat = [EvolutionStrategy.COMMA, EvolutionStrategy.PLUS];
#     test_strat_recomb = [EvolutionRecombStrategy.DISCRETE, EvolutionRecombStrategy.INTERMEDIATE];
    
    #filter tests (2nd step)
    test_sigma = [1];    #testing only with sig=1
    test_sigma_tau = [x for x in numpy.arange(0,5.1,0.1)];
    test_rho = [2,3]; #rho=2,3 were superior
    test_strat = [ EvolutionStrategy.PLUS]; #PLUS strategy was superior
    test_strat_recomb = [EvolutionRecombStrategy.DISCRETE, EvolutionRecombStrategy.INTERMEDIATE];
    
    for sigma in test_sigma:
        for sigma_tau in test_sigma_tau:
            for rho in test_rho:
                for rc_strat in test_strat_recomb:
                    for strat in test_strat:
                        param = EvolutionTaskParameters();
                        param.generations = 200;           
                        param.test_runs=25;            
                        param.sigma = sigma;
                        param.sigma_tau_parameter = sigma_tau;
                        param.rho = rho;
                        param.evolution_strategy = strat;
                        param.evolution_recomb_strategy = rc_strat;
                        
                        name = "SIG{0}TAU{1}__RHO{2}__EVO-{3}__RECOMB-{4}x{5}".format(sigma,sigma_tau,rho,strat,rc_strat,300);
                        
                        #ok!
                        result = run_tests_sphere(name, param);
                        indiv = result["bestfitness_mean"][-1];
                        
                        if bestfitness > indiv:                            
                            
                            bestparams = param;
                            bestfitness = indiv;
                            
                        if rho == 1: #cancel recomb strat testing if rho is 1 (recomb strat is ignored)
                            break;
                        
                        
    
    #run tests
    #always select best param
    
    #print best param
    print("--- DONE: Best fitness was:");
    bestparams.printdata();    

    
#===============================================================================
# Loads a plot from filename, load it and save it to a svg
#===============================================================================
def show_plot(filename):
    result = EvolutionTestResult.load(filename);
    result.plotdata(filename + ".svg");

    

run_tests_ubung_4();
#test_spheremodels();
#run_tests_sphere();
#run_tests();
#run_tests_recomb();
#run();
# show_plot("evolution_test_00");
# show_plot("evolution_test_01");
# show_plot("evolution_test_10");
# show_plot("evolution_test_11");
# show_plot("evolution_test_NORECOMB_0");
# show_plot("evolution_test_NORECOMB_1");