package cs.hm.edu.sisy.chat.communication;

import java.security.NoSuchAlgorithmException;

import android.content.Context;
import cs.hm.edu.sisy.chat.enums.State;
import cs.hm.edu.sisy.chat.generators.PinHashGenerator;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.Common;

public class RestWrapper {


	public static boolean isRegistered(Context context)
    {
		boolean isRegistered = ( Storage.getID(context) != 0 );
		
		if( State.getState() <= State.NOT_LOGGED_IN )
		{
			if(isRegistered)
				State.setState(State.REGISTERED);
			else
				State.setState(State.NOT_REGISTERED);
		}
		
    	return isRegistered;
    }
    
    public static boolean isLoggedIn(Context context)
    {
		boolean isLoggedIn = ( Storage.getSessionId(context) != null );
		
		if(isLoggedIn)
			State.setState(State.LOGGED_IN);
		else
			State.setState(State.NOT_LOGGED_IN);
		
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
        if( !Common.isPinCurrent( Storage.getStoragedPinDate(context), Common.getCurrentDate() ) )
        {
          Storage.savePIN( context, PinHashGenerator.generatePIN() );
          Storage.saveStoragedPinDate( context, Common.getCurrentDate() );
        }
    	
    	return ( Storage.getID(context) != 0 && Storage.getSessionId(context) == null );
    }
    
    public static boolean connect2Chat(Context context) 
    {
    	boolean status = false;
    	
    	//check oncreate in home or service
    	if( Storage.getID(context) != 0 && Storage.getSessionId(context) != null )//&& Storage.getChatSession(this) != null ) //oder callback funktion in REST-Service von IncomingChat
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
