package fschmidt.feature.selection.ranking;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

// ----------------------------------------------
//  BIN TARGET FEATURE VALUES.
// ----------------------------------------------
// Data structure that keeps the corresponding values
// ordered by dimension for each of the values in
// the ground truth (true, false). In other words,
// each value of the ground truth (true, false)
// is assigned the corresponding columns from
// the data set.
//
//  EXAMPLE:
//  +-------+-------+-------+-------+
//  | dim00 | dim01 | dim02 | class |
//  +-------+-------+-------+-------+
//  |     1 |     3 |    12 | true  |
//  |    15 |    18 |     4 | false |
//  |    17 |    19 |     5 | true  |
//  |    89 |    71 |     7 | false |
//  +-------+-------+-------+-------+
//
//  BECOMES:
//
//              dim00 --> [ 15, 89]
//  false --->  dim01 --> [ 18, 71]
//              dim02 --> [  4,  7]
//
//              dim00 --> [  1, 17]
//  true  --->  dim01 --> [  3, 19]
//              dim02 --> [ 12,  5]
//
//  @author fschmidt
//
// TODO this is basically a different representation of the Dataset and should be integrated into the Dataset abstraction as a transformation
public class BinTargetFeatureValues {

    private static final Logger logger = Logger.getLogger(BinTargetFeatureValues.class.getName());

    private final Map<Boolean, Map<String, List<Double>>> sampleMetricsByFieldNameAndTargetType;

    public BinTargetFeatureValues() {
        sampleMetricsByFieldNameAndTargetType = new HashMap<>();
    }

    public void update(Boolean target, String[] fieldNames, double[] metrics) {
        if (sampleMetricsByFieldNameAndTargetType.containsKey(target)) {
            this.addSampleMetrics(fieldNames, metrics,
                    sampleMetricsByFieldNameAndTargetType.get(target));
        } else {
            if (sampleMetricsByFieldNameAndTargetType.keySet().size() <= 2) {
                sampleMetricsByFieldNameAndTargetType.put(target,
                        this.initializeInnerSampleMetricMap(fieldNames));
                this.addSampleMetrics(fieldNames, metrics,
                        sampleMetricsByFieldNameAndTargetType.get(target));
            } else if (sampleMetricsByFieldNameAndTargetType.keySet().size() > 2) {
                logger.warning("More than two targets detected. Further targets will be ignored.");
            }
        }
    }

    /**
     * Method adds the sample metrics defined in {@code header} and {@code metrics} to the {@code target}. The {@code header} values are the
     * keys for the map. The {@code metric} values are going to be added to the list identified by the respective {@code target} value.
     *
     * @param fields Field identifier for the {@code metric} values.
     * @param metrics {@link Double} values which should be added to the respective {@code target} list.
     * @param target Destination {@link Map} containing metric value {@link List}s identified by respective filed name strings.
     */
    private void addSampleMetrics(String[] fields, double[] metrics,
            Map<String, List<Double>> target) {
        for (int i = 0; i < metrics.length; i++) {
            if (metrics[i] != Double.NaN) { // is always true!
                target.get(fields[i]).add(metrics[i]);
            }
        }
    }

    /**
     * Method creates a {@link HashMap} with each element of {@code header} as a key value and an empty {@link ArrayList} of {@link Double}
     * as values.
     *
     * @param header Key elements for the resulting {@link Map}.
     * @return {@link Map} created with the {@code header} elements.
     */
    private Map<String, List<Double>> initializeInnerSampleMetricMap(String[] header) {
        Map<String, List<Double>> ret = new HashMap<>();
        for (String aHeader : header) {
            ret.put(aHeader, new ArrayList<>());
        }
        return ret;
    }

    public Map<Boolean, Map<String, List<Double>>> getSampleMetricsByFieldNameAndTargetType() {
        return sampleMetricsByFieldNameAndTargetType;
    }
}
