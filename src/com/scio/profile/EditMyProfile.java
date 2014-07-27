package com.scio.profile;

import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scio.model.G;
import com.scio.model.ServiceHandler;
import com.scio.ui.BaseFragment;
import com.scio.ui.R;
import com.scio.ui.SCIOMain;
import com.scio.util.FieldTextUtil;
import com.scio.util.StringUtil;

public class EditMyProfile extends BaseFragment implements OnClickListener {

	private Button submitBtn;

	private EditText firstName, lastName, userName, email, password, confirm,
			phoneNumber;
	private ImageView icFirstName, icLastName, icUserName, icEmail, icPassword,
			icConfirm, icPhoneNumber;
	private ProgressDialog pDialog;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.myprofile_edit, container, false);
		
		TextView title = (TextView) view.findViewById(R.id.titleHeaderTextID);
		title.setVisibility(View.VISIBLE);
		title.setTypeface(G.FONT);
		title.setText("Edit My Profile");

		android.view.Display display1 = ((android.view.WindowManager) mActivity
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

		submitBtn = (Button) view.findViewById(R.id.submitBtnID);
		submitBtn.setText("Update");
		submitBtn.setTypeface(G.FONT);
		submitBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				closeKeyboard();

				if (!mActivity.haveNetworkConnection()) {
					mActivity
							.toast(getString(R.string.error_network_unreachable));
					return;
				}

				new UpdateAdmin().execute();
			}
		});

		icFirstName = (ImageView) view.findViewById(R.id.icFirstnameID);
		icFirstName.setOnClickListener(this);

		icLastName = (ImageView) view.findViewById(R.id.icLastnameID);
		icLastName.setOnClickListener(this);

		icUserName = (ImageView) view.findViewById(R.id.icUsernameID);
//		icUserName.setOnClickListener(this);

		icEmail = (ImageView) view.findViewById(R.id.icEmailID);
//		icEmail.setOnClickListener(this);

		icPassword = (ImageView) view.findViewById(R.id.icPasswordID);
		icPassword.setOnClickListener(this);

		icConfirm = (ImageView) view.findViewById(R.id.icConfirmID);
		icConfirm.setOnClickListener(this);

		icPhoneNumber = (ImageView) view.findViewById(R.id.icPhonenumberID);
		icPhoneNumber.setOnClickListener(this);

		firstName = (EditText) view.findViewById(R.id.firstNameFieldID);
		firstName.setText(G.ADMIN_FIRSTNAME);
		icFirstName.setVisibility(View.VISIBLE);
		icFirstName.setImageResource(R.drawable.ic_info_good);
		G.CHECK_FIRSTNAME = true;
		firstName.setSelection(firstName.getText().toString().length());
		FieldTextUtil.checkFirstName(firstName, icFirstName, submitBtn);

		lastName = (EditText) view.findViewById(R.id.lastNameFieldID);
		lastName.setText(G.ADMIN_LASTNAME);
		icLastName.setVisibility(View.VISIBLE);
		icLastName.setImageResource(R.drawable.ic_info_good);
		G.CHECK_LASTNAME = true;
		FieldTextUtil.checkLastName(lastName, icLastName, submitBtn);
		
		G.CHECK_CLIENT_EMAIL = true;
		G.CHECK_CLIENT_PASS = true;

		userName = (EditText) view.findViewById(R.id.userNameFieldID);
		userName.setKeyListener(null);
		userName.setText(G.ADMIN_USERNAME);
		icUserName.setVisibility(View.VISIBLE);
		icUserName.setImageResource(R.drawable.ic_info_good);
		G.CHECK_USERNAME = true;
		FieldTextUtil.checkUsername(userName, icUserName, submitBtn);

		email = (EditText) view.findViewById(R.id.emailFieldID);
		email.setKeyListener(null);
		email.setText(G.ADMIN_EMAIL);
		icEmail.setVisibility(View.VISIBLE);
		icEmail.setImageResource(R.drawable.ic_info_good);
		G.CHECK_EMAIL = true;
		FieldTextUtil.checkEmail(email, icEmail, submitBtn);

		password = (EditText) view.findViewById(R.id.passwordFieldID);
		confirm = (EditText) view.findViewById(R.id.confirmFieldID);
		
		password.setText(G.ADMIN_PASSWORD);
		icPassword.setVisibility(View.VISIBLE);
		icPassword.setImageResource(R.drawable.ic_info_good);
		G.CHECK_PASSWORD = true;
		password.setInputType(InputType.TYPE_CLASS_TEXT);
		FieldTextUtil.checkPassword(password, confirm, icPassword, icConfirm, submitBtn);

		
		confirm.setText(G.ADMIN_PASSWORD);
		icConfirm.setVisibility(View.VISIBLE);
		icConfirm.setImageResource(R.drawable.ic_info_good);
		G.CHECK_CONFIRM = true;
		FieldTextUtil.checkConfirmPassword(confirm, password, icConfirm,
				submitBtn);

		phoneNumber = (EditText) view.findViewById(R.id.phoneNumberFieldID);
		phoneNumber.setText(G.ADMIN_PHONENUMBER + "");
		icPhoneNumber.setVisibility(View.VISIBLE);
		icPhoneNumber.setImageResource(R.drawable.ic_info_good);
		phoneNumber.setInputType(InputType.TYPE_CLASS_NUMBER);
		G.CHECK_PHONENUMBER = true;
		FieldTextUtil.checkPhoneNumber(phoneNumber, icPhoneNumber, submitBtn);

		return view;
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
//		case R.id.icUsernameID:
//
//			if (StringUtil.isNullOrEmpty(userName.getText().toString()))
//				LifeSave.instance().displayToast("Username cannot be blank!");
//			else if (userName.getText().toString().length() >= 6)
//				LifeSave.instance()
//						.displayToast(
//								"Username must be greater than or equal to 6 characters!");
//
//			break;
//		case R.id.icEmailID:
//
//			if (StringUtil.isNullOrEmpty(email.getText().toString()))
//				LifeSave.instance().displayToast("Email Name cannot be blank!");
//			else if (new EmailValidator().validate(email.getText().toString()) == false)
//				LifeSave.instance().displayToast("Invalid email!");
//
//			break;
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

	private class UpdateAdmin extends AsyncTask<String, Void, String> {

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

			G.ADMIN_USERNAME = userName.getText().toString();
			G.ADMIN_PASSWORD = password.getText().toString();
			G.ADMIN_FIRSTNAME = firstName.getText().toString();
			G.ADMIN_LASTNAME = lastName.getText().toString();
			G.ADMIN_PHONENUMBER = phoneNumber.getText()
					.toString();
			G.ADMIN_EMAIL = email.getText().toString();

			String msgResponse = "";

			try {

				json.put(G.ID, G.ADMIN_ID);
				json.put(G.EMAIL, G.ADMIN_EMAIL);
				json.put(G.FIRSTNAME, G.ADMIN_FIRSTNAME);
				json.put(G.LASTNAME, G.ADMIN_LASTNAME);
				json.put(G.PASSWORD, G.ADMIN_PASSWORD);
				json.put(G.PHONENUMBER, G.ADMIN_PHONENUMBER);
				json.put(G.USERNAME, G.ADMIN_USERNAME);
				json.put(G.USERTYPE, "Admin");
				json.put(G.VERIFIED, 1);
//				json.put(G.EXPIRY, G.ADMIN_EXPIRY);
				

				StringEntity se = new StringEntity(json.toString());
				se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE,
						"application/json"));

				msgResponse = sh.makeServiceCall(G.URL + G.ADMIN + G.EDIT_ADMIN
						+ G.ADMIN_ID + "/", ServiceHandler.PUT, se);

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
				mActivity.toast("Failed to update Admin, Please try again!");
			else {
				// resetFields();
				if (result.equals("true"))
					mActivity.toast("Succesfully updated Admin!");
				else
					mActivity
							.toast("Unable to update Admin, Please try again!");
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
