package xyz.scyllasrock.ScyCosmetics.util;

import java.util.Calendar;

import org.bukkit.Bukkit;

public class TimeUtils {
	
	
	public long getLongFromDate(int year, int month, int day, int hour, int minute){
		Calendar cal = Calendar.getInstance();
		cal.set(year, month, day, hour, minute);
		return cal.getTimeInMillis();
	}
	
	/**
	 * Assumes s is of the form yyyy:mm:dd:hh:mm or mm:dd:hh:mm, if it does not match this format, returns -1L.
	 * @param s
	 * @return time from string
	 */
	public static long getLongFromString(String s) {
		if(s == null) return -1L;
		s = s.strip();
		Calendar cal = Calendar.getInstance();
		String[] arr = s.split(":");
		int year = 0, month = 0, day = 0, hour = 0, minute = 0;
		if(arr.length == 4) {
			year = cal.get(Calendar.YEAR);
			month = Integer.parseInt(arr[0]) - 1;
			day = Integer.parseInt(arr[1]);
			hour = Integer.parseInt(arr[2]);
			minute = Integer.parseInt(arr[3]);
		}
		else if(arr.length == 5) {
			year = Integer.parseInt(arr[0]);
			month = Integer.parseInt(arr[1]) - 1;
			day = Integer.parseInt(arr[2]);
			hour = Integer.parseInt(arr[3]);
			minute = Integer.parseInt(arr[4]);
		}
		else {
			Bukkit.getConsoleSender().sendMessage("Scycosmetics >> Error! String " + s + " has not been formatted correctly!");
			return -1L;
		}

		cal.set(year, month, day, hour, minute);
		return cal.getTimeInMillis();
	}

}
