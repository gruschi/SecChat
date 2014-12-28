package edu.hm.cs.sisy.chat;

import edu.hm.cs.sisy.chat.R;
import edu.hm.cs.sisy.communication.RestThreadTask;
import edu.hm.cs.sisy.enums.SCPartner;
import edu.hm.cs.sisy.enums.SCState;
import edu.hm.cs.sisy.enums.SCTypes;
import edu.hm.cs.sisy.objects.Partner;
import edu.hm.cs.sisy.services.BGService;
import edu.hm.cs.sisy.storage.SharedPrefs;
import edu.hm.cs.sisy.tools.Common;
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
    	Common.resetClient(Debug.this);
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