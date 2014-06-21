package rna.solver;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

public class RNAField
{

    public String sequence;
    public HashMap<Point, Nucleotide> structure;
    public int structureLength;
    public Nucleotide current;
    public Nucleotide initial;

    public RNAField(String sequence)
    {
        this.sequence = sequence;
        structure = new HashMap<>();
        structureLength = 0;
    }

    public boolean occupied(int x, int y)
    {
        return get(x, y) != null;
    }

    /**
     * Checks if adding a nucleotide would cross diagonal structure (evil shit!)
     *
     * @param currentx
     * @param currenty
     * @param dir
     * @return
     */
    public boolean crosses(int currentx, int currenty, NucleotideDirection dir)
    {
        Point pleft = NucleotideDirection.shiftByDir(currentx, currenty,
                dir.rotateLeft());
        Point pright = NucleotideDirection.shiftByDir(currentx, currenty,
                dir.rotateRight());

        Nucleotide nucleft = get(pleft.x, pleft.y);
        Nucleotide nucright = get(pright.x, pright.y);

        if (nucleft != null)
        {
            if (nucleft.dir.is90DegreeTo(dir))
            {
                return true;
            }
        }
        if (nucright != null)
        {
            if (nucright.dir.is90DegreeTo(dir))
            {
                return true;
            }
        }

        return false;
    }

    // /**
    // * Checks if it's next to a bonded nucleotide or a curve
    // *
    // * @param x
    // * @param y
    // * @return
    // */
    // public boolean disturbsBonding(int xi, int yi)
    // {
    // /*
    // * Nucleotide east = get(x + 1, y); Nucleotide west = get(x - 1, y);
    // * Nucleotide north = get(x, y - 1); Nucleotide south = get(x, y + 1);
    // *
    // * if (east != null && east.bond != null) return true; if (west != null
    // * && west.bond != null) return true; if (north != null && north.bond !=
    // * null) return true; if (south != null && south.bond != null) return
    // * true;
    // *
    // * return false;
    // */
    //
    // Nucleotide nuc = get(xi, yi);
    //
    // if (nuc == null)
    // return false;
    //
    // for (int x = nuc.x - 1; x <= nuc.x + 1; x++)
    // {
    // for (int y = nuc.y - 1; y <= nuc.y; y++)
    // {
    // Nucleotide other = get(x, y);
    //
    // if (other != null && other != nuc && other.isBond())
    // {
    // return true;
    // }
    // }
    // }
    //
    // return false;
    // }
    public Nucleotide get(int x, int y)
    {
        return structure.get(new Point(x, y));
    }

    public void set(int x, int y, Nucleotide nuc)
    {
        structure.put(new Point(x, y), nuc);
    }

    /**
     * Sets initial nucleotide, algorithm will extend it!
     *
     * @param x
     * @param y
     * @param type
     * @param dir
     */
    public void initial(int x, int y, NucleotideType type,
            NucleotideDirection dir)
    {
        Nucleotide n = new Nucleotide(type, dir, RelativeDirection.STRAIGHT);
        n.x = x;
        n.y = y;
        n.previous = null;

        this.initial = n;
        this.current = n;
        structureLength++;

        set(x, y, n);
    }

    /**
     * Tries to append nucleotide to field
     */
    public boolean append(RelativeDirection dir, NucleotideType type,
            boolean testOnly)
    {
        // calculate new x,y for the nucleotide
        NucleotideDirection ndir = dir.toAbsolute(current.dir);

        // Use dir to shift x,y
        // Note: For left/right, shift it also by current.dir
        Point cursor = NucleotideDirection.shiftByDir(current.x, current.y,
                ndir);

        // Test x,y
        if (occupied(cursor.x, cursor.y))
        {
            return false;
        }
        // if (disturbsBonding(cursor.x, cursor.y))
        // return false;
        if (ndir.isDiagonal() && crosses(current.x, current.y, ndir))// Diagonal
        // directions
        // may
        // cross
        // structures
        // =>
        // prevent
        // this!
        {
            return false;
        }

        if (testOnly)
        {
            return true;
        }

        // Create nucleotide
        Nucleotide nuc = new Nucleotide(type, ndir, dir);

        nuc.x = cursor.x;
        nuc.y = cursor.y;

        // Add to field (if not test)
        this.current.next = nuc;
        nuc.previous = this.current;

        this.current = nuc; // !Reihenfolge!

        structureLength++;

        set(nuc.x, nuc.y, nuc);

        /**
         * Update Bondings
         */
        bond(nuc, new LinkedList<Integer>());

        return true;
    }

    /**
     * Reads all nucleotides around Nucleotide nuc into candidates array
     */
    private Nucleotide[] fetchNucleotidesAround(Nucleotide nuc)
    {
        Nucleotide[] candidates = new Nucleotide[8];

        Nucleotide north = get(nuc.x, nuc.y - 1);
        Nucleotide south = get(nuc.x, nuc.y + 1);
        Nucleotide west = get(nuc.x - 1, nuc.y);
        Nucleotide east = get(nuc.x + 1, nuc.y);

        Nucleotide north_west = get(nuc.x - 1, nuc.y - 1);
        Nucleotide south_east = get(nuc.x + 1, nuc.y + 1);
        Nucleotide south_west = get(nuc.x - 1, nuc.y - 1);
        Nucleotide north_east = get(nuc.x + 1, nuc.y + 1);

        candidates[0] = north;
        candidates[1] = south;
        candidates[2] = east;
        candidates[3] = west;

        candidates[4] = north_west;
        candidates[5] = north_east;
        candidates[6] = south_west;
        candidates[7] = south_east;

        return candidates;
    }

    /**
     * Aktualisiert Bindungen des Nukleotids
     *
     * I) Gerade Bindungen werden bei gleicher Energie bevorzugt
     *
     * II) Eine Bindung kommt nur zustande, wenn die Richtungen invers
     * zueinander sind III) Die Inverse Richtung der Nukleotide kann auch vom
     * NÄCHSTEN Element übernommen werden, wenn es passt (*** Noch nicht IMPL)
     *
     *
     * III) Rekursive Übergabe
     *
     * IV) Immer die beste Bindung wählen
     *
     * @param nuc
     * @return
     */
    public boolean bond(Nucleotide nuc, LinkedList<Integer> watcher)
    {
        /**
         * Watcher passt auf, dass das selbe Nukleotid nich mehrfach behandlet
         * wird
         */
        if (watcher.contains(nuc.getIndex()))
        {
            return false;
        }
        else
        {
            watcher.add(nuc.getIndex());
        }

        /**
         * Lade Kandidaten
         */
        Nucleotide[] candidates = fetchNucleotidesAround(nuc);

        Nucleotide best = null;
        int bestEnergy = Integer.MAX_VALUE;
        boolean bestIsStraight = false;

        if (nuc.isBond())
        {
            bestEnergy = nuc.energy(this);
            bestIsStraight = nuc.isBondStraight();
        }

        for (int i = 0; i < 8; i++)
        {
            Nucleotide candidateNucleotide = candidates[i];
            boolean candidateIsStraight = i < 4;

            if (candidateNucleotide == null)
            {
                continue;
            }
            
            /**
             * Check if conditions are met
             */
            NucleotideDirection nucdir = nuc.dir;
            NucleotideDirection canddir = candidateNucleotide.dir;
            
           
            if (nucdir != canddir.reverse())
            {
                if (nuc.previous != null)
                {
                    nucdir = nuc.previous.dir;
                }
                if (candidateNucleotide.previous != null)
                {
                    canddir = candidateNucleotide.previous.dir;
                }

                if (nucdir != canddir.reverse())
                {
                    continue;
                }
            }

            int energy = Nucleotide.calculateEnergy(candidateNucleotide, nuc);

            /**
             * - Wenn es eine gerade Variante mit gleicher Energie gibt
             * - Wenn es kein bestes gibt und der Kandidat keinen Bindungspartner hat
             */
            if ((bestEnergy == energy && candidateIsStraight && !bestIsStraight)
                    || (!candidateNucleotide.isBond() && energy < bestEnergy)
                    || (candidateNucleotide.isBond() && energy < bestEnergy && candidateNucleotide.energy(this) < energy)
                    || (best == null && !candidateNucleotide.isBond()))
            {
                /**
                 * Set as best
                 */
                best = candidateNucleotide;
                bestIsStraight = candidateIsStraight;
                bestEnergy = energy;
            }
        }

        if (best != null)
        {
            /**
             * Bonding ausführen
             */
            Nucleotide stolen = best.bond; // Wenn best gebunden war

            best.unBond();
            nuc.unBond();

            nuc.bondTo(best);

            if (stolen != null)
            {
                bond(stolen, watcher);
            }

            return true;
        }
        else
        {
            return false;
        }

    }

    // /**
    // * Veränderter Bond-Algorithmus
    // *
    // * Betrachte alle Nukleotide, die nicht prev oder next sind und wähle die
    // * SCHLECHTESTE Bindung aus
    // *
    // * Gibt bond rekursiv an ein verändertes Nukleotid weiter, damit dieses
    // die
    // * Bindung aktualisieren kann.
    // *
    // * @param nuc
    // * @return
    // */
    // public boolean bond(Nucleotide nuc, LinkedList<Integer> watcher)
    // {
    // if (watcher.contains(nuc.getIndex()))
    // {
    // return false;
    // }
    // else
    // {
    // watcher.add(nuc.getIndex());
    // }
    //
    // Nucleotide best = null;
    // int bestenergy = Integer.MIN_VALUE;
    //
    // if (nuc.isBond())
    // bestenergy = nuc.energy(this);
    //
    // for (int x = nuc.x - 1; x <= nuc.x + 1; x++)
    // {
    // for (int y = nuc.y - 1; y <= nuc.y; y++)
    // {
    // /**
    // * Wenn selber diagonal => kann mit diagonalen Binden
    // * Wenn gerade => Kann nur mit geraden binden
    // */
    // // if(!nuc.dir.isDiagonal())
    // // {
    // // if(nuc.x != x && nuc.y != y)
    // // continue;
    // // }
    // // else
    // // {
    // // if(nuc.x == x || nuc.y == y)
    // // continue;
    // // }
    //
    //
    // Nucleotide candidate = get(x, y);
    //
    // if (candidate != null && candidate != nuc
    // && candidate != nuc.next && candidate != nuc.previous)
    // {
    // /**
    // * Sind die Nukleotide gegeneinander?
    // *
    // * Mit kleinem Spielraum
    // */
    // // if (NucleotideDirection.difference(nuc.dir, candidate.dir.reverse()) >
    // 2)
    // // continue;
    // if(!nuc.dir.intersectsWith(candidate.dir.reverse()))
    // continue;
    //
    // /**
    // * Spezialfall: Wenn noch kein Candidat gefunden wurde,
    // * force it
    // */
    // if (best == null)
    // {
    // best = candidate;
    // bestenergy = Nucleotide.calculateEnergy(nuc.type,
    // candidate.type);
    // continue;
    // }
    //
    // int candidateEnergy = Nucleotide.calculateEnergy(nuc.type,
    // candidate.type);
    //
    // /**
    // * Ist neue Energie schlechter?
    // */
    // if (candidateEnergy > candidate.energy(this))
    // {
    // /**
    // * Ist die Energie besser als die aktuelle Energie?
    // */
    // if (candidateEnergy > bestenergy)
    // {
    // best = candidate;
    // bestenergy = candidateEnergy;
    // }
    // }
    // }
    // }
    // }
    //
    // /**
    // * Gibt es einen besten? *
    // */
    // if (best == null)
    // return false;
    //
    // /**
    // * Ist best = aktueller? oder ist best überhaupt besser?
    // */
    // if (nuc.bond == best)
    // return false; // Keine Änderung
    //
    // /**
    // * Bonding ausführen
    // */
    // Nucleotide stolen = best.bond; // Wenn best gebunden war
    //
    // best.unBond();
    // nuc.unBond();
    //
    // nuc.bondTo(best);
    //
    // if (stolen != null)
    // bond(stolen, watcher);
    //
    // return true;
    // }
    // /**
    // * Veränderter Bond-Algorithmus
    // *
    // * Betrachte alle Nukleotide, die nicht prev oder next sind und wähle die
    // * Beste Bindung aus
    // *
    // * Gibt bond rekursiv an ein verändertes Nukleotid weiter, damit dieses
    // die
    // * Bindung aktualisieren kann.
    // *
    // * @param nuc
    // * @return
    // */
    // public boolean bond(Nucleotide nuc, LinkedList<Integer> watcher)
    // {
    // if (watcher.contains(nuc.getIndex()))
    // {
    // return false;
    // }
    // else
    // {
    // watcher.add(nuc.getIndex());
    // }
    //
    // Nucleotide best = null;
    // int bestenergy = Integer.MAX_VALUE;
    //
    // if (nuc.isBond())
    // bestenergy = nuc.energy(this);
    //
    // for (int x = nuc.x - 1; x <= nuc.x + 1; x++)
    // {
    // for (int y = nuc.y - 1; y <= nuc.y; y++)
    // {
    // /**
    // * Wenn selber diagonal => kann mit allen binden
    // * Wenn gerade => Kann nur mit geraden binden
    // */
    // if(!nuc.dir.isDiagonal())
    // {
    // if(nuc.x != x && nuc.y != y)
    // continue;
    // }
    //
    //
    // Nucleotide candidate = get(x, y);
    //
    // if (candidate != null && candidate != nuc
    // && candidate != nuc.next && candidate != nuc.previous)
    // {
    // /**
    // * Sind die Nukleotide gegeneinander?
    // *
    // * Mit kleinem Spielraum
    // */
    // // if (NucleotideDirection.difference(nuc.dir, candidate.dir.reverse()) >
    // 2)
    // // continue;
    // if(!nuc.dir.intersectsWith(candidate.dir.reverse()))
    // continue;
    //
    // /**
    // * Spezialfall: Wenn noch kein Candidat gefunden wurde,
    // * force it
    // */
    // if (best == null)
    // {
    // best = candidate;
    // bestenergy = Nucleotide.calculateEnergy(nuc.type,
    // candidate.type);
    // continue;
    // }
    //
    // int candidateEnergy = Nucleotide.calculateEnergy(nuc.type,
    // candidate.type);
    //
    // /**
    // * Ist neue Energie besser?
    // */
    // if (candidateEnergy < candidate.energy(this))
    // {
    // /**
    // * Ist die Energie besser als die aktuelle Energie?
    // */
    // if (candidateEnergy < bestenergy)
    // {
    // best = candidate;
    // bestenergy = candidateEnergy;
    // }
    // }
    // }
    // }
    // }
    //
    // /**
    // * Gibt es einen besten? *
    // */
    // if (best == null)
    // return false;
    //
    // /**
    // * Ist best = aktueller? oder ist best überhaupt besser?
    // */
    // if (nuc.bond == best || nuc.energy(this) <= bestenergy)
    // return false; // Keine Änderung
    //
    // /**
    // * Bonding ausführen
    // */
    // Nucleotide stolen = best.bond; // Wenn best gebunden war
    //
    // best.unBond();
    // nuc.unBond();
    //
    // nuc.bondTo(best);
    //
    // if (stolen != null)
    // bond(stolen, watcher);
    //
    // return true;
    // }
    // /**
    // * Aktualisiert Bindungen des Nukleotids
    // *
    // * Regeln: I) Gerade Bindungen haben Vorrang gegenüber diagonalen => Erst
    // * WENN keine GERADEN Bindungen gefunden werden, wird diagonal gebunden!
    // *
    // * II) Eine Bindung kommt nur zustande, wenn die Richtungen invers
    // * zueinander sind III) Die Inverse Richtung der Nukleotide kann auch vom
    // * NÄCHSTEN Element übernommen werden, wenn es passt (*** Noch nicht IMPL)
    // *
    // * @param nuc
    // * @return
    // */
    // public boolean bond(Nucleotide nuc)
    // {
    // LinkedList<Nucleotide> straights = new LinkedList<>();
    // LinkedList<Nucleotide> diagonals = new LinkedList<>();
    //
    // for (int x = nuc.x - 1; x <= nuc.x + 1; x++)
    // {
    // for (int y = nuc.y - 1; y <= nuc.y; y++)
    // {
    // if (x == nuc.x && y == nuc.y)
    // continue;
    //
    // Nucleotide other = get(x, y);
    //
    // if (other != null)
    // {
    // if (x == nuc.x || y == nuc.y)
    // {
    // straights.add(other);
    // }
    // else
    // {
    // diagonals.add(other);
    // }
    // }
    // }
    // }
    //
    // Nucleotide best = null;
    // int currentenergy = Integer.MAX_VALUE;
    //
    // if (nuc.isBond())
    // currentenergy = nuc.energy();
    //
    // /**
    // * If NO straights => try diagonals
    // */
    // if (straights.size() == 0)
    // {
    // straights = diagonals;
    // }
    //
    // for (Nucleotide candidate : straights)
    // {
    // /**
    // * Check if conditions are met
    // */
    // /*
    // * if(nuc.dir != candidate.dir.reverse()) continue;
    // */
    //
    // NucleotideDirection nucdir = nuc.dir;
    // NucleotideDirection canddir = candidate.dir;
    //
    // if (nucdir != canddir.reverse())
    // {
    // if (nuc.previous != null)
    // nucdir = nuc.previous.dir;
    // if (candidate.previous != null)
    // canddir = candidate.previous.dir;
    //
    // if (nucdir != canddir.reverse())
    // continue;
    // }
    //
    // int energy = Nucleotide.calculateEnergy(candidate, nuc);
    //
    // /**
    // * Check if energy is better
    // */
    // if (currentenergy < energy)
    // continue;
    //
    // /**
    // * Check if this bond is better
    // */
    // if (candidate.isBond())
    // {
    // if (candidate.energy() < energy)
    // continue;
    // }
    //
    // /**
    // * Set as best
    // */
    // best = candidate;
    // currentenergy = energy;
    //
    // }
    //
    // if (best != null)
    // {
    // nuc.bondTo(best);
    //
    // return true;
    // }
    // else
    // {
    // return false;
    // }
    //
    // }
    /**
     * handles 'aligning' bonding mechanism; loop "bonding" is handled by
     * append()
     *
     * @param nuc
     * @return
     */
    // public boolean bond(Nucleotide nuc)
    // {
    // // Bonding mechanism
    // // Look for (unbound) nucleotide next to it with reversed direction
    // // Bond each other to it
    // Nucleotide best = null;
    // int currentenergy = Integer.MAX_VALUE;
    //
    // for(int x = nuc.x - 1; x <= nuc.x + 1; x++)
    // {
    // for(int y = nuc.y - 1; y <= nuc.y; y++)
    // {
    // /**
    // * Überprüfe x,y uns überspringe, je nachdem ob nuc gerade oder diagonal
    // ist
    // */
    // if(nuc.dir.isDiagonal())
    // {
    // /*//Eigenschaft ungerader Verbindungen ist, dass weder x,y noch y mit
    // center übereinsimmen
    // if(x == nuc.x || y == nuc.y)
    // continue;*/
    //
    // /**
    // * Diagonale können mit allen Partnern interagieren; Gerade Nukleotide nur
    // mit geraden
    // */
    // }
    // else
    // {
    // if(x != nuc.x && y != nuc.y)
    // continue;
    // }
    //
    //
    // Nucleotide other = get(x,y);
    //
    // /**
    // * Anderer nicht null usw, und Richting zur reversen nicht
    // Unterschiedlich!
    // */
    // if(other != null && other != nuc && nuc.dir == other.dir.reverse())
    // {
    // if(best == null)
    // {
    // best = other;
    // }
    // else
    // {
    // int testedenergy = Nucleotide.calculateEnergy(best, nuc);
    // int otherexisitingbond = Integer.MAX_VALUE;
    //
    // if(other.isBond())
    // {
    // otherexisitingbond = Nucleotide.calculateEnergy(other, other.bond);
    // }
    //
    // if(testedenergy < otherexisitingbond && testedenergy < currentenergy)
    // {
    // best = other;
    // currentenergy = testedenergy;
    // }
    // }
    // }
    // }
    // }
    //
    // if(best != null)
    // {
    // nuc.bondTo(best);
    //
    // return true;
    // }
    // else
    // {
    // return false;
    // }
    // }
    /**
     * Removes last nucleotide from structure
     *
     * @param x
     * @param y
     * @return
     */
    public boolean undo()
    {
        if (initial == current)
        {
            return false;
        }

        structure.remove(new Point(current.x, current.y));

        // Reset bonds
        current.unBond();

        // Reset lists
        this.current = current.previous;
        this.current.next = null;
        structureLength--;

        return true;
    }

    /**
     * Sum of energy of each nucleotide
     *
     * O(n^2)
     *
     * @return
     */
    public int energy()
    {
        int e = 0;

        for (Nucleotide nuc : structure.values())
        {
            e += nuc.energy(this);
        }

        return e;
    }

    public double fitness()
    {
        double leftover_sequence = sequence.length() - structureLength;

        return ((double) energy() + leftover_sequence * 14)
                / sequence.length();
    }

    public Nucleotide getByIndex(int index)
    {
        if (index < 0 || index >= structureLength)
        {
            return null;
        }

        Nucleotide nuc = initial;

        for (int i = 0; i < index; i++)
        {
            nuc = nuc.next;
        }

        return nuc;
    }
}
