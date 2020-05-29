import fschmidt.feature.selection.Classifiers;
import fschmidt.feature.selection.Experiments;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static fschmidt.feature.selection.Datasets.*;
import static fschmidt.feature.selection.Experiments.create.newExperiment;
import static fschmidt.feature.selection.Selectors.*;

/**
 * @author kevinstyp
 */
// ----------------------------------------------
//  SIMPLE WEKA TESTS.
// ----------------------------------------------
// These tests are intended to determine the result
// of the weka feature selections
public class WekaBased {
    @Test
    @Tag("Glass")
    @DisplayName("Glass and RankerGain")
    void wekaRankerGainForGlassData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(GLASS)
                    .withSelector(newWekaRankerGain())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Glass")
    @DisplayName("Glass and GreedyGain Classifier")
    void wekaGreedForGlassData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(GLASS)
                    .withSelector(newWekaGreedy())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Wine")
    @DisplayName("Wine and RankerGain")
    void wekaRankerGainForWineData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(WINE)
                    .withSelector(newWekaRankerGain())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Wine")
    @DisplayName("Wine and GreedyGain Classifier")
    void wekaGreedForWineData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(WINE)
                    .withSelector(newWekaGreedy())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Shuttle")
    @DisplayName("Shuttle and RankerGain")
    void wekaRankerGainForShuttleData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(SHUTTLE)
                    .withSelector(newWekaRankerGain())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Shuttle")
    @DisplayName("Shuttle and GreedyGain Classifier")
    void wekaGreedForShuttleData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(SHUTTLE)
                    .withSelector(newWekaGreedy())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Ionosphere")
    @DisplayName("Ionosphere and RankerGain")
    void wekaRankerGainForIonosphereData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(IO_SPHERE)
                    .withSelector(newWekaRankerGain())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }

    @Test
    @Tag("Ionosphere")
    @DisplayName("Ionosphere and GreedyGain Classifier")
    void wekaGreedForIonosphereData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(IO_SPHERE)
                    .withSelector(newWekaGreedy())
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }
}
