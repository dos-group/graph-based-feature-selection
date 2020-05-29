
import fschmidt.feature.selection.Datasets;
import tech.tablesaw.api.Table;
import fschmidt.feature.selection.Classifiers;
import fschmidt.feature.selection.Experiments;
import org.apache.commons.math3.util.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;

import java.util.ArrayList;
import java.util.HashMap;

import static fschmidt.feature.selection.Datasets.IO_SPHERE;
import static fschmidt.feature.selection.Experiments.create.newExperiment;
import static fschmidt.feature.selection.Selectors.newGraph;
import static fschmidt.feature.selection.Utils.Text.boldCyan;

/**
 * @author kevinstyp
 */
// ----------------------------------------------
//  SIMPLE GRAPH TESTS.
// ----------------------------------------------
// These tests are intended to determine the result
// if a single (best) clique is chosen
public class GraphBasedTimeMeasurer {
    @Test
    @Tag("Glass")
    @DisplayName("Glass and All Classifier")
    void graphAndallClassifierForGlassData() {
        final int NUMBER_OF_RUNS = 10;
        final int MAX_VERTICES = 22;

        HashMap<Integer, Long[]> vertToTimes = new HashMap<>();

        //var stats = new ArrayList<Experiments.Experiment.Statistics>();

        Classifiers classifier = Classifiers.BASELINE_CLASSIFIER;

        Datasets[] datasets = new Datasets[]{IO_SPHERE, Datasets.WINE};

        for (int vert = 2; vert < MAX_VERTICES; vert++) {
            if(vertToTimes.get(vert) == null){
                vertToTimes.put(vert, new Long[NUMBER_OF_RUNS * datasets.length]);
            }
            for (int i = 0; i < NUMBER_OF_RUNS; i++) {
                for (int j = 0; j < datasets.length; j++) {

                    Experiments.Experiment.Statistics experiment = newExperiment()
                            .withDataSet(IO_SPHERE)
                            //.withSelector(newGraph(0.0, 0.618))
                            .withSelector(newGraph(vert, 0.9, true))
                            .withClassifier(classifier)
                            .get();
                    vertToTimes.get(vert)[i + (j * NUMBER_OF_RUNS)] = experiment.getFeatureSelectionTime();
                }

                //stats.add(experiment);
            }
        }

        System.out.println(vertToTimes.size());

        var vertices  = DoubleColumn.create("Vertices");
        var meanTime  = DoubleColumn.create("Mean-Time");
        var stdDevTime = DoubleColumn.create("StdDev-Time");



        Table myTable = Table.create(boldCyan("Compare Experiments"), vertices, meanTime, stdDevTime);

        for (int vert = 2; vert < MAX_VERTICES; vert++) {
            Long[] longList = vertToTimes.get(vert);
            for (int i = 0; i < longList.length; i++) {
                System.out.print(longList[i] + ", ");
            }
            System.out.println();
            Pair<Double, Double> meanStdDev = calculateSD(longList);
            System.out.println("Vertices: " + vert + ",\t" + meanStdDev.getFirst() + ",\t" + meanStdDev.getSecond());


            vertices.append(Math.round(vert * 1000.0) / 1000.0);
            meanTime.append(Math.round(meanStdDev.getFirst() * 10.0) / 10.0);
            stdDevTime.append(Math.round(meanStdDev.getSecond() * 1.0) / 1.0);

        }

        System.out.println(myTable);

        //System.out.println(Experiments.table(stats));
    }

    public static Pair<Double, Double> calculateSD(Long[] numArray)
    {
        double sum = 0.0, standardDeviation = 0.0;
        int length = numArray.length;
        for(double num : numArray) {
            sum += num;
        }
        double mean = sum/length;
        for(double num: numArray) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        return new Pair<>(mean, Math.sqrt(standardDeviation/length));
    }

}
