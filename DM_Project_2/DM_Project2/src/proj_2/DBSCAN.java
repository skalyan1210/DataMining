package proj_2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import proj_2.DBSCAN_Functions;
/**
 * @author SAI KALYAN KATTA	
 */
public class DBSCAN {
	static ArrayList<Integer> groundTruthList = new ArrayList<Integer>(); //ground truth values from the dataset
	static List<ArrayList<Float>> genesList = new ArrayList<ArrayList<Float>>();//the values from the dataset without id and the groundtruth
	static Map<Integer,Integer> cluster_ids = new HashMap<Integer,Integer>();//the final cluster ids of the genes
	static ArrayList<ArrayList<Integer>> clusterList= new ArrayList<ArrayList<Integer>>(); // the list of clusters (values)
	static ArrayList<Integer> visited= new ArrayList<Integer>(); //to keep a count of all the genes that are visited
	static ArrayList<Integer> clusterResult= new ArrayList<Integer>(); //the list with only the cluster id values
	static double eps= 1.03;
	static int minpts = 4;
	static String outputPath = "/Users/saikalyan/Documents/DBSCAN_output.txt";
	public static void display(String input_path) 
	{	try
		{BufferedReader bufferedReader;
		//System.out.println("Enter the input file path:");
		//Scanner sc = new Scanner(System.in);
		//String input_path = sc.nextLine(); 
		bufferedReader = new BufferedReader(new FileReader(input_path));
		String line = null;
		ArrayList<Float> geneList = null;
		while ((line = bufferedReader.readLine()) != null) {
			geneList = new ArrayList<Float>();
			String cols[] = line.split("\t");
			groundTruthList.add(Integer.parseInt(cols[1]));
			for (int i = 2; i < cols.length; i++) {
				geneList.add(Float.parseFloat(cols[i]));
			}
			genesList.add(geneList);
		}
		bufferedReader.close();
		DBSCAN_Functions.DBSCAN_Algo(genesList,cluster_ids,clusterList,visited,eps,minpts); // pass the genes values, the cluster id hashmap(to store the cluster id),clusterlist to store the clusters,visted list to keep a count on the genes that are visited 
		System.out.println("------------------------------");
		System.out.println("No. of clusters formed: "+clusterList.size());
		//System.out.println("------------------------------");
		//System.out.println("Enter the output file path:");
		//String output_path = sc.nextLine(); 
		//sc.close();
		File file = new File(outputPath);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bufferedWriter = new BufferedWriter(fw);
		if(!file.exists())
			file.createNewFile();
		
		for( int i=0; i< cluster_ids.size();i++)
		{
			bufferedWriter.write(cluster_ids.get(i)+"\n");
			clusterResult.add(cluster_ids.get(i));
		}
		bufferedWriter.close();
		System.out.println("------------------------------");
		ExternalValidator extValidation = new ExternalValidator(genesList.size(), groundTruthList, clusterResult);

		float res = extValidation.getCoefficient();
		System.out.println("Rand Index = " + res);

		float jaccard = extValidation.getJaccardCoeff();
		System.out.println("Jaccard co-efficient = " + jaccard);
		System.out.println("Gene_ID: ClusterID");
		for(int i =0; i< cluster_ids.size();i++)
		{	System.out.println((i+1)+": "+cluster_ids.get(i));
		}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}

