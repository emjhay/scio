package com.scio.model;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.scio.ui.R;

public class CheckUsersListAdapter extends ArrayAdapter<Users> {
	

	private LayoutInflater mInflater;
	private Context mContext;

	public CheckUsersListAdapter(Context context) {
		super(context, android.R.layout.simple_list_item_2);
		mContext = context;
		// TODO Auto-generated constructor stub
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public void setData(List<Users> data) {
		clear();

		if (data != null)
			for (Users sipContact : data) {
				add(sipContact);
			}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View view = null;

		if (convertView == null) {
			view = mInflater.inflate(R.layout.check_users_item, parent, false);
		} else {
			view = convertView;
		}

		Users users = getItem(position);

		android.view.Display display1 = ((android.view.WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (display1.getHeight() * 0.13),
				0.5f);

		LinearLayout layout1 = (LinearLayout) view
				.findViewById(R.id.checkUsersItemLayoutID);
		layout1.setLayoutParams(lp1);

		TextView usersListName = (TextView) view
				.findViewById(R.id.usersListNameID);
		usersListName.setTypeface(G.FONT);
		usersListName.setText(users.getUsername());
		
		TextView firstNameList = (TextView) view
				.findViewById(R.id.firstNameListID);
		firstNameList.setTypeface(G.FONT);
		firstNameList.setText(users.getFirstName());
		
		
		TextView lastNameList = (TextView) view
				.findViewById(R.id.lastNameListID);
		lastNameList.setTypeface(G.FONT);
		lastNameList.setText(users.getLastName());

		return view;
	}

}
