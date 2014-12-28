package edu.hm.cs.sisy.enums;

import android.content.Context;
import android.content.SharedPreferences;
import edu.hm.cs.sisy.tools.Common;

public class SCState {
	
	public static final int ERROR = -1;
	public static final int TIMED_OUT = 0;
    public static final int NOT_REGISTERED = 1;
    public static final int LOGGED_OUT = 3;
    public static final int REGISTERED = 4;
    public static final int NOT_LOGGED_IN = 5;
    public static final int LOGGED_IN = 6;
    public static final int NOT_CONNECTED_TO_CHAT = 7;
    public static final int CHAT_CONNECTION_INCOMING = 8;
	public static final int CONNECT_TO_CHAT_PENDING = 9;
    public static final int CONNECTED_TO_CHAT = 10;
    public static final int MSG_DEFAULT = 11;
    public static final int MSG_NOT_SENT = 12;
    public static final int MSG_SENT = 13;
	public static final int MSG_NOT_RECEIVED = 14;
    public static final int MSG_RECEIVED = 15;
    
	public static int getState(Context context) {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(SCConstants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		   return sharedpreferences.getInt( SCConstants.SCSTATE, -1 );
	}
	
	public static void setState(int state, Context context) {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(SCConstants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		   //do toast when state is changed
		   if(getState(context) != state)
			   Common.doToast(context, SCState.getStateMessageByState(context, state) +"");
		   
		   sharedpreferences.edit().putInt(SCConstants.SCSTATE, state).apply();
	}
	
	public static int getMsgState(Context context) {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(SCConstants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		   return sharedpreferences.getInt( SCConstants.MSGSTATE, -1 );
	}
	
	public static void setMsgState(int msgState, Context context) {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(SCConstants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		   //do toast when state is changed
		   if(msgState == MSG_SENT)
			   Common.doToast(context, SCState.getMsgStateMessageById(context, msgState) +"");
		   
		   sharedpreferences.edit().putInt(SCConstants.MSGSTATE, msgState).apply();
	}
	
	public static String getStateMessage(Context context) {
		return getStateMessageByState(context, getState(context));
	}
	
	public static String getStateMessageByState(Context context, int state) {
		String stateMessage;
		
        switch (state) {
	        case 0:
	        	stateMessage = "Timeout";
	        	break;
	        case 1:     
	        	stateMessage = "Not Registered";
	        	break;
	        case 2: 
	        	stateMessage = "-";
	        	break;
	        case 3:
	        	stateMessage = "Logged Out";
	        	break;
	        case 4:
	        	stateMessage = "Registered";
	        	break;
	        case 5:
	        	stateMessage = "Not Logged In";
	        	break;
	        case 6:
	        	stateMessage = "Logged In";
	        	break;
	        case 7:
	        	stateMessage = "Not Connected To Chat";
	        	break;
	        case 8:
	        	stateMessage = "Chat Connection Incoming";
	        	break;
	        case 9:
	        	stateMessage = "Connect To Chat Pending";
	        	break;
	        case 10:
	        	stateMessage = "Connected To Chat";
	        	break;
            default:
            	stateMessage = "-";
	        	break;
        }
        
		return stateMessage;
	}
	
	public static String getMsgStateMessage(Context context) {
		return getMsgStateMessageById(context, getMsgState(context));
	}
	
	public static String getMsgStateMessageById(Context context, int state) {
		String msgStateMessage;
		
        switch (state) {
	        case 12:
	        	msgStateMessage = "Msg Not Sent";
	        	break;
	        case 13:
	        	msgStateMessage = "Msg Sent";
	        	break;
	        case 14:
	        	msgStateMessage = "Msg Not Received";
	        	break;
	        case 15:
	        	msgStateMessage = "Msg Received";
	        	break;
            default:
            	msgStateMessage = "-";
	        	break;
        }
        
		return msgStateMessage;
	}
}
