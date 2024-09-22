package net.swigglesoft.shackbrowse;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.StrictMode;
import android.util.Log;

import java.lang.*;



/**
 * Created by brad on 2/17/2018.
 */

public class SBApplication extends Application
{
	private static Context context;
	private static final String TAG = SBApplication.class.getSimpleName();

	public SBApplication() {
		if (BuildConfig.DEBUG) {
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder(StrictMode.getVmPolicy())
					.detectLeakedClosableObjects()
					.build());
		}
	}

	public void onCreate() {
		super.onCreate();
		SBApplication.context = getApplicationContext();
	}
	public static Context getAppContext() {
		return SBApplication.context;
	}

	public static String getVersionName() {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			Log.e(TAG, "Package name not found.", e);
			return "1.0";
		}
	}
}