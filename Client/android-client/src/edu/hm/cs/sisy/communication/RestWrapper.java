package edu.hm.cs.sisy.communication;

import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.util.Log;
import edu.hm.cs.sisy.enums.SCState;
import edu.hm.cs.sisy.generators.PinHashGenerator;
import edu.hm.cs.sisy.generators.PubPrivKeyGenerator;
import edu.hm.cs.sisy.storage.SharedPrefs;
import edu.hm.cs.sisy.tools.Common;

public class RestWrapper {

    private static final String TAG = "RestWrapper";

	public static boolean isRegistered(Context context)
    {
		Log.d(TAG, "Check if client is registered...");
		
		boolean isRegistered = ( SharedPrefs.getID(context) != 0 );
		if( SCState.getState(context) <= SCState.NOT_LOGGED_IN )
		{
			if(isRegistered)
				SCState.setState(SCState.REGISTERED, context, true);
			else
				SCState.setState(SCState.NOT_REGISTERED, context, true);
		}
		
    	return isRegistered;
    }
    
    public static boolean isLoggedIn(Context context)
    {
    	Log.d(TAG, "Check if client is logged in...");
    	
		boolean isLoggedIn = ( SharedPrefs.getSessionId(context) != null );
		
		if(isLoggedIn)
			SCState.setState(SCState.LOGGED_IN, context, true);
		else
			SCState.setState(SCState.NOT_LOGGED_IN, context, true);
		
    	return isLoggedIn;
    }  
    
    public static boolean registerNeeded(Context context, String alias)
    {
    	if( !isRegistered(context) )
    	{
    	  if( PubPrivKeyGenerator.getOwnPublicKeyAsString(context) == null || PubPrivKeyGenerator.getOwnPublicKeyAsString(context) == "" )
	    	  PubPrivKeyGenerator.generateKeys(context);
	    	
	    	try 
	    	{
	    		SharedPrefs.saveAlias( context, alias );
				SharedPrefs.saveHash( context, PinHashGenerator.createHash() );
			} 
	    	catch (NoSuchAlgorithmException e) 
	    	{
				e.printStackTrace();
			}
    	}
    	
    	return !isRegistered(context); //if not registred = true = registerNeeded
    }
    
    public static boolean loginNeeded(Context context)
    {
        if( !Common.isPinCurrent( SharedPrefs.getStoragedPinDate(context), Common.getCurrentDate() ) )
        {
          SharedPrefs.savePIN( context, PinHashGenerator.generatePIN() );
          SharedPrefs.saveStoragedPinDate( context, Common.getCurrentDate() );
        }
    	
    	return ( SharedPrefs.getID(context) != 0 && SharedPrefs.getSessionId(context) == null );
    }
    
    public static boolean connect2Chat(Context context) 
    {
		return false;
		// TODO Auto-generated method stub
    }
    
    public static boolean logout(Context context) {
		return false;
		// TODO Auto-generated method stub
	}
    
    public static boolean receiveMessage(Context context) {
		return false;
		// TODO Auto-generated method stub
	}

    public static boolean sendMessage(Context context) {
		return false;
		// TODO Auto-generated method stub
	}
}
