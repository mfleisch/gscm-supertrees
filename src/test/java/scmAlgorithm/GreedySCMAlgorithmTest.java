package scmAlgorithm;

import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.TreeUtilsBasic;
import evalUtils.EvalUtils;
import flipCut.Global;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.junit.Test;
import scmAlgorithm.treeScorer.AbstractOverlapScorer;
import scmAlgorithm.treeSelector.GreedyTreeSelector;
import scmAlgorithm.treeSelector.TreeSelector;

import java.io.File;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by fleisch on 16.02.15.
 */


public class GreedySCMAlgorithmTest {
    public final static String newickInput100 = "sm.13.sourceTrees_OptSCM-Rooting.tre";
    public final static String newickSCM100 = "sm.13.sourceTrees.scmTree.tre_OptRoot.tre";
    public final static String newickInput100_NORoot = "sm.13.sourceTrees.tre";
    public final static String newickSCM100_NORoot = "sm.13.sourceTrees.scmTree.tre";

    public final static String newickInput1000 = "sm.5.sourceTrees_OptSCM-Rooting.tre";
    public final static String newickSCM1000 = "sm.5.sourceTrees.scmTree.tre_OptRoot.tre";
    public final static String newickInput1000_NORoot = "sm.5.sourceTrees.tre";
    public final static String newickSCM1000_NORoot = "sm.5.sourceTrees.scmTree.tre";

    //tree constants
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML_SCM_OPT;//Global.SM_SOURCE_TREES_RAXML_SCM;//
//    final static String sourceTreeLocation = Global.SM_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    final static String sourceTreeLocation = Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    //    final static String alternateSourceTreeLocation =  Global.SM_SOURCE_TREES_RAXML_MODEL_LEAST;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    final static String alternateSourceTreeLocation =  Global.SMOG_SOURCE_TREES_RAXML;//Global.SM_SOURCE_TREES_RAXML_SCM;//
    //    final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML_OPT_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
//    final static String scmTreeLocation = Global.SM_SCM_TREES_RAXML; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//
    final static String scmTreeLocation = Global.SMOG_SCM_TREES_SUPER_ROOTED; //Global.SM_SOURCE_TREE_PATH + "RaxML/sm_data." + Global.TAG_INSTANCE + ".scmTreeRooted.tre";//

    /*@Test
    public void test(){
        System.out.printf("just for vm starting");
//        Tree[] inputTrees = {Newick.getTreeFromString("((A,B),((C,D),(F,E)))"),Newick.getTreeFromString("((A,B),((C,D),(F,E)))")};
        Tree[] inputTrees = {Newick.getTreeFromString("((t31:0.34794262316543867,((t30:0.31841362876557555,((t44:0.10681395480155051,t45:0.178199080910333)100:0.22069504967636794,t62:0.22233320372768547)96:0.03895547199911872)100:0.07275250707280582,t71:0.37253899043019006)99:0.06497785741978976)100:0.13779747453116212,(((t88:0.1444042084652763,t21:0.13719153890314426)100:0.13428353473109353,(t6:0.18768319351581622,(t42:0.16526428976845187,t96:0.137591460714016)100:0.08976942523786526)100:0.07243799666087426)97:0.020028769849659574,((t76:0.4128409039824788,((t60:0.2767821203274252,t89:0.23972682094947703)86:0.05934951729013295,(t28:0.11530031321876041,t67:0.06501814210361741)100:0.275486999513763)94:0.05132826557318959)44:0.0010762057250973682,((t13:0.26574900134331153,((((t77:0.10696230369431146,t90:0.09403647015565379)100:0.07956620247535828,(t98:0.024838859973723558,t65:0.020023377886215832)100:0.09368116389671026)89:0.028762357427680497,t82:0.16434506105358487)100:0.05365831170091232,(t22:0.16601183752074947,(t23:0.08570202511507254,t99:0.06900589792729805)100:0.06328288515305962)100:0.10105430474884551)100:0.09841730557975933)75:0.014771626116522076,(t50:0.2010497059716759,((t38:0.02440878477572648,t32:0.02081843295287259)100:0.07399038598162526,(t61:0.04608499759467848,(t18:0.02225346368549311,t92:0.028997700594351822)100:0.02395772172684409)100:0.11964757857217051)100:0.07620809616140957)100:0.14609585978493939)60:0.010922518573312305)100:0.051582660788568796)100:0.13779747453116212);"),Newick.getTreeFromString("(((t71:0.5416306339612201,((t62:0.2822459937552794,(t44:0.16702899600714066,t45:0.2754709450310518)100:0.26606191495529885)92:0.0340410031795963,t30:0.45120148572016877)100:0.07927350543241352)96:0.07094660878862342,t31:0.5295080259185292)100:0.11280722110797754,(((t21:0.17449688533065125,t88:0.22858941191374663)100:0.16502173370555134,((t42:0.18589797002288128,t96:0.1852936071255776)100:0.1464116801473894,t6:0.2961619537375223)99:0.11672909855693266)95:0.02473986820540825,(((t50:0.3380600228599689,((t32:0.038893392747218966,t38:0.03404260544018965)100:0.097446131591035,(t61:0.06265323994278993,(t92:0.0351106435881108,t18:0.026292161170685446)98:0.030091325821724232)100:0.18560139099451284)100:0.1080587893206038)100:0.16828714241668352,(t13:0.3382166457968794,(((t23:0.1118857761923961,t99:0.09808140656889233)100:0.06537447512341762,t22:0.2332970918510611)100:0.11954853463963759,(t82:0.1993690202628642,((t90:0.13527649064331027,t77:0.13438272143075825)98:0.07249439292268055,(t98:0.03559133954228222,t65:0.04126983367567251)100:0.14391223446524476)99:0.03413244028564304)100:0.08944794984924642)100:0.15905285328018243)90:0.04721014711675956)57:0.014004602385130387,(((t89:0.32879320491388825,t60:0.36503862998608905)96:0.1110797047981274,(t67:0.12096923180335344,t28:0.14393114475044377)100:0.36984412392141686)69:0.04014318758073618,t76:0.5902795216536575)53:0.005717878110788041)100:0.09122288920483604)100:0.19622186945688008);")};
        NConsensus c =  new NConsensus();
        c.setMethod(NConsensus.METHOD_STRICT);
        Tree st = c.consesusTree(inputTrees);

        System.out.println(Newick.getStringFromTree(st));
        System.out.println();
        System.out.println();

    }*/

    @Test
    public void smidgenSample100() {
        File inputFile = new File(getClass().getResource("/" + newickInput100).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        Tree scm =  Newick.getTreeFromFile(new File(getClass().getResource("/" + newickSCM100_NORoot).getFile()))[0];
        List<String> order = new ArrayList<>(TreeUtilsBasic.getLeafLabels(supertree.getRoot()));
        Collections.sort(order);
        TreeUtilsBasic.sortTree(supertree, order);
        TreeUtilsBasic.sortTree(scm,order);
        System.out.println("Clades_Real_SCM: " + (scm.vertexCount() - scm.getNumTaxa()));
        System.out.println(Newick.getStringFromTree(scm));
        System.out.println("Strict-100: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        assertEquals(scm.getNumTaxa(),supertree.getNumTaxa());
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample100_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput100_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Strict-100_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample100Adam() {
        File inputFile = new File(getClass().getResource("/" + newickInput100).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector,false, GreedySCMAlgorithm.Methods.ADAMS);
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-100: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample100Adam_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput100_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true, GreedySCMAlgorithm.Methods.ADAMS);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-100_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }

    @Test
    public void smidgenSample100Loose() {
        File inputFile = new File(getClass().getResource("/" + newickInput100).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector,false, GreedySCMAlgorithm.Methods.SEMI_STRICT);
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-100: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample100Loose_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput100_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true, GreedySCMAlgorithm.Methods.SEMI_STRICT);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-100_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }


    @Test
    public void smidgenSample1000() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector);
        Tree supertree = algo.getSupertree();
        Tree scm =  Newick.getTreeFromFile(new File(getClass().getResource("/" + newickSCM1000_NORoot).getFile()))[0];
        System.out.println("Clades_Real_SCM: " + (scm.vertexCount() - scm.getNumTaxa()));
        System.out.println("Strict-1000: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        assertEquals(scm.getNumTaxa(),supertree.getNumTaxa());
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);



    }

    @Test
    public void smidgenSample1000_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true);
        Tree supertree = algo.getSupertree();
        System.out.println("Strict-1000_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample1000Adam() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector,false, GreedySCMAlgorithm.Methods.ADAMS);
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-1000: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample1000Adam_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true, GreedySCMAlgorithm.Methods.ADAMS);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Adams-1000_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }

    @Test
    public void smidgenSample1000Loose() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector,false, GreedySCMAlgorithm.Methods.SEMI_STRICT);
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-1000: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);

    }

    @Test
    public void smidgenSample1000Loose_NORoot() {
        File inputFile = new File(getClass().getResource("/" + newickInput1000_NORoot).getFile());
        long t = System.currentTimeMillis();
        Tree[] inputTrees = Newick.getTreeFromFile(inputFile);
        TreeSelector selector = new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), inputTrees);
        AbstractSCMAlgorithm algo = new GreedySCMAlgorithm(selector, true, GreedySCMAlgorithm.Methods.SEMI_STRICT);//rooting stuff
        Tree supertree = algo.getSupertree();
        System.out.println("Semi-Strict-1000_Root: " + (double) (System.currentTimeMillis() - t) / 1000d + "s");
        assertNotNull(supertree);
        System.out.println(Newick.getStringFromTree(supertree));
        int inner =  supertree.vertexCount() - supertree.getNumTaxa();
        System.out.println("Clades: " + inner);
    }

    @Test
    public void debugTest() {
        int[] taxas = {
                100,
                500,
                1000
        };

        int[] scaffoldFactors = {
                20,
                50,
                75,
                100
        };

        //0 <= instances <= 30
        final int instanceMin = 0;
        final int instanceMax = 30;
        final Map<String, String> TEMPLATES = new HashMap<String, String>(Global.NR_OF_TEMPLATE_TAGS);
        final DescriptiveStatistics[] scmTimes =  new DescriptiveStatistics[4];

        int trees = 0;
        int fptrees = 0;
        int swfptrees = 0;
        int equalrees = 0;
        for (int taxa : taxas) {
            TEMPLATES.put(Global.TAG_TAXA, Integer.toString(taxa));

            int currentInstanceMax = 0;
            //the 1000 taxa dataset has only 10 replicates
            if (taxa == 100 || taxa == 500) {
                currentInstanceMax = Math.min(30, instanceMax);
            } else if (taxa == 1000) {
                currentInstanceMax = Math.min(30, instanceMax);
//                currentInstanceMax = Math.min(10, OPTIONS.instanceMax);
            }
            for (int scaffoldFactorIndex = 0; scaffoldFactorIndex < scaffoldFactors.length; scaffoldFactorIndex++) {
                scmTimes[scaffoldFactorIndex] = new DescriptiveStatistics();
                TEMPLATES.put(Global.TAG_SCAFFOLD, Integer.toString(scaffoldFactors[scaffoldFactorIndex]));



                for (int instance = instanceMin; instance < currentInstanceMax; instance++) {
                    TEMPLATES.put(Global.TAG_INSTANCE, Integer.toString(instance));
                    File scmFile = new File(EvalUtils.expandTemplate(scmTreeLocation, TEMPLATES));

                    if (scmFile.exists()) {
                        trees++;
                        Tree swensonSCM =  Newick.getTreeFromFile(scmFile)[0];
                        File inputFile = new File(EvalUtils.expandTemplate(sourceTreeLocation, TEMPLATES));
                        Tree[] input =  Newick.getTreeFromFile(inputFile);


                        System.out.println();
                        System.out.println("### Calc SCM Trees ###");
                        System.out.println("SCM-SMID: " + (swensonSCM.vertexCount() - swensonSCM.getNumTaxa()));
                        System.out.println(Newick.getStringFromTree(swensonSCM));

                        long scmTime =  System.currentTimeMillis();
                        GreedySCMAlgorithm scmAlgorithm =  new GreedySCMAlgorithm(new GreedyTreeSelector.GTSMapPQ(new AbstractOverlapScorer.OverlapScorerTroveObject(), TreeUtilsBasic.cloneTrees(input)),false, GreedySCMAlgorithm.Methods.STRICT);
                        Tree scmTree = scmAlgorithm.getSupertree();
                        scmTime =  System.currentTimeMillis() - scmTime;
                        scmTimes[scaffoldFactorIndex].addValue(scmTime);
                        System.out.println("SCM: " + (scmTree.vertexCount() - scmTree.getNumTaxa()));
                        System.out.println(Newick.getStringFromTree(scmTree));
                        System.out.println("SCM Time " + taxa + "/" + scaffoldFactors[scaffoldFactorIndex] + "/" + instance + ": " + (float)scmTime/1000f + "s");

                        double[] rates = FN_FP_RateComputer.calculateRates(scmTree, swensonSCM, false);
                        double[] sumOfRates = FN_FP_RateComputer.calculateSumOfRates(scmTree, input);
                        double[] swensonSumOfRates = FN_FP_RateComputer.calculateSumOfRates(swensonSCM, input);

                        List<String> order = new ArrayList<>(TreeUtilsBasic.getLeafLabels(scmTree.getRoot()));
                        Collections.sort(order);
                        TreeUtilsBasic.sortTree(swensonSCM, order);
                        TreeUtilsBasic.sortTree(scmTree,order);

                        System.out.println("FN/FP to Swenson: ");
                        System.out.println(Arrays.toString(rates));
                        if (Double.compare(rates[0],0d)==0 && Double.compare(rates[1],0d)==0)
                            equalrees++;

                        System.out.println("SFN/SFP to Source: ");
                        System.out.println(Arrays.toString(sumOfRates));
                        assertEquals(0,Double.compare(0d,sumOfRates[1]));
                        if (Double.compare(sumOfRates[1],0d) != 0)
                            fptrees++;
                        System.out.println("SFN/SFP Swenson to Source: ");
                        System.out.println(Arrays.toString(swensonSumOfRates));
                        assertEquals(0,Double.compare(0d,swensonSumOfRates[1]));
                        if (Double.compare(swensonSumOfRates[1],0d) != 0)
                            swfptrees++;

                        System.out.println("######################");
                        System.out.println();


                    }

                }
            }
        }
        System.out.println("trees calculated: " + trees);
        System.out.println("trees with false positives: " + fptrees + " relative: " + (double)fptrees/(double)trees);
        System.out.println("swenson trees with false positives: " + swfptrees + " relative: " + (double)swfptrees/(double)trees);
        System.out.println("trees equal to swenson: " + equalrees);

    }
}
