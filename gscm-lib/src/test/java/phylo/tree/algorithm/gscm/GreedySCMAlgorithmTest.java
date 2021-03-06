package phylo.tree.algorithm.gscm;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import phylo.tree.algorithm.consensus.Consensus;
import phylo.tree.algorithm.gscm.treeSelector.GreedyTreeSelector;
import phylo.tree.algorithm.gscm.treeSelector.TreeScorer;
import phylo.tree.algorithm.gscm.treeSelector.TreeScorers;
import phylo.tree.algorithm.gscm.treeSelector.TreeSelector;
import phylo.tree.io.Newick;
import phylo.tree.model.Tree;
import phylo.tree.model.TreeUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 16.02.15.
 */

//todo simple sample test
@RunWith(Parameterized.class)
public class GreedySCMAlgorithmTest extends BasicSCMTest {
    Consensus.ConsensusMethod method;
    TreeScorer scorer;

    public GreedySCMAlgorithmTest(Tree[] inputData, Tree scmResult, Consensus.ConsensusMethod method, TreeScorer scorer) {
        super(inputData, scmResult);
        this.method = method;
        this.scorer = scorer;
    }

    @Parameterized.Parameters
    public static List<Object[]> data() {
        List<Object[]> paras = new LinkedList<>();
        for (TreeScorer scorer : TreeScorers.getFullScorerArray(false)) {
//        for (TreeScorer scorer : new TreeScorer[]{TreeScorers.getScorer(TreeScorers.ScorerType.UNIQUE_CLADES_REMAINING)}) {
//        for (TreeScorer scorer : new TreeScorer[]{TreeScorers.getScorer(TreeScorers.ScorerType.UNIQUE_TAXA)}) {
            for (Consensus.ConsensusMethod method : Consensus.ConsensusMethod.values()) {
//                paras.add(new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(),TreeSelector.ConsensusMethod.STRICT , scorer});
                paras.add(new Object[]{LOCATIONS.newickInput100(), LOCATIONS.newickSCM100_NORoot(),method , scorer});
//                paras.add(new Object[]{LOCATIONS.newickInput1000(), LOCATIONS.newickSCM1000_NORoot(), method, scorer});
//                paras.add(new Object[]{LOCATIONS.newickInsufficientInput500(), null, method, scorer});
//                paras.add(new Object[]{LOCATIONS.insufficientInputSimple(), null, method, scorer});
            }
        }
        return paras;
    }

    @Test
    public void testSmidgenSamples() {
        StringBuffer buf = new StringBuffer();

        buf.append("########## Test with following Parameters [" + scorer.getClass().getCanonicalName() + ", " + inputData + ", " + scmResult + ", " + method + "] ##########");
        buf.append(SEP);
        long t = System.currentTimeMillis();
//        TreeSelector selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
//        selector.setScorer(scorer);
//        selector.setInputTrees(inputData);
        SCMAlgorithm algo = new GreedySCMAlgorithm(scorer);
        algo.setInput(Arrays.asList(inputData));
        algo.run();
        Tree supertree = algo.getResult();
        if (scmResult != null)
            assertNotNull(supertree);
        else
            assertNull(supertree);

        if (supertree != null) {
            List<String> order = new ArrayList<>(TreeUtils.getLeafLabels(supertree.getRoot()));
            Collections.sort(order);
            TreeUtils.sortTree(supertree, order);
            TreeUtils.sortTree(scmResult, order);
            buf.append("Clades-Swenson_SCM: " + (scmResult.vertexCount() - scmResult.getNumTaxa()));
            buf.append(SEP);
            buf.append(Newick.getStringFromTree(scmResult));
            buf.append(SEP);
            buf.append("Fleisch-" + method + "-SCM: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
            buf.append(SEP);
            assertNotNull(supertree);
            assertEquals(scmResult.getNumTaxa(), supertree.getNumTaxa());
            buf.append(Newick.getStringFromTree(supertree));
            buf.append(SEP);
            int inner = supertree.vertexCount() - supertree.getNumTaxa();
            buf.append("Clades: " + inner);
            buf.append(SEP);
            buf.append("########## DONE ##########");
            buf.append(SEP);
            buf.append(SEP);
            buf.append(SEP);
            System.out.println(buf.toString());
        }
    }

    //        @Test
    public void largeSample() throws IOException {
        Path inputFile = Paths.get("/media/fleisch/wallace/home@wallace/data/simulated/SMIDGenOutgrouped/10000/0/Source_Trees/RaxML/smo.0.sourceTrees.tre");
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile.toFile());
         /*Files.copy(inputFile, inputFile.getParent().resolve("smo.0.sourceTrees_UNSUFFICIENT.tre"));

        LinkedHashMap<Object, Set<Tree>> treesSets = new LinkedHashMap<>();
        Map<Object, Set<Tree>> finished = new HashMap<>();
        for (int i = 0; i < inputTrees.length; i++) {
            Tree tree = inputTrees[i];
            Set<String> leafset = TreeUtils.getLeafLabels(tree.getRoot());
            treesSets.put(leafset, new HashSet<>(Arrays.asList(tree)));
        }

        while (treesSets.size() > 0) {
            boolean unchanged = false;
            Map.Entry<Object, Set<Tree>> e = treesSets.entrySet().iterator().next();
            Set<String> f = (Set<String>) e.getKey();
            Set<Tree> fset = e.getValue();
            treesSets.remove(f);

            while (!unchanged) {
                unchanged = true;
                Set<Set<String>> toRemove = new HashSet<>();
                for (Object o : treesSets.keySet()) {
                    Set<String> n = (Set<String>) o;
                    Set<String> check = new HashSet<>(n);
                    check.retainAll(f);
                    if (check.size() > 2) {
                        f.addAll(n);
                        Set<Tree> nset = treesSets.get(n);
                        fset.addAll(nset);
                        toRemove.add(n);
                        unchanged = false;
                    }
                }

                for (Set<String> remove : toRemove) {
                    treesSets.remove(remove);
                }
            }
            finished.put(f, fset);
        }

        Set<String> best = new HashSet<>();
        Set<Tree> trees = null;
        for (Object o : finished.keySet()) {
            Set<String> n = (Set<String>) o;
            if (n.size()>best.size()){
                best =  n;
                trees = finished.get(n);
            }
        }
        Path modelFile = Paths.get("/media/fleisch/wallace/home@wallace/data/simulated/SMIDGenOutgrouped/10000/0/Model_Trees/pruned/smo.0.modelTree.tre");
        Path modelOLD =  modelFile.getParent().resolve("smo.0.modelTree_UNSUFFICIENT.tre");
        Tree model  = Newick.getTreeFromFile(modelFile.toFile())[0];
        Files.copy(modelFile,modelOLD);
        TreeUtils.keepLeafesAndPruneTree(model, TreeUtils.getLeafesFromLabels(best, model));
        Newick.tree2File(modelFile.toFile(), model);

        Path modelSourceFile = Paths.get("/media/fleisch/wallace/home@wallace/data/simulated/SMIDGenOutgrouped/10000/0/Source_Trees/ModelSourceTrees/smo.0.modelSourceTrees.tre");
        Tree[] modelSourceTrees = Newick.getTreeFromFile(modelSourceFile.toFile());
        Files.copy(modelSourceFile,modelSourceFile.getParent().resolve("smo.0.modelSourceTrees_UNSUFFICIENT.tre"));
        Path modelSourceOGFile = modelSourceFile.getParent().resolve("smo.0.modelSourceTreesOGs.dat");
        List<String> lines = new ArrayList<>(Files.readAllLines(modelSourceOGFile));
        Files.copy(modelSourceOGFile,modelSourceOGFile.getParent().resolve("smo.0.modelSourceTreesOGs_UNSUFFICIENT.dat"));


        List<Tree> modelSourceTreesNu = new ArrayList<>(trees.size());
        List<Tree> sourceTreesNu = new ArrayList<>(trees.size());

        for (int i = 0; i < inputTrees.length; i++) {
            Tree inputTree = inputTrees[i];
            if (trees.contains(inputTree)){
                modelSourceTreesNu.add(modelSourceTrees[i]);
                sourceTreesNu.add(inputTree);
//                lines.remove(i);
            }
        }

        Newick.trees2File(modelSourceFile.toFile(), modelSourceTreesNu);
        Newick.trees2File(inputFile.toFile(), sourceTreesNu);

//        Files.write(modelSourceOGFile,lines);

*/


        TreeSelector selector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
        selector.setScorer(new TreeScorer.ConsensusResolutionScorer());
        selector.setInputTrees(inputTrees);
//        selector.setThreads(Runtime.getRuntime().availableProcessors());
        selector.setThreads(1);
        SCMAlgorithm algo = new GreedySCMAlgorithm(selector);//rooting stuff
        algo.run();
        Tree supertree = algo.getResult();
        System.out.println("large example: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner = supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }
}
