package cs.hm.edu.sisy.chat.communication;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cs.hm.edu.sisy.chat.Login;
import cs.hm.edu.sisy.chat.enums.Constants;
import cs.hm.edu.sisy.chat.enums.State;
import cs.hm.edu.sisy.chat.enums.Types;
import cs.hm.edu.sisy.chat.services.BGService;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.Common;

//Params: < Param doInBG, onPrgoressUpdate, Return doInBG >
public class RestThreadTask extends AsyncTask<String, Void, Boolean> {

    private Types type; 
    private Context context;
    
    private int state = 0;
	
	public RestThreadTask(Types type, Context context)
	{
		this.type = type;
		this.context = context;
	}
	
    protected void onPreExecute() {
    }

    protected Boolean doInBackground(String... params) {
    	
		switch (this.type) {
			case REGISTER: 
				return RestService.registerUser( params[0], Storage.getHash(context), context );
		    case LOGIN: 
		    	return RestService.loginUser( Storage.getID(context), Storage.getHash(context), context);
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
		    	return RestService.sendChatMessage(Integer.parseInt(params[1]), params[0], context); //receiverID, message
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
            		state = State.LOGGED_IN;
            	else
            		state = State.NOT_LOGGED_IN;
            	break;
            case LOGOUT:  
            	if(result)
            		state = State.LOGGED_OUT;
            	break;
            case REGISTER: 
            	if(result)
            		state = State.REGISTERED;
            	else
            		state = State.NOT_REGISTERED;
            	break;
            case CONNCET_PRIVATE_CHAT:  
            	if(result)
            		state = State.CONNECT_TO_CHAT_PENDING;
            	else
            		state = State.NOT_CONNECTED_TO_CHAT;
            	break;
           /* case CONNECT_PUBLIC_CHAT: 
            	//TODO
            	break;
            case SEND_MSG:  
            	if(result)
            		state = State.MSG_SENT;
            	else
            		state = State.MSG_NOT_SENT;
            	break;
            case RECEIVE_MSG: 
            	if(result)
            		state = State.MSG_RECEIVED;
            	else
            		state = State.MSG_NOT_RECEIVED;
            	break;*/
            case CONNECT_SERVICE: 
            	if(result) {
            		state = State.CONNECTED_TO_CHAT;
            		context.stopService(new Intent(context, BGService.class));
            		context.startService(new Intent(context, BGService.class));
            	}
            	else
            		state = State.NOT_CONNECTED_TO_CHAT;
            	break;
            case SERVICE:
              if(State.getState() == State.CONNECT_TO_CHAT_PENDING)
                if(result) {
                  state = State.CONNECTED_TO_CHAT;
                  context.stopService(new Intent(context, BGService.class));
                  context.startService(new Intent(context, BGService.class));
                }
              else
                if(result)
                  state = State.CHAT_CONNECTION_INCOMING;
            	break;
            default: 
                break;
        }
        
        Common.doToast(context, State.getStateMessage() +"");
        
        State.setState(state);
    }

}
