package com.scio.ui;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.scio.model.EmailValidator;
import com.scio.model.G;
import com.scio.model.Mail;
import com.scio.model.ServiceHandler;
import com.scio.util.FieldTextUtil;
import com.scio.util.StringUtil;
import com.scio.util.TimeUtil;

public class RegisterFragment extends Fragment implements OnClickListener {

	private ProgressDialog pDialog;
	private EditText firstName, lastName, userName, email, password, confirm,
			phoneNumber;
	private ImageView icFirstName, icLastName, icUserName, icEmail, icPassword,
			icConfirm, icPhoneNumber;

	private PopupWindow popup;
	private Mail mail;

//	private String[] state = { "smtp.gmail.com", "smtp.mail.yahoo.com"};
	
	private TextView alertMsgPopup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.register, container, false);

		TextView titleHeader = (TextView) view
				.findViewById(R.id.titleHeaderTextID);
		titleHeader.setTypeface(G.FONT);
		titleHeader.setText("Register Admin");

		ImageView backBtn = (ImageView) view.findViewById(R.id.backBtnID);
		backBtn.setVisibility(View.VISIBLE);
//		backBtn.setTypeface(G.FONT);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getFragmentManager().popBackStack();
			}
		});

		android.view.Display display1 = ((android.view.WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.7),
				0.1f);

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.5));

		LinearLayout tableRow1 = (LinearLayout) view
				.findViewById(R.id.layoutFirstLayoutID);
		tableRow1.setLayoutParams(lp1);

		LinearLayout tableRow2 = (LinearLayout) view
				.findViewById(R.id.registerSecondLayoutID);
		tableRow2.setLayoutParams(lp2);

		Button submitBtn = (Button) view.findViewById(R.id.submitBtnID);
		submitBtn.setTypeface(G.FONT);
		submitBtn.setEnabled(false);
		submitBtn.setClickable(false);
		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				// sendJson();
				closeKeyboard();

				if (!SCIOMain.instance().haveNetworkConnection()) {
					SCIOMain.instance().displayToast(
							getString(R.string.error_network_unreachable));
					return;
				}
				new CreateAdmin().execute();
			}
		});

		icFirstName = (ImageView) view.findViewById(R.id.icFirstnameID);
		icFirstName.setVisibility(View.VISIBLE);
		icFirstName.setOnClickListener(this);

		icLastName = (ImageView) view.findViewById(R.id.icLastnameID);
		icLastName.setVisibility(View.VISIBLE);
		icLastName.setOnClickListener(this);

		icUserName = (ImageView) view.findViewById(R.id.icUsernameID);
		icUserName.setVisibility(View.VISIBLE);
		icUserName.setOnClickListener(this);

		icEmail = (ImageView) view.findViewById(R.id.icEmailID);
		icEmail.setVisibility(View.VISIBLE);
		icEmail.setOnClickListener(this);

		icPassword = (ImageView) view.findViewById(R.id.icPasswordID);
		icPassword.setVisibility(View.VISIBLE);
		icPassword.setOnClickListener(this);

		icConfirm = (ImageView) view.findViewById(R.id.icConfirmID);
		icConfirm.setVisibility(View.VISIBLE);
		icConfirm.setOnClickListener(this);

		icPhoneNumber = (ImageView) view.findViewById(R.id.icPhonenumberID);
		icPhoneNumber.setVisibility(View.VISIBLE);
		icPhoneNumber.setOnClickListener(this);

		firstName = (EditText) view.findViewById(R.id.firstNameFieldID);
		FieldTextUtil.checkFirstName(firstName, icFirstName, submitBtn);

		lastName = (EditText) view.findViewById(R.id.lastNameFieldID);
		FieldTextUtil.checkLastName(lastName, icLastName, submitBtn);

		userName = (EditText) view.findViewById(R.id.userNameFieldID);
		FieldTextUtil.checkUsername(userName, icUserName, submitBtn);

		email = (EditText) view.findViewById(R.id.emailFieldID);
		email.setKeyListener(null);
		email.setOnClickListener(this);

		// FieldTextUtil.checkEmail(email, icEmail, submitBtn);

		password = (EditText) view.findViewById(R.id.passwordFieldID);
		confirm = (EditText) view.findViewById(R.id.confirmFieldID);
		
		FieldTextUtil.checkPassword(password, confirm ,icPassword, icConfirm, submitBtn);

		
		FieldTextUtil.checkConfirmPassword(confirm, password, icConfirm,
				submitBtn);

		phoneNumber = (EditText) view.findViewById(R.id.phoneNumberFieldID);
		phoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		FieldTextUtil.checkPhoneNumber(phoneNumber, icPhoneNumber, submitBtn);

		// new RemoveAdminIfExist().execute();
		G.POPUP_ALERT_MSG = "";
		
		G.CHECK_CLIENT_EMAIL= true;
		G.CHECK_CLIENT_PASS = true;

		return view;
	}

	private class CreateAdmin extends AsyncTask<String, Void, String> {

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
		protected String doInBackground(String... arg0) {
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			JSONObject json = new JSONObject();
			Looper.prepare();

			String fNameField = firstName.getText().toString();
			String lNameField = lastName.getText().toString();
			String userNameField = userName.getText().toString();
			String emailField = email.getText().toString();
			String passwordField = password.getText().toString();
			String phoneNumberField = phoneNumber.getText().toString();

			String msgResponse = "";

			String jsonStr = sh.makeServiceCall(G.URL + G.ADMIN
					+ G.FINDBY_USERNAME + userNameField, ServiceHandler.GET);
			
			String expiry = TimeUtil.setExpiryDate();

			if (!checkUsernameIfExist(jsonStr, userNameField)) {

				try {

					json.put(G.EMAIL, emailField);
					json.put(G.FIRSTNAME, fNameField);
					json.put(G.LASTNAME, lNameField);
					json.put(G.PASSWORD, passwordField);
					json.put(G.PHONENUMBER, phoneNumberField);
					json.put(G.USERNAME, userNameField);
					json.put(G.USERTYPE, "Admin");
					json.put(G.VERIFIED, 1);
					json.put(G.EXPIRY, expiry);

					StringEntity se = new StringEntity(json.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/json"));

					if (sh.makeServiceCall(G.URL + G.ADMIN + G.CREATE_ADMIN,
							ServiceHandler.POST, se).equals("true"))
						G.RESPONSE = true;
					else
						G.RESPONSE = false;

				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				msgResponse = "Succesfully Registered!";

			} else {
				msgResponse = "exist";
			}

			return msgResponse;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */
			if (!G.RESPONSE) {
				SCIOMain.instance().displayToast(
						"Registration failed, Please try again!");
			} else {

				if (result.equals("exist")) {
					SCIOMain.instance().displayToast("Username Already exist!");
				} else{
					mail = new Mail(G.POPUP_EMAIL, G.POPUP_PASS, G.POPUP_HOST);;
					new SendEmail().execute();
				}
			}

		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.icFirstnameID:

			if (StringUtil.isNullOrEmpty(firstName.getText().toString()))
				SCIOMain.instance().displayToast("First Name cannot be blank!");

			break;
		case R.id.icLastnameID:

			if (StringUtil.isNullOrEmpty(lastName.getText().toString()))
				SCIOMain.instance().displayToast("Last Name cannot be blank!");

			break;
		case R.id.icUsernameID:

			if (StringUtil.isNullOrEmpty(userName.getText().toString()))
				SCIOMain.instance().displayToast("Username cannot be blank!");
			else if (userName.getText().toString().length() >= 6)
				SCIOMain.instance()
						.displayToast(
								"Username must be greater than or equal to 6 characters!");

			break;
		case R.id.emailFieldID:
			popEmailSetup();
			break;

		case R.id.icEmailID:

			// if (StringUtil.isNullOrEmpty(email.getText().toString()))
			// LifeSave.instance().displayToast("Email Name cannot be blank!");
			// else if (new
			// EmailValidator().validate(email.getText().toString()) == false)
			// LifeSave.instance().displayToast("Invalid email!");
			popEmailSetup();

			break;
		case R.id.icPasswordID:

			if (StringUtil.isNullOrEmpty(password.getText().toString()))
				SCIOMain.instance().displayToast(
						"Password Name cannot be blank!");
			else if (password.getText().toString().length() < 6)
				SCIOMain.instance()
						.displayToast(
								"Password must be greater than or equal to 6 characters!");

			break;
		case R.id.icConfirmID:

			if (StringUtil.isNullOrEmpty(confirm.getText().toString()))
				SCIOMain.instance().displayToast(
						"Confirm Password cannot be blank!");
			else if (!password.getText().toString()
					.equals(confirm.getText().toString()))
				SCIOMain.instance().displayToast(
						"Confirm Password not match to Password!");

			break;
		case R.id.icPhonenumberID:

			if (StringUtil.isNullOrEmpty(phoneNumber.getText().toString()))
				SCIOMain.instance().displayToast(
						"Phone Number cannot be blank!");

			break;

		default:
			break;
		}
	}

	private boolean checkUsernameIfExist(String jsonStr, String username) {

		JSONArray jArray;
		JSONObject jObject = null;
		try {

			jArray = new JSONArray(jsonStr);
			if (jArray != null && jArray.length() > 0) {
				jObject = jArray.getJSONObject(0);
				if (username.equals(jObject.get("username"))) {
					G.RESPONSE = true;
					return true;
				}
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View v = getActivity().getCurrentFocus();
		if (v == null)
			return;
		inputManager.hideSoftInputFromWindow(v.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private class SendEmail extends AsyncTask<Void, Void, Boolean> {

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

			Looper.prepare();
			// Creating service handler class instance

			boolean result = false;

			

			String[] toArr = { G.POPUP_EMAIL }; // This is an array,
												// you can add more
												// emails, just
												// separate them
												// with a coma
			mail.setTo(toArr); // load array to setTo function
			mail.setFrom(G.POPUP_EMAIL); // who is sending the email
			mail.setSubject("LifeSave Registration");

			mail.setBody("Congratulations! Thank you for registering LifeSave.");

			try {
				// m.addAttachment(externamStorage
				// + "/DCIM/Camera/20140404_094540.jpg"); // path to file
				// // you want to
				// // attach
				if (mail.send()) {
					// success
					result = true;
//					DBRepository.insertExpiry(G.EXPIRY_TABLE, userName.getText()
//							.toString(), password.getText().toString(), "Admin",
//							TimeUtil.getTime(), TimeUtil.setExpiryDate());
				} else {
					// failure
					result = false;
					G.POPUP_ALERT_MSG = "Invalid Email or Password! Please try again.";
				}
			} catch (Exception e) {
				// some other problem
				result = false;
				G.POPUP_ALERT_MSG = "Invalid Email or Password! Please try again.";
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
				G.POPUP_ALERT_MSG = "";
				SCIOMain.instance().displayToast("Succesfully Registered!");
				getFragmentManager().popBackStack();
			} else {
				icEmail.setImageResource(R.drawable.ic_info_bad);
				G.CHECK_EMAIL = false;
				new RemoveAdminIfExist().execute();
			}
		}

	}

	private void popEmailSetup() {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		popup = new PopupWindow(inflater.inflate(R.layout.popup, null, false),
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);

		popup.setAnimationStyle(Animation.START_ON_FIRST_FRAME);

		popup.setBackgroundDrawable(getActivity().getResources().getDrawable(
				R.drawable.bg_dialog));

		final View pvu = popup.getContentView();

		pvu.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					popup.dismiss();
					G.POPUP_EMAIL = "";
					G.POPUP_PASS = "";
					G.POPUP_HOST = "";
				}
				return false;
			}
		});

//		final Spinner hostSpin = (Spinner) pvu.findViewById(R.id.hostSpinID);
		
		final ImageView icEmailImage = (ImageView) pvu.findViewById(R.id.emailImageID);

//		ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(
//				getActivity(), android.R.layout.simple_spinner_item, state);
//		adapter_state
//				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		hostSpin.setAdapter(adapter_state);
//		hostSpin.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int position, long arg3) {
//				// TODO Auto-generated method stub
//				if(position == 0){
//					icEmailImage.setImageResource(R.drawable.ic_gmail);
//				}else{
//					icEmailImage.setImageResource(R.drawable.ic_yahoo);
//				}
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//				// TODO Auto-generated method stub
//
//			}
//		});

		ImageView icPopEmail = (ImageView) pvu
				.findViewById(R.id.icPopupEmailID);
		ImageView icPopPass = (ImageView) pvu.findViewById(R.id.icPopupPassID);
		
		

		final EditText emailPopup = (EditText) pvu
				.findViewById(R.id.emailPopupID);
		emailPopup.setText(email.getText().toString());
		emailPopup.setSelection(emailPopup.getText().toString().length());
		FieldTextUtil.popEmailTextWatcher(emailPopup, icPopEmail, icEmailImage);

		final EditText passPopup = (EditText) pvu
				.findViewById(R.id.passPopupID);
		passPopup.setText(G.POPUP_PASS);
		FieldTextUtil.popPassTextWatcher(passPopup, icPopPass);

		if (new EmailValidator().validate(emailPopup.getText().toString()) == true) {
			icPopEmail.setImageResource(R.drawable.ic_info_good);
		}

		if (!StringUtil.isNullOrEmpty(passPopup.getText().toString())) {
			icPopPass.setImageResource(R.drawable.ic_info_good);
		}
		
		
		alertMsgPopup = (TextView) pvu.findViewById(R.id.alertMsgPopupID);
		alertMsgPopup.setText(G.POPUP_ALERT_MSG);

		Button okBtn = (Button) pvu.findViewById(R.id.okEmailBtnID);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if (emailPopup.getText().toString().contains("gmail")) {
//					hostSpin.setSelection(0);
					G.POPUP_HOST = "smtp.gmail.com";
				} else if (emailPopup.getText().toString().contains("yahoo")) {
//					hostSpin.setSelection(1);
					G.POPUP_HOST = "smtp.mail.yahoo.com";
				} 

				popup.dismiss();
				G.POPUP_EMAIL = emailPopup.getText().toString();
				G.POPUP_PASS = passPopup.getText().toString();
//				G.POPUP_HOST = hostSpin.getSelectedItem().toString();
				email.setText(G.POPUP_EMAIL);

				if (new EmailValidator().validate(emailPopup.getText()
						.toString()) == true
						&& !StringUtil.isNullOrEmpty(passPopup.getText()
								.toString())) {
					icEmail.setImageResource(R.drawable.ic_info_good);
					G.CHECK_EMAIL = true;
				} else {
					icEmail.setImageResource(R.drawable.ic_info_bad);
					G.CHECK_EMAIL = false;
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

	private class RemoveAdminIfExist extends AsyncTask<Void, Void, Boolean> {

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
			// Creating service handler class instance
			ServiceHandler sh = new ServiceHandler();

			boolean result = false;

			// Making a request to url and getting response
			String username = userName.getText().toString();

			String jsonStr = sh.makeServiceCall(G.URL + G.ADMIN
					+ G.FINDBY_USERNAME + username, ServiceHandler.GET);

			JSONArray jArray = null;

			if (!StringUtil.isNullOrEmpty(jsonStr)) {
				try {
					jArray = new JSONArray(jsonStr);
					int id = jArray.getJSONObject(0).getInt("id");
					String remove = sh.deleteByID(G.URL + G.ADMIN
							+ G.DELETE_ADMIN + id);

					System.out.println("remove============" + remove);

					if (remove.equals("OK")) {
						result = true;
					} else {
						result = false;
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			return result;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */

			if (result) {
				SCIOMain.instance().displayToast(
						"Registration failed, Please try again!");
			} else {
				new RemoveAdminIfExist().execute();
			}

		}

	}

}
