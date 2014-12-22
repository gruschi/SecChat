package cs.hm.edu.sisy.chat.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.CONST;
import cs.hm.edu.sisy.chat.types.STATUS;
import cs.hm.edu.sisy.chat.types.TYPES;

//Params: < Param doInBG, onPrgoressUpdate, Return doInBG >
public class RestThreadTask extends AsyncTask<String, Void, Boolean> {

    private TYPES type; 
    private Context context;
    
    private int state = 0;
	
	public RestThreadTask(TYPES type, Context context)
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
		    case CONNCET_PRIVATE_CHAT:  
		    	//return RestWrapper.connect2Chat(context);
		    	return RestService.connectPrivChat(context, params[0], params[1]); //String receiverPin, String receiverID
		    case CONNECT_PUBLIC_CHAT: 
		    	//TODO
		    	break;
		    case SEND_MSG:  
		    	return RestService.sendChatMessage(Integer.parseInt(params[1]), params[0], context); //receiverID, message
		    case RECEIVE_MSG: 
		    	//return RestService.receiveChatMessage(context);
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
            		state = STATUS.LOGGED_IN;
            	else
            		state = STATUS.NOT_LOGGED_IN;
            	break;
            case LOGOUT:  
            	if(result)
            		state = STATUS.LOGGED_OUT;
            	break;
            case REGISTER: 
            	if(result)
            		state = STATUS.REGISTERED;
            	else
            		state = STATUS.NOT_REGISTERED;
            	break;
            case CONNCET_PRIVATE_CHAT:  
            	if(result)
            		state = STATUS.CONNECTED_TO_CHAT;
            	else
            		state = STATUS.NOT_CONNECTED_TO_CHAT;
            	break;
            case CONNECT_PUBLIC_CHAT: 
            	//TODO
            	break;
            case SEND_MSG:  
            	if(result)
            		state = STATUS.MSG_SENT;
            	else
            		state = STATUS.MSG_NOT_SENT;
            	break;
            case RECEIVE_MSG: 
            	if(result)
            		state = STATUS.MSG_RECEIVED;
            	else
            		state = STATUS.MSG_NOT_RECEIVED;
            	break;
            default: 
                break;
        }
        
        Toast.makeText(context, "RESULT: "+result+"", Toast.LENGTH_SHORT).show();
        
        STATUS.setState(state);
    }

}
