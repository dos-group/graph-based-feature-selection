package fschmidt.feature.selection;

import fschmidt.feature.selection.Classifiers.Classifier;
import fschmidt.feature.selection.Datasets.Dataset;
import fschmidt.feature.selection.Experiments.Experiment.Statistics;
import fschmidt.feature.selection.Selectors.Selector;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;
import weka.classifiers.evaluation.Evaluation;
import weka.classifiers.evaluation.Prediction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static fschmidt.feature.selection.Utils.Text.*;
import static fschmidt.feature.selection.Utils.Tuples.*;

// ----------------------------------------------
//  EXPERIMENTS.
// ----------------------------------------------
//
public enum  Experiments {
    ;

    // ----------------------------------------------
    //  EXPERIMENT.
    // ----------------------------------------------
    //
    public static class Experiment implements Supplier<Statistics> {
        final Dataset dataset;
        final Selector<?> selector;
        final Classifier classifier;

        private Experiment(Dataset _dataset, Selector _selector, Classifier _classifier) {
            dataset = _dataset;
            selector = _selector;
            classifier = _classifier;
        }

        public Statistics get() {
            // Dimensions selected by the Feature Selector.
            long time1 = System.currentTimeMillis();
            var selectedDims = selector.apply(dataset);
            long time2  = System.currentTimeMillis();
            long featureSelectionTime = time2 - time1;
            System.out.println("Needed time for feature selection: " + featureSelectionTime + "ms");
            // The dataset reduced to the dimensions
            // selected by the Feature Selector.
            var reducedData = dataset.reduceDimsTo(selectedDims);
            // Splitting the dataset into a test dataset set and
            // a training dataset set
            //var splitData = reducedData.split();
            // Train & predict
            //var precision = classifier.train(splitData._1).predict(splitData._2);

            //10-fold cross validation
            // reducedData - Data
            // Classifier is the classifier

            //Training instances are held in "originalTrain"
            double newPrecision = 0.0;
            double rmse = 0.0;
            try {
                Evaluation eval = new Evaluation(reducedData);
                eval.crossValidateModel(classifier.getClassifier(), reducedData, 10, new Random(1337));
                newPrecision = eval.pctCorrect();
                // RMSE!
                rmse = eval.rootMeanSquaredError();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Return new Statistics
            return new Statistics(selectedDims, newPrecision, rmse, featureSelectionTime);
        }

        // ----------------------------------------------
        //  STATISTICS.
        // ----------------------------------------------
        // A statistic is always the result of an experiment.
        // Statistics are comparable, can be plotted and
        // displayed on the console.
        public class Statistics {
            final String[] dimensions;
            final double precision;
            final double rmse;
            final long featureSelectionTime;

            private Statistics(String[] _dimensions, double _precision, double _rmse, long _featureSelectionTime) {
                dimensions = _dimensions;
                precision = _precision;
                rmse = _rmse;
                featureSelectionTime = _featureSelectionTime;
            }

            Dataset dataset() { return dataset; }
            Selector selector() { return selector; }
            Classifier classifier() { return classifier; }
            String selectedDimensions() { return Arrays.toString(dimensions); }

            public String toString() {
                return "++ ================================================== ++" + "\n" +
                Stream.of(of("Dataset:       ", boldRed(dataset.name())),
                          of("Selector:      ", lightBlue(selector.name())),
                          of("Classifier:    ", lightGreen(classifier.name())),
                          of("Selected Dims: ", lightBlack(selectedDimensions())),
                          of("Precision:     ", boldRed(precision)),
                          of("RMSE:     ", boldRed(rmse)),
                          of("Feature-Selection-Time:     ", boldRed(featureSelectionTime)))
                           .map(t -> blue(t._1) + t._2 + "\n")
                           .reduce((a,b) -> a + b)
                           .orElseThrow();
            }

            public long getFeatureSelectionTime(){
                return featureSelectionTime;
            }

            public void print() { System.out.println(toString()); }
        }
    }

    // ----------------------------------------------
    //  BUILDER.
    // ----------------------------------------------
    // Type safe builder pattern for Experiments.
    // Guarantees that an experiment always consists
    // of a Dataset, a FeatureSelector and a
    // Classifier. The result of an experiment is
    // always a statistic.
    public enum create {
        ;
        public interface DatasetBuilder { SelectorBuilder withDataSet(Datasets dataSet); }

        public interface SelectorBuilder { ClassifierBuilder withSelector(Selector selector);}

        public interface ClassifierBuilder { Experiment withClassifier(Classifiers classifier); }

        public static DatasetBuilder newExperiment() { return d -> s -> c -> new Experiment(d.get(), s, c.get()); }
    }

    public static Table table(List<Statistics> statistics) {
        return table(statistics.toArray(new Statistics[0]));
    }

    public static Table table(Statistics... statistics) {
        var dataset    = StringColumn.create("Dataset");
        var selector   = StringColumn.create("Selector");
        var classifier = StringColumn.create("Classifier");
        var precision  = DoubleColumn.create("Precision");
        var rmse  = DoubleColumn.create("RMSE");
        var featureSelectionTime = DoubleColumn.create("Feature-Selection-Time");

        for (Statistics stat : statistics) {
            dataset.append(stat.dataset().name());
            selector.append(stat.selector().name());
            classifier.append(stat.classifier().name());
            precision.append(Math.round(stat.precision * 1000.0) / 1000.0);
            rmse.append(Math.round(stat.rmse * 100.0) / 100.0);
            featureSelectionTime.append(stat.featureSelectionTime);
        }

        return Table.create(boldCyan("Compare Experiments"), dataset, selector, classifier, precision, rmse, featureSelectionTime);
    }
}

