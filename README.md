# Graph Based Feature Selection

This repository contains the code for the paper __Graph-based Feature Selection Filter Utilizing Maximal Cliques__ - Daniel Thilo Schroeder, Kevin Styp-Rekowski, Florian Schmidt, Alexander Acker and Odej Kao. In 2019 Sixth International Conference on Social Networks Analysis, Management and Security (SNAMS). IEEE. [Link to Publication](https://www.researchgate.net/publication/337187000_Graph-based_Feature_Selection_Filter_Utilizing_Maximal_Cliques)

## Project Description

## Datasets

For the experiment we have decided on the following data sets. All data sets selected can be found [here](http://odds.cs.stonybrook.edu/). 
When choosing the dataset we considered the following. 
All our datasets are changed to the extent that the outliers are marked false and all other datapoints are marked true.

##### 1. Size
We test each record with all classifiers on all combinations of features. 
The datasets should not be too large to perform the experiments in a reasonable time. 
For example, the shuttle dataset is about 50,000 entries in size.
The calculation time for K-Nearest-Neighbour with all possible combinations of features was XXX.

##### 2. Number of Features
The Feature Selection algorithm we introduced includes finding the largest cliques with the [Bron-Kerbosch](https://en.wikipedia.org/wiki/Bron%E2%80%93Kerbosch_algorithm) algorithm, the [complexity](https://www.sciencedirect.com/science/article/pii/S0304397506003586?via%3Dihub) here is XXX in the average case. 
We also calculate the entire solution space for comparison.

##### 3. Outliers
XXX

### Shuttle
[link](http://odds.cs.stonybrook.edu/shuttle-dataset/)

| points | dims | outlier |
|------|-----|---------|
| 49097  | 9   | 3511 (7%) |

### Ionosphere
[link](http://odds.cs.stonybrook.edu/ionosphere-dataset/)

| points | dims | outlier |
|------|-----|---------|
| 351  | 33   | 126 (36%) |


### Wine
[link](http://odds.cs.stonybrook.edu/wine-dataset/)

| points | dims | outlier |
|------|-----|---------|
| 129  | 13   | 10 (7.7%) |

### Glass
[link](http://odds.cs.stonybrook.edu/glass-data/)

| points | dims | outlier |
|------|-----|---------|
| 214  | 9   | 9 (4.2%) |



## Experiments

### Explore Dataset

#### Results

##### Shuttle 

##### Ionosphere

##### Wine

##### Glass

