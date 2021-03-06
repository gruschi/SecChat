package edu.hm.cs.sisy.generators;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import edu.hm.cs.sisy.storage.SharedPrefs;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class PinHashGenerator {
	
    private static final String TAG = "PinHashGenerator";
	
       //PIN, 6 default length
       public static String generatePIN() 
       {
         return generatePIN( 6 );
       }  
  
	   public static String generatePIN( int length ) 
	   {
		    String pin = "";
		    String possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; //abcdefghijklmnopqrstuvwxyz

		    for( int i=0; i < length; i++ )
		    	pin += possible.charAt((int) Math.floor(Math.random() * possible.length()));
		    
		    return pin;
	    }

	   public static String createHash() throws NoSuchAlgorithmException 
	   {
	        String randomStringToHash = generatePIN( 5 );
	        String salt = getSalt();
	        
	        String generatedHash = null;
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-1");
	            md.update(salt.getBytes());
	            byte[] bytes = md.digest(randomStringToHash.getBytes());
	            StringBuilder sb = new StringBuilder();
	            for(int i=0; i< bytes.length ;i++)
	            {
	                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
	            }
	            generatedHash = sb.toString();
	        } 
	        catch (NoSuchAlgorithmException e) 
	        {
	            e.printStackTrace();
	        }
	        return generatedHash;
	   }
	   
	    @SuppressLint("TrulyRandom")
		private static String getSalt() throws NoSuchAlgorithmException
	    {
	        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
	        byte[] salt = new byte[16];
	        sr.nextBytes(salt);
	        return salt.toString();
	    }
	    
	    public static boolean validatePin(Context context, String pin)
	    {
	    	boolean valid = SharedPrefs.getPIN(context).equalsIgnoreCase(pin);
	    	
    		Log.d(TAG, "Testing: PIN: " + SharedPrefs.getPIN(context) + " vs " + pin);
	    	
	    	if(valid)
	    		Log.d(TAG, "ID and PIN valid!");
	    	else
	    		Log.d(TAG, "ID and PIN are NOT valid!");
			
	    	return valid;
	    }
}
