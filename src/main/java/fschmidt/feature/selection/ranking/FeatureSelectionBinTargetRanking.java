package fschmidt.feature.selection.ranking;

import fschmidt.feature.selection.Datasets.Dataset;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.List;

// ----------------------------------------------
//  FEATURE SELECTION BIN TARGET RANKING.
// ----------------------------------------------
// Here the BinTargetFeatureRanker (usually the
// distance function) and the data set are brought
// together. First, the data set is converted to
// the format described in BinTargetFeatureValues
// and then passed to the BinTargetFeatureRanker.
// The result is a distance value to each dimension
// that expresses how far away (for each dimension)
// the values that are false and true are. The
// tuples [dimension, distance] in the result are
// ordered by their distance. The filterProportion
// is a threshold all distances smaller than
// this value are discarded.
//
// @author fschmidt, Alex (16.05.2017)
//
public class FeatureSelectionBinTargetRanking {

    private final double filterProportion;
    private final BinTargetFeatureRanker<?> ranker;
    private final BinTargetFeatureValues featureValues;
    private final boolean newFilter;

    public FeatureSelectionBinTargetRanking(BinTargetFeatureRanker<?> _ranker, double _filterProportion, boolean newFilter) {
        ranker = _ranker;
        filterProportion = _filterProportion;
        this.newFilter = newFilter;
        featureValues = new BinTargetFeatureValues();
    }

    // TODO Maybe integrate the BinTargetFeatureValues as a transformation into Dataset because it is basically just a different representation
    public Map<String, Double> run(Dataset dataset) {
        return run(dataset.header(), dataset.values(), dataset.groundTruth());
    }

    // TODO I think we should use the Dataset here
    // TODO use Dataset and delete
    public Map<String, Double> run(String[] header, List<double[]> values, List<Boolean> groundTruth) {

        for (int i = 0; i < values.size(); i++) {
            featureValues.update(groundTruth.get(i), header, values.get(i));
        }

        var rankedResultMap = ranker.rank(featureValues);
        int numberOfSamplesSent;
        if(newFilter){
            numberOfSamplesSent = calculateSize(rankedResultMap, filterProportion);
        }
        else{
            // Old variant with proportional subtraction
            numberOfSamplesSent = rankedResultMap.size() - (int) (rankedResultMap.size() * filterProportion);
        }

        return rankedResultMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(numberOfSamplesSent)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    private int calculateSize(Map<String, Double> rankedResultMap, double filterProportion) {
        int maxElements = (int) filterProportion;
        final double minimumDistance = 0.1;
        //Filter elements higher than 0.1 and up to maxElements
        //TODO Could possibly be done smarter than building the list only to count it's size... (done is better than good :P)
        return rankedResultMap.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .filter(entry -> entry.getValue() > minimumDistance)
                .limit(maxElements)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new))
                .size();
    }
}
