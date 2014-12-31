package edu.hm.cs.sisy.chat;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import edu.hm.cs.sisy.communication.RestThreadTask;
import edu.hm.cs.sisy.enums.SCTypes;
import edu.hm.cs.sisy.objects.Partner;
import edu.hm.cs.sisy.storage.SharedPrefs;
import edu.hm.cs.sisy.tools.Common;
import edu.hm.cs.sisy.tools.DiscussArrayAdapter;
import edu.hm.cs.sisy.tools.OneComment;

public class Messaging extends Activity {

	private static final int MESSAGE_CANNOT_BE_SENT = 0;
	private EditText messageText;
	private ListView messageHistoryText;
	private Button sendMessageButton;
	
	ArrayList<String> listItems;
	
	private DiscussArrayAdapter adapter;
	
    private static boolean activityVisible;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.messaging_screen); //messaging_screen);
				
		messageHistoryText = (ListView) findViewById(R.id.messageHistory);
		
		messageText = (EditText) findViewById(R.id.newMessage);
		
		messageText.requestFocus();			
		
		sendMessageButton = (Button) findViewById(R.id.sendMessageButton);
		
		setTitle("Messaging with " + Partner.getPartnerAlias());
		
		sendMessageButton.setOnClickListener(new OnClickListener(){
			CharSequence message;
			public void onClick(View arg0) {
				message = messageText.getText();
				if (message.length()>0) 
				{
					adapter.add(new OneComment(false, SharedPrefs.getAlias(Messaging.this), message.toString()));
								
					messageText.setText("");
					Thread thread = new Thread(){					
						public void run() {
							new RestThreadTask(SCTypes.SEND_MSG, Messaging.this).execute(message.toString()); 
							
							//TODO timer "message sent" or "message not sent" via msgState
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
		
		adapter = new DiscussArrayAdapter(getApplicationContext(), R.layout.listitem_discuss);
		
		messageHistoryText.setAdapter(adapter);
		
		receiveMessages();
		disconnect() ;
		finishMessaging();
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
		//unregisterReceiver(messageReceiver);
		//unbindService(mConnection);

		Messaging.activityPaused();
	}

	@Override
	protected void onResume() 
	{		
		super.onResume();
		//bindService(new Intent(Messaging.this, BGService.class), mConnection , Context.BIND_AUTO_CREATE);
		
		//registerReceiver(messageReceiver, i);
		
		Messaging.activityResumed();
	}
	
	private void appendToMessageHistory(String alias, String message) {
		if (alias != null && message != null) 
		{
			adapter.add(new OneComment(true, alias, message));
			
			//adapter2.notifyDataSetChanged();
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
	    		backButtonHandler();
	    		return true;
	    }
	    
	    return super.onMenuItemSelected(featureId, item);
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
      
      
   // Our handler for received Intents. This will be called whenever an Intent
   // with an action named "custom-event-name" is broadcasted.
   private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
     @Override
     public void onReceive(Context context, Intent intent) {
       // Get extra data included in the Intent
       //String message = intent.getStringExtra("message");
    	 
	   if (Partner.getPartnerNewMsg() != "")
		{
			appendToMessageHistory(Partner.getPartnerAlias(), Partner.getPartnerNewMsg());
			
			Partner.resetPartnerNewMsg();
		}
     }
   };
   
	private void receiveMessages() 
	{
	  // Register to receive messages.
	  // We are registering an observer (mMessageReceiver) to receive Intents
	  // with actions named "custom-event-name".
	  LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
	      new IntentFilter("custom-event-name"));
	}
   
   // Our handler for received Intents. This will be called whenever an Intent
   // with an action named "custom-event-name" is broadcasted.
   private BroadcastReceiver discReceiver = new BroadcastReceiver() {
     @Override
     public void onReceive(Context context, Intent intent) {
       // Get extra data included in the Intent
       //String message = intent.getStringExtra("message");
    	 
	   exitChat();
	   messageHistoryText = null;
	   adapter = null;
	   //finish();
     }
   };
   
   private BroadcastReceiver finishReceiver = new BroadcastReceiver() {
     @Override
     public void onReceive(Context context, Intent intent) {
    	 
	   exitChat();
	   messageHistoryText = null;
	   adapter = null;
	   //finish();
     }
   };
   
	private void disconnect() 
	{
	  // Register to receive messages.
	  // We are registering an observer (mMessageReceiver) to receive Intents
	  // with actions named "custom-event-name2".
	  LocalBroadcastManager.getInstance(this).registerReceiver(discReceiver,
	      new IntentFilter("custom-event-name2"));
	}
	
	private void finishMessaging() 
	{
	  LocalBroadcastManager.getInstance(this).registerReceiver(finishReceiver,
	      new IntentFilter("finish_activity"));
	}
   
   @Override
   protected void onDestroy() {
     // Unregister since the activity is about to be closed.
     LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
     LocalBroadcastManager.getInstance(this).unregisterReceiver(discReceiver);
     LocalBroadcastManager.getInstance(this).unregisterReceiver(finishReceiver);
     super.onDestroy();
   }
      
	private void exitChat() {
		Common.resetClient(Messaging.this);
	}
}
