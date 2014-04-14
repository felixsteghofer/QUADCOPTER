package aztux.quadcontrol;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.*;
import android.view.View.OnTouchListener;

import java.io.IOException;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;


public class QuadcontrolActivity extends Activity {
    /** Called when the activity is first created. */
	
	public static int throttle = 6;  // 0 to 100
	public static int yaw = 0;  // -50 to + 50
	public static int pitch = 0; // -50 to + 50
	public static int roll = 0;  // -50 to + 50
	
	int height, width, thrCenterX, thrCenterY, pitCenterX, pitCenterY;
	static DatagramSocket s;
	static InetAddress local;
	
	static Timer t = new Timer();
	static TimerTask udptask;
	boolean listening = true;
	String serverMsgs = "";
	
	Handler hand = new Handler();
	
	Context ctx = null;
	View v = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        
		thrCenterX = width/4;
		thrCenterY = height/2;
		pitCenterX = (width/4) * 3;
		pitCenterY = height/2;
		
		
		try {
			s = new DatagramSocket(); 
			
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Thread listenThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
				try {
					DatagramSocket sock = new DatagramSocket(7001);
					while (listening) {
						byte[] message = new byte[1500];
						DatagramPacket p = new DatagramPacket(message, message.length);
						sock.receive(p);
						serverMsgs = new String(message, 0, p.getLength());
						hand.post(new Runnable() {
							@Override
							public void run() {
								v.invalidate();
							}
						});
						
					}
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
			
		});
		listenThread.start();
	
		
		//the task are now set in the PID Activity when Start or Stop is pressed
		//t.schedule(udptask, 50, 20);
		
		
        v = new View(this) {
        	protected void onDraw(Canvas g) {
        
        		
        		Paint wpt = new Paint();
        		wpt.setColor(Color.WHITE);
        		wpt.setStyle(Style.FILL);
        		g.drawRect(0, 0, width,height, wpt);
        		
        		Paint p = new Paint();
        		p.setColor(Color.BLACK);
        		p.setStrokeWidth(3);
        		
        		int widthRange = width/2;
        		int heightRange = height;
        		
        		g.drawLine(thrCenterX, 0, thrCenterX, height, p);
        		g.drawLine(0, thrCenterY, width/2, thrCenterY, p);
        		g.drawCircle((int)(thrCenterX + (yaw/50.0)*(widthRange/2)), (int)(height - (throttle/100.0)*heightRange), 10, p);
        		
        		g.drawCircle((int)(pitCenterX  + (roll/50.0)*(widthRange/2)), (int)(pitCenterY - (pitch/50.0)*(heightRange/2)), 10, p);
        		g.drawLine(pitCenterX, 0, pitCenterX, height, p);
        		g.drawLine(width/2, pitCenterY, width, pitCenterY, p);

        		Paint p2 = new Paint();
        		g.drawText("Thr "+throttle+" Yaw "+yaw + " Pitch "+pitch + " Roll" + roll, 10, 10, p2);
        		Paint p3 = new Paint();
        		p3.setTextSize(20);
        		g.drawText(serverMsgs, 10, 40, p3);
        	}
        };
        
        
        v.setOnTouchListener(new OnTouchListener() {
			@Override
			//------------------------------------------------
			//Handles finger motions on the screen
			//and calculates their position = Remote commands
			//------------------------------------------------
			
			public boolean onTouch(View v, MotionEvent event) {
				int pointerCount = event.getPointerCount();
				for(int i=0; i<pointerCount; i++) {
					
					int act = event.getActionMasked();
					if(act == MotionEvent.ACTION_MOVE || act == MotionEvent.ACTION_DOWN) {
						int x = (int) event.getX(i), y = (int) event.getY(i);
						
						
						
						if ( x < width/2.0) { // throttle and yaw
							int n_throttle = (int)(((height - y )/ (double)height)*100.0);
							if(Math.abs(n_throttle - throttle) < 20)
								throttle = n_throttle;
							int n_yaw = (int) (((x - thrCenterX) / (width/2.0))*100.0);
							if(Math.sqrt(Math.pow(n_throttle - throttle,2) + Math.pow(n_yaw - yaw,2)) < 25) {
								yaw = n_yaw;
								throttle = n_throttle;
							}
						} else {
							int n_pitch = (int) (((pitCenterY - y) / ((double)height))*100.0);
							
							int n_roll = (int) (((x - pitCenterX) / ((double)width/2.0))*100.0);
							if(Math.sqrt(Math.pow(n_roll - roll,2) + Math.pow(n_pitch - pitch,2)) < 25) {
								roll = n_roll;
								pitch = n_pitch;
							}
						}
	
					} else if (act == MotionEvent.ACTION_UP || act == MotionEvent.ACTION_POINTER_UP ) {
						//
						//one finger or the two fingers went out of the screen
						int x = (int) event.getX(i); 
						//int y = (int) event.getY(i);
						if ( x < width/2.0) { // throttle and yaw 
							yaw = 0;
						} else {//pitch and roll
							pitch = 0;
							roll = 0;
						}
					}
				}
				v.invalidate();
				
				return true;
				
			}
		});
        
        //hand.post(new Runnable() {
			
			//@Override
			//public void run() {
				//Toast.makeText(ctx, "PLEASE DISABLE PHONE CALLS", 500).show();
			//}
		//});
        
		setContentView(v);
    }
    
 
    public static void pause_remote() {
    	t.cancel();	
    }
    
    public static void resume_remote() {
        t = new Timer();
        udptask =  new TimerTask() {
			
			@Override
			public void run() {

				try {
					local = InetAddress.getByName(PIDActivity.ip);
					String msg = "{ \"type\": \"rcinput\", \"thr\": " + throttle + ", \"yaw\": " + yaw
							+ ", \"pitch\": " + pitch + ", \"roll\": " + roll + "}\n";
					int msg_length = msg.length();
					byte[] message = msg.getBytes();
					s.send(new DatagramPacket(message, msg_length, local, 7000));
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		};
		//schedul the new remote
        t.schedule(udptask, 50, 20);
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
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	//put values while pausing	
    	//throttle = UNCHANGED !!!!
    	yaw = roll = pitch = 0;
    }
    
    @Override
    protected void onResume() {
    	super.onResume();

    }
    
    
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	udptask.cancel();

    	listening = false;
    }
}