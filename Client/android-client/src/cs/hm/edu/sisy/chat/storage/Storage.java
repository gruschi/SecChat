package cs.hm.edu.sisy.chat.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cs.hm.edu.sisy.chat.enums.Constants;
import cs.hm.edu.sisy.chat.objects.PubKey;

public class Storage {
		   
	   public static void saveHash(Context context, String hash)
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putString(Constants.HASH, hash).apply();
		   
		  //Editor editor = sharedpreferences.edit();
		  //editor.putString(CONSTANTS.HASH, n);
		  //editor.commit(); 
	   }
	   
	   public static String getHash(Context context)
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.HASH ))
		      {
		    	  return sharedpreferences.getString( Constants.HASH, null );
		      }
		      
			return null;
	   }
	   
	   public static void saveID(Context context, int id) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putInt(Constants.ID, id).apply();
	   }
	   
	   
	   public static int getID(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.ID ))
		      {
		    	  return sharedpreferences.getInt( Constants.ID, 0 );
		      }
		      
			return 0;
	   }
	   
	   public static void saveAlias(Context context, String alias) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putString(Constants.ALIAS, alias).apply();
	   }
	   
	   public static String getAlias(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.ALIAS ))
		      {
		    	  return sharedpreferences.getString( Constants.ALIAS, null );
		      }
		      
			return null;
	   }
	   
	   public static void savePublicKeyFriend(int friendId, String pk, Context context) 
	   {
	   DatabaseHandler dbh = new DatabaseHandler(context);
	   
	   dbh.addPubKey(new PubKey(friendId, pk), dbh); 
	   }
	   
	   public static String getPublicKeyFriend(int id, Context context) 
	   {
	     DatabaseHandler dbh = new DatabaseHandler(context);
	   
	     return dbh.getPubKey(id).getPubKey();
	   }
     
	   public static void saveSessionId(Context context, String alias) 
	   {
	  	   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);

		   sharedpreferences.edit().putString(Constants.SESSION_ID, alias).apply();
	   }
	   
	   public static String getSessionId(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.SESSION_ID ))
		      {
		    	  return sharedpreferences.getString( Constants.SESSION_ID, null );
		      }
		      
			return null;
	   }
	   
	   public static void clearSession(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.SESSION_ID ))
		      {
				  Editor editor = sharedpreferences.edit();
			      editor.putString(null, Constants.SESSION_ID);
			      editor.commit();
		      }
	   }
	   
     public static void saveStoragedPinDate(Context context, int currentDate) 
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
       
       sharedpreferences.edit().putInt(Constants.DATE_FOR_PIN_REFRESH, currentDate).apply();

     }
     
     public static int getStoragedPinDate(Context context) 
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
       
       if (sharedpreferences.contains( Constants.DATE_FOR_PIN_REFRESH ))
       {
         return sharedpreferences.getInt( Constants.DATE_FOR_PIN_REFRESH, 0 );
       }
       
       return 0;
     }
     
     public static void savePIN(Context context, String hash)
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
  
       sharedpreferences.edit().putString(Constants.PIN, hash).apply();
     }
     
     public static String getPIN(Context context)
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
       
          if (sharedpreferences.contains( Constants.PIN ))
          {
            return sharedpreferences.getString( Constants.PIN, null );
          }
          
      return null;
     }
     
	   public static void saveChatSessionId(Context context, int chatSessionId) 
	   {
	  	   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);

		   sharedpreferences.edit().putInt(Constants.CHAT_SESSION_ID, chatSessionId).apply();
	   }
	   
	   public static int getChatSessionId(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.CHAT_SESSION_ID ))
		      {
		    	  return sharedpreferences.getInt( Constants.CHAT_SESSION_ID, 0 );
		      }
		      
			return 0;
	   }
	   
	   public static void resetChatSessionId(Context context) 
	   {
	  	   SharedPreferences sharedpreferences = context.getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( Constants.CHAT_SESSION_ID ))
		      {
		    	  sharedpreferences.edit().putInt(Constants.CHAT_SESSION_ID, 0).apply();
		      }
	   }

	    public static String getPublicKeyAsString(Context context){
	    	SharedPreferences sharedpreferences = context.getSharedPreferences("KeyPair", Context.MODE_PRIVATE);
	        return sharedpreferences.getString("PublicKey", "");       
	    }
     
}

/*
//get data from activity to another

Intent i = new Intent(FirstScreen.this, SecondScreen.class);   
String keyIdentifer  = null;
i.putExtra("STRING_I_NEED", strName);


String newString;
if (savedInstanceState == null) {
    extras = getIntent().getExtras();
    if(extras == null) {
        newString= null;
    } else {
        newString= extras.getString("STRING_I_NEED");
    }
} else {
    newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
}
*/