package com.sharad.android;

import android.app.*;
import android.os.*;
import java.io.*;
import java.net.*;

public class MainActivity extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		try {
			Socket s = new Socket("192.168.1.3",1234);	
			PrintWriter out = new PrintWriter(s.getOutputStream(),true);
			System.out.println("WRITING");
			out.print("hello world");
			out.close();
			s.close();
		} catch (IOException e){
			e.printStackTrace();
		}
		
        setContentView(R.layout.main);
    }
}
