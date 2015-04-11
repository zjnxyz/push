package com.xtuone.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	public static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}

	public static Date changeLongToDate(long date) {
		return new Date(date);
	}

	public static String formateDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(date);
	}

	public static String changeLongToString(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date(date));
	}

	public static long getCurrentTimeLong() {
		return new Date().getTime();
	}

	public static Date changStringToDate(String date) {
		Date retVal = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			retVal = sdf.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		return retVal;
	}

	public static void main(String[] args) {
		System.out.println(DateUtil.changeLongToString(1394179193));
	}
}
