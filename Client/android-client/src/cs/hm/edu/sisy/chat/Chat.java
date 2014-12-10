package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import cs.hm.edu.sisy.chat.services.RestService;

public class Chat extends Activity {
  
	private Button connectButton;
    private TextView receiverPin;
    private TextView receiverID;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_screen);
        setTitle("Chat");
        
        receiverPin = (TextView) findViewById(R.id.txtYourPin);
        receiverID = (TextView) findViewById(R.id.txtYourId);
        connectButton = (Button) findViewById(R.id.loginButton);
         
        connectButton.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) 
			{
				RestService.connectPrivChat(Chat.this, receiverPin.toString(), receiverID.toString());
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
}