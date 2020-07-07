package proj_2;

import java.util.ArrayList;
/**
 * @author TANUSHRI NAYAK
 *
 */
public class ExternalValidator {

	int[][] C;
	int[][] P;
	int SS;
	int DD;
	int SD;
	int DS;
	int dataSize;
	ArrayList<Integer> groundTruthList;
	ArrayList<Integer> clusterResultsList;
	float randIndex;
	float jaccardCoefficient;

	/**
	 * @param size
	 * @param groundTruthList
	 * @param clusterResultsList
	 */
	public ExternalValidator(int size, ArrayList<Integer> groundTruthList, ArrayList<Integer> clusterResultsList) {

		C = new int[size][size];
		P = new int[size][size];
		SS = 0;
		DD = 0;
		SD = 0;
		DS = 0;
		dataSize = size;
		this.groundTruthList = groundTruthList;
		this.clusterResultsList = clusterResultsList;
	}

	/**
	 * populateMatrix
	 */
	void populateMatrix() {
		for (int i = 0; i < dataSize; i++) {
			for (int j = i; j < dataSize; j++) {
				if (clusterResultsList.get(i) == clusterResultsList.get(j))
					C[i][j] = C[j][i] = 1;
				else
					C[i][j] = C[j][i] = 0;

				if (groundTruthList.get(i) == groundTruthList.get(j))
					P[i][j] = P[j][i] = 1;
				else
					P[i][j] = P[j][i] = 0;
			}
		}

	}

	/**
	 * @return randIndex
	 */
	public float getCoefficient() {
		populateMatrix();
		for (int i = 0; i < dataSize; i++) {
			for (int j = 0; j < dataSize; j++) {
				if (C[i][j] == P[i][j]) {
					if (C[i][j] == 1)
						SS++;
					else
						DD++;
				} else {
					if (C[i][j] == 1 && P[i][j] == 0)
						SD++;
					else if (C[i][j] == 0 && P[i][j] == 1)
						DS++;
				}
			}
		}

		randIndex = (float) (SS + DD) / (SS + SD + DS + DD);
		return randIndex;
	}

	/**
	 * @return jaccardCoefficient
	 */
	public float getJaccardCoeff() {
		jaccardCoefficient = (float) (SS) / (SS + SD + DS);
		return jaccardCoefficient;

	}

}

