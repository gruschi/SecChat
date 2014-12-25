package cs.hm.edu.sisy.chat.services;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cs.hm.edu.sisy.chat.Messaging;
import cs.hm.edu.sisy.chat.R;
import cs.hm.edu.sisy.chat.communication.RestThreadTask;
import cs.hm.edu.sisy.chat.enums.SCState;
import cs.hm.edu.sisy.chat.enums.SCTypes;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.objects.Partner;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;
import cs.hm.edu.sisy.chat.tools.Common;

public class BGService extends Service {

    // Check interval: every 24 hours
    //private static long UPDATES_CHECK_INTERVAL = 24 * 60 * 60 * 1000;
    //private static long UPDATES_CHECK_INTERVAL = 5000;
	
	private static ScheduledExecutorService waitingScheduleExecutor;
	private static ScheduledExecutorService messagingScheduleExecutor;
	
    private static final String TAG = "BGService";
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
    @Override
    public void onCreate() {
    	Common.doToast(this, "Congrats! MyService Created");
        Log.d(TAG, "onCreate");
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    	Common.doToast(this, "MyService Started");
        Log.d(TAG, "onStart");

   	if(SCState.getState() == SCState.CONNECTED_TO_CHAT) {
      if(waitingScheduleExecutor != null)
        waitingScheduleExecutor.shutdownNow();
   		messagingSchedule(BGService.this);
   		
		SharedPrefs.savePIN( BGService.this, PinHashGenerator.generatePIN() );
		SharedPrefs.saveStoragedPinDate( BGService.this, Common.getCurrentDate() );

        Intent i = new Intent();
        i.setClass(BGService.this.getBaseContext(), Messaging.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
   	}
   	else {
      if(messagingScheduleExecutor != null)
        messagingScheduleExecutor.shutdownNow();
   		waitingSchedule(BGService.this);
   	}
   	
   	
   	/* Timer is used to take the friendList info every UPDATE_TIME_PERIOD;
		timer = new Timer();   
		
		Thread thread = new Thread()
		{
			@Override
			public void run() {			
				
				//socketOperator.startListening(LISTENING_PORT_NO);
				Random random = new Random();
				int tryCount = 0;
				while (socketOperator.startListening(10000 + random.nextInt(20000))  == 0 )
				{		
					tryCount++; 
					if (tryCount > 10)
					{
						// if it can't listen a port after trying 10 times, give up...
						break;
					}
					
				}
			}
		};		
		thread.start();
   */
    }
 
    @Override
    public void onDestroy() {
    	//destoy scheduleTaskExecutor
    	if(waitingScheduleExecutor != null)
    		waitingScheduleExecutor.shutdownNow();
    	
    	if(messagingScheduleExecutor != null)
    		messagingScheduleExecutor.shutdownNow();
    	
        Common.doToast(this, "MyService Stopped");
        Log.d(TAG, "onDestroy");
        
        this.stopSelf();
        
		super.onDestroy();
    }
	
    static void waitingSchedule(final Context context) {
    	waitingScheduleExecutor = Executors.newScheduledThreadPool(5);

    	// This schedule a runnable task every 5 seconds
    	waitingScheduleExecutor.scheduleAtFixedRate(new Runnable() {
    	  public void run() {
    		Log.d(TAG, "wSCHEDULE");
    		if(SCState.getState() != SCState.CHAT_CONNECTION_INCOMING)
    			new RestThreadTask(SCTypes.SERVICE, context).execute();
    		
    		if(SCState.getState() < SCState.LOGGED_IN)
    		{
    			disconnect(context);
    		}
    		  
    		//someone is calling me, call back
    		if(SCState.getState() == SCState.CHAT_CONNECTION_INCOMING)
    			new RestThreadTask(SCTypes.CONNECT_SERVICE, context).execute(Partner.getPartnerId()+"");
    		  
    	  }
    	}, 0, 5, TimeUnit.SECONDS);
    }
    
    static void messagingSchedule(final Context context) {
    	messagingScheduleExecutor = Executors.newScheduledThreadPool(5);

    	// This schedule a runnable task every 1 seconds
    	messagingScheduleExecutor.scheduleAtFixedRate(new Runnable() {
    	  public void run() {
    		  Log.d(TAG, "mSCHEDULE");
    		  
    		  new RestThreadTask(SCTypes.RECEIVE_MSG, context).execute();
    		  
    		  sendMessage(context);
    		  
      		  if(SCState.getState() < SCState.LOGGED_IN)
      		  {
      			  disconnect(context);
      		  }
    		  
    		  if(!Messaging.isActivityVisible() && SCState.getMsgState()==SCState.MSG_RECEIVED) 
    		  {
    			  showNotification( Partner.getPartnerAlias(), context );
    			  SCState.setMsgState(SCState.MSG_DEFAULT);
    		  }
    	  }
    	}, 0, 1, TimeUnit.SECONDS);
    }
	
	 // Send an Intent with an action named "custom-event-name". The Intent sent should 
	 // be received by the ReceiverActivity.
	 private static void sendMessage(Context context) {
	   Intent intent = new Intent("custom-event-name");
	   // You can also include some extra data.
	   //intent.putExtra("message", "This is my message!");
	   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	 }
	 
	 // Send an Intent with an action named "custom-event-name". The Intent sent should 
	 // be received by the ReceiverActivity.
	 private static void disconnect(Context context) {
	   Intent intent = new Intent("custom-event-name2");
	   // You can also include some extra data.
	   //intent.putExtra("message", "This is my message!");
	   LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	 }

	/**
	 * Show a notification while this service is running.
	 * @param msg 
	 **/
	private static void showNotification(String alias, Context context) 
	{       
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(context, Messaging.class);
	    PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);

	    // Build notification
	    Notification noti = new Notification.Builder(context)
	        .setContentTitle("SecChat - Unread Message")
	        .setContentText("Message from: " + alias)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent).build();
	    NotificationManager mNM = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    mNM.notify(0, noti);
    }

	/*
	public String authenticateUser(String usernameText, String passwordText) 
	{
		this.username = usernameText;
		this.password = passwordText;	
		
		this.authenticatedUser = false;
		
		String result = this.getFriendList(); //socketOperator.sendHttpRequest(getAuthenticateUserParams(username, password));
		if (result != null);//WICHTIG && !result.equals(Login.AUTHENTICATION_FAILED)) 
		{			
			// if user is authenticated then return string from server is not equal to AUTHENTICATION_FAILED
			this.authenticatedUser = true;
			rawFriendList = result;
			
			Intent i = new Intent(FRIEND_LIST_UPDATED);					
			//i.putExtra(FriendInfo.FRIEND_LIST, rawFriendList);
			sendBroadcast(i);
			
			timer.schedule(new TimerTask()
			{			
				public void run() 
				{
					try {					
						//rawFriendList = IMService.this.getFriendList();
						// sending friend list 
						Intent i = new Intent(FRIEND_LIST_UPDATED);
						String tmp = BGService.this.getFriendList();
						if (tmp != null) {
							//i.putExtra(FriendInfo.FRIEND_LIST, tmp);
							sendBroadcast(i);	
							Log.i("friend list broadcast sent ", "");
						}
						else {
							Log.i("friend list returned null", "");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}					
				}			
			}, UPDATE_TIME_PERIOD, UPDATE_TIME_PERIOD);
		}
		
		return result;		
	}
	*/
}