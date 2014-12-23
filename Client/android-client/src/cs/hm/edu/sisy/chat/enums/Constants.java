package cs.hm.edu.sisy.chat.enums;

public class Constants {

     //SHARED PREFERENCES
	 public static final String MyPREFERENCES = "MyPrefs" ;
	 public static final String HASH = "hashKey"; 
	 public static final String ID = "idKey"; 
	 public static final String ALIAS = "aliasKey";
	 public static final String SESSION_ID = "sessionID"; 
     public static final String DATE_FOR_PIN_REFRESH = "date4PinRefresh";
     public static final String PIN = "pin";
 	 public static final String CHAT_SESSION_ID = "chatSessionId";

	  //REST
     //public static final String REST_SERVER = "localhost/SecChat/Server" ;
     //public static final String REST_SERVER = "https://x.x.x.x/SecChat/Server" ;
     public static final String REST_SERVER = "https://secchat.bg-world.de" ;
     public static final String REST_LOGIN = REST_SERVER + "/users/login";
     public static final String REST_LOGOUT = REST_SERVER + "/users/logout"; 
     public static final String REST_REGISTER = REST_SERVER + "/users/add"; 
     public static final String REST_CONNECT_FRIEND = REST_SERVER + "/connect/contact";
     public static final String REST_CONNECT_GROUP = REST_SERVER + "/connect/group";     
     public static final String REST_SEND_MSG = REST_SERVER + "/send";
     public static final String REST_RECEIVE_MSG = REST_SERVER + "/receive";
     public static final String REST_SERVICE = REST_SERVER + "/service";
     
     //LOG
     public static final String LOG = "SecChat_LOGGING";
}
