package datapeek;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;

import motifs.getmotifs;

public class plotting_traj {

	/**
	 * class for extracting individual trajectory for plotting 
	 * what tool should I use for visualizing animation? 
	 * -- compare with estimated motif
	 */

	protected static final SimpleDateFormat DATE = new SimpleDateFormat("yyyyMMdd");//change time format
	protected static final SimpleDateFormat DATETIME = new SimpleDateFormat("yyyyMMdd HH:mm:ss");

	public static void main(String[] args) throws NumberFormatException, IOException, ParseException {

		// GPS data path
//		String gpspath = "/home/umni2/a/umnilab/data/Hurricane_GPS/maria/";
//		File gps1 = new File(gpspath+"SAFEGRAPH/PRGPSalldata_SepNov.csv");  // 200M lines
//		File gps2 = new File(gpspath+"SAFEGRAPH2/PRAlldata_DecFeb.csv"); // 120M lines

		// output path 
		String outpath = "/home/bridge/c/tyabe/workspace/recoverymotif/";  
//		String outpath = "C:/users/yabec/Desktop/RecoveryMotifs/";  
		
		File id_nightlocs = new File(outpath+"id_date_nightloc.csv");

		HashSet<String> ids = new HashSet<String>();
		ids.add("ed69c6cd01956c35020def42e222c000d20d1da4a76b33f5a636410312329214");
		ids.add("d67cc46bf9096781db83ea5a80a08892491046eb6298555f3f5f27964e1a3cd0");
		ids.add("e551096535e35226ecbd02c01c5a0917874c0a42de6fda3cdf4c9273adc0fdb0");
		ids.add("c0b7570c18316191851f98d509ab420f7664cc963889a65e1883da395fd1eb01");
		ids.add("a94a6ee3ff8144799292e0fc37d97b3ded378d6650023eb33cb4e6ebbd455dc3");

//		getlogs(gps1, "type1", ids, outpath);
//		getlogs(gps2, "type2", ids, outpath);
		
		getmotifs.computemotifs_indiv(id_nightlocs, 1000d, ids, outpath+"indiv/");
		
	}


	public static void getlogs(
			File gps,
			String doctype,
			HashSet<String> ids,
			String outpath
			) throws NumberFormatException, IOException, ParseException{
		String cols = getdoctype(doctype);
		File out = new File(outpath+"visualize_data.csv");
		BufferedReader br1 = new BufferedReader(new FileReader(gps));
		BufferedWriter bw = new BufferedWriter(new FileWriter(out));
		String line1 = null;
		int count = 0;
		bw.write("id,datetime,long,lat"); bw.newLine();
		while((line1=br1.readLine())!=null){
			try {
				count++; if(count%10000000==0) {System.out.println("done "+count); }
				String[] tokens = line1.split(",");
				if(tokens.length==Integer.valueOf(cols.split(",")[0])){
					String id_br1 = tokens[Integer.valueOf(cols.split(",")[1])];
					if(ids.contains(id_br1)) {
						Double lon = Double.parseDouble(tokens[Integer.valueOf(cols.split(",")[2])]);
						Double lat = Double.parseDouble(tokens[Integer.valueOf(cols.split(",")[3])]);
						String unixtime = tokens[Integer.valueOf(cols.split(",")[4])];
						Date currentDate = new Date (Long.parseLong(unixtime)*((long)1000));
						DATETIME.setTimeZone(TimeZone.getTimeZone("GMT-4"));
						String datetime = DATETIME.format(currentDate); // yyyymmdd hh:mm:ss
						bw.write(id_br1+","+datetime+","+String.valueOf(lon)+","+String.valueOf(lat));
						bw.newLine();
					}}
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
		bw.close();
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

}
