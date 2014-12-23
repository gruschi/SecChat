package cs.hm.edu.sisy.chat.services;

import java.security.NoSuchAlgorithmException;

import android.content.Context;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.Misc;
import cs.hm.edu.sisy.chat.types.STATUS;

public class RestWrapper {


	public static boolean isRegistered(Context context)
    {
		boolean isRegistered = ( Storage.getID(context) != 0 );
		
		if( STATUS.getState() <= STATUS.NOT_LOGGED_IN )
		{
			if(isRegistered)
				STATUS.setState(STATUS.REGISTERED);
			else
				STATUS.setState(STATUS.NOT_REGISTERED);
		}
		
    	return isRegistered;
    }
    
    public static boolean isLoggedIn(Context context)
    {
		boolean isLoggedIn = ( Storage.getSessionId(context) != null );
		
		if(isLoggedIn)
			STATUS.setState(STATUS.LOGGED_IN);
		else
			STATUS.setState(STATUS.NOT_LOGGED_IN);
		
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
	    		Storage.saveAlias( context, alias );
				Storage.saveHash( context, PinHashGenerator.createHash() );
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
        if( !Misc.isPinCurrent( Storage.getStoragedPinDate(context), Misc.getCurrentDate() ) )
        {
          Storage.savePIN( context, PinHashGenerator.generatePIN() );
          Storage.saveStoragedPinDate( context, Misc.getCurrentDate() );
        }
    	
    	return ( Storage.getID(context) != 0 && Storage.getSessionId(context) == null );
    }
    
    public static boolean connect2Chat(Context context) 
    {
    	boolean status = false;
    	
    	//check oncreate in home or service
    	if( Storage.getID(context) != 0 && Storage.getSessionId(context) != null )//&& Storage.getChatSession(this) != null ) //oder callback funktion in REST-Service von IncomingChat
    	{
    		//go to this chat, service überschrieb im hintergrund das globale partner-objekt mit alias, pubkey etc.
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
