package datasets;

import fschmidt.feature.selection.Classifiers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.Histogram2D;

import java.io.*;
import java.nio.file.Path;

import static datasets.TestUtils.*;
import static fschmidt.feature.selection.Classifiers.*;
import static fschmidt.feature.selection.Datasets.*;

// -----------------------------------------------------------
@Tag("Wine") @DisplayName("Explore Wine Dataset")
// -----------------------------------------------------------
// Allows you to examine the wine dataset. For more
// information on each method, see the class "Explore".
// For each combination of features the correlation,
// the distance and (for each classifier) the precision
// were calculated in advance and stored in the
// corresponding csv file in "solutions". Before tests
// in this class are executed, the entire solution
class Wine {

    // TODO for all other Datasets
    private static final Classifiers classifier = K_NEAREST_NEIGHBOUR;
    @Test
    @DisplayName("Distribution Correlation/Precision Histogram2D")
    void correlationPrecision() {
        var table = exploreDataset(WINE, classifier).table;
        Plot.show(Histogram2D.create("Correlation Precision", table, "Correlation", "Precision"));
    }
    @Test
    @DisplayName("Distribution Distance/Precision Histogram2D")
    void distancePrecision() {
        var table = exploreDataset(WINE, classifier).table;
        Plot.show(Histogram2D.create("Distance Precision", table, "KS-Distance", "Precision"));
    }
    @Test
    @DisplayName("Distribution Distance/Correlation Histogram2D")
    void distanceCorrelation() {
        var table = exploreDataset(WINE, classifier).table;
        Plot.show(Histogram2D.create("Distance Precision", table, "KS-Distance", "Correlation"));
    }
    @Test
    @DisplayName("Generates a csv containing the whole solution space for all classifiers")
    void drawSolutionSpace() {
        var classifiers = Classifiers.values();
        var result = exploreDatasetMonteCarlo(SHUTTLE, classifiers[3], 50).table;
        //for (int i = 1; i < classifiers.length; i++)
        //    result.append(exploreDatasetMonteCarlo(SHUTTLE, classifiers[i], 2).table);
        writeTableToFile(result, SOLUTIONS_FOLDER.resolve("datasets/shuttle.csv"));
    }


    private static SolutionSpace solutions;

    private static Path plotFolder = PLOTS_FOLDER.resolve("wine");

    @BeforeAll // loading the solutions to memory
    static void beforeAll() throws IOException {
        solutions = new SolutionSpace(SOLUTIONS_FOLDER
                .resolve("datasets/shuttle.csv"));
    }

    // ----------------------------------------------
    @Nested @DisplayName("Best Solutions")
    // ----------------------------------------------
    // for detailed infos see class "Explore"
    class BestSolutions {
        @Test
        @DisplayName("All Classifiers")
        void bestSolutionsForAll() {
            Explore.BestSolutions.bestSolutionsForAll(solutions);
        }
        @ParameterizedTest
        @DisplayName("For Classifier")
        @ValueSource(strings = {
                ///"J48_TREE",
                "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
                "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION"
                //,
                //"BASELINE_CLASSIFIER"
        })
        void bestSolutionsForClassifier(String classifier) {
            Explore.BestSolutions.bestSolutionsForClassifier(classifier, solutions);
        }
    }

    // ----------------------------------------------
    @Nested @DisplayName("Distributions")
    // ----------------------------------------------
    // for detailed infos see class "Explore"
    class Distributions {

        // ----------------------------------------------
        @Nested @DisplayName("Correlation x Precision")
        // ----------------------------------------------
        // for detailed infos see class "Explore"
        class CorrelationPrecision {
            @Test
            @DisplayName("All")
            void all() {
                Explore.Distributions.CorrelationPrecision
                        .all(solutions, plotFolder);
            }
            @ParameterizedTest
            @DisplayName("For")
            @ValueSource(strings = {
                    "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
                    "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
                    "BASELINE_CLASSIFIER"
            })
            void forClassifier(String classifier) {
                Explore.Distributions.CorrelationPrecision
                        .forClassifier(classifier, solutions, plotFolder);
            }
        }

        // ----------------------------------------------
        @Nested @DisplayName("Distance x Precision")
        // ----------------------------------------------
        // for detailed infos see class "Explore"
        class DistancePrecision {
            @Test
            @DisplayName("All")
            void all() {
                Explore.Distributions.DistancePrecision
                        .all(solutions, plotFolder);
            }
            @ParameterizedTest
            @DisplayName("For")
            @ValueSource(strings = {
                    "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
                    "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
                    "BASELINE_CLASSIFIER"
            })
            void correlationPrecisionClassifier(String classifier) {
                Explore.Distributions.DistancePrecision
                        .forClassifier(classifier, solutions, plotFolder);
            }
        }

        // ----------------------------------------------
        @Nested @DisplayName("Distance x Correlation")
        // ----------------------------------------------
        // for detailed infos see class "Explore"
        class DistanceCorrelation {
            @Test
            @DisplayName("All")
            void all() {
                Explore.Distributions.DistanceCorrelation
                        .all(solutions, plotFolder);
            }
            @ParameterizedTest
            @DisplayName("For")
            @ValueSource(strings = {
                    "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
                    "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
                    "BASELINE_CLASSIFIER"
            })
            void forClassifier(String classifier) {
                Explore.Distributions.DistanceCorrelation
                        .forClassifier(classifier, solutions, plotFolder);
            }
        }

        // ----------------------------------------------
        @Nested @DisplayName("Distance x Correlation x Precision")
        // ----------------------------------------------
        // for detailed infos see class "Explore"
        class DistanceCorrelationPrecision {
            @Test
            @DisplayName("All")
            void all() {
                Explore.Distributions.DistanceCorrelationPrecision
                        .all(solutions, plotFolder);
            }
            @ParameterizedTest
            @DisplayName("For")
            @ValueSource(strings = {
                    "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
                    "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
                    "BASELINE_CLASSIFIER"
            })
            void forClassifier(String classifier) {
                Explore.Distributions.DistanceCorrelationPrecision
                        .forClassifier(classifier, solutions, plotFolder);
            }
        }
    }
}

//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    private static SolutionSpace solutions;
//
//    private static Path plotFolder = PLOTS_FOLDER.resolve("wine");
//
//    @BeforeAll
//    static void beforeAll() throws IOException
//    { solutions = new SolutionSpace(SOLUTIONS_FOLDER.resolve("wine.csv")); }
//
//
//// ----------------------------------------------
//@DisplayName("Best Solutions")
//        // ----------------------------------------------
//class BestSolutions {
//
//    @Test
//    @DisplayName("All Classifiers")
//    void bestSolutionsForAll() {
//        var result = solutions
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//
//    @ParameterizedTest
//    @DisplayName("For Classifier")
//    @ValueSource(strings = {
//            "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
//            "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
//            "BASELINE_CLASSIFIER"
//    })
//    void bestSolutionsForClassifier(String classifier) {
//        var result = solutions
//                .whereClassifier(Classifiers.valueOf(classifier))
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//}
//
//// ----------------------------------------------
//@Nested @DisplayName("Distributions")
//        // ----------------------------------------------
//class Distributions {
//
//    // ----------------------------------------------
//    @Nested @DisplayName("Correlation x Precision")
//            // ----------------------------------------------
//    class CorrelationPrecision {
//
//        @ParameterizedTest
//        @DisplayName("For")
//        @ValueSource(strings = {
//                "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
//                "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
//                "BASELINE_CLASSIFIER"
//        })
//        void correlationPrecisionClassifier(String classifier) {
//            var plot =
//                    ScatterPlot.create(classifier,
//                            solutions.whereClassifier(Classifiers.valueOf(classifier)),
//                            "Correlation",
//                            "Precision");
//            var name = "CorrelationPrecisionFor" + classifier;
//            Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
//        }
//
//        @Test
//        @DisplayName("All")
//        void correlationPrecisionAll(TestInfo info) {
//            var plot =
//                    ScatterPlot.create("All without Baseline",
//                            solutions.withoutBaseline(),
//                            "Correlation",
//                            "Precision");
//            var name = "CorrelationPrecisionAll";
//            Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
//        }
//    }
//
//
//    // ----------------------------------------------
//    @Nested @DisplayName("Distance x Precision")
//            // ----------------------------------------------
//    class DistancePrecision {
//
//        @ParameterizedTest
//        @DisplayName("For")
//        @ValueSource(strings = {
//                "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
//                "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
//                "BASELINE_CLASSIFIER"
//        })
//        void correlationPrecisionClassifier(String classifier) {
//            var plot =
//                    ScatterPlot.create(classifier,
//                            solutions.whereClassifier(Classifiers.valueOf(classifier)),
//                            "KS-Distance",
//                            "Precision");
//            var name = "DistancePrecisionFor" + classifier;
//            Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
//        }
//
//        @Test
//        @DisplayName("All")
//        void correlationPrecisionAll(TestInfo info) {
//            var plot =
//                    ScatterPlot.create("All without Baseline",
//                            solutions.withoutBaseline(),
//                            "KS-Distance",
//                            "Precision");
//            var name = "DistancePrecisionAll";
//            Plot.show(plot, name, plotFolder.resolve(name + "All.html").toFile());
//        }
//    }
//
//
//    // ----------------------------------------------
//    @Nested @DisplayName("Distance x Correlation")
//            // ----------------------------------------------
//    class DistanceCorrelation {
//
//        @ParameterizedTest
//        @DisplayName("For")
//        @ValueSource(strings = {
//                "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
//                "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
//                "BASELINE_CLASSIFIER"
//        })
//        void distanceCorrelationClassifier(String classifier) {
//            var plot =
//                    ScatterPlot.create(classifier,
//                            solutions.whereClassifier(Classifiers.valueOf(classifier)),
//                            "KS-Distance",
//                            "Correlation");
//            var name = "DistanceCorrelationFor" + classifier;
//            Plot.show(plot, name, plotFolder.resolve(name + ".html").toFile());
//        }
//
//        @Test
//        @DisplayName("All")
//        void correlationPrecisionAll(TestInfo info) {
//            var plot =
//                    ScatterPlot.create("All without Baseline",
//                            solutions.withoutBaseline(),
//                            "KS-Distance",
//                            "Correlation");
//            var name = "DistanceCorrelationAll";
//            Plot.show(plot, name, plotFolder.resolve(name + "All.html").toFile());
//        }
//    }
//
//
//    // ----------------------------------------------
//    @Nested @DisplayName("Distance x Correlation x Precision")
//            // ----------------------------------------------
//    class DistanceCorrelationPrecision {
//
//        final LayoutBuilder layout = Layout.builder()
//                .height(1000)
//                .width(1200)
//                .showLegend(true);
//
//        @ParameterizedTest
//        @DisplayName("For")
//        @ValueSource(strings = {
//                "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
//                "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
//                "BASELINE_CLASSIFIER"
//        })
//        void distanceCorrelationPrecisionClassifier(String classifier) {
//            var solutionsWithClassifier = solutions.whereClassifier(Classifiers.valueOf(classifier));
//            var trace = Scatter3DTrace.builder(
//                    solutionsWithClassifier.doubleColumn("KS-Distance"),
//                    solutionsWithClassifier.doubleColumn("Correlation"),
//                    solutionsWithClassifier.doubleColumn("Precision"))
//                    .marker(Marker.builder()
//                            .color("red")
//                            .size(2)
//                            .build())
//                    .build();
//            var name = "DistanceCorrelationPrecisionFor" + classifier;
//            var figure = new Figure(layout.title(classifier).build(), trace);
//            Plot.show(figure, name, plotFolder.resolve(name + ".html").toFile());
//        }
//
//        @Test
//        @DisplayName("All")
//        void distanceCorrelationPrecisionAll() {
//            var solutionsWithoutBaseline = solutions.withoutBaseline();
//            var trace = Scatter3DTrace.builder(
//                    solutionsWithoutBaseline.doubleColumn("KS-Distance"),
//                    solutionsWithoutBaseline.doubleColumn("Correlation"),
//                    solutionsWithoutBaseline.doubleColumn("Precision"))
//                    .marker(Marker.builder()
//                            .color("red")
//                            .size(2)
//                            .build())
//                    .build();
//            var name = "DistanceCorrelationPrecisionAll";
//            var figure = new Figure(layout.title("All without Baseline").build(), trace);
//            Plot.show(figure, name, plotFolder.resolve(name + "All.html").toFile());
//        }
//    }
//}
//
//
//
//
//
//


















//    // ==================> DISTRIBUTIONS
//
//    private static final Classifiers classifier = K_NEAREST_NEIGHBOUR;
//
//    @Test
//    @DisplayName("Correlation/Precision on Wine")
//    void correlationPrecisionWithKnearestOnWine() {
//        var correlationAndPrecision = ScatterPlot.create("Correlation/Precision on Wine " + classifier.get().name(),
//                exploreDataset(WINE, classifier).table,
//                "Correlation",
//                "Precision");
//        Plot.show(correlationAndPrecision);
//    }
//
//    @Test
//    @DisplayName("KS-Distance/Precision with K-Nearest on Wine")
//    void KsDistancePrecisionWithKnearestOnWine() {
//        var correlationAndPrecision = ScatterPlot.create("KS-Distance/Precision on Wine " + classifier.get().name(),
//                exploreDataset(WINE, classifier).table,
//                "KS-Distance",
//                "Precision");
//        Plot.show(correlationAndPrecision);
//    }
//
//    @Test
//    @DisplayName("KS-Distance/Correlation/Precision on Wine")
//    void KsDistanceCorrelationWithKnearestPrecisionOnWine() {
//
//        var table = exploreDataset(WINE, classifier).table;
//
//        var layout = Layout.builder()
//                .title("KS-Distance/Correlation/Precision on Wine " + classifier.get().name())
//                .height(1000)
//                .width(1200)
//                .showLegend(true)
//                .build();
//
//        var trace = Scatter3DTrace.builder(
//                table.doubleColumn("KS-Distance"),
//                table.doubleColumn("Precision"),
//                table.doubleColumn("Correlation"))
//                .marker(Marker.builder()
//                        .color("red")
//                        .size(2)
//                        .build())
//                .build();
//
//        Plot.show(new Figure(layout, trace));
//    }


//@Tag("Best")
//// ----------------------------------------------
//@DisplayName("Best Solutions")
//        // ----------------------------------------------
//class BestSolutions {
//
//    @Test
//    @Tag("J48_TREE")
//    @DisplayName("J48")
//    void J48() {
//        var result = solutions
//                .whereClassifier(J48_TREE)
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//
//    @Test
//    @Tag("NAIVE_BAYES")
//    @DisplayName("Naive Bayes")
//    void NaiveBayes() {
//        var result = solutions
//                .whereClassifier(NAIVE_BAYES)
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//
//    @Test
//    @Tag("K_NEAREST_NEIGHBOUR")
//    @DisplayName("K Nearest Neighbour")
//    void KNearestNeighbours() {
//        var result = solutions
//                .whereClassifier(K_NEAREST_NEIGHBOUR)
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//
//    @Test
//    @Tag("SUPPORT_VECTOR_MACHINE")
//    @DisplayName("Support Vector Machine")
//    void SupportVectorMachine() {
//        var result = solutions
//                .whereClassifier(SUPPORT_VECTOR_MACHINE)
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//
//    @Test
//    @Tag("LOGISTIC_REGRESSION")
//    @DisplayName("Logistic Regression")
//    void LogisticRegression() {
//        var result = solutions
//                .whereClassifier(LOGISTIC_REGRESSION)
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//
//    @Test
//    @Tag("BASELINE_CLASSIFIER")
//    @DisplayName("Baseline")
//    void BASELINE() {
//        var result = solutions
//                .whereClassifier(BASELINE_CLASSIFIER)
//                .withHighestPrecision()
//                .sortedByNumDimensions();
//        System.out.println(result.printAll());
//    }
//}