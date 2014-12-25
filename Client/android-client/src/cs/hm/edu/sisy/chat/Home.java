package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.services.BGService;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;
import cs.hm.edu.sisy.chat.tools.Common;
import cs.hm.edu.sisy.chat.tools.TestData;

public class Home extends Activity {
  
    private TextView yourAlias;
    private TextView yourPIN;
    private TextView yourID;
        
	private Button hintButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        
        yourAlias = (TextView) findViewById(R.id.txtYourAlias);
        yourPIN = (TextView) findViewById(R.id.txtYourPin);
        yourID = (TextView) findViewById(R.id.txtYourId);
        
        hintButton = (Button) findViewById(R.id.btnHint);
        
        hintButton.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View arg0) 
			{
        		new AlertDialog.Builder(Home.this)
	        	    .setTitle("Hint")
	        	    .setMessage("We place great importance on anonymity, so don't score an own goal by telling you real name or your private matters in chat!")
	        	    /*.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) { 
	        	            // continue with delete
	        	        }
	        	     })
	        	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        	        public void onClick(DialogInterface dialog, int which) { 
	        	            // do nothing
	        	        }
	        	     })*/
	        	    .setIcon(android.R.drawable.ic_dialog_alert)
	        	     .show();
			}
        });
        
        //Just for testing.
        TestData.printInterestingData(this);
        
        updateDate();
        
        startService(new Intent(this, BGService.class));
        
        disconnect();
        
         /*
		// Start and bind the BGService 
		startService(new Intent(Home.this,  BGService.class));

		if (imService == null) {
			showDialog(NOT_CONNECTED_TO_SERVICE);
			return;
		}
		else if (imService.isNetworkConnected() == false)
		{
			showDialog(NOT_CONNECTED_TO_NETWORK);
			
		}
		else if (alias.length() > 0)
		{
			
			Thread loginThread = new Thread(){
				private Handler handler = new Handler();
				@Override
				public void run() {
					String result = imService.authenticateUser(alias.getText().toString());
					if (result == null || result.equals(AUTHENTICATION_FAILED)) 
					{
						//Authenticatin failed, inform the user
						handler.post(new Runnable(){
							public void run() {										
								showDialog(MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT);
							}									
						});
												
					}
					else {
					
					    //if result not equal to authentication failed, result is equal to friend list of the user
						handler.post(new Runnable(){
							public void run() {										
								Intent i = new Intent(Login.this, Home.class);												
								//i.putExtra(FRIEND_LIST, result);						
								startActivity(i);	
								Login.this.finish();
							}									
						});
						
					}
					
				}
			};
			loginThread.start();
			
		}
		else {
		    //Username or Password is not filled, alert the user
			showDialog(FILL_BOTH_USERNAME_AND_PASSWORD);
		}		
  		*/
    }
    
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuPrivChat:
          Intent createPrivChat = new Intent(Home.this, Chat.class);
          startActivity(createPrivChat);
          return true;
        case R.id.menuPubChat:
          //Intent createPubChat = new Intent(Home.this, Chat.class);
          //startActivity(createPubChat);
          //Toast.makeText(this, "@string/menuChat", Toast.LENGTH_SHORT).show();
          return true;
        case R.id.menuOptions:
          Intent settings = new Intent(Home.this, Settings.class);
          startActivity(settings);
          return true; 
        case R.id.menuDebug:
            Intent debug = new Intent(Home.this, Debug.class);
            startActivity(debug);
            return true;        
        default:
          return super.onOptionsItemSelected(item);
        } 
    }
    
    @Override
    protected void onPause() 
    {
	  //unbindService(mConnection);
    	
      updateDate();
      
      super.onPause();
    }

    @Override
    protected void onResume() 
    {   
	  //bindService(new Intent(Home.this, BGService.class), mConnection , Context.BIND_AUTO_CREATE);
    	
      updateDate();
            
      super.onResume();
    }
    
    @Override
    public void onDestroy() 
    {
    	//new RestThreadTask(SCTypes.LOGOUT, Home.this).execute(); 
    	
    	LocalBroadcastManager.getInstance(this).unregisterReceiver(discReceiver);
    	
    	super.onDestroy();
    }
    
    private void updateDate()
    {
      if( !Common.isPinCurrent( SharedPrefs.getStoragedPinDate(this), Common.getCurrentDate() ) )
      {
        SharedPrefs.savePIN( this, PinHashGenerator.generatePIN() );
        SharedPrefs.saveStoragedPinDate( this, Common.getCurrentDate() );
      }
      
      yourAlias.setText( SharedPrefs.getAlias(this) +"" );
      yourPIN.setText( SharedPrefs.getPIN(this) +"" );
      yourID.setText( SharedPrefs.getID(this) +"" );
    }
    
    // Our handler for received Intents. This will be called whenever an Intent
    // with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver discReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        // Get extra data included in the Intent
        //String message = intent.getStringExtra("message");
     	 
 	   finish();
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
}