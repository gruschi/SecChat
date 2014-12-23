package cs.hm.edu.sisy.chat.services;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.util.Log;
import cs.hm.edu.sisy.chat.Messaging;
import cs.hm.edu.sisy.chat.R;
import cs.hm.edu.sisy.chat.tools.Misc;
import cs.hm.edu.sisy.chat.types.STATUS;
import cs.hm.edu.sisy.chat.types.TYPES;

public class BGService extends Service {
//	private NotificationManager mNM;
	
	public static final String TAKE_MESSAGE = "Take_Message";
	public static final String FRIEND_LIST_UPDATED = "Take Friend List";
	public ConnectivityManager conManager = null; 

    // Check interval: every 24 hours
    //private static long UPDATES_CHECK_INTERVAL = 24 * 60 * 60 * 1000;
    private static long UPDATES_CHECK_INTERVAL = 5000;

	private Timer timer;
	
	private NotificationManager mNM;
	private static ScheduledExecutorService waitingScheduleExecutor;
	private static ScheduledExecutorService messagingScheduleExecutor;
	
    private static final String TAG = "BGService";
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
 
    @Override
    public void onCreate() {
    	Misc.doToast(this, "Congrats! MyService Created");
        Log.d(TAG, "onCreate");
    }
 
    @Override
    public void onStart(Intent intent, int startId) {
    	Misc.doToast(this, "MyService Started");
        Log.d(TAG, "onStart");
        
    //Note: You can start a new thread and use it for long background processing from here.
        
        //mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

       // Display a notification about us starting.  We put an icon in the status bar.
   	//conManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
   	
   	showNotification("Chris"); //Partner.alias
   	
   	if(STATUS.getState() == STATUS.CONNECTED_TO_CHAT)
   		messagingSchedule(BGService.this);
   	else 
   		waitingSchedule(BGService.this);
   	
   	
   	// Timer is used to take the friendList info every UPDATE_TIME_PERIOD;
		timer = new Timer();   
		
		/*
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
    	
        Misc.doToast(this, "MyService Stopped");
        Log.d(TAG, "onDestroy");
		super.onDestroy();
    }
	
    static void waitingSchedule(final Context context) {
    	waitingScheduleExecutor = Executors.newScheduledThreadPool(5);

    	// This schedule a runnable task every 5 seconds
    	waitingScheduleExecutor.scheduleAtFixedRate(new Runnable() {
    	  public void run() {
    		  Log.d(TAG, "wSCHEDULE");
    		  new RestThreadTask(TYPES.SERVICE, context).execute();
    		  
    		  //someone is calling me
    		   	if(STATUS.getState() == STATUS.LOGGED_IN)
    		   		//
    		   	else if(STATUS.getState() == STATUS.CONNECT_TO_CHAT_PENDING) //I called someone and waiting for response
    		   		//
    		  
    	  }
    	}, 0, 5, TimeUnit.SECONDS);
    }
    
    static void messagingSchedule(final Context context) {
    	messagingScheduleExecutor = Executors.newScheduledThreadPool(5);

    	// This schedule a runnable task every 1 seconds
    	messagingScheduleExecutor.scheduleAtFixedRate(new Runnable() {
    	  public void run() {
    		  Log.d(TAG, "mSCHEDULE");
    		  new RestThreadTask(TYPES.RECEIVE_MSG, context).execute();
    	  }
    	}, 0, 1, TimeUnit.SECONDS);
    }
	
	


/*
    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(R.string.local_service_started);

        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }
*/	

    /*
	@Override
	public IBinder onBind(Intent intent) 
	{
		return mBinder;
	}*/




	/**
	 * Show a notification while this service is running.
	 * @param msg 
	 **/
	private void showNotification(String alias) 
	{       
		//ActivityManager.getRunningAppProcesses()
		//http://stackoverflow.com/questions/2314969/how-to-determine-if-one-of-my-activities-is-in-the-foreground
		
	    // Prepare intent which is triggered if the
	    // notification is selected
	    Intent intent = new Intent(this, Messaging.class);
	    PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

	    // Build notification
	    // Actions are just fake
	    Notification noti = new Notification.Builder(this)
	        .setContentTitle("SecChat - Unread Message")
	        .setContentText("Message from: " + alias)
	        .setSmallIcon(R.drawable.ic_launcher)
	        .setContentIntent(pIntent).build();
	    mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	    // hide the notification after its selected
	    noti.flags |= Notification.FLAG_AUTO_CANCEL;

	    mNM.notify(0, noti);
    }

	public boolean sendMessage(String  username, String message) {
		//Partner friendInfo = Partner.getFriendInfo(username);
		String msg = null, IP = null;
		int port = 0;
		/*
		String IP = friendInfo.ip;
		//IP = "10.0.2.2";
		int port = Integer.parseInt(friendInfo.port);
		
		msg = FriendInfo.USERNAME +"=" + URLEncoder.encode(this.username) +
		 "&" + FriendInfo.USER_KEY + "=" + URLEncoder.encode(userKey) +
		 "&" + FriendInfo.MESSAGE + "=" + URLEncoder.encode(message) +
		 "&";
		*/
		return false; 
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

	public void messageReceived(String message) 
	{				
		String[] params = message.split("&");
		String username= new String();
		String userKey = new String();
		String msg = new String();
		/*
		for (int i = 0; i < params.length; i++) {
			String[] localpar = params[i].split("=");
			if (localpar[0].equals(FriendInfo.USERNAME)) {
				username = URLDecoder.decode(localpar[1]);
			}
			else if (localpar[0].equals(FriendInfo.USER_KEY)) {
				userKey = URLDecoder.decode(localpar[1]);
			}
			else if (localpar[0].equals(FriendInfo.MESSAGE)) {
				msg = URLDecoder.decode(localpar[1]);
			}			
		}
		Log.i("Message received in service", message);
		
		FriendInfo friend = FriendController.checkFriend(username, userKey);
		if ( friend != null)
		{			
			Intent i = new Intent(TAKE_MESSAGE);
		
			i.putExtra(FriendInfo.USERNAME, friend.userName);			
			i.putExtra(FriendInfo.MESSAGE, msg);			
			sendBroadcast(i);
			String activeFriend = FriendController.getActiveFriend();
			if (activeFriend == null || activeFriend.equals(username) == false) 
			{
				showNotification(username, msg);
			}
			Log.i("TAKE_MESSAGE broadcast sent by im service", "");
		}	
		*/
		
	}  

	public void exit() 
	{
		timer.cancel();
		this.stopSelf();
	}
}