package com.scio.ui;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;

import com.scio.model.EmailValidator;
import com.scio.model.FragmentAvailable;
import com.scio.model.G;
import com.scio.model.Mail;
import com.scio.model.ServiceHandler;
import com.scio.util.FieldTextUtil;
import com.scio.util.StringUtil;
import com.scio.util.TimeUtil;

public class LoginScreen extends Fragment implements OnItemSelectedListener,
		OnClickListener {

	private Spinner userType;
	private String[] state = { "Admin", "Client" };
	private Button signin, register;

	private ProgressDialog pDialog;

	private EditText loginUserName, loginPassword;
	private TextView forgotPass;
	private PopupWindow popup;
	private Mail mail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_sciomain, container,
				false);
		// Intent in = new
		// Intent(ACTION_MAIN).setClass(getActivity(),LocationIntervalService.class);
		//
		// getActivity().startService(in);
		// getActivity().stopService(in);

		userType = (Spinner) view.findViewById(R.id.userTypeID);
		signin = (Button) view.findViewById(R.id.signInID);
		signin.setOnClickListener(this);

		register = (Button) view.findViewById(R.id.registerID);
		register.setOnClickListener(this);

		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(
				getActivity(), R.layout.spinner_item, state);
		adapter_state
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		userType.setAdapter(adapter_state);
		userType.setOnItemSelectedListener(this);

		if (userType.getSelectedItemPosition() == 0)
			register.setVisibility(View.VISIBLE);
		else
			register.setVisibility(View.GONE);

		loginUserName = (EditText) view.findViewById(R.id.loginUserNameID);
//		loginUserName.setText("dummy");
		loginPassword = (EditText) view.findViewById(R.id.loginPasswordID);
//		loginPassword.setText("dummypass");
		forgotPass = (TextView) view.findViewById(R.id.forgotPassordID);
		forgotPass.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				forgotPass.setSelected(false);

				if (!SCIOMain.instance().haveNetworkConnection()) {
					SCIOMain.instance().displayToast(
							getString(R.string.error_network_unreachable));
					return;
				}

				showPopupForgot();
			}
		});

		((Button) view.findViewById(R.id.signInID)).setTypeface(G.FONT);
		((Button) view.findViewById(R.id.registerID)).setTypeface(G.FONT);

		return view;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		userType.setSelection(position);
		// String user = (String) userType.getSelectedItem();

		if (position == 0) {
			register.setVisibility(View.VISIBLE);
			forgotPass.setVisibility(View.VISIBLE);
		} else {
			register.setVisibility(View.GONE);
			forgotPass.setVisibility(View.GONE);
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.signInID:
			// Intent intentAdmin = new Intent(getActivity(),
			// MainForAdmin.class);
			// startActivity(intentAdmin);
			loginWithUserType();
			break;

		case R.id.registerID:
			SCIOMain.instance().changeFragment(FragmentAvailable.REGISTER);
			break;

		default:
			break;
		}

	}

	private void loginWithUserType() {
		if (!SCIOMain.instance().haveNetworkConnection()) {
			SCIOMain.instance().displayToast(
					getString(R.string.error_network_unreachable));
			return;
		} else if (StringUtil.isNullOrEmpty(loginUserName.getText().toString())) {
			SCIOMain.instance().displayToast("Username cannot be blank!");
			return;
		} else if (StringUtil.isNullOrEmpty(loginPassword.getText().toString())) {
			SCIOMain.instance().displayToast("Password cannot be blank!");
			return;
		}

		new FetchUsers().execute();

	}

	private class FetchUsers extends AsyncTask<JSONArray, Void, JSONArray> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected JSONArray doInBackground(JSONArray... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response

			String model = "";
			String usertype = "";
			String verified = "";

			switch (userType.getSelectedItemPosition()) {
			case 0:
				model = G.ADMIN;
				usertype = "Admin";
				verified = "/1";
				break;
			case 1:
				model = G.CLIENT;
				usertype = "Client";
				verified = "";
				break;

			default:
				break;
			}

			JSONArray jArray = null;

			// if (!DBRepository.checkIfUserExpired(G.EXPIRY_TABLE,
			// loginUserName
			// .getText().toString(), loginPassword.getText().toString(),
			// userType.getSelectedItem().toString())) {

			System.out.println(G.URL + model + G.LOGINWITH_USERTYPE
					+ loginUserName.getText().toString() + "/"
					+ loginPassword.getText().toString() + "/" + usertype + "/"
					+ verified);

			String jsonStr = sh.makeServiceCall(G.URL + model
					+ G.LOGINWITH_USERTYPE + loginUserName.getText().toString()
					+ "/" + loginPassword.getText().toString() + "/" + usertype
					+ "/" + verified, ServiceHandler.GET);

			System.out.println("jsonStr=========" + jsonStr);

			if (!StringUtil.isNullOrEmpty(jsonStr)) {
				try {
					jArray = new JSONArray(jsonStr);

					// if (jArray != null
					// && jArray.length() > 0) {
					// G.CHECK_IF_EXPIRED = true;
					// } else {
					// G.CHECK_IF_EXPIRED = false;
					// }
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// else {
			// G.CHECK_IF_EXPIRED = false;
			// }

			// } else {
			// G.CHECK_IF_EXPIRED = true;
			// }

			return jArray;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */

			System.out.println("result=======" + result);

			if (result != null && result.length() > 0) {

				switch (userType.getSelectedItemPosition()) {
				case 0:
					G.ADMIN_USERNAME = loginUserName.getText().toString();
					G.ADMIN_PASSWORD = loginPassword.getText().toString();
					try {
						G.ADMIN_EMAIL = result.getJSONObject(0).getString(
								"email");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Intent intentAdmin = new Intent(getActivity(),
							MainForAdmin.class);
					startActivity(intentAdmin);
					break;

				case 1:
					G.CLIENT_USERNAME = loginUserName.getText().toString();
					G.CLIENT_PASSWORD = loginPassword.getText().toString();
					try {
						G.CLIENT_ADMIN_EMAIL = result.getJSONObject(0)
								.getString("aemail");
						G.CLIENT_CLIENT_EMAIL = result.getJSONObject(0)
								.getString("cemail");
						G.CLIENT_PASS_EMAIL = result.getJSONObject(0)
								.getString("cpass");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					Intent intentClient = new Intent(getActivity(),
							MainForClient.class);
					startActivity(intentClient);
					break;

				default:
					break;
				}

				loginUserName.setText("");
				loginUserName.requestFocus();
				loginPassword.setText("");

			}
			// else if (result != null && result.length() <= 0) {
			// if (!G.CHECK_IF_EXPIRED) {
			// SCIOMain.instance().displayToast(
			// "Username and Password does not exist!");
			// } else {
			// LifeSave.instance()
			// .displayToast(
			// "Account has expired, Please try avail with no expiration.");
			// SCIOMain.instance()
			// .showDialog("Alert",
			// "Your account has expired, Please get the version with no expiration.");
			// }
			// } else {
			// if (!G.CHECK_IF_EXPIRED) {
			// SCIOMain.instance()
			// .displayToast(
			// "Unable to connect this time, Please try again later.");
			// } else {
			// LifeSave.instance()
			// .displayToast(
			// "Account has expired, Please try avail with no expiration.");
			// SCIOMain.instance()
			// .showDialog("Alert",
			// "Your account has expired, Please get the version with no expiration.");
			// // }
			// }

		}

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		G.POPUP_EMAIL = "";
		G.POPUP_PASS = "";
		G.POPUP_HOST = "";

		G.CHECK_CLIENT_EMAIL = false;
		G.CHECK_CLIENT_PASS = false;
	}

	private void showPopupForgot() {

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		popup = new PopupWindow(inflater.inflate(R.layout.popup_forgotpass,
				null, false), LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);

		popup.setAnimationStyle(Animation.START_ON_FIRST_FRAME);

		popup.setBackgroundDrawable(getActivity().getResources().getDrawable(
				R.drawable.bg_dialog));

		final View pvu = popup.getContentView();

		pvu.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					popup.dismiss();

				}
				return false;
			}
		});

		final ImageView emailImg = (ImageView) pvu
				.findViewById(R.id.forgotEmailImgID);
		final EditText forgotUsername = (EditText) pvu
				.findViewById(R.id.forgotUsernameID);
		TextView forgotUsernameCheckBtn = (TextView) pvu
				.findViewById(R.id.forgotUsernameCheckBtnID);
		final TextView forgotPersonalEmail = (TextView) pvu
				.findViewById(R.id.forgotPersonalEmailID);
		final ImageView icForgotImgEmail = (ImageView) pvu
				.findViewById(R.id.icForgotImgEmailID);
		final ImageView icForgotImgPassword = (ImageView) pvu
				.findViewById(R.id.icForgotImgPasswordID);

		forgotUsernameCheckBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				closeKeyboard(pvu);

				if (!StringUtil.isNullOrEmpty(forgotUsername.getText()
						.toString())) {

					new FetchEmail(forgotUsername.getText().toString(),
							forgotPersonalEmail, emailImg, icForgotImgEmail)
							.execute();

					// forgotPersonalEmail.setText("eduardo.delito@gmail.com");

				} else {
					SCIOMain.instance().displayToast(
							"Username cannot be empty.");
				}
			}
		});

		icForgotImgEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (new EmailValidator().validate(forgotPersonalEmail.getText()
						.toString()) == false) {
					SCIOMain.instance()
							.displayToast(
									"Please click check button to get your personal email.");
				}
			}
		});

		icForgotImgPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SCIOMain.instance().displayToast("Password cannot be empty.");
			}
		});

		final EditText forgotPassword = (EditText) pvu
				.findViewById(R.id.forgotPasswordID);
		FieldTextUtil.popPassTextWatcher(forgotPassword, icForgotImgPassword);

		TextView cancelBtn = (TextView) pvu.findViewById(R.id.forgotCancelID);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				popup.dismiss();
			}
		});

		TextView sendBtn = (TextView) pvu.findViewById(R.id.forgotSendID);
		sendBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				closeKeyboard(pvu);

				if (StringUtil.isNullOrEmpty(forgotUsername.getText()
						.toString())
						|| new EmailValidator().validate(forgotPersonalEmail
								.getText().toString()) == false
						|| StringUtil.isNullOrEmpty(forgotPassword.getText()
								.toString())) {
					SCIOMain.instance().displayToast("Please complete fields.");
				} else {

					popup.dismiss();
					String host = "";
					if (forgotPersonalEmail.getText().toString()
							.contains("gmail")) {
						host = "smtp.gmail.com";
					} else if (forgotPersonalEmail.getText().toString()
							.contains("yahoo")) {
						host = "smtp.mail.yahoo.com";
					}

					mail = new Mail(forgotPersonalEmail.getText().toString(),
							forgotPassword.getText().toString(), host);

					StringBuilder sb = new StringBuilder();
					sb.append("Username : " + G.FORGOT_USERNAME + "\n");
					sb.append("Password : " + G.FORGOT_PASSWORD);

					new SendEmail(forgotPersonalEmail.getText().toString(), sb
							.toString()).execute();
				}
			}
		});

		getActivity().findViewById(R.id.frameFragment).post(new Runnable() {
			public void run() {
				popup.showAtLocation(
						getActivity().findViewById(R.id.frameFragment),
						Gravity.CENTER, 0, 0);
			}
		});

	}

	private class FetchEmail extends AsyncTask<Void, Void, JSONArray> {

		private String mUsername;
		private TextView mEmail;
		private ImageView mIcEmailLogo;
		private ImageView mIcEmailBtn;

		public FetchEmail(String username, TextView email,
				ImageView icEmailLogo, ImageView icEmailBtn) {
			this.mUsername = username;
			this.mEmail = email;
			this.mIcEmailLogo = icEmailLogo;
			this.mIcEmailBtn = icEmailBtn;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected JSONArray doInBackground(Void... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			String jsonStr = sh.makeServiceCall(G.URL + G.ADMIN
					+ G.FINDBY_USERNAME + mUsername, ServiceHandler.GET);
			JSONArray jArray = null;

			if (!StringUtil.isNullOrEmpty(jsonStr)) {
				try {
					jArray = new JSONArray(jsonStr);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {

			}

			System.out.println("jsonStr===========" + jsonStr);

			// Making a request to url and getting response

			return jArray;
		}

		@Override
		protected void onPostExecute(JSONArray result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */

			if (result != null && result.length() > 0) {
				try {
					mEmail.setText(result.getJSONObject(0).getString("email"));
					G.FORGOT_PASSWORD = result.getJSONObject(0).getString(
							"password");
					G.FORGOT_USERNAME = result.getJSONObject(0).getString(
							"username");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (new EmailValidator().validate(mEmail.getText().toString()) == true
						&& mEmail.getText().toString().contains("gmail")) {
					mIcEmailLogo.setImageResource(R.drawable.ic_gmail);
					mIcEmailBtn.setImageResource(R.drawable.ic_info_good);
				} else if (new EmailValidator().validate(mEmail.getText()
						.toString()) == true
						&& mEmail.getText().toString().contains("yahoo")) {
					mIcEmailLogo.setImageResource(R.drawable.ic_yahoo);
					mIcEmailBtn.setImageResource(R.drawable.ic_info_good);
				} else {
					mIcEmailBtn.setImageResource(R.drawable.ic_info_bad);
				}

			} else {
				SCIOMain.instance().displayToast("Username not exist!");
			}

		}

	}

	private class SendEmail extends AsyncTask<Void, Void, Boolean> {

		private String mEmail;
		private String mMsg;

		public SendEmail(String email, String msg) {
			this.mEmail = email;
			this.mMsg = msg;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// Showing progress dialog
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Please wait...");
			pDialog.setCancelable(false);
			pDialog.show();

		}

		@Override
		protected Boolean doInBackground(Void... arg0) {

			// Looper.prepare();
			// Creating service handler class instance
			G.POPUP_ALERT_MSG = "";

			boolean result = false;

			String[] toArr = { mEmail }; // This is an array,
											// you can add more
											// emails, just
											// separate them
											// with a coma
			mail.setTo(toArr); // load array to setTo function
			mail.setFrom(mEmail); // who is sending the email
			mail.setSubject("LifeSave Admin Account");
			mail.setBody(mMsg);

			try {
				if (mail.send()) {
					// success
					result = true;

				} else {
					// failure
					result = false;
					G.POPUP_ALERT_MSG = "Sending Failed, Invalid Password! Please try again.";
				}
			} catch (Exception e) {
				// some other problem
				result = false;
				G.POPUP_ALERT_MSG = "Sending Failed, Invalid Password! Please try again.";
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
				SCIOMain.instance().displayToast("Email Sent!");
			} else {
				SCIOMain.instance().displayToast(G.POPUP_ALERT_MSG);
			}
			G.POPUP_ALERT_MSG = "";
			G.FORGOT_PASSWORD = "";
			G.FORGOT_USERNAME = "";
		}

	}

	private void closeKeyboard(View v) {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		// View v = getActivity().getCurrentFocus();
		if (v == null)
			return;
		inputManager.hideSoftInputFromWindow(v.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
