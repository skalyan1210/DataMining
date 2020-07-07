package proj_2;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author TANUSHRI NAYAK
 *
 */
public class KMeans {

	BufferedReader bufferedReader;
	String fileName, rowIDFilePath = null;
	TreeMap<Integer, ArrayList<Float>> labelsMap = new TreeMap<Integer, ArrayList<Float>>();
	ArrayList<Integer> groundTruthList = new ArrayList<Integer>();
	ArrayList<Integer> clusterResultsList = new ArrayList<Integer>();
	List<ArrayList<Float>> genesList = new ArrayList<ArrayList<Float>>();
	List<ArrayList<Float>> clusterCentroidsList = new ArrayList<ArrayList<Float>>();
	List<ArrayList<Float>> oldCentroidsList;
	HashMap<ArrayList<Float>, ArrayList<ArrayList<Float>>> centroidsPointsList = new HashMap<ArrayList<Float>, ArrayList<ArrayList<Float>>>();
	HashMap<ArrayList<Float>, Integer> clusterIdMap = new HashMap<ArrayList<Float>, Integer>();
	int MAX_ITERATIONS;
	int numberOfClusters;
	int iterations;
	public int count = 0;

	/**
	 * @param filePath
	 * @param noOfClusters
	 * @param rowIDFilePath
	 * @param iterations
	 */
	public KMeans(String filePath, int noOfClusters, String rowIDFilePath, int iterations) {
		fileName = filePath;
		if (rowIDFilePath != null) {
			this.rowIDFilePath = rowIDFilePath;
		}
		if (iterations != 0) {
			MAX_ITERATIONS = iterations;
		} else {
			MAX_ITERATIONS = 50;
		}
		this.numberOfClusters = noOfClusters;
		try {
			bufferedReader = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * populateData
	 */
	void populateData() {
		String line = null;
		try {
			ArrayList<Float> geneList = null;
			while ((line = bufferedReader.readLine()) != null) {
				count++;
				geneList = new ArrayList<Float>();
				String cols[] = line.split("\t");

				groundTruthList.add(Integer.parseInt(cols[1]));

				for (int i = 2; i < cols.length; i++) {
					geneList.add(Float.parseFloat(cols[i]));
				}
				genesList.add(geneList);
			}

			bufferedReader.close();
			// generate random cluster centroids
			if (rowIDFilePath == null) {
				Random random = new Random();
				int k;
				for (int j = 0; clusterCentroidsList.size() < numberOfClusters; j++) {
					k = random.nextInt(count);
					if (!clusterCentroidsList.contains(genesList.get(k))) {
						clusterCentroidsList.add(genesList.get(k));
					}
				}
			} else {
				String str = null;
				BufferedReader bufferedReader = new BufferedReader(new FileReader(rowIDFilePath));
				while ((str = bufferedReader.readLine()) != null) {
					clusterCentroidsList.add(genesList.get(Integer.parseInt(str)));
				}
				bufferedReader.close();

			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * updateClusters
	 */
	private void updateClusters() {
		List<Double> distanceList = new ArrayList<Double>();
		HashMap<Double, Integer> distancesMap = new HashMap<Double, Integer>();
		centroidsPointsList.clear();

		for (int i = 0; i < genesList.size(); i++) {
			distanceList.clear();
			distancesMap.clear();
			for (int j = 0; j < numberOfClusters; j++) {
				double dist = getEuclideanDistance(genesList.get(i), clusterCentroidsList.get(j));
				distancesMap.put(dist, j);
				distanceList.add(dist);
			}

			Collections.sort(distanceList);
			// get the index of the centroid with min distance
			int temp;
			temp = distancesMap.get(distanceList.get(0));
			labelsMap.put(i, clusterCentroidsList.get(temp));

			if (!centroidsPointsList.containsKey(clusterCentroidsList.get(temp))) {
				ArrayList<ArrayList<Float>> arrListEntities = new ArrayList<ArrayList<Float>>();
				arrListEntities.add(genesList.get(i));
				centroidsPointsList.put(clusterCentroidsList.get(temp), arrListEntities);
			} else {
				ArrayList<ArrayList<Float>> entitiesArrList = centroidsPointsList.get(clusterCentroidsList.get(temp));
				entitiesArrList.add(genesList.get(i));
				centroidsPointsList.put(clusterCentroidsList.get(temp), entitiesArrList);
			}
		}
	}

	/**
	 * createCluster
	 */
	void createCluster() {
		populateData();
		updateClusters();

		iterations = 0;
		while (!checkTermination(oldCentroidsList, iterations)) {
			// make a copy of old centroids
			oldCentroidsList = new ArrayList<ArrayList<Float>>(clusterCentroidsList);
			// get new centroids
			updateCentroids();
			updateClusters();

			iterations++;

		}

	}

	/**
	 * @param oldCentroidsList
	 * @param iterations
	 * @return true/false
	 */
	boolean checkTermination(List<ArrayList<Float>> oldCentroidsList, int iterations) {

		if (iterations > MAX_ITERATIONS)
			return true;

		List<ArrayList<Float>> arrList = new ArrayList<ArrayList<Float>>(clusterCentroidsList);
		if (!(oldCentroidsList == null)) {
			arrList.removeAll(oldCentroidsList);
			if (arrList.isEmpty())
				return true;
			return false;
		} else
			return false;

	}

	/**
	 * updateCentroids
	 */
	private void updateCentroids() {
		clusterCentroidsList.clear();

		for (ArrayList<Float> keyList : centroidsPointsList.keySet()) {
			ArrayList<Float> clusterCenter = new ArrayList<Float>();
			float val;
			ArrayList<ArrayList<Float>> points = centroidsPointsList.get(keyList);
			for (int j = 0; j < points.get(0).size(); j++) {
				val = 0;
				for (int i = 0; i < points.size(); i++) {
					val += points.get(i).get(j);

				}
				clusterCenter.add(val / points.size());
			}
			clusterCentroidsList.add(clusterCenter);
		}

	}

	/**
	 * assignClusterID
	 */
	private void assignClusterID() {
		ArrayList<Float> val;
		int i = 1;
		for (int key : labelsMap.keySet()) {
			val = labelsMap.get(key);
			if (!clusterIdMap.containsKey(val)) {
				clusterIdMap.put(val, i);
				i++;
			}
		}
	}

	/**
	 * displayResults for RandIndex and Jaccard co-efficient
	 */
	public void displayResults() {
		createCluster();
		assignClusterID();
		System.out.println(iterations);
		Writer writer;
		try {
			writer = new FileWriter("/Users/saikalyan/Documents/ClusterResult_kmeans.txt");
			for (int key : labelsMap.keySet()) {
				clusterResultsList.add(clusterIdMap.get(labelsMap.get(key)));
				writer.write(String.valueOf(clusterIdMap.get(labelsMap.get(key))));
				writer.write("\r\n");
				System.out.println(key + " : " + clusterIdMap.get(labelsMap.get(key)));
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		ExternalValidator extValidation = new ExternalValidator(count, groundTruthList, clusterResultsList);

		float res = extValidation.getCoefficient();
		System.out.println("Rand Index------------" + res);

		float jaccard = extValidation.getJaccardCoeff();
		System.out.println("Jaccard co-efficient------------" + jaccard);

	}

	/**
	 * @param point1
	 * @param point2
	 * @return distance
	 */
	double getEuclideanDistance(ArrayList<Float> point1, ArrayList<Float> point2) {
		double distance = 0;
		for (int i = 0; i < point1.size(); i++) {
			double diffSquare = Math.pow((point1.get(i) - point2.get(i)), 2);
			distance += diffSquare;
		}
		return Math.sqrt(distance);

	}
}
