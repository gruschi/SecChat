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
import cs.hm.edu.sisy.chat.communication.RestThreadTask;
import cs.hm.edu.sisy.chat.communication.RestWrapper;
import cs.hm.edu.sisy.chat.enums.State;
import cs.hm.edu.sisy.chat.enums.Types;
import cs.hm.edu.sisy.chat.tools.Common;

public class Login extends Activity {	

	private EditText alias;
	private Button registerButton;
	private Button loginButton;
	
    public static final int SIGN_UP_ID = Menu.FIRST;
    public static final int EXIT_APP_ID = Menu.FIRST + 1;
    
    private boolean timerIsStarted = false;
    
    //handler - background thread which acts as state machine handler
	final Handler handler = new Handler();
	final Runnable runnable = new Runnable() {
		   @Override
		   public void run() {
			   
			      Login.this.runOnUiThread(new Runnable() {
			    	  public void run() {
			    		  //TestData.printInterestingData(Login.this);
			    		  		    		  
			    		  if( State.getState() == State.REGISTERED || State.getState() == State.NOT_LOGGED_IN )
			    		  {
			    		        loginButton.setVisibility(View.VISIBLE);
			    		        registerButton.setEnabled(false);
			    		        alias.setEnabled(false);
			    		  }
			    		  
						  if( State.getState() == State.LOGGED_IN ) {
								redirectToHome();
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
					Common.doToast(Login.this, "Please write down an alias first!");
				}
				else
				{
					Thread registerThread = new Thread(runnable);
					registerThread.start();
					
					if(State.getState() == State.TIMED_OUT)
						State.setState(State.NOT_REGISTERED);
				      
					if( State.getState() == State.NOT_REGISTERED ) {
						if(RestWrapper.registerNeeded(Login.this, alias.getText().toString()))
							new RestThreadTask(Types.REGISTER, (Context) Login.this).execute(alias.getText().toString());
					}	
						
					//TODO: Timer �berhaupt n�tig?
					if(!timerIsStarted) {
						timerIsStarted = true;
						
						 new CountDownTimer(10000, 1000) {

						     public void onTick(long millisUntilFinished) {
						         //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
						    	 //Misc.doToast(Login.this, STATUS.getStateMessage() +"");
						     }

						     public void onFinish() {
						    	if(State.getState() == State.NOT_REGISTERED)
						    	{
						    		State.setState(State.TIMED_OUT);
						    		Common.doToast(Login.this, State.getStateMessage() +"");
						    	}
								timerIsStarted = false;
						     }
						  }.start();
					}
				}
			}
        });
        
        loginButton.setOnClickListener(new OnClickListener(){
        	@Override
			public void onClick(View arg0) 
			{
				if( State.getState() == State.REGISTERED || State.getState() == State.NOT_LOGGED_IN ) {
					if (RestWrapper.loginNeeded(Login.this))
						new RestThreadTask(Types.LOGIN, (Context) Login.this).execute();
				}
			}
        });
    }
    
	@Override
	protected void onPause() 
	{
    	super.onPause();
		
		handler.removeCallbacks(runnable);
	}

	@Override
	protected void onResume() 
	{		
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
    
    private void redirectToHome() {
		Intent i = new Intent(Login.this, Home.class);												
		startActivity(i);	
		Login.this.onDestroy();
	}
    
}