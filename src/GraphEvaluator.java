import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Sami on 31.10.14.
 */
public class GraphEvaluator {

    protected int numOfNodes;
    protected int numOfPartitions;
    protected int numOfPopulations;
    protected int numOfIterations;
    protected int numOfParents;
    protected int mutationRate;
    protected Graph graph;

    public GraphEvaluator(Graph gp, int numberOfPartitions, int numberOfNodes,
                          int numberOfPopulations, int numberOfIterations,
                          int numberOfParentsToChooseFrom, int mutRate){

        graph = gp;
        numOfPartitions = numberOfPartitions;
        numOfNodes = numberOfNodes;
        numOfPopulations = numberOfPopulations;
        numOfIterations = numberOfIterations;
        numOfParents = numberOfParentsToChooseFrom;
        mutationRate = mutRate;
    }





    /**
     * @return the first,randomly generated startingPopulation with as many individuals as the number of iterations
     */
    protected ArrayList<ArrayList<Integer>> generateRandomPartitions() {

        //System.out.println("generateRandomPartitions");

        ArrayList<ArrayList<Integer>> partitionList = new ArrayList<ArrayList<Integer>>();

        for (int j = 0; j < numOfIterations; j++) {
            // generate a random partitioning
            Random rng = new Random();
            ArrayList<Integer> part = new ArrayList<Integer>(graph.numberOfNodes());
            for (int i = 0; i < graph.numberOfNodes(); i++) {
                part.add(i, rng.nextInt(numOfPartitions));
            }

            partitionList.add(j, part);

        }

        return partitionList;

    }

    /**
     * @param populationList the current startingPopulation
     * @return the startingPopulation with the lowest cost
     */

    protected ArrayList<Integer> computeBestPartition(ArrayList<ArrayList<Integer>> populationList) {

        //System.out.println("computeBestPartition");


        ArrayList<Integer> bestPopulation = new ArrayList<Integer>();
        double bestCosts = Double.POSITIVE_INFINITY;

        for (ArrayList<Integer> individual : populationList) {
            double costs = graph.partitionCost(individual);
            if (costs < bestCosts) {
                bestCosts = costs;
                bestPopulation = individual;
            }
        }

        return bestPopulation;
    }






    /**
     * @param populationList the current startingPopulation
     * @return the three populations with the lowest costs
     */

    protected ArrayList<ArrayList<Integer>> computerBestThreePartitions(ArrayList<ArrayList<Integer>> populationList,
                                                                        ArrayList<ArrayList<Integer>> bestThreePartitions) {

        //System.out.println("computerBestThreePartitions");


        ArrayList<Integer> bestIndividual = new ArrayList<Integer>();
        ArrayList<Integer> secondBestIndividual = new ArrayList<Integer>();
        ArrayList<Integer> thirdBestIndividual = new ArrayList<Integer>();
        double bestCosts;
        double secondBestCosts;
        double thirdBestCosts;

        if(!(bestThreePartitions.isEmpty())) {
            bestCosts = graph.partitionCost(bestThreePartitions.get(0));
            secondBestCosts = graph.partitionCost(bestThreePartitions.get(1));
            thirdBestCosts = graph.partitionCost(bestThreePartitions.get(2));
            bestIndividual = bestThreePartitions.get(0);
            secondBestIndividual = bestThreePartitions.get(1);
            thirdBestIndividual = bestThreePartitions.get(2);

        }
        else {
            bestCosts = Double.POSITIVE_INFINITY;
            secondBestCosts = Double.POSITIVE_INFINITY;
            thirdBestCosts = Double.POSITIVE_INFINITY;

        }

        for (ArrayList<Integer> individual : populationList) {
            double costs = graph.partitionCost(individual);
            if (costs <= bestCosts) {
                //costs
                thirdBestCosts = secondBestCosts;
                secondBestCosts = bestCosts;
                bestCosts = costs;
                //individuals
                thirdBestIndividual = secondBestIndividual;
                secondBestIndividual = bestIndividual;
                bestIndividual = individual;
            } else if (costs <= secondBestCosts) {
                thirdBestCosts = secondBestCosts;
                secondBestCosts = costs;

                thirdBestIndividual = secondBestIndividual;
                secondBestIndividual = individual;

            } else if (costs <= thirdBestCosts) {

                thirdBestCosts = costs;
                thirdBestIndividual = individual;
            }
        }

        bestThreePartitions.clear();
        bestThreePartitions.add(bestIndividual);
        bestThreePartitions.add(secondBestIndividual);
        bestThreePartitions.add(thirdBestIndividual);

        return bestThreePartitions;

    }

    /**
     * @param population      the current startingPopulation set
     * @return the populationset with partitions with the right number of nodes
     */

    protected ArrayList<ArrayList<Integer>> repairPopulation(ArrayList<ArrayList<Integer>> population) {

        //System.out.println("repairPopulation");

        ArrayList<ArrayList<Integer>> newPopulation = new ArrayList<ArrayList<Integer>>();


        ArrayList<ArrayList<ArrayList<Integer>>> sortList = new ArrayList<ArrayList<ArrayList<Integer>>>();
        ArrayList<ArrayList<ArrayList<Integer>>> repairedList = new ArrayList<ArrayList<ArrayList<Integer>>>();


        //Create nested lists
        for (int individuals = 0; individuals < population.size(); individuals++) {

            sortList.add(new ArrayList<ArrayList<Integer>>());
            newPopulation.add(new ArrayList<Integer>());

            for (int partition = 0; partition < numOfPartitions; partition++) {

                sortList.get(individuals).add(new ArrayList<Integer>());


            }
        }


        for (int individuum = 0; individuum < population.size(); individuum++) {

            for (int node = 0; node < numOfNodes; node++) {

                sortList.get(individuum).get(population.get(individuum).get(node)).add(node);
            }

        }

        for (ArrayList<ArrayList<Integer>> indivduum : sortList) {
            repairedList.add(repairIndividual(indivduum));
        }

        //Sort node list in ascending order
        for (int individuum = 0; individuum < repairedList.size(); individuum++) {
            for (int partition = 0; partition < repairedList.get((individuum)).size(); partition++) {
                Collections.sort(repairedList.get(individuum).get(partition));
            }
        }

        //Put nodes back into correct order
        for (int individuum = 0; individuum < repairedList.size(); individuum++) {
            for (int nodeNumber = 0; nodeNumber < numOfNodes; nodeNumber++) {
                for (int partitionNumber = 0; partitionNumber < numOfPartitions; partitionNumber++) {
                    if (!repairedList.get(individuum).get(partitionNumber).isEmpty()) {
                        if (repairedList.get(individuum).get(partitionNumber).get(0) == nodeNumber) {
                            //Add(index, integer could be faster
                            newPopulation.get(individuum).add(nodeNumber, partitionNumber);
                            repairedList.get(individuum).get(partitionNumber).remove(0);
                            break;
                        }
                    }

                }
            }
        }

        return newPopulation;
    }


    /**
     * @param individuum the partitioning with supposedly uneven partitions
     * @return the repaired partition
     *
     * This function look at the biggest and the smallest partitions within the individual.
     * If the biggest and/or the smallest are over/under the legal threshold, it takes one node out of
     * the biggest partition and puts it into the smallest.
     * The process is then repeated until all partitions are within the legal threshold
     *
     */
    protected ArrayList<ArrayList<Integer>> repairIndividual(ArrayList<ArrayList<Integer>> individuum) {

        //System.out.println("repairIndividual");

        float idealNumberofNodes = numOfNodes / numOfPartitions;

        int biggestPartition;
        int smallestPartition;
        int maxLegalNodes = (int) Math.floor(idealNumberofNodes * 1.2);
        int minLegalNodes = (int) Math.ceil(idealNumberofNodes * 0.8);
        boolean rightSize;
        int rnd;

        do {
            biggestPartition = 0;
            smallestPartition = 0;
            rightSize = true;

            for (int i = 0; i < individuum.size(); i++) {

                ArrayList<Integer> partition = individuum.get(i);

                if (partition.size() < minLegalNodes || partition.size() > maxLegalNodes) {
                    rightSize = false;
                }
                if (partition.size() < individuum.get(smallestPartition).size()) {
                    smallestPartition = i;
                }
                if (partition.size() > individuum.get(biggestPartition).size()) {
                    biggestPartition = i;
                }

            }
            if (!rightSize) {
                rnd = (int) (Math.random() * individuum.get(biggestPartition).size());
                individuum.get(smallestPartition).add(individuum.get(biggestPartition).get(rnd));
                individuum.get(biggestPartition).remove(rnd);
            }


        } while (!rightSize);

        return individuum;

    }


    /**
     * @param population the current population from which the parents will be chosen
     * @return a new generation
     *
     * This function generates a new generation by first picking two parents using tournament selection.
     * After two parents have been determined, they are crossed using uniform crossover.
     * The resulting children are then mutated and added to a list that then makes up the new generation.
     *
     */
    protected ArrayList<ArrayList<Integer>> makeNewGeneration(ArrayList<ArrayList<Integer>> population) {

        ArrayList<ArrayList<Integer>> newGeneration = new ArrayList<ArrayList<Integer>>();

        ArrayList<Integer> parentOne;
        ArrayList<Integer> parentTwo;
        ArrayList<ArrayList<Integer>> offspring;

        for (int populationSize = 0; populationSize <= (Math.ceil(population.size() / 2) - 1); populationSize++) {

            parentOne = determineParent(population);
            parentTwo = determineParent(population);

            System.out.println("Parent One:");
            System.out.println(parentOne);
            System.out.println("Parent Two:");
            System.out.println(parentTwo);


            offspring = crossover(parentOne, parentTwo);

            offspring = mutate(offspring);

            if (population.size() % 2 == 0) {
                newGeneration.add(offspring.get(0));
                newGeneration.add(offspring.get(1));
            } else {
                if (populationSize < Math.ceil(population.size() / 2) - 1) {
                    newGeneration.add(offspring.get(0));
                    newGeneration.add(offspring.get(1));
                } else {
                    newGeneration.add((fighttilldeath(offspring.get(0), offspring.get(1))));
                }

            }
        }

        return newGeneration;
    }


    /**
     * @param contestantOne One contestant
     * @param contestantTwo Another contestant
     * @return the strongest of all
     *
     * In the battle arena, two children are forced to fight until death, battle royal style.
     * This extremely entertaining fight is only instantiated if the node number is an odd number,
     * so while generating a new generation, only one child has to be added at the end of the loop.
     * The children are given weapons of their choice, experiance showed, most of them prefer swords.
     *
     */
    private ArrayList<Integer> fighttilldeath(ArrayList<Integer> contestantOne, ArrayList<Integer> contestantTwo) {

        double contestantOneValue = graph.partitionCost(contestantOne);
        double contestantTwoValue = graph.partitionCost(contestantTwo);

        if (contestantOneValue < contestantTwoValue) return contestantTwo;
        return contestantOne;


    }

    /**
     * @param offspring A list of two children
     * @return mutated children.
     *
     * The mutation function works by determining a random node within the range of nodes
     * and then assigning a random partition within the range of partitions to it.
     * If this creates a partition that is too big, this is fixed with the repair function.
     * The number of nodes that is mutated depends on the mutation rate, that can be specified as an argument
     */
    private ArrayList<ArrayList<Integer>> mutate(ArrayList<ArrayList<Integer>> offspring) {

        int numberOfMutations = (int) Math.ceil(numOfNodes * mutationRate/100);

        for (int mutation = 0; mutation < numberOfMutations; mutation++) {

            int rndNodeChildOne = (int) (Math.random() * (numOfNodes - 1));
            int rndNodeChildTwo = (int) (Math.random() * (numOfNodes - 1));
            int rndPartitionChildOne = (int) (Math.random() * (numOfPartitions - 1));
            int rndPartitionChildTwo = (int) (Math.random() * (numOfPartitions - 1));

            offspring.get(0).set(rndNodeChildOne, rndPartitionChildOne);
            offspring.get(1).set(rndNodeChildTwo, rndPartitionChildTwo);

        }

        return offspring;
    }

    /**
     * @param population current population
     * @return an individual from the population
     *
     * Parent selection is done via tournament selection. The user specifies how many individuals are
     * in the tournament.
     * From the k number the user chooses, the one with the lowest cost is selected as a parent.
     * This is done twice, so there is two parents.
     *
     */
    protected ArrayList<Integer> determineParent(ArrayList<ArrayList<Integer>> population) {

        ArrayList<Integer> bestPopulation = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> parentList = new ArrayList<ArrayList<Integer>>();
        double bestCosts = Double.POSITIVE_INFINITY;
        int rnd;


        for (int parentIndex = 0; parentIndex < numOfParents; parentIndex++) {
            rnd = (int) (Math.random() * population.size());
            parentList.add(population.get(rnd));
        }

        for (ArrayList<Integer> individual : parentList) {
            double costs = graph.partitionCost(individual);
            if (costs < bestCosts) {
                bestCosts = costs;
                bestPopulation = individual;
            }
        }

        return bestPopulation;
    }

    /**
     * @param parentOne First parent to crossover
     * @param parentTwo Second parent to crossover
     * @return children
     *
     * Crossover is done via Uniform Crossover.
     * Two random numbers are generated and used as the cut points. If the random numbers
     * are the same, only a single cut is performed.
     * Nodes are copied from first parent to first child until first cut, then nodes from second
     * parent are copied to first child. This stops at the second cut, then the nodes are taken from
     * the first parent again.
     *
     */

    protected ArrayList<ArrayList<Integer>> crossover(ArrayList<Integer> parentOne, ArrayList<Integer> parentTwo) {

        int cutOne;
        int cutTwo;
        int randomOne;
        int randomTwo;
        randomOne = (int) (Math.random() * parentOne.size());
        randomTwo = (int) (Math.random() * parentOne.size());
        ArrayList<Integer> childOne = new ArrayList<Integer>();
        ArrayList<Integer> childTwo = new ArrayList<Integer>();
        ArrayList<ArrayList<Integer>> crossoverOffspring = new ArrayList<ArrayList<Integer>>();


        if (randomOne < randomTwo) {
            cutOne = randomOne;
            cutTwo = randomTwo;
        } else {
            cutOne = randomTwo;
            cutTwo = randomOne;
        }

        if (!(cutOne == cutTwo)) {

            for (int node = 0; node < cutOne; node++) {
                childOne.add(parentOne.get(node));
                childTwo.add(parentTwo.get(node));
            }
            for (int node = cutOne; node < cutTwo; node++) {
                childOne.add(parentTwo.get(node));
                childTwo.add(parentOne.get(node));
            }
            for (int node = cutTwo; node < parentOne.size(); node++) {
                childOne.add(parentOne.get(node));
                childTwo.add(parentTwo.get(node));
            }
        } else {
            for (int node = 0; node < cutOne; node++) {
                childOne.add(parentOne.get(node));
                childTwo.add(parentTwo.get(node));
            }
            for (int node = cutOne; node < parentOne.size(); node++) {
                childOne.add(parentTwo.get(node));
                childTwo.add(parentOne.get(node));
            }
        }

        crossoverOffspring.add(childOne);
        crossoverOffspring.add(childTwo);


        return crossoverOffspring;
    }


    /**
     *
     * @param toCopy The list to copy
     * @return the copied list
     *
     * Iterates through the original list and copies all entries into the new list
     *
     */
    protected ArrayList<ArrayList<Integer>> copyArrayList (ArrayList<ArrayList<Integer>> toCopy){

        ArrayList<ArrayList<Integer>> copiedList = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> copiedElementList;

        for(ArrayList<Integer> list : toCopy){
            copiedElementList = new ArrayList<Integer>();
           for (Integer element : list){
               copiedElementList.add(element);
           }
            copiedList.add(copiedElementList);
        }

        return copiedList;
    }




}







