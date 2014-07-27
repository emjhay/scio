package com.scio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.scio.database.DBRepository;
import com.scio.model.G;
import com.scio.model.Preview;

public class CameraFragment extends Fragment{
	private ImageView shutterBtn;
	private int click = 0;
	private Preview mPreview;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		View view = inflater.inflate(R.layout.camera_client, container, false);
		
		TextView title = (TextView) view.findViewById(R.id.titleHeaderTextID);
		title.setVisibility(View.VISIBLE);
		title.setTypeface(G.FONT);
		title.setText("Take Photo!");
		
		mPreview = (Preview) view.findViewById(R.id.camerapreview21);
		
		shutterBtn = (ImageView) view.findViewById(R.id.shutterBtnID);
		shutterBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				click++;
				if (click == 1){
					mPreview.onSnap();
//					DBRepository.updateCurrentHour(G.PHOTO_TABLE, 0);
				}
			}
		});
		
		return view;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		G.SHOW_SCREEN_CAMERA = true;
		G.SHOW_SCREEN_CLIENT = false;
	}

}
