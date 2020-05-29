package fschmidt.feature.selection.graph;

import java.util.Objects;

/**
 * @author fschmidt
 */
public class WeightedEdge extends WeightedGraphElement {

    private final WeightedVertex vertex1;
    private final WeightedVertex vertex2;

    public WeightedEdge(WeightedVertex vertex1, WeightedVertex vertex2) {
        this.vertex1 = vertex1;
        this.vertex2 = vertex2;
    }

    public void remove() {
        vertex1.getNeighbors().remove(vertex2);
        vertex2.getNeighbors().remove(vertex1);
    }

    public WeightedVertex getVertex1() {
        return vertex1;
    }

    public WeightedVertex getVertex2() {
        return vertex2;
    }

    @Override
    public String toString() {
        return "V1: " + vertex1.toString() + ", V2: " + vertex2.toString() + ", weight: " + this.getProperties();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WeightedEdge that = (WeightedEdge) o;
        return Objects.equals(vertex1, that.vertex1) &&
                Objects.equals(vertex2, that.vertex2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex1, vertex2);
    }
}
