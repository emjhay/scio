package com.scio.tab.add;

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
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.scio.model.EmailValidator;
import com.scio.model.G;
import com.scio.model.ServiceHandler;
import com.scio.ui.BaseFragment;
import com.scio.ui.R;
import com.scio.util.FieldTextUtil;
import com.scio.util.StringUtil;
import com.scio.util.TimeUtil;

public class AddUsers extends BaseFragment implements OnClickListener {

	private CheckBox activateLocation, deactivateLocation, activateSms,
			deactivateSms, activateCall, deactivateCall;
	private RadioGroup radioGroup1, radioGroup2, radioGroup3;
	private ProgressDialog pDialog;
	// CheckBox[] chkArray = new CheckBox[2];
	private Button addbtn;
	private int locationStatus, smsStatus, callStatus;

	private EditText firstName, lastName, userName, email, clientEmail,
			clientPass, password, confirmPass, cphoneNumber, aphoneNumber;

	private ImageView icFirstName, icLastName, icUserName, icEmail,
			icClientEmail, icClientPass, icPassword, icConfirm, iccPhoneNumber, icaPhoneNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_users, container, false);

		TextView titleHeader = (TextView) view
				.findViewById(R.id.titleHeaderTextID);
		titleHeader.setTypeface(G.FONT);
		titleHeader.setText("Add Client");

		android.view.Display display1 = ((android.view.WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.85),
				0.1f);

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 1));

		LinearLayout.LayoutParams lp3 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.35),
				3);

		LinearLayout tableRow1 = (LinearLayout) view
				.findViewById(R.id.layoutFirstLayoutID);
		tableRow1.setLayoutParams(lp1);

		LinearLayout tableRow2 = (LinearLayout) view
				.findViewById(R.id.layoutSecondLayoutID);
		tableRow2.setLayoutParams(lp2);

		LinearLayout btnLayout = (LinearLayout) view
				.findViewById(R.id.btnLayoutID);
		btnLayout.setLayoutParams(lp3);

		radioGroup1 = (RadioGroup) view.findViewById(R.id.radioGroup1);

		radioGroup2 = (RadioGroup) view.findViewById(R.id.radioGroup2);

		radioGroup3 = (RadioGroup) view.findViewById(R.id.radioGroup3);

		activateLocation = (CheckBox) view
				.findViewById(R.id.activateLocationID);
		activateLocation.setOnClickListener(this);

		deactivateLocation = (CheckBox) view
				.findViewById(R.id.deactivateLocationID);
		deactivateLocation.setOnClickListener(this);

		activateSms = (CheckBox) view.findViewById(R.id.activateSmsID);
		activateSms.setOnClickListener(this);

		deactivateSms = (CheckBox) view.findViewById(R.id.deactivateSmsID);
		deactivateSms.setOnClickListener(this);

		activateCall = (CheckBox) view.findViewById(R.id.activateCallID);
		activateCall.setOnClickListener(this);

		deactivateCall = (CheckBox) view.findViewById(R.id.deactivateCallID);
		deactivateCall.setOnClickListener(this);

		icFirstName = (ImageView) view.findViewById(R.id.icFirstnameID);
		icFirstName.setOnClickListener(this);

		icLastName = (ImageView) view.findViewById(R.id.icLastnameID);
		icLastName.setOnClickListener(this);

		icUserName = (ImageView) view.findViewById(R.id.icUsernameID);
		icUserName.setOnClickListener(this);

		icEmail = (ImageView) view.findViewById(R.id.icEmailID);
		icEmail.setOnClickListener(this);

		icClientEmail = (ImageView) view.findViewById(R.id.icClientEmailID);
		icClientEmail.setOnClickListener(this);

		icClientPass = (ImageView) view.findViewById(R.id.icClientPasswordID);
		icClientPass.setOnClickListener(this);

		icPassword = (ImageView) view.findViewById(R.id.icPasswordID);
		icPassword.setOnClickListener(this);

		icConfirm = (ImageView) view.findViewById(R.id.icConfirmID);
		icConfirm.setOnClickListener(this);

		iccPhoneNumber = (ImageView) view.findViewById(R.id.icPhonenumberID);
		iccPhoneNumber.setOnClickListener(this);
		
		icaPhoneNumber = (ImageView) view.findViewById(R.id.icAdminnumberID);
		icaPhoneNumber.setOnClickListener(this);

		addbtn = (Button) view.findViewById(R.id.addUsersBtnID);
		addbtn.setTypeface(G.FONT);
		addbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				closeKeyboard();

				if (mActivity.haveNetworkConnection())
					new AddClient().execute();
				else
					mActivity
							.toast(getString(R.string.error_network_unreachable));
			}
		});

		firstName = (EditText) view.findViewById(R.id.firstNameFieldID);
		FieldTextUtil.checkFirstName(firstName, icFirstName, addbtn);

		lastName = (EditText) view.findViewById(R.id.lastNameFieldID);
		FieldTextUtil.checkLastName(lastName, icLastName, addbtn);

		userName = (EditText) view.findViewById(R.id.userNameFieldID);
		FieldTextUtil.checkUsername(userName, icUserName, addbtn);

		email = (EditText) view.findViewById(R.id.emailFieldID);
		email.setText(G.ADMIN_EMAIL);
		email.setKeyListener(null);
		icEmail.setVisibility(View.VISIBLE);
		icEmail.setImageResource(R.drawable.ic_info_good);
		G.CHECK_EMAIL = true;
		// FieldTextUtil.checkEmail(email, icEmail, addbtn);

		clientEmail = (EditText) view.findViewById(R.id.clientEmailID);
		FieldTextUtil.clientCheckEmail(clientEmail, icClientEmail, addbtn);

		clientPass = (EditText) view.findViewById(R.id.clientPasswordID);
		FieldTextUtil.clientCheckPass(clientPass, icClientPass, addbtn);

		password = (EditText) view.findViewById(R.id.passwordFieldID);
		confirmPass = (EditText) view.findViewById(R.id.confirmFieldID);
		
		FieldTextUtil.checkPassword(password, confirmPass, icPassword, icConfirm, addbtn);

		
		FieldTextUtil.checkConfirmPassword(confirmPass, password, icConfirm,
				addbtn);

		cphoneNumber = (EditText) view.findViewById(R.id.phoneNumberFieldID);
		cphoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		FieldTextUtil.checkPhoneNumber(cphoneNumber, iccPhoneNumber, addbtn);
		
		aphoneNumber = (EditText) view.findViewById(R.id.adminNumberFieldID);
		aphoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		FieldTextUtil.checkPhoneNumber(aphoneNumber, icaPhoneNumber, addbtn);

		return view;
	}

	private int getSelectedHour(int id) {

		int value = 0;

		switch (id) {
		case R.id.radio0:
			value = 2;
			break;
		case R.id.radio1:
			value = 4;
			break;
		case R.id.radio2:
			value = 6;
			break;
		case R.id.radio3:
			value = 8;
			break;

		default:
			break;
		}

		return value;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.activateLocationID:
			activateLocation.setChecked(true);
			deactivateLocation.setChecked(false);
			locationStatus = 1;
			break;

		case R.id.deactivateLocationID:
			activateLocation.setChecked(false);
			deactivateLocation.setChecked(true);
			locationStatus = 0;
			break;

		case R.id.activateSmsID:
			activateSms.setChecked(true);
			deactivateSms.setChecked(false);
			smsStatus = 1;
			break;

		case R.id.deactivateSmsID:
			activateSms.setChecked(false);
			deactivateSms.setChecked(true);
			smsStatus = 0;
			break;

		case R.id.activateCallID:
			activateCall.setChecked(true);
			deactivateCall.setChecked(false);
			callStatus = 1;
			break;

		case R.id.deactivateCallID:
			activateCall.setChecked(false);
			deactivateCall.setChecked(true);
			callStatus = 0;
			break;

		case R.id.icFirstnameID:

			if (StringUtil.isNullOrEmpty(firstName.getText().toString()))
				mActivity.toast("First Name cannot be blank!");

			break;
		case R.id.icLastnameID:

			if (StringUtil.isNullOrEmpty(lastName.getText().toString()))
				mActivity.toast("Last Name cannot be blank!");

			break;
		case R.id.icUsernameID:

			if (StringUtil.isNullOrEmpty(userName.getText().toString()))
				mActivity.toast("Username cannot be blank!");
			else if (userName.getText().toString().length() >= 6)
				mActivity
						.toast("Username must be greater than or equal to 6 characters!");

			break;
		case R.id.icEmailID:

			if (StringUtil.isNullOrEmpty(email.getText().toString()))
				mActivity.toast("Admin email cannot be blank!");
			else if (new EmailValidator().validate(email.getText().toString()) == false)
				mActivity.toast("Invalid admin email!");

			break;

		case R.id.icClientEmailID:

			System.out.println("=================");

			if (StringUtil.isNullOrEmpty(clientEmail.getText().toString()))
				mActivity.toast("Client email cannot be blank!");
			else if (new EmailValidator().validate(email.getText().toString()) == false)
				mActivity.toast("Invalid client email!");

			break;

		case R.id.icClientPasswordID:

			if (StringUtil.isNullOrEmpty(clientPass.getText().toString()))
				mActivity.toast("Client email password cannot be blank!");

			break;
		case R.id.icPasswordID:

			if (StringUtil.isNullOrEmpty(password.getText().toString()))
				mActivity.toast("Password cannot be blank!");
			else if (password.getText().toString().length() < 6)
				mActivity
						.toast("Password must be greater than or equal to 6 characters!");

			break;
		case R.id.icConfirmID:

			if (StringUtil.isNullOrEmpty(confirmPass.getText().toString()))
				mActivity.toast("Confirm Password cannot be blank!");
			else if (!password.getText().toString()
					.equals(confirmPass.getText().toString()))
				mActivity.toast("Confirm Password not match to Password!");

			break;
		case R.id.icPhonenumberID:

			if (StringUtil.isNullOrEmpty(cphoneNumber.getText().toString()))
				mActivity.toast("Phone Number cannot be blank!");

			break;
			
		case R.id.icAdminnumberID:

			if (StringUtil.isNullOrEmpty(aphoneNumber.getText().toString()))
				mActivity.toast("Admin Number cannot be blank!");

			break;

		default:
			break;
		}

	}

	private class AddClient extends AsyncTask<String, Void, String> {

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
			String clientEmailField = clientEmail.getText().toString();
			String clientEmailPassField = clientPass.getText().toString();
			String passwordField = password.getText().toString();
			
			String phoneNumberField = cphoneNumber.getText().toString();
			
			String adminNumberField = aphoneNumber.getText().toString();
			
			int locationInterval = getSelectedHour(radioGroup1
					.getCheckedRadioButtonId());
			int smsInterval = getSelectedHour(radioGroup2
					.getCheckedRadioButtonId());

			int callInterval = getSelectedHour(radioGroup3
					.getCheckedRadioButtonId());

			String msgResponse = "";

			String jsonStr = sh.makeServiceCall(G.URL + G.ADMIN
					+ G.FINDBY_USERNAME + userNameField, ServiceHandler.GET);
			
			String expiry = TimeUtil.setExpiryDate();

			if (!checkUsernameIfExist(jsonStr, userNameField)) {

				try {

					json.put(G.ADMIN_ADMIN, G.ADMIN_USERNAME);
					json.put(G.AEMAIL, emailField);
					json.put(G.CEMAIL, clientEmailField);
					json.put(G.CPASS, clientEmailPassField);
					json.put(G.FIRSTNAME, fNameField);
					json.put(G.LASTNAME, lNameField);
					json.put(G.CPHONENUMBER, phoneNumberField);
					json.put(G.APHONENUMBER, adminNumberField);
					json.put(G.USERNAME, userNameField);
					json.put(G.PASSWORD, passwordField);
					json.put(G.LOCATION_INTERVAL, locationInterval);
					json.put(G.SMS_INTERVAL, smsInterval);
					json.put(G.CALL_INTERVAL, callInterval);
					json.put(G.USERTYPE, "Client");
					json.put(G.LOCATION_STATUS, locationStatus);
					json.put(G.SMS_STATUS, smsStatus);
					json.put(G.CALL_STATUS, callStatus);
					json.put(G.EXPIRY, expiry);

					StringEntity se = new StringEntity(json.toString());
					se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
							"application/json"));

					if (sh.makeServiceCall(G.URL + G.CLIENT + G.CREATE_CLIENT,
							ServiceHandler.POST, se).equals("true")) {
						G.RESPONSE = true;
//						DBRepository.insertExpiry(G.EXPIRY_TABLE, userName
//								.getText().toString(), password.getText()
//								.toString(), "Client", TimeUtil.getTime(),
//								TimeUtil.setExpiryDate());
					} else
						G.RESPONSE = false;

				} catch (Exception ex) {
					// TODO Auto-generated catch block
					ex.printStackTrace();
				}

				msgResponse = "Succesfully added " + userNameField
						+ " as Client.";

			} else {
				msgResponse = "Username already exist!";
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
			if (!G.RESPONSE)
				mActivity.toast("Failed to add Client, Please try again!");
			else {
				mActivity.toast(result);
				resetFields();
			}

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

	private void resetFields() {
		firstName.setText("");
		lastName.setText("");
		userName.setText("");
		email.setText("");
		password.setText("");
		confirmPass.setText("");
		cphoneNumber.setText("");
		aphoneNumber.setText("");

		radioGroup1.check(radioGroup1.getChildAt(0).getId());
		radioGroup2.check(radioGroup2.getChildAt(0).getId());
		radioGroup3.check(radioGroup3.getChildAt(0).getId());

		activateLocation.setChecked(false);
		deactivateLocation.setChecked(true);

		activateSms.setChecked(false);
		deactivateSms.setChecked(true);

		activateCall.setChecked(false);
		deactivateCall.setChecked(true);

		locationStatus = 0;
		smsStatus = 0;
		callStatus = 0;
//		status = 0;

	}

	private void closeKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) mActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		// check if no view has focus:
		View v = mActivity.getCurrentFocus();
		if (v == null)
			return;
		inputManager.hideSoftInputFromWindow(v.getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

}