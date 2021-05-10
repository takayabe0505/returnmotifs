package motifs;

import java.util.HashMap;
import java.util.Iterator;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class algorithms {	
	
	public static LonLat weighted_meanshift(
			HashMap<LonLat, Integer> ll_duration,
			Double bw,
			Double maxshift,
			Double cutoff
			) {
		HashMap<LonLat, Integer> p_count = new HashMap<LonLat, Integer>();
		while(ll_duration.size()>0) {
			// choose initial point 
			LonLat init = null;
			Integer z = 0;
			for(LonLat d : ll_duration.keySet()) {
				init = d;
				z+=1;
				if(z==1) {
					break;
				}
			}
			LonLat befmean = init;
			LonLat newmean = new LonLat(0d,0d);
			while(befmean.distance(newmean)>maxshift) { // 
				if(newmean.getLon()!=0d) {
					befmean = newmean;
				}
				Double tmplon = 0d;
				Double tmplat = 0d;
				Double tmpwei = 0d;
				for(LonLat p : ll_duration.keySet()) {
					Double distance = befmean.distance(p);
					if(distance<cutoff) {
						Double dist2 = Math.pow(distance, 2d);
						Double wei = Math.exp((dist2)/(-2d*(Math.pow(bw, 2d))));
						tmplon += wei*p.getLon();
						tmplat += wei*p.getLat();
						tmpwei += wei;
					}
				}
				newmean = new LonLat(tmplon/tmpwei, tmplat/tmpwei);
				//				System.out.println(newmean);
			}
			// newmean is the stable point 
			Integer counter = 0;
			for(Iterator<LonLat> i = ll_duration.keySet().iterator();i.hasNext();){
				LonLat p = i.next();
				if(p.distance(newmean)<cutoff){
					i.remove();
//					counter+=1;
					counter+=ll_duration.get(p)/1800;
				}
			}
			p_count.put(newmean, counter);
		}
		// now we have the p_count --> get the p with most count
		Integer maxcount = 0;
		LonLat res = new LonLat(0d,0d);
		for(LonLat p : p_count.keySet()) {
			if(p_count.get(p)>maxcount) {
				res = p;
				maxcount = p_count.get(p);
			}
		}
		return res;
	}




	public static LonLat meanshift(
			HashMap<String, LonLat> date_ll,
			Double bw,
			Double maxshift,
			Double cutoff
			) {
		HashMap<LonLat, Integer> p_count = new HashMap<LonLat, Integer>();
		while(date_ll.size()>0) {
			// choose initial point 
			LonLat init = null;
			Integer z = 0;
			for(String d : date_ll.keySet()) {
				init = date_ll.get(d);
				z+=1;
				if(z==1) {
					break;
				}
			}
			//			System.out.print("init: ");
			//			System.out.println(init);
			// 
			LonLat befmean = init;
			LonLat newmean = new LonLat(0d,0d);
			while(befmean.distance(newmean)>maxshift) { // 
				if(newmean.getLon()!=0d) {
					befmean = newmean;
				}
				Double tmplon = 0d;
				Double tmplat = 0d;
				Double tmpwei = 0d;
				for(String d : date_ll.keySet()) {
					LonLat p = date_ll.get(d);
					Double distance = befmean.distance(p);
					if(distance<cutoff) {
						Double dist2 = Math.pow(distance, 2d);
						Double wei = Math.exp((dist2)/(-2d*(Math.pow(bw, 2d))));
						tmplon += wei*p.getLon();
						tmplat += wei*p.getLat();
						tmpwei += wei;
					}
				}
				newmean = new LonLat(tmplon/tmpwei, tmplat/tmpwei);
				//				System.out.println(newmean);
			}
			//			System.out.println("---");
			// newmean is the stable point 
			Integer counter = 0;
			for(Iterator<String> i = date_ll.keySet().iterator();i.hasNext();){
				String k = i.next();
				LonLat p = date_ll.get(k);
				if(p.distance(newmean)<cutoff){
					i.remove();
					counter+=1;
				}
			}
			p_count.put(newmean, counter);
			//			System.out.print("newmean, counter: ");
			//			System.out.print(newmean); System.out.print(" ");
			//			System.out.print(counter);System.out.print(" ");
			//			System.out.println(date_ll.size());
		}
		// now we have the p_count --> get the p with most count
		Integer maxcount = 0;
		LonLat res = new LonLat(0d,0d);
		for(LonLat p : p_count.keySet()) {
			if(p_count.get(p)>maxcount) {
				res = p;
				maxcount = p_count.get(p);
			}
		}
		return res;
	}


}
