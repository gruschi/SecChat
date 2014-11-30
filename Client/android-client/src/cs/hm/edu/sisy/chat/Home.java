package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.services.RestService;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.Misc;

public class Home extends Activity {
  
    private TextView yourAlias;
    private TextView yourPIN;
    private TextView yourID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen);
        
        yourAlias = (TextView) findViewById(R.id.txtYourAlias);
        yourPIN = (TextView) findViewById(R.id.txtYourPin);
        yourID = (TextView) findViewById(R.id.txtYourId);
        
        updateDate();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menuChat:
          Intent createChat = new Intent(Home.this, Chat.class);
          startActivity(createChat);
          //Toast.makeText(this, "@string/menuChat", Toast.LENGTH_SHORT).show();
          return true;
        case R.id.menuOptions:
          Intent settings = new Intent(Home.this, Settings.class);
          startActivity(settings);
          return true;        
        default:
          return super.onOptionsItemSelected(item);
        } 
    }
    
    @Override
    protected void onPause() 
    {
      updateDate();
      
      super.onPause();
    }

    @Override
    protected void onResume() 
    {   
      updateDate();
            
      super.onResume();
    }
    
    @Override
    public void onDestroy() 
    {
        super.onDestroy();

        RestService.logoutUser(this);
    }
    
    private void updateDate()
    {
      if( !Misc.isPinCurrent( Storage.getStoragedPinDate(this), Misc.getCurrentDate() ) )
      {
        Storage.savePIN( this, PinHashGenerator.generatePIN() );
        Storage.saveStoragedPinDate( this, Misc.getCurrentDate() );
      }
      
      yourAlias.setText( Storage.getAlias(this) +"" );
      yourPIN.setText( Storage.getPIN(this) +"" );
      yourID.setText( Storage.getID(this) +"" );
    }
}