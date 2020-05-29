package datasets;

import fschmidt.feature.selection.Classifiers;
import fschmidt.feature.selection.Datasets;
import fschmidt.feature.selection.FeatureRankers;
import fschmidt.feature.selection.correlation.CorrelationAlgorithm;
import fschmidt.feature.selection.correlation.CorrelationMetricReduction;
import fschmidt.feature.selection.graph.UndirectedGraph;
import fschmidt.feature.selection.graph.WeightedEdge;
import fschmidt.feature.selection.graph.WeightedVertex;
import fschmidt.feature.selection.ranking.FeatureSelectionBinTargetRanking;
import org.apache.commons.math3.util.Pair;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;
import tech.tablesaw.io.Destination;
import tech.tablesaw.io.csv.CsvWriter;
import weka.classifiers.evaluation.Evaluation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum TestUtils {
    ;

    // FOLDERS
    public static final Path TEST_RESOURCE_FOLDER =
            Path.of(System.getProperty("user.dir"))
                    .resolve("src")
                    .resolve("test")
                    .resolve("resources");

    public static final Path SOLUTIONS_FOLDER = TEST_RESOURCE_FOLDER
            .resolve("solutions");

    public static final Path PLOTS_FOLDER = TEST_RESOURCE_FOLDER
            .resolve("plots");

    public static final String BASELINE_CLASSIFIER = ".";

    public static void writeTableToFile(Table table, Path path) {
        try {
            if (Files.notExists(path)) Files.createFile(path);
            var writer = Files.newBufferedWriter(path);
            new CsvWriter().write(table, new Destination(writer));
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------
    //  RESULTS.
    // ----------------------------------------------
    //
    public static class Results {
        final DoubleColumn precisionCol = DoubleColumn.create("Precision");
        final DoubleColumn rmseCol = DoubleColumn.create("RMSE");
        final DoubleColumn averageDistanceCol = DoubleColumn.create("KS-Distance");
        final DoubleColumn averageCorrCol = DoubleColumn.create("Correlation");
        final StringColumn dimensionsCol = StringColumn.create("Dimensions");
        final IntColumn numberDimsCol = IntColumn.create("NumberDimensions");
        final StringColumn classifierCol = StringColumn.create("Classifier");
        final Table table = Table.create("All Results",
                precisionCol,
                rmseCol,
                averageDistanceCol,
                averageCorrCol,
                numberDimsCol,
                dimensionsCol,
                classifierCol);

        public void add(double precision,
                        double rmse,
                        double averageDistance,
                        double averageCorr,
                        List<String> dimensions,
                        Classifiers classifier) {
            precisionCol.append(precision);
            rmseCol.append(rmse);
            averageDistanceCol.append(averageDistance);
            averageCorrCol.append(averageCorr);
            dimensionsCol.append(dimensions.toString());
            numberDimsCol.append(dimensions.size());
            classifierCol.append(classifier.name());
        }

        public Table allRowsWithHighestPrecision() {
            // sort the table to have all the best
            // precisions first. Then get the highest
            // precision value from the first row
            var highestPrecision = (double) table.sortDescendingOn("Precision").get(0, 0);
            // get all rows with the highest precision
            return table.dropWhere(table.numberColumn(0).isNotEqualTo(highestPrecision));
        }
        // All rows with highest precession and
        // minimum number of dimensions
        public Table bestFeatureSets() {
            var rowsHighestPrecision = allRowsWithHighestPrecision()
                    .sortOn("NumberDimensions");
            var minimumNumberOfDimensions = (int) rowsHighestPrecision.get(0, 3);
            return rowsHighestPrecision.dropWhere(rowsHighestPrecision.numberColumn(3).isNotEqualTo(minimumNumberOfDimensions));
        }
    }


    // ----------------------------------------------
    //  SOLUTION SPACE.
    // ----------------------------------------------
    //
    public static class SolutionSpace extends Table {

        SolutionSpace(Path path) throws IOException {
            super();
            addColumns(
                    read().csv(path.toFile())
                            .columns()
                            .toArray(new Column[0])
            );
        }

        private SolutionSpace(Table table) { super();
            addColumns(table.columns().toArray(new Column[0]));
        }

        // All rows with highest precession and
        // minimum number of dimensions
        public SolutionSpace highestPrecisionAndLowestNumberOfFeatures() {
            var rowsHighestPrecision = withHighestPrecision().sortOn("NumberDimensions");
            var minimumNumberOfDimensions = (int) rowsHighestPrecision.get(0, 3);
            return new SolutionSpace(
                    rowsHighestPrecision.dropWhere(
                            rowsHighestPrecision.numberColumn(3)
                                    .isNotEqualTo(minimumNumberOfDimensions))
            );
        }

        // All rows with highest precision
        public SolutionSpace withHighestPrecision() {
            // sort the table to have all the best
            // precisions first. Then get the highest
            // precision value from the first row
            var highestPrecision = highestPrecision();
            // get all rows with the highest precision
            return new SolutionSpace(
                    dropWhere(numberColumn(0)
                            .isNotEqualTo(highestPrecision))
            );
        }

        SolutionSpace sortedByPrecision() { return new SolutionSpace(sortDescendingOn("Precision", "RMSE")); }
        SolutionSpace sortedByRmse() { return new SolutionSpace(sortDescendingOn("RMSE")); }
        SolutionSpace sortedByCorrelation() { return new SolutionSpace(sortDescendingOn("Correlation")); }
        SolutionSpace sortedByDistance() { return new SolutionSpace(sortDescendingOn("KS-Distance")); }
        SolutionSpace sortedByNumDimensions() { return new SolutionSpace(sortOn("NumberDimensions")); }

        double highestPrecision() { return (double) sortedByPrecision().get(0, 0); }
        double highestRmse() { return (double) sortedByRmse().get(0, 0); }
        double highestCorrelation() { return (double) sortedByCorrelation().get(0, 0); }
        double highestDistance() { return (double) sortedByDistance().get(0, 0); }

        SolutionSpace whereClassifier(Classifiers classifier) {
            var select = stringColumn("Classifier")
                    .isEqualTo(classifier.name());
            return new SolutionSpace(where(select));
        }

        SolutionSpace withoutBaseline() {
            var select = stringColumn("Classifier")
                    .isNotEqualTo(BASELINE_CLASSIFIER);
            return new SolutionSpace(where(select));
        }
    }


    // takes the whole dataset
    static Results exploreDataset(Datasets datasets, Classifiers classifiers) {
        var results = new Results();
        var dataset = datasets.get();
        var classifier = classifiers.get();
        var allSets = runAll(dataset.header(), dataset.values(), dataset.groundTruth());
        for (Pair<List<String>, Pair<Double, Double>> pair : allSets) {
            if (!pair.getFirst().isEmpty()) {
                var reduced = dataset.reduceDimsTo(pair.getFirst().toArray(new String[0]));

                double newPrecision = 0.0;
                double rmse = 0.0;
                try {
                    Evaluation eval = new Evaluation(reduced);
                    eval.crossValidateModel(classifier.getClassifier(), reduced, 10, new Random(1337));
                    //System.out.println("Estimated Accuracy: "+ Double.toString(eval.pctCorrect()));
                    newPrecision = eval.pctCorrect();
                    // RMSE!
                    rmse = eval.rootMeanSquaredError();
                    //System.out.println("rmse: " + rmse);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                results.add(newPrecision, rmse, pair.getSecond().getFirst(), pair.getSecond().getSecond(), pair.getFirst(), classifiers);
            }
        }
        return results;
    }

    // takes the whole dataset
    static Results exploreDatasetMonteCarlo(Datasets datasets, Classifiers classifiers, int numberOfRuns) {
        var results = new Results();
        var dataset = datasets.get();
        var classifier = classifiers.get();
        var allSets = runAllMonteCarlo(dataset.header(), dataset.values(), dataset.groundTruth(), numberOfRuns);
        System.out.println("allSets-Size: " + allSets.size());

        //for (Pair<List<String>, Pair<Double, Double>> pair : allSets) {
        for (int i = 0; i < allSets.size(); i++) {
            System.out.println("i: " + i);
            Pair<List<String>, Pair<Double, Double>> pair = allSets.get(i);
            if (!pair.getFirst().isEmpty()) {
                var reduced = dataset.reduceDimsTo(pair.getFirst().toArray(new String[0]));

                double newPrecision = 0.0;
                double rmse = 0.0;
                try {
                    Evaluation eval = new Evaluation(reduced);
                    eval.crossValidateModel(classifier.getClassifier(), reduced, 10, new Random(1337));
                    //System.out.println("Estimated Accuracy: "+ Double.toString(eval.pctCorrect()));
                    newPrecision = eval.pctCorrect();
                    // RMSE!
                    rmse = eval.rootMeanSquaredError();
                    //System.out.println("rmse: " + rmse);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                results.add(newPrecision, rmse, pair.getSecond().getFirst(), pair.getSecond().getSecond(), pair.getFirst(), classifiers);
            }
        }
        return results;
    }

    static List<Pair<List<String>, Pair<Double, Double>>> runAllMonteCarlo(String[] header, List<double[]> values, List<Boolean> groundTruth, int numberOfRuns) {
        FeatureSelectionBinTargetRanking binTargetRanking = new FeatureSelectionBinTargetRanking(FeatureRankers.KS.newInstance(), 0.0, false);
        List<Pair<List<String>, Pair<Double, Double>>> recommendedSetsCorrelations = new ArrayList<>();
        Map<String, Double> rankings = binTargetRanking.run(header, values, groundTruth);
        List<Pair<List<String>, Pair<Double, Double>>> recommendedSets = new ArrayList<>();
        int n = header.length;
        Random random = new Random(System.currentTimeMillis());
        for (long i = 0; i < numberOfRuns; i++) {
            //System.out.println("i: " + i);
            List<String> newHeaders = new ArrayList<>();
            double kolmogorovValue = 0.0;
            for (int j = 0; j < n; j++) {
                if(random.nextInt(2) > 0) {
                    newHeaders.add(header[j]);
                    kolmogorovValue += rankings.get(header[j]);
                }
            }
            kolmogorovValue = kolmogorovValue / newHeaders.size();
            double dummy = 0.0;
            Pair<List<String>, Pair<Double, Double>> newSet = new Pair<>(newHeaders, new Pair<Double, Double>(kolmogorovValue, dummy));
            recommendedSets.add(newSet);
        }
        //fillCorrelations(header, values, rankings, recommendedSetsCorrelations, recommendedSets);
        return recommendedSets;
    }

    // TODO REFACTOR
    static List<Pair<List<String>, Pair<Double, Double>>> runAll(String[] header, List<double[]> values, List<Boolean> groundTruth) {
        FeatureSelectionBinTargetRanking binTargetRanking = new FeatureSelectionBinTargetRanking(FeatureRankers.KS.newInstance(), 0.0, false);
        List<Pair<List<String>, Pair<Double, Double>>> recommendedSetsCorrelations = new ArrayList<>();
        Map<String, Double> rankings = binTargetRanking.run(header, values, groundTruth);
        List<Pair<List<String>, Pair<Double, Double>>> recommendedSets = new ArrayList<>();
        int n = header.length - 15;
        System.out.println("n: " + n);
        for (long i = 0; i < (1l << n); i++) {
            //System.out.println("i: " + i);
            List<String> newHeaders = new ArrayList<>();
            double kolmogorovValue = 0.0;
            for (int j = 0; j < n; j++) {
                if ((i & (1 << j)) > 0) {
                    newHeaders.add(header[j]);
                    kolmogorovValue += rankings.get(header[j]);
                }
            }
            kolmogorovValue = kolmogorovValue / newHeaders.size();
            Pair<List<String>, Pair<Double, Double>> newSet = new Pair<>(newHeaders, new Pair<Double, Double>(kolmogorovValue, null));
            recommendedSets.add(newSet);
        }
        fillCorrelations(header, values, rankings, recommendedSetsCorrelations, recommendedSets);
        return recommendedSets;
    }

    // TODO REFACTOR
    static void fillCorrelations(String[] header,
                                 List<double[]> values,
                                 Map<String, Double> rankings,
                                 List<Pair<List<String>, Pair<Double, Double>>> recommendedSetsCorrelations,
                                 List<Pair<List<String>, Pair<Double, Double>>> recommendedSets) {
        UndirectedGraph totalGraph = new UndirectedGraph();
        for (String headerS : header) {
            totalGraph.addVertex(new WeightedVertex(headerS));
        }
        //Calculate correlation between all different pairs of metrics
        CorrelationAlgorithm.Correlation keyCorrelationMethod = CorrelationMetricReduction.Pearson;
        for (int i = 0; i < header.length; i++) {
            for (int j = 0; j < header.length; j++) {
                if (i != j && rankings.containsKey(header[i]) && rankings.containsKey(header[j])) {
                    double[] vector1 = new double[values.size()];
                    double[] vector2 = new double[values.size()];
                    for (int s = 0; s < values.size(); s++) {
                        vector1[s] = values.get(s)[i];
                        vector2[s] = values.get(s)[j];
                    }
                    if (totalGraph.containsVertex(header[i]) && totalGraph.containsVertex(header[j])) {
                        double corrResult = keyCorrelationMethod.correlation(vector1, vector2);
                        WeightedEdge e = totalGraph.addEdge(totalGraph.getVertex(header[i]), totalGraph.getVertex(header[j]));
                        if (e == null) continue; //Edge already existed
                        e.addProperty(keyCorrelationMethod.toString(), corrResult);
                    }
                }
            }
        }
        for (Pair<List<String>, Pair<Double, Double>> recommendedSet : recommendedSets) {
            double averageCorrelation = totalGraph.getAverageCorrelation(recommendedSet.getKey(), keyCorrelationMethod.toString());
            recommendedSetsCorrelations.add(new Pair<>(recommendedSet.getKey(), new Pair<Double, Double>(recommendedSet.getSecond().getFirst(), averageCorrelation)));
            //recommendedSet = new Pair<>(recommendedSet.getKey(), new Pair<Double, Double>(recommendedSet.getSecond().getFirst(), averageCorrelation));
        }
    }
}
