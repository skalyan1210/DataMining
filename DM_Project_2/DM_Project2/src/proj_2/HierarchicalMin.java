package proj_2;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * @author TANUSHRI NAYAK
 *
 */
public class HierarchicalMin {

	static BufferedReader bufferedReader;
	static int numberOfClusters;
	static ArrayList<Integer> groundTruthArrayList = new ArrayList<Integer>();
	static String filePathName = null;

	/**
	 * @param filePath
	 * @param noOfClusters
	 */
	public HierarchicalMin(String filePath, int noOfClusters) {
		filePathName = filePath;
		numberOfClusters = noOfClusters;
	}

	/**
	 * Displays result of clustering solution
	 */
	public static void displayResults() {
		ArrayList<ArrayList<Float>> rowsInInputFile = readFromFile();
		double[][] distanceMatrix = distanceMatrix(rowsInInputFile);
		int[] closestClusterArray = findCloseCluster(distanceMatrix);
		System.out.println("Order in which clusters are merged:");
		mergeClusters(distanceMatrix, closestClusterArray);
	}

	/**
	 * Read each gene from the file
	 * 
	 * @return
	 */
	static ArrayList<ArrayList<Float>> readFromFile() {
		ArrayList<ArrayList<Float>> allGeneRowsArrayList = new ArrayList<ArrayList<Float>>();
		try {
			bufferedReader = new BufferedReader(new FileReader(filePathName));
			String line = bufferedReader.readLine();
			ArrayList<Float> gene = null;

			while (line != null) {
				String row[] = line.split("\t");
				groundTruthArrayList.add(Integer.parseInt(row[1]));
				gene = new ArrayList<Float>();
				for (String geneData : row)
					gene.add(Float.valueOf(geneData));
				allGeneRowsArrayList.add(gene);
				line = bufferedReader.readLine();
			}
		} catch (IOException e) {
			try {
				bufferedReader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return allGeneRowsArrayList;
	}

	/**
	 * Computes distance matrix considering each data point to be a cluster
	 * 
	 * @param allGeneRowsArrayList
	 * @return
	 */
	static double[][] distanceMatrix(ArrayList<ArrayList<Float>> allGeneRowsArrayList) {
		int n = allGeneRowsArrayList.size();
		double[][] distanceMatrix = new double[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j)
					distanceMatrix[i][j] = -1;
				else
					distanceMatrix[i][j] = findDistance(allGeneRowsArrayList.get(i), allGeneRowsArrayList.get(j));
			}
		}
		return distanceMatrix;
	}

	/**
	 * Finds the nearest cluster for each gene data point
	 * 
	 * @param distanceMatrix
	 * @return
	 */
	private static int[] findCloseCluster(double[][] distanceMatrix) {
		int n = distanceMatrix.length;
		int[] closestClusterArray = new int[n];
		closestClusterArray[0] = 1;
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i != j) {
					if (distanceMatrix[i][j] < distanceMatrix[i][closestClusterArray[i]])
						closestClusterArray[i] = j;
				}
			}
		}
		return closestClusterArray;
	}

	/**
	 * Computes Euclidean distance between two clusters
	 * 
	 * @param gene1
	 * @param gene2
	 * @return
	 */
	static double findDistance(ArrayList<Float> gene1, ArrayList<Float> gene2) {
		int n = gene1.size();
		double distanceSum = 0.0;
		for (int i = 2; i < n; i++) {
			double diff = gene1.get(i) - gene2.get(i);
			distanceSum = distanceSum + (diff * diff);
		}
		double euclidean_distance = Math.sqrt(distanceSum);
		return euclidean_distance;
	}

	/**
	 * Merges two closest clusters. Updates the distance matrix.
	 * 
	 * @param distanceMatrix
	 * @param closestClusterArray
	 */
	static void mergeClusters(double[][] distanceMatrix, int[] closestClusterArray) {
		int n = distanceMatrix.length;
		ArrayList<ArrayList<Integer>> aggregatedList = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> clusteringOrderList = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < n; i++) {
			ArrayList<Integer> innerList = new ArrayList<Integer>();
			innerList.add(i);
			aggregatedList.add(innerList);
			ArrayList<Integer> arr = new ArrayList<Integer>();
			arr.add(0);
			clusteringOrderList.add(arr);
		}
		for (int i = 0; i < n; i++) {
			int counter = 0;
			// Count the number of clusters formed.
			for (int j = 0; j < n; j++) {
				if (aggregatedList.get(j).contains(-1))
					counter++;
			}
			if (counter >= n - numberOfClusters)
				break;
			// Find the closest cluster pair.
			int rowIndex1 = 0, rowIndex2 = 0;
			for (int j = 0; j < n; j++) {
				if (distanceMatrix[j][closestClusterArray[j]] < distanceMatrix[rowIndex1][closestClusterArray[rowIndex1]])
					if (distanceMatrix[j][closestClusterArray[j]] != -1)
						rowIndex1 = j;
			}
			rowIndex2 = closestClusterArray[rowIndex1];
			// store the order of clustering
			aggregatedList.get(rowIndex1).addAll(aggregatedList.get(rowIndex2));
			aggregatedList.get(rowIndex2).add(-1);
			clusteringOrderList.set(i, aggregatedList.get(rowIndex1));
			System.out.println(clusteringOrderList.get(i));
			// update the distance matrix
			for (int j = 0; j < n; j++) {
				if ((distanceMatrix[rowIndex2][j] < distanceMatrix[rowIndex1][j]))
					if ((distanceMatrix[rowIndex2][j] != -1))
						distanceMatrix[rowIndex1][j] = distanceMatrix[rowIndex2][j];
			}

			for (int j = 0; j < n; j++) {
				distanceMatrix[j][rowIndex1] = distanceMatrix[rowIndex1][j];
			}

			for (int j = 0; j < n; j++) {
				distanceMatrix[rowIndex2][j] = -1;
				distanceMatrix[j][rowIndex2] = -1;
			}
			closestClusterArray[rowIndex1] = 0;
			if (rowIndex1 == 0)
				closestClusterArray[rowIndex1] = 1;
			for (int j = 0; j < n; j++) {
				if (closestClusterArray[j] == rowIndex2)
					closestClusterArray[j] = rowIndex1;
				while (distanceMatrix[rowIndex1][closestClusterArray[rowIndex1]] == -1) {
					if (closestClusterArray[rowIndex1] < n - 1)
						closestClusterArray[rowIndex1] = closestClusterArray[rowIndex1] + 1;
					else
						closestClusterArray[rowIndex1] = 0;
				}
				if (distanceMatrix[rowIndex1][j] < distanceMatrix[rowIndex1][closestClusterArray[rowIndex1]])
					if (distanceMatrix[rowIndex1][j] != -1)
						closestClusterArray[rowIndex1] = j;
			}
		}
		ArrayList<Integer> clusterResultList = new ArrayList<Integer>(n);
		for (int i = 0; i < n; i++) {
			clusterResultList.add(0);
		}
		ArrayList<ArrayList<Integer>> clustersList = new ArrayList<ArrayList<Integer>>();
		// Here, each cluster = arraylist
		for (int i = 0; i < n; i++) {
			if (!aggregatedList.get(i).contains(-1)) {
				ArrayList<Integer> eachClusterList = new ArrayList<Integer>(aggregatedList.get(i));
				clustersList.add(eachClusterList);
			}
		}
		try {
			// Write the clusterResult to a txt file - for visualization in PCA
			Writer w = new FileWriter("/Users/saikalyan/Documents/hierMin-result.txt");
			// Format input for RandIndex
			for (int i = 0; i < clustersList.size(); i++) {
				ArrayList<Integer> each_cluster = clustersList.get(i);
				for (int j = 0; j < each_cluster.size(); j++) {
					clusterResultList.set(each_cluster.get(j), i + 1);
					w.write(String.valueOf(i + 1));
					w.write("\r\n");
				}
			}
			w.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = n - numberOfClusters + 1; i < n; i++) {
			// Find the closest cluster pair
			int index1 = 0, index2;
			for (int j = 0; j < n; j++) {
				if (distanceMatrix[j][closestClusterArray[j]] < distanceMatrix[index1][closestClusterArray[index1]])
					if (distanceMatrix[j][closestClusterArray[j]] != -1)
						index1 = j;
			}
			index2 = closestClusterArray[index1];

			// Store the order of clustering
			aggregatedList.get(index1).addAll(aggregatedList.get(index2));
			aggregatedList.get(index2).add(-1);
			clusteringOrderList.set(i, aggregatedList.get(index1));
			System.out.println(clusteringOrderList.get(i));

			if (i == n - 1)
				break;

			// Update the distance matrix
			for (int j = 0; j < n; j++) {
				if ((distanceMatrix[index2][j] < distanceMatrix[index1][j]) && (distanceMatrix[index2][j] != -1)) {
					distanceMatrix[index1][j] = distanceMatrix[index2][j];
				}
			}

			for (int j = 0; j < n; j++) {
				distanceMatrix[j][index1] = distanceMatrix[index1][j];
			}

			for (int j = 0; j < n; j++) {
				distanceMatrix[index2][j] = -1;
				distanceMatrix[j][index2] = -1;
			}

			closestClusterArray[index1] = 0;
			if (index1 == 0)
				closestClusterArray[index1] = 1;
			for (int j = 0; j < n; j++) {
				if (closestClusterArray[j] == index2)
					closestClusterArray[j] = index1;
				while (distanceMatrix[index1][closestClusterArray[index1]] == -1) {
					if (closestClusterArray[index1] < n - 1)
						closestClusterArray[index1] = closestClusterArray[index1] + 1;
					else
						closestClusterArray[index1] = 0;
				}
				if (distanceMatrix[index1][j] < distanceMatrix[index1][closestClusterArray[index1]])
					if (distanceMatrix[index1][j] != -1)
						closestClusterArray[index1] = j;
			}
		}

		System.out.println("\n" + numberOfClusters + " clusters:");
		System.out.println("\nsizeOfClusters: " + " cluster");
		for (int i = 0; i < clustersList.size(); i++) {
			System.out.print(+clustersList.get(i).size() + ": ");
			System.out.println(clustersList.get(i));
		}

		// Rand Index and Jaccard Coefficient
		ExternalValidator extValidation = new ExternalValidator(n, groundTruthArrayList, clusterResultList);

		float res = extValidation.getCoefficient();
		System.out.println("Rand Index------------" + res);

		float jaccard = extValidation.getJaccardCoeff();
		System.out.println("Jaccard co-efficient------------" + jaccard);

	}
}
