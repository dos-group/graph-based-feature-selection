package datasets;


import datasets.TestUtils.SolutionSpace;
import fschmidt.feature.selection.Classifiers;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.components.Layout.LayoutBuilder;
import tech.tablesaw.plotly.components.Marker;
import tech.tablesaw.plotly.traces.Scatter3DTrace;

import java.nio.file.Path;

// ----------------------------------------------
//  EXPLORE - DATASET.
// ----------------------------------------------
// this class is a collection of functions used
// to examine a dataset.
enum Explore {
    ;
    // ----------------------------------------------
    //  BEST SOLUTIONS.
    // ----------------------------------------------
    // A few methods that search the solution room and
    // return the best solutions. These serve as
    // comparative values for all further tests.
    enum BestSolutions {
        ;
        // searches the solution space for the value
        // with the highest precision and returns
        // all combinations of features with this
        // highest precision value regardless of the
        // classifier.
        static void bestSolutionsForAll(SolutionSpace solutions) {
            var result = solutions
                    .withHighestPrecision()
                    .sortedByNumDimensions();
            System.out.println(result.printAll());
        }
        // Does the same as the method above only that
        // here is filtered by a particular classifier.
        static void bestSolutionsForClassifier(String classifier, SolutionSpace solutions) {
            var result = solutions
                    .whereClassifier(Classifiers.valueOf(classifier))
                    .withHighestPrecision()
                    .sortedByNumDimensions();
            System.out.println(result.printAll());
        }
    }

    // ----------------------------------------------
    //  DISTRIBUTIONS.
    // ----------------------------------------------
    // This class is a collection of different methods
    // that allow to display the distribution of the
    // different n-tuples of features in terms of
    // their average correlation, precision and distance.
    enum Distributions {
        ;
        // ----------------------------------------------
        //  CORRELATION X PRECISION.
        // ----------------------------------------------
        // Collection of visualizations that illustrate
        // the distribution of the correlation with respect
        // to precision.
        enum CorrelationPrecision {
            ;
            // Paint a scatter plot for all n tuples of
            // features for precision and correlation.
            // The classifier is ignored.
            static void all(SolutionSpace solutions, Path plotFolder) {
                var plot = ScatterPlot.create("All without Baseline",
                                solutions.withoutBaseline(),
                                "Correlation",
                                "Precision");
                var name = "CorrelationPrecisionAll";
                Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
            }
            // The same as above, only for a special classifier.
            static void forClassifier(String classifier, SolutionSpace solutions, Path plotFolder) {
                var plot = ScatterPlot.create(classifier,
                                solutions.whereClassifier(Classifiers.valueOf(classifier)),
                                "Correlation",
                                "Precision");
                var name = "CorrelationPrecisionFor" + classifier;
                Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
            }
        }

        // ----------------------------------------------
        //  DISTANCE X PRECISION.
        // ----------------------------------------------
        // Collection of visualizations that illustrate
        // the distribution of the distance with respect
        // to precision.
        enum DistancePrecision {
            ;
            // Paint a scatter plot for all n tuples of
            // features for distance and correlation.
            // The classifier is ignored.
            static void all(SolutionSpace solutions, Path plotFolder) {
                var plot = ScatterPlot.create("All without Baseline",
                                solutions.withoutBaseline(),
                                "KS-Distance",
                                "Precision");
                var name = "DistancePrecisionAll";
                Plot.show(plot, name, plotFolder.resolve(name + "All.html").toFile());
            }
            // The same as above, only for a special classifier.
            static void forClassifier(String classifier, SolutionSpace solutions, Path plotFolder) {
                var plot = ScatterPlot.create(classifier,
                                solutions.whereClassifier(Classifiers.valueOf(classifier)),
                                "KS-Distance",
                                "Precision");
                var name = "DistancePrecisionFor" + classifier;
                Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
            }
        }

        // ----------------------------------------------
        //  DISTANCE X CORRELATION.
        // ----------------------------------------------
        // Collection of visualizations that illustrate
        // the distribution of the distance with respect
        // to correlation.
        enum  DistanceCorrelation {
            ;
            // Paint a scatter plot for all n tuples of
            // features for distance and correlation.
            // The classifier is ignored.
            static void all(SolutionSpace solutions, Path plotFolder) {
                var plot =
                        ScatterPlot.create("All without Baseline",
                                solutions.withoutBaseline(),
                                "KS-Distance",
                                "Correlation");
                var name = "DistanceCorrelationAll";
                Plot.show(plot, name, plotFolder.resolve(name + "All.html").toFile());
            }
            // The same as above, only for a special classifier.
            static void forClassifier(String classifier, SolutionSpace solutions, Path plotFolder) {
                var plot =
                        ScatterPlot.create(classifier,
                                solutions.whereClassifier(Classifiers.valueOf(classifier)),
                                "KS-Distance",
                                "Correlation");
                var name = "DistanceCorrelationFor" + classifier;
                Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
            }
        }

        // ----------------------------------------------
        //  DISTANCE X PRECISION X CORRELATION
        // ----------------------------------------------
        // Collection of visualizations that illustrate
        // the distribution of the distance x correlation
        // with respect to precision.
        enum  DistanceCorrelationPrecision {
            ;
            static final LayoutBuilder layout = Layout.builder()
                    .height(1000)
                    .width(1200)
                    .showLegend(true);
            //
            static void all(SolutionSpace solutions, Path plotFolder) {
                var solutionsWithoutBaseline = solutions.withoutBaseline();
                var trace = Scatter3DTrace.builder(
                        solutionsWithoutBaseline.doubleColumn("KS-Distance"),
                        solutionsWithoutBaseline.doubleColumn("Correlation"),
                        solutionsWithoutBaseline.doubleColumn("Precision"))
                        .marker(Marker.builder()
                                .color("red")
                                .size(2)
                                .build())
                        .build();
                var name = "DistanceCorrelationPrecisionAll";
                var figure = new Figure(layout.title("All without Baseline").build(), trace);
                Plot.show(figure, name, plotFolder.resolve(name + "All.html").toFile());
            }
            //
            static void forClassifier(String classifier, SolutionSpace solutions, Path plotFolder) {
                var solutionsWithClassifier = solutions.whereClassifier(Classifiers.valueOf(classifier));
                var trace = Scatter3DTrace.builder(
                        solutionsWithClassifier.doubleColumn("KS-Distance"),
                        solutionsWithClassifier.doubleColumn("Correlation"),
                        solutionsWithClassifier.doubleColumn("Precision"))
                        .marker(Marker.builder()
                                .color("red")
                                .size(2)
                                .build())
                        .build();
                var name = "DistanceCorrelationPrecisionFor" + classifier;
                var figure = new Figure(layout.title(classifier).build(), trace);
                Plot.show(figure, name, plotFolder.resolve(name + ".html").toFile());
            }
        }
    }
}
