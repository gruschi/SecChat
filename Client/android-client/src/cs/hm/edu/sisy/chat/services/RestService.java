package cs.hm.edu.sisy.chat.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import cs.hm.edu.sisy.chat.storage.Partner;
import cs.hm.edu.sisy.chat.storage.Storage;
import cs.hm.edu.sisy.chat.tools.EasySSLSocketFactory;
import cs.hm.edu.sisy.chat.types.CONST;

//alternative: rest lib: http://java.dzone.com/articles/android-%E2%80%93-volley-library

public class RestService
{
    //login user
	public static boolean loginUser(int id, String hash, Context context) {
		
		  final String url = CONST.REST_LOGIN;

	      Uri uri = Uri.parse(url);
	      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
	      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
	      HttpPost httppost = new HttpPost(url);
	
		  try {
		      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		      nameValuePairs.add(new BasicNameValuePair("data[User][id]", id+"")); //alias at beginning?!
		      nameValuePairs.add(new BasicNameValuePair("data[User][password]", hash)); //hash value
		      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		
		      // Execute HTTP Post Request
		      HttpResponse response = httpclient.execute(host, httppost);
		
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
		
		          Storage.saveSessionId(context, sessionId);
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
	  
	  final String url = CONST.REST_LOGOUT;
	  String sessionId = Storage.getSessionId(context);

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
      

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request	      httpclient.execute(host, httppost);;
	      Storage.saveSessionId(context, null);
	      return true;
	  }
	  catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  return false;
  }
  
  
  //register user
  public static boolean registerUser(String alias, String hash, Context context) {
	  
	  final String url = CONST.REST_REGISTER;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("alias", alias)); //alias
	      nameValuePairs.add(new BasicNameValuePair("password", hash)); //hash value
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);

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
	  
	  final String url = CONST.REST_CONNECT_FRIEND;
	  String sessionId = Storage.getSessionId(context);
	  String pubKey = Storage.getPublicKeyAsString(context);
	  String alias = Storage.getAlias(context);

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
      try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverID ]", receiverID));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][alias]", alias));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverPin]", receiverPin));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][pubKey]", pubKey));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
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
	  
	  final String url = CONST.REST_SERVICE;
	  String sessionId = Storage.getSessionId(context);

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][sessionId]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
		  Log.d(CONST.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(1==1 || (response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199))
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	          String json = reader.readLine();
	          //TODO TEST JSON
	          json = "{\"chatSession\":[{\"Connection\":{\"id\":\"2\",\"senderId\":\"5\",\"receiverId\":\"10\",\"alias\":\"tu001\",\"receiverPin\":\"1234\",\"pubKey\":\"g0kn4pg0kn4p23oge5ng0kn4p23oge5neqp5f73i1ghf5eqp5f73i1ghf523oge5neqp5f73i1ghf5\"}}]}";
	          JSONObject jsonobject = new JSONObject(json);

	          Log.d(CONST.LOG, "HTTP-Response: ServiceJSON1: " + jsonobject.toString());
	          
	          if(jsonobject.toString() == "{\"chatSession\":[]}")
	        	  return false;

	          JSONArray cast = jsonobject.getJSONArray("chatSession");
	          for (int i=0; i<cast.length(); i++) 
	          {
	        	  Log.d(CONST.LOG, "HTTP-Response: ServiceJSON2: " + cast.getJSONObject(i).getJSONObject("Connection").toString());
	        	  
	              JSONObject connection = cast.getJSONObject(i).getJSONObject("Connection");
	              
	              int id = connection.getInt("id");
	              //String senderId = connection.getString("senderId");
	              int receiverId = connection.getInt("receiverId");
	              String alias = connection.getString("alias");
	              String receiverPin = connection.getString("receiverPin");
	              String pubKey = connection.getString("pubKey");
	              
	              Partner.setPartnerAlias(alias);
	              Partner.setPartnerId(receiverId);
	              Partner.setPartnerPin(receiverPin);
	              Partner.setPartnerPubKey(pubKey);
	              
	              Storage.saveChatSessionId(context, id);
	          }
	          
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
  
  //TODO serivceConnect
  public static Boolean serviceConnect(Context context, String receiverID) {
	String sessionId = Storage.getSessionId(context);
	String pubKey = Storage.getPublicKeyAsString(context);
	String alias = Storage.getAlias(context);
	
	final String url = CONST.REST_CONNECT_FRIEND;

    Uri uri = Uri.parse(url);
    HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
    HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
    HttpPost httppost = new HttpPost(url);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverID ]", receiverID));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][alias]", alias));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][pubKey]", pubKey));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      HttpResponse response = httpclient.execute(host, httppost);
	      
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
  
  //send message
  public static boolean sendChatMessage(int receiverId, String message, Context context) {
	  String sessionId = Storage.getSessionId(context);
	  final String url = CONST.REST_SEND_MSG;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
      boolean messageReceived = false;

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Message][connectionId]", Integer.toString(receiverId)));
	      nameValuePairs.add(new BasicNameValuePair("data[Message][message]", message));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	
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
	          
	          Log.d(CONST.LOG, "HTTP-Response: messageReceived?: " + messageReceived);
	          
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
	  String sessionId = Storage.getSessionId(context);
	  int chatSessionId = Storage.getChatSessionId(context);
	  
	  final String url = CONST.REST_SEND_MSG;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);

      String receivedMessage = "";

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[User][chatSessionId]", chatSessionId +""));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	
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
	          
	          Log.d(CONST.LOG, "HTTP-Response: receiveMessage: " + receivedMessage);
	          
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
