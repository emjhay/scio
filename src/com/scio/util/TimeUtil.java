package com.scio.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.net.ParseException;
import android.util.Log;

public class TimeUtil {

	public static String getTime() {
		try {
			// Make the Http connection so we can retrieve the time
			HttpClient httpclient = new DefaultHttpClient();
			// I am using yahoos api to get the time
			HttpResponse response = httpclient
					.execute(new HttpGet(
							"http://developer.yahooapis.com/TimeService/V1/getTime?appid=YahooDemo"));
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				response.getEntity().writeTo(out);
				out.close();
				// The response is an xml file and i have stored it in a string
				String responseString = out.toString();
				Log.d("Response", responseString);
				// We have to parse the xml file using any parser, but since i
				// have to
				// take just one value i have deviced a shortcut to retrieve it
				int x = responseString.indexOf("<Timestamp>");
				int y = responseString.indexOf("</Timestamp>");
				// I am using the x + "<Timestamp>" because x alone gives only
				// the start value
				Log.d("Response",
						responseString.substring(x + "<Timestamp>".length(), y));
				String timestamp = responseString.substring(
						x + "<Timestamp>".length(), y);
				// The time returned is in UNIX format so i need to multiply it
				// by 1000 to use it
				Date d = new Date(Long.parseLong(timestamp) * 1000);

				SimpleDateFormat sdf = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);

				String strDate = sdf.format(d);

				// System.out.println("strDate=========="+strDate);

				// Log.d("Response", d.toString());
				return strDate;
			} else {
				// Closes the connection.
				response.getEntity().getContent().close();
				throw new IOException(statusLine.getReasonPhrase());
			}
		} catch (ClientProtocolException e) {
			Log.d("Response", e.getMessage());
		} catch (IOException e) {
			Log.d("Response", e.getMessage());
		}
		return null;
	}

	public static String setExpiryDate() {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.ENGLISH);
		Calendar today = Calendar.getInstance();

		try {
			Date todaysdf = sdf.parse(getTime());
			today.setTime(todaysdf);

			int currYear = today.get(Calendar.YEAR);
			int currMonth = today.get(Calendar.MONTH) + 1;
			int currDay = today.get(Calendar.DAY_OF_MONTH);

			int currHour = today.get(Calendar.HOUR_OF_DAY);
			int currMin = today.get(Calendar.MINUTE);
			int currSecs = today.get(Calendar.SECOND);

			Date expiresdf = sdf.parse(currYear + "-" + currMonth + "-"
					+ (currDay + 2) + " " + currHour + ":" + currMin + ":"
					+ currSecs);

			return sdf.format(expiresdf);

		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}

	public static void timeConverted() {

		// System.out.println("ONLINE TIME======="+getTime());

		Calendar today = Calendar.getInstance();

		int currYear = today.get(Calendar.YEAR);
		int currMonth = today.get(Calendar.MONTH) + 1;
		int currDay = today.get(Calendar.DAY_OF_MONTH);

		int currHour = today.get(Calendar.HOUR_OF_DAY);
		int currMin = today.get(Calendar.MINUTE);
		int currSecs = today.get(Calendar.SECOND);

		Calendar expireDate = Calendar.getInstance();
		expireDate.set(currYear, currMonth, currDay - 5);

		try {

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
					Locale.ENGLISH);
			Date todaysdf = sdf.parse(getTime());
			Date expiresdf = sdf.parse(currYear + "-" + currMonth + "-"
					+ (currDay - 1) + " " + currHour + ":" + currMin + ":"
					+ currSecs);

			// System.out.println(sdf.format(todaysdf));
			// System.out.println(sdf.format(expiresdf));

			if (todaysdf.after(expiresdf)) {
				System.out.println("Today is after Expiry");
			}

			if (todaysdf.before(expiresdf)) {
				System.out.println("Today is before Expiry");
			}

			if (todaysdf.equals(expiresdf)) {
				System.out.println("Today is equal Expiry");
			}

		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if (expireDate.getTime().after(today.getTime()))
		//
		// {
		// // expired - please purchase app
		// System.out.println("================expire");
		//
		// } else {
		// // do some stuff
		// System.out.println("================not expire");
		// }

	}

	public static boolean checkSetDateIfExpired(String expireDate) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.ENGLISH);

		try {
//			Date current = sdf.parse("2014-05-30 12:00:00");
			Date current = sdf.parse(getTime());
			Date expire = sdf.parse(expireDate);

			if (current.after(expire)) {
				System.out.println("CURRENT DATE is after EXPIRE");
				return true;
			} else if (current.before(expire)) {
				System.out.println("CURRENT DATE is before EXPIRE");
			} else if (current.equals(expire)) {
				System.out.println("CURRENT DATE is equal EXPIRE");
			}

		} catch (Exception e) {
			// TODO: handle exception
		}

		return false;
	}

}
