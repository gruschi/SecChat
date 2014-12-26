package cs.hm.edu.sisy.chat.tools;

import java.util.Calendar;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import cs.hm.edu.sisy.chat.enums.SCConstants;
import cs.hm.edu.sisy.chat.enums.SCState;

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
