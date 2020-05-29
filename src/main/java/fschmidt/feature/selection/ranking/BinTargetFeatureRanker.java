package fschmidt.feature.selection.ranking;

import fschmidt.feature.selection.Utils.Self;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

// ----------------------------------------------
//  BIN TARGET FEATURE RANKER.
// ----------------------------------------------
// Calculates for each dimension the distance
// between the values assigned True and the values
// assigned False.
//
// @author fschmidt
//
public interface BinTargetFeatureRanker<T extends BinTargetFeatureRanker<T>> extends Self<T> {

    Logger logger = Logger.getLogger(BinTargetFeatureRanker.class.getName());

    /**
     * Calculation of the p-values for each metric type.
     *
     * @param binTargetFeatureValues
     * @return {@link Map} of metric identifiers as key and p-values as values. {@code null} on fail.
     */
    default Map<String, Double> rank(BinTargetFeatureValues binTargetFeatureValues) {
        //p-Values which will be returned
        Map<String, Double> pValues = new HashMap<>();

        Map<Boolean, Map<String, List<Double>>> values =
                binTargetFeatureValues.getSampleMetricsByFieldNameAndTargetType();

        //Get respective target maps
        Map<String, List<Double>> mapTrue = values.get(true);
        Map<String, List<Double>> mapFalse = values.get(false);

        if (mapTrue == null || mapFalse == null || mapTrue.isEmpty() || mapFalse.isEmpty()) {
            logger.warning("Kolmogorov-Smirnov-Test cannot be executed. Either normal or anomaly" + " collection empty.");
            return null;
        }
        Set<String> keys = mapTrue.keySet();

        //Create double arrays for the test
        double[] d1 = new double[mapTrue.get(mapTrue.keySet().iterator().next()).size()];
        double[] d2 = new double[mapFalse.get(mapTrue.keySet().iterator().next()).size()];

        for (String key : keys) {
            for (int i = 0; i < mapTrue.get(key).size(); i++) {
                d1[i] = mapTrue.get(key).get(i);
            }
            for (int i = 0; i < mapFalse.get(key).size(); i++) {
                d2[i] = mapFalse.get(key).get(i);
            }
            pValues.put(key, getDistance(d1, d2));
        }

        return pValues;
    }

    default String getName() { return self().getClass().getSimpleName(); };

    double getDistance(double[] d1, double[] d2);
}
