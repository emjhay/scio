package com.scio.ui;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.provider.CallLog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.scio.database.DBRepository;
import com.scio.model.FragmentsAvailable;
import com.scio.model.MyLocationListener;
import com.scio.model.G;
import com.scio.model.Mail;
import com.scio.model.Sms;
import com.scio.util.StringUtil;

public class MainForClient extends FragmentActivity {

	private ProgressDialog pDialog;

	private AlertDialog alert;

	private MyLocationListener mylistner;
	private Geocoder geocoder;
	private List<Address> addresses;

	public FragmentsAvailable currentFragment, nextFragment;
	private Fragment clientFragment;

	private static MainForClient instance;

	// private PopupWindow popup;
	private Mail mail;

	// private String[] state = { "smtp.gmail.com", "smtp.mail.yahoo.com" };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_client);

		instance = this;

		currentFragment = nextFragment = FragmentsAvailable.CLIENT;
		if (savedInstanceState == null) {
			if (findViewById(R.id.clientContainer) != null) {
				clientFragment = new ClientFragment();
				clientFragment.setArguments(getIntent().getExtras());
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.clientContainer, clientFragment,
								currentFragment.toString()).commit();
			}
		}

		

		if (G.TIMER_GLOBAL == null) {
			startService(new Intent(MainForClient.this, BackgroundService.class));
			startService(new Intent(MainForClient.this,
					MailCheckerService.class));
		}

	}

	public void showLogoutDialog() {

		stopService(new Intent(MainForClient.this, BackgroundService.class));
		stopService(new Intent(MainForClient.this, MailCheckerService.class));

		final ProgressDialog progDailog = new ProgressDialog(MainForClient.this);
		progDailog.setMessage("Logging out...");
		progDailog.setIndeterminate(false);
		progDailog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDailog.setCancelable(false);
		progDailog.show();
		// exit();
		new CountDownTimer(3000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				progDailog.setMessage("Logging out...");
			}

			@Override
			public void onFinish() {
				progDailog.dismiss();
				finish();
			}
		}.start();
	}

	static final boolean isInstanciated() {
		return instance != null;
	}

	public static final MainForClient instance() {
		if (instance != null)
			return instance;
		throw new RuntimeException("MainMenu not instantiated yet");
	}

	public void changeCurrentFragment(FragmentsAvailable newFragmentType,
			boolean withoutAnimation) {

		nextFragment = newFragmentType;

		Fragment newFragment = null;

		switch (newFragmentType) {
		case CLIENT:
			newFragment = new ClientFragment();
			break;
		case CAMERA:
			newFragment = new CameraFragment();
			break;

		default:
			break;
		}

		changeFragment(newFragment, newFragmentType, withoutAnimation);
	}

	private void changeFragment(Fragment newFragment,
			FragmentsAvailable newFragmentType, boolean withoutAnimation) {

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();

		// if (withoutAnimation) {
		// if (newFragmentType.isRightOf(currentFragment)) {
		transaction.setCustomAnimations(R.anim.slide_in_right_to_left,
				R.anim.slide_out_right_to_left, R.anim.slide_in_left_to_right,
				R.anim.slide_out_left_to_right);
		// } else {
		// transaction.setCustomAnimations(R.anim.slide_in_left_to_right,
		// R.anim.slide_out_left_to_right,
		// R.anim.slide_in_right_to_left,
		// R.anim.slide_out_right_to_left);
		// }
		// }

		transaction.addToBackStack(newFragmentType.toString());
		transaction.replace(R.id.clientContainer, newFragment,
				newFragmentType.toString());
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();

		currentFragment = newFragmentType;
	}

	public void sendInfoToAdmin() {

		if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
				.getSetInterval(G.PHOTO_TABLE)
				&& DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
			showDialogCamera();
		} else {

			if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
					.getSetInterval(G.LOCATION_TABLE)
					&& DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
				fetchLocation();
			} else if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
					.getSetInterval(G.SMS_TABLE)
					&& DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
				fetchSMSLogs();
			} else if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
					.getSetInterval(G.CALL_TABLE)
					&& DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
				readCallLogs();
			} else {
				displayToast("No pending report to submit.");
			}
		}
	}

	private void fetchLocation() {

		final StringBuilder sb = new StringBuilder();

		(new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				mylistner = new MyLocationListener(getApplicationContext());
				geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
			}

			@Override
			protected void onPostExecute(String msg) {
				super.onPostExecute(msg);

				if (!StringUtil.isNullOrEmpty(msg)) {
					sendViaEmail(" Client " + G.CLIENT_USERNAME + " Location",
							msg);
				} else {
					displayToast("Unable to get location, Please press again the button.");
				}
			}

			@Override
			protected String doInBackground(Void... params) {

				try {
					addresses = geocoder.getFromLocation(
							mylistner.getLatitude(), mylistner.getLongitude(),
							1);
					String address = addresses.get(0).getAddressLine(0);

					String street = addresses.get(0).getSubLocality();
					String city = addresses.get(0).getLocality();
					String country = addresses.get(0).getAddressLine(2);

					sb.append(address + "\n");

					sb.append(street + "\n");
					sb.append(city + "\n");
					sb.append(country + "\n");

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				return sb.toString();
			}

		}).execute();

	}

	private void fetchSMSLogs() {

		(new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String msg) {
				super.onPostExecute(msg);

				if (!StringUtil.isNullOrEmpty(msg.toString())) {
					sendViaEmail(" Client " + G.CLIENT_USERNAME + " SMS logs.",
							msg);
				} else {
					displayToast("No current sms logs available.");
					DBRepository.updateCurrentHour(G.SMS_TABLE, 0);
				}

			}

			@Override
			protected String doInBackground(Void... params) {

				StringBuilder sb = new StringBuilder();
				List<Sms> listSms = getAllSms();

				for (int i = 0; i < listSms.size(); i++) {

					System.out.println("TIME-----" + listSms.get(i).getTime());
					sb.append(listSms.get(i).getAddress() + "\n");
					sb.append(listSms.get(i).getTime() + "\n");
					sb.append(listSms.get(i).getMsg() + "\n");

				}

				return sb.toString();
			}

		}).execute();

	}

	public List<Sms> getAllSms() {
		List<Sms> lstSms = new ArrayList<Sms>();
		Uri message = Uri.parse("content://sms/");
		ContentResolver cr = getContentResolver();

		Cursor c = cr.query(message, null, null, null, null);

		if (c.getCount() > 0 && c.moveToFirst()) {

			do {
				Sms objSms = new Sms();
				objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
				objSms.setAddress(c.getString(c
						.getColumnIndexOrThrow("address")));
				objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
				objSms.setReadState(c.getString(c.getColumnIndex("read")));
				objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
				if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
					objSms.setFolderName("inbox");
				} else {
					objSms.setFolderName("sent");
				}
				lstSms.add(objSms);
			} while (c.moveToNext());
		}

		c.close();

		return lstSms;
	}

	private void readCallLogs() {

		(new AsyncTask<Void, Void, String>() {

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
			}

			@Override
			protected void onPostExecute(String msg) {
				super.onPostExecute(msg);

				if (!StringUtil.isNullOrEmpty(msg)) {
					sendViaEmail(
							" Client " + G.CLIENT_USERNAME + " Call logs.", msg);
				} else {
					displayToast("No current call logs available.");
					DBRepository.updateCurrentHour(G.CALL_TABLE, 0);
				}
			}

			@Override
			protected String doInBackground(Void... params) {

				StringBuilder sb = new StringBuilder();

				final SimpleDateFormat completeFormat = new SimpleDateFormat(
						"yyyy-MM-dd", Locale.ENGLISH);
				Calendar calendar = Calendar.getInstance();
				String currtime = completeFormat.format(calendar.getTime());

				Cursor callLogCursor = getContentResolver()
						.query(android.provider.CallLog.Calls.CONTENT_URI,
								null,
								null,
								null,
								android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);

				if (callLogCursor.getCount() > 0 && callLogCursor.moveToFirst()) {
					do {

						String name = callLogCursor.getString(callLogCursor
								.getColumnIndex(CallLog.Calls.CACHED_NAME));
						String cacheNumber = callLogCursor
								.getString(callLogCursor
										.getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
						String number = callLogCursor.getString(callLogCursor
								.getColumnIndex(CallLog.Calls.NUMBER));
						long dateTimeMillis = callLogCursor
								.getLong(callLogCursor
										.getColumnIndex(CallLog.Calls.DATE));
						long durationMillis = callLogCursor
								.getLong(callLogCursor
										.getColumnIndex(CallLog.Calls.DURATION));
						int callType = callLogCursor.getInt(callLogCursor
								.getColumnIndex(CallLog.Calls.TYPE));

						String duration = getDuration(durationMillis * 1000);

						if (cacheNumber == null)
							cacheNumber = number;
						if (name == null)
							name = "No Name";

						Date callTime = new Date(dateTimeMillis);
						String date = completeFormat.format(callTime.getTime());

						if (currtime.equals(date)) {

							sb.append("========================" + "\n");
							sb.append(name + "\n");
							sb.append(cacheNumber + "\n");
							sb.append(duration + "\n");
							sb.append(date + "\n");
							sb.append(getCallType(callType) + "\n");
							sb.append("========================" + "\n");
						}

					} while (callLogCursor.moveToNext());
				}

				callLogCursor.close();

				return sb.toString();
			}

		}).execute();
	}

	public void sendViaEmail(String subject, String msg) {
		if (G.CLIENT_CLIENT_EMAIL.contains("gmail")) {
			G.POPUP_HOST = "smtp.gmail.com";
		} else if (G.CLIENT_CLIENT_EMAIL.contains("yahoo")) {
			G.POPUP_HOST = "smtp.mail.yahoo.com";
		}

		mail = new Mail(G.CLIENT_CLIENT_EMAIL, G.CLIENT_PASS_EMAIL,
				G.POPUP_HOST);

		new SendEmail(G.CLIENT_CLIENT_EMAIL, G.CLIENT_ADMIN_EMAIL,
				G.CLIENT_PASS_EMAIL, G.POPUP_HOST, msg, subject).execute();
	}

	public void runAlert() {
		showDialog("Reminder", "Please Press button to confirm.");

		Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(2000);
	}

	private void showDialog(final String title, final String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				getApplicationContext());

		builder.setTitle(title);
		builder.setMessage(message).setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						if (!G.SHOW_SCREEN_CLIENT && !G.SHOW_SCREEN_CAMERA)
							openMainClientClass();
						else if (G.SHOW_SCREEN_CAMERA)
							changeCurrentFragment(FragmentsAvailable.CLIENT,
									true);
					}
				});
		if (alert == null) {
			alert = builder.create();
			alert.getWindow().setType(
					WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
			alert.show();
		} else if (alert != null && !alert.isShowing()) {
			alert.show();
		}

	}

	public void openMainClientClass() {
		Intent intent = new Intent(this, MainForClient.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
		AlarmManager alarmManagersession = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManagersession.set(AlarmManager.RTC_WAKEUP, 0, pIntent);
		alarmManagersession.cancel(pIntent);
		finish();
	}

	// private class FetchUsers extends AsyncTask<JSONArray, Void, JSONArray> {
	//
	// private Context mContext;
	//
	// public FetchUsers(Context context) {
	// this.mContext = context;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// // Showing progress dialog
	// pDialog = new ProgressDialog(mContext);
	// pDialog.setMessage("Please wait...");
	// pDialog.setCancelable(false);
	// pDialog.show();
	//
	// }
	//
	// @Override
	// protected JSONArray doInBackground(JSONArray... arg0) {
	// // Creating service handler class instance
	// ServiceHandler sh = new ServiceHandler();
	//
	// // Making a request to url and getting response
	//
	// String jsonStr = sh.makeServiceCall(G.URL + G.CLIENT
	// + G.LOGINWITH_USERTYPE + G.CLIENT_USERNAME + "/" + "Client"
	// + "/" + G.CLIENT_PASSWORD, ServiceHandler.GET);
	// //
	// JSONArray jArray = null;
	// if (!StringUtil.isNullOrEmpty(jsonStr)) {
	// try {
	// jArray = new JSONArray(jsonStr);
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// return jArray;
	// }
	//
	// @Override
	// protected void onPostExecute(JSONArray result) {
	// super.onPostExecute(result);
	// // Dismiss the progress dialog
	// if (pDialog.isShowing())
	// pDialog.dismiss();
	// /**
	// * Updating parsed JSON data into ListView
	// * */
	//
	// if (result != null && result.length() > 0) {
	//
	// try {
	//
	// JSONObject jObj = result.getJSONObject(0);
	//
	// G.CLIENT_LOCATION_INTERVAL = jObj
	// .getInt("locationInterval");
	// G.CLIENT_SMS_INTERVAL = jObj.getInt("smsInterval");
	// G.CLIENT_CALL_INTERVAL = jObj.getInt("callInterval");
	// G.CLIENT_PHOTO_INTERVAL = jObj.getInt("photoInterval");
	//
	// G.CLIENT_LOCATION_STATUS = jObj.getInt("locationStatus");
	// G.CLIENT_SMS_STATUS = jObj.getInt("smsStatus");
	// G.CLIENT_CALL_STATUS = jObj.getInt("callStatus");
	// G.CLIENT_STATUS = jObj.getInt("status");
	//
	// if (!DBRepository.checkHourValue(G.LOCATION_TABLE)) {
	// DBRepository.insert(G.LOCATION_TABLE,
	// G.CLIENT_LOCATION_INTERVAL, 0,
	// G.CLIENT_LOCATION_STATUS);
	// } else {
	// DBRepository.updateInterval(G.LOCATION_TABLE,
	// G.CLIENT_LOCATION_INTERVAL, 0);
	// DBRepository.updateEmailStatus(G.LOCATION_TABLE,
	// G.CLIENT_LOCATION_STATUS);
	// }
	//
	// if (!DBRepository.checkHourValue(G.SMS_TABLE)) {
	// DBRepository.insert(G.SMS_TABLE, G.CLIENT_SMS_INTERVAL,
	// 0, G.CLIENT_SMS_STATUS);
	// } else {
	// DBRepository.updateInterval(G.SMS_TABLE,
	// G.CLIENT_SMS_INTERVAL, 0);
	// DBRepository.updateEmailStatus(G.SMS_TABLE,
	// G.CLIENT_SMS_STATUS);
	// }
	//
	// if (!DBRepository.checkHourValue(G.CALL_TABLE)) {
	// DBRepository
	// .insert(G.CALL_TABLE, G.CLIENT_CALL_INTERVAL,
	// 0, G.CLIENT_CALL_STATUS);
	// } else {
	// DBRepository.updateInterval(G.CALL_TABLE,
	// G.CLIENT_CALL_INTERVAL, 0);
	// DBRepository.updateEmailStatus(G.CALL_TABLE,
	// G.CLIENT_CALL_STATUS);
	// }
	//
	// if (!DBRepository.checkHourValue(G.PHOTO_TABLE)) {
	// DBRepository.insert(G.PHOTO_TABLE,
	// G.CLIENT_PHOTO_INTERVAL, 0, G.CLIENT_STATUS);
	// } else {
	// DBRepository.updateInterval(G.PHOTO_TABLE,
	// G.CLIENT_PHOTO_INTERVAL, 0);
	// DBRepository.updateEmailStatus(G.PHOTO_TABLE,
	// G.CLIENT_STATUS);
	// }
	//
	// // SimpleDateFormat s = new SimpleDateFormat(
	// // "dd-MM-yyyy hh:mm:ss");
	// // String format = s.format(new Date());
	// //
	// // showHourID.setText(format + "======"
	// // + DBRepository.getCurrentHour(G.LOCATION_TABLE));
	//
	// } catch (Exception e) {
	// // TODO: handle exception
	// }
	//
	// } else if (result != null && result.length() <= 0) {
	// displayToast("Not Exist, Please try again later.");
	// } else {
	// displayToast("Unable to connect this time, Please try again later.");
	// }
	//
	// }
	//
	// }

	private void displayToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (currentFragment == FragmentsAvailable.CLIENT) {
				// G.TIMER_GLOBAL.cancel();
				// G.TIMER_GLOBAL.purge();
				// G.TIMER_GLOBAL = null;
				// stopService(new Intent(MainForClient.this,
				// BackgroundService.class));
				// stopService(new Intent(MainForClient.this,
				// MailCheckerService.class));
				// finish();
				startActivity(new Intent().setAction(Intent.ACTION_MAIN)
						.addCategory(Intent.CATEGORY_HOME));
				return true;
			} else if (currentFragment == FragmentsAvailable.CAMERA) {
				changeCurrentFragment(FragmentsAvailable.CLIENT, true);
				return true;
			}

		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		G.SHOW_SCREEN_CLIENT = true;
		super.onResume();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		G.SHOW_SCREEN_CLIENT = false;
		G.SHOW_SCREEN_CAMERA = false;
		super.onStop();
	}

	private String getDuration(long milliseconds) {
		int seconds = (int) (milliseconds / 1000) % 60;
		int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
		int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
		if (hours < 1)
			return minutes + ":" + seconds;
		return hours + ":" + minutes + ":" + seconds;
	}

	private String getCallType(int type) {

		String value = "";

		if (type == CallLog.Calls.INCOMING_TYPE) {
			value = getString(R.string.title_incoming_call);
		} else if (type == CallLog.Calls.OUTGOING_TYPE) {
			value = getString(R.string.title_outgoing_call);
		} else if (type == CallLog.Calls.MISSED_TYPE) {
			value = getString(R.string.title_missed_call);
		} else if (type == 4) {
			value = getString(R.string.title_missed_call);
		} else if (type == 5) {
			value = getString(R.string.title_missed_call);
		} else if (type == 6) {
			value = getString(R.string.title_missed_call);
		}

		return value;
	}

	public void showDialogCamera() {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				getApplicationContext());
		builder.setTitle("Alert");
		builder.setMessage("Press OK to take a photo.").setCancelable(false)
				.setNeutralButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						changeCurrentFragment(FragmentsAvailable.CAMERA, true);
					}
				});
		AlertDialog alertDialog = builder.create();
		alertDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alertDialog.show();

	}

	// public void sendPhoto() {
	// new SendEmail("", "Current " + G.CLIENT_USERNAME + " photo.");
	// }

	// private class SendEmail extends AsyncTask<Void, Void, Boolean> {
	//
	// String mSubject;
	// String mMsg;
	//
	// public SendEmail(String msg, String subject) {
	// this.mMsg = msg;
	// this.mSubject = subject;
	// }
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// // Showing progress dialog
	// pDialog = new ProgressDialog(MainForClient.this);
	// pDialog.setMessage("Sending email, Please wait...");
	// pDialog.setCancelable(false);
	// pDialog.show();
	//
	// }
	//
	// @Override
	// protected Boolean doInBackground(Void... arg0) {
	// String externamStorage = Environment.getExternalStorageDirectory()
	// .toString();
	//
	// Looper.prepare();
	// // Creating service handler class instance
	//
	// boolean result = false;
	//
	// Mail m = new Mail(G.POPUP_EMAIL, G.POPUP_PASS, G.POPUP_HOST);
	//
	// String[] toArr = { G.CLIENT_EMAIL }; // This is an array,
	// // you can add more
	// // emails, just
	// // separate them
	// // with a coma
	// m.setTo(toArr); // load array to setTo function
	// m.setFrom(G.POPUP_EMAIL); // who is sending the email
	// m.setSubject(mSubject);
	// m.setBody(mMsg);
	//
	// try {
	//
	// if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
	// .getSetInterval(G.PHOTO_TABLE)
	// && DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
	// m.addAttachment(externamStorage
	// + "/DCIM/Camera/20140404_094540.jpg"); // path to
	// // file you
	// // want to
	// // attach
	// }
	//
	// if (m.send()) {
	// // success
	// result = true;
	// } else {
	// // failure
	// result = false;
	// }
	// } catch (Exception e) {
	// // some other problem
	// result = false;
	// }
	//
	// return result;
	// }
	//
	// @Override
	// protected void onPostExecute(Boolean result) {
	// super.onPostExecute(result);
	// // Dismiss the progress dialog
	// if (pDialog.isShowing())
	// pDialog.dismiss();
	//
	// if (result) {
	//
	// if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
	// .getSetInterval(G.PHOTO_TABLE)
	// && DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
	// DBRepository.updateCurrentHour(G.PHOTO_TABLE, 0);
	// } else {
	//
	// if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
	// .getSetInterval(G.LOCATION_TABLE)
	// && DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
	// DBRepository.updateCurrentHour(G.LOCATION_TABLE, 0);
	// } else if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
	// .getSetInterval(G.SMS_TABLE)
	// && DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
	// DBRepository.updateCurrentHour(G.SMS_TABLE, 0);
	// } else if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
	// .getSetInterval(G.CALL_TABLE)
	// && DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
	// DBRepository.updateCurrentHour(G.CALL_TABLE, 0);
	//
	// }
	// }
	// } else {
	// displayToast("Unable to send email! Please check email and password.");
	// }
	// }
	//
	// }

	// public void popEmailSetup(final String msg, final String subject) {
	// LayoutInflater inflater = (LayoutInflater)
	// getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	//
	// popup = new PopupWindow(inflater.inflate(R.layout.popup_client, null,
	// false), LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
	// true);
	//
	// popup.setAnimationStyle(Animation.START_ON_FIRST_FRAME);
	//
	// popup.setBackgroundDrawable(getResources().getDrawable(
	// R.drawable.bg_dialog));
	//
	// final View pvu = popup.getContentView();
	//
	// pvu.setOnKeyListener(new View.OnKeyListener() {
	//
	// @Override
	// public boolean onKey(View v, int keyCode, KeyEvent event) {
	// if (keyCode == KeyEvent.KEYCODE_BACK) {
	// popup.dismiss();
	// }
	// return false;
	// }
	// });
	//
	//
	// final ImageView icEmailImage = (ImageView)
	// pvu.findViewById(R.id.emailImageID);
	//
	//
	// ImageView icPopEmail = (ImageView) pvu
	// .findViewById(R.id.icPopupEmailID);
	// ImageView icPopPass = (ImageView) pvu.findViewById(R.id.icPopupPassID);
	//
	//
	//
	// final EditText emailPopup = (EditText) pvu
	// .findViewById(R.id.emailPopupID);
	// emailPopup.setSelection(emailPopup.getText().toString().length());
	// FieldTextUtil.popEmailTextWatcher(emailPopup, icPopEmail, icEmailImage);
	//
	// final EditText passPopup = (EditText) pvu
	// .findViewById(R.id.passPopupID);
	// FieldTextUtil.popPassTextWatcher(passPopup, icPopPass);
	//
	// TextView alertMsgPopup = (TextView) pvu
	// .findViewById(R.id.alertMsgPopupID);
	// alertMsgPopup.setText(G.POPUP_ALERT_MSG);
	//
	// final CheckBox emailCheck = (CheckBox) pvu
	// .findViewById(R.id.checkEmailPopupID);
	// final CheckBox passCheck = (CheckBox) pvu
	// .findViewById(R.id.checkPassPopupID);
	//
	// DBRepository.setEmailPassValue(G.EMAIL_STORAGE_TABLE,
	// G.CLIENT_USERNAME, G.CLIENT_PASSWORD, emailPopup, passPopup,
	// emailCheck, passCheck);
	//
	// Button okBtn = (Button) pvu.findViewById(R.id.okEmailBtnID);
	// okBtn.setText("Send");
	// okBtn.setOnClickListener(new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// // TODO Auto-generated method stub
	//
	// if (StringUtil.isNullOrEmpty(emailPopup.getText().toString())) {
	// displayToast("Email field cannot be empty.");
	// return;
	// } else if (StringUtil.isNullOrEmpty(emailPopup.getText()
	// .toString())) {
	// displayToast("Password field cannot be empty.");
	// return;
	// }
	//
	// if (emailPopup.getText().toString().contains("gmail")) {
	// // hostSpin.setSelection(0);
	// G.POPUP_HOST = "smtp.gmail.com";
	// } else if (emailPopup.getText().toString().contains("yahoo")) {
	// // hostSpin.setSelection(1);
	// G.POPUP_HOST = "smtp.mail.yahoo.com";
	// }
	//
	// G.POPUP_EMAIL = emailPopup.getText().toString();
	// G.POPUP_PASS = passPopup.getText().toString();
	// // G.POPUP_HOST = hostSpin.getSelectedItem().toString();
	//
	// if (emailCheck.isChecked() && passCheck.isChecked()) {
	//
	// if (!DBRepository.getEmailStorageUser(
	// G.EMAIL_STORAGE_TABLE, G.CLIENT_USERNAME,
	// G.CLIENT_PASSWORD, emailPopup.getText().toString(),
	// passPopup.getText().toString())) {
	// System.out.println("1===============insert");
	// DBRepository.insertEmailStorage(G.EMAIL_STORAGE_TABLE,
	// G.CLIENT_USERNAME, G.CLIENT_PASSWORD,
	// emailPopup.getText().toString(), passPopup
	// .getText().toString(), 1, 1);
	// } else {
	// System.out.println("1===============update");
	// DBRepository.updateEmailStorageEmailPass(
	// G.EMAIL_STORAGE_TABLE, emailPopup.getText()
	// .toString(), passPopup.getText()
	// .toString(), 1, 1);
	// }
	// } else if (emailCheck.isChecked()) {
	//
	// if (!DBRepository.getEmailStorageUser(
	// G.EMAIL_STORAGE_TABLE, G.CLIENT_USERNAME,
	// G.CLIENT_PASSWORD, emailPopup.getText().toString(),
	// passPopup.getText().toString())) {
	// System.out.println("2===============insert");
	// DBRepository.insertEmailStorage(G.EMAIL_STORAGE_TABLE,
	// G.CLIENT_USERNAME, G.CLIENT_PASSWORD,
	// emailPopup.getText().toString(), passPopup
	// .getText().toString(), 1, 0);
	// } else {
	// System.out.println("2===============update");
	// DBRepository.updateEmailStorageEmailPass(
	// G.EMAIL_STORAGE_TABLE, emailPopup.getText()
	// .toString(), passPopup.getText()
	// .toString(), 1, 0);
	// }
	// } else if (passCheck.isChecked()) {
	//
	// if (!DBRepository.getEmailStorageUser(
	// G.EMAIL_STORAGE_TABLE, G.CLIENT_USERNAME,
	// G.CLIENT_PASSWORD, emailPopup.getText().toString(),
	// passPopup.getText().toString())) {
	// System.out.println("3===============insert");
	// DBRepository.insertEmailStorage(G.EMAIL_STORAGE_TABLE,
	// G.CLIENT_USERNAME, G.CLIENT_PASSWORD,
	// emailPopup.getText().toString(), passPopup
	// .getText().toString(), 0, 0);
	// } else {
	// System.out.println("3===============update");
	// DBRepository.updateEmailStorageEmailPass(
	// G.EMAIL_STORAGE_TABLE, emailPopup.getText()
	// .toString(), passPopup.getText()
	// .toString(), 1, 0);
	// }
	// } else {
	// if (!DBRepository.getEmailStorageUser(
	// G.EMAIL_STORAGE_TABLE, G.CLIENT_USERNAME,
	// G.CLIENT_PASSWORD, emailPopup.getText().toString(),
	// passPopup.getText().toString())) {
	// System.out.println("4===============insert");
	// DBRepository.insertEmailStorage(G.EMAIL_STORAGE_TABLE,
	// G.CLIENT_USERNAME, G.CLIENT_PASSWORD,
	// emailPopup.getText().toString(), passPopup
	// .getText().toString(), 0, 0);
	// } else {
	// System.out.println("4===============update");
	// DBRepository.updateEmailStorageEmailPass(
	// G.EMAIL_STORAGE_TABLE, emailPopup.getText()
	// .toString(), passPopup.getText()
	// .toString(), 0, 0);
	//
	// }
	// }
	//
	// // new SendEmail(msg, subject).execute();
	// new SendEmail(emailPopup.getText().toString(), G.CLIENT_ADMIN_EMAIL,
	// passPopup.getText().toString(), G.POPUP_HOST, msg, subject)
	// .execute();
	//
	// popup.dismiss();
	//
	// }
	// });
	//
	// findViewById(R.id.mainClientID).post(new Runnable() {
	// public void run() {
	// popup.showAtLocation(findViewById(R.id.mainClientID),
	// Gravity.CENTER, 0, 0);
	// }
	// });
	// }

	// private class RunSingleTask extends AsyncTask<Void, Void, String> {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// // Showing progress dialog
	// pDialog = new ProgressDialog(getApplicationContext());
	// pDialog.setMessage("Please wait...");
	// pDialog.setCancelable(false);
	// pDialog.show();
	//
	// if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
	// .getSetInterval(G.LOCATION_TABLE)
	// && DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
	// mylistner = new MyLocationListener(getApplicationContext());
	// geocoder = new Geocoder(getApplicationContext(), Locale.ENGLISH);
	// }
	// }
	//
	// @Override
	// protected String doInBackground(Void... arg0) {
	// // Creating service handler class instance
	//
	// StringBuilder sb = new StringBuilder();
	//
	// if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
	// .getSetInterval(G.LOCATION_TABLE)
	// && DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
	// try {
	// addresses = geocoder.getFromLocation(
	// mylistner.getLatitude(), mylistner.getLongitude(),
	// 1);
	// String address = addresses.get(0).getAddressLine(0);
	//
	// String street = addresses.get(0).getSubLocality();
	// String city = addresses.get(0).getLocality();
	// String country = addresses.get(0).getAddressLine(2);
	//
	// sb.append(address + "\n");
	//
	// sb.append(street + "\n");
	// sb.append(city + "\n");
	// sb.append(country + "\n");
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// } else if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
	// .getSetInterval(G.SMS_TABLE)
	// && DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
	//
	// List<Sms> listSms = getAllSms();
	//
	// for (int i = 0; i < listSms.size(); i++) {
	//
	// System.out.println("TIME-----" + listSms.get(i).getTime());
	// sb.append(listSms.get(i).getAddress() + "\n");
	// sb.append(listSms.get(i).getTime() + "\n");
	// sb.append(listSms.get(i).getMsg() + "\n");
	//
	// }
	//
	// } else if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
	// .getSetInterval(G.CALL_TABLE)
	// && DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
	// // readCallLogs();
	// final SimpleDateFormat completeFormat = new SimpleDateFormat(
	// "yyyy-MM-dd", Locale.ENGLISH);
	// Calendar calendar = Calendar.getInstance();
	// String currtime = completeFormat.format(calendar.getTime());
	//
	// Cursor callLogCursor = getContentResolver()
	// .query(android.provider.CallLog.Calls.CONTENT_URI,
	// null,
	// null,
	// null,
	// android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);
	//
	// if (callLogCursor.getCount() > 0 && callLogCursor.moveToFirst()) {
	// do {
	//
	// String name = callLogCursor.getString(callLogCursor
	// .getColumnIndex(CallLog.Calls.CACHED_NAME));
	// String cacheNumber = callLogCursor
	// .getString(callLogCursor
	// .getColumnIndex(CallLog.Calls.CACHED_NUMBER_LABEL));
	// String number = callLogCursor.getString(callLogCursor
	// .getColumnIndex(CallLog.Calls.NUMBER));
	// long dateTimeMillis = callLogCursor
	// .getLong(callLogCursor
	// .getColumnIndex(CallLog.Calls.DATE));
	// long durationMillis = callLogCursor
	// .getLong(callLogCursor
	// .getColumnIndex(CallLog.Calls.DURATION));
	// int callType = callLogCursor.getInt(callLogCursor
	// .getColumnIndex(CallLog.Calls.TYPE));
	//
	// String duration = getDuration(durationMillis * 1000);
	//
	// if (cacheNumber == null)
	// cacheNumber = number;
	// if (name == null)
	// name = "No Name";
	//
	// Date callTime = new Date(dateTimeMillis);
	// String date = completeFormat.format(callTime.getTime());
	//
	// if (currtime.equals(date)) {
	//
	// sb.append("========================" + "\n");
	// sb.append(name + "\n");
	// sb.append(cacheNumber + "\n");
	// sb.append(duration + "\n");
	// sb.append(date + "\n");
	// sb.append(getCallType(callType) + "\n");
	// sb.append("========================" + "\n");
	// }
	//
	// } while (callLogCursor.moveToNext());
	// }
	// }
	//
	// return sb.toString();
	// }
	//
	// @Override
	// protected void onPostExecute(String msg) {
	// super.onPostExecute(msg);
	// // Dismiss the progress dialog
	// if (pDialog.isShowing())
	// pDialog.dismiss();
	// /**
	// * Updating parsed JSON data into ListView
	// * */
	//
	// if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
	// .getSetInterval(G.LOCATION_TABLE)
	// && DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
	// popEmailSetup(msg, "Current " + G.CLIENT_USERNAME
	// + " location.");
	//
	// } else if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
	// .getSetInterval(G.SMS_TABLE)
	// && DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
	// popEmailSetup(msg, "Current " + G.CLIENT_USERNAME
	// + " SMS logs.");
	//
	// } else if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
	// .getSetInterval(G.CALL_TABLE)
	// && DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
	// popEmailSetup(msg, "Current " + G.CLIENT_USERNAME
	// + " call logs.");
	// }
	// }
	//
	// }

	private class SendEmail extends AsyncTask<Void, Void, Boolean> {

		String mEmailFrom;
		String mEmailTo;
		String mSubject;
		String mMsg;

		public SendEmail(String emailFrom, String emailTo, String pass,
				String host, String msg, String subject) {
			this.mEmailFrom = emailFrom;
			this.mEmailTo = emailTo;
			this.mSubject = subject;
			this.mMsg = msg;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog

			pDialog = new ProgressDialog(MainForClient.this);
			pDialog.setMessage("Sending email, Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			// Looper.prepare();
			// Creating service handler class instance

			boolean result = false;

			String[] toArr = { mEmailTo }; // This is an array,
											// you can add more
											// emails, just
											// separate them
											// with a coma
			mail.setTo(toArr); // load array to setTo function
			mail.setFrom(mEmailFrom); // who is sending the email
			mail.setSubject(mSubject);
			mail.setBody(mMsg);

			try {

				if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
						.getSetInterval(G.PHOTO_TABLE)
						&& DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1
						&& G.BYTE_ARRAY != null) {
					mail.addAttachmentPhoto("Photo", G.BYTE_ARRAY);
				}

				if (mail.send()) {
					// success
					result = true;
				} else {
					// failure
					result = false;
				}
			} catch (Exception e) {
				// some other problem
				result = false;
			}

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();

			if (result) {

				if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
						.getSetInterval(G.PHOTO_TABLE)
						&& DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
					DBRepository.updateCurrentHour(G.PHOTO_TABLE, 0);
				} else {

					if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
							.getSetInterval(G.LOCATION_TABLE)
							&& DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
						System.out.println("================LOCATION_TABLE");
						DBRepository.updateCurrentHour(G.LOCATION_TABLE, 0);
					} else if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
							.getSetInterval(G.SMS_TABLE)
							&& DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
						System.out.println("================SMS_TABLE");
						DBRepository.updateCurrentHour(G.SMS_TABLE, 0);
					} else if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
							.getSetInterval(G.CALL_TABLE)
							&& DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
						System.out.println("================CALL_TABLE");
						DBRepository.updateCurrentHour(G.CALL_TABLE, 0);

					}
				}

				displayToast("Email Sent.");
			} else {
				displayToast("Unable to send email! Please check email and password.");
			}

		}

	}

	// @Override
	// protected void onDestroy() {
	// // TODO Auto-generated method stub
	// stopService(new Intent(MainForClient.this, BackgroundService.class));
	// super.onDestroy();
	// }

	public void sendViaEmailInBG(String subject, String msg) {
		if (G.CLIENT_CLIENT_EMAIL.contains("gmail")) {
			G.POPUP_HOST = "smtp.gmail.com";
		} else if (G.CLIENT_CLIENT_EMAIL.contains("yahoo")) {
			G.POPUP_HOST = "smtp.mail.yahoo.com";
		}

		mail = new Mail(G.CLIENT_CLIENT_EMAIL, G.CLIENT_PASS_EMAIL,
				G.POPUP_HOST);

		new SendEmailInBG(G.CLIENT_CLIENT_EMAIL, G.CLIENT_ADMIN_EMAIL,
				G.CLIENT_PASS_EMAIL, G.POPUP_HOST, msg, subject).execute();
	}

	private class SendEmailInBG extends AsyncTask<Void, Void, Boolean> {

		String mEmailFrom;
		String mEmailTo;
		String mSubject;
		String mMsg;

		public SendEmailInBG(String emailFrom, String emailTo, String pass,
				String host, String msg, String subject) {
			this.mEmailFrom = emailFrom;
			this.mEmailTo = emailTo;
			this.mSubject = subject;
			this.mMsg = msg;

		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			// Looper.prepare();
			// Creating service handler class instance

			boolean result = false;

			String[] toArr = { mEmailTo }; // This is an array,
											// you can add more
											// emails, just
											// separate them
											// with a coma
			mail.setTo(toArr); // load array to setTo function
			mail.setFrom(mEmailFrom); // who is sending the email
			mail.setSubject(mSubject);
			mail.setBody(mMsg);

			try {

				if (mail.send()) {
					// success
					result = true;
				} else {
					// failure
					result = false;
				}
			} catch (Exception e) {
				// some other problem
				result = false;
			}

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);

			if (result) {

				if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
						.getSetInterval(G.PHOTO_TABLE)
						&& DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
					DBRepository.updateCurrentHour(G.PHOTO_TABLE, 0);
				} else {

					if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
							.getSetInterval(G.LOCATION_TABLE)
							&& DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
						System.out.println("================LOCATION_TABLE");
						DBRepository.updateCurrentHour(G.LOCATION_TABLE, 0);
					} else if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
							.getSetInterval(G.SMS_TABLE)
							&& DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
						System.out.println("================SMS_TABLE");
						DBRepository.updateCurrentHour(G.SMS_TABLE, 0);
					} else if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
							.getSetInterval(G.CALL_TABLE)
							&& DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
						System.out.println("================CALL_TABLE");
						DBRepository.updateCurrentHour(G.CALL_TABLE, 0);

					}
				}

				displayToast("Email Sent.");
			} else {
				displayToast("Unable to send email! Please check email and password.");
			}

		}

	}

}