'''

    Tests für genetische Algorithmen

'''

import numpy;
import math;
import random;
import matplotlib.pyplot as plt;

#===============================================================================
# Zahl zu Bitstring kodieren (Standard)
#===============================================================================
def encode(number):   
   
    return numpy.binary_repr(number);

#===============================================================================
# Bitstring in Zahl dekodieren
#===============================================================================
def decode(bitstring):    
  
    #convert default binary to 10
    return sum([ int(int(bitstring[-i-1]) * math.pow(2, i)) for i in range(len(bitstring)) ]);

#===============================================================================
# Bitstring in Zahl dekodieren (Gray)
#===============================================================================
def decode_gray(bitstring):
    
    code = list(bitstring);
    result = list("0"*len(bitstring));
    
    #Convert to default binary
    
    #first letter handling
    result[0] = code[0]; # append first value from code - if code[0] == 1, 1 XOR 0 = 1
    
    for i in range(1, len(bitstring)):
        
        result[i] = "1" if code[i] != result[i-1] else "0";
        
        
    #print("bin:" + "".join(result));
    
    #convert default binary to 10
    return sum([ int(int(result[-i-1]) * math.pow(2, i)) for i in range(len(result)) ]);
    
#===============================================================================
# Zahl zu Bitstring kodieren (Gray)
#===============================================================================
def encode_gray(number):
    
    s1 = numpy.binary_repr(number);
    s2 = "0" + numpy.binary_repr(number)[:-1];
    
    
    return "".join(["1" if s1[i] != s2[i] else "0" for i in range(len(s1))]);

#===============================================================================
# Normiert Bitstring auf eine Länge
#===============================================================================
def normalize(bitstring, length):
    
    return "0"*(length - len(bitstring)) + bitstring;

#===============================================================================
# Mutiert Bitstring mit entsprechenden Pm
#===============================================================================
def mutate(bitstring, pm):
    
    s = list(bitstring);
    
    for i in range(len(s)):
        
        u = random.random(); #returns umber in [0,1) |=| [0,1[
        
        if u<= pm:
            s[i] = "1" if s[i] == "0" else "0"; #invert bit
            
    return "".join(s);
    

def compareencodings():
    
    def diff_genotype(s1, s2):
        return sum(1 if s1[i] != s2[i] else 0 for i in range(len(s1)));
    
    def diff_phentotype(n1, n2):
        return numpy.abs(n1 - n2);
    
    def diff_phentotype_per_genotype(s1, s2, n1, n2):
        
        geno = diff_genotype(s1, s2);
        pheno = diff_phentotype(n1, n2);
        
        if geno == 0 or pheno == 0:
            return 0;
        
        return float(pheno) / geno;
    
    print("Comparing mutation default and gray");
    P = 500;
    pm = 0.1;
    generations = 100;
    runs = 25; 
    
    genotypes_default = [[encode(P) for _ in range(runs)]];
    genotypes_gray = [[encode_gray(P) for _ in range(runs)]];
    phenotypes_default = [[P for _ in range(runs)]];
    phenotypes_gray = [[P for _ in range(runs)]];
    
    #Mutate
    for gen in range(generations):
        
        #Mutate genomes
        
        genotypes_default.append([mutate(genotypes_default[gen][run], pm) for run in range(runs)]);
        genotypes_gray.append([mutate(genotypes_gray[gen][run], pm) for run in range(runs)]);
        
        phenotypes_default.append([decode(genotypes_default[gen+1][run]) for run in range(runs)]);
        phenotypes_gray.append([decode_gray(genotypes_gray[gen+1][run]) for run in range(runs)]);
        
        if gen % 10 == 0:
            print("Generation " + str(gen));
            
    # Evaluate data
    # will calculate average PHENOTYPE-CHANGES / GENOTYPE-CHANGES
    
    changes_default = [ [ diff_phentotype_per_genotype(genotypes_default[generation][run], genotypes_default[generation - 1][run], phenotypes_default[generation][run], phenotypes_default[generation - 1][run])  for run in range(runs)]  for generation in range(1, generations) ];
    changes_gray = [ [ diff_phentotype_per_genotype(genotypes_gray[generation][run], genotypes_gray[generation - 1][run], phenotypes_gray[generation][run], phenotypes_gray[generation - 1][run])  for run in range(runs)]  for generation in range(1, generations) ];
    
    #Plot data
    def plot(generations, runs, pm, P, changes_default, changes_gray):
        plt.figure(figsize = (20,14), dpi = 50);                                                                                                                                                   
                                                                     
        x_axis = range(1, generations);       
        
        default_min = [min(x) for x in changes_default]; 
        gray_min = [min(x) for x in changes_gray];   
        default_max = [min(x) for x in changes_default]; 
        gray_max = [min(x) for x in changes_gray];   
        default_avg = [min(x) for x in changes_default]; 
        gray_avg = [min(x) for x in changes_gray];   
        
        default_error = [numpy.std(x) / numpy.sqrt(len(x)) for x in changes_default];
        gray_error = [numpy.std(x) / numpy.sqrt(len(x)) for x in changes_gray];
        
        #set title
        plt.title("Vergleich zwischen Gray-Code und Binärcode mit $P={0}$, $p_m={1}$, {2} Durchläufe".format(P, pm, runs));
        
        #Set axes
        plt.xlabel("Generation (t)");
        plt.ylabel("$\\frac{\\Delta phenotype(t,t-1)}{\\Delta genotype(t,t-1)}$");
        
        #Plot default data
        plt.plot(x_axis, default_min, color="#f24a4a", linestyle=":", label="Minimum value for binaray");
        plt.plot(x_axis, default_avg, color="red", linestyle="-", linewidth="2.0", label="Average value for binaray");
        plt.plot(x_axis, default_max, color="#f24a4a", linestyle=":", label="Maximum value for binaray");
        plt.errorbar(x_axis, default_avg, yerr = default_error);
        
        #Plot gray data
        plt.plot(x_axis, gray_min, color="#61c14a", linestyle=":", label="Minimum value for gray code");
        plt.plot(x_axis, gray_avg, color="green", linestyle="-", linewidth="2.0", label="Average value for gray code");
        plt.plot(x_axis, gray_max, color="#61c14a", linestyle=":", label="Maximum value for gray code");
        plt.errorbar(x_axis, gray_avg, yerr = gray_error);
        
        plt.show();
        
    plot(generations, runs, pm, P, changes_default, changes_gray);

compareencodings();

        
    