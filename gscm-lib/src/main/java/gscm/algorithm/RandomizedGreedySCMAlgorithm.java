/*
 * GSCM-Project
 * Copyright (C)  2016. Chair of Bioinformatics, Friedrich-Schilller University Jena.
 *
 * This file is part of the GSCM-Project.
 *
 * The GSCM-Project is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The GSCM-Project is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GSCM-Project.  If not, see <http://www.gnu.org/licenses/>;.
 *
 */
package gscm.algorithm;

import gscm.algorithm.treeSelector.*;
import phyloTree.model.tree.Tree;
import utils.parallel.ParallelUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by Markus Fleischauer (markus.fleischauer@gmail.com) on 24.03.15.
 */
public class RandomizedGreedySCMAlgorithm extends MultiResultsSCMAlgorithm {
//    private int iterations = 0;
    private int individualIterations = 0;

    public RandomizedGreedySCMAlgorithm(TreeScorer... scorer) {
        this(null, scorer);
    }

    public RandomizedGreedySCMAlgorithm(Tree[] trees, TreeScorer... scorer) {
        this(0, trees, scorer);
    }

    public RandomizedGreedySCMAlgorithm(int numberOfIterations, TreeScorer... scorer) {
        this(numberOfIterations, null, scorer);
    }

    public RandomizedGreedySCMAlgorithm(int numberOfIterations, Tree[] trees, TreeScorer... scorer) {
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

    protected List<Tree> calculateSequencial() throws InsufficientOverlapException {
        final int iterations = getIterations();
        final GreedyTreeSelector nonRandomResultSelector = GreedyTreeSelector.FACTORY.getNewSelectorInstance();
        nonRandomResultSelector.setInputTrees(inputTrees);
        final RandomizedGreedyTreeSelector randomResultSelector = RandomizedGreedyTreeSelector.FACTORY.getNewSelectorInstance();
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
        TreeSelectorFactory.shutdown(nonRandomResultSelector);
        TreeSelectorFactory.shutdown(randomResultSelector);
        return superTrees;
    }

    protected List<Tree> calculateParallel() {
        final int iterations = getIterations();
        List<Tree> superTrees = new ArrayList<>(numOfJobs());
        List<Future<List<Tree>>> futurList = new LinkedList<>();


        //calculate random results
        GSCMCallableFactory randomFactory = new GSCMCallableFactory(RandomizedGreedyTreeSelector.FACTORY, inputTrees);
        for (int i = 0; i < iterations; i++) {
            futurList.addAll(
                    ParallelUtils.parallelForEach(executorService, randomFactory, Arrays.asList(scorerArray)));
//                    ParallelUtils.parallelBucketForEach(executorService, randomFactory, Arrays.asList(scorerArray))); //maybe buckets parallelism is faster?
        }

        //calculate nonRandomResults
        GSCMCallableFactory nonRandomFactory = new GSCMCallableFactory(GreedyTreeSelector.FACTORY, inputTrees);
        futurList.addAll(
                ParallelUtils.parallelForEach(executorService, nonRandomFactory, Arrays.asList(scorerArray)));
//                ParallelUtils.parallelBucketForEach(executorService, nonRandomFactory, Arrays.asList(scorerArray)));

        //collect results
        try {
            for (Future<List<Tree>> future : futurList) {
                superTrees.addAll(future.get());
            }
            return superTrees;
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        nonRandomFactory.shutdownSelectors();
        randomFactory.shutdownSelectors();
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

    @Override
    protected String name() {
        return getClass().getSimpleName();
    }
}
