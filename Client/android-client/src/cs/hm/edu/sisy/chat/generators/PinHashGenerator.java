package cs.hm.edu.sisy.chat.generators;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import android.annotation.SuppressLint;

public class PinHashGenerator {
	
	   public static String generatePIN( int length ) 
	   {
		    String pin = "";
		    String possible = "abcdefghijklmnopqrstuvwxyz0123456789"; //ABCDEFGHIJKLMNOPQRSTUVWXYZ

		    for( int i=0; i < length; i++ )
		    	pin += possible.charAt((int) Math.floor(Math.random() * possible.length()));
		    
		    return pin;
	    }


	   public static String createHash() throws NoSuchAlgorithmException 
	   {
	        String randomStringToHash = generatePIN( 8 );
	        String salt = getSalt();
	        
	        String generatedHash = null;
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-256");
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
}
