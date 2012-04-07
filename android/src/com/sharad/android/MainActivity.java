package com.sharad.android;

import android.app.*;
import android.content.pm.*;
import android.media.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;

public class MainActivity extends Activity
{
  // User Interface Elements
  VideoView mView;
  TextView connectionStatus;
  SurfaceHolder mHolder;
  // Video variable
  MediaRecorder recorder; 
  // Networking variables
  public static String SERVERIP="";
  public static final int SERVERPORT = 1234;
  private Handler handler = new Handler();
  private ServerSocket serverSocket;
  private ParcelFileDescriptor pfd;
  private Socket socket;
  /** Called when the activity is first created. */
  @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
	  
      setContentView(R.layout.main);
	  Button button = (Button)findViewById(R.id.stop);
      String hostname = "192.168.1.3";
      int port = 1234;
      if (android.os.Build.VERSION.SDK_INT > 9) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
          .permitAll().build();
        StrictMode.setThreadPolicy(policy);
      }
      socket = null;
      try {
        socket = new Socket(InetAddress.getByName(hostname), port);
      } catch (UnknownHostException e) {

        e.printStackTrace();
      } catch (IOException e) {

        e.printStackTrace();
      }

      pfd = ParcelFileDescriptor.fromSocket(socket);

      recorder = new MediaRecorder();
      VideoView p = (VideoView)findViewById(R.id.video_preview);
	  SurfaceHolder holder = p.getHolder();
	  holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	  recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
      recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
      recorder.setVideoEncoder(MediaRecorder.VideoEncoder.MPEG_4_SP);
      recorder.setOutputFile(pfd.getFileDescriptor());
	  recorder.setPreviewDisplay(holder.getSurface());
	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	  
      try {
        recorder.prepare();
      } catch (IllegalStateException e) {

        e.printStackTrace();
      } catch (IOException e) {

        e.printStackTrace();
      }
      recorder.start();
	  
    }
	public void stop(View v){
		recorder.stop();
		
		try {
			if (socket!=null){
				pfd.close();
				socket.close();
				}
		} catch (IOException e){
			e.printStackTrace();
		}
	}
  public class SendVideoThread implements Runnable{
    public void run(){
      // From Server.java
      try {
        if(SERVERIP!=null){
          handler.post(new Runnable() {
            @Override
            public void run() {
              connectionStatus.setText("Listening on IP: " + SERVERIP);
            }
          });
          while(true) {
            //listen for incoming clients
            Socket client = new Socket(SERVERIP,SERVERPORT);
            handler.post(new Runnable(){
              @Override
              public void run(){
                connectionStatus.setText("Connected.");
              }
            });
            try{
              // Begin video communication
              final ParcelFileDescriptor pfd = ParcelFileDescriptor.fromSocket(client);
              handler.post(new Runnable(){
                @Override
                public void run(){
                  recorder = new MediaRecorder();
                  recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
                  recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);                 
                  recorder.setOutputFile(pfd.getFileDescriptor());
                  recorder.setVideoFrameRate(20);
                  recorder.setVideoSize(176,144);
                  recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H263);
                  recorder.setPreviewDisplay(mHolder.getSurface());
                  try {
                    recorder.prepare();
                  } catch (IllegalStateException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                  }
                  recorder.start();
                  recorder.release();
                }
              });
            } catch (Exception e) {
              handler.post(new Runnable(){
                @Override
                public void run(){
                  connectionStatus.setText("Oops.Connection interrupted. Please reconnect your phones.");
                }
              });
              e.printStackTrace();
            }
          }
        } else {
          handler.post(new Runnable() {
            @Override
            public void run(){
              connectionStatus.setText("Couldn't detect internet connection.");
            }
          });
        }
      } catch (Exception e){
        handler.post(new Runnable() {
          @Override
          public void run() {
            connectionStatus.setText("Error");
          }
        });
        e.printStackTrace();
      }
      // End from server.java
    }
  }
}
