package cs.hm.edu.sisy.chat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.Misc;
import cs.hm.edu.sisy.qr.Contents;
import cs.hm.edu.sisy.qr.QRCodeEncoder;

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
     
     txtYourAlias.setText(Storage.getAlias(this));
     txtYourPin.setText(Storage.getPIN(this));
     txtYourId.setText(Storage.getID(this));
     
     saveNewAlias = (Button) findViewById(R.id.btnSaveAlias);
     saveNewAlias.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			Storage.saveAlias(Settings.this, txtYourAlias.getText().toString());
		}
	});
    
    generateNewPin = (Button) findViewById(R.id.btnGenerateNewPIN); 
    generateNewPin.setOnClickListener(new OnClickListener(){
		@Override
		public void onClick(View v) {
			Storage.savePIN( Settings.this, PinHashGenerator.generatePIN() );
			Storage.saveStoragedPinDate( Settings.this, Misc.getCurrentDate() );
			txtYourPin.setText(Storage.getPIN(Settings.this));
		}
	});
   }
    
    //https://github.com/zxing/zxing/wiki/Getting-Started-Developing
    //http://www.mysamplecode.com/2012/09/android-generate-qr-code-using-zxing.html
    public void GenerateQRCode() {
    	PubPrivKeyGenerator pubKey = new PubPrivKeyGenerator(this);
        String qrInputText = pubKey.getPublicKeyAsString();
    
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
    
      } catch (WriterException e) {
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
}