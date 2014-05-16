'''

    Tests für genetische Algorithmen
    Alle Ergebnisse und Code im Repository git clone https://github.com/rumangerst/evolution.git

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
# Mutiert Bitstring mit entsprechenden Pm und gegebenen u
#===============================================================================
def mutate(bitstring, pm, us):
    
    s = list(bitstring);
        
    for i in range(len(s)):
        
        u = us[i];        
        
        if u<= pm:
            s[i] = "1" if s[i] == "0" else "0"; #invert bit
            
    return "".join(s);
    

def compareencodings(pm):
    
    #===========================================================================
    # Hamming-Distanz zwischen den Bitstrings
    #===========================================================================
    def diff_genotype(s1, s2):
        return sum(1 if s1[i] != s2[i] else 0 for i in range(len(s1)));
    
    #===========================================================================
    # Normaler abstand
    #===========================================================================
    def diff_phentotype(n1, n2):
        return numpy.abs(n1 - n2);
    
    #===========================================================================
    # Berechnet diff(phenotype)/diff(genotype)
    #===========================================================================
    def diff_phentotype_per_genotype(s1, s2, n1, n2):
        
        geno = diff_genotype(s1, s2);
        pheno = diff_phentotype(n1, n2);
        
        print("Genotype: " + str(geno) + ", Phenotype: " + str(pheno));
        
        if geno == 0:
            return 0;
        
        return float(pheno) / float(geno);
    
    print("Comparing mutation default and gray");
    Width = len(encode(10000)); #normalized string width, binary = gray
      
    
    P = 0;   
    generations = 100;
    runs = 50; 
    
    genotypes_default = [[normalize(encode(P), Width) for _ in range(runs)]]; #important: normalize encoded string here to set search space
    genotypes_gray = [[normalize(encode_gray(P), Width) for _ in range(runs)]];
   
    phenotypes_default = [[P for _ in range(runs)]];
    phenotypes_gray = [[P for _ in range(runs)]];
    
    #Mutate
    for gen in range(generations):
                   
        #Mutate genomes
        genotypes_default.append([]);
        genotypes_gray.append([]);
        
        for run in range(runs):
            
            #Generiere u's
            us = [random.random() for _ in range(Width)];
            
            #übergebe u's an Mutationsfunktion, um die gleichen Bits zu mutieren!
            genotypes_default[gen+1].append(mutate(genotypes_default[gen][run], pm, us));
            genotypes_gray[gen+1].append(mutate(genotypes_gray[gen][run], pm, us));
            
             
        phenotypes_default.append([decode(genotypes_default[gen+1][run]) for run in range(runs)]);
        phenotypes_gray.append([decode_gray(genotypes_gray[gen+1][run]) for run in range(runs)]);
        
        if gen % 10 == 0:
            print("Generation " + str(gen));
            
    # Evaluate data
    # will calculate average PHENOTYPE-CHANGES / GENOTYPE-CHANGES
    print(str(genotypes_gray[10]));
    print(str(genotypes_default[10]));
    
    changes_default = [ [ diff_phentotype_per_genotype(genotypes_default[generation][run], genotypes_default[generation - 1][run], phenotypes_default[generation][run], phenotypes_default[generation - 1][run])  for run in range(runs)]  for generation in range(1, generations) ];
    changes_gray = [ [ diff_phentotype_per_genotype(genotypes_gray[generation][run], genotypes_gray[generation - 1][run], phenotypes_gray[generation][run], phenotypes_gray[generation - 1][run])  for run in range(runs)]  for generation in range(1, generations) ];
    
    #Plot data
    def plot(generations, runs, pm, P, changes_default, changes_gray):
        plt.figure(figsize = (15,10), dpi = 70);         
                                                                                                                                                 
                                                                     
        x_axis = range(1, generations);       
        
        default_min = [min(x) for x in changes_default]; 
        gray_min = [min(x) for x in changes_gray];   
        default_max = [max(x) for x in changes_default]; 
        gray_max = [max(x) for x in changes_gray];   
        default_avg = [numpy.mean(x) for x in changes_default]; 
        gray_avg = [numpy.mean(x) for x in changes_gray];   
        
        default_error = [numpy.std(x) / numpy.sqrt(len(x)) for x in changes_default];
        gray_error = [numpy.std(x) / numpy.sqrt(len(x)) for x in changes_gray];
        
        #set title
        plt.title("Vergleich zwischen Gray-Code und Binärcode mit $P={0}$, $p_m={1}$, {2} Durchläufe".format(P, pm, runs));
        
        #Set axes
        plt.xlabel("Generation (t)");
        plt.ylabel("$\\frac{\\Delta phenotype(t,t-1)}{\\Delta genotype(t,t-1)}$");
        
        #Plot default data
        plt.plot(x_axis, default_min, color="#f24a4a", linestyle=":", label="Minimum value for binary");
        plt.plot(x_axis, default_avg, color="red", linestyle="-", linewidth="2.0", label="Average value for binary");
        plt.plot(x_axis, default_max, color="#f24a4a", linestyle=":", label="Maximum value for binary");
        plt.errorbar(x_axis, default_avg, yerr = default_error);
        
        #Plot gray data
        plt.plot(x_axis, gray_min, color="#61c14a", linestyle=":", label="Minimum value for gray code");
        plt.plot(x_axis, gray_avg, color="green", linestyle="-", linewidth="2.0", label="Average value for gray code");
        plt.plot(x_axis, gray_max, color="#61c14a", linestyle=":", label="Maximum value for gray code");
        plt.errorbar(x_axis, gray_avg, yerr = gray_error);
        
        plt.subplot(1,1,1).legend(loc = "upper right") 
        #plt.show();
        plt.savefig("result_pm_{0}.svg".format(pm));
        
    plot(generations, runs, pm, P, changes_default, changes_gray);

for pm in numpy.arange(0.1,1,0.1):
    compareencodings(numpy.round(pm, decimals=2));
for pm in numpy.arange(0,0.1,0.01):
    compareencodings(numpy.round(pm, decimals=2));
for pm in numpy.arange(0.9,1.01,0.01):
    compareencodings(numpy.round(pm, decimals=2));

       
    