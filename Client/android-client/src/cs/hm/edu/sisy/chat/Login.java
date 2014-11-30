package cs.hm.edu.sisy.chat;

import java.security.NoSuchAlgorithmException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cs.hm.edu.sisy.chat.generators.KeyGenerator;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.interfaces.IAppManager;
import cs.hm.edu.sisy.chat.services.IMService;
import cs.hm.edu.sisy.chat.services.RestService;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.Misc;

public class Login extends Activity {	

    protected static final int NOT_CONNECTED_TO_SERVICE = 0;
	protected static final int FILL_BOTH_USERNAME_AND_PASSWORD = 1;
	public static final String AUTHENTICATION_FAILED = "0";
	protected static final int MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT = 2 ;
	protected static final int NOT_CONNECTED_TO_NETWORK = 3;
	private EditText alias;
	private Button loginButton;
    private IAppManager imService;
    public static final int SIGN_UP_ID = Menu.FIRST;
    public static final int EXIT_APP_ID = Menu.FIRST + 1;
    
    private int state;
    
    /*
	private static final int FILL_ALL_FIELDS = 0;
	protected static final int TYPE_SAME_PASSWORD_IN_PASSWORD_FIELDS = 1;
	private static final int SIGN_UP_FAILED = 2;
	private static final int SIGN_UP_USERNAME_CRASHED = 3;
	private static final int SIGN_UP_SUCCESSFULL = 4;
	protected static final int USERNAME_AND_PASSWORD_LENGTH_SHORT = 5;
	
	private static final String SERVER_RES_SIGN_UP_FAILED = "0";
	private static final String SERVER_RES_RES_SIGN_UP_SUCCESFULL = "1";
	private static final String SERVER_RES_SIGN_UP_USERNAME_CRASHED = "2";
	*/


    /** Called when the activity is first created. */	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    

        //Start and bind the  imService 
    	startService(new Intent(Login.this,  IMService.class));
    	
    	//Already loggedin or at least registred
    	if( isRegistred() )
        	if( isLoggedIn() )
        		redirectToHome();
    	//TODO if registred, but not logged in, build state machine
               
        setContentView(R.layout.login_screen);
        setTitle("Login");
        
        loginButton = (Button) findViewById(R.id.loginButton);
        alias = (EditText) findViewById(R.id.loginAlias);
         
        loginButton.setOnClickListener(new OnClickListener(){
			@SuppressWarnings("deprecation")
			public void onClick(View arg0) 
			{
				
				
				if (alias.getText() == null) 
				{
					showDialog(FILL_BOTH_USERNAME_AND_PASSWORD);
				}
				else
				{
					if( isRegistred() || register() )
						if( login() )
							redirectToHome();
				}
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
        });
               
    }
    
	@Override
    protected Dialog onCreateDialog(int id) 
    {    	
    	int message = -1;    	
    	switch (id) 
    	{
    		case NOT_CONNECTED_TO_SERVICE:
    			message = R.string.not_connected_to_service;			
    			break;
    		case FILL_BOTH_USERNAME_AND_PASSWORD:
    			message = R.string.fill_both_username_and_password;
    			break;
    		case MAKE_SURE_USERNAME_AND_PASSWORD_CORRECT:
    			message = R.string.make_sure_username_and_password_correct;
    			break;
    		case NOT_CONNECTED_TO_NETWORK:
    			message = R.string.not_connected_to_network;
    			break;
    		default:
    			break;
    	}
    	
    	if (message == -1) 
    	{
    		return null;
    	}
    	else 
    	{
    		return new AlertDialog.Builder(Login.this)       
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
	protected void onPause() 
	{
		unbindService(mConnection);
		
		super.onPause();
	}

	@Override
	protected void onResume() 
	{		
		bindService(new Intent(Login.this, IMService.class), mConnection , Context.BIND_AUTO_CREATE);
	    		
		super.onResume();
	}
	
    @Override
    public void onDestroy() 
    {
        super.onDestroy();

        RestService.logoutUser(this);
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
	
    public boolean isRegistred()
    {
    	return Storage.getID(this) != 0;
    }
    
    public boolean isLoggedIn()
    {
    	return Storage.getSession(this) != null;
    }  
    
    public boolean register()
    {
    	boolean status = false;
    	KeyGenerator kg = new KeyGenerator(this);
    	
    	if( kg.getPublicKey() == null )
    		kg.generateKeys();
    	
    	try 
    	{
    		Storage.saveAlias( this, alias.getText().toString() );
			Storage.saveHash( this, PinHashGenerator.createHash() );
    	
	    	if( isRegistred() )
	    	{
	    		status = RestService.registerUser( alias.getText().toString(), PinHashGenerator.createHash(), this );
	    	}
		} 
    	catch (NoSuchAlgorithmException e) 
    	{
			e.printStackTrace();
		}
    	
    	return status;
    }
    
    public boolean login()
    {
    	boolean status = false;
    	
        if( !Misc.isPinCurrent( Storage.getStoragedPinDate(this), Misc.getCurrentDate() ) )
        {
          Storage.savePIN( this, PinHashGenerator.generatePIN() );
          Storage.saveStoragedPinDate( this, Misc.getCurrentDate() );
        }
    	
    	if( Storage.getID(this) != 0 && Storage.getSession(this) == null )
    	{
    		status = RestService.loginUser( Storage.getAlias(this), Storage.getHash(this), this);
    	}
    	
    	return status;
    }
    
    public void connect2Chat() 
    {
    	boolean status = false;
    	
    	//check oncreate in home or service
    	if( Storage.getID(this) != 0 && Storage.getSession(this) != null )//&& Storage.getChatSession(this) != null ) //oder callback funktion in REST-Service von IncomingChat
    	{
    		//go to this chat, service überschrieb im hintergrund das globale partner-objekt mit alias, pubkey etc.
    	}
    	else {
    		//go to home
    	}
    }
    
    private void redirectToHome() {
		Intent i = new Intent(Login.this, Home.class);												
		startActivity(i);	
		Login.this.finish();
	}
    
}