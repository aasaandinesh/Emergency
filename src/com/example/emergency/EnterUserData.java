package com.example.emergency;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * This activity requests the user to enter the details requied to register him on the Parse server.
 * @author dineshsingh
 *
 */
public class EnterUserData extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_enter_user_data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_enter_user_data, menu);
		return true;
	}

	public void enterUserDetails(View v){
		String name = ((EditText) findViewById(R.id.name)).getText().toString();
		String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
		String email = ((EditText) findViewById(R.id.email)).getText().toString();
		
		String body = name+","+phone+","+email;
		writeFile(body);
		/*
		Intent intent = new Intent(EnterUserData.this, Register.class);
		intent.putExtra("Name", name);
		intent.putExtra("Phone", phone);
		intent.putExtra("Email", email);
		startActivity(intent);
		*/
		
		
	}
	
	
	
	/**
     * Creates a directory and inside that a text file "number.txt"
     * which is supposed to store the phone number of the user
     *  
     * @param v
     */
    
    public void writeFile(String sBody){
    	String sFileName = "data.txt";
    	
    	//String sBody = "sdfdsf";
    	String dir = "Emergency";
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), dir);
            if (!root.exists()) {
                root.mkdirs();
            }

           
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        }
        catch(IOException e)
        {
             e.printStackTrace();
             Toast.makeText(this, "Not saved", Toast.LENGTH_SHORT).show();
             
        }
       }   
}
