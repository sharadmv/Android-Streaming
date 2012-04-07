package com.sharad.android;

import android.app.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;

public class MainActivity extends Activity
{
  EditText textOut;
  TextView textIn;

  /** Called when the activity is first created. */
  @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      if (android.os.Build.VERSION.SDK_INT > 9) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
          .permitAll().build();
        StrictMode.setThreadPolicy(policy);
      }
      socket = new Socket("192.158.1.3", 1234);
      ParcelFileDescriptor  pfd =ParcelFileDescriptor.fromSocket(socket);
      recorder = new MediaRecorder();
      recorder.setVideoSource(MediaRecorder.VideoSource.DEFAULT);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
      recorder.setOutputFile(pfd.getFileDescriptor());
      recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
      mPreview = new Preview(VideoRecorder.this,recorder);

      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
      setContentView(mPreview);
    }
}
