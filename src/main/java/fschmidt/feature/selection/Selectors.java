package fschmidt.feature.selection;

import fschmidt.feature.selection.Datasets.Dataset;
import fschmidt.feature.selection.Utils.Self;
import fschmidt.feature.selection.correlation.CorrelationAlgorithm;
import fschmidt.feature.selection.correlation.CorrelationMetricReduction;
import fschmidt.feature.selection.graph.UndirectedGraph;
import fschmidt.feature.selection.graph.WeightedEdge;
import fschmidt.feature.selection.graph.WeightedVertex;
import fschmidt.feature.selection.ranking.FeatureSelectionBinTargetRanking;
import org.apache.commons.math3.util.Pair;
import weka.attributeSelection.*;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

// ----------------------------------------------
//  FEATURE SELECTIONS.
// ----------------------------------------------
//
public enum Selectors {
    ;

    // FACTORIES
    public static WekaFeatureSelection newWekaGreedy() {
        return new WekaFeatureSelection(new GreedyStepwise(), new CfsSubsetEval());
    }

    public static WekaFeatureSelection newWekaRankerGain() {
        Ranker ranker = new Ranker();
        ranker.setThreshold(0.0);
        return new WekaFeatureSelection(ranker, new GainRatioAttributeEval());
    }

    public static GraphFeatureSelection newGraph(double _filterProportion, double _maxCorrelation, boolean newFilter) {
        return new GraphFeatureSelection(_filterProportion, _maxCorrelation, newFilter);
    }

    public static GraphFeatureSelection newGraph(double _filterProportion, double _maxCorrelation) {
        return new GraphFeatureSelection(_filterProportion, _maxCorrelation, false);
    }

    public static GraphFeatureSelection newGraphDefault() {
        return new GraphFeatureSelection(0.3, 0.9, false);
    }

    public static AllFeatureSelection newAll() {
        return new AllFeatureSelection();
    }

    public static OptimalFeatureSelection newOptimal() {
        return new OptimalFeatureSelection();
    }

    // ----------------------------------------------
    //  BINARY FEATURE SELECTOR INTERFACE.
    // ----------------------------------------------
    //
    public interface Selector<T extends Selector<T>> extends Function<Dataset, String[]>, Self<T> {

        default String name() { return self().getClass().getSimpleName(); }
    }


    // ----------------------------------------------
    //  OPTIMAL SELECTION.
    // ----------------------------------------------
    //
    public static class OptimalFeatureSelection implements Selector<OptimalFeatureSelection> {

        public String[] apply(Dataset dataset) {

            var header = dataset.header();

            var values = dataset.values();
//
//            var groundTruth = dataset.groundTruth();

            List<Pair<List<String>, Double>> recommendedSetsCorrelations = new ArrayList<>();

            var binTargetRanking = new FeatureSelectionBinTargetRanking(FeatureRankers.KS.newInstance(), 0.0, false);

            Map<String, Double> rankings = binTargetRanking.run(dataset);

            String[] allRankings = rankings.keySet().toArray(new String[0]);

            for (int i = 0; i < allRankings.length; i++) {

                System.out.println("Ranking: " + allRankings[i] + " = " + rankings.get(allRankings[i]));
            }

            System.out.println("Retrieved " + rankings.size() + " features after Kolmogorov-Smirnov filtering.");

            ArrayList<Pair<ArrayList<String>, Double>> recommendedSets = new ArrayList<>();

            int n = header.length;

            for (int i = 0; i < (1 << n); i++) {

                ArrayList<String> newHeaders = new ArrayList<>();
                double kolmogorovValue = 0.0;
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) > 0) {
                        newHeaders.add(header[j]);
                        kolmogorovValue += rankings.get(header[j]);
                    }
                }
                kolmogorovValue = kolmogorovValue / newHeaders.size();
                Pair<ArrayList<String>, Double> newSet = new Pair<>(newHeaders, kolmogorovValue);
                recommendedSets.add(newSet);
            }


            //fillCorrelations(header, values, rankings, recommendedSetsCorrelations, recommendedSets);

            return new String[0];
        }
    }

    // ----------------------------------------------
    //  ALL FEATURE SELECTION.
    // ----------------------------------------------
    //
    public static class AllFeatureSelection implements Selector<AllFeatureSelection> {
        public String[] apply(Dataset instances) {
            return instances.header();
        }
    }


    // ----------------------------------------------
    //  GRAPH BASED FEATURE SELECTION.
    // ----------------------------------------------
    public static class GraphFeatureSelection implements Selector<GraphFeatureSelection> {
        final double filterProportion;
        final double maxCorrelation;
        final boolean newFilter;

        private GraphFeatureSelection(double _filterProportion, double _maxCorrelation, boolean newFilter) {
            filterProportion = _filterProportion;
            maxCorrelation = _maxCorrelation;
            this.newFilter = newFilter;
        }

        public String[] apply(Dataset dataset) {
            var header = dataset.header();
            var values = dataset.values();
            var ground = dataset.groundTruth();
            var binTargetRankings =
                    new FeatureSelectionBinTargetRanking(FeatureRankers.KS.newInstance(), filterProportion, newFilter)
                            .run(header, values, ground);
            var recommended =
                    new CorrelationMetricReduction(CorrelationMetricReduction.Pearson, maxCorrelation)
                            .run(binTargetRankings, header, values);

            // TODO
            System.out.println("RECOMMENDED (" + recommended.iterator().next().getFirst().toArray(new String[0]).length + "): " + recommended);
            //System.out.println(recommended.stream().filter(a -> a.getValue() > 0).findFirst().orElseThrow());
            //recommended.stream().filter(a -> a.getValue() > 0).findFirst().orElseThrow();
            //System.out.println("recommended.iterator().next().getFirst().toArray(new String[0])-size: " +
            //        recommended.iterator().next().getFirst().toArray(new String[0]).length);
            return recommended.iterator().next().getFirst().toArray(new String[0]);
        }
    }


    // ----------------------------------------------
    //  WEKA FEATURE SELECTION.
    // ----------------------------------------------
    //
    public static class WekaFeatureSelection implements Selector<WekaFeatureSelection> {
        private final ASEvaluation eval;
        private final ASSearch search;

        private WekaFeatureSelection(ASSearch _search, ASEvaluation _eval) {
            search = _search;
            eval = _eval;
        }

        public String[] apply(Dataset dataset) {
            try {
                String[] identifiedFeatures = filter(dataset);
                System.out.println("Number of identified Features: " + identifiedFeatures.length);
                return identifiedFeatures;
            }
            catch (Exception ex) {
                Logger.getLogger(WekaFeatureSelection.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }

        private String[] filter(Instances data) throws Exception {
            AttributeSelection filter = new AttributeSelection();
            filter.setEvaluator(eval);
            filter.setSearch(search);
            filter.setInputFormat(data);
            // generate new data
            Instances newData = Filter.useFilter(data, filter);
            int classIndex = newData.classIndex();
            String[] filtered = new String[newData.numAttributes() - 1];
            for (int i = 0; i < newData.numAttributes(); i++) {
                if (i != classIndex) {
                    filtered[i] = newData.attribute(i).name();
                }
            }
            return filtered;
        }
    }
}
