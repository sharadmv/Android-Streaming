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
      setContentView(R.layout.main);
	  if (android.os.Build.VERSION.SDK_INT > 9) {
		  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
			  .permitAll().build();
		  StrictMode.setThreadPolicy(policy);
	  }

      textOut = (EditText)findViewById(R.id.textout);
      Button buttonSend = (Button)findViewById(R.id.send);
      textIn = (TextView)findViewById(R.id.textin);
      buttonSend.setOnClickListener(buttonSendOnClickListener);
    }

  Button.OnClickListener buttonSendOnClickListener
    = new Button.OnClickListener(){

      @Override
        public void onClick(View arg0) {
          // TODO Auto-generated method stub
          Socket socket = null;
          OutputStream dataOutputStream = null;
          DataInputStream dataInputStream = null;

          try {
            socket = new Socket("192.168.1.3", 1234);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
			PrintWriter out = new PrintWriter(dataOutputStream);
            out.print(textOut.getText().toString());
			out.close();
            textIn.setText(dataInputStream.readUTF());
          } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          finally{
            if (socket != null){
              try {
                socket.close();
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }

            if (dataOutputStream != null){
              try {
                dataOutputStream.close();
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }

            if (dataInputStream != null){
              try {
                dataInputStream.close();
              } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
              }
            }
          }
        }
    };
}
