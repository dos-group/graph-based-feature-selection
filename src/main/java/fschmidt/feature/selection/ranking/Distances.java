package fschmidt.feature.selection.ranking;

import org.apache.commons.math3.ml.distance.EarthMoversDistance;
import org.apache.commons.math3.stat.inference.KolmogorovSmirnovTest;

// ----------------------------------------------
//  DISTANCES.
// ----------------------------------------------
// A collection of different distance functions
//
// @author fschmidt
//
public enum Distances {
    ;

    // ----------------------------------------------
    //  BHATTACHARYY DISTANCE.
    // ----------------------------------------------
    public static class BhattacharyyaDistance implements BinTargetFeatureRanker<BhattacharyyaDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.bhattacharyya(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  CANBERRA DISTANCE.
    // ----------------------------------------------
    public static class CanberraDistance implements BinTargetFeatureRanker<CanberraDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var calc = new org.apache.commons.math3.ml.distance.CanberraDistance();
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return calc.compute(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  CHEBYSHEV DISTANCE.
    // ----------------------------------------------
    public static class ChebyshevDistance implements BinTargetFeatureRanker<ChebyshevDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var calc = new org.apache.commons.math3.ml.distance.ChebyshevDistance();
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return calc.compute(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  COSINE DISTANCE.
    // ----------------------------------------------
    public static class CosineSimilarity implements BinTargetFeatureRanker<CosineSimilarity> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.cosine(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  EUCLIDEAN DISTANCE.
    // ----------------------------------------------
    public static class EuclideanDistance implements BinTargetFeatureRanker<EuclideanDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.euclidean(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  HAMMING DISTANCE.
    // ----------------------------------------------
    public static class HammingDistance implements BinTargetFeatureRanker<HammingDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.hamming(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  JACCARD DISTANCE.
    // ----------------------------------------------
    public static class JaccardDistance implements BinTargetFeatureRanker<JaccardDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.jaccard(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  JENSEN SHANNON DISTANCE.
    // ----------------------------------------------
    public static class JensenShannonDistance implements BinTargetFeatureRanker<JensenShannonDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.jensenShannon(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  KOLMOGOROV SMIRNOV DISTANCE.
    // ----------------------------------------------
    public static class KolmogorovSmirnovDistance implements BinTargetFeatureRanker<KolmogorovSmirnovDistance> {
        private final KolmogorovSmirnovTest test = new KolmogorovSmirnovTest();
        public double getDistance(double[] d1, double[] d2) { return test.kolmogorovSmirnovStatistic(d1, d2); }
    }

    // ----------------------------------------------
    //  KULLBACK LEIBLER DIVERGENCE.
    // ----------------------------------------------
    public static class KullbackLeiblerDivergence implements BinTargetFeatureRanker<KullbackLeiblerDivergence> {
        public double getDistance(double[] d1, double[] d2) {
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return DistancesUtils.kullbackLeibler(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  MANHATTAN DISTANCE.
    // ----------------------------------------------
    public static class ManhattanDistance implements BinTargetFeatureRanker<ManhattanDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var calc = new org.apache.commons.math3.ml.distance.ManhattanDistance();
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return calc.compute(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }

    // ----------------------------------------------
    //  WASSERSTEIN DISTANCE.
    // ----------------------------------------------
    public static class WassersteinDistance implements BinTargetFeatureRanker<WassersteinDistance> {
        public double getDistance(double[] d1, double[] d2) {
            var calc = new EarthMoversDistance();
            var histograms = DistancesUtils.getComparableHistogram(d1, d2);
            return calc.compute(histograms.getHistogram1(), histograms.getHistogram2());
        }
    }
}
