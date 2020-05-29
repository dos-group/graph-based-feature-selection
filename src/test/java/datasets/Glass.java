package datasets;

import fschmidt.feature.selection.Classifiers;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;

import static datasets.TestUtils.*;
import static fschmidt.feature.selection.Datasets.GLASS;

// ----------------------------------------------
@Tag("Glass") @DisplayName("Explore Glass Dataset")
// ----------------------------------------------
// Allows you to examine the glass dataset. For more
// information on each method, see the class "Explore".
// For each combination of features the correlation,
// the distance and (for each classifier) the precision
// were calculated in advance and stored in the
// corresponding csv file in "solutions". Before tests
// in this class are executed, the entire solution
// space is loaded from the file into the memory.
class Glass {

    private static SolutionSpace solutions;

    private static Path plotFolder = PLOTS_FOLDER.resolve("glass");

    @BeforeAll // loading the solutions to memory
    static void beforeAll() throws IOException {
        solutions = new SolutionSpace(SOLUTIONS_FOLDER
                .resolve("glass.csv"));
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
                "J48_TREE", "NAIVE_BAYES", "K_NEAREST_NEIGHBOUR",
                "SUPPORT_VECTOR_MACHINE", "LOGISTIC_REGRESSION",
                "BASELINE_CLASSIFIER"
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

    /*@Test
    @DisplayName("Generates a csv containing the whole solution space for all classifiers")
    void drawSolutionSpace() {
        var classifiers = Classifiers.values();
        var result = exploreDataset(GLASS, classifiers[0]).table;
        for (int i = 1; i < classifiers.length; i++)
            result.append(exploreDataset(GLASS, classifiers[i]).table);
        writeTableToFile(result, SOLUTIONS_FOLDER.resolve("glass.csv"));
    }*/
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
//
//
//
//
//
//
//
//
//    @Test
//    @DisplayName("Best Set of Features on GLASS with all ")
//        //
//        //  RESULTS
//        //
//        // =======> J48 TREE
//        //      Precision       |     KS-Distance      |      Correlation      |  NumberDimensions  |           Dimensions           |
//        // ---------------------------------------------------------------------------------------------------------------------------
//        //  0.9906542056074766  |  0.6571815718157181  |  0.28983271114395553  |                 2  |  [refractiveIndex, Potassium]  |
//        //
//        //
//        // =======> NAIVE BAYES
//        //      Precision      |     KS-Distance      |      Correlation      |  NumberDimensions  |                Dimensions                 |
//        // -------------------------------------------------------------------------------------------------------------------------------------
//        //  0.985981308411215  |                0.65  |  0.23444953150066847  |                 4  |   [Sodium, Magnesium, Potassium, Barium]  |
//        //  0.985981308411215  |  0.5317073170731708  |  0.16694745530396282  |                 4  |  [Magnesium, Silicon, Potassium, Barium]  |
//        //  0.985981308411215  |  0.5707317073170731  |  0.09160542545499888  |                 4  |    [Magnesium, Silicon, Potassium, Iron]  |
//        //
//        //
//        // =======> K NEAREST NEIGHBOUR
//        //      Precision       |     KS-Distance      |      Correlation      |  NumberDimensions  |          Dimensions           |
//        // --------------------------------------------------------------------------------------------------------------------------
//        //  0.9906542056074766  |  0.6162601626016261  |  0.21176914770477054  |                 3  |  [Sodium, Potassium, Barium]  |
//        //
//        //
//        // ========> SUPPORT VECTOR MACHINE
//        //      Precision       |      KS-Distance      |  Correlation  |  NumberDimensions  |     Dimensions      |
//        // ---------------------------------------------------------------------------------------------------------
//        //  0.9579439252336449  |  0.18536585365853658  |               |                 1  |           [Barium]  |
//        //  0.9579439252336449  |   0.8975609756097561  |               |                 1  |        [Potassium]  |
//        //  0.9579439252336449  |   0.5582655826558266  |               |                 1  |          [Calcium]  |
//        //  0.9579439252336449  |   0.3203252032520325  |               |                 1  |         [Aluminum]  |
//        //  0.9579439252336449  |    0.751219512195122  |               |                 1  |        [Magnesium]  |
//        //  0.9579439252336449  |   0.7658536585365854  |               |                 1  |           [Sodium]  |
//        //  0.9579439252336449  |  0.41680216802168024  |               |                 1  |  [refractiveIndex]  |
//        //  0.9579439252336449  |   0.2926829268292683  |               |                 1  |          [Silicon]  |
//        //  0.9579439252336449  |  0.34146341463414637  |               |                 1  |             [Iron]  |
//        //
//        //
//        // ========> LOGISTIC REGRESSION
//        //      Precision       |      KS-Distance      |     Correlation      |  NumberDimensions  |               Dimensions               |
//        // -----------------------------------------------------------------------------------------------------------------------------------
//        //  0.9953271028037384  |  0.49990966576332424  |  0.1109455964716895  |                 3  |  [refractiveIndex, Potassium, Barium]  |
//        //
//        //
//        // ========> BASELINE
//        //       Precision       |      KS-Distance      |  Correlation  |  NumberDimensions  |     Dimensions      |
//        // ----------------------------------------------------------------------------------------------------------
//        //  0.04205607476635514  |  0.18536585365853658  |               |                 1  |           [Barium]  |
//        //  0.04205607476635514  |   0.8975609756097561  |               |                 1  |        [Potassium]  |
//        //  0.04205607476635514  |   0.5582655826558266  |               |                 1  |          [Calcium]  |
//        //  0.04205607476635514  |   0.3203252032520325  |               |                 1  |         [Aluminum]  |
//        //  0.04205607476635514  |    0.751219512195122  |               |                 1  |        [Magnesium]  |
//        //  0.04205607476635514  |   0.7658536585365854  |               |                 1  |           [Sodium]  |
//        //  0.04205607476635514  |  0.41680216802168024  |               |                 1  |  [refractiveIndex]  |
//        //  0.04205607476635514  |   0.2926829268292683  |               |                 1  |          [Silicon]  |
//        //  0.04205607476635514  |  0.34146341463414637  |               |                 1  |             [Iron]  |
//        //
//    void bestSetOfFeaturesGlass() {
//        for (Classifiers classifier : Classifiers.values()) {
//            var results = exploreDataset(GLASS, classifier);
//            results.table.setName(boldCyan(classifier.get().name()));
//            System.out.println("\n\n\n\n" + results.bestFeatureSets().printAll());
//        }
//    }
//
//    // ==================> DISTRIBUTIONS
//
//    private static final Classifiers classifier = K_NEAREST_NEIGHBOUR;
//
//    @Test
//    @DisplayName("Correlation/Precision on Wine")
//    void correlationPrecisionScatter() {
//        var correlationAndPrecision = ScatterPlot.create("Correlation/Precision on Wine " + classifier.get().name(),
//                exploreDataset(GLASS, classifier).table,
//                "Correlation",
//                "Precision");
//        Plot.show(correlationAndPrecision);
//    }
//
//    @Test
//    @DisplayName("KS-Distance/Precision with K-Nearest on Wine")
//    void KsDistancePrecisionScatter() {
//        var correlationAndPrecision = ScatterPlot.create("KS-Distance/Precision on Wine " + classifier.get().name(),
//                exploreDataset(GLASS, classifier).table,
//                "KS-Distance",
//                "Precision");
//        Plot.show(correlationAndPrecision);
//    }
//
//    @Test
//    @DisplayName("KS-Distance/Correlation/Precision on Wine")
//    void KsDistanceCorrelationScatter3d() {
//        var table = exploreDataset(GLASS, classifier).table;
//        var layout = Layout.builder()
//                .title("KS-Distance/Correlation/Precision on Wine " + classifier.get().name())
//                .height(1000)
//                .width(1200)
//                .showLegend(true)
//                .build();
//        var trace = Scatter3DTrace.builder(
//                table.doubleColumn("KS-Distance"),
//                table.doubleColumn("Precision"),
//                table.doubleColumn("Correlation"))
//                .marker(Marker.builder()
//                        .color("red")
//                        .size(2)
//                        .build())
//                .build();
//        Plot.show(new Figure(layout, trace));
//    }
//
//
//    @Test
//    @DisplayName("Distribution Correlation/Precision Histogram2D")
//    void correlationPrecision() {
//        var table = exploreDataset(GLASS, classifier).table;
//        Plot.show(Histogram2D.create("Correlation Precision", table, "Correlation", "Precision"));
//    }
//
//    @Test
//    @DisplayName("Distribution Distance/Precision Histogram2D")
//    void distancePrecision() {
//        var table = exploreDataset(GLASS, classifier).table;
//        Plot.show(Histogram2D.create("Distance Precision", table, "KS-Distance", "Precision"));
//    }
//
//    @Test
//    @DisplayName("Distribution Distance/Correlation Histogram2D")
//    void distanceCorrelation() {
//        var table = exploreDataset(GLASS, classifier).table;
//        Plot.show(Histogram2D.create("Distance Precision", table, "KS-Distance", "Correlation"));
//    }
//
//    @Test
//    @DisplayName("Generates a csv containing the whole solution space for all classifiers")
//    void drawSolutionSpace() {
//        var classifiers = Classifiers.values();
//        var result = exploreDataset(GLASS, classifiers[0]).table;
//        for (int i = 1; i < classifiers.length; i++)
//            result.append(exploreDataset(WINE, classifiers[0]).table);
//        writeTableToFile(result, SOLUTIONS_FOLDER.resolve("glass.csv"));
//    }
