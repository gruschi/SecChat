package cs.hm.edu.sisy.chat.enums;

public class SCState {
	
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
	
	private static int state;
	private static int msgState = MSG_DEFAULT;

	public static int getState() {
		return state;
	}
	
	public static int getMsgState() {
		return msgState;
	}
	
	public static String getStateMessage() {
		String stateMessage;
		
        switch (state) {
	        case 0:
	        	stateMessage = "Timeout";
	        	break;
	        case 1:     
	        	stateMessage = "Not Registred";
	        	break;
	        case 2: 
	        	stateMessage = "-";
	        	break;
	        case 3:
	        	stateMessage = "Logged Out";
	        	break;
	        case 4:
	        	stateMessage = "Registred";
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
	        	stateMessage = "Connect To Chat Pending";
	        	break;
	        case 9:
	        	stateMessage = "Connected To Chat";
	        	break;
            default:
            	stateMessage = "-";
	        	break;
        }
        
		return stateMessage;
	}
	
	public static String getMsgStateMessage() {
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

	public static void setState(int state) {
		SCState.state = state;
	}
	
	public static void setMsgState(int msgState) {
		SCState.msgState = msgState;
	}
}
