package fschmidt.feature.selection.graph;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author fschmidt
 */
public class CliqueAlgorithm {

    /*
     *    BronKerbosch1(R, P, X):
       if P and X are both empty:
           report R as a maximal clique
       for each vertex v in P:
           BronKerbosch1(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
           P := P \ {v}
           X := X ⋃ {v}
     */
    public static Set<Set<WeightedVertex>> bronKerbosch(UndirectedGraph graph) {
        Set<Set<WeightedVertex>> allCliques = new HashSet<>();
        Set<WeightedVertex> graphsVertices = new HashSet<>(graph.getVertices());
        runBronKerbosch(new HashSet<>(), graphsVertices, new HashSet<>(), allCliques);
        return allCliques;
    }

    /*
     *    BronKerbosch2(R,P,X):
       if P and X are both empty:
           report R as a maximal clique
       choose a pivot vertex u in P ⋃ X
       for each vertex v in P \ N(u):
           BronKerbosch2(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
           P := P \ {v}
           X := X ⋃ {v}
     */
    public static Set<Set<WeightedVertex>> bronKerboschPivoting(UndirectedGraph graph) {
        Set<Set<WeightedVertex>> allCliques = new HashSet<>();
        Set<WeightedVertex> graphsVertices = new HashSet<>(graph.getVertices());
        runBronKerboschPivoting(new HashSet<>(), graphsVertices, new HashSet<>(), allCliques);
        return allCliques;
    }

    /*
        R is current growing clique, p all possible vertices, x also
     */
    private static void runBronKerbosch(Set<WeightedVertex> r, Set<WeightedVertex> p, Set<WeightedVertex> x,
                                        Set<Set<WeightedVertex>> allCliques) {
        if (notContainsSet(r, allCliques) && !r.isEmpty()) {
            Set<WeightedVertex> newClique = new HashSet<>(r);
            allCliques.add(newClique);
        }
        if (p.isEmpty() && x.isEmpty()) {
            return; //as max clique
        }
        Iterator<WeightedVertex> pIter = p.iterator();
        while (pIter.hasNext()) {
            WeightedVertex v = pIter.next();
            Set<WeightedVertex> newR = new HashSet<>(r);
            newR.add(v);
            Set<WeightedVertex> newP = new HashSet<>(p);
            newP.retainAll(v.getNeighbors());
            Set<WeightedVertex> newX = new HashSet<>(x);
            newX.retainAll(v.getNeighbors());
            runBronKerbosch(newR, newP, newX, allCliques);
            pIter.remove();
            x.add(v);
        }
    }

    private static void runBronKerboschPivoting(Set<WeightedVertex> r, Set<WeightedVertex> p, Set<WeightedVertex> x,
                                                Set<Set<WeightedVertex>> allCliques) {
        if (notContainsSet(r, allCliques)) {
            Set<WeightedVertex> newClique = new HashSet<>(r);
            allCliques.add(newClique);
        }
        if (p.isEmpty() && x.isEmpty()) {
            return; //as max clique
        }
        Set<WeightedVertex> possiblePivotSet = new HashSet<>(p);
        possiblePivotSet.addAll(x);
        WeightedVertex pivorU = findHighestNeighbors(possiblePivotSet);
        Set<WeightedVertex> pivotSet = new HashSet<>(p);
        pivotSet.removeAll(pivorU.getNeighbors());
        for (WeightedVertex v : pivotSet) {
            Set<WeightedVertex> newR = new HashSet<>(r);
            newR.add(v);
            Set<WeightedVertex> newP = new HashSet<>(p);
            newP.retainAll(v.getNeighbors());
            Set<WeightedVertex> newX = new HashSet<>(x);
            newX.retainAll(v.getNeighbors());
            runBronKerbosch(newR, newP, newX, allCliques);
            p.remove(v);
            x.add(v);
        }
    }

    private static WeightedVertex findHighestNeighbors(Set<WeightedVertex> set) {
        WeightedVertex v = null;
        for (WeightedVertex s : set) {
            if (v == null || s.getNeighbors().size() > v.getNeighbors().size()) {
                v = s;
            }
        }
        return v;
    }

    private static boolean notContainsSet(Set<WeightedVertex> currentClique, Set<Set<WeightedVertex>> allCliques) {
        boolean notContained = true;
        for (Set<WeightedVertex> c : allCliques) {
            if (c.equals(currentClique)) {
                notContained = false;
                break;
            }
        }
        return notContained;
    }

    /*
     *    BronKerbosch2(R,P,X):
       if P and X are both empty:
           report R as a maximal clique
       choose a pivot vertex u in P ⋃ X
       for each vertex v in P \ N(u):
           BronKerbosch2(R ⋃ {v}, P ⋂ N(v), X ⋂ N(v))
           P := P \ {v}
           X := X ⋃ {v}
     */
    public static Set<Set<WeightedVertex>> bronKerboschPivotingOnlyMax(UndirectedGraph graph) {
        Set<Set<WeightedVertex>> allCliques = new HashSet<>();
        Set<WeightedVertex> graphsVertices = new HashSet<>(graph.getVertices());
        runBronKerboschPivotingOnlyMax(new HashSet<>(), graphsVertices, new HashSet<>(), allCliques);
        return allCliques;
    }

    private static void runBronKerboschPivotingOnlyMax(Set<WeightedVertex> r,
                                                       Set<WeightedVertex> p,
                                                       Set<WeightedVertex> x,
                                                       Set<Set<WeightedVertex>> allCliques) {
        if (p.isEmpty() && x.isEmpty()) {
            allCliques.add(r);
            return; //as max clique
        }
        Set<WeightedVertex> possiblePivotSet = new HashSet<>(p);
        possiblePivotSet.addAll(x);
        WeightedVertex pivorU = findHighestNeighbors(possiblePivotSet);
        Set<WeightedVertex> pivotSet = new HashSet<>(p);
        pivotSet.removeAll(pivorU.getNeighbors());
        for (WeightedVertex v : pivotSet) {
            Set<WeightedVertex> newR = new HashSet<>(r);
            newR.add(v);
            Set<WeightedVertex> newP = new HashSet<>(p);
            newP.retainAll(v.getNeighbors());
            Set<WeightedVertex> newX = new HashSet<>(x);
            newX.retainAll(v.getNeighbors());
            runBronKerbosch(newR, newP, newX, allCliques);
            p.remove(v);
            x.add(v);
        }
    }
}
