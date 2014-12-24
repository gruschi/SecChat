package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import cs.hm.edu.sisy.chat.communication.RestThreadTask;
import cs.hm.edu.sisy.chat.enums.SCState;
import cs.hm.edu.sisy.chat.enums.SCTypes;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.objects.Partner;
import cs.hm.edu.sisy.chat.services.BGService;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;

public class Messaging extends Activity {

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	private EditText messageText;
	private EditText messageHistoryText;
	private Button sendMessageButton;
	
    private static boolean activityVisible;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		//TODO messageListArray + observerThread + decode on gettin/writin msg to array and encode on reading
		
		setContentView(R.layout.messaging_screen); //messaging_screen);
				
		messageHistoryText = (EditText) findViewById(R.id.messageHistory);
		
		messageText = (EditText) findViewById(R.id.message);
		
		messageText.requestFocus();			
		
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		
		Bundle extras = this.getIntent().getExtras();
		
		//setTitle("Messaging with " + friend.userName);
	
		
	//	EditText friendUserName = (EditText) findViewById(R.id.friendUserName);
	//	friendUserName.setText(friend.userName);
		
		/*
		if (msg != null) 
		{
			this.appendToMessageHistory(friend.userName , msg);
			((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel((friend.userName+msg).hashCode());
		}*/
		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			CharSequence message;
			public void onClick(View arg0) {
				message = messageText.getText();
				if (message.length()>0) 
				{
					appendToMessageHistory(SharedPrefs.getAlias(Messaging.this), message.toString());
								
					messageText.setText("");
					Thread thread = new Thread(){					
						public void run() {
							new RestThreadTask(SCTypes.SEND_MSG, Messaging.this).execute(Partner.getPartnerId()+"", message.toString()); 
							
							//TODO timer "message sent" or "message not sent"
							/*
							if (!imService.sendMessage(friend.userName, message.toString()))
							{
								
								handler.post(new Runnable(){	
	
									public void run() {
										showDialog(MESSAGE_CANNOT_BE_SENT);										
									}
									
								});
							}
							*/
						}						
					};
					thread.start();
										
				}
				
			}
		});
		messageText.setOnKeyListener(new OnKeyListener(){
			public boolean onKey(View v, int keyCode, KeyEvent event) 
			{
				if (keyCode == 66){
					sendMessageButton.performClick();
					return true;
				}
				return false;
			}
		});
		
		
		receiveMessages();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		int message = -1;
		switch (id)
		{
		case MESSAGE_CANNOT_BE_SENT:
			message = R.string.message_cannot_be_sent;
		break;
		}
		
		if (message == -1)
		{
			return null;
		}
		else
		{
			return new AlertDialog.Builder(Messaging.this)       
			.setMessage(message)
			.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					/* User clicked OK so do some stuff */
				}
			})        
			.create();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(messageReceiver);
		//unbindService(mConnection);
		
		//FriendController.setActiveFriend(null);
		Messaging.activityPaused();
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		//bindService(new Intent(Messaging.this, BGService.class), mConnection , Context.BIND_AUTO_CREATE);
				
		IntentFilter i = new IntentFilter();
		i.addAction(BGService.TAKE_MESSAGE);
		
		registerReceiver(messageReceiver, i);
		
		Messaging.activityResumed();
	}
	
	
	public class  MessageReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) 
		{	
			/*	
			Bundle extra = intent.getExtras();
			String username = extra.getString(FriendInfo.USERNAME);			
			String message = extra.getString(FriendInfo.MESSAGE);
			
			
			if (username != null && message != null)
			{
				if (friend.userName.equals(username)) {
					appendToMessageHistory(username, message);					
				}
				else {
					if (message.length() > 15) {
						message = message.substring(0, 15);
					}
					Toast.makeText(Messaging.this,  username + " says '"+
													message + "'",
													Toast.LENGTH_SHORT).show();		
				}
			}	*/		
		}
		
	};
	private MessageReceiver messageReceiver = new MessageReceiver();
	
	private void appendToMessageHistory(String username, String message) {
		if (username != null && message != null) {
			messageHistoryText.append(username + ":\n");								
			messageHistoryText.append(message + "\n");	
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);
		
		 menu.add(0, 0, 0, R.string.exit);

		return result;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    
		switch(item.getItemId()) 
	    {
	    	case 0:
	    		exitChat();
	    		return true;
	    }
	    
	    return super.onMenuItemSelected(featureId, item);
	}

	private void exitChat() {
		// TODO Auto-generated method stub
		// destroy objects chat-msg-log,  ...
		
		//change state from CONNECTED_TO_CHAT to
		SCState.setState(SCState.LOGGED_IN);
		
		//reset partner
		Partner.setPartnerAlias(null);
		Partner.setPartnerId(0);
		Partner.setPartnerPin(null);
		Partner.setPartnerPubKey(null);
		
		//reset chatSession
		SharedPrefs.resetChatSessionId(Messaging.this);
		
		finish();
	}
	
	@Override
	public void onBackPressed() {
		backButtonHandler();
	    return;
	}

	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
	    if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
	    	
	    	backButtonHandler()
	    }

	    return super.onKeyDown(keyCode, event);
	}
	*/
	
    public void backButtonHandler() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                Messaging.this);
        alertDialog.setTitle("Leave chat?");
        alertDialog.setMessage("Are you sure you want to leave the chat? Consider that you 're not able to come back to this chat session!");
        alertDialog.setIcon(R.drawable.ic_launcher);
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    	exitChat();
                    }
                });
        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
    
    public static boolean isActivityVisible() {
        return activityVisible;
      }  

      public static void activityResumed() {
        activityVisible = true;
      }

      public static void activityPaused() {
        activityVisible = false;
      }
      
      
	private void receiveMessages() {
		
	    AsyncTaskRunner runner = new AsyncTaskRunner();
	    String sleepTime = (1*1000)+"";
	    runner.execute(sleepTime);
		
		/*
		final Handler handler = new Handler();
		final Runnable runnable = new Runnable() {
			   @Override
			   public void run() {
				   
				    		  new RestThreadTask(SCTypes.RECEIVE_MSG, Messaging.this).execute(); 
				    		  
								if (Partner.getPartnerNewMsg() != "")
								{
									handler.post(new Runnable(){

										@Override
										public void run() {
											appendToMessageHistory(Partner.getPartnerAlias(), Partner.getPartnerNewMsg());
										}	
									});
								}
				      
			      handler.postDelayed(runnable, 2000);
			   }
			};*/
	}
      
      
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {

    	  private String resp;

    	  @Override
    	  protected String doInBackground(String... params) {

    	   try {
    		   //TODO: add this to BGService?
    		   if (Partner.getPartnerNewMsg() != "")
				{
					appendToMessageHistory(Partner.getPartnerAlias(), Partner.getPartnerNewMsg());
					
					Partner.resetPartnerNewMsg();
				}
    		   
	    	    int time = Integer.parseInt(params[0]);    
	    	    Thread.sleep(time);
    	    
    	   } catch (InterruptedException e) {
    	    e.printStackTrace();
    	    resp = e.getMessage();
    	   } catch (Exception e) {
    	    e.printStackTrace();
    	    resp = e.getMessage();
    	   }
    	   return resp;
    	  }
    }
}
