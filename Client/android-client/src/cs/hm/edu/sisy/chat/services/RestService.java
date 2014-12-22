package cs.hm.edu.sisy.chat.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.CONST;

public class RestService
{
	
    //login user
	public static boolean loginUser(int id, String hash, Context context) {
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(CONST.REST_LOGIN);
	
		  try {
		      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		      nameValuePairs.add(new BasicNameValuePair("data[User][id]", id+"")); //alias at beginning?!
		      nameValuePairs.add(new BasicNameValuePair("data[User][password]", hash)); //hash value
		      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		      // Execute HTTP Post Request
		      HttpResponse response = httpclient.execute(httppost);
		
		      Log.d(CONST.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
		      
		      if(response.getStatusLine().getStatusCode() < 300 && 
		          response.getStatusLine().getStatusCode() > 199)
		      {
		        try
		        {
		          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		          
		          String json = reader.readLine();
		          JSONObject jsonobject = new JSONObject(json);
		          
		          String sessionId = jsonobject.getString("sessionId");
		          
		          Log.d(CONST.LOG, "HTTP-Response: SessionID: " + sessionId);	
		
		          Storage.saveSession(context, sessionId);
		          return true;
		        }
		        catch ( JSONException e )
		        {
		          e.printStackTrace();
		        }
		      }
		  } catch (ClientProtocolException e) {
		  } catch (IOException e) {
		  }
		  return false;
	}
  
  
  //logout user
  public static Boolean logoutUser(Context context) {
	String sessionId = Storage.getSession(context);
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(CONST.REST_LOGOUT);

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      httpclient.execute(httppost);
	      Storage.saveSession(context, null);
	      return true;
	  }
	  catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  return false;
  }
  
  
  //register user
  public static boolean registerUser(String alias, String hash, Context context) {
	  HttpClient httpclient = new DefaultHttpClient();
	  HttpPost httppost = new HttpPost(CONST.REST_REGISTER);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("alias", alias)); //alias
	      nameValuePairs.add(new BasicNameValuePair("password", hash)); //hash value
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(httppost);
	      
		  Log.d(CONST.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	          String json = reader.readLine();
	          JSONObject jsonobject = new JSONObject(json);
	          
	          int userId = jsonobject.getInt("userId");
	          
	          Log.d(CONST.LOG, "HTTP-Response: ID: " + userId);
	
	          Storage.saveID(context, userId);
	          
	          //httpclient.getConnectionManager().shutdown(); //TODO überall??
	          return true;
	        }
	        catch ( JSONException e )
	        {
	          e.printStackTrace();
	        }
	      }
	  } catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  
	  //httpclient.getConnectionManager().shutdown(); //TODO überall??
      return false;
  }
  
  
  //connect private chat
  public static Boolean connectPrivChat(Context context, String receiverPin, String receiverID) {
	String sessionId = Storage.getSession(context);
	String pubKey = Storage.getPublicKeyAsString(context);
	String alias = Storage.getAlias(context);
	
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(CONST.REST_CONNECT_FRIEND);

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverID ]", receiverID));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][alias]", alias));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverPin]", receiverPin));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][pubKey]", pubKey));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(httppost);
	      
		  Log.d(CONST.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          JSONTokener tokener = new JSONTokener(json);
	          JSONArray jsonarray = new JSONArray(tokener);
	
	          JSONObject jsonobject = jsonarray.getJSONObject(0);
	          int chatSessionId = jsonobject .getInt("chatSessionId");
	          
	          Log.d(CONST.LOG, "HTTP-Response: chatSessionId: " + chatSessionId);
	          
	          if(chatSessionId != 0)
	        	  Storage.saveChatSessionId(context, chatSessionId);
	          else 
	        	  return false;

	          return true;
	        }
	        catch ( JSONException e )
	        {
	          e.printStackTrace();
	        }
	      }
		  } catch (ClientProtocolException e) {
		  } catch (IOException e) {
		  }
	  return false;
  }
  
  //service
  public static boolean service(Context context) {
	  HttpClient httpclient = new DefaultHttpClient();
	  HttpPost httppost = new HttpPost(CONST.REST_SERVICE);
	  String session = Storage.getSession(context);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", session));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(httppost);
	      
		  Log.d(CONST.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	          String json = reader.readLine();
	          JSONObject jsonobject = new JSONObject(json);
	          
	          Log.d(CONST.LOG, "HTTP-Response: ServiceJSON: " + jsonobject.toString());
	          
	          //TODO json-array?
	          //int userId = jsonobject.getInt("userId");
	          
	          //Storage.saveID(context, userId);
	          
	          //httpclient.getConnectionManager().shutdown(); //TODO überall??
	          return true;
	        }
	        catch ( JSONException e )
	        {
	          e.printStackTrace();
	        }
	      }
	  } catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  
	  //httpclient.getConnectionManager().shutdown(); //TODO überall??
      return false;
  }
  
  //send message
  public static boolean sendChatMessage(int receiverId, String message, Context context) {
	  String sessionId = Storage.getSession(context);
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(CONST.REST_SEND_MSG);
      boolean messageReceived = false;

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[User][receiverId]", Integer.toString(receiverId)));
	      nameValuePairs.add(new BasicNameValuePair("data[User][message]", message));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(httppost);
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          JSONTokener tokener = new JSONTokener(json);
	          JSONArray jsonarray = new JSONArray(tokener);
	
	          //instead of 0, maybe -> for (int i = 0; i < jsonarray.length(); i++)
	          JSONObject jsonobject = jsonarray.getJSONObject(0);
	          messageReceived = jsonobject .getString("received").equals("true");
	        }
	        catch ( JSONException e )
	        {
	          e.printStackTrace();
	        }
	      }
	  } catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  
	  return messageReceived;
  }
 
  
  //receive message
  public static String receiveChatMessage(Context context) {
	  String sessionId = Storage.getSession(context);
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(CONST.REST_SEND_MSG);
      String receivedMessage = "";

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(httppost);
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          JSONTokener tokener = new JSONTokener(json);
	          JSONArray jsonarray = new JSONArray(tokener);
	
	          //instead of 0, maybe -> for (int i = 0; i < jsonarray.length(); i++)
	          JSONObject jsonobject = jsonarray.getJSONObject(0);
	          receivedMessage = jsonobject .getString("receivedMessage");
	          
	        }
	        catch ( JSONException e )
	        {
	          e.printStackTrace();
	        }
	      }
	  } catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  
      return receivedMessage;
  }
  
}
