package motifs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class getmotifs {

	public static void computemotifs(
			File in, 
			Double thres, 
			File out) throws IOException {
		HashMap<String, TreeMap<String, LonLat>> map = intomap(in);
		Integer count = 0;
		for(String id : map.keySet()) {
			count+=1;
			if(count%10000==0) {
				System.out.println("done "+String.valueOf(count)+" / "+String.valueOf(map.size()));
			}
			TreeMap<String, LonLat> ps = map.get(id);
			getsequence(id, ps, thres, out, count);
		}
	}

	public static void getsequence(
			String id,
			TreeMap<String, LonLat> date_ll,
			Double thres,
			File out,
			Integer count
			) throws IOException {
		// temp map for lonlat and node number
		HashMap<LonLat, Integer> ll_num = new HashMap<LonLat, Integer>();
		String result = "";
		Integer clusnum = 1;
		for(String date : date_ll.keySet()) {
			LonLat p = date_ll.get(date);
			// first point 
			if(ll_num.isEmpty()) {
				ll_num.put(p, clusnum);
				result = result + date+","+p+","+clusnum+";";
				clusnum+=1;
			}
			// from second point
			else {
				// check in all clusters
				String flag = "no";
				for(LonLat clus : ll_num.keySet()) {
					if(p.distance(clus)<thres) {
						result = result + date+","+p+","+ll_num.get(clus)+";";
						flag = "yes";
						break;
					}
				}
				// if point is not near any cluster center
				if(flag.equals("no")) {
					ll_num.put(p, clusnum);
					result = result + date+","+p+","+clusnum+";";
					clusnum+=1;
				}
			}
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));
		bw.write(id+"\t"+result);
		bw.newLine();
		if(count%10000==0) {
			System.out.println(result);
		}
		bw.close();
	}

	public static void computemotifs_indiv(
			File in, 
			Double thres, 
			HashSet<String> ids, 
			String outpath) throws IOException {
		HashMap<String, TreeMap<String, LonLat>> map = intomap_onlyselectedIDs(in, ids);
		for(String id : map.keySet()) {
			File out = new File(outpath+id+"_motiflocs.csv");
			TreeMap<String, LonLat> ps = map.get(id);
			getsequence_indiv(id, ps, thres, out);
		}
	}


	public static void getsequence_indiv(
			String id,
			TreeMap<String, LonLat> date_ll,
			Double thres,
			File out
			) throws IOException {
		// temp map for lonlat and node number
		HashMap<LonLat, Integer> ll_num = new HashMap<LonLat, Integer>();
		String result = "";
		Integer clusnum = 1;
		for(String date : date_ll.keySet()) {
			LonLat p = date_ll.get(date);
			// first point 
			if(ll_num.isEmpty()) {
				ll_num.put(p, clusnum);
				result = result + date+","+String.valueOf(p.getLon())+","+String.valueOf(p.getLat())+","+clusnum+"\n";
				clusnum+=1;
			}
			// from second point
			else {
				// check in all clusters
				String flag = "no";
				for(LonLat clus : ll_num.keySet()) {
					if(p.distance(clus)<thres) {
						result = result + date+","+String.valueOf(p.getLon())+","+String.valueOf(p.getLat())+","+ll_num.get(clus)+"\n";
						flag = "yes";
						break;
					}
				}
				// if point is not near any cluster center
				if(flag.equals("no")) {
					ll_num.put(p, clusnum);
					result = result + date+","+String.valueOf(p.getLon())+","+String.valueOf(p.getLat())+","+clusnum+"\n";
					clusnum+=1;
				}
			}
		}
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		bw.write(result);
		bw.newLine();
		bw.close();
	}



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
		System.out.println("done into map");
		return map;
	}

	public static HashMap<String, TreeMap<String, LonLat>> intomap_onlyselectedIDs(
			File in, HashSet<String> ids
			) throws IOException{
		HashMap<String, TreeMap<String, LonLat>> map = new HashMap<String, TreeMap<String, LonLat>>();
		BufferedReader br = new BufferedReader(new FileReader(in));
		String line1 = null;
		while((line1=br.readLine())!=null){
			String[] tokens = line1.split(",");
			String id = tokens[0];
			if(ids.contains(id)) {
				String date = tokens[1];
				LonLat p = new LonLat(Double.parseDouble(tokens[2]), Double.parseDouble(tokens[3]));
				if(map.containsKey(id)) {
					map.get(id).put(date, p);
				}
				else {
					TreeMap<String, LonLat> tmp = new TreeMap<String, LonLat>();
					tmp.put(date,p);
					map.put(id, tmp);
				}}
		}
		br.close();
		System.out.println("done into map");
		return map;
	}


}
