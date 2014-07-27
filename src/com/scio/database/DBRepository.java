package com.scio.database;

import android.content.ContentValues;
import android.database.Cursor;

import com.scio.model.G;

public class DBRepository {

	public static void insert(String table, int interval, int curHour,
			int emailStatus) {
		ContentValues values = new ContentValues();
		values.put(G.SET_INTERVAL, interval);
		values.put(G.CURRENT_HOUR, curHour);
		values.put(G.EMAIL_STATUS, emailStatus);
		G.DATA_BASE.insert(table, values);
	}

	public static void updateCurrentHour(String table, int hour) {
		ContentValues values = new ContentValues();
		values.put(G.CURRENT_HOUR, hour);
		G.DATA_BASE.update(table, values);
	}

	public static void updateEmailStatus(String table, int emailStatus) {
		ContentValues values = new ContentValues();
		values.put(G.EMAIL_STATUS, emailStatus);
		G.DATA_BASE.update(table, values);
	}

	public static void updateInterval(String table, int interval, int current) {
		ContentValues values = new ContentValues();
		values.put(G.SET_INTERVAL, interval);
		values.put(G.CURRENT_HOUR, current);
		G.DATA_BASE.update(table, values);
	}

	public static boolean checkHourValue(String table) {
		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			closeCursor(cursor);
			return true;
		}
		closeCursor(cursor);
		return false;

	}

	public static int getCurrentHour(String table) {
		int hour = 0;
		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			hour = cursor.getInt(1);
			closeCursor(cursor);
			return hour;
		}
		closeCursor(cursor);
		return hour;

	}

	public static int getSetInterval(String table) {
		int interval = 0;
		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			interval = cursor.getInt(0);
			closeCursor(cursor);
			return interval;
		}
		closeCursor(cursor);
		return interval;

	}

	public static int getEmailStatus(String table) {
		int emailStatus = 0;
		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
			emailStatus = cursor.getInt(2);
			closeCursor(cursor);
			return emailStatus;
		}
		closeCursor(cursor);
		return emailStatus;

	}

	// ================
//
//	public static void insertEmailStorage(String table, String username,
//			String userPass, String email, String emailPass, int emailStatus,
//			int passStatus) {
//
//		ContentValues values = new ContentValues();
//		values.put(G.EMAIL_STORAGE_USERNAME, username);
//		values.put(G.EMAIL_STORAGE_USER_PASS, userPass);
//		values.put(G.EMAIL_STORAGE_EMAIL, email);
//		values.put(G.EMAIL_STORAGE_EMAIL_PASS, emailPass);
//		values.put(G.EMAIL_STORAGE_EMAIL_STATUS, emailStatus);
//		values.put(G.EMAIL_STORAGE_PASS_STATUS, passStatus);
//		G.DATA_BASE.insert(table, values);
//
//	}
//
//	public static void updateEmailStorageEmailPass(String table, String email,
//			String emailPass, int emailStatus, int passStatus) {
//		ContentValues values = new ContentValues();
//		values.put(G.EMAIL_STORAGE_EMAIL, email);
//		values.put(G.EMAIL_STORAGE_EMAIL_PASS, emailPass);
//		values.put(G.EMAIL_STORAGE_EMAIL_STATUS, emailStatus);
//		values.put(G.EMAIL_STORAGE_PASS_STATUS, passStatus);
//		G.DATA_BASE.update(table, values);
//	}
//
//	public static void updateEmailStorageEmail(String table, String email,
//			String emailPass, int emailStatus, int passStatus) {
//		ContentValues values = new ContentValues();
//		values.put(G.EMAIL_STORAGE_EMAIL, email);
//		values.put(G.EMAIL_STORAGE_EMAIL_PASS, emailPass);
//		values.put(G.EMAIL_STORAGE_EMAIL_STATUS, emailStatus);
//		G.DATA_BASE.update(table, values);
//	}
//
//	public static void updateEmailStoragePass(String table, String email,
//			String emailPass, int emailStatus, int passStatus) {
//		ContentValues values = new ContentValues();
//		values.put(G.EMAIL_STORAGE_EMAIL, email);
//		values.put(G.EMAIL_STORAGE_EMAIL_PASS, emailPass);
//		values.put(G.EMAIL_STORAGE_PASS_STATUS, passStatus);
//		G.DATA_BASE.update(table, values);
//	}
//
//	// public static boolean checkEmailStorage(String table) {
//	// Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
//	// if (cursor.getCount() > 0 && cursor.moveToFirst()) {
//	// closeCursor(cursor);
//	// return true;
//	// }
//	// closeCursor(cursor);
//	// return false;
//	//
//	// }
//
//	public static boolean getEmailStorageUser(String table, String username,
//			String userPass, String email, String emailPass) {
//		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
//		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
//
//			do {
//
//				if (cursor.getString(1).toString().equals(username)
//						&& cursor.getString(2).toString().equals(userPass)
//						&& cursor.getString(3).toString().equals(email)
//						&& cursor.getString(4).toString().equals(emailPass)) {
//					return true;
//				}
//
//			} while (cursor.moveToNext());
//		}
//		closeCursor(cursor);
//		return false;
//	}
//
//	public static void setEmailPassValue(String table, String username,
//			String password, EditText email, EditText pass,
//			CheckBox emailCheck, CheckBox passCheck) {
//		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
//		if (cursor.getCount() > 0 && cursor.moveToFirst()) {
//
//			do {
//
//				if (cursor.getString(1).toString().equals(username)
//						&& cursor.getString(2).toString().equals(password)
//						&& cursor.getInt(5) == 1 && cursor.getInt(6) == 1) {
//					email.setText(cursor.getString(3).toString());
//					pass.setText(cursor.getString(4).toString());
//					emailCheck.setChecked(true);
//					passCheck.setChecked(true);
//				} else if (cursor.getString(1).toString().equals(username)
//						&& cursor.getString(2).toString().equals(password)
//						&& cursor.getInt(5) == 1 && cursor.getInt(6) == 0) {
//					email.setText(cursor.getString(3).toString());
//					emailCheck.setChecked(true);
//					passCheck.setChecked(false);
//				} else if (cursor.getString(1).toString().equals(username)
//						&& cursor.getString(2).toString().equals(password)
//						&& cursor.getInt(5) == 0 && cursor.getInt(6) == 1) {
//					pass.setText(cursor.getString(4).toString());
//					emailCheck.setChecked(false);
//					passCheck.setChecked(true);
//				}
//
//			} while (cursor.moveToNext());
//		}
//		closeCursor(cursor);
//
//	}

//	public static void insertExpiry(String table, String username,
//			String password, String userType, String createDate,
//			String expiryDate) {
//		ContentValues values = new ContentValues();
//		values.put(G.EXPIRY_USERNAME, username);
//		values.put(G.EXPIRY_PASSWORD, password);
//		values.put(G.EXPIRY_USERTYPE, userType);
//		values.put(G.EXPIRY_CREATE_DATE, createDate);
//		values.put(G.EXPIRY_EXPIRY_DATE, expiryDate);
//		G.DATA_BASE.insert(table, values);
//	}

//	public static boolean checkIfUserExpired(String table, String username,
//			String password, String userType) {
//
//		Cursor cursor = G.DATA_BASE.select("SELECT * FROM " + table);
//
//		if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//			do {
//
//				if (cursor.getString(1).equals(username)
//						&& cursor.getString(2).equals(password)
//						&& cursor.getString(3).equals(userType)
//						&& TimeUtil.checkSetDateIfExpired(cursor.getString(5))) {
//					return true;
//				}
//
//			} while (cursor.moveToNext());
//
//			
//		}
//		
//		closeCursor(cursor);
//
//		return false;
//	}

	private static void closeCursor(Cursor cursor) {
		if (cursor != null) {
			cursor.close();
		}
	}

}
