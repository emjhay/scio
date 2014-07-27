package com.scio.profile;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.scio.model.G;
import com.scio.model.ServiceHandler;
import com.scio.ui.BaseFragment;
import com.scio.ui.R;
import com.scio.util.StringUtil;

public class MyProfile extends BaseFragment {

	private TextView firstName, lastName, userName, password, email,
			phoneNumber;

	private ProgressDialog pDialog;
	private ImageView editBtn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.myprofile, container, false);

		
		TextView headerTitle = (TextView) view
				.findViewById(R.id.titleHeaderTextID);
		headerTitle.setTypeface(G.FONT);
		headerTitle.setText("My Profile");

		firstName = (TextView) view.findViewById(R.id.firstNameTextID);
		lastName = (TextView) view.findViewById(R.id.lastNameTextID);
		userName = (TextView) view.findViewById(R.id.userNameTextID);
		password = (TextView) view.findViewById(R.id.passwordTextID);
		email = (TextView) view.findViewById(R.id.emailTextID);
		email.setSelected(true);
		phoneNumber = (TextView) view.findViewById(R.id.phoneNumberTextID);

		editBtn = (ImageView) view.findViewById(R.id.editOrUpdateID);
		editBtn.setVisibility(View.VISIBLE);
//		editBtn.setText("Edit");
//		editBtn.setTypeface(G.FONT);
		editBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mActivity.pushFragments(G.TAB_C, new EditMyProfile(), true,
						true);
			}
		});

		if (mActivity.haveNetworkConnection())
			new FetchAdmin().execute();
		else
			mActivity.toast(getString(R.string.error_network_unreachable));

		return view;
	}

	private class FetchAdmin extends AsyncTask<JSONArray, Void, JSONArray> {

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
			

			String jsonStr = sh.makeServiceCall(G.URL + G.ADMIN
					+ G.LOGINWITH_USERTYPE + G.ADMIN_USERNAME + "/" +G.ADMIN_PASSWORD+"/"+ "Admin"
					 +"/1", ServiceHandler.GET);

			JSONArray jArray = null;

			if (!StringUtil.isNullOrEmpty(jsonStr)) {

				try {
					jArray = new JSONArray(jsonStr);

				} catch (Exception e) {
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
			if (pDialog.isShowing())
				pDialog.dismiss();

			JSONObject jObj = null;

			if (result != null && result.length() > 0) {
				try {
					jObj = result.getJSONObject(0);

					G.ADMIN_ID = jObj.getInt("userKey");
					G.ADMIN_FIRSTNAME = jObj.getString("fname");
					G.ADMIN_LASTNAME = jObj.getString("lname");
					G.ADMIN_USERNAME = jObj.getString("userN");
					G.ADMIN_PASSWORD = jObj.getString("passW");
					G.ADMIN_EMAIL = jObj.getString("email");
					G.ADMIN_PHONENUMBER = jObj.getString("pnumber");
//					G.ADMIN_EXPIRY = jObj.getString("expiry");
					
					
					
					firstName.setText(G.ADMIN_FIRSTNAME);
					lastName.setText(G.ADMIN_LASTNAME);
					userName.setText(G.ADMIN_USERNAME);
					password.setText(G.ADMIN_PASSWORD);
					email.setText(G.ADMIN_EMAIL);
					phoneNumber.setText(G.ADMIN_PHONENUMBER + "");
					

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				mActivity.toast("Unable to connect, Please try again later!");
			}

		}

	}

}
