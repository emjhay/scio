package com.scio.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.scio.database.DataBaseManager;
import com.scio.model.FragmentAvailable;
import com.scio.model.G;

public class SCIOMain extends FragmentActivity {

	private Fragment splashFragment;
	private FragmentAvailable currentFragment;

	private static SCIOMain instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.main_sciomain);
		instance = this;
		currentFragment = FragmentAvailable.SPLASH;

		G.CONTEXT = this;
		
		G.FONT = Typeface.createFromAsset(getAssets(),
				"fonts/Arial Rounded Bold.ttf");

		if (G.DATA_BASE == null)
			G.DATA_BASE = DataBaseManager.instance(this);

		if (savedInstanceState == null) {
			if (findViewById(R.id.sciomain) != null) {
				splashFragment = new SplashScreen();
				splashFragment.setArguments(getIntent().getExtras());
				getSupportFragmentManager()
						.beginTransaction()
						.add(R.id.sciomain, splashFragment,
								currentFragment.toString()).commit();
			}
		}
		// fetchTime();

	}

	// private void fetchTime() {
	//
	// (new AsyncTask<Void, Void, Void>() {
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// }
	//
	// @Override
	// protected void onPostExecute(Void arg) {
	// super.onPostExecute(arg);
	//
	// }
	//
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// // System.out.println("get current time===========" +
	// TimeUtil.getTime());
	// System.out.println("SET EXPIRY DATE========="+TimeUtil.setExpiryDate());
	// return null;
	// }
	//
	// }).execute();
	//
	// }

	public static SCIOMain instance() {
		return instance;
	}

	public void changeFragment(FragmentAvailable newFragmentType) {

		Fragment newFragment = null;

		switch (newFragmentType) {
		case SPLASH:
			newFragment = new SplashScreen();
			break;

		case LOGIN:
			newFragment = new LoginScreen();
			break;
		case REGISTER:
			newFragment = new RegisterFragment();
			break;

		default:
			break;
		}

		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		/*
		 * transaction.replace(R.id.authenticationContainer, newFragment);
		 * transaction.commitAllowingStateLoss();
		 */

		try {
			getSupportFragmentManager().popBackStackImmediate(
					newFragmentType.toString(),
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
		} catch (java.lang.IllegalStateException e) {

		}

		if (currentFragment != FragmentAvailable.SPLASH)
			transaction.addToBackStack(newFragmentType.toString());

		transaction.replace(R.id.frameFragment, newFragment,
				newFragmentType.toString());
		transaction.commitAllowingStateLoss();
		getSupportFragmentManager().executePendingTransactions();

		currentFragment = newFragmentType;
	}

	public void displayToast(String str) {
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
		G.RESPONSE = false;
	}

	public boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;

		}

		return haveConnectedWifi || haveConnectedMobile;

	}

	public void showDialog(final String title, final String message) {

		AlertDialog.Builder builder = new AlertDialog.Builder(
				getApplicationContext());

		builder.setTitle(title);
		builder.setMessage(message)
				.setCancelable(false)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();

							}
						})
				.setPositiveButton("Download",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
								Intent intent = new Intent(Intent.ACTION_VIEW);
								intent.setData(Uri.parse("market://details?id=com.cryptovoip"));
								startActivity(intent);
							}
						});

		AlertDialog alert = builder.create();
		alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		alert.show();

	}

}
