package proj_2;

import proj_2.HierarchicalMin;
import proj_2.KMeans;
import proj_2.DBSCAN;

/**
 * @author TANUSHRI NAYAK
 *
 */
public class Main {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String filePath1 = null;
		String filePath2 = null;
		int noOfClusters = 0;
		int iterations = 0;
		String rowIDFilePath = null;

		for (int i = 0; i < args.length; i++) {
			if (args[i].equals("-f1")) {
				filePath1 = args[i + 1];
			} else if (args[i].equals("-f2")) {
				filePath2 = args[i + 1];
			} else if ((args[i].equals("-n"))) {
				noOfClusters = Integer.parseInt(args[i + 1]);

			} else if ((args[i].equals("-r"))) {
				rowIDFilePath = args[i + 1];

			} else if ((args[i].equals("-i"))) {
				iterations = Integer.parseInt(args[i + 1]);

			}

		}
		System.out.println(
				"-------------------------------------------------------KMeans----------------------------------------------------------------------------------");
		KMeans kmeans = new KMeans(filePath1, noOfClusters, rowIDFilePath, iterations);
		kmeans.displayResults();
		System.out.println(
				"-------------------------------------------------------HAC----------------------------------------------------------------------------------");
		HierarchicalMin hier_agglo = new HierarchicalMin(filePath2, noOfClusters);
		hier_agglo.displayResults();
		System.out.println(
				"-------------------------------------------------------DBSCAN----------------------------------------------------------------------------------");
		DBSCAN dbscan = new DBSCAN();
		dbscan.display(filePath1);

	}
}