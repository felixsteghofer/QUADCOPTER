package aztux.quadcontrol;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.net.*;

public class CameraActivity extends Activity {
	
	private SharedPreferences prefs;
	
	void sendMsg(String str) {
		DatagramSocket s = null;
		InetAddress local = null;
		try {
			s = new DatagramSocket(); 
			local = InetAddress.getByName(PIDActivity.ip);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			byte[] message = str.getBytes();
			s.send(new DatagramPacket(message, str.length(), local, 7000));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	void generateMsg(){
		
		String msg = "";
		sendMsg(msg);
				
	}	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.camera);
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
    {
         super.onCreateOptionsMenu(menu);
         
         MenuItem Item = menu.add("PID");
         MenuItem Itemcam = menu.add("Camera");
		return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	if (item.getTitle() == "PID") {
    		Intent intent = new Intent(this, PIDActivity.class);
    		startActivity(intent);
    	}
    	if (item.getTitle() == "Camera") {
    		Intent intent = new Intent(this, CameraActivity.class);
    		startActivity(intent);
    	}
    	return true;
    }
	
}