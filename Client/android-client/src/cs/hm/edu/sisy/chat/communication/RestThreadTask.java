package cs.hm.edu.sisy.chat.communication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import cs.hm.edu.sisy.chat.enums.SCState;
import cs.hm.edu.sisy.chat.enums.SCTypes;
import cs.hm.edu.sisy.chat.services.BGService;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;
import cs.hm.edu.sisy.chat.tools.Common;

//Params: < Param doInBG, onPrgoressUpdate, Return doInBG >
public class RestThreadTask extends AsyncTask<String, Void, Boolean> {

    private SCTypes type; 
    private Context context;
    
    private int state = 0;
	
	public RestThreadTask(SCTypes type, Context context)
	{
		this.type = type;
		this.context = context;
	}
	
    protected void onPreExecute() {
    }

    protected Boolean doInBackground(String... params) {
    	
		switch (this.type) {
			case REGISTER: 
				return RestService.registerUser( params[0], SharedPrefs.getHash(context), context );
		    case LOGIN: 
		    	return RestService.loginUser( SharedPrefs.getID(context), SharedPrefs.getHash(context), context);
		    case LOGOUT:  
		    	return RestService.logoutUser( context);
		    case CONNECT_SERVICE:  
		    	return RestService.serviceConnect(context, params[0]); //String String receiverID
		    case CONNCET_PRIVATE_CHAT:  
		    	//return RestWrapper.connect2Chat(context);
		    	return RestService.connectPrivChat(context, params[0], params[1]); //String receiverPin, String receiverID
		    case CONNECT_PUBLIC_CHAT: 
		    	//TODO
		    	break;
		    case SEND_MSG:  
		    	return RestService.sendChatMessage(Integer.parseInt(params[0]), params[1], context); //receiverID, message
		    case RECEIVE_MSG: 
		    	return RestService.receiveChatMessage(context);
		    case SERVICE:  
		    	return RestService.service(context);
		    default: 
		        break;
		};
        
		return false;
    }
    
    protected void onProgressUpdate(){
    }

    @Override
    protected void onPostExecute(Boolean result) {
       
        switch (type) {
            case LOGIN: 
            	if(result)
            		state = SCState.LOGGED_IN;
            	else
            		state = SCState.NOT_LOGGED_IN;
            	break;
            case LOGOUT:  
            	if(result)
            		state = SCState.LOGGED_OUT;
            	break;
            case REGISTER: 
            	if(result)
            		state = SCState.REGISTERED;
            	else
            		state = SCState.NOT_REGISTERED;
            	break;
            case CONNCET_PRIVATE_CHAT:  
            	if(result)
            		state = SCState.CONNECT_TO_CHAT_PENDING;
            	else
            		state = SCState.NOT_CONNECTED_TO_CHAT;
            	break;
            case CONNECT_PUBLIC_CHAT: 
            	//TODO
            	break;
            case SEND_MSG:  
            	if(result) {
            		SCState.setMsgState(SCState.MSG_SENT);
            		return;
            	}
            	else
            	{
            		SCState.setMsgState(SCState.MSG_NOT_SENT);
            		return;
            	}
            case RECEIVE_MSG: 
            	if(result) {
            		SCState.setMsgState(SCState.MSG_RECEIVED);
            		return;
            	}
            	else
            	{
            		SCState.setMsgState(SCState.MSG_NOT_RECEIVED);
            		return;
            	}
            case CONNECT_SERVICE: 
            	if(result) {
            		state = SCState.CONNECTED_TO_CHAT;
            		context.stopService(new Intent(context, BGService.class));
            		context.startService(new Intent(context, BGService.class));
            	}
            	else
            		state = SCState.NOT_CONNECTED_TO_CHAT;
            	break;
            case SERVICE:
              if(SCState.getState() == SCState.CONNECT_TO_CHAT_PENDING)
                if(result) {
                  state = SCState.CONNECTED_TO_CHAT;
                  context.stopService(new Intent(context, BGService.class));
                  context.startService(new Intent(context, BGService.class));
                }
              else
                if(result)
                  state = SCState.CHAT_CONNECTION_INCOMING;
            	break;
            default: 
                break;
        }
        
        Common.doToast(context, SCState.getStateMessage() +"");
        
        SCState.setState(state);
    }

}