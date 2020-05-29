package fschmidt.feature.selection.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author fschmidt
 */
public class WeightedVertex extends WeightedGraphElement {

    private final String name;
    private final Map<WeightedVertex, WeightedEdge> edges;
    
    public WeightedVertex(String name){
        this.name = name;
        edges = new HashMap<>();
    }

    public void addEdge(WeightedVertex vertex, WeightedEdge edge) {
        edges.put(vertex, edge);
    }

    public Set<WeightedVertex> getNeighbors() {
        return edges.keySet();
    }

    public String getName() {
        return name;
    }

    public Map<WeightedVertex, WeightedEdge> getEdges() {
        return edges;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WeightedVertex) {
            WeightedVertex objV = (WeightedVertex) obj;
            if (objV.name.equals(this.name)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(name: " + this.name + ", weight: " + this.getProperties()+ ")";
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
