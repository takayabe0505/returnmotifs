package motifs;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class mainrun {

	public static void main(String[] args) throws NumberFormatException, IOException, ParseException {

		// GPS data path
		String gpspath = "/home/umni2/a/umnilab/data/Hurricane_GPS/maria/";
		File gps1 = new File(gpspath+"SAFEGRAPH/PRGPSalldata_SepNov.csv");  // 200M lines
		File gps2 = new File(gpspath+"SAFEGRAPH2/PRAlldata_DecFeb.csv"); // 120M lines

		// output path 
		String outpath = "/home/bridge/c/tyabe/workspace/recoverymotif/";  

		// Clustering parameters
		Double bandwidth = 250d;
		Double maxshift  = 250d;
		Double cutoff    = 1000d;		
		Double motifthres = 500d;
		
		// result files 
		File id_nightlocs = new File(outpath+"id_date_nightloc.csv");
		File id_motifseq = new File(outpath+"id_motifseq.csv");

		
//		runcode(gps1, "type1", bandwidth, maxshift, cutoff, id_nightlocs); System.out.println("Done File 1");
//		runcode(gps2, "type2", bandwidth, maxshift, cutoff, id_nightlocs); System.out.println("Done File 1");

		// 3. analyze sequence and compute motifs
		getmotifs.computemotifs(id_nightlocs, motifthres, id_motifseq);
		
	}

	public static void runcode(
			File gps, 
			String doctype, 
			Double bandwidth,
			Double maxshift,
			Double cutoff,		
			File id_nightlocs
			) throws NumberFormatException, IOException, ParseException {

		// 1. store location data in hashmap
		HashMap<String, HashMap<String, TreeMap<Integer, LonLat>>> id_date_time_ll = displacement.getalllogs(gps, doctype);
		System.out.println("--- got data hashmap!");
		
		// 2. get nighttime staypoints for each ID for each day
		displacement.getnightlocs(id_date_time_ll, bandwidth, maxshift, cutoff, id_nightlocs);	
		
	}



}
