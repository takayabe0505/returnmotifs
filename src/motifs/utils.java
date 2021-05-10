package motifs;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import jp.ac.ut.csis.pflow.geom.LonLat;

public class utils {

	public static Date beforeday_date(Date day) throws ParseException{
		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(day);
		nextCal.add(Calendar.DAY_OF_MONTH, -1);
		Date nextDate = nextCal.getTime();
		return nextDate;
	}
	public static String insidebox(LonLat p, LonLat pmax, LonLat pmin) {
		String label = "no";
		if(p.getLon()<pmax.getLon()) {
			if(p.getLon()>pmin.getLon()) {
				if(p.getLat()<pmax.getLat()) {
					if(p.getLat()>pmin.getLat()) {
						label = "yes";
					}}}}
		return label;
	}
	public static Date nextday_date(Date day) throws ParseException{
		Calendar nextCal = Calendar.getInstance();
		nextCal.setTime(day);
		nextCal.add(Calendar.DAY_OF_MONTH, 1);
		Date nextDate = nextCal.getTime();
		return nextDate;
	}
	
}
