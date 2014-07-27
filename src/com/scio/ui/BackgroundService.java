package com.scio.ui;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.scio.database.DBRepository;
import com.scio.model.G;
import com.scio.model.ServiceHandler;
import com.scio.util.StringUtil;

public class BackgroundService extends Service {

	// private Timer timer;
	// private ProgressDialog pDialog;
	final Handler hand = new Handler();

	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			hand.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					System.out.println("=============RUNNING SERVICE");

					if (DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1
							&& DBRepository.getCurrentHour(G.LOCATION_TABLE) < DBRepository
									.getSetInterval(G.LOCATION_TABLE))
						DBRepository
								.updateCurrentHour(
										G.LOCATION_TABLE,
										DBRepository
												.getCurrentHour(G.LOCATION_TABLE) + 1);

					if (DBRepository.getEmailStatus(G.SMS_TABLE) == 1
							&& DBRepository.getCurrentHour(G.SMS_TABLE) < DBRepository
									.getSetInterval(G.SMS_TABLE))
						DBRepository.updateCurrentHour(G.SMS_TABLE,
								DBRepository.getCurrentHour(G.SMS_TABLE) + 1);

					if (DBRepository.getEmailStatus(G.CALL_TABLE) == 1
							&& DBRepository.getCurrentHour(G.CALL_TABLE) < DBRepository
									.getSetInterval(G.CALL_TABLE))
						DBRepository.updateCurrentHour(G.CALL_TABLE,
								DBRepository.getCurrentHour(G.CALL_TABLE) + 1);

					if (DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1
							&& DBRepository.getCurrentHour(G.PHOTO_TABLE) < DBRepository
									.getSetInterval(G.PHOTO_TABLE))
						DBRepository.updateCurrentHour(G.PHOTO_TABLE,
								DBRepository.getCurrentHour(G.PHOTO_TABLE) + 1);

					System.out.println("locHour========"
							+ DBRepository.getCurrentHour(G.LOCATION_TABLE));
					System.out.println("smsHour========"
							+ DBRepository.getCurrentHour(G.SMS_TABLE));
					System.out.println("callHour========"
							+ DBRepository.getCurrentHour(G.CALL_TABLE));

					System.out.println("photoHour========"
							+ DBRepository.getCurrentHour(G.PHOTO_TABLE));

					if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
							.getSetInterval(G.LOCATION_TABLE)
							&& DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
						MainForClient.instance().runAlert();
					}

					if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
							.getSetInterval(G.SMS_TABLE)
							&& DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
						MainForClient.instance().runAlert();
					}

					if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
							.getSetInterval(G.CALL_TABLE)
							&& DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
						MainForClient.instance().runAlert();
					}

//					if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
//							.getSetInterval(G.PHOTO_TABLE)
//							&& DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
//						MainForClient.instance().runAlert();
//					}

				}
			});
		}
	};


	private final IBinder mBinder = new LocalBinder();

	public class LocalBinder extends Binder {
		BackgroundService getService() {
			return BackgroundService.this;
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("=================SERVICE START");
		new FetchUsers().execute();
		G.TIMER_GLOBAL = new Timer("TweetCollectorTimer");
		G.TIMER_GLOBAL.schedule(updateTask, 60 * 1000L, 60 * 1000L);
		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	// @Override
	// public void onCreate() {
	// super.onCreate();
	//
	// timer = new Timer("TweetCollectorTimer");
	// timer.schedule(updateTask, 1000L, 60 * 1000L);
	// }

	@Override
	public void onDestroy() {
		super.onDestroy();

		System.out.println("=================SERVICE DESTROY");

		G.TIMER_GLOBAL.cancel();
		G.TIMER_GLOBAL = null;
	}

	private class FetchUsers extends AsyncTask<JSONArray, Void, JSONArray> {

		// private Context mContext;

		// public FetchUsers(Context context) {
		// this.mContext = context;
		// }

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			// pDialog = new ProgressDialog(mContext);
			// pDialog.setMessage("Please wait...");
			// pDialog.setCancelable(false);
			// pDialog.show();

		}

		@Override
		protected JSONArray doInBackground(JSONArray... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response

			String jsonStr = sh.makeServiceCall(G.URL + G.CLIENT
					+ G.LOGINWITH_USERTYPE + G.CLIENT_USERNAME + "/"+G.CLIENT_PASSWORD+"/" + "Client"
					, ServiceHandler.GET);
			//
			JSONArray jArray = null;
			if (!StringUtil.isNullOrEmpty(jsonStr)) {
				try {
					jArray = new JSONArray(jsonStr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return jArray;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			// if (pDialog.isShowing())
			// pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */

			if (result != null && result.length() > 0) {

				try {

					JSONObject jObj = result.getJSONObject(0);

					G.CLIENT_LOCATION_INTERVAL = jObj
							.getInt("locInt");
					G.CLIENT_SMS_INTERVAL = jObj.getInt("smsInt");
					G.CLIENT_CALL_INTERVAL = jObj.getInt("callInt");

					G.CLIENT_LOCATION_STATUS = jObj.getInt("loc");
					G.CLIENT_SMS_STATUS = jObj.getInt("sms");
					G.CLIENT_CALL_STATUS = jObj.getInt("calls");
//					G.CLIENT_STATUS = jObj.getInt("status");

					if (!DBRepository.checkHourValue(G.LOCATION_TABLE)) {
						DBRepository.insert(G.LOCATION_TABLE,
								G.CLIENT_LOCATION_INTERVAL, 0,
								G.CLIENT_LOCATION_STATUS);
					} else {
						DBRepository.updateInterval(G.LOCATION_TABLE,
								G.CLIENT_LOCATION_INTERVAL, 0);
						DBRepository.updateEmailStatus(G.LOCATION_TABLE,
								G.CLIENT_LOCATION_STATUS);
					}

					if (!DBRepository.checkHourValue(G.SMS_TABLE)) {
						DBRepository.insert(G.SMS_TABLE, G.CLIENT_SMS_INTERVAL,
								0, G.CLIENT_SMS_STATUS);
					} else {
						DBRepository.updateInterval(G.SMS_TABLE,
								G.CLIENT_SMS_INTERVAL, 0);
						DBRepository.updateEmailStatus(G.SMS_TABLE,
								G.CLIENT_SMS_STATUS);
					}

					if (!DBRepository.checkHourValue(G.CALL_TABLE)) {
						DBRepository
								.insert(G.CALL_TABLE, G.CLIENT_CALL_INTERVAL,
										0, G.CLIENT_CALL_STATUS);
					} else {
						DBRepository.updateInterval(G.CALL_TABLE,
								G.CLIENT_CALL_INTERVAL, 0);
						DBRepository.updateEmailStatus(G.CALL_TABLE,
								G.CLIENT_CALL_STATUS);
					}

//					if (!DBRepository.checkHourValue(G.PHOTO_TABLE)) {
//						DBRepository.insert(G.PHOTO_TABLE,
//								G.CLIENT_PHOTO_INTERVAL, 0, G.CLIENT_STATUS);
//					} else {
//						DBRepository.updateInterval(G.PHOTO_TABLE,
//								G.CLIENT_PHOTO_INTERVAL, 0);
//						DBRepository.updateEmailStatus(G.PHOTO_TABLE,
//								G.CLIENT_STATUS);
//					}

					// SimpleDateFormat s = new SimpleDateFormat(
					// "dd-MM-yyyy hh:mm:ss");
					// String format = s.format(new Date());
					//
					// showHourID.setText(format + "======"
					// + DBRepository.getCurrentHour(G.LOCATION_TABLE));

				} catch (Exception e) {
					// TODO: handle exception
				}

			} else if (result != null && result.length() <= 0) {
				displayToast("Not Exist, Please try again later.");
			} else {
				displayToast("Unable to connect this time, Please try again later.");
			}

		}

	}

	private void displayToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

}