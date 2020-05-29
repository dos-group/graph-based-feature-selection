package fschmidt.feature.selection.graph;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fschmidt
 */
public abstract class WeightedGraphElement {

    private Map<String, Object> properties = new HashMap<>();

    public void addProperty(String key, Object data) {
        properties.put(key, data);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
