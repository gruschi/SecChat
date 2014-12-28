package edu.hm.cs.sisy.tools;

import java.util.Calendar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import edu.hm.cs.sisy.chat.Home;
import edu.hm.cs.sisy.communication.RestThreadTask;
import edu.hm.cs.sisy.enums.SCConstants;
import edu.hm.cs.sisy.enums.SCPartner;
import edu.hm.cs.sisy.enums.SCState;
import edu.hm.cs.sisy.enums.SCTypes;
import edu.hm.cs.sisy.objects.Partner;
import edu.hm.cs.sisy.storage.SharedPrefs;

public class Common {
     
     public static int getCurrentDate() //int like 20141212 for 12.12.2014
     {
       return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + 
           Calendar.getInstance().get(Calendar.MONTH)*100 + 
           Calendar.getInstance().get(Calendar.YEAR)*100*100;
     }
     
     public static boolean isPinCurrent(int storagedDate, int currentDate)
     {
       return storagedDate == currentDate;
     }
     
     public static void doToast(Context context, String message)
     {
    	Log.d(SCConstants.LOG, "TOAST: " + message);
     	Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
     }
     
     public static String stateToString(int id) 
     {
	     String message = "";
	     	
     	switch (id) 
     	{
	     	case SCState.NOT_REGISTERED:
     			message = "";			
     			break;
     		default:
     			message = "default";
     			break;
     	}
     	
     	return message;
     }
     
     public static String beautifyId(int id) 
     {
	     String secChatId = id+"";

	     return secChatId.substring(0,3) + " " + secChatId.substring(3,6) + " " + secChatId.substring(6);
     }
     
     public static String replaceWhitespaces(String id) 
     {
	     return id.replaceAll("\\s","");
     }
     
     //reset client
     public static void resetClient(Context context)
     {
 		//change state from CONNECTED_TO_CHAT to
 		SCState.setState(SCState.LOGGED_IN, context);
 		
 		//exit server connection to other peer
 		new RestThreadTask(SCTypes.DESTROY_CHAT_SESSION, context).execute();
 		
 		//reset partner-object
 		Partner.setPartnerAlias(null);
 		Partner.setPartnerId(0);
 		Partner.setPartnerPin(null);
 		Partner.setPartnerPubKey(null);
 		Partner.resetPartnerNewMsg();
 		Partner.setType(SCPartner.RECEIVER);
 		
 		//reset chatSession
 		SharedPrefs.resetChatSessionId(context);
 		
 		//TODO: workaround, needed?
 		Intent i = new Intent(context, Home.class);
 	    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 	    context.startActivity(i);
 		
 	   ((Activity) context).finish();
     }
     
     /*
     public int stateToInt(int id) 
     {
     	int message = -1;    	
     	switch (id) 
     	{
     		case STATUS.NOT_REGISTERED:
     			message = R.string.not_connected_to_service;			
     			break;
     		default:
     			break;
     	}
     	
     	return message;
     }
     
     public String stateToString(int id, Context context) 
     {
     	int message = -1;    	
     	switch (id) 
     	{
     		case STATUS.NOT_REGISTERED:
     			message = R.string.not_connected_to_service;			
     			break;
     		default:
     			break;
     	}
     	
     	return context.getResources().getString(message);
     }
     */

}
