package fschmidt.feature.selection;

import fschmidt.feature.selection.Datasets.Dataset;
import fschmidt.feature.selection.Utils.Self;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.Logistic;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.J48;
import weka.core.Instance;
import weka.core.Instances;

import java.util.function.Supplier;

import static fschmidt.feature.selection.Classifiers.*;
import static fschmidt.feature.selection.Utils.*;
import static fschmidt.feature.selection.Utils.newObject;

// ----------------------------------------------
//  CLASSIFIERS.
// ----------------------------------------------
// Contains all classifiers used in this project.
// For further information see Weka documentation.
// The only classifier we provide is the Baseline-
// classifier, whose task is always to predict 1.0.
public enum Classifiers implements Supplier<Classifier<?,?>> {
    //J48_TREE(J48.class),
    NAIVE_BAYES(NaiveBayes.class),
    K_NEAREST_NEIGHBOUR(IBk.class),
    SUPPORT_VECTOR_MACHINE(SMO.class),
    LOGISTIC_REGRESSION(Logistic.class),
    BASELINE_CLASSIFIER(BaseLine.class)
    ;

    final Classifier classifier;

    <T extends AbstractClassifier> Classifiers(Class<T> clazz) { classifier = new Classifier<>(clazz); }

    public Classifier get() { return classifier; }

    // ----------------------------------------------
    //  CLASSIFIER INTERFACE.
    // ----------------------------------------------
    // Is a wrapper for the Weka Abstract classifier.
    // With an API that limits itself to the functionality
    // needed in this project.
    public static class Classifier<T extends Classifier<T, C>, C extends AbstractClassifier>
            implements Self<T> {

        private C model;

        private Classifier(Class<C> clazz, Object... args) {
            model = newObject(clazz, args);
        }

        public T resetModel(Object... args) {
            model = TCast.cast(newObject(model.getClass(), args));
            return self();
        }

        public T train(Dataset dataset) {
            try { model.buildClassifier(dataset); }
            catch (Exception e) { e.printStackTrace(); }
            return self();
        }

        public double predict(Dataset dataset) {
            int hit = 0, miss = 0;
            for (Instance inst : dataset) {
                try {
                    double classif = model.classifyInstance(inst);
                    if (classif == inst.value(inst.numAttributes() - 1)) hit++;
                    else miss++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return 1.0 * hit / (hit + miss);
        }

        public String name() { return model.getClass().getSimpleName(); }

        public weka.classifiers.Classifier getClassifier() {
            return this.model;
        }
    }

    // ----------------------------------------------
    //  BASELINE CLASSIFIER.
    // ----------------------------------------------
    // The Baseline classifier is used in the experiments
    // for comparison. It always predicts 1.0 for each value.
    // TODO Baseline classifier: always returns yes ... (numOut / numSamples)
    public static class BaseLine extends AbstractClassifier {

        public void buildClassifier(Instances _dataset) {
            // does not need to have any state because it
            // is not supposed to do anything but returning
            // 1.0
        }

        public double classifyInstance(Instance instance) {
            return 1.0D;
        }
    }
}
