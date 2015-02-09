package com.example.emergency;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;

public class Register extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		registerOnParse();
	}

	private void registerOnParse() {
		// TODO Auto-generated method stub
		Intent intent = getIntent();
		String name = intent.getExtras().getString("Name");
		String email = intent.getExtras().getString("Email");
		String phone = intent.getExtras().getString("Phone");
		
		ParseObject obj = new ParseObject("user");
		obj.put("Name", name);
		obj.put("Email", email);
		obj.put("Phone", phone);
		
		obj.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException e) {
				// TODO Auto-generated method stub
				if(e==null){
					Intent intent = new Intent(Register.this, Emergency.class);
					startActivity(intent);
				}
				else{
					Extras.createDialogueBox(Register.this, "Error", e.getMessage());
				}
			}
			
		});
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}

}
