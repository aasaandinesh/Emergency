package com.example.emergency;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.parse.Parse;

import com.parse.FindCallback;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.PushService;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity is the main Activity which is called when the application is launched for the first time.
 * It checks if the user has already entered his details or not, if not then it prompts the user to enter his details.
 * It is expected that the user should fill in the details as soon as he installs the application.
 * @author dineshsingh
 *
 */
public class Emergency extends Activity implements LocationListener {

	private LocationManager locationManager;

	private boolean recording = false;
	private Location location;
	private String phoneNumberFile = Environment.getExternalStorageDirectory()
			+ "/Emergency/data.txt";
	TextView timeText;
	String phoneNumber;
	String provider;

	Thread thread;
	private int timeCounter = 0;
	Handler h;
	private int recordingStarted = 0;
	FileInputStream fileInputStream;
	CamcorderView camcorderView;
	TextView hello;
	private boolean isRecording = false;
	private int counter = 0;
	private boolean isFirstTimeRecordingStarted = false;

/**
 * @param savedInstanceState
 * 
 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Parse.initialize(this, "iB9EooJGLURPv5rluOreQ1VhAiFkc83yhJxYSvLE",
				"Kiy1GemOfQFy9D0UWvEYodxwJ58ZXHWhM0cQsBMj");

		Extras.parseGeoPoint = new ParseGeoPoint(0, 0);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		// hello = (TextView) findViewById(R.id.hello);

		createEmergencyDirectory();
		if (!isDetailsEntered())
			enterUserDetails();

		// Creating Parse User
		setContentView(R.layout.loading);
		String dataFromFile = readPhoneNumber(phoneNumberFile);
		final String[] data = dataFromFile.split(",");

		setLocationManager();

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		phoneNumber = data[1];
		final String name = data[0];
		String email = data[2];
		Extras.createDialogueBox(this, "phone number", phoneNumber);

		final ParseObject obj = new ParseObject("user");
		obj.put("Name", name);
		obj.put("Email", email);
		obj.put("Phone", phoneNumber);

		obj.saveInBackground(new SaveCallback() {

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if (e != null) {
					Toast.makeText(Emergency.this, "Error:" + e.getMessage(),
							Toast.LENGTH_LONG).show();

				} else {
			

						Extras.parseObject = obj;

						setContentView(R.layout.activity_emergency);
						timeText = (TextView) findViewById(R.id.time);

						Toast.makeText(Emergency.this, "Welcome "+name, Toast.LENGTH_LONG).show();

						// uploadFile(Environment.getExternalStorageDirectory().getPath()+
						// "/Emergency/emergency_1.mp4");

						h = new Handler();
						startTimer();
					
				}
			}

		});

	}

	/**
	 * Sets the location manager.
	 * Some initial work that needs to be done to get access to the location of the user
	 * If GPS is not enabled then it guides the user to the settings activity, where he/she can activate the GPS
	 * 
	 */
	public void setLocationManager() {
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);

		boolean enabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);

		// Check if enabled and if not send user to the GSP settings
		// Better solution would be to display a dialog and suggesting to
		// go to the settings
		if (!enabled) {
			createDialogueBox();

		}

		// Initialize the location fields
		if (location != null) {
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		}
	}

	/**
	 * Creates the dialogue box.
	 */
	public void createDialogueBox() {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle("Enable GPS");

		// set dialog message
		alertDialogBuilder
				.setMessage("Kindly enable GPS to access proper location")
				.setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, close
						// current activity
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	/**
	 * Start timer.
	 * This is a timer being executed in a background thread.
	 * It calls the stopRecordingFunction every 10 seconds.
	 */
	public void startTimer() {
		thread = new Thread() {
			@Override
			public void run() {
				try {
					while (true) {
						sleep(1000);
						timeCounter++;

						h.post(new Runnable() {
							public void run() {

								timeText.setText(timeCounter + "");
								if (timeCounter - recordingStarted > 10
										&& isRecording) {
									Toast.makeText(getApplicationContext(),
											"Recording stopped by thread",
											Toast.LENGTH_SHORT).show();
									stopRecordingFunction();
								}
								// hTextView.setText((System.currentTimeMillis()
								// - initTime)+"");
							}
						});

					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	/**
	 * Enter user details.
	 * It starts a new Activity, EnterUserData Activity, which asks the user to enter his/her details
	 */
	private void enterUserDetails() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Emergency.this, EnterUserData.class);
		startActivity(intent);

	}

	/**
	 * Start recording.
	 *
	 * @param v This function is called when the user presses the Start button on the screen.
	 * Once he/she presses the button the real activity gets activates. after this, everything happens automatically.
	 */
	public void startRecording(View v) {
		if(!isFirstTimeRecordingStarted){
		sendLocation();

		isRecording = true;
		try {
			counter++;
			startRecordingFunction(counter);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
		}
		isFirstTimeRecordingStarted = true;
		}
	}

	private void createParseObject() {
		// TODO Auto-generated method stub
		String fileData = readPhoneNumber(phoneNumberFile);
		String[] data = fileData.split(",");

	}

	/**
	 * This function reads the entire information stored in the data file in the form of a string separate by comma	 * 
	 * @param filename
	 * @return String in CSV format
	 */
	public String readPhoneNumber(String filename) {
		// AssetManager am = getApplicationContext().getAssets();
		InputStream is;
		String number = "";

		try {
			File myFile = new File(filename);
			FileInputStream fIn = new FileInputStream(myFile);
			BufferedReader myReader = new BufferedReader(new InputStreamReader(
					fIn));
			String aDataRow = "";
			String aBuffer = "";
			number = myReader.readLine();

			// txtData.setText(aBuffer);
			myReader.close();
			// Toast.makeText(this,"Done reading SD 'mysdfile.txt'",Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "number file not readable", Toast.LENGTH_SHORT)
					.show();
			// e.printStackTrace();
		}

		return number;

	}

	private void sendLocation() {
		// TODO Auto-generated method stub

	}

	/**
	 * Start recording function. It starts the recording. 
	 *
	 * @param counter the counter
	 * @throws InterruptedException the interrupted exception
	 */
	public void startRecordingFunction(int counter) throws InterruptedException {
		camcorderView = (CamcorderView) findViewById(R.id.camcorder_preview);
		recording = true;

		camcorderView.startRecording(counter + "");
		recordingStarted = timeCounter;
		isRecording = true;

		Toast.makeText(getApplicationContext(), "Recording started",
				Toast.LENGTH_LONG).show();

		// Thread.sleep(5000);

		// stopRecordingFunction();

	}

	/**
	 * This function stops the recording. Calls the function to upload the recorded file to the media in background thread.
	 */
	public void stopRecordingFunction() {
		isRecording = false;
		Toast.makeText(getApplicationContext(), "Recording stopped by thread",
				Toast.LENGTH_SHORT).show();
		sendLocation();

		camcorderView.stopRecording();
		Toast.makeText(this, counter + "", Toast.LENGTH_SHORT).show();
		String existingFileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Emergency/emergency_" + counter + ".mp4";
		uploadFile(existingFileName, Extras.parseObject, counter);
		// doFileUpload(existingFileName);
		// sendInformation(counter);
		counter++;

		try {
			startRecordingFunction(counter);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// finish();

	}

	/**
	 * Stops the entire application. This function is called when Stop button is pressed.
	 *
	 * @param v
	 */
	public void stopRecording(View v) {
		stopRecordingFunction();
		System.exit(0);
	}

	/**
	 * Checks if  details were entered.
	 *
	 * @return true, if  details entered
	 */
	private boolean isDetailsEntered() {
		// TODO Auto-generated method stub
		File f = new File(Environment.getExternalStorageDirectory()
				+ "/Emergency", "data.txt");

		return (f.exists());
	}

	/**
	 * Creates the emergency directory.
	 */
	private void createEmergencyDirectory() {
		// TODO Auto-generated method stub
		File direct = new File(Environment.getExternalStorageDirectory()
				+ "/Emergency");

		if (!direct.exists()) {
			direct.mkdir(); // directory is created;

		}
	}

	/**
	 * Creates the dialogue box.
	 *
	 * @param title the title
	 * @param message the message
	 */
	public void createDialogueBox(String title, String message) {

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder.setMessage(message).setCancelable(false)
				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_emergency, menu);
		return true;
	}

	/**
	 * Uploads the  file to the Parse server in background thread.
	 *
	 * @param fileName the file name
	 * @param obj the Parse object
	 * @param count the counter for recording number
	 */
	public void uploadFile(String fileName, final ParseObject obj, int count) {

		try {

			/* do whatever you want with buffer here */

			FileInputStream fis = new FileInputStream(fileName);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			byte[] buf = new byte[1024];
			try {
				for (int readNum; (readNum = fis.read(buf)) != -1;) {
					bos.write(buf, 0, readNum); // no doubt here is 0

				}
			} catch (IOException ex) {
				createDialogueBox("Error", ex.getMessage());

			}
			byte[] bytes = bos.toByteArray();

			final ParseFile file = new ParseFile("video_" + count, bytes);

			final String key = "video_" + count;
			file.saveInBackground(new SaveCallback(){

				@Override
				public void done(ParseException e) {
					// TODO Auto-generated method stub
					if(e!=null){
						Toast.makeText(Emergency.this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					else{
						obj.put(key, file);
						obj.put("coordinates", Extras.parseGeoPoint);
						obj.saveEventually();
					}
					
				}
				
			});
			
			// createDialogueBox("Succes", "File uploaded succesfully");

			// hello.setText(file.getUrl());
		} catch (Exception e) {
			createDialogueBox("Error", e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 *  Request updates at startup
	 *
	 */
	@Override
	protected void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/**
	 *  Remove the locationlistener updates when Activity is paused 
	 */
	@Override
	protected void onPause() {
		super.onPause();
		locationManager.removeUpdates(this);
	}


	@Override
	public void onLocationChanged(Location location) {
		float lat = (float) (location.getLatitude());
		float lng = (float) (location.getLongitude());

		Extras.parseGeoPoint.setLatitude(lat);
		Extras.parseGeoPoint.setLongitude(lng);
		// latituteField.setText(String.valueOf(lat));
		// longitudeField.setText(String.valueOf(lng));
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

}
