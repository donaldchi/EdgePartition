import java.io.IOException;
import java.util.ArrayList;

public class GraphTest
{
    protected static int numOfPartitions;
    protected static int numOfPopulations;
    protected static int numOfIterations;
    protected static int numOfParentsToChooseFrom;
    protected static int mutationRate;
    protected static ArrayList<ArrayList<Integer>> startingPopulation;
    protected static ArrayList<ArrayList<Integer>> offspringGeneration;

    protected static ArrayList<ArrayList<Integer>> bestThreePartitions = new ArrayList<ArrayList<Integer>>();
    protected static ArrayList<ArrayList<Integer>> tempBestThreePartitions;
    protected static ArrayList<Integer> bestPartition;

    public static void main(String[] args) {

        int bestPartitionIteration = 0;
        int secondPartitionIteration = 0;
        int thirdPartitionIteration = 0;

        numOfPartitions = Integer.parseInt(args[1]);
        numOfPopulations = Integer.parseInt(args[2]);
        numOfIterations = Integer.parseInt(args[3]);
        numOfParentsToChooseFrom = Integer.parseInt(args[4]);
        mutationRate = Integer.parseInt(args[5]);

		if(args.length != 6) {
			System.out.println("usage: Graph <datafile> <number of partitions> <number of starting populations> <number of iterations> <number of candidates in tournament selection> <mutation rate in percent>");
			System.exit(1);
		}

		Graph gp = new Graph();
		try {
			gp.readGraphFile(args[0]);
		} catch(IOException e) {
            System.out.println("Error parsing file " + args[0]);
            e.printStackTrace();
        }
        GraphEvaluator graphEvaluator = new GraphEvaluator(gp, numOfPartitions, gp.numberOfNodes(),
                numOfPopulations, numOfIterations, numOfParentsToChooseFrom, mutationRate);

        //Start startingPopulation
        startingPopulation =  graphEvaluator.generateRandomPartitions();
        startingPopulation = graphEvaluator.repairPopulation(startingPopulation);

        bestThreePartitions = graphEvaluator.computerBestThreePartitions(startingPopulation, bestThreePartitions);

        offspringGeneration = graphEvaluator.makeNewGeneration(startingPopulation);
        offspringGeneration = graphEvaluator.repairPopulation(offspringGeneration);

        bestThreePartitions = graphEvaluator.computerBestThreePartitions(offspringGeneration, bestThreePartitions);


        for (int iterations = 0; iterations < numOfIterations-1; iterations++){

            System.out.println(iterations + " ");
            offspringGeneration = graphEvaluator.makeNewGeneration(offspringGeneration);
            offspringGeneration = graphEvaluator.repairPopulation(offspringGeneration);

            tempBestThreePartitions = graphEvaluator.copyArrayList(bestThreePartitions);

            bestThreePartitions = graphEvaluator.computerBestThreePartitions(offspringGeneration, bestThreePartitions);

            if(!(tempBestThreePartitions.get(0).equals(bestThreePartitions.get(0)))) {
                bestPartitionIteration = iterations;
            }
            if(!(tempBestThreePartitions.get(1).equals(bestThreePartitions.get(1)))) {
                secondPartitionIteration = iterations;
            }
            if(!(tempBestThreePartitions.get(2).equals(bestThreePartitions.get(2)))) {
                thirdPartitionIteration = iterations;
            }

        }

        bestPartition = graphEvaluator.computeBestPartition(bestThreePartitions);



        System.out.println("best three partitions:\t" + bestThreePartitions.get(0));
        System.out.println("cost of best partition: " + gp.partitionCost(bestThreePartitions.get(0)));
        System.out.println("Generated at " + bestPartitionIteration + " iteration");
        System.out.println("\t \t \t \t \t \t" + bestThreePartitions.get(1));
        System.out.println("cost of second best partition: " + gp.partitionCost(bestThreePartitions.get(1)));
        System.out.println("Generated at " + secondPartitionIteration + " iteration");
        System.out.println("\t \t \t \t \t \t" + bestThreePartitions.get(2));
        System.out.println("cost of third best partition: " + gp.partitionCost(bestThreePartitions.get(2)));
        System.out.println("Generated at " + thirdPartitionIteration + " iteration");







    }





}
