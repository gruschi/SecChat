package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cs.hm.edu.sisy.chat.communication.RestThreadTask;
import cs.hm.edu.sisy.chat.enums.SCTypes;

public class Chat extends Activity {
  
	private Button connectButton;
    private TextView receiverPin;
    private TextView receiverID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);
        setTitle("Chat");
        
        receiverPin = (TextView) findViewById(R.id.partnerPin);
        receiverID = (TextView) findViewById(R.id.partnerId);
        connectButton = (Button) findViewById(R.id.connectButton);
         
        connectButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) 
			{
				new RestThreadTask(SCTypes.CONNCET_PRIVATE_CHAT, (Context) Chat.this).execute( receiverPin.toString(), receiverID.toString() );
				//RestService.connectPrivChat(Chat.this, receiverPin.toString(), receiverID.toString());
			}
        });
    }
    
    @Override
    protected void onPause() 
    {
      super.onPause();
    }

    @Override
    protected void onResume() 
    {   
      super.onResume();
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