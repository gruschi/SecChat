package cs.hm.edu.sisy.chat.communication;

import java.security.NoSuchAlgorithmException;

import android.content.Context;
import cs.hm.edu.sisy.chat.enums.SCState;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;
import cs.hm.edu.sisy.chat.tools.Common;

public class RestWrapper {


	public static boolean isRegistered(Context context)
    {
		boolean isRegistered = ( SharedPrefs.getID(context) != 0 );
		
		if( SCState.getState() <= SCState.NOT_LOGGED_IN )
		{
			if(isRegistered)
				SCState.setState(SCState.REGISTERED);
			else
				SCState.setState(SCState.NOT_REGISTERED);
		}
		
    	return isRegistered;
    }
    
    public static boolean isLoggedIn(Context context)
    {
		boolean isLoggedIn = ( SharedPrefs.getSessionId(context) != null );
		
		if(isLoggedIn)
			SCState.setState(SCState.LOGGED_IN);
		else
			SCState.setState(SCState.NOT_LOGGED_IN);
		
    	return isLoggedIn;
    }  
    
    public static boolean registerNeeded(Context context, String alias)
    {
    	if( !isRegistered(context) )
    	{
	    	PubPrivKeyGenerator kg = new PubPrivKeyGenerator(context);
	    	
	    	if( kg.getPublicKeyAsString() == null || kg.getPublicKeyAsString() == "" )
	    		kg.generateKeys();
	    	
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
    	boolean status = false;
    	
    	//check oncreate in home or service
    	if( SharedPrefs.getID(context) != 0 && SharedPrefs.getSessionId(context) != null )//&& Storage.getChatSession(this) != null ) //oder callback funktion in REST-Service von IncomingChat
    	{
    		//go to this chat, service �berschrieb im hintergrund das globale partner-objekt mit alias, pubkey etc.
    	}
    	else {
    		//go to home
    	}
		return status;
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
