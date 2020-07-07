package proj_2;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * @author SAI KALYAN KATTA
 *
 */
class DBSCAN_Functions {
	/**
	 * @param point1
	 * @param point2
	 */
	private static double getEuclideanDistance(ArrayList<Float> point1, ArrayList<Float> point2) 
	{
		double distance = 0;
		for (int i = 0; i < point1.size(); i++)
		{
			double diffSquare = Math.pow((point1.get(i) - point2.get(i)), 2);
			distance += diffSquare;
		}
		return Math.sqrt(distance);

	}
	/**
	 * @param genesList
	 * @param cluster_ids
	 * @param clusterList
	 * @param visited
	 * @param eps
	 * @param minpts
	 */
	static void DBSCAN_Algo(List<ArrayList<Float>>genesList,Map<Integer,Integer> cluster_ids,ArrayList<ArrayList<Integer>> clusterList,ArrayList<Integer> visited,double eps, int minpts)
	{	
		int c_num = 0;
		for(int i = 0; i < genesList.size() ;i++)
		{
			if(!visited.contains(i))
			{
				visited.add(i);
				ArrayList<Integer> neighborPts = DBSCAN_Functions.regionQuery(genesList,i,eps);
				if(neighborPts.size() < minpts) 
					cluster_ids.put(i, -1);
				else
				{
				ArrayList<Integer> c = new ArrayList<Integer>();
				c.add(i);
				c_num++;
				cluster_ids.put(i,c_num);
				for(int j = 0; j < neighborPts.size(); j++)
				{
					int p = neighborPts.get(j);
					if(!visited.contains(p))
					{
						visited.add(p);
						ArrayList<Integer> neighborPts2 = regionQuery(genesList,p,eps);
						if (neighborPts2.size() >= minpts)
							{
							neighborPts= addneighbors(neighborPts,neighborPts2);
							c.add(p);
							}
					}
					if(!cluster_ids.containsKey(p)) 
					{	
						cluster_ids.put(p,c_num);	
					}
				}
				clusterList.add(c);
				}		
			}	
		}
	}
	/**
	 * @param neighborPts
	 * @param neighborPts2
	 */
	private static ArrayList<Integer> addneighbors(ArrayList<Integer> neighborPts, ArrayList<Integer> neighborPts2) 
	{
		for(int i = 0; i < neighborPts2.size(); i++) 
		{
			int j = neighborPts2.get(i);
			if(!neighborPts.contains(j)) 
				neighborPts.add(j);
		}
		return neighborPts;
	}
	/**
	* @param geneList
	 * @param point
	 * @param eps
	 */
	private static ArrayList<Integer> regionQuery(List<ArrayList<Float>>genesList,int point, double eps) {
		ArrayList<Integer> neighborList = new ArrayList<Integer>();
		for(int i = 0; i < genesList.size(); i++) {
			if(getEuclideanDistance(genesList.get(point),genesList.get(i)) <= eps)
				neighborList.add(i);
		}	
		return neighborList;
	}
}
