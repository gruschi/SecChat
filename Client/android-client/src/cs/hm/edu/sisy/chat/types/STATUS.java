package cs.hm.edu.sisy.chat.types;

public class STATUS {
	
	public static final int TIMED_OUT = 0;
    public static final int NOT_REGISTERED = 1;
    public static final int LOGGED_OUT = 3;
    public static final int REGISTERED = 4;
    public static final int NOT_LOGGED_IN = 5;
    public static final int LOGGED_IN = 6;
    public static final int NOT_CONNECTED_TO_CHAT = 7;
    public static final int CONNECTED_TO_CHAT = 8;
    public static final int MSG_NOT_SENT = 9;
    public static final int MSG_SENT = 10;
	public static final int MSG_NOT_RECEIVED = 11;
    public static final int MSG_RECEIVED = 12;
	
	private static int state;

	public static int getState() {
		return state;
	}

	public static void setState(int state) {
		STATUS.state = state;
	}
}
