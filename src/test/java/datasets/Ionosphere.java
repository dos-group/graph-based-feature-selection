package datasets;

import datasets.TestUtils.SolutionSpace;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Path;

import static datasets.TestUtils.PLOTS_FOLDER;
import static datasets.TestUtils.SOLUTIONS_FOLDER;

// -----------------------------------------------------------
@Tag("Ionosphere") @DisplayName("Explore Ionosphere Dataset")
// -----------------------------------------------------------
// Allows you to examine the ionosphere dataset. For more
// information on each method, see the class "Explore".
// For each combination of features the correlation,
// the distance and (for each classifier) the precision
// were calculated in advance and stored in the
// corresponding csv file in "solutions". Before tests
// in this class are executed, the entire solution
// space is loaded from the file into the memory.
class Ionosphere {

    private static SolutionSpace solutions;

    private static Path plotFolder = PLOTS_FOLDER.resolve("ionosphere");

    @BeforeAll // loading the solutions to memory
    static void beforeAll() throws IOException {
        solutions = new SolutionSpace(SOLUTIONS_FOLDER
                .resolve("io.csv"));
    }

    // ----------------------------------------------
    @Nested
    @Disabled @DisplayName("Best Solutions")
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
    @Nested @Disabled @DisplayName("Distributions")
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

//    @Test
//    @DisplayName("Best Set of Features on Ionosphere with all ")
//        //
//        // RESULTS
//        //
//        // ATTENTION ... Does not work. Too many features
//    void bestSetOfFeaturesIonosphere() {
//        for (Classifiers classifier : Classifiers.values()) {
//            var results = exploreDataset(IO_SPHERE, classifier);
//            results.table.setName(boldCyan(classifier.get().name()));
//            System.out.println("\n\n\n\n" + results.bestFeatureSets().printAll());
//        }
//    }