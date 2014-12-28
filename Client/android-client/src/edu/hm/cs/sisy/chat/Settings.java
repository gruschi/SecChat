package edu.hm.cs.sisy.chat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import edu.hm.cs.sisy.chat.R;
import edu.hm.cs.sisy.generators.PinHashGenerator;
import edu.hm.cs.sisy.generators.PubPrivKeyGenerator;
import edu.hm.cs.sisy.qr.Contents;
import edu.hm.cs.sisy.qr.QRCodeEncoder;
import edu.hm.cs.sisy.storage.SharedPrefs;
import edu.hm.cs.sisy.tools.Common;

public class Settings extends Activity
{
    private EditText txtYourAlias;
    private TextView txtYourPin;
    private TextView txtYourId;
    
	private Button saveNewAlias;
	private Button generateNewPin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.settings_screen);
    
     txtYourAlias = (EditText) findViewById(R.id.txtYourAlias);
     txtYourPin = (TextView) findViewById(R.id.txtYourPin);
     txtYourId = (TextView) findViewById(R.id.txtYourId);
     
     txtYourAlias.setText(SharedPrefs.getAlias(this) +"");
     txtYourPin.setText(SharedPrefs.getPIN(this) +"");
     txtYourId.setText( Common.beautifyId(SharedPrefs.getID(this)) );
     
     GenerateQRCode();
     
     saveNewAlias = (Button) findViewById(R.id.btnSaveAlias);
     saveNewAlias.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			SharedPrefs.saveAlias(Settings.this, txtYourAlias.getText().toString());
		}
	});
    
    generateNewPin = (Button) findViewById(R.id.btnGenerateNewPIN); 
    generateNewPin.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			SharedPrefs.savePIN( Settings.this, PinHashGenerator.generatePIN() );
			SharedPrefs.saveStoragedPinDate( Settings.this, Common.getCurrentDate() );
			txtYourPin.setText(SharedPrefs.getPIN(Settings.this));
		}
	});
   }
    
    //https://github.com/zxing/zxing/wiki/Getting-Started-Developing
    //http://www.mysamplecode.com/2012/09/android-generate-qr-code-using-zxing.html
    public void GenerateQRCode() {
        String qrInputText = PubPrivKeyGenerator.getOwnPublicKeyAsString(Settings.this);
    
      //Find screen size
      WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
      Display display = manager.getDefaultDisplay();
      Point point = new Point();
      display.getSize(point);
      int width = point.x;
      int height = point.y;
      int smallerDimension = width < height ? width : height;
      smallerDimension = smallerDimension * 3/4;

      //Encode with a QR Code image
      QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(qrInputText, 
                null, 
                Contents.Type.TEXT,  
                BarcodeFormat.QR_CODE.toString(), 
                smallerDimension);
      try {
       Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
       ImageView myImage = (ImageView) findViewById(R.id.qrImg);
       myImage.setImageBitmap(bitmap);
      } 
      catch (WriterException e) {
       e.printStackTrace();
      }
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
    public void onDestroy() 
    {
        super.onDestroy();
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