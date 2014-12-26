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
		    	//TODO: we could catch something if we check/do: if(state != SCState.CONNECTED_TO_CHAT) return false;
		    	return RestService.sendChatMessage(Integer.parseInt(params[0]), params[1], context); //receiverID, message
		    case RECEIVE_MSG: 
		    	//TODO: we could catch something if we check/do: if(state != SCState.CONNECTED_TO_CHAT) return false;
		    	return RestService.receiveChatMessage(context);
		    case SERVICE:  
		    	return RestService.service(context);
		    case DESTROY_CHAT_SESSION:  
		    	return RestService.destroyChatSession(context);
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
            	else {
            		state = SCState.NOT_CONNECTED_TO_CHAT;
            		Common.doToast(context, "Try again...");
            	}
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
            	if(state == SCState.CONNECTED_TO_CHAT)
            	{
	            	if(result) {
	            		SCState.setMsgState(SCState.MSG_RECEIVED);
	            		return;
	            	}
	            	else
	            	{
	            		SCState.setMsgState(SCState.MSG_NOT_RECEIVED);
	            		return;
	            	}
            	}
            	else {
            		restartBGService();
            	}
            case CONNECT_SERVICE: 
            	if(result) {
            		state = SCState.CONNECTED_TO_CHAT;
            		restartBGService();
            	}
            	else
            		state = SCState.NOT_CONNECTED_TO_CHAT;
            	break;
            case SERVICE:
                if(SCState.getState(context) == SCState.CONNECT_TO_CHAT_PENDING) {
                  if(result) {
                    state = SCState.CONNECTED_TO_CHAT;
                    restartBGService();
                  }
                  //else: waiting, do nothing, there is no incoming transmission
                }
                else {
                  if(result) {
                    state = SCState.CHAT_CONNECTION_INCOMING;
                  }
                  //else: waiting, do nothing, there is no incoming transmission
                }
            	break;
            case DESTROY_CHAT_SESSION: 
            	if(result)
            		state = SCState.LOGGED_IN;
            	else
            		state = SCState.NOT_LOGGED_IN;
            	break;
            default: 
                break;
        }
        
        if(state != 0)
        	SCState.setState(state, context);
    }
    
    private void restartBGService() {
		context.stopService(new Intent(context, BGService.class));
		context.startService(new Intent(context, BGService.class));
    }

}
