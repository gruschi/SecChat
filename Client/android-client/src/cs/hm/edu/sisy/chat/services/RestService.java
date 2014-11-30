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
import android.widget.TextView;
import cs.hm.edu.sisy.chat.generators.KeyGenerator;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.CONSTANTS;

public class RestService
{
	
    //login user
	public static boolean loginUser(String alias, String hash, Context context) {
	    HttpClient httpclient = new DefaultHttpClient();
	    HttpPost httppost = new HttpPost(CONSTANTS.REST_LOGIN);
	
		  try {
		      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		      nameValuePairs.add(new BasicNameValuePair("data[User][username]", alias)); //alias
		      nameValuePairs.add(new BasicNameValuePair("data[User][password]", hash)); //hash value
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
		          String sessionId = jsonobject .getString("sessionId");
		
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
  public static void logoutUser(Context context) {
	String sessionId = Storage.getSession(context);
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(CONSTANTS.REST_LOGOUT);

  try {
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

      // Execute HTTP Post Request
      httpclient.execute(httppost);
      Storage.saveSession(context, null);
  } 
  catch (ClientProtocolException e) {
  } catch (IOException e) {
  }
  }
  
  
  //register user
  public static boolean registerUser(String alias, String hash, Context context) {
	  HttpClient httpclient = new DefaultHttpClient();
	  HttpPost httppost = new HttpPost(CONSTANTS.REST_REGISTER);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][alias]", alias)); //alias
	      nameValuePairs.add(new BasicNameValuePair("data[User][password]", hash)); //hash value
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
	          int userId = jsonobject .getInt("userId");
	
	          Storage.saveID(context, userId);
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
  
  
  //connect private chat
  public static void connectPrivChat(Context context, String receiverPin, String receiverID) {
	String sessionId = Storage.getSession(context);
    KeyGenerator kg = new KeyGenerator(context);
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(CONSTANTS.REST_CONNECT_FRIEND);

  try {
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
      nameValuePairs.add(new BasicNameValuePair("data[User][receiverID ]", receiverID));
      nameValuePairs.add(new BasicNameValuePair("data[User][alias]", Storage.getAlias(context)));
      nameValuePairs.add(new BasicNameValuePair("data[User][receiverPin]", receiverPin));
      nameValuePairs.add(new BasicNameValuePair("data[User][pubKey]", kg.getPublicKeyAsString()));
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
          String chatSessionId = jsonobject .getString("chatSessionId");
          String receiverPubKey = jsonobject .getString("receiverPubKey");
          String receiverAlias = jsonobject .getString("receiverAlias");
          //TODO: MODIFY THIS FUNCTION
        }
        catch ( JSONException e )
        {
          e.printStackTrace();
        }
      }
  } catch (ClientProtocolException e) {
  } catch (IOException e) {
  }
  }
  
  //TODO: connectedByPrivChat()
  
  
  //send message
  public static boolean sendChatMessage(int receiverId, String message, Context context) {
	  String sessionId = Storage.getSession(context);
      HttpClient httpclient = new DefaultHttpClient();
      HttpPost httppost = new HttpPost(CONSTANTS.REST_SEND_MSG);
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
      HttpPost httppost = new HttpPost(CONSTANTS.REST_SEND_MSG);
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
