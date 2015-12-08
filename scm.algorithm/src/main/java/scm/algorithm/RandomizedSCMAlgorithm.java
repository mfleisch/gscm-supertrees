package scm.algorithm;

import phyloTree.model.tree.Tree;
import scm.algorithm.treeSelector.GreedyTreeSelector;
import scm.algorithm.treeSelector.RandomizedGreedyTreeSelector;
import scm.algorithm.treeSelector.TreeScorer;
import utils.parallel.ParallelUtils;
import utils.progressBar.CLIProgressBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by fleisch on 24.03.15.
 */
public class RandomizedSCMAlgorithm extends AbstractMultipleResultsSCMAlgorithm {
//    private int iterations = 0;
    private int individualIterations = 0;

    public RandomizedSCMAlgorithm(TreeScorer... scorer) {
        this(null, scorer);
    }

    public RandomizedSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        this(0, trees, scorer);
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, TreeScorer... scorer) {
        this(numberOfIterations, null, scorer);
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(trees, scorer);
        individualIterations = numberOfIterations;
    }

    private int defaultIterations() {
//        return inputTrees.length * inputTrees.length;
        return inputTrees.length;
    }

    @Override
    protected int numOfJobs() {
        return (getIterations() + 1) * scorerArray.length;
    }

    protected List<Tree> calculateSequencial() {
        final int iterations = getIterations();
        final GreedyTreeSelector nonRandomResultSelector = new GreedyTreeSelector();
        nonRandomResultSelector.setInputTrees(inputTrees);
        final RandomizedGreedyTreeSelector randomResultSelector = new RandomizedGreedyTreeSelector();
        randomResultSelector.setInputTrees(inputTrees);

        List<Tree> superTrees = new ArrayList<>();
        for (TreeScorer treeScorer : scorerArray) {
            List<Tree> scms = new ArrayList<>(iterations + 1);

            nonRandomResultSelector.setScorer(treeScorer);
            scms.add((calculateGreedyConsensus(nonRandomResultSelector, false)));
            randomResultSelector.setScorer(treeScorer);
            for (int i = 0; i < iterations; i++) {
                scms.add(calculateGreedyConsensus(randomResultSelector,false));
            }
            superTrees.addAll(scms);
        }

        return superTrees;
    }

    protected List<Tree> calculateParallel() {
        final int iterations = getIterations();
        List<Tree> superTrees = new ArrayList<>(numOfJobs());
        List<Future<List<Tree>>> futurList = new LinkedList<>();

        //todo maybe bucked parallelism
        //calculate random results
        GSCMCallableFactory randomFactory = new GSCMCallableFactory(RandomizedGreedyTreeSelector.getFactory(), inputTrees);
        for (int i = 0; i < iterations; i++) {
            futurList.addAll(
                    ParallelUtils.parallelForEach(executorService, randomFactory, Arrays.asList(scorerArray)));
//                    ParallelUtils.parallelBucketForEach(executorService, randomFactory, Arrays.asList(scorerArray)));
        }

        //calculate nonRandomResults
        GSCMCallableFactory nonRandomFactory = new GSCMCallableFactory(GreedyTreeSelector.getFactory(), inputTrees);
        futurList.addAll(
                ParallelUtils.parallelForEach(executorService, nonRandomFactory, Arrays.asList(scorerArray)));
//                ParallelUtils.parallelBucketForEach(executorService, nonRandomFactory, Arrays.asList(scorerArray)));

        //collect results
        try {
            for (Future<List<Tree>> future : futurList) {
                superTrees.addAll(future.get());
            }
            return superTrees;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setNumberOfIterations(int iterations) {
        this.individualIterations = iterations;
    }

    public int getIterations() {
        if (individualIterations > 0 )
            return individualIterations;
        else if (inputTrees != null)
            return defaultIterations();
        return 0;
    }
}
