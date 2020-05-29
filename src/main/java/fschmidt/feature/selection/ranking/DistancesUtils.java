package fschmidt.feature.selection.ranking;

import weka.core.matrix.DoubleVector;

/**
 *
 * @author fschmidt
 */
public enum  DistancesUtils {
    ;

    // ----------------------------------------------
    //  COMPARABLE HISTOGRAMS.
    // ----------------------------------------------
    static class ComparableHistograms {

        private final double[] histogram1;
        private final double[] histogram2;

        ComparableHistograms(double[] histogram1, double[] histogram2) {
            this.histogram1 = histogram1;
            this.histogram2 = histogram2;
        }

        public double[] getHistogram1() {
            return histogram1;
        }

        public double[] getHistogram2() {
            return histogram2;
        }
    }

    // --------> METHODS

    public static double hamming(double[] a, double[] b) {
        double dist = 0;
        for (int i = 0; i < a.length; ++i) {
            double diff = (a[i] - b[i]);
            dist += Math.abs(diff);
        }
        return dist;
    }

    public static double euclidean(double[] a, double[] b) {
        double dist = 0;
        for (int i = 0; i < a.length; ++i) {
            double diff = (a[i] - b[i]);
            dist += diff * diff;
        }
        return Math.sqrt(dist);
    }

    public static double bhattacharyya(double[] a, double[] b) {
        double dist = 0.0;
        double[] mixedData = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            mixedData[i] = Math.sqrt(a[i] * b[i]);
        }
        for (int i = 0; i < mixedData.length; i++) {
            dist += mixedData[i];
        }
        return dist;
    }

    public static double cosine(double[] inst1, double[] inst2) {
        DoubleVector x = new DoubleVector(inst1);
        DoubleVector y = new DoubleVector(inst2);

        double dotXY = x.times(y).norm1();
        double cosim = dotXY / (x.norm2() * y.norm2());

        return cosim;
    }

    public static double jaccard(double[] inst1, double[] inst2) {
        DoubleVector x = new DoubleVector(inst1);
        DoubleVector y = new DoubleVector(inst2);

        double intersection = 0.0;
        for (int i = 0; i < x.size(); i++) {
            intersection += Math.min(x.get(i), y.get(i));
        }
        if (intersection > 0.0) {
            double union = x.norm1() + y.norm1() - intersection;
            return intersection / union;
        } else {
            return 0.0;
        }
    }

    public static double kullbackLeibler(double[] inst1, double[] inst2) {
        double divergence = 0.0;
        for (int i = 0; i < inst1.length; ++i) {
            if (inst1[i] != 0 && inst2[i] != 0) {
                divergence += inst1[i] * Math.log(inst1[i] / inst2[i]);
            }
        }
        divergence /= Math.log(2);
        return divergence;
    }

    public static double jensenShannon(double[] inst1, double[] inst2) {
        double[] averageInst = new double[inst1.length];
        for (int i = 0; i < inst1.length; i++) {
            averageInst[i] = ((inst1[i] + inst2[i]) / 2);
        }
        double divergence = (kullbackLeibler(inst1, averageInst) + kullbackLeibler(inst2, averageInst)) / 2;
        return divergence;
    }

    public static ComparableHistograms getComparableHistogram(double[] timeseries1, double[] timeseries2) {
        double maxT1 = Double.MIN_VALUE;
        double minT1 = Double.MAX_VALUE;
        for (double value : timeseries1) {
            if (value > maxT1) {
                maxT1 = value;
            }
            if (value < minT1) {
                minT1 = value;
            }
        }
        double maxT2 = Double.MIN_VALUE;
        double minT2 = Double.MAX_VALUE;
        for (double value : timeseries2) {
            if (value > maxT2) {
                maxT2 = value;
            }
            if (value < minT2) {
                minT2 = value;
            }
        }
        double max = Math.max(maxT1, maxT2);
        double min = Math.min(minT1, minT2);

        final int BIN_COUNT = 20;
        final double binSize = (max - min) / BIN_COUNT;
        double[] histogramT1 = new double[BIN_COUNT];
        double[] histogramT2 = new double[BIN_COUNT];

        for (double t : timeseries1) {
            int bin = (int) ((t - min) / binSize);
            if (bin < 0) {
                throw new UnsupportedOperationException("this data is smaller than min");
            } else if (bin > BIN_COUNT) {
                throw new UnsupportedOperationException("this data point is bigger than max ");
            } else if (bin == BIN_COUNT) {
                histogramT2[bin - 1] += 1.0;
            } else {
                histogramT1[bin] += 1.0;
            }
        }

        for (double t : timeseries2) {
            int bin = (int) ((t - min) / binSize);
            if (bin < 0) {
                throw new UnsupportedOperationException("this data is smaller than min");
            } else if (bin > BIN_COUNT) {
                throw new UnsupportedOperationException("this data point is bigger than max ");
            } else if (bin == BIN_COUNT) {
                histogramT2[bin - 1] += 1.0;
            } else {
                histogramT2[bin] += 1.0;
            }
        }
        ComparableHistograms results = new ComparableHistograms(histogramT1, histogramT2);
        return results;
    }
}
