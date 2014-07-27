package com.scio.ui;

import com.scio.model.G;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ClientFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.mainfor_client, container, false);
		
		TextView title = (TextView) view.findViewById(R.id.titleHeaderTextID);
		title.setVisibility(View.VISIBLE);
		title.setTypeface(G.FONT);
		title.setText("Welcome Client!");
		
		
//		Button logout = (Button) view.findViewById(R.id.editOrUpdateID);
//		logout.setText("Logout");
//		logout.setVisibility(View.VISIBLE);
//		logout.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				// TODO Auto-generated method stub
//				MainForClient.instance().showLogoutDialog();
//			}
//		});
		
		ImageView logout = (ImageView) view.findViewById(R.id.editOrUpdateID);
		logout.setBackgroundResource(R.drawable.btn_edit);
		logout.setVisibility(View.VISIBLE);
		logout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainForClient.instance().showLogoutDialog();
			}
		});

		ImageView pressBtn = (ImageView) view.findViewById(R.id.pressBtnID);
		pressBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainForClient.instance().sendInfoToAdmin();
			}
		});

		return view;
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		G.SHOW_SCREEN_CAMERA = false;
		G.SHOW_SCREEN_CLIENT = true;
	}

}
