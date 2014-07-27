package com.scio.tab.check;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.scio.model.CheckUsersListAdapter;
import com.scio.model.G;
import com.scio.model.ServiceHandler;
import com.scio.model.Users;
import com.scio.ui.BaseFragment;
import com.scio.ui.R;

public class CheckUsers extends BaseFragment implements OnItemClickListener {

	private ListView checkUsersList;
	private CheckUsersListAdapter adapter;
	private ProgressDialog pDialog;

	List<Users> cliet = new ArrayList<Users>();

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.check_users_list, container,
				false);

		TextView titleHeader = (TextView) view
				.findViewById(R.id.titleHeaderTextID);
		titleHeader.setTypeface(G.FONT);
		titleHeader.setText("Client List");

		checkUsersList = (ListView) view.findViewById(R.id.checkUsersListID);
		checkUsersList.setOnItemClickListener(this);

		adapter = new CheckUsersListAdapter(getActivity());

		if (mActivity.haveNetworkConnection())
			new FetchAllClient().execute();
		else
			mActivity.toast(getString(R.string.error_network_unreachable));

		adapter = new CheckUsersListAdapter(getActivity());

		// if (cliet.size() == 0)
		// cliet.add(new Users(0, "lawrence", "123456", "Lawrence", "Benitez",
		// 123456789, 4, 2, 6, 1, "eduardo.delito@gmail.com",
		// "lawrence@gmail.com", "123456", "eduardo", "Client", 1, 1,
		// 1, 8, "2014-08-01 09:30:30"));
		// adapter.setData(cliet);
		// checkUsersList.setAdapter(adapter);

		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// TODO Auto-generated method stub
		final Users user = cliet.get(position);

		G.CLIENT_ID = user.getId();
		G.CLIENT_USERNAME = user.getUsername();
		G.CLIENT_PASSWORD = user.getPassword();
		G.CLIENT_FIRSTNAME = user.getFirstName();
		G.CLIENT_LASTNAME = user.getLastName();
		G.CLIENT_PHONENUMBER = user.getPhoneNumber();
		G.CLIENT_LOCATION_INTERVAL = user.getLocationInterval();
		G.CLIENT_SMS_INTERVAL = user.getSmsInterval();
		G.CLIENT_CALL_INTERVAL = user.getCallInterval();
//		G.CLIENT_STATUS = user.getStatus();
		G.CLIENT_ADMIN_EMAIL = user.getEmail();
		G.CLIENT_CLIENT_EMAIL = user.getClientEmail();
		G.CLIENT_PASS_EMAIL = user.getClientPass();
		G.CLIENT_ADMIN = user.getAdmin();
		G.CLIENT_USERTYPE = user.getUsertype();
		G.CLIENT_LOCATION_STATUS = user.getLocationStatus();
		G.CLIENT_SMS_STATUS = user.getSmsStatus();
		G.CLIENT_CALL_STATUS = user.getCallStatus();
//		G.CLIENT_PHOTO_INTERVAL = user.getPhotoInterval();
		G.CLIENT_EXPIRY = user.getExpiry();
		G.CLIENT_PHONENUMBER = user.getPhoneNumber();
		G.ADMIN_PHONENUMBER = user.getAdminPhone();
		

		mActivity.pushFragments(G.TAB_A, new UsersDetails(), true, true);
	}

	private class FetchAllClient extends
			AsyncTask<List<Users>, Void, List<Users>> {

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
		protected List<Users> doInBackground(List<Users>... arg0) {
			// Creating service handler class instance

			List<Users> usersData = new ArrayList<Users>();

			ServiceHandler sh = new ServiceHandler();

			// Making a request to url and getting response
			String jsonStr = sh.makeServiceCall(G.URL + G.CLIENT
					+ G.FINDCLIENTBY_ADMIN + G.ADMIN_USERNAME,
					ServiceHandler.GET);

			JSONArray jArray;
			JSONObject jObject = null;
			try {

				jArray = new JSONArray(jsonStr);
				for (int i = 0; i < jArray.length(); i++) {
					jObject = jArray.getJSONObject(i);
					System.out.println("ADMIN PHONE======"+jObject
									.getString("APhoneN"));
					usersData.add(new Users(jObject.getInt("userKey"), jObject
							.getString("userN"), jObject
							.getString("passW"), jObject
							.getString("fname"), jObject
							.getString("lname"), jObject
							.getString("phoneN"), jObject
							.getInt("locInt"), jObject
							.getInt("smsInt"), jObject
							.getInt("callInt"),
							jObject.getString("aemail"), jObject
									.getString("cemail"), jObject
									.getString("cpass"), jObject
									.getString("admin"), jObject
									.getString("userType"), jObject
									.getInt("loc"), jObject
									.getInt("sms"), jObject
									.getInt("calls"), jObject
									.getString("expiry"),jObject
									.getString("APhoneN")));

				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return usersData;
		}

		@Override
		protected void onPostExecute(List<Users> result) {
			super.onPostExecute(result);
			// Dismiss the progress dialog
			if (pDialog.isShowing())
				pDialog.dismiss();
			/**
			 * Updating parsed JSON data into ListView
			 * */

			cliet = result;

			adapter.setData(result);
			checkUsersList.setAdapter(adapter);

		}

	}

}
