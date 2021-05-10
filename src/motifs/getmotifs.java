package motifs;

import java.util.HashMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class getmotifs {

	public static HashMap<String, Integer> getsequence(
			HashMap<String, LonLat> date_ll,
			Double thres
			) {
		// temp map for lonlat and node number
		HashMap<LonLat, Integer> ll_num = new HashMap<LonLat, Integer>();
		// result map of date and node number
		HashMap<String, Integer> date_node = new HashMap<String, Integer>();
		Integer clusnum = 1;
		for(String date : date_ll.keySet()) {
			LonLat p = date_ll.get(date);
			if(ll_num.isEmpty()) {
				ll_num.put(p, clusnum);
				date_node.put(date, clusnum);
				clusnum+=1;
			}
			else {
				for(LonLat clus : ll_num.keySet()) {
					if(p.distance(clus)<thres) {
						date_node.put(date, ll_num.get(clus));
						break;
					}
				}
			}
		}
		return date_node;
	}
	
	
	
	
}
