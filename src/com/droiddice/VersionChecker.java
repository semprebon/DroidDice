/* Copyright (C) 2009-2011 Andrew Semprebon */
package com.droiddice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class VersionChecker implements Runnable {

	private static final String NEXT_VERSION_CHECK = "nextVersionCheck";

	public static final String TAG = "VersionChecker";

	private static final long MINUTES_BETWEEN_CHECKS = 60L; // check no more than once an hour
	private static final long MILLIS_BETWEEN_CHECKS = MINUTES_BETWEEN_CHECKS * 60L * 1000L;

	public Handler handler;
	public Activity activity;

	private static final SimpleDateFormat DATE_FORMAT 
		= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Check for a new version of the application in a separate thread; displaying a
	 * Toast if a new version is available.
	 * 
	 * @param activity
	 */
	public void checkForNewVersion(final Activity activity) {
		this.activity = activity;
		if (isTimeToCheckVersion()) {
			startVersionRequestThread(activity);
		}
	}

	/**
	 * Determine if its time to check for a new version by looking at the previously saved 
	 * time to check
	 * 
	 * @param context
	 * @return
	 */
	private boolean isTimeToCheckVersion() {
		SharedPreferences settings = activity.getPreferences(Activity.MODE_PRIVATE);
		try {
			Date nextCheck = DATE_FORMAT.parse(settings.getString(NEXT_VERSION_CHECK, "2001-01-01"));
			return (new Date()).after(nextCheck);
		} catch (ParseException e) {
			Log.e(TAG, e.toString());
			return false;
		}
	}

	/**
	 * Start a thread to check the version
	 * 
	 * @param activity
	 */
	private void startVersionRequestThread(final Activity activity) {
		Thread thread = new Thread(this);
		handler = new Handler() {
			public void handleMessage(Message msg) {
				Toast.makeText(activity,
						"A new version of DroidDice is now available",
						Toast.LENGTH_LONG).show();
			}
		};
		this.activity = activity;
		thread.start();
	}

	/**
	 * Threaded code
	 */
	public void run() {
		try {
			String packageName = activity.getClass().getPackage().getName();
			String latestVersion = getVersionFromWebPage();
			String currentVersion = activity.getPackageManager().getPackageInfo(packageName, 0).versionName;

			Log.d(TAG, "got " + latestVersion + " current is " + currentVersion);
			setNextVersionCheck();
			if (!latestVersion.equals(currentVersion)) {
				notifyMainThread(latestVersion);
			}
		} catch (IOException e) {
			Log.d(TAG, "IO Error: " + e);
		} catch (NameNotFoundException e2) {
			Log.d(TAG, "package not found");
		}
	}

	/**
	 * Send a message back to UI thread to inform user
	 * 
	 * @param version latest version of app
	 */
	private void notifyMainThread(String version) {
		Bundle bundle = new Bundle();
		bundle.putString("version", version);
		Message message = Message.obtain();
		message.setData(bundle);
		handler.sendMessage(message);
	}

	/**
	 * Go to known web page and extract lastest version from there
	 * 
	 * @return latest version
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private String getVersionFromWebPage() throws MalformedURLException, IOException {
		URL url = new URL("http://service.droiddice.com/AndroidManifest.xml");
		String s = readStreamIntoString(url.openStream());
		int start = s.indexOf(VERSION_TAG) + VERSION_TAG.length();
		int end = s.indexOf("\"", start);
		String version = s.substring(start, end);
		return version;
	}

	/**
	 * Read the stream into a string
	 * 
	 * @param in stream to read
	 * @return a string with the contents of the stream
	 * @throws IOException
	 */
	public String readStreamIntoString(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * Save the date/time when we need to next check for a new version as a preference
	 */
	private void setNextVersionCheck() {
		SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		Date nextCheck = new Date();
		nextCheck.setTime(nextCheck.getTime() + MILLIS_BETWEEN_CHECKS);
		editor.putString(NEXT_VERSION_CHECK, DATE_FORMAT.format(nextCheck));
		editor.commit();
	}
	
	private static final String VERSION_TAG = "android:versionName=\"";

}
