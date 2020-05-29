import fschmidt.feature.selection.Classifiers;
import fschmidt.feature.selection.Experiments;
import fschmidt.feature.selection.Experiments.Experiment.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.HorizontalBarPlot;

import java.util.ArrayList;

import static fschmidt.feature.selection.Datasets.*;
import static fschmidt.feature.selection.Experiments.create.newExperiment;
import static fschmidt.feature.selection.Selectors.newAll;

// ----------------------------------------------
//  BASELINE TESTS.
// ----------------------------------------------
// These tests are intended to determine the core
// metric for each dataset.
class BaseLines {

    @Test
    @Tag("Glass")
    @DisplayName("All Features with Glass and All Classifiers")
    void allFeatureSelectionForGlassData() {

        var stats = new ArrayList<Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(GLASS)
                    .withSelector(newAll())
                    .withClassifier(classifier)
                    .get());
        }
        System.out.println(Experiments.table(stats));
        Plot.show(
                HorizontalBarPlot.create("All Features with Glass and All Classifiers",
                        Experiments.table(stats), "Classifier", "Precision"));
    }

    @Test
    @Tag("IONOSPHERE")
    @DisplayName("All Features with Ionosphere and All Classifiers")
    void allFeatureSelectionForIonosphereData() {

        var stats = new ArrayList<Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(IO_SPHERE)
                    .withSelector(newAll())
                    .withClassifier(classifier)
                    .get());
        }
        System.out.println(Experiments.table(stats));
        Plot.show(
                HorizontalBarPlot.create("All Features with Ionosphere and All Classifiers",
                        Experiments.table(stats), "Classifier", "Precision"));
    }

    @Test
    @Tag("WINE")
    @DisplayName("All Features with Wine and All Classifiers")
    void allFeatureSelectionForWineData() {

        var stats = new ArrayList<Statistics>();

        for (Classifiers classifier : Classifiers.values()) {

            stats.add(newExperiment()
                    .withDataSet(WINE)
                    .withSelector(newAll())
                    .withClassifier(classifier)
                    .get());
        }
        System.out.println(Experiments.table(stats));
        Plot.show(
                HorizontalBarPlot.create("All Features with Wine and All Classifiers",
                        Experiments.table(stats), "Classifier", "Precision"));
    }

    @Test
    @Tag("SHUTTLE")
    @DisplayName("All Features with Shuttle and All Classifiers")
    void allFeatureSelectionForShuttleData() {

        var stats = new ArrayList<Statistics>();

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(SHUTTLE)
                    .withSelector(newAll())
                    .withClassifier(classifier)
                    .get());
        }
        System.out.println(Experiments.table(stats));
        Plot.show(
                HorizontalBarPlot.create("All Features with Shuttle and All Classifiers",
                        Experiments.table(stats), "Classifier", "Precision"));
    }
}
