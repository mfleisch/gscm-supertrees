package scm.algorithm;


import phyloTree.model.tree.Tree;
import scm.algorithm.treeSelector.GreedyTreeSelector;
import scm.algorithm.treeSelector.TreeScorer;
import scm.algorithm.treeSelector.TreeSelector;
import scm.algorithm.treeSelector.TreeSelectorFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm {
    final TreeSelector selector;

    public GreedySCMAlgorithm() {
        super();
        this.selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
    }

    public GreedySCMAlgorithm(Logger logger, ExecutorService executorService) {
        super(logger, executorService);
        this.selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
    }

    public GreedySCMAlgorithm(Logger logger) {
        super(logger);
        this.selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
    }

    public GreedySCMAlgorithm(TreeScorer scorer) {
        this();
        this.selector.setScorer(scorer);
    }

    public GreedySCMAlgorithm(TreeSelector selector) {
        super();
        this.selector = selector;
    }

    public GreedySCMAlgorithm(Logger logger, ExecutorService executorService, TreeSelector selector) {
        super(logger, executorService);
        this.selector = selector;
    }

    public GreedySCMAlgorithm(Logger logger, TreeSelector selector) {
        super(logger);
        this.selector = selector;
    }

    @Override
    protected String name() {
        return getClass().getSimpleName();
    }

    @Override
    public void setScorer(TreeScorer scorer) {
        this.selector.setScorer(scorer);
    }

    @Override
    public void setInput(List<Tree> trees) {
        setInput(trees.toArray(new Tree[trees.size()]));
    }

    @Override
    public void setInput(Tree... trees) {
        selector.setInputTrees(trees);
    }

    @Override
    protected List<Tree> calculateSuperTrees() {
        return new ArrayList<>(Arrays.asList(calculateSuperTree()));
    }

    Tree calculateSuperTree() {
        return calculateGreedyConsensus(selector);
    }

    @Override
    public boolean shutdown() {
        TreeSelectorFactory.shutdown(selector);
        return super.shutdown();
    }
}
