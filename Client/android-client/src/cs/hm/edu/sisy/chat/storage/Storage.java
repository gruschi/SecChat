package cs.hm.edu.sisy.chat.storage;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import cs.hm.edu.sisy.chat.tools.CONST;

public class Storage {
		   
	   public static void saveHash(Context context, String hash)
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putString(CONST.HASH, hash).apply();
		   
		  //Editor editor = sharedpreferences.edit();
		  //editor.putString(CONSTANTS.HASH, n);
		  //editor.commit(); 
	   }
	   
	   public static String getHash(Context context)
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONST.HASH ))
		      {
		    	  return sharedpreferences.getString( CONST.HASH, null );
		      }
		      
			return null;
	   }
	   
	   public static void saveID(Context context, int id) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putInt(CONST.ID, id).apply();
	   }
	   
	   
	   public static int getID(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONST.ID ))
		      {
		    	  return sharedpreferences.getInt( CONST.ID, 0 );
		      }
		      
			return 0;
	   }
	   
	   public static void saveAlias(Context context, String alias) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putString(CONST.ALIAS, alias).apply();
	   }
	   
	   public static String getAlias(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONST.ALIAS ))
		      {
		    	  return sharedpreferences.getString( CONST.ALIAS, null );
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
     
	   public static void saveSession(Context context, String alias) 
	   {
	  	   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);

		   sharedpreferences.edit().putString(CONST.SESSION_ID, alias).apply();
	   }
	   
	   public static String getSession(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONST.SESSION_ID ))
		      {
		    	  return sharedpreferences.getString( CONST.SESSION_ID, null );
		      }
		      
			return null;
	   }
	   
	   public static void clearSession(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONST.SESSION_ID ))
		      {
				  Editor editor = sharedpreferences.edit();
			      editor.putString(null, CONST.SESSION_ID);
			      editor.commit();
		      }
	   }
	   
     public static void saveStoragedPinDate(Context context, int currentDate) 
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
       
       sharedpreferences.edit().putInt(CONST.DATE_FOR_PIN_REFRESH, currentDate).apply();

     }
     
     public static int getStoragedPinDate(Context context) 
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
       
       if (sharedpreferences.contains( CONST.DATE_FOR_PIN_REFRESH ))
       {
         return sharedpreferences.getInt( CONST.DATE_FOR_PIN_REFRESH, 0 );
       }
       
       return 0;
     }
     
     public static void savePIN(Context context, String hash)
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
  
       sharedpreferences.edit().putString(CONST.PIN, hash).apply();
     }
     
     public static String getPIN(Context context)
     {
       SharedPreferences sharedpreferences = context.getSharedPreferences(CONST.MyPREFERENCES, Context.MODE_PRIVATE);
       
          if (sharedpreferences.contains( CONST.PIN ))
          {
            return sharedpreferences.getString( CONST.PIN, null );
          }
          
      return null;
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