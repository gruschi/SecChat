package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import cs.hm.edu.sisy.chat.services.RestThreadTask;
import cs.hm.edu.sisy.chat.services.RestWrapper;
import cs.hm.edu.sisy.chat.tools.Misc;
import cs.hm.edu.sisy.chat.tools.TestData;
import cs.hm.edu.sisy.chat.types.STATUS;
import cs.hm.edu.sisy.chat.types.TYPES;

public class Login extends Activity {	

	private EditText alias;
	private Button registerButton;
	private Button loginButton;
	
    public static final int SIGN_UP_ID = Menu.FIRST;
    public static final int EXIT_APP_ID = Menu.FIRST + 1;
    
    private int currentState = -1;
    private boolean timerIsStarted = false;
    
    //handler - background thread which acts as state machine handler
	final Handler handler = new Handler();
	final Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
			   
			      Login.this.runOnUiThread(new Runnable() {
			    	  public void run() {
			    		  TestData.printInterestingData(Login.this);
			    		  		    		  
			    		  if( STATUS.getState() == STATUS.REGISTERED || STATUS.getState() == STATUS.NOT_LOGGED_IN )
			    		        loginButton.setVisibility(View.VISIBLE);
			    		  
						  if( STATUS.getState() == STATUS.LOGGED_IN ) {
								redirectToHome();
								currentState = STATUS.getState();
						  }
			    	  }
			    	});
			      
		      handler.postDelayed(runnable, 2000);
		   }
		};

		
    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    

        //Start and bind the  imService 
    	//startService(new Intent(Login.this,  IMService.class));
    	
    	//Already loggedin or at least registred?
    	if( RestWrapper.isRegistered(this) )
        	if( RestWrapper.isLoggedIn(this) )
        		redirectToHome();
               
        setContentView(R.layout.login_screen);
        setTitle("Login");
        
        registerButton = (Button) findViewById(R.id.registerButton);
        loginButton = (Button) findViewById(R.id.loginButton);
        alias = (EditText) findViewById(R.id.loginAlias);
        
        loginButton.setVisibility(View.INVISIBLE);
       
        registerButton.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View arg0) 
			{
				if (alias.getText().toString().matches("")) 
				{
					Misc.doToast(Login.this, "Please write down an alias first!");
				}
				else
				{
					Thread registerThread = new Thread(runnable);
					registerThread.start();
				      
						if( STATUS.getState() == STATUS.NOT_REGISTERED && currentState != STATUS.getState() ) {
							if(RestWrapper.registerNeeded(Login.this, alias.getText().toString()))
								new RestThreadTask(TYPES.REGISTER, (Context) Login.this).execute(alias.getText().toString());
							currentState = STATUS.getState();
						}	
						
						//TODO: Timer überhaupt nötig? Und falls ja: Dann auch für Login?
						if(!timerIsStarted) {
							timerIsStarted = true;
							
							 new CountDownTimer(10000, 1000) {
	
							     public void onTick(long millisUntilFinished) {
							         //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
							    	 Misc.doToast(Login.this, STATUS.getState() +"");
							     }
	
							     public void onFinish() {
							    	if(STATUS.getState() == STATUS.NOT_REGISTERED)
							    		STATUS.setState(STATUS.TIMED_OUT);
									currentState = -1;
									timerIsStarted = false;
									Misc.doToast(Login.this, "Timeout!");
							     }
							  }.start();
						}
				}
			}
        });
        
        registerButton.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View arg0) 
			{
				if( STATUS.getState() == STATUS.REGISTERED || STATUS.getState() == STATUS.NOT_LOGGED_IN ) {
					if (RestWrapper.loginNeeded(Login.this))
						new RestThreadTask(TYPES.LOGIN, (Context) Login.this).execute();
					currentState = STATUS.getState();
				}
			}
        });
        		
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
//							String result = imService.authenticateUser(alias.getText().toString());
//							if (result == null || result.equals(AUTHENTICATION_FAILED)) 
//							{
//								//Authenticatin failed, inform the user
//								handler.post(new Runnable(){
//									public void run() {										
//										showDialog(MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT);
//									}									
//								});
//														
//							}
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
	protected void onPause() 
	{
		//unbindService(mConnection);
		
		super.onPause();
		
		handler.removeCallbacks(runnable);
	}

	@Override
	protected void onResume() 
	{		
		//bindService(new Intent(Login.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);

		super.onResume();
		
		handler.removeCallbacks(runnable);
	}
	
	@Override
	protected void onDestroy() 
	{		
		super.onDestroy();
		
		handler.removeCallbacks(runnable);
		
		Login.this.finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);
		
		 menu.add(0, SIGN_UP_ID, 0, R.string.index);
		 menu.add(0, EXIT_APP_ID, 0, R.string.exit_application);

		return result;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    
		switch(item.getItemId()) 
	    {
	    	case SIGN_UP_ID:
	    		Intent i = new Intent(Login.this, Home.class);
	    		startActivity(i);
	    		return true;
	    }
	    
	    return super.onMenuItemSelected(featureId, item);
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
    
    private void redirectToHome() {
		Intent i = new Intent(Login.this, Home.class);												
		startActivity(i);	
		onDestroy();
	}
    
}