package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
	
    /*private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
        	bgService = ((IMService.IMBinder)service).getService();  
            
            if (bgService.isUserAuthenticated() == true)
            {
            	Intent i = new Intent(Home.this, BGService.class);
            	Home.this.startService(i);
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
        	imService = null;
            Toast.makeText(Login.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };*/

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
        
        //TODO: Just for testing.
        TestData.printInterestingData(this);
        
        updateDate();
        
        startService(new Intent(this, BGService.class));
        
        //Start and bind the BGService 
     	//startService(new Intent(Home.this,  BGService.class));
         
         /*
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
// 							String result = imService.authenticateUser(alias.getText().toString());
// 							if (result == null || result.equals(AUTHENTICATION_FAILED)) 
// 							{
// 								//Authenticatin failed, inform the user
// 								handler.post(new Runnable(){
// 									public void run() {										
// 										showDialog(MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT);
// 									}									
// 								});
// 														
// 							}
 							//else {
 							
     						    //if result not equal to authentication failed, result is equal to friend list of the user
 								handler.post(new Runnable(){
 									public void run() {										
 										Intent i = new Intent(Login.this, Home.class);												
 										//i.putExtra(FRIEND_LIST, result);						
 										startActivity(i);	
 										Login.this.finish();
 									}									
 								});
 								
 							//}
 							
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
          //Toast.makeText(this, "@string/menuChat", Toast.LENGTH_SHORT).show();
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
        super.onDestroy();

        //RestService.logoutUser(this);
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
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
	/*
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            imService = ((IMService.IMBinder)service).getService();  
            
            if (imService.isUserAuthenticated() == true)
            {
            	Intent i = new Intent(Login.this, Home.class);																
				startActivity(i);
				Login.this.finish();
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
        	imService = null;
            Toast.makeText(Login.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };
    
    */
    /*
	private ServiceConnection mConnection = new ServiceConnection() {
        

		public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            imService = ((IMService.IMBinder)service).getService();  
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
        	imService = null;
            Toast.makeText(SignUp.this, R.string.local_service_stopped,
                    Toast.LENGTH_SHORT).show();
        }
    };*/
    
	/* 
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);    

	    
 
	        setContentView(R.layout.sign_up_screen);
	        setTitle("Sign up");
	        
	        Button signUpButton = (Button) findViewById(R.id.signUp);
	        Button cancelButton = (Button) findViewById(R.id.cancel_signUp);
	        usernameText = (EditText) findViewById(R.id.userName);
	        passwordText = (EditText) findViewById(R.id.password);  
	        passwordAgainText = (EditText) findViewById(R.id.passwordAgain);  
	        eMailText = (EditText) findViewById(R.id.email);
	        
	        signUpButton.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) 
				{						
					if (usernameText.length() > 0 &&		
						passwordText.length() 
						> 0 && 
						passwordAgainText.length() > 0 &&
						eMailText.length() > 0
						)
					{
						if (passwordText.getText().toString().equals(passwordAgainText.getText().toString())){
						
							if (usernameText.length() >= 5 && passwordText.length() >= 5) {
							
									Thread thread = new Thread(){
										String result = new String();
										@Override
										public void run() {
											result = imService.signUpUser(usernameText.getText().toString(), 
													passwordText.getText().toString(), 
													eMailText.getText().toString());
		
											handler.post(new Runnable(){
		
												public void run() {
													if (result.equals(SERVER_RES_RES_SIGN_UP_SUCCESFULL)) {
														showDialog(SIGN_UP_SUCCESSFULL);
													}
													else if (result.equals(SERVER_RES_SIGN_UP_USERNAME_CRASHED)){
														showDialog(SIGN_UP_USERNAME_CRASHED);
													}
													else  //if (result.equals(SERVER_RES_SIGN_UP_FAILED)) 
													{
														showDialog(SIGN_UP_FAILED);
													}			
												}
		
											});
										}
		
									};
									thread.start();
							}
							else{
								showDialog(USERNAME_AND_PASSWORD_LENGTH_SHORT);
							}							
						}
						else {
							showDialog(TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS);
						}
						
					}
					else {
						showDialog(FILL_ALL_FIELDS);
						
					}				
				}       	
	        });
	        
	        cancelButton.setOnClickListener(new OnClickListener(){
				public void onClick(View arg0) 
				{						
					finish();					
				}	        	
	        });
	        
	    }
	
	
	protected Dialog onCreateDialog(int id) 
	{    	
		  	
		switch (id) 
		{
			case TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS:			
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_type_same_password_in_password_fields)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// User clicked OK so do some stuff
					}
				})        
				.create();			
			case FILL_ALL_FIELDS:				
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_fill_all_fields)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// User clicked OK so do some stuff
					}
				})        
				.create();
			case SIGN_UP_FAILED:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_failed)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// User clicked OK so do some stuff
					}
				})        
				.create();
			case SIGN_UP_USERNAME_CRASHED:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_username_crashed)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// User clicked OK so do some stuff
					}
				})        
				.create();
			case SIGN_UP_SUCCESSFULL:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.signup_successfull)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}
				})        
				.create();	
			case USERNAME_AND_PASSWORD_LENGTH_SHORT:
				return new AlertDialog.Builder(SignUp.this)       
				.setMessage(R.string.username_and_password_length_short)
				.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// User clicked OK so do some stuff
					}
				})        
				.create();
			default:
				return null;
				
		}
	}
	
	@Override
	protected void onResume() {
		bindService(new Intent(SignUp.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);
		   
		super.onResume();
	}
	
	@Override
	protected void onPause() 
	{
		unbindService(mConnection);
		super.onPause();
	}
	*/
    
}