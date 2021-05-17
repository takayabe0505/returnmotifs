package motifs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.TreeMap;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class displacement {

	protected static final SimpleDateFormat DATE = new SimpleDateFormat("yyyyMMdd");//change time format
	protected static final SimpleDateFormat DATETIME = new SimpleDateFormat("yyyyMMdd HH:mm:ss");


	public static void getnightlocs(
			HashMap<String, HashMap<String, TreeMap<Integer, LonLat>>> logs, 
			Double bandwidth, Double maxshift, Double cutoff,
			File out
			) throws IOException {
		BufferedWriter bw = new BufferedWriter(new FileWriter(out, true));
		Integer count = 0;
		for(String id : logs.keySet()) {
			count+=1;
			if(count%10000==0) {
				System.out.println("--- done "+String.valueOf(count)+" / "+String.valueOf(logs.keySet().size()));
			}
			for(String date : logs.get(id).keySet()) {
				TreeMap<Integer, LonLat> thisdaylogs = logs.get(id).get(date);
				//				System.out.println("--- example logs ---");
				//				System.out.println(thisdaylogs);
				HashMap<String,Integer> thisdaylogs_duration = getdurations(thisdaylogs);
				//				System.out.println("--- example log durations---");
				//				System.out.println(thisdaylogs_duration);
				LonLat p = algorithms.weighted_meanshift(thisdaylogs_duration, bandwidth, maxshift, cutoff);
				//				System.out.println("--- example home estimate---");
				//				System.out.println(p);
				bw.write(id+","+date+","+String.valueOf(p.getLon())+","+String.valueOf(p.getLat()));
				bw.newLine();
			}
		}
		bw.close();
	}



	public static HashMap<String, HashMap<String, TreeMap<Integer, LonLat>>> getalllogs(
			File gps,
			String doctype
			) throws NumberFormatException, IOException, ParseException{
		String cols = getdoctype(doctype);
		HashMap<String, HashMap<String, TreeMap<Integer, LonLat>>> id_date_time_ll = 
				new HashMap<String, HashMap<String, TreeMap<Integer, LonLat>>>();
		BufferedReader br1 = new BufferedReader(new FileReader(gps));
		String line1 = null;
		int count = 0;
		while((line1=br1.readLine())!=null){
			try {
				count++; if(count%10000000==0) {System.out.println("done "+count); }
				String[] tokens = line1.split(",");
				if(tokens.length==Integer.valueOf(cols.split(",")[0])){
					String id_br1 = tokens[Integer.valueOf(cols.split(",")[1])];
					Double lon = Double.parseDouble(tokens[Integer.valueOf(cols.split(",")[2])]);
					Double lat = Double.parseDouble(tokens[Integer.valueOf(cols.split(",")[3])]);
					LonLat p = new LonLat(lon,lat);
					String unixtime = tokens[Integer.valueOf(cols.split(",")[4])];
					Date currentDate = new Date (Long.parseLong(unixtime)*((long)1000));
					DATETIME.setTimeZone(TimeZone.getTimeZone("GMT-4"));
					String datetime = DATETIME.format(currentDate); // yyyy-mm-dd hh:mm:ss
					String yyyymmdd = datetime.split(" ")[0];
					String time = datetime.split(" ")[1];
					Integer hour = Integer.valueOf(time.split(":")[0]);
					Integer mins = Integer.valueOf(time.split(":")[1]);
					Integer secs = Integer.valueOf(time.substring(6,8));
					Integer time_int = 3600*hour+60*mins+secs;
					if((hour>=18)||(hour<=6)){
						if(hour<=6) {
							yyyymmdd = DATE.format(utils.beforeday_date(DATE.parse(yyyymmdd)));
							time_int = time_int + 24*3600;
						}
						if(id_date_time_ll.containsKey(id_br1)) {
							if(id_date_time_ll.get(id_br1).containsKey(yyyymmdd)) {
								id_date_time_ll.get(id_br1).get(yyyymmdd).put(time_int, p);
							}
							else {
								TreeMap<Integer, LonLat> tmp = new TreeMap<Integer, LonLat>();
								tmp.put(time_int, p);
								id_date_time_ll.get(id_br1).put(yyyymmdd, tmp);
							}
						}
						else {
							TreeMap<Integer, LonLat> tmp = new TreeMap<Integer, LonLat>();
							tmp.put(time_int, p);
							HashMap<String, TreeMap<Integer, LonLat>> tmp2 = new 
									HashMap<String, TreeMap<Integer, LonLat>>();
							tmp2.put(yyyymmdd, tmp);
							id_date_time_ll.put(id_br1, tmp2);
						}}}
				else{System.out.println("ERROR LINE: "+line1);}
			}
			catch (ArrayIndexOutOfBoundsException  e){
				System.out.println("OUT OF BOUNDS EXCEPTION ----");
				System.out.println(line1);
				System.out.println("----");
			}
			catch (Exception  e){
				System.out.println("OTHER ERROR IN LINE ----");
				System.out.println(line1);
				System.out.println("----");				
			}
		}
		br1.close();
		return id_date_time_ll;
	}


	public static String getdoctype(String doctype) {
		// num of cols, id, lon, lat, time
		if(doctype.equals("type1")) {
			return "4,0,1,2,3";
		}
		else if (doctype.equals("type2")){
			return "7,0,2,1,5";
		}
		else {
			return "identify doc type";
		}
	}


	public static HashMap<String,Integer> getdurations(TreeMap<Integer, LonLat> time_ll) {
		time_ll.put(3600*31, new LonLat(0d,0d));
		HashMap<String,Integer> ll_duration = new HashMap<String,Integer>();
		Integer beftime = 3600*20;
		String  beforep = "0";
		Iterator<Entry<Integer, LonLat>> entries = time_ll.entrySet().iterator();
//		if(time_ll.size()<100) {
			while(entries.hasNext()) {
				Map.Entry<Integer, LonLat> entry = (Map.Entry<Integer, LonLat>)entries.next();
				Integer t = (Integer) entry.getKey();
				LonLat p = (LonLat) entry.getValue();
				String p_str = String.valueOf(p.getLon())+","+String.valueOf(p.getLat());
				//				System.out.println("thisline---"+t+","+p);
				Integer time = t - beftime;
				if(!beforep.equals("0")) {
					// change to clustering points together if they are close
					String yesno = "no";
					LonLat beforep_p = new LonLat(Double.parseDouble(beforep.split(",")[0]),Double.parseDouble(beforep.split(",")[1]));
					for(String point : ll_duration.keySet()) {
						LonLat point_p = new LonLat(Double.parseDouble(point.split(",")[0]),Double.parseDouble(point.split(",")[1]));
						if(beforep_p.distance(point_p)<500d) {
							Integer totaltime = time+ll_duration.get(beforep);
							ll_duration.put(beforep, totaltime);
							yesno = "yes";
							//						System.out.println("added---"+time+","+beforep+","+totaltime);
						}
					}
					if(yesno.equals("no")) {
						ll_duration.put(beforep, time);
					}
					//					System.out.println("duration---"+time+","+beforep);
				}
				beforep = p_str;
				beftime = t;
			}
//		}
		//		System.out.println("res---"+ll_duration);
		return ll_duration;
	}



}


















