import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Sami on 31.10.14.
 */
public class GraphEvaluator {

   /* protected ArrayList<Integer> evolveGraph(ArrayList<>){



    }
*/


    /**
     *
     * @param gp the used Graph
     * @param numOfPartitions the number of partitions
     * @param numOfIterations the number of iterations
     * @return the first,randomly generated population with as many individuals as the number of iterations
     */
    protected ArrayList<ArrayList<Integer>> generateRandomPartitions(Graph gp, int numOfPartitions, int numOfIterations ){

        ArrayList<ArrayList<Integer>> partitionList = new ArrayList<ArrayList<Integer>>();

        for(int j = 0; j < numOfIterations; j++) {
            // generate a random partitioning
            Random rng = new Random();
            ArrayList<Integer> part = new ArrayList<Integer>(gp.numberOfNodes());
            for (int i = 0; i < gp.numberOfNodes(); i++) {
                part.add(i, rng.nextInt(numOfPartitions));
            }

            partitionList.add(j, part);

        }

        return partitionList;

    }

    /**
     *
     * @param gp the used Graph
     * @param populationList the current population
     * @return the population with the lowest cost
     */


    protected ArrayList<Integer> computeBestPartition(Graph gp, ArrayList<ArrayList<Integer>> populationList){

        ArrayList<Integer> bestPopulation = new ArrayList<Integer>();
        double bestCosts = Double.POSITIVE_INFINITY;

        for( ArrayList<Integer> individual : populationList ){
            double costs = gp.partitionCost(individual);
            if (costs < bestCosts){
                bestCosts = costs;
                bestPopulation = individual;
            }
        }

        return bestPopulation;
    }

    /**
     *
     * @param gp the used Graph
     * @param populationList the current population
     * @return the three populations with the lowest costs
     */
    protected ArrayList<ArrayList<Integer>> computerBestThreePartitions(Graph gp, ArrayList<ArrayList<Integer>> populationList){

        ArrayList<ArrayList<Integer>> bestThreePartitions = new ArrayList<ArrayList<Integer>>();
        ArrayList<Integer> bestIndividual = new ArrayList<Integer>();
        ArrayList<Integer> secondBestIndividual = new ArrayList<Integer>();
        ArrayList<Integer> thirdBestIndividual = new ArrayList<Integer>();

        double bestCosts = Double.POSITIVE_INFINITY;
        double secondBestCosts = Double.POSITIVE_INFINITY;
        double thirdBestCosts = Double.POSITIVE_INFINITY;



        for( ArrayList<Integer> individual : populationList ){
            double costs = gp.partitionCost(individual);
            if (costs <= bestCosts){
                //costs
                thirdBestCosts = secondBestCosts;
                secondBestCosts = bestCosts;
                bestCosts = costs;
                //individuals
                thirdBestIndividual = secondBestIndividual;
                secondBestIndividual = bestIndividual;
                bestIndividual = individual;
            }
            else if(costs <= secondBestCosts){
                thirdBestCosts = secondBestCosts;
                secondBestCosts = costs;

                thirdBestIndividual = secondBestIndividual;
                secondBestIndividual = individual;

            }
            else if (costs <= thirdBestCosts){

                thirdBestCosts = costs;
                thirdBestIndividual = individual;
            }
        }

        bestThreePartitions.add(bestIndividual);
        bestThreePartitions.add(secondBestIndividual);
        bestThreePartitions.add(thirdBestIndividual);

        return bestThreePartitions;

    }

    /**
     *
     * @param population the current population set
     * @param numOfNodes the number of nodes within the graph
     * @param numOfPartitions the number of partitions of the graph
     * @return the populationset with partitions with the right number of nodes
     */

    protected ArrayList<ArrayList<Integer>> repairPopulation(ArrayList<ArrayList<Integer>> population,
                                                             int numOfNodes, int numOfPartitions) {

        //TODO: Check the number of nodes within the populations. If a population has too little nodes, check for
        //TODO: populations with more nodes than the ideal number and add some of them to the population with
        //TODO: too little nodes.
        //TODO: If a population has too many nodes, proceed vice versa
        float idealNumOfNodes = numOfNodes/numOfPartitions;
        int maxLegalNodes = (int) Math.floor(idealNumOfNodes * 1.2);
        int minLegalNodes = (int) Math.ceil(idealNumOfNodes * 0.8);





        return null;
    }




}
