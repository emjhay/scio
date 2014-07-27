package com.scio.tab.check;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
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

public class EditClient extends BaseFragment implements OnClickListener {

	private EditText firstName, lastName, userName, email, clientEmail,
			clientPass, password, confirmPass, cPhoneNumber, aPhoneNumber;

	private ImageView icFirstName, icLastName, icUserName, icEmail,
			icClientEmail, icClientPass, icPassword, icConfirm, iccPhoneNumber,
			icaPhoneNumber;

	private RadioGroup radioGroup1, radioGroup2, radioGroup3;

	private CheckBox activateLocation, deactivateLocation, activateSms,
			deactivateSms, activateCall, deactivateCall;

	private Button addbtn;

	// private int locationStatus, smsStatus, callStatus, status = 0;

	private ProgressDialog pDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.add_users, container, false);

		TextView title = (TextView) view.findViewById(R.id.titleHeaderTextID);
		title.setVisibility(View.VISIBLE);
		title.setTypeface(G.FONT);
		title.setText("Edit Client");

		android.view.Display display1 = ((android.view.WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.9),
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
		setRadioBtn(radioGroup1, G.CLIENT_LOCATION_INTERVAL);

		radioGroup2 = (RadioGroup) view.findViewById(R.id.radioGroup2);
		setRadioBtn(radioGroup2, G.CLIENT_SMS_INTERVAL);

		radioGroup3 = (RadioGroup) view.findViewById(R.id.radioGroup3);
		setRadioBtn(radioGroup3, G.CLIENT_CALL_INTERVAL);

		activateLocation = (CheckBox) view
				.findViewById(R.id.activateLocationID);
		activateLocation.setOnClickListener(this);

		deactivateLocation = (CheckBox) view
				.findViewById(R.id.deactivateLocationID);
		deactivateLocation.setOnClickListener(this);

		setCheckOnStatus(activateLocation, deactivateLocation,
				G.CLIENT_LOCATION_STATUS);

		activateSms = (CheckBox) view.findViewById(R.id.activateSmsID);
		activateSms.setOnClickListener(this);

		deactivateSms = (CheckBox) view.findViewById(R.id.deactivateSmsID);
		deactivateSms.setOnClickListener(this);

		setCheckOnStatus(activateSms, deactivateSms, G.CLIENT_SMS_STATUS);

		activateCall = (CheckBox) view.findViewById(R.id.activateCallID);
		activateCall.setOnClickListener(this);

		deactivateCall = (CheckBox) view.findViewById(R.id.deactivateCallID);
		deactivateCall.setOnClickListener(this);

		setCheckOnStatus(activateCall, deactivateCall, G.CLIENT_CALL_STATUS);

		icFirstName = (ImageView) view.findViewById(R.id.icFirstnameID);
		icFirstName.setOnClickListener(this);

		icLastName = (ImageView) view.findViewById(R.id.icLastnameID);
		icLastName.setOnClickListener(this);

		icUserName = (ImageView) view.findViewById(R.id.icUsernameID);
		// icUserName.setOnClickListener(this);

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
		addbtn.setText("Update");
		addbtn.setTypeface(G.FONT);
		addbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				closeKeyboard();

				if (mActivity.haveNetworkConnection())
					new UpdateClient().execute();
				else
					mActivity
							.toast(getString(R.string.error_network_unreachable));
			}
		});

		firstName = (EditText) view.findViewById(R.id.firstNameFieldID);
		firstName.setText(G.CLIENT_FIRSTNAME);
		icFirstName.setVisibility(View.VISIBLE);
		icFirstName.setImageResource(R.drawable.ic_info_good);
		G.CHECK_FIRSTNAME = true;
		firstName.setSelection(firstName.getText().toString().length());
		FieldTextUtil.checkFirstName(firstName, icFirstName, addbtn);

		lastName = (EditText) view.findViewById(R.id.lastNameFieldID);
		lastName.setText(G.CLIENT_LASTNAME);
		icLastName.setVisibility(View.VISIBLE);
		icLastName.setImageResource(R.drawable.ic_info_good);
		G.CHECK_LASTNAME = true;
		FieldTextUtil.checkLastName(lastName, icLastName, addbtn);

		userName = (EditText) view.findViewById(R.id.userNameFieldID);
		userName.setKeyListener(null);
		userName.setText(G.CLIENT_USERNAME);
		// FieldTextUtil.checkUsername(userName, icUserName, addbtn);
		icUserName.setVisibility(View.VISIBLE);
		icUserName.setImageResource(R.drawable.ic_info_good);
		G.CHECK_USERNAME = true;

		email = (EditText) view.findViewById(R.id.emailFieldID);
		email.setText(G.CLIENT_ADMIN_EMAIL);
		email.setKeyListener(null);
		icEmail.setVisibility(View.VISIBLE);
		icEmail.setImageResource(R.drawable.ic_info_good);
		G.CHECK_EMAIL = true;
		FieldTextUtil.checkEmail(email, icEmail, addbtn);

		clientEmail = (EditText) view.findViewById(R.id.clientEmailID);
		clientEmail.setText(G.CLIENT_CLIENT_EMAIL);
		icClientEmail.setImageResource(R.drawable.ic_info_good);
		FieldTextUtil.clientCheckEmail(clientEmail, icClientEmail, addbtn);
		G.CHECK_CLIENT_EMAIL = true;

		clientPass = (EditText) view.findViewById(R.id.clientPasswordID);
		clientPass.setText(G.CLIENT_PASS_EMAIL);
		icClientPass.setImageResource(R.drawable.ic_info_good);
		FieldTextUtil.clientCheckPass(clientPass, icClientPass, addbtn);
		G.CHECK_CLIENT_PASS = true;

		password = (EditText) view.findViewById(R.id.passwordFieldID);
		confirmPass = (EditText) view.findViewById(R.id.confirmFieldID);

		password.setText(G.CLIENT_PASSWORD);
		icPassword.setVisibility(View.VISIBLE);
		icPassword.setImageResource(R.drawable.ic_info_good);
		G.CHECK_PASSWORD = true;
		password.setInputType(InputType.TYPE_CLASS_TEXT);
		FieldTextUtil.checkPassword(password, confirmPass, icPassword,
				icConfirm, addbtn);

		confirmPass.setText(G.CLIENT_PASSWORD);
		icConfirm.setVisibility(View.VISIBLE);
		icConfirm.setImageResource(R.drawable.ic_info_good);
		G.CHECK_CONFIRM = true;
		FieldTextUtil.checkConfirmPassword(confirmPass, password, icConfirm,
				addbtn);

		cPhoneNumber = (EditText) view.findViewById(R.id.phoneNumberFieldID);
		cPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		cPhoneNumber.setText(G.CLIENT_PHONENUMBER + "");
		iccPhoneNumber.setVisibility(View.VISIBLE);
		iccPhoneNumber.setImageResource(R.drawable.ic_info_good);
		G.CHECK_PHONENUMBER = true;
		FieldTextUtil.checkPhoneNumber(cPhoneNumber, iccPhoneNumber, addbtn);

		aPhoneNumber = (EditText) view.findViewById(R.id.adminNumberFieldID);
		aPhoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		aPhoneNumber.setText(G.ADMIN_PHONENUMBER + "");
		icaPhoneNumber.setVisibility(View.VISIBLE);
		icaPhoneNumber.setImageResource(R.drawable.ic_info_good);
		G.CHECK_ADMIN_PHONENUMBER = true;
		FieldTextUtil.checkPhoneNumber(aPhoneNumber, icaPhoneNumber, addbtn);

		return view;
	}

	private void setRadioBtn(RadioGroup radioGroup, int interval) {

		switch (interval) {
		case 2:
			radioGroup.check(radioGroup.getChildAt(0).getId());
			break;
		case 4:
			radioGroup.check(radioGroup.getChildAt(1).getId());
			break;
		case 6:
			radioGroup.check(radioGroup.getChildAt(2).getId());
			break;
		case 8:
			radioGroup.check(radioGroup.getChildAt(3).getId());
			break;

		default:
			break;
		}

	}

	private void setCheckOnStatus(CheckBox activate, CheckBox deactivate,
			int status) {
		switch (status) {
		case 0:
			activate.setChecked(false);
			deactivate.setChecked(true);
			break;

		case 1:
			activate.setChecked(true);
			deactivate.setChecked(false);
			break;

		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.activateLocationID:
			activateLocation.setChecked(true);
			deactivateLocation.setChecked(false);
			G.CLIENT_LOCATION_STATUS = 1;
			break;

		case R.id.deactivateLocationID:
			activateLocation.setChecked(false);
			deactivateLocation.setChecked(true);
			G.CLIENT_LOCATION_STATUS = 0;
			break;

		case R.id.activateSmsID:
			activateSms.setChecked(true);
			deactivateSms.setChecked(false);
			G.CLIENT_SMS_STATUS = 1;
			break;

		case R.id.deactivateSmsID:
			activateSms.setChecked(false);
			deactivateSms.setChecked(true);
			G.CLIENT_SMS_STATUS = 0;
			break;

		case R.id.activateCallID:
			activateCall.setChecked(true);
			deactivateCall.setChecked(false);
			G.CLIENT_CALL_STATUS = 1;
			break;

		case R.id.deactivateCallID:
			activateCall.setChecked(false);
			deactivateCall.setChecked(true);
			G.CLIENT_CALL_STATUS = 0;
			break;

		// case R.id.activatePhotoID:
		// activatePhoto.setChecked(true);
		// deactivatePhoto.setChecked(false);
		// G.CLIENT_STATUS = 1;
		// break;
		//
		// case R.id.deactivatePhotoID:
		// activatePhoto.setChecked(false);
		// deactivatePhoto.setChecked(true);
		// G.CLIENT_STATUS = 0;
		// break;

		case R.id.icFirstnameID:

			if (StringUtil.isNullOrEmpty(firstName.getText().toString()))
				mActivity.toast("First Name cannot be blank!");

			break;
		case R.id.icLastnameID:

			if (StringUtil.isNullOrEmpty(lastName.getText().toString()))
				mActivity.toast("Last Name cannot be blank!");

			break;
		// case R.id.icUsernameID:
		//
		// if (StringUtil.isNullOrEmpty(userName.getText().toString()))
		// mActivity.toast("Username cannot be blank!");
		// else if (userName.getText().toString().length() >= 6)
		// mActivity.toast(
		// "Username must be greater than or equal to 6 characters!");
		//
		// break;
		case R.id.icEmailID:

			if (StringUtil.isNullOrEmpty(email.getText().toString()))
				mActivity.toast("Email Name cannot be blank!");
			else if (new EmailValidator().validate(email.getText().toString()) == false)
				mActivity.toast("Invalid email!");

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
				mActivity.toast("Password Name cannot be blank!");
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

			if (StringUtil.isNullOrEmpty(cPhoneNumber.getText().toString()))
				mActivity.toast("Phone Number cannot be blank!");
			break;

		case R.id.icAdminnumberID:

			if (StringUtil.isNullOrEmpty(aPhoneNumber.getText().toString()))
				mActivity.toast("Phone Number cannot be blank!");
			break;

		default:
			break;
		}

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

	private class UpdateClient extends AsyncTask<String, Void, String> {

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
//			Looper.prepare();


			G.CLIENT_USERNAME = userName.getText().toString();
			G.CLIENT_PASSWORD = password.getText().toString();
			G.CLIENT_FIRSTNAME = firstName.getText().toString();
			G.CLIENT_LASTNAME = lastName.getText().toString();
			G.CLIENT_PHONENUMBER = cPhoneNumber.getText().toString();

			G.ADMIN_PHONENUMBER = aPhoneNumber.getText().toString();

			G.CLIENT_LOCATION_INTERVAL = getSelectedHour(radioGroup1
					.getCheckedRadioButtonId());
			G.CLIENT_SMS_INTERVAL = getSelectedHour(radioGroup2
					.getCheckedRadioButtonId());
			G.CLIENT_CALL_INTERVAL = getSelectedHour(radioGroup3
					.getCheckedRadioButtonId());
			G.CLIENT_ADMIN_EMAIL = email.getText().toString();
			G.CLIENT_CLIENT_EMAIL = clientEmail.getText().toString();
			G.CLIENT_PASS_EMAIL = clientPass.getText().toString();

			String msgResponse = "";

			try {

				json.put(G.USERTYPE, "Client");
				json.put(G.FIRSTNAME, G.CLIENT_FIRSTNAME);
				json.put(G.LASTNAME, G.CLIENT_LASTNAME);
				json.put(G.USERNAME, G.CLIENT_USERNAME);
				json.put(G.PASSWORD, G.CLIENT_PASSWORD);
				json.put(G.AEMAIL, G.CLIENT_ADMIN_EMAIL);
				json.put(G.CEMAIL, G.CLIENT_CLIENT_EMAIL);
				json.put(G.CPASS, G.CLIENT_PASS_EMAIL);
				
				json.put(G.PHONENUMBER, G.CLIENT_PHONENUMBER);

				json.put(G.APHONENUMBER, G.ADMIN_PHONENUMBER);

				json.put(G.LOCATION_INTERVAL, G.CLIENT_LOCATION_INTERVAL);
				json.put(G.SMS_INTERVAL, G.CLIENT_SMS_INTERVAL);
				json.put(G.CALL_INTERVAL, G.CLIENT_CALL_INTERVAL);

				json.put(G.ID, G.CLIENT_ID);
				json.put(G.ADMIN_ADMIN, G.ADMIN_USERNAME);

				json.put(G.LOCATION_STATUS, G.CLIENT_LOCATION_STATUS);
				json.put(G.SMS_STATUS, G.CLIENT_SMS_STATUS);
				json.put(G.CALL_STATUS, G.CLIENT_CALL_STATUS);

				json.put(G.EXPIRY, G.CLIENT_EXPIRY);
				

				StringEntity se = new StringEntity(json.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));
				

				msgResponse = sh.makeServiceCall(G.URL + G.CLIENT
						+ G.EDIT_CLIENT + G.CLIENT_ID + "/",
						ServiceHandler.PUT, se);
				
				System.out.println("msgResponse2=========="+msgResponse);

				if (msgResponse.equals("true"))
					G.RESPONSE = true;
				else
					G.RESPONSE = false;

			} catch (Exception ex) {
				// TODO Auto-generated catch block
				ex.printStackTrace();
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
				mActivity.toast("Failed to update Client, Please try again!");
			else {
				// resetFields();
				if (result.equals("true"))
					mActivity.toast("Succesfully updated Client!");
				else
					mActivity
							.toast("Unable to update Client, Please try again!");
				mActivity.popFragments();
			}

		}

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
