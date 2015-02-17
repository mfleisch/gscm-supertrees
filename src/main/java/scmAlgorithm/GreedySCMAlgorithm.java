package scmAlgorithm;

import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.treetools.TreeUtilsBasic;
import scmAlgorithm.treeSelector.TreePair;
import scmAlgorithm.treeSelector.TreeSelector;

import java.util.Arrays;

/**
 * Created by fleisch on 10.02.15.
 */
public class GreedySCMAlgorithm extends AbstractSCMAlgorithm {

    public GreedySCMAlgorithm(TreeSelector selector) {
        super(selector);
    }

    @Override
    protected void run() {
        Tree superCandidate = null;
        TreePair pair;
        while((pair = selector.pollTreePair()) != null){
            superCandidate = mergeTrees(pair);
            selector.addTree(superCandidate);
        }
        TreeUtilsBasic.cleanTree(superCandidate); //remove inner labels and branch lengths //todo create useful branch length?!
        superTrees = Arrays.asList(superCandidate);

    }

    private Tree mergeTrees(TreePair pair) {
        final NConsensus consMaker =  new NConsensus();
        consMaker.setMethod(NConsensus.METHOD_STRICT); //todo we want semi-strict maybe?

        pair.pruneToCommonLeafes();
        Tree consensus;
        if (pair.t1.vertexCount() <= pair.getCommonLeafes().size()+1){
            consensus = pair.t2;
        }else if (pair.t2.vertexCount() <= pair.getCommonLeafes().size()+1){
            consensus = pair.t1;
        }else{
            consensus = consMaker.consesusTree(pair.t1,pair.t2);
        }

        pair.reinsertSingleTaxa(consensus);
        return consensus;
    }




}
