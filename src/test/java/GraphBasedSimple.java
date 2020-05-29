import fschmidt.feature.selection.Classifiers;
import fschmidt.feature.selection.Experiments;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.HorizontalBarPlot;

import java.util.ArrayList;

import static fschmidt.feature.selection.Datasets.*;
import static fschmidt.feature.selection.Experiments.create.newExperiment;
import static fschmidt.feature.selection.Selectors.newAll;
import static fschmidt.feature.selection.Selectors.newGraph;

/**
 * @author kevinstyp
 */
// ----------------------------------------------
//  SIMPLE GRAPH TESTS.
// ----------------------------------------------
// These tests are intended to determine the result
// if a single (best) clique is chosen
public class GraphBasedSimple {
    @Test
    @Tag("Glass")
    @DisplayName("Glass and All Classifier")
    void graphAndallClassifierForGlassData() {
        var stats = new ArrayList<Experiments.Experiment.Statistics>();

        //Classifiers classifier = Classifiers.BASELINE_CLASSIFIER;

        for (Classifiers classifier : Classifiers.values()) {
            stats.add(newExperiment()
                    .withDataSet(IO_SPHERE)
                    //.withSelector(newGraph(0.0, 0.618))
                    .withSelector(newGraph(19.0, 0.9, true))
                    .withClassifier(classifier)
                    .get());
        }

        System.out.println(Experiments.table(stats));
    }
}
