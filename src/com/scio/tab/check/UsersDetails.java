package com.scio.tab.check;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.scio.model.G;
import com.scio.model.ServiceHandler;
import com.scio.ui.BaseFragment;
import com.scio.ui.R;
import com.scio.util.StringUtil;

public class UsersDetails extends BaseFragment implements OnClickListener {

	// private CheckBox activatePhoto, deactivatePhoto;
	private Button editBtn, deleteBtn;
	private TextView firstName, lastName, userName, password, email,
			clientEmail, clientPass, clientPhone, adminPhone, location, sms, call;

	private ImageView locationNotif, smsNotif, callNotif;

	private ProgressDialog pDialog;
	private PopupWindow deletePopup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.check_users_details, container,
				false);

		TextView titleHeader = (TextView) view
				.findViewById(R.id.titleHeaderTextID);
		titleHeader.setTypeface(G.FONT);
		titleHeader.setText("Client Details");

		ImageView backBtn = (ImageView) view.findViewById(R.id.backBtnID);
		backBtn.setVisibility(View.VISIBLE);
//		backBtn.setTypeface(G.FONT);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				mActivity.popFragments();
			}
		});

		android.view.Display display1 = ((android.view.WindowManager) getActivity()
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.9),
				0.1f);

		LinearLayout.LayoutParams lp2 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.65));

		LinearLayout tableRow1 = (LinearLayout) view
				.findViewById(R.id.detailsFirstLayoutID);
		tableRow1.setLayoutParams(lp1);

		LinearLayout tableRow2 = (LinearLayout) view
				.findViewById(R.id.layoutSecondLayoutID);
		tableRow2.setLayoutParams(lp2);



		editBtn = (Button) view.findViewById(R.id.editBtnID);
		editBtn.setTypeface(G.FONT);
		editBtn.setOnClickListener(this);

		deleteBtn = (Button) view.findViewById(R.id.deleteBtnID);
		deleteBtn.setTypeface(G.FONT);
		deleteBtn.setOnClickListener(this);

		firstName = (TextView) view.findViewById(R.id.firstNameTextID);
		lastName = (TextView) view.findViewById(R.id.lastNameTextID);
		userName = (TextView) view.findViewById(R.id.userNameTextID);
		password = (TextView) view.findViewById(R.id.passwordTextID);
		email = (TextView) view.findViewById(R.id.emailTextID);
		email.setSelected(true);
		clientEmail = (TextView) view.findViewById(R.id.clientEmailTextID);
		clientEmail.setSelected(true);
		clientPass = (TextView) view.findViewById(R.id.clientPassTextID);
		clientPhone = (TextView) view.findViewById(R.id.clientPhoneID);
		adminPhone = (TextView) view.findViewById(R.id.phoneNumberTextID);

		location = (TextView) view.findViewById(R.id.locationIntervalTextID);
		sms = (TextView) view.findViewById(R.id.smsIntervalTextID);
		call = (TextView) view.findViewById(R.id.callIntervalTextID);

		locationNotif = (ImageView) view.findViewById(R.id.locationNotifID);
		smsNotif = (ImageView) view.findViewById(R.id.smsNotifID);
		callNotif = (ImageView) view.findViewById(R.id.callNotifID);



		setValueOnFields();

		return view;
	}

	private void setValueOnFields() {
		firstName.setText(G.CLIENT_FIRSTNAME);
		lastName.setText(G.CLIENT_LASTNAME);
		userName.setText(G.CLIENT_USERNAME);
		password.setText(G.CLIENT_PASSWORD);
		email.setText(G.CLIENT_ADMIN_EMAIL);
		clientEmail.setText(G.CLIENT_CLIENT_EMAIL);
		clientPass.setText(G.CLIENT_PASS_EMAIL);
		clientPhone.setText(G.CLIENT_PHONENUMBER+"");
		adminPhone.setText(G.ADMIN_PHONENUMBER + "");
		location.setText("Send email every " + G.CLIENT_LOCATION_INTERVAL
				+ " hours.");
		sms.setText("Send email every " + G.CLIENT_SMS_INTERVAL + " hours.");
		call.setText("Send email every " + G.CLIENT_CALL_INTERVAL + " hours.");

		// String status = "";

		if (G.CLIENT_LOCATION_STATUS == 1)
			locationNotif.setImageResource(R.drawable.ic_notif_green);
		else
			locationNotif.setImageResource(R.drawable.ic_notif_gray);

		if (G.CLIENT_SMS_STATUS == 1)
			smsNotif.setImageResource(R.drawable.ic_notif_green);
		else
			smsNotif.setImageResource(R.drawable.ic_notif_gray);

		if (G.CLIENT_CALL_STATUS == 1)
			callNotif.setImageResource(R.drawable.ic_notif_green);
		else
			callNotif.setImageResource(R.drawable.ic_notif_gray);

//		if (G.CLIENT_STATUS == 1)
//			photoNotif.setImageResource(R.drawable.ic_notif_green);
//		else
//			photoNotif.setImageResource(R.drawable.ic_notif_gray);

		// if (G.CLIENT_STATUS == 1) {
		// status = "Activate";
		// } else {
		// status = "Deactivate";
		// }
		// takePhoto.setText(status);

	}

	// private void setCheckOnStatus(int status) {
	// switch (status) {
	// case 0:
	// activatePhoto.setClickable(false);
	// activatePhoto.setChecked(false);
	// deactivatePhoto.setClickable(false);
	// deactivatePhoto.setChecked(true);
	// break;
	//
	// case 1:
	// activatePhoto.setClickable(false);
	// activatePhoto.setChecked(true);
	// deactivatePhoto.setClickable(false);
	// deactivatePhoto.setChecked(false);
	// break;
	//
	// default:
	// break;
	// }
	// }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.editBtnID:
			mActivity.pushFragments(G.TAB_A, new EditClient(), true, true);
			break;

		case R.id.deleteBtnID:
			// new DeleteClient().execute();
			if (mActivity.haveNetworkConnection())
				deleteDialog();
			else
				mActivity.toast(getString(R.string.error_network_unreachable));
			break;

		default:
			break;
		}
	}

	private class DeleteClient extends AsyncTask<String, Void, String> {

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
			String response = sh.deleteByID(G.URL + G.CLIENT + G.DELETE_CLIENT
					+ G.CLIENT_ID);
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();

			if (!StringUtil.isNullOrEmpty(result) && result.equals("OK")) {
				mActivity.toast("Succesfully deleted " + G.CLIENT_USERNAME);
			} else {
				mActivity.toast("Error occured please try again later!");
			}

			mActivity.popFragments();

		}

	}

	private void deleteDialog() {

		LayoutInflater inflater = (LayoutInflater) mActivity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		deletePopup = new PopupWindow(inflater.inflate(R.layout.dialog_delete,
				null, false), LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		deletePopup.setBackgroundDrawable(mActivity.getResources().getDrawable(
				R.drawable.bg_dialog));

		final View pvu = deletePopup.getContentView();

		pvu.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					deletePopup.dismiss();
				}
				return false;
			}
		});

		TextView textDialog = (TextView) pvu.findViewById(R.id.dialogTextID);
		textDialog.setText("Are you sure you want to delete "
				+ G.CLIENT_USERNAME + "?");

		Button noBtn = (Button) pvu.findViewById(R.id.noBtnID);
		noBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				deletePopup.dismiss();
			}
		});

		Button yesBtn = (Button) pvu.findViewById(R.id.yesBtnID);
		yesBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				deletePopup.dismiss();
				new DeleteClient().execute();
			}
		});

		mActivity.findViewById(R.id.userDetailsLayoutID).post(new Runnable() {
			public void run() {
				deletePopup.showAtLocation(
						mActivity.findViewById(R.id.userDetailsLayoutID),
						Gravity.CENTER, 0, 0);
			}
		});
	}

}