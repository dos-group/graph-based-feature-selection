package fschmidt.feature.selection.correlation;

import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;

import java.util.Arrays;

/**
 * Compute different types of correlation measures for the features.
 * <p>
 * Created by anton on 4/11/16.
 */
public class CorrelationAlgorithm {

    public static final Correlation Pearson = new Correlation() {

        public double correlation(double[] x, double[] y) {
            return new PearsonsCorrelation().correlation(x, y);
        }

        public String toString() {
            return "pearson";
        }
    };

    public static final Correlation Spearmans = new Correlation() {

        public double correlation(double[] x, double[] y) {
            return new SpearmansCorrelation().correlation(x, y);
        }

        public String toString() {
            return "spearmans";
        }
    };

    public static final Correlation Kendalls = new Correlation() {

        public double correlation(double[] x, double[] y) {
            return new KendallsCorrelation().correlation(x, y);
        }

        public String toString() {
            return "kendalls";
        }
    };

    public static final Correlation SimplePearson = new Correlation() {

        public double correlation(double[] xs, double[] ys) {
            //TODO: check here that arrays are not null, of the same length etc

            double sx = 0.0;
            double sy = 0.0;
            double sxx = 0.0;
            double syy = 0.0;
            double sxy = 0.0;

            int n = xs.length;

            for (int i = 0; i < n; ++i) {
                double x = xs[i];
                double y = ys[i];

                sx += x;
                sy += y;
                sxx += x * x;
                syy += y * y;
                sxy += x * y;
            }

            // covariation
            double cov = sxy / n - sx * sy / n / n;
            // standard error of x
            double sigmax = Math.sqrt(sxx / n - sx * sx / n / n);
            // standard error of y
            double sigmay = Math.sqrt(syy / n - sy * sy / n / n);

            // correlation is just a normalized covariation
            return cov / sigmax / sigmay;
        }

        @Override
        public String toString() {
            return "simplePearson";
        }
    };
    private static final String sourceSeparator = " <-> ";

    private final Correlation[] correlations;

    public CorrelationAlgorithm(Correlation... correlations) {
        this.correlations = correlations;
    }

    public CorrelationAlgorithm() {
        this(Pearson, Spearmans, Kendalls);
    }

    public String toString() {
        return "correlation algorithm " + Arrays.toString(correlations);
    }

    public interface Correlation {

        double correlation(double[] x, double y[]);

        String toString();
    }

    public Correlation[] getCorrelations() {
        return correlations;
    }
}
