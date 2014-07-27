package com.scio.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.scio.model.FragmentAvailable;

public class SplashScreen extends Fragment{
	
	private ProgressBar progress;
	private Thread splashTread;
	protected int _splashTime = 2000;
	private LinearLayout splashscreen;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.splash, container, false);
		
		splashscreen = (LinearLayout) view.findViewById(R.id.splashscreenID);

		progress = (ProgressBar) view.findViewById(R.id.progressBar1);
		splashTread = new Thread() {
			@Override
			public void run() {
				try {
					synchronized (this) {
						wait(_splashTime);
					}

				} catch (InterruptedException e) {
				} finally {

					splashscreen.getHandler().post(new Runnable() {
						public void run() {
							progress.setVisibility(View.GONE);
							SCIOMain.instance().changeFragment(
									FragmentAvailable.LOGIN);
						}
					});
				}
			}
		};

		splashTread.start();

		return view;
	}

}
