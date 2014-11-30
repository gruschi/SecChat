package cs.hm.edu.sisy.chat.tools;

public class CONSTANTS {

     //SHARED PREFERENCES
	   public static final String MyPREFERENCES = "MyPrefs" ;
	   public static final String HASH = "hashKey"; 
	   public static final String ID = "idKey"; 
	   public static final String ALIAS = "aliasKey";
	   public static final String SESSION_ID = "sessionID"; 
     public static final String DATE_FOR_PIN_REFRESH = "date4PinRefresh";
     public static final String PIN = "pin";

	   //REST
     public static final String REST_SERVER = "localhost/SecChat/Server" ;
     //public static final String REST_SERVER = "http://x.x.x.x/SecChat/Server" ;
     public static final String REST_LOGIN = REST_SERVER + "/users/login";
     public static final String REST_LOGOUT = REST_SERVER + "/users/logout"; 
     public static final String REST_REGISTER = REST_SERVER + "/users/add"; 
     public static final String REST_CONNECT_FRIEND = REST_SERVER + "/connect/contact";
     public static final String REST_CONNECT_GROUP = REST_SERVER + "/connect/group";     
     public static final String REST_SEND_MSG = REST_SERVER + "/send";
     public static final String REST_RECEIVE_MSG = REST_SERVER + "/receive";
     
}
