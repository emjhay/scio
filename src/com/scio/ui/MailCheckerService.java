package com.scio.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;

import com.scio.database.DBRepository;
import com.scio.model.G;

public class MailCheckerService extends Service {

	private Timer timer;
	private int countLocation = 0;
	private int countSMS = 0;
	private int countCall = 0;
//	private int countPhoto = 0;

	private final IBinder mBinder = new LocalBinder();

	final Handler hand = new Handler();

	private TimerTask updateTask = new TimerTask() {
		@Override
		public void run() {
			hand.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					
					System.out.println("=============RUNNING SERVICE MAIL");
					
					if (DBRepository.getCurrentHour(G.LOCATION_TABLE) == DBRepository
							.getSetInterval(G.LOCATION_TABLE)
							&& DBRepository.getEmailStatus(G.LOCATION_TABLE) == 1) {
						countLocation++;
						if (countLocation == 4) {
							System.out.println("if================="
									+ countLocation);
							countLocation = 0;
							// Send email to admin, this user not comply to
							// press button
							MainForClient
									.instance()
									.sendViaEmailInBG(
											" Client " + G.CLIENT_USERNAME
													+ " Location",
											"This email informing you that user not comply to press the button for status report.");
						} else {
							System.out.println("else================="
									+ countLocation);
							MainForClient.instance().runAlert();
						}
					}

					if (DBRepository.getCurrentHour(G.SMS_TABLE) == DBRepository
							.getSetInterval(G.SMS_TABLE)
							&& DBRepository.getEmailStatus(G.SMS_TABLE) == 1) {
						countSMS++;
						if (countSMS == 4) {
							// Send email to admin, this user not comply to
							// press button
							System.out
									.println("if=================" + countSMS);
							countSMS = 0;
							MainForClient
									.instance()
									.sendViaEmailInBG(
											" Client " + G.CLIENT_USERNAME
													+ " SMS",
											"This email informing you that user not comply to press the button for status report.");
						} else {
							System.out.println("else================="
									+ countSMS);
							MainForClient.instance().runAlert();
						}
					}

					if (DBRepository.getCurrentHour(G.CALL_TABLE) == DBRepository
							.getSetInterval(G.CALL_TABLE)
							&& DBRepository.getEmailStatus(G.CALL_TABLE) == 1) {
						countCall++;
						if (countCall == 4) {
							// Send email to admin, this user not comply to
							// press button
							System.out.println("if================="
									+ countCall);
							countCall = 0;
							MainForClient
									.instance()
									.sendViaEmailInBG(
											" Client " + G.CLIENT_USERNAME
													+ " Call",
											"This email informing you that user not comply to press the button for status report.");
						} else {
							System.out.println("else================="
									+ countCall);
							MainForClient.instance().runAlert();
						}
					}

//					if (DBRepository.getCurrentHour(G.PHOTO_TABLE) == DBRepository
//							.getSetInterval(G.PHOTO_TABLE)
//							&& DBRepository.getEmailStatus(G.PHOTO_TABLE) == 1) {
//						countPhoto++;
//						if (countPhoto == 4) {
//							// Send email to admin, this user not comply to
//							// press button
//							System.out.println("if================="
//									+ countPhoto);
//							countPhoto = 0;
//							MainForClient
//									.instance()
//									.sendViaEmailInBG(
//											" Client " + G.CLIENT_USERNAME
//													+ " Photo",
//											"This email informing you that user not comply to press the button for status report.");
//						} else {
//							System.out.println("else================="
//									+ countPhoto);
//							MainForClient.instance().runAlert();
//						}
//					}
				}
			});
		}
	};

	public class LocalBinder extends Binder {
		MailCheckerService getService() {
			return MailCheckerService.this;
		}
	}

	public int onStartCommand(Intent intent, int flags, int startId) {
		System.out.println("=================SERVICE MAIL START");
		timer = new Timer("TweetCollectorTimer");
		timer.schedule(updateTask, 10 * 60 * 1000L, 10 * 60 * 1000L);
		return START_STICKY;

	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("=================SERVICE MAIL DESTROY");
		timer.cancel();
		timer = null;
	}

}
