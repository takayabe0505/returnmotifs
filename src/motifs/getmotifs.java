package motifs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class getmotifs {

	public static HashMap<String, TreeMap<String, LonLat>> intomap(File in) throws IOException{
		HashMap<String, TreeMap<String, LonLat>> map = new HashMap<String, TreeMap<String, LonLat>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line1 = null;
		while((line1=br.readLine())!=null){
			String[] tokens = line1.split(",");
			String id = tokens[0];
			String date = tokens[1];
			LonLat p = new LonLat(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
			if(map.containsKey(id)) {
				map.get(id).put(date, p);
			}
			else {
				TreeMap<String, LonLat> tmp = new TreeMap<String, LonLat>();
				tmp.put(date,p);
				map.put(id, tmp);
			}
		}
		br.close();
		return map;
	}
	
	
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
			// first point 
			if(ll_num.isEmpty()) {
				ll_num.put(p, clusnum);
				date_node.put(date, clusnum);
				clusnum+=1;
			}
			// from second point
			else {
				// check in all clusters
				String flag = "no";
				for(LonLat clus : ll_num.keySet()) {
					if(p.distance(clus)<thres) {
						date_node.put(date, ll_num.get(clus));
						flag = "yes";
						break;
					}
				}
				// if point is not near any cluster center
				if(flag.equals("no")) {
					ll_num.put(p, clusnum);
					date_node.put(date, clusnum);
					clusnum+=1;
				}
			}
		}
		return date_node;
	}
	
	
	
	
}
