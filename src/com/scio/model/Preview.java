package com.scio.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.Size;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.scio.model.FragmentsAvailable;
import com.scio.ui.MainForClient;

@SuppressLint("NewApi")
public class Preview extends SurfaceView implements SurfaceHolder.Callback {
	SurfaceHolder mHolder;
	Camera mCamera;
	Context mContext;
	Camera.Parameters parameters;

	public boolean flash = false;
	public boolean camIsFront = false;
	private int orientation = 270;

	@SuppressWarnings("deprecation")
	public Preview(Context context, AttributeSet attrs) {
		super(context, attrs);
		mHolder = getHolder();
		mHolder.addCallback(this);
		mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		mContext = context;
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;

		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;

		int targetHeight = h;

		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}

		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		try {
			mCamera.stopPreview();
		} catch (Exception e) {
			e.printStackTrace();
		}
		parameters = mCamera.getParameters();
		List<Size> sizes = parameters.getSupportedPreviewSizes();
		Size optimalSize = getOptimalPreviewSize(sizes, width, height);
		try {

			G.G_W = width;
			G.G_H = height;

			parameters.setPreviewSize(optimalSize.width, optimalSize.height);
			mCamera.setParameters(parameters);
			mCamera.setDisplayOrientation(90);
			mCamera.startPreview();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// public void openFlash() {
	// parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
	// mCamera.setParameters(parameters);
	// mCamera.startPreview();
	// }

	// public void flipcamera(boolean isFront) {
	//
	// if (mCamera != null) {
	// System.out.println("flipcamera");
	// mCamera.stopPreview();
	// mCamera.release();
	// mCamera = null;
	//
	// }
	// int camID;
	//
	// if (isFront) {
	// orientation = 270;
	// flash = false;
	// camID = Camera.CameraInfo.CAMERA_FACING_FRONT;
	// } else {
	// orientation = 90;
	// camID = Camera.CameraInfo.CAMERA_FACING_BACK;
	// }
	//
	// mCamera = Camera.open(camID);
	//
	// if (mCamera != null) {
	// try {
	// mCamera.setDisplayOrientation(90);
	// mCamera.setPreviewDisplay(mHolder);
	// mCamera.startPreview();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// }

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		PackageManager packageManager = mContext.getPackageManager();
		// if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
		// result += "Camera: YES";
		// } else {
		// result += "Camera: NO";
		// }
		if (packageManager
				.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		} else {
			mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
		}

		// mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
		try {
			mCamera.setPreviewDisplay(holder);
		} catch (IOException exception) {
			mCamera.release();
			mCamera = null;
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		mCamera.stopPreview();
		mCamera.release();
		mCamera = null;
	}

	// /////////////////////////////////////////////////////////////////////////////
	public static Bitmap bmpRotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) b.getWidth() / 2,
					(float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(b, 0, 0, b.getWidth(),
						b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();

					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// We have no memory to rotate. Return the original bitmap.
			}
		}
		return resizeBmp(b);
	}

	private static Bitmap resizeBmp(Bitmap myBmp) {

		Paint bitPaint = new Paint();
		Bitmap resultBmp = Bitmap.createBitmap((int) G.G_W, (int) G.G_H,
				Config.ARGB_8888);

		Canvas cv = new Canvas(resultBmp);
		cv.drawBitmap(myBmp, null, new Rect(0, 0, (int) G.G_W, (int) G.G_H),
				bitPaint);
		if (!myBmp.isRecycled())
			myBmp.recycle();

		return resultBmp;
	}

	private void saveBitmapToFileCache(final byte[] data) {
		(new AsyncTask<Void, Void, Void>() {
			private ProgressDialog mDlg;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// mDlg = ProgressDialog.show(mContext, null, "Please Wait...");
				mDlg = new ProgressDialog(mContext);
				mDlg.setMessage("Please wait...");
				mDlg.setCancelable(false);
				mDlg.show();
			}

			@Override
			protected void onPostExecute(Void arg) {
				super.onPostExecute(arg);
				mDlg.dismiss();
				MainForClient.instance().changeCurrentFragment(
						FragmentsAvailable.CLIENT, true);
				// MainForClient.instance().popEmailSetup(
				// "This is an attachment of current taken photo.",
				// "Current " + G.CLIENT_USERNAME + " photo");
				MainForClient.instance().sendViaEmail(
						"Current " + G.CLIENT_USERNAME + " photo",
						"This is an attachment of current taken photo.");
			}

			@Override
			protected Void doInBackground(Void... params) {
				Bitmap bmp = bmpRotate(
						BitmapFactory.decodeByteArray(data, 0, data.length),
						orientation);

				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				G.BYTE_ARRAY = stream.toByteArray();

				// MainForClient.instance().uploadPhoto(G.URL +
				// G.CLIENT+"images");
				// MainForClient.instance().uploadPhoto();
				return null;
			}

		}).execute();
	}

	public void onSnap() {
		mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
	}

	ShutterCallback shutterCallback = new ShutterCallback() {
		public void onShutter() {
			// Log.d(TAG, "onShutter'd");
		}
	};

	PictureCallback rawCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			// Log.d(TAG, "onPictureTaken - raw");
		}
	};

	PictureCallback jpegCallback = new PictureCallback() {
		public void onPictureTaken(byte[] data, Camera camera) {
			saveBitmapToFileCache(data);
		}
	};

}
