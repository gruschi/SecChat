package cs.hm.edu.sisy.chat.tools;

import android.content.Context;
import android.content.SharedPreferences;

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
}
