package cs.hm.edu.sisy.chat;

import cs.hm.edu.sisy.chat.communication.RestThreadTask;
import cs.hm.edu.sisy.chat.enums.SCPartner;
import cs.hm.edu.sisy.chat.enums.SCState;
import cs.hm.edu.sisy.chat.enums.SCTypes;
import cs.hm.edu.sisy.chat.objects.Partner;
import cs.hm.edu.sisy.chat.services.BGService;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
 
public class Debug extends Activity {
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.debug_screen);
    }
 
    //start the service
    public void onClickStartServie(View V)
    {
        //start the service from here //MyService is your service class name
        startService(new Intent(this, BGService.class));
    }
    //Stop the started service
    public void onClickStopService(View V)
    {
        //Stop the running service from here//MyService is your service class name
        //Service will only stop if it is already running.
        stopService(new Intent(this, BGService.class));
    }
    
    //reset client
    public void resetAll(View V)
    {
		//change state from CONNECTED_TO_CHAT to
		SCState.setState(SCState.LOGGED_IN, Debug.this);
		
		//exit server connection to other peer
		new RestThreadTask(SCTypes.DESTROY_CHAT_SESSION, Debug.this).execute();
		
		//reset partner-object
		Partner.setPartnerAlias(null);
		Partner.setPartnerId(0);
		Partner.setPartnerPin(null);
		Partner.setPartnerPubKey(null);
		Partner.resetPartnerNewMsg();
		Partner.setType(SCPartner.RECEIVER);
		
		//reset chatSession
		SharedPrefs.resetChatSessionId(Debug.this);
		
		//TODO: workaround, needed?
		Intent i = new Intent(Debug.this, Home.class);
	    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    Debug.this.startActivity(i);
		
		finish();
    }
 
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		boolean result = super.onCreateOptionsMenu(menu);
		
		 menu.add(0, 0, 0, R.string.close);

		return result;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
	    
		switch(item.getItemId()) 
	    {
	    	case 0:
	    		finish();
	    		return true;
	    }
	    
	    return super.onMenuItemSelected(featureId, item);
	}
 
}