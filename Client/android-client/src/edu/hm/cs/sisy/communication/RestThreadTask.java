package edu.hm.cs.sisy.communication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import edu.hm.cs.sisy.enums.SCState;
import edu.hm.cs.sisy.enums.SCTypes;
import edu.hm.cs.sisy.services.BGService;
import edu.hm.cs.sisy.storage.SharedPrefs;
import edu.hm.cs.sisy.tools.Common;

//Params: < Param doInBG, onPrgoressUpdate, Return doInBG >
public class RestThreadTask extends AsyncTask<String, Void, Boolean> {

    private SCTypes type; 
    private Context context;
    
    private static final String TAG = "RestThreadTask";
    
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
		    	return RestService.serviceConnect(context);
		    case CONNCET_PRIVATE_CHAT:
		    	//return RestWrapper.connect2Chat(context);
		    	return RestService.connectPrivChat(context, params[0], params[1]); //String receiverPin, String receiverID
		    case CONNECT_PUBLIC_CHAT: 
		    	//TODO
		    	break;
		    case SEND_MSG:  
        		if(SCState.getState(context) == SCState.LOGGED_IN) {
        			Common.resetClient(context);
        			//not forced to restart directly, because receive_msg should do it also after state has changed
        			//restartBGService();
        		}
        		else
        			return RestService.sendChatMessage(params[0], context); //, message
		    case RECEIVE_MSG: 
            	if(SCState.getState(context) == SCState.CONNECTED_TO_CHAT)
            		return RestService.receiveChatMessage(context);
            	else
            		break;
		    case SERVICE:  
		    	return RestService.service(context);
		    case DESTROY_CHAT_SESSION:  
		    	return RestService.destroyChatSession(context, params[0]);
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
            	if(result)
            		SCState.setMsgState(SCState.MSG_SENT, context);
            	else
          			SCState.setMsgState(SCState.MSG_NOT_SENT, context);
           		return;
            case RECEIVE_MSG:
            	if(SCState.getState(context) == SCState.CONNECTED_TO_CHAT)
                	if(result) {
                		SCState.setMsgState(SCState.MSG_RECEIVED, context);
                		return;
                	}
                	else
                	{
                		SCState.setMsgState(SCState.MSG_NOT_RECEIVED, context);
                		return;
    	            }
            	else {
            		Common.resetClient(context);
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
            	if(result) {
            		state = SCState.LOGGED_IN;
            		break;
            	}
            	else
            		Log.e(TAG, "Destryoing Chat Session didnt work...");
            	//there should no else case... if DestroyChatSession really didnt
            	//work, then we have to call it again... but this may not be
            	break;
            default: 
                break;
        }
        
        if(state != 0)
        	SCState.setState(state, context, true);
    }
    
    private void restartBGService() {
    	Log.d(TAG, "Restarting BGService");
		context.stopService(new Intent(context, BGService.class));
		context.startService(new Intent(context, BGService.class));
    }

}
