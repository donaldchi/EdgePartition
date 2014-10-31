import java.io.IOException;
import java.util.ArrayList;

public class GraphTest
{
    protected static int numOfPartitions;
    protected static int numOfIterations;
    protected static ArrayList<ArrayList<Integer>> population;
    protected static ArrayList<ArrayList<Integer>> bestThreePopulations;

    protected static ArrayList<Integer> bestPartition;


    public static void main(String[] args) {

        numOfPartitions = Integer.parseInt(args[1]);
        numOfIterations = Integer.parseInt(args[2]);

		if(args.length != 3) {
			System.out.println("usage: Graph <datafile> <number of partitions> <number of iterations>");
			System.exit(1);
		}


		Graph gp = new Graph();
        GraphEvaluator graphEvaluator = new GraphEvaluator();
		try {
			gp.readGraphFile(args[0]);
		} catch(IOException e) {
            System.out.println("Error parsing file " + args[0]);
            e.printStackTrace();
        }

        //Start population
        population =  graphEvaluator.generateRandomPartitions(gp, numOfPartitions, numOfIterations);
        //repairfunction
        population = graphEvaluator.repairPopulation(population, gp.numberOfNodes(), numOfPartitions);
        bestThreePopulations = graphEvaluator.computerBestThreePartitions(gp, population);

        bestPartition = graphEvaluator.computeBestPartition(gp, bestThreePopulations);


		System.out.println("partition: " + bestPartition);
		System.out.println("cost: " + gp.partitionCost(bestPartition));
	}





}
