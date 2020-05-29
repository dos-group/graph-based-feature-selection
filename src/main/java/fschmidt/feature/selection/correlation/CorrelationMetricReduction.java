package fschmidt.feature.selection.correlation;

import fschmidt.feature.selection.graph.*;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.math3.util.Pair;

import static java.util.stream.Collectors.toList;

/**
 * @author fschmidt
 */
public class CorrelationMetricReduction extends CorrelationAlgorithm {

    private final double maxCorrelation;
    private final List<Pair<List<String>, Double>> recommendedFeatureSets;
    private final Correlation keyCorrelationMethod;

    public CorrelationMetricReduction(Correlation keyCorrelationMethod, double maxCorrelation) {
        this.maxCorrelation = maxCorrelation;
        recommendedFeatureSets = new ArrayList<>();
        this.keyCorrelationMethod = keyCorrelationMethod;
    }

    public List<Pair<List<String>, Double>> run(Map<String, Double> rankingValues, String[] header, List<double[]> values) {
        List<Double> allCorrelationValues = new ArrayList<>();
        UndirectedGraph baseGraph = new UndirectedGraph();
        //create baseGraph vertices
        for (String metricName : rankingValues.keySet()) {
            double rankingValue = rankingValues.get(metricName);
            WeightedVertex v = new WeightedVertex(metricName);
            v.addProperty("ranking", rankingValue);
            if (!Double.isNaN(rankingValue)) {
                baseGraph.addVertex(v);
            }
        }
        //Calculate correlation between all different pairs of metrics
        for (int i = 0; i < header.length; i++) {
            for (int j = 0; j < header.length; j++) {
                if (i != j && rankingValues.containsKey(header[i]) && rankingValues.containsKey(header[j])) {
                    double[] vector1 = new double[values.size()];
                    double[] vector2 = new double[values.size()];
                    for (int s = 0; s < values.size(); s++) {
                        vector1[s] = values.get(s)[i];
                        vector2[s] = values.get(s)[j];
                    }
                    if (baseGraph.containsVertex(header[i]) && baseGraph.containsVertex(header[j])) {
                        WeightedEdge e = baseGraph.addEdge(baseGraph.getVertex(header[i]), baseGraph.getVertex(header[j]));
                        if (e == null) continue; //Edge already existed
                        double corrResult = keyCorrelationMethod.correlation(vector1, vector2);
                        e.addProperty(keyCorrelationMethod.toString(), corrResult);
                        allCorrelationValues.add(Math.abs(corrResult));
                    }
                }
            }
        }

        //TODO: 0.9 should be maxCorrelation? Not every
        Collections.sort(allCorrelationValues);
        int maxCorrelationSize = (int) (allCorrelationValues.size() * 0.9);
        allCorrelationValues = allCorrelationValues.stream().limit(maxCorrelationSize).collect(toList());
        if (allCorrelationValues.size() > 0) {
            if (allCorrelationValues.get(allCorrelationValues.size() - 1) > 0.9) {
                allCorrelationValues = allCorrelationValues.stream().filter(v -> v <= 0.9).collect(toList());
            }
        }

        //Only analyze one correlation value (a lot shorter in terms of computation time)
        double corrValue = maxCorrelation;
        if (allCorrelationValues.size() > 0) {
            corrValue = allCorrelationValues.get(allCorrelationValues.size() - 1);
        }

        //create graph
        UndirectedGraph graph = createGraph(baseGraph, corrValue);
        int totalPossibleEdges = graph.getNumberOfVertices() * (graph.getNumberOfVertices() - 1) / 2;
        System.out.println("Number of Edges in Graph: " + graph.getNumberOfEdges() + " / " + totalPossibleEdges + " total possible edges.");

        //find cliques
        long timeClique1 = System.currentTimeMillis();
        //Clique Finding
        Set<Set<WeightedVertex>> cliques = CliqueAlgorithm.bronKerboschPivoting(graph);
        long timeClique2 = System.currentTimeMillis();
        long timeClique = timeClique2 - timeClique1;
        System.out.println(String.format("Found %s cliques. Clique algorithm needed: %s ms.", cliques.size(), timeClique));

        Set<WeightedVertex> bestClique = findBestClique(cliques, graph, 1);

        //TODO: currently for testing how to choose the best correlation by checking the ranking result values. (next also to test: how the results might influence the algorithmic anomaly detection result)
        Pair<List<String>, Double> featureSet = new Pair<>(featureSetArrayList(bestClique), corrValue);
        recommendedFeatureSets.add(featureSet);

        return recommendedFeatureSets;
    }

    private Set<String> featureSet(Set<WeightedVertex> vertices) {
        Set<String> features = new HashSet<>();
        for (WeightedVertex vertice : vertices) {
            features.add(vertice.getName());
        }
        return features;
    }

    private List<String> featureSetArrayList(Set<WeightedVertex> vertices) {
        List<String> features = new ArrayList<>();
        for (WeightedVertex vertice : vertices) {
            features.add(vertice.getName());
        }
        return features;
    }

    private UndirectedGraph createGraph(UndirectedGraph baseGraph, double maxCorrelation) {
        UndirectedGraph graph = baseGraph.copy();
        graph.removeWeightedEdges(keyCorrelationMethod.toString(), maxCorrelation);
        return graph;
    }

    /*
        Max clique with min avg ranked value
     */
    private Set<WeightedVertex> findBestClique(Set<Set<WeightedVertex>> cliques, UndirectedGraph graph, int dimensionSearchDepth) {
        Set<WeightedVertex> bestClique = new HashSet<>();
        double bestCliqueValue = Double.MIN_VALUE;

        int biggestSize = 0;
        for (Set<WeightedVertex> clique : cliques) {
            if (clique.size() > biggestSize) {
                biggestSize = clique.size();
            }
        }

        for (Set<WeightedVertex> clique : cliques) {
            if (clique.size() > biggestSize - dimensionSearchDepth) {
                double currentCliqueDistanceValue = avgVertexWeightValue(clique, "ranking");
                List<String> list = clique.stream().map(WeightedVertex::getName).collect(toList());
                double currentCliqueCorrelationValue = graph.getAverageCorrelation(list, "pearson");
                double currentCliqueValue = 1 - currentCliqueCorrelationValue + currentCliqueDistanceValue;

                if (currentCliqueValue > bestCliqueValue) {
                    bestCliqueValue = currentCliqueValue;
                    bestClique = clique;
                }
            }
        }
        return bestClique;
    }

    private double avgVertexWeightValue(Set<WeightedVertex> vSet, String key) {
        double result = 0.0;
        for (WeightedVertex v : vSet) {
            result += (Double) v.getProperty(key);
        }
        return result / vSet.size();
    }

    private double maxVertexWeightValue(Set<WeightedVertex> vSet, String key) {
        double result = Double.MIN_VALUE;
        for (WeightedVertex v : vSet) {
            if (result < (Double) v.getProperty(key)) {
                result = (Double) v.getProperty(key);
            }
        }
        return result;
    }

    private double minVertexWeightValue(Set<WeightedVertex> vSet, String key) {
        double result = Double.MAX_VALUE;
        for (WeightedVertex v : vSet) {
            if (result > (Double) v.getProperty(key)) {
                result = (Double) v.getProperty(key);
            }
        }
        return result;
    }

    private double avgEdgeWeightValue(Set<WeightedVertex> vSet, String key) {
        double result = 0.0;
        if (!vSet.isEmpty()) {
            WeightedVertex v = vSet.iterator().next();
            for (WeightedEdge e : v.getEdges().values()) {
                double corr = (Double) e.getProperty(key);
                result += corr;
            }
        } else {
            return result;
        }
        return result / vSet.size();
    }

    private double maxEdgeWeightValue(Set<WeightedVertex> vSet, String key) {
        double result = Double.MIN_VALUE;
        if (!vSet.isEmpty()) {
            WeightedVertex v = vSet.iterator().next();
            for (WeightedEdge e : v.getEdges().values()) {
                double corr = (Double) e.getProperty(key);
                if (result < corr) {
                    result = corr;
                }
            }
        } else {
            return 0.0;
        }
        return result;
    }

    private double minEdgeWeightValue(Set<WeightedVertex> vSet, String key) {
        double result = Double.MAX_VALUE;
        if (!vSet.isEmpty()) {
            WeightedVertex v = vSet.iterator().next();
            for (WeightedEdge e : v.getEdges().values()) {
                double corr = (Double) e.getProperty(key);
                if (result > corr) {
                    result = corr;
                }
            }
        } else {
            return 0.0;
        }
        return result;
    }

    private Set<Set<WeightedVertex>> uniquify(Set<Set<WeightedVertex>> allCliques, int dimensionSearchDepth) {
        Set<Set<WeightedVertex>> uniquifiedCliques = new HashSet<>();
        Set<List<WeightedVertex>> uniqueCliques = new HashSet<>();

        int biggestSize = 0;
        for (Set<WeightedVertex> clique : allCliques) {
            if (clique.size() > biggestSize) {
                biggestSize = clique.size();
            }
        }
        for (Set<WeightedVertex> clique : allCliques) {
            List<WeightedVertex> list = new ArrayList<>(clique);
            list.sort(Comparator.comparing(WeightedVertex::getName));
            if (!uniqueCliques.contains(list) && list.size() > (biggestSize - dimensionSearchDepth)) {
                uniqueCliques.add(list);
                uniquifiedCliques.add(clique);
            }
        }

        return uniquifiedCliques;
    }
}
