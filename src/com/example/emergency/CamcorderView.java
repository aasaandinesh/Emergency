package com.example.emergency;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CamcorderView extends SurfaceView implements
		SurfaceHolder.Callback {

	MediaRecorder recorder;
	SurfaceHolder holder;
	String outputFile = Environment.getExternalStorageDirectory().getPath()+"/Emergency/emergency";

	public CamcorderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		holder = getHolder();
		holder.addCallback(this);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		
		
	}

	public void surfaceCreated(SurfaceHolder holder) {
		
	}
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
	}

	public void surfaceDestroyed(SurfaceHolder holder) {
	}

	public void setOutputFile(String filename)
	{
		outputFile = filename;
		recorder.setOutputFile(filename);
	}
	
    public void startRecording(String number)
    {
    	recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
		 //recorder.setVideoSize(480, 320);
		 recorder.setVideoFrameRate(15);
		// recorder.setMaxDuration(10000);
		 
		 recorder.setOutputFile(outputFile+"_"+number+".mp4");
			recorder.setPreviewDisplay(holder.getSurface());
			if (recorder != null) {
				try {
					recorder.prepare();
				} catch (IllegalStateException e) {
					Log.e("IllegalStateException", e.toString());
				} catch (IOException e) {
					Log.e("IOException", e.toString());
				}
			}
    	recorder.start();
    }
    
    public void stopRecording()
    {
    	recorder.stop();
    	recorder.release();
    }
}