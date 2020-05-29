package fschmidt.feature.selection;

import fschmidt.feature.selection.Utils.Tuples.Tuple2;
import it.unimi.dsi.fastutil.booleans.BooleanArrayList;
import it.unimi.dsi.fastutil.booleans.BooleanList;
import weka.core.Attribute;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.instance.StratifiedRemoveFolds;
import weka.filters.unsupervised.attribute.Remove;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import static com.google.common.base.Preconditions.checkNotNull;
import static fschmidt.feature.selection.Datasets.*;
import static fschmidt.feature.selection.Utils.*;
import static java.util.stream.Collectors.*;

// ----------------------------------------------
//  DATA SETS.
// ----------------------------------------------
// This class contains all currently supported
// data sets.
public enum Datasets implements Supplier<Dataset> {

    GLASS("glass.csv"),
    // Vina conducted a comparison test of her rule-based system, BEAGLE, the
    // nearest-neighbor algorithm, and discriminant analysis.  BEAGLE is
    // a product available through VRS Consulting, Inc.; 4676 Admiralty Way,
    // Suite 206; Marina Del Ray, CA 90292 (213) 827-7890 and FAX: -3189.
    // In determining whether the glass was a type of "float" glass or not,
    // the following results were obtained (# incorrect answers):
    //
    //    Type of Sample                            Beagle   NN    DA
    //    Windows that were float processed (87)     10      12    21
    //    Windows that were not:            (76)     19      16    22
    //
    // The study of classification of types of glass was motivated by
    // criminological investigation.  At the scene of the crime, the glass left
    // can be used as evidence...if it is correctly identified!
    // 9 / 214 outliers = 4.2%
    // 9 Attributes

    IO_SPHERE("ionosphere.csv"),
    // This radar data was collected by a system in Goose Bay, Labrador.  This
    // system consists of a phased array of 16 high-frequency antennas with a
    // total transmitted power on the order of 6.4 kilowatts.  See the paper
    // for more details.  The targets were free electrons in the ionosphere.
    // "Good" radar returns are those showing evidence of some type of structure
    // in the ionosphere.  "Bad" returns are those that do not; their signals pass
    // through the ionosphere.
    // Received signals were processed using an autocorrelation function whose
    // arguments are the time of a pulse and the pulse number.  There were 17
    // pulse numbers for the Goose Bay system.  Instances in this databse are
    // described by 2 attributes per pulse number, corresponding to the complex
    // values returned by the function resulting from the complex electromagnetic
    // signal.
    // 126 / 351 outliers = 36%
    // 34 Attributes

    SHUTTLE("shuttle.csv"),
    // The original Statlog (Shuttle) dataset from UCI machine learning repository
    // is a multi-class classification dataset with dimensionality 9. Here, the
    // training and test data are combined. The smallest five classes, i.e. 2, 3,
    // 5, 6, 7 are combined to form the outliers class, while class 1 forms the
    // inlier class. Data for class 4 is discarded.
    // 3511 / 49097 outliers: 7%
    // 9 Attributes

    WINE("wine.csv");
    // These data are the results of a chemical analysis of wines grown in the
    // same region in Italy but derived from three different cultivars. The analysis
    // determined the quantities of 13 constituents found in each of the three types
    // of wines.
    // 10 from 129 outliers: 7.7%
    // 13 Attributes

    final Dataset dataset;

    public Dataset get() { return dataset; }

    Datasets(String name) { dataset = new Dataset(name); }

    // ----------------------------------------------
    //  DATA SET.
    // ----------------------------------------------
    // Represents a data set. Because our approach to
    // feature extraction is to be compared to that
    // of Weka, a dataset contains our representation
    // of the data (header, values, ground truth) as
    // well as the Weka representation (instances).
    public static class Dataset extends Instances {

        private final String name;

        private Dataset(String name) {
            this(instancesOf(name), name.substring(0, name.length()-4));
        }

        private Dataset(Instances inst, String _name) {
            super(inst);
            name = _name;
            setClassIndex(numAttributes() - 1);
        }

        public String[] header() {
            return m_Attributes.stream()
                    .filter(attr -> !classAttribute().equals(attr))
                    .map(Attribute::name)
                    .toArray(String[]::new);
        }

        public List<double[]> values() {
            return m_Instances.stream()
                    .map(inst -> {
                        var res = new double[numAttributes() - 1];
                        for (int i = 0, j = 0; i < numAttributes(); i++)
                            if (i != classIndex()) res[j++] = inst.value(i);
                        return res;
                    }).collect(toList());
        }

        public BooleanList groundTruth() {
            var result = new BooleanArrayList(numInstances());
            for (int i = 0; i < numInstances(); i++) {
                result.add(get(i).stringValue(numAttributes() - 1).equals("true"));
            }
            return result;
        }

        public Tuple2<Dataset, Dataset> split() {
            // set options for creating the subset of data
            var options = new String[6];
            options[0] = "-N";                      // indicate we want to set the number of folds
            options[1] = Integer.toString(2);       // split the data into five random folds
            options[2] = "-F";                      // indicate we want to select a specific fold
            options[3] = Integer.toString(1);       // select the first fold
            options[4] = "-S";                      // indicate we want to set the random seed
            options[5] = Integer.toString(1337);    // set the random seed to 1337
            Dataset trainData = null, testData = null;
            try {
                // use StratifiedRemoveFolds to randomly split the data
                var filter = new StratifiedRemoveFolds();
                filter.setOptions(options); // set the filter options
                filter.setInputFormat(this); // prepare the filter for the data format
                filter.setInvertSelection(false); // do not invert the selector
                // apply filter for test data here
                // 1 of 5 for training
                trainData = new Dataset(Filter.useFilter(this, filter), name + "-train");
                //  prepare and apply filter for training data here
                filter.setInvertSelection(true); // invert the selector to get other data
                // 4 of 5 for testing
                testData = new Dataset(Filter.useFilter(this, filter), name + "-test");
            }
            catch (Exception e) { e.printStackTrace(); }
            return Tuples.of(trainData, testData);
        }

        // immutable
        public Dataset reduceDimsTo(String[] dimensions) {
            var filter = new Remove();
            var indices = Arrays.stream(dimensions)
                    .map(nmn -> m_NamesToAttributeIndices.get(nmn) + 1)
                    .map(String::valueOf)
                    .reduce((a,b) -> a + "," + b)
                    .orElseThrow() + "," + (classIndex() + 1);
            filter.setAttributeIndices(indices);
            filter.setInvertSelection(true);
            Instances newData = null;
            try {
                filter.setInputFormat(this);
                newData = Filter.useFilter(this, filter);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return new Dataset(newData, name + "-reduced");
        }

        public String name() { return name; }
    }

    private static Instances instancesOf(String name) {
        var input = ClassLoader.getSystemResourceAsStream("datasets/" + name);
        var loader = new CSVLoader();
        Instances result = null;
        try {
            loader.setSource(checkNotNull(input));
            result = loader.getDataSet();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
