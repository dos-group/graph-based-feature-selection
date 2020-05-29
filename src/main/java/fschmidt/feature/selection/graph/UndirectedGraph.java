package fschmidt.feature.selection.graph;

import java.util.*;

/**
 * @author fschmidt
 */
public class UndirectedGraph {

    private final Map<String, WeightedVertex> vertices;
    private final Set<WeightedEdge> edges;

    public UndirectedGraph() {
        vertices = new HashMap<>();
        edges = new HashSet<>();
    }

    public void addVertex(WeightedVertex vertex) {
        if (!vertices.containsKey(vertex.getName())) {
            vertices.put(vertex.getName(), vertex);
        }
    }

    public WeightedEdge addEdge(WeightedVertex vertex1, WeightedVertex vertex2) {
        if (containsEdge(vertex1, vertex2)) {
            return null; //Dont add already existing edges
        } else {
            WeightedEdge edge = new WeightedEdge(vertex1, vertex2);
            vertex1.addEdge(vertex2, edge);
            vertex2.addEdge(vertex1, edge);
            edges.add(edge);
            return edge;
        }
    }

    public boolean containsEdge(WeightedVertex vertex1, WeightedVertex vertex2) {
        return getEdge(vertex1, vertex2) != null;
    }

    public WeightedEdge getEdge(WeightedVertex vertex1, WeightedVertex vertex2) {
        for (Iterator<WeightedEdge> iter = edges.iterator(); iter.hasNext(); ) {
            WeightedEdge currEdge = iter.next();
            if (currEdge.getVertex1().equals(vertex1) || currEdge.getVertex1().equals(vertex2)) {
                if (currEdge.getVertex2().equals(vertex1) || currEdge.getVertex2().equals(vertex2)) {
                    // Any combination of vertex1-vertex1, vertex2-vertex2, vertex1-vertex2, vertex2-vertex1
                    // Undirected Graph
                    return currEdge;
                }
            }
        }
        return null;
    }

    public void removeWeightedEdges(String key, double maxWeight) {
        Iterator<WeightedEdge> edgeIter = edges.iterator();
        while (edgeIter.hasNext()) {
            WeightedEdge e = edgeIter.next();
            Double weight = (Double) e.getProperty(key);
            if (Double.isNaN(weight) || Math.abs(weight) > maxWeight || Math.abs(weight) < -maxWeight) {
                e.remove();
                edgeIter.remove();
            }
        }
    }

    public List<WeightedEdge> getAllEdgesToBeRemoved(String key, double maxWeight) {
        Iterator<WeightedEdge> edgeIter = edges.iterator();
        List<WeightedEdge> allEdges = new ArrayList<>();
        while (edgeIter.hasNext()) {
            WeightedEdge e = edgeIter.next();
            Double weight = (Double) e.getProperty(key);
            if (Double.isNaN(weight) || Math.abs(weight) > maxWeight || Math.abs(weight) < -maxWeight) {
                allEdges.add(e);
            }
        }
        return allEdges;
    }

    public Set<WeightedVertex> getVertices() {
        return new HashSet<>(vertices.values());
    }

    public Map<String, WeightedVertex> getVerticesMap() {
        return vertices;
    }

    public WeightedVertex getVertex(String name) {
        return vertices.get(name);
    }

    public boolean containsVertex(String name) {
        return vertices.containsKey(name);
    }

    public int getNumberOfEdges() {
        return edges.size();
    }

    public int getNumberOfVertices() {
        return vertices.size();
    }

    public UndirectedGraph copy() {
        UndirectedGraph graph = new UndirectedGraph();
        for (WeightedVertex v : vertices.values()) {
            WeightedVertex newV = new WeightedVertex(v.getName());
            for (Map.Entry<String, Object> property : v.getProperties().entrySet()) {
                newV.addProperty(property.getKey(), property.getValue());
            }
            graph.addVertex(newV);
        }
        for (WeightedEdge e : edges) {
            WeightedVertex newV1 = graph.getVertex(e.getVertex1().getName());
            WeightedVertex newV2 = graph.getVertex(e.getVertex2().getName());
            WeightedEdge newE = graph.addEdge(newV1, newV2);
            if (newE == null) continue; //Edge already existed, should never occur when copying a valid graph
            for (Map.Entry<String, Object> property : e.getProperties().entrySet()) {
                newE.addProperty(property.getKey(), property.getValue());
            }
        }
        return graph;
    }

    @Override
    public String toString() {
        return "Graph = vertices: " + vertices + ", edges: " + edges;
    }

    //TODO: add function to get information about set of vertices/edges (eg avg, sum, etc)

    public double getAverageCorrelation(List<String> verticesString, String propertyName) {
        double propertyAverage = 0.0;
        int totalEdges = 0;
        for (int i = 0; i < verticesString.size(); i++) {
            for (int j = i; j < verticesString.size(); j++) {
                WeightedVertex vertexI = vertices.get(verticesString.get(i));
                WeightedVertex vertexJ = vertices.get(verticesString.get(j));
                WeightedEdge foundEdge = getEdge(vertexI, vertexJ);
                if (j != i && foundEdge != null) {
                    propertyAverage += Math.abs((double) foundEdge.getProperty(propertyName));
                    totalEdges++;
                }

            }
        }
        return (propertyAverage / totalEdges);
    }

    public WeightedEdge getEdgeByWeight(String key, double byWeight) {
        Iterator<WeightedEdge> edgeIter = edges.iterator();
        while (edgeIter.hasNext()) {
            WeightedEdge e = edgeIter.next();
            Double weight = (Double) e.getProperty(key);
            if (weight == byWeight) return e;
        }
        return null;
    }

    public void removeVertex(WeightedVertex vertex) {
        edges.removeIf(e -> e.getVertex1().equals(vertex) || e.getVertex2().equals(vertex));
        vertices.remove(vertex.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UndirectedGraph that = (UndirectedGraph) o;
        return Objects.equals(vertices, that.vertices) &&
                Objects.equals(edges, that.edges);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertices, edges);
    }
}
