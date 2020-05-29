package fschmidt.feature.selection;

import fschmidt.feature.selection.ranking.BinTargetFeatureRanker;
import fschmidt.feature.selection.ranking.Distances.KolmogorovSmirnovDistance;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fschmidt.feature.selection.ranking.Distances.*;

/**
 * @author fschmidt, Alex (16.05. 2017)
 */
public enum FeatureRankers {
    KS(KolmogorovSmirnovDistance.class),
    BHATTACHARYYA(BhattacharyyaDistance.class),
    CHEBYSHEV(ChebyshevDistance.class),
    COSINE(CosineSimilarity.class),
    EUCLIDEAN(EuclideanDistance.class),
    HAMMING(HammingDistance.class),
    JACCARD(JaccardDistance.class),
    JS(JensenShannonDistance.class),
    MANHATTAN(ManhattanDistance.class),
    WASSERSTEIN(WassersteinDistance.class),
    CANBERRA(CanberraDistance.class),
    KL(KullbackLeiblerDivergence.class);

    private final Class ranker;

    FeatureRankers(Class ranker) { this.ranker = ranker; }

    public BinTargetFeatureRanker newInstance() {
        try {
            return (BinTargetFeatureRanker) ranker.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException | NoSuchMethodException | InvocationTargetException ex) {
            Logger.getLogger(FeatureRankers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public String getName() {
        try {
            return ((BinTargetFeatureRanker) ranker.newInstance()).getName();
        } catch (InstantiationException | IllegalAccessException | SecurityException | IllegalArgumentException ex) {
            Logger.getLogger(FeatureRankers.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
