package cs.hm.edu.sisy.chat.storage;

import cs.hm.edu.sisy.chat.tools.CONSTANTS;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Storage {
		   
	   public static void saveHash(Context context, String hash)
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putString(CONSTANTS.HASH, hash).apply();
		   
		  //Editor editor = sharedpreferences.edit();
		  //editor.putString(CONSTANTS.HASH, n);
		  //editor.commit(); 
	   }
	   
	   public static String getHash(Context context)
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONSTANTS.HASH ))
		      {
		    	  return sharedpreferences.getString( CONSTANTS.HASH, null );
		      }
		      
			return null;
	   }
	  
	   
	   public static void saveID(Context context, int id) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putInt(CONSTANTS.ID, id).apply();
	   }
	   
	   
	   public static int getID(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONSTANTS.ID ))
		      {
		    	  return sharedpreferences.getInt( CONSTANTS.ID, 0 );
		      }
		      
			return 0;
	   }
	   
	   public static void saveAlias(Context context, String alias) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
	
		   sharedpreferences.edit().putString(CONSTANTS.ALIAS, alias).apply();
	   }
	   
	   public static String getAlias(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONSTANTS.ALIAS ))
		      {
		    	  return sharedpreferences.getString( CONSTANTS.ALIAS, null );
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
	  	   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);

		   sharedpreferences.edit().putString(CONSTANTS.SESSION_ID, alias).apply();
	   }
	   
	   public static String getSession(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONSTANTS.SESSION_ID ))
		      {
		    	  return sharedpreferences.getString( CONSTANTS.SESSION_ID, null );
		      }
		      
			return null;
	   }
	   
	   public static void clearSession(Context context) 
	   {
		   SharedPreferences sharedpreferences = context.getSharedPreferences(CONSTANTS.MyPREFERENCES, Context.MODE_PRIVATE);
		   
		      if (sharedpreferences.contains( CONSTANTS.SESSION_ID ))
		      {
				  Editor editor = sharedpreferences.edit();
			      editor.putString(null, CONSTANTS.SESSION_ID);
			      editor.commit();
		      }
	   }
}
