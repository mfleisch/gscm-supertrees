package scmAlgorithm;


import epos.algo.consensus.nconsensus.NConsensus;
import epos.model.tree.Tree;
import epos.model.tree.TreeNode;
import epos.model.tree.io.Newick;
import epos.model.tree.treetools.FN_FP_RateComputer;
import epos.model.tree.treetools.TreeUtilsBasic;
import org.apache.log4j.Level;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;
import java.util.Random;
import org.jgrapht.traverse.*;

/**
 * Created with IntelliJ IDEA.
 * User: Anika
 * Date: 27.10.14
 * Time: 13:13
 * To change this template use File | Settings | File Templates.
 */


public class GreedySCM {

    protected LinkedHashMap<String, List<String>> TreeCut1 = new LinkedHashMap<String, List<String>>();
    protected LinkedHashMap<String, List<String>> TreeCut2 = new LinkedHashMap<String, List<String>>();

    public boolean StringListsEquals(List<String> one, List<String> two){
        if (one == null && two == null){
            return true;
        }
        if((one == null && two != null)
                || one != null && two == null
                || one.size() != two.size()){
            return false;
        }
        List copyone = new ArrayList<String>(one);
        List copytwo = new ArrayList<String>(two);
        Collections.sort(copyone);
        Collections.sort(copytwo);
        return copyone.equals(copytwo);
    }

    public List<String> helpgetLabelsFromNodes(List<TreeNode> input){
        List<String> output = new ArrayList<String>();
        String def = "";
        for (TreeNode nod : input){
            output.add(def.concat(nod.getLabel()));
        }
        return output;
    }
    

    public List<TreeNode> helpgetTreeNodesFromLabels(List<String> li, Tree input){
        List<TreeNode> output = new ArrayList<TreeNode>();
        for (String x: li){
            output.add(input.getVertex(x));
        }
        return output;
    }


    /*public boolean helpCompareTreeStructure (Tree one, TreeNode nodone, Tree two, TreeNode nodtwo){
        boolean foundcorrectiteration = false;
        if (!StringListsEquals(helpgetLabelsFromNodes(Arrays.asList(nodone.getLeaves())), helpgetLabelsFromNodes(Arrays.asList(nodtwo.getLeaves())))) return false;
        else {
            List<TreeNode> childrenone = new ArrayList<TreeNode>(nodone.getChildren());
            List<TreeNode> childrentwo = new ArrayList<TreeNode>(nodtwo.getChildren());
            subiteration:for (TreeNode iter1 : childrenone){
                for (TreeNode iter2 : childrentwo){
                    if (!iter1.isLeaf() && !iter2.isLeaf()){
                        if (helpCompareTreeStructure(one, iter1, two, iter2)){
                            foundcorrectiteration = true;
                            break subiteration;
                        }
                    }
                    else return true; //foundcorrectiteration = true;
                }
            }

            if (!foundcorrectiteration) return false;
            return true;
        }
    }*/

    public boolean helpCompareTreeStructure (Tree one, TreeNode nodone, Tree two, TreeNode nodtwo){
        boolean foundcorrectiteration = false;
        //if (!StringListsEquals(helpgetLabelsFromNodes(Arrays.asList(nodone.getLeaves())), helpgetLabelsFromNodes(Arrays.asList(nodtwo.getLeaves())))) return false;
        List<String> list1 = helpgetLabelsFromNodes(Arrays.asList(nodone.getLeaves()));
        List<String> list2 = helpgetLabelsFromNodes(Arrays.asList(nodtwo.getLeaves()));
        if (!StringListsEquals(list1, list2)) return false;
        else {
            List<TreeNode> childrenone = new ArrayList<TreeNode>(nodone.getChildren());
            List<TreeNode> childrentwo = new ArrayList<TreeNode>(nodtwo.getChildren());
            for (TreeNode iter1 : childrenone){
                foundcorrectiteration = false;
                for (TreeNode iter2 : childrentwo){
                    if (!iter1.isLeaf() && !iter2.isLeaf()){
                        if (helpCompareTreeStructure(one, iter1, two, iter2)){
                            foundcorrectiteration = true;
                            break;
                        }
                    }
                    else if (iter1.getLabel().equalsIgnoreCase(iter2.getLabel())){
                        //return true;
                        foundcorrectiteration = true;
                        break;
                    }
                }
                if (!foundcorrectiteration) return false;
            }
            //if (!foundcorrectiteration) return false;
            return true;
        }
    }

    public boolean CompareTreeStructure (Tree one, Tree two){
        TreeNode start1 = one.getRoot();
        TreeNode start2 = two.getRoot();
        if (helpCompareTreeStructure(one, start1, two, start2)) return true;
        else return false;
    }

    public void testCompareTreeStructure(){
        File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        List<Tree> alltrees = new ArrayList();
        try{
            FileReader re = new FileReader(fi);
            alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
        }
        catch (FileNotFoundException e){
            System.err.println("kein File");
        };
        Tree tree = alltrees.get(1);
        Tree copy = tree.cloneTree();
        if (TreeEquals(tree, copy)) System.out.println("Baum 1 und seine Kopie sind gleich.");
    }

    public boolean TreeEquals (Tree one, Tree two){
        if (one==two) return true;
        else {
            //compare number of vertices
            if (one.vertexCount()!=two.vertexCount()) return false;
            //List<String> leaveslabellistone = new ArrayList<String>(helpgetLabelsFromNodes(Arrays.asList(one.getLeaves())));
            //List<String> leaveslabellisttwo = new ArrayList<String>(helpgetLabelsFromNodes(Arrays.asList(two.getLeaves())));
            //if (!StringListsEquals(leaveslabellistone, leaveslabellisttwo)) return false;
            List<TreeNode> leaveslistone = new ArrayList<TreeNode>(Arrays.asList(one.getLeaves()));
            List<TreeNode> leaveslisttwo = new ArrayList<TreeNode>(Arrays.asList(two.getLeaves()));
            //compare number and labeling of leaves
            if (!StringListsEquals(helpgetLabelsFromNodes(Arrays.asList(one.getLeaves())), helpgetLabelsFromNodes(Arrays.asList(two.getLeaves())))) return false;
            //compare TreeStructure
            if (!CompareTreeStructure(one, two)) return false;
            //TODO is this a valid equals method? What is missing to make it complete? How to change the comparison of vertices for correctness?
            /*TreeNode leastca = new TreeNode();
            TreeNode mintwo = new TreeNode();
            TreeNode nintwo = new TreeNode();
            for (TreeNode m : leaveslistone){
                for (TreeNode n : leaveslistone){
                    if (!m.getLabel().equalsIgnoreCase(n.getLabel())){
                        leastca = one.findLeastCommonAncestor(m, n);
                        mintwo = two.getVertex(m.getLabel());
                        nintwo = two.getVertex(n.getLabel());
                        //TreeNode lcatwo = two.findLeastCommonAncestor(two.getVertex(m.getLabel()), two.getVertex(n.getLabel()));
                        TreeNode lcatwo = two.findLeastCommonAncestor(mintwo, nintwo);
                        //if (!leastca.getLabel().equalsIgnoreCase(two.findLeastCommonAncestor(two.getVertex(m.getLabel()), two.getVertex(n.getLabel())).getLabel())){
                        if (!leastca.getLabel().equalsIgnoreCase(lcatwo.getLabel())){
                            return false;
                        }
                    }
                }
                //if (!vertexequals) return false;
                //vertexequals = false;
            }*/
            //TODO why does consensus method mess up the node-labels so equalsNode doesn't work correctly anymore?
            /*if (!one.findLeastCommonAncestor(one.getLeaves()).equalsNode(two.findLeastCommonAncestor(two.getLeaves()))){
                System.out.println("Die Wurzel ist wohl ungleich.");
                return false;
            }
            if (!one.getRoot().equalsNode(two.getRoot())){
                System.out.println("Die Wurzel ist wohl ungleich.");
                return false;
            }*/
        }
        return true;
    }

    public Tree getPaperConsensus (List<Tree> input) {
        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = getTreesForPaperSCM(output);
            one = save.get(0);
            two = save.get(1);
            three = save.get(2);
            System.out.println("Got one SCM: " +Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }

    public Tree getSuperfineConsensus (List<Tree> input){
        List<Tree> output = input.subList(0, input.size()-1);
        List<Tree> save;
        Tree one;
        Tree two;
        Tree three;
        while (output.size() > 1){
            save = getTreesWithBiggestOverlap(output);
            one = save.get(0);
            two = save.get(1);
            three = SCM(one, two);
            System.out.println("Got one SCM: " +Newick.getStringFromTree(three));
            System.out.println(output.size()+" left.");
            output.remove(one);
            output.remove(two);
            output.add(three);
        }
        return output.get(0);
    }

    public List<Tree> getTreesForPaperSCM (List<Tree> input){
        float curscore;
        float resolution;
        Tree scm;
        Tree maxscm = new Tree();
        float maxscore = 0;
        int mem1 = 0, mem2 = 0;
        int a, b, c = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    //TODO is it possible that two trees have no overlap?
                    if (getOverLappingNodes(iter, cur).size()==0);
                    else {
                        scm = SCM(iter, cur);
                        //TODO look up the definition of resolution
                        //a = scmAlgorithm.vertexCount();
                        //b = scmAlgorithm.getLeaves().length-3;
                        //c = scmAlgorithm.getLeaves().length;
                        resolution = scm.vertexCount() / (scm.getLeaves().length-3);
                        //resolution = a / b;
                        //TODO is this possible or do I have to take |L(Ti)UL(Tj)|?
                        curscore = resolution / scm.getLeaves().length;
                        //curscore = resolution / c;
                        if (curscore > maxscore){
                            maxscm = scm;
                            maxscore = curscore;
                            mem1 = input.indexOf(iter);
                            mem2 = input.indexOf(cur);
                        }
                    }
                }
            }
        }
        if (maxscore == 0 && input.size()>=2){
            output.add(input.get(0));
            output.add(input.get(1));
            System.err.println("In der Liste gibt es keine Bäume mit Scoring Ã¼ber 0");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
            output.add(maxscm);
        }
        return output;
    }

    public List<Tree> getTreesWithBiggestOverlap (List<Tree> input){
        int cursize;
        int maxsize = 0;
        int mem1 = 0, mem2 = 0;
        List<Tree> output = new ArrayList<Tree>();
        for (Tree iter : input){
            for (Tree cur : input){
                if (!iter.equals(cur)){
                    cursize = getOverLappingNodes(iter, cur).size();
                    if (cursize > maxsize){
                        maxsize = cursize;
                        mem1 = input.indexOf(iter);
                        mem2 = input.indexOf(cur);
                    }
                }
            }
        }
        if (maxsize == 0 && input.size()>=2){
            output.add(input.get(0));
            output.add(input.get(1));
            System.err.println("In der Liste gibt es keine Bäume mit Overlap");
        }
        else if (input.size()<2){
            System.err.println("Im Input existieren weniger als 2 Bäume");
        }
        else {
            output.add(input.get(mem1));
            output.add(input.get(mem2));
        }
        return output;
    }


    public void takeCareOfCollisions(){
        Set<Map.Entry<String, List<String>>> mapValues1 = TreeCut1.entrySet();
        Set<Map.Entry<String, List<String>>> mapValues2 = TreeCut2.entrySet();
        int maplength1 = mapValues1.size();
        int maplength2 = mapValues2.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength1];
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength2];
        mapValues1.toArray(TreeCut1entries);
        mapValues2.toArray(TreeCut2entries);
        Map<List<String>, List<String>> order = new HashMap();
        List<String> currvalue;
        String currkey;
        List<String> currlist;

        for (int iter1=maplength1-1; iter1>=0; iter1--){
            currvalue = TreeCut1entries[iter1].getValue();
            currkey = TreeCut1entries[iter1].getKey();
            if (order.containsKey(currvalue)){
                currlist = order.get(currvalue);
            }
            else {
                currlist = new ArrayList<String>();
            }
            currlist.add(currkey);
            currlist.add("Tree1");
            order.put(currvalue, currlist);
        }


        for (int iter2=maplength2-1; iter2>=0; iter2--){
            currvalue = TreeCut2entries[iter2].getValue();
            currkey = TreeCut2entries[iter2].getKey();
            if (order.containsKey(currvalue)){
                currlist = order.get(currvalue);
            }
            else {
                currlist = new ArrayList<String>();
            }
            currlist.add(currkey);
            currlist.add("Tree2");
            order.put(currvalue, currlist);
        }


        for (List<String> ke : order.keySet()){
            List<String> list = order.get(ke);
            if (list.contains("Tree1")&&list.contains("Tree2")){
                list.remove("Tree1");
                list.remove("Tree2");
                for (String el : list){
                    if (TreeCut2.containsKey(el)){
                        TreeCut2.remove(el);
                    }
                    if (TreeCut1.containsKey(el)){
                        TreeCut1.remove(el);
                    }
                }
                List<String> newlist;
                List<String> work;
                for (int iter=list.size()-1; iter>0; iter--){
                    work = new ArrayList<String>(list);
                    newlist = work.subList(0,iter);
                    newlist.addAll(ke);
                    newlist.add("Polytomy");
                    TreeCut1.put(list.get(iter), newlist);
                }
                TreeCut1.put(list.get(0), ke);
            }
        }

    }

    public boolean checkDirectionOfBackbone(List<String> nodes, Tree one, Tree two){
        List<String> dfiterator1 = generateDepthFirstListChildren (one);
        List<String> dfiterator2 = generateDepthFirstListChildren (two);
        boolean foundfirst = false;
        boolean foundlast = false;
        int iter = 0;
        String cur;
        int posf1;
        int posl1;
        String elf1 = "";
        String ell1 = "";
        //search for the first occurence of an element both trees have in common in depth first iterator of tree 1
        do {
            cur = dfiterator1.get(iter);
            if (nodes.contains(cur)) {
                foundfirst = true;
                //remember the element and its position
                posf1 = iter;
                elf1 = cur;
            }
            iter ++;
        } while (!foundfirst);
        //search for the last occurence of an element both trees have in common in depth first iterator of tree 1
        iter = dfiterator1.size()-1;
        do {
            cur = dfiterator1.get(iter);
            if (nodes.contains(cur)) {
                foundlast = true;
                //remember the element and its position
                posl1 = iter;
                ell1 = cur;
            }
            iter--;
        } while (!foundlast);
        //search for the first and last non-cut element from tree 1 in depth first iterator of tree 2
        int posf2 = dfiterator2.indexOf(elf1);
        int posl2 = dfiterator2.indexOf(ell1);
        //if the first appears later than the last, the list has to be rotated
        if (posf2 > posl2) return true;
        else return false;
    }

    public Tree SCM (Tree one, Tree two){
        ArrayList<String> nodesofboth = getOverLappingNodes(one, two);
        Tree result;
        Tree between;
        NConsensus con = new NConsensus();
        con.getLog().setLevel(Level.OFF);
        if (nodesofboth.size()==0){
            System.err.println("Kein SCM möglich, da kein Overlap");
        }
        if (nodesofboth.size() == one.getLeaves().length && nodesofboth.size() == two.getLeaves().length){
            result = con.consesusTree(new Tree[]{one, two}, 1.0);
            System.err.println("SCM der Bäume ergab Consensus, da gleiches Taxaset.");
        }
        else {
            boolean turn = checkDirectionOfBackbone(nodesofboth, one, two);
            one = CutTree(one, nodesofboth, turn);
            two = CutTree(two, nodesofboth, turn);

            takeCareOfCollisions();

            between = con.consesusTree(new Tree[]{one, two}, 1.0);
            result = hangInNodes(between);
        }

        return result;

    }

    public Tree hangInNodes (Tree input){
        Tree output = input.cloneTree();
        String currlabel;
        List<String> currlist;
        List<TreeNode> finallist = new ArrayList<TreeNode>();
        TreeNode lca = new TreeNode();
        TreeNode newvertex;
        TreeNode betw1;
        TreeNode betw2;


        Set<Map.Entry<String, List<String>>> mapValues = TreeCut1.entrySet();
        int maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut1entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut1entries);
        for (int iter=maplength-1; iter>=0; iter--){
            //System.out.println(test[iter].getKey()+" "+test[iter].getValue());
            currlabel = TreeCut1entries[iter].getKey();
            currlist = TreeCut1entries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                finallist = helpgetTreeNodesFromLabels(currlist, output);
                /*for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }*/
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        TreeCut1.clear();


        mapValues = TreeCut2.entrySet();
        maplength = mapValues.size();
        Map.Entry<String,List<String>>[] TreeCut2entries = new Map.Entry[maplength];
        mapValues.toArray(TreeCut2entries);
        for (int iter=maplength-1; iter>=0; iter--){
            //System.out.println(test[iter].getKey()+" "+test[iter].getValue());
            currlabel = TreeCut2entries[iter].getKey();
            currlist = TreeCut2entries[iter].getValue();
            if (currlist.contains("Polytomy")){
                currlist.remove("Polytomy");
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }
                lca = output.findLeastCommonAncestor(finallist);
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
            else {
                for (String x : currlist){
                    finallist.add(output.getVertex(x));
                }

                if (finallist.size()==1){
                    lca = finallist.get(0);
                }
                else {
                    lca = output.findLeastCommonAncestor(finallist);
                }
                if (lca.getParent()!=null){
                    betw1 = lca.getParent();
                    betw2 = new TreeNode();
                    output.removeEdge(betw1, lca);
                    output.addVertex(betw2);
                    output.addEdge(betw1, betw2);
                    output.addEdge(betw2, lca);
                    lca = betw2;
                }
                else {
                    betw1 = new TreeNode();
                    output.addVertex(betw1);
                    output.addEdge(betw1, lca);
                    output.setRoot(betw1);
                    lca = betw1;
                }
                newvertex = new TreeNode(currlabel);
                output.addVertex(newvertex);
                output.addEdge(lca, newvertex);
                finallist.clear();
            }
        }
        TreeCut2.clear();

        return output;
    }

    public ArrayList<String> getOverLappingNodes (Tree one, Tree two){
        List<TreeNode> leavesone = new ArrayList<TreeNode>(Arrays.asList(one.getLeaves()));
        List<TreeNode> leavestwo = new ArrayList<TreeNode>(Arrays.asList(two.getLeaves()));
        ArrayList<String> tokeep = new ArrayList<String>();

        for (TreeNode x : leavesone){
            for (TreeNode y : leavestwo){
                if (x.getLabel().equals(y.getLabel())){
                    tokeep.add(x.getLabel());
                }
            }
        }

        return tokeep;

    }

    public void testCutTree(){
        Tree a = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(d:1.0,c:1.0):1.0);");
        Tree b = Newick.getTreeFromString("((b:1.0,a:1.0):1.0,(c:1.0,d:1.0):1.0);");
        Tree c = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0):1.0,d:1.0);");
        Tree d = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,d:1.0);");
        Tree e = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        Tree f = Newick.getTreeFromString("(e:1.0,(d:1.0,(a:1.0,(b:1.0,c:1.0):1.0):1.0):1.0);");
        Tree g = Newick.getTreeFromString("((d:1.0,e:1.0):1.0,(a:1.0,(c:1.0,b:1.0):1.0):1.0)");
        Tree h = Newick.getTreeFromString("(d:1.0,(a:1.0,(b:1.0,(e:1.0,c:1.0):1.0):1.0):1.0)");
        Tree result = new Tree();
        //List<TreeNode> tokeep = new ArrayList<TreeNode>();
        List<String> tokeep = new ArrayList<String>();

        TreeNode ah = a.getVertex("a");
        TreeNode be = a.getVertex("b");
        TreeNode ce = a.getVertex("c");
        TreeNode de;
        TreeNode eh;
        String print = "";

           /*
        //BEISPIEL A eingeschränkt auf a, b, c
        //tokeep.add(ah);
        //tokeep.add(be);
        //tokeep.add(ce);
        tokeep.add("a");
        tokeep.add("b");
        tokeep.add("c");
        result = CutTree(a, tokeep);
        print = Newick.getStringFromTree(result);
        System.out.println("A eingeschränkt auf a, b, c: "+print);
        */

        //BEISPIEL A eingeschränkt auf a, b
        tokeep.clear();
        a = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(d:1.0,c:1.0):1.0);");
        //tokeep.add(a.getVertex("a"));
        //tokeep.add(a.getVertex("b"));
        tokeep.add("a");
        tokeep.add("b");
        result = CutTree(a, tokeep, false);
        print = Newick.getStringFromTree(result);
        System.out.println("A eingeschränkt auf a, b: "+print);

        //BEISPIEL C eingeschränkt auf a, b
        tokeep.clear();

        boolean te = a.getVertex("a").equalsNode(c.getVertex("a"));
        List<TreeNode> testt = new ArrayList(Arrays.asList(a.getLeaves()));
        boolean ze = testt.contains(c.getVertex("a"));

        ah = c.getVertex("a");
        be = c.getVertex("b");
        ce = c.getVertex("c");
        de = c.getVertex("d");
        //tokeep.add(ah);
        //tokeep.add(be);
        tokeep.add("a");
        tokeep.add("b");
        //tokeep.add(c.getVertex("a"));
        //tokeep.add(c.getVertex("b"));
        Tree result2;
        result2 = CutTree(c, tokeep, false);
        print = Newick.getStringFromTree(result);
        System.out.println("C eingeschränkt auf a, b: "+print);

        //NConsensus con = new NConsensus();
        //result = con.consesusTree(new Tree[]{result, result2}, 1.0);


        /*
        //BEISPIEL C eingeschränkt auf c, d
        tokeep.clear();
        //c = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0):1.0,d:1.0);");
        //tokeep.add(c.getVertex("c"));
        //tokeep.add(c.getVertex("d"));
        //tokeep.add(ce);
        //tokeep.add(de);
        tokeep.add("c");
        tokeep.add("d");
        result = CutTree(c, tokeep);
        print = Newick.getStringFromTree(result);
        System.out.println("C eingeschränkt auf c, d: "+print);
        */
        /*
        //BEISPIEL C eingeschränkt auf d
        //tokeep.remove(ce);
        tokeep.clear();
        c = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0):1.0,d:1.0);");
        //tokeep.add(c.getVertex("d"));
        tokeep.add("d");
        result = CutTree(c, tokeep);
        print = Newick.getStringFromTree(result);
        System.out.println("C eingeschränkt auf d : "+print);
        */

        Object zwischen = TreeCut2.get("d");
        System.out.println(zwischen.toString());

        //result = hangInNodes(result);

        System.out.println(Newick.getStringFromTree(result));

    }

    public Tree randomInsert(Tree tre, int nodenumber){
        //don't hang nodes at leaves
        Tree out = tre.cloneTree();
        String label = "";
        int vertexsize = 0;
        int randnumber = 0;
        boolean correct = false;
        Random rand = new Random();
        for (int iter=0; iter<nodenumber; iter++){
            label = "hangsatnode";
            vertexsize = tre.vertexCount();
            while (!correct){
                randnumber = rand.nextInt(vertexsize);
                if (!out.getVertex(randnumber).isLeaf()) correct = true;
            }
            label = label.concat(Integer.toString(randnumber));
            label = label.concat(",labeled_"+out.getVertex(randnumber).getLabel());
            label = label.concat(",try_"+Integer.toString(iter));
            TreeNode nod = new TreeNode(label);
            out.addVertex(nod);
            out.addEdge(out.getVertex(randnumber),nod);
        }
        return out;
    }

    public void testCuttingAndInserting(){
        File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        List<Tree> alltrees = new ArrayList();
        try{
            FileReader re = new FileReader(fi);
            alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
            //Tree result = getPaperConsensus(alltrees);
            //System.out.println("Supertree ist "+Newick.getStringFromTree(result));
        }
        catch (FileNotFoundException e){
            System.err.println("kein File");
        };
        Tree tree = alltrees.get(1);
        Tree compare = tree.cloneTree();
        Tree cutcompare;
        Tree cuttree;
        Tree hanginconsensus;
        Tree consensus = new Tree();
        //System.out.println(Newick.getStringFromTree(compare));
        NConsensus con = new NConsensus();
        compare = randomInsert(compare, 100);
        System.out.println("Randomisierter Baum erstellt");
        //System.out.println(Newick.getStringFromTree(compare));
        ArrayList<String> nodesofboth = getOverLappingNodes(tree, compare);

        boolean turn = checkDirectionOfBackbone(nodesofboth, tree, compare);
        cuttree = CutTree(tree, nodesofboth, turn);
        cutcompare = CutTree(compare, nodesofboth, turn);
        consensus = con.consesusTree(new Tree[]{cuttree, cutcompare}, 1.0);
        //if (consensus.equals(tree)){
        if (TreeEquals(tree, consensus)){
            System.out.println ("Konsensusbaum ist gleich Ursprungsbaum");
        }
        else System.out.println("Fehler, Konsensusbaum ungleich Ursprungsbaum");
        hanginconsensus = hangInNodes(consensus);
        if (TreeEquals(compare, hanginconsensus)){
            System.out.println("Baum mit eingefügten Vertices ist gleich Vergleichsbaum");
        }
        else System.out.println("Fehler, Baum mit eingefügten Verticees ungleich Vergleichsbaum");



        Tree one = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,e:1.0):1.0,((f:1.0,g:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0);");
        Tree two = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,(c:1.0,e:1.0):1.0):1.0,((g:1.0,f:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0):1.0,(k:1.0,l:1.0):1.0);");
        //Tree two = Newick.getTreeFromString("((k:1.0,l:1.0):1.0,(((a:1.0,b:1.0):1.0,(c:1.0,e:1.0):1.0):1.0,((g:1.0,f:1.0):1.0,((h:1.0,i:1.0):1.0,j:1.0):1.0):1.0):1.0);");
        //Tree one = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,(c:1.0,d:1.0):1.0):1.0,(e:1.0,f:1.0):1.0);");
        //Tree two = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(c:1.0,e:1.0):1.0);");
        nodesofboth = getOverLappingNodes(one, two);
        turn = checkDirectionOfBackbone(nodesofboth, one, two);
        Tree cutone = CutTree(one, nodesofboth, turn);
        Tree cuttwo = CutTree(two, nodesofboth, turn);
        NConsensus nee = new NConsensus();
        Tree res = nee.consesusTree(new Tree[]{cutone, cuttwo}, 1.0);
        //consensus = con.consesusTree(new Tree[]{cutone, cuttwo}, 1.0);
        Tree hungin = hangInNodes(res);
        System.out.println(Newick.getStringFromTree(hungin));


    }


    public void testStrictConsensus(){

        Tree a = Newick.getTreeFromString("((a:1.0,b:1.0):1.0,(d:1.0,c:1.0):1.0);");
        Tree b = Newick.getTreeFromString("((b:1.0,a:1.0):1.0,(c:1.0,d:1.0):1.0);");
        Tree c = Newick.getTreeFromString("(((a:1.0,b:1.0):1.0,c:1.0):1.0,d:1.0);");
        Tree d = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,d:1.0);");
        Tree e = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        //Tree f = Newick.getTreeFromString("(((d:1.0,(a:1.0,(b:1.0,c:1.0):1.0):1.0):1.0),e:1.0)");
        Tree f = Newick.getTreeFromString("(e:1.0,(d:1.0,(a:1.0,(b:1.0,c:1.0):1.0):1.0):1.0);");
        Tree g = Newick.getTreeFromString("((d:1.0,e:1.0):1.0,(a:1.0,(c:1.0,b:1.0):1.0):1.0)");
        Tree h = Newick.getTreeFromString("(d:1.0,(a:1.0,(b:1.0,(e:1.0,c:1.0):1.0):1.0):1.0)");

        String s = Newick.getStringFromTree(a);
        String t = Newick.getStringFromTree(b);
        String u = Newick.getStringFromTree(c);
        String v = Newick.getStringFromTree(d);
        String w = Newick.getStringFromTree(e);
        String x = Newick.getStringFromTree(f);
        String y = Newick.getStringFromTree(g);

        String z = Newick.getStringFromTree(h);

        System.out.println("Baum a: "+s);
        System.out.println("Baum b: "+t);
        System.out.println("Baum c: "+u);
        System.out.println("Baum d: "+v);
        System.out.println("Baum e: "+w);
        System.out.println("Baum f: "+x);
        System.out.println("Baum g: "+y);
        System.out.println("Baum h: "+z);


        NConsensus eins = new NConsensus();
        NConsensus zwei = new NConsensus();
        NConsensus drei = new NConsensus();
        NConsensus vier = new NConsensus();
        NConsensus fuenf = new NConsensus();

        Tree reseins = eins.consesusTree(new Tree[]{a,b},1.0);
        Tree reszwei = zwei.consesusTree(new Tree[]{c,d},1.0);
        Tree resdrei = drei.consesusTree(new Tree[]{e,f},1.0);
        Tree resvier = vier.consesusTree(new Tree[]{e,g},1.0);
        Tree resfuenf = fuenf.consesusTree(new Tree[]{e,h},1.0);

        v = Newick.getStringFromTree(reseins);
        w = Newick.getStringFromTree(reszwei);
        x = Newick.getStringFromTree(resdrei);
        y = Newick.getStringFromTree(resvier);
        z = Newick.getStringFromTree(resfuenf);

        System.out.println("Baum 1: "+v);
        System.out.println("Baum 2: "+w);
        System.out.println("Baum 3: "+x);
        System.out.println("Baum 4: "+y);
        System.out.println("Baum 5: "+z);

    }


    public void testSCM(){
        /*Tree e = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,f:1.0)");
        //Tree f = Newick.getTreeFromString("(f:1.0,(z:1.0,(c:1.0,(y:1.0,a:1.0):1.0):1.0):1.0)");
        Tree f = Newick.getTreeFromString("((((a:1.0,y:1.0):1.0,c:1.0):1.0,z:1.0):1.0,f:1.0)");
        Tree result = SCM(e, f);
        System.out.println("SCM-Ergebnis von e und f: "+Newick.getStringFromTree(result));*/





        //File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\100\\20\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        File fi = new File ("C:\\Eigene Dateien\\Studium\\7. Semester\\Bachelorarbeit\\SMIDGen_Anika\\500\\50\\Source_Trees\\RaxML\\sm.0.sourceTrees_OptSCM-Rooting.tre");
        //File fi = new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Source_Trees/RaxML/sm.0.sourceTrees_OptSCM-Rooting.tre");
        //Tree realSCM = Newick.getTreeFromFile(new File ("/home/fleisch/Work/data/simulated/SMIDGen_Anika/500/50/Super_Trees/Superfine/RaxML_Source/sm.0.sourceTrees.scmTree.tre"))[0];


        try{
            FileReader re = new FileReader(fi);
            List<Tree> alltrees = new ArrayList<Tree>(Arrays.asList(Newick.getAllTrees(re)));
            //alltrees.get(1).
            //Tree result = getSuperfineConsensus(alltrees);
            Tree result = getPaperConsensus(alltrees);
            System.out.println("Supertree ist "+Newick.getStringFromTree(result));
            //System.out.println(result.vertexCount() - result.getNumTaxa());
            //System.out.println("swenson scm ist "+Newick.getStringFromTree(realSCM));
            //System.out.println(realSCM.vertexCount() - realSCM.getNumTaxa());
            //todo mach was damit :)

            //FN_FP_RateComputer.calculateSumOfRates(result,alltrees.toArray(new Tree[alltrees.size()])); //sum FP has to be 0 for any SCM result [1]
            //FN_FP_RateComputer.calculateRates(result, realSCM,false);

        }
        catch (FileNotFoundException e){
            System.err.println("kein File");
        };




    }

    public Tree CutTree (Tree input, List<String> keep, boolean turn){

        //List<String> originalleaves = new ArrayList<String>();
        List<String> originalleaves = new ArrayList<String>(helpgetLabelsFromNodes(Arrays.asList(input.getLeaves())));
        //makes a list that contains all leaves in the input tree
        //for (TreeNode iter : input.getLeaves()){
        //    originalleaves.add(iter.getLabel());
        //}
        if (keep.isEmpty()) return new Tree();
        Tree output = input.cloneTree();
        List<String> depthFirst = generateDepthFirstListChildren(output);
        List<TreeNode> tokeep = new ArrayList<TreeNode>(helpgetTreeNodesFromLabels(keep, output));
        //Problem mit neuem Objekt
        //for (String x : keep){
        //    tokeep.add(output.getVertex(x));
        //}
        //List<TreeNode> cut = new ArrayList<TreeNode>(Arrays.asList(output.getLeaves()));
        List<TreeNode> cut = new ArrayList<TreeNode>(helpgetTreeNodesFromLabels(depthFirst, output));
        if (turn){
            Collections.reverse(cut);
        }

        for (TreeNode iter : tokeep){
            cut.remove(iter);
        }
        boolean firstpass = true;
        int savewhere;
        if (TreeCut1.isEmpty()) savewhere = 1;
        else savewhere = 2;
        //List<TreeNode> savenodes = new ArrayList<TreeNode>();
        if (cut.size()==0) return output;
        while (cut.size()!=0){
            for (TreeNode iter : cut){

                if (firstpass){
                    String label = "";
                    List<TreeNode> between;
                    List<TreeNode> between2;
                    TreeNode par = iter.getParent();
                    List<String> finallist = new ArrayList<String>();
                    between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                    between2 = new ArrayList<TreeNode>(between);
                    for (TreeNode el : between2){
                        if (!originalleaves.contains(el.getLabel())) between.remove(el);
                    }
                    //while (between.size() <= 1 && !between.get(0).equalsNode(output.getRoot())){
                    while (between.size() <= 1 && between.get(0).getParent()!=null){
                        par = par.getParent();
                        between = new ArrayList<TreeNode>(Arrays.asList(par.getLeaves()));
                        between2 = new ArrayList<TreeNode>(between);
                        for (TreeNode el : between2){
                            if (!originalleaves.contains(el.getLabel())) between.remove(el);
                        }
                        //between = new ArrayList<TreeNode>(Arrays.asList(between.get(0).getParent().getLeaves()));
                    }
                    /*between = new ArrayList<TreeNode>(Arrays.asList(iter.getParent().getLeaves()));
                    if (between.size()==1){
                        between = new ArrayList<TreeNode>(Arrays.asList(iter.getParent().getParent().getLeaves()));
                    }*/
                    between.remove(iter);
                    if (between.size()==0){
                        System.err.println("Fehler bei CutTree");
                    }
                    if (between.size()>1 && par.getChildren().size()>2){
                        finallist.add("Polytomy");
                    }
                    for (TreeNode x : between){
                        finallist.add(x.getLabel());
                    }
                    if (savewhere == 1){
                        TreeCut1.put(label.concat(iter.getLabel()), finallist);
                    }

                    else {
                        TreeCut2.put(label.concat(iter.getLabel()), finallist);
                    }
                }
                output.removeVertex(iter);
            }
            /*for (TreeNode iter : cut){
                output.removeVertex(iter);
            }*/
            firstpass = false;
            cut = new ArrayList<TreeNode>(Arrays.asList(output.getLeaves()));
            for (TreeNode iter : tokeep){
                cut.remove(iter);
            }
        }
        /*if (!output.getRoot().equalsNode(output.findLeastCommonAncestor(tokeep))){
            output.setRoot(output.findLeastCommonAncestor(tokeep));
        }*/
        //TODO !!
        for (TreeNode x : output.vertices()){
            if (x.degree() == 2 && x!=output.getRoot()){
                for (TreeNode y : x.children()){
                    output.addEdge(x.getParent(),y);
                }
                output.removeVertex(x);

            }
        }
        TreeNode newroot = output.findLeastCommonAncestor(tokeep);
        if (!output.getRoot().equalsNode(newroot)){
            output.setRoot(newroot);
            //output.removeEdge(newroot,newroot.getParent());
            output.removeVertex(newroot.getParent());

        }
        return output;
    }

    public List<String> generateDepthFirstListChildren (Tree input){
        List<String> dnodes = new ArrayList<String>();
        TreeNode root = input.getRoot();
        helpgenerateDepthFirstListChildren(input, root, dnodes);

        return dnodes;
    }

    public void helpgenerateDepthFirstListChildren(Tree input, TreeNode cur, List<String> dnodes){
        List<TreeNode> children = new ArrayList<TreeNode>(cur.getChildren());
        for (TreeNode iter : children){
            if (!iter.isLeaf()){
                helpgenerateDepthFirstListChildren(input, iter, dnodes);
            }
            else dnodes.add(iter.getLabel());
        }
    }

    public static void main (String args[]){

        /*GreedySCM hey = new GreedySCM();
        Tree b = Newick.getTreeFromString("((((a:1.0,b:1.0):1.0,c:1.0):1.0,(d:1.0,e:1.0):1.0):1.0,f:1.0)");
        List<String> tryout = hey.generateDepthFirstListChildren(b);
        for (String x : tryout){
            System.out.println(x);
        }*/

        /*LinkedHashMap<String, List<String>> list = new LinkedHashMap<String, List<String>>();
        ArrayList<String> abc = new ArrayList<String>();
        abc.add("a");
        abc.add("b");
        abc.add("c");
        ArrayList<String> ab = new ArrayList<String>();
        ArrayList<String> a = new ArrayList<String>();
        ab.add("a");
        ab.add("b");
        a.add("a");
        list.put("1", abc);
        list.put("2", a);
        list.put("3", abc);
        list.put("4", ab);
        list.put("5", ab);
        for (String str : list.keySet()){
            System.out.println(str + " "+ list.get(str));
        }

        for( Map.Entry<String,String> entry : list.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println(key + " " + value);
        }

        Set<Map.Entry<String, List<String>>> mapValues = list.entrySet();
        int maplength = mapValues.size();
        Collection<List<String>> values = list.values();
        System.out.println(values.size());
        values.toArray();
        for (List<String> x : values){
            System.out.println(x);
        }*/
        //Map.Entry<String,String>[] test = new Map.Entry[maplength];
        //mapValues.toArray(test);
        //for (int iter=maplength-1; iter>=0; iter--){
        //    System.out.println(test[iter].getKey()+" "+test[iter].getValue());
        //}*/


        GreedySCM hey = new GreedySCM();
        hey.testSCM();



        //hey.testCuttingAndInserting();
        //hey.testCompareTreeStructure();




        //hey.testStrictConsensus();

        /*Tree test = Newick.getTreeFromString("((a:1.0,(b:1.0,c:1.0):1.0):1.0,(d:1.0,e:1.0):1.0)");
        TreeNode a = test.findLeastCommonAncestor(test.getVertex("a"), test.getVertex("c"));
        System.out.println("Indegree vom LCA von a und c ist "+a.indegree());
        System.out.println("Degree vom LCA von a und c ist "+a.degree());
        */



        /*test.removeVertex(test.getVertex("a"));
        for (TreeNode x : test.vertices()){
            if (x.degree() == 2 && x!=test.getRoot()){
                for (TreeNode y : x.children()){
                    test.addEdge(x.getParent(),y);
                }
                test.removeVertex(x);
            }
        }*/

        /*TreeNode b = test.getVertex("b");
        TreeNode d = test.getVertex("d");
        TreeNode e = test.getVertex("e");
        List<TreeNode> tokeep = new ArrayList<TreeNode>();
        tokeep.add(b);
        tokeep.add(d);
        tokeep.add(e);
        test = hey.CutTree(test, tokeep);
        String print = Newick.getStringFromTree(test);
        System.out.println(print);*/

        //hey.testCutTree();




    }

}
