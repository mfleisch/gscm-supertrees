package scmAlgorithm;

import epos.model.tree.Tree;
import scmAlgorithm.treeScorer.TreeScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.RandomizedGreedyTreeSelector;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fleisch on 24.03.15.
 */
public class RandomizedSCMAlgorithm extends AbstractSCMAlgorithm implements RandomizedSCMCalculation {
    private final Tree[] inputTrees;
    private GreedySCMAlgorithm nonRandomResult;
    private TreeScorer[] scorerArray = null;
    private final int iterations;
    private boolean multipleRandomizedRuns = false;

    public RandomizedSCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector(scorer[0],false,trees));
        nonRandomResult =  new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0],trees));
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations = trees.length * trees.length;
    }

    public RandomizedSCMAlgorithm(int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector(scorer[0],false,trees));
        nonRandomResult =  new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0],trees));
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations =  numberOfIterations;
    }

    public RandomizedSCMAlgorithm(boolean multipleRandomizedRuns, int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
        super(new RandomizedGreedyTreeSelector(scorer[0],false,trees));
        nonRandomResult =  new GreedySCMAlgorithm(new GreedyTreeSelector(scorer[0],trees));
        this.inputTrees = trees;
        this.scorerArray = scorer;
        this.iterations =  numberOfIterations;
        this.multipleRandomizedRuns = multipleRandomizedRuns;
    }



    @Override
    protected List<TreePair> calculateSuperTrees() {
        List<TreePair> superTrees =  new ArrayList<>((iterations + 1) * scorerArray.length);
        superTrees.add(nonRandomResult.calculateSuperTree());
        superTrees.addAll(calculateRandomizedConsensus(selector, inputTrees, iterations));

        //some additional, optional non random results with different scorings.
        if (scorerArray.length > 1) {
            for (int i = 1; i < scorerArray.length; i++) {
                TreeScorer scorer = scorerArray[i];
                nonRandomResult.selector.setScorer(scorer);
                nonRandomResult.selector.init(inputTrees);
                superTrees.add(nonRandomResult.calculateSuperTree());
            }

            if (multipleRandomizedRuns){
                for (int i = 1; i < scorerArray.length; i++) {
                    TreeScorer scorer = scorerArray[i];
                    selector.setScorer(scorer);
                    superTrees.addAll(calculateRandomizedConsensus(selector, inputTrees, iterations));
                }
            }
        }

        return superTrees;
    }

    // todo move to defaut method to interface if java 8 is common
    @Override
    public List<TreePair> calculateRandomizedConsensus(TreeSelector selector, Tree[] inputTrees, int iterations) {
        List<TreePair> superTrees =  new ArrayList<>(iterations);
        for (int i = 0; i < iterations; i++) {
            selector.init(inputTrees);
            superTrees.add(calculateGreedyConsensus(selector));
        }
        //sort supertrees
        return superTrees;
    }

    // todo move to defaut method to interface if java 8 is common
    @Override
    public TreePair calculateGreedyConsensus(TreeSelector selector) {
        TreePair superCandidatePair = null;
        TreePair pair;
        while((pair = selector.pollTreePair()) != null){
            Tree superCandidate = pair.getConsensus(selector.getScorer().getConsensusAlgorithm());
            selector.addTree(superCandidate);
            superCandidatePair =  pair;
        }
        return superCandidatePair;
    }
}
