package com.example.emergency;

import com.parse.ParseGeoPoint;
import com.parse.ParseObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

public class Extras {

	public static ParseObject parseObject;
	public static ParseGeoPoint parseGeoPoint;
	 public static void createDialogueBox(Context context, String title, String message) {
      	  
  		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
  			context);

  		// set title
  		alertDialogBuilder.setTitle(title);

  		// set dialog message
  		alertDialogBuilder
  			.setMessage(message)
  			.setCancelable(false)
  			.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
  				public void onClick(DialogInterface dialog,int id) {
  					// if this button is clicked, close
  					// current activity
  					
  				}
  			  });

  			// create alert dialog
  			AlertDialog alertDialog = alertDialogBuilder.create();

  			// show it
  			alertDialog.show();
  		}
}
