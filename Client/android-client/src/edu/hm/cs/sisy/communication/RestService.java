package edu.hm.cs.sisy.communication;

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

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import edu.hm.cs.sisy.enums.SCConstants;
import edu.hm.cs.sisy.enums.SCPartner;
import edu.hm.cs.sisy.enums.SCState;
import edu.hm.cs.sisy.generators.PinHashGenerator;
import edu.hm.cs.sisy.generators.PubPrivKeyGenerator;
import edu.hm.cs.sisy.objects.Partner;
import edu.hm.cs.sisy.storage.SharedPrefs;
import edu.hm.cs.sisy.tools.Common;
import edu.hm.cs.sisy.tools.EasySSLSocketFactory;

public class RestService
{
    //login user
	public static boolean loginUser(int id, String hash, Context context) {
		
		  final String url = SCConstants.REST_LOGIN;

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
		
		      Log.d(SCConstants.LOG, "Login: HTTP-Status: "+response.getStatusLine().getStatusCode());
		      
		      if(response.getStatusLine().getStatusCode() < 300 && 
		          response.getStatusLine().getStatusCode() > 199)
		      {
		        try
		        {
		          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

		          String json = reader.readLine();
		          JSONObject jsonobject = new JSONObject(json);

		          String sessionId = jsonobject.getString("sessionId");

		          Log.d(SCConstants.LOG, "Login: HTTP-Response: SessionID: " + sessionId);	
		
		          SharedPrefs.saveSessionId(context, sessionId);
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
	  
	  final String url = SCConstants.REST_LOGOUT;
	  String sessionId = SharedPrefs.getSessionId(context);

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
      

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request	      httpclient.execute(host, httppost);;
	      SharedPrefs.saveSessionId(context, null);
	      return true;
	  }
	  catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  return false;
  }
  
  
  //register user
  public static boolean registerUser(String alias, String hash, Context context) {
	  
	  final String url = SCConstants.REST_REGISTER;

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

		  Log.d(SCConstants.LOG, "Register: HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	          String json = reader.readLine();
	          JSONObject jsonobject = new JSONObject(json);
	          
	          int userId = jsonobject.getInt("userId");
	          
	          Log.d(SCConstants.LOG, "Register: HTTP-Response: ID: " + userId);
	
	          SharedPrefs.saveID(context, userId);

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
  public static Boolean connectPrivChat(Context context, String receiverPin, String receiverID) {
	  
	  final String url = SCConstants.REST_CONNECT_FRIEND;
	  String sessionId = SharedPrefs.getSessionId(context);
	  String pubKey = PubPrivKeyGenerator.getOwnPublicKeyAsString(context);
	  String alias = SharedPrefs.getAlias(context);

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
      try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverId]", receiverID));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][alias]", alias));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverPin]", receiverPin));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][pubKey]", pubKey));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	      
	      /*
	      Log.d(SCConstants.LOG, "Link: "+SCConstants.REST_CONNECT_FRIEND);
		  Log.d(SCConstants.LOG, "data[User][sessionId]: "+sessionId);
		  Log.d(SCConstants.LOG, "data[Connection][receiverId]: "+receiverID);
		  Log.d(SCConstants.LOG, "data[Connection][alias]: "+alias);
		  Log.d(SCConstants.LOG, "data[Connection][receiverPin]: "+receiverPin);
		  Log.d(SCConstants.LOG, "data[Connection][pubKey]: "+pubKey);
		  */
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
		  Log.d(SCConstants.LOG, "Connect: HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          
	          //if sessionId is expired, catch here and logg out
	          if(json.equalsIgnoreCase("[\"disconnected\"]")) {
	        	  Common.disconnectedFromSystem(context);
	        	  return false;
	          }
	          
	          //If there is no valid request...
	          //TODO ATM: disconnected! Sollte mit SessionID-Fix gefixt sein.
	          if(json.equalsIgnoreCase("null") || json == null || json.equalsIgnoreCase("") || json.isEmpty() || json == JSONObject.NULL)
	        	  return false;
	          
	          JSONObject jsonobject = new JSONObject(json);
	          
	          int chatSessionId = jsonobject .getInt("chatSessionId");
	          
	          Log.d(SCConstants.LOG, "Connect: HTTP-Response: chatSessionId: " + chatSessionId);
	          
	          if(chatSessionId != 0)
	        	  SharedPrefs.saveChatSessionId(context, chatSessionId);
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
	  
	  final String url = SCConstants.REST_SERVICE;
	  String sessionId = SharedPrefs.getSessionId(context);

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
		    Log.d(SCConstants.LOG, "Service: HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          
	          //If there is no request...
	          //TODO ATM: disconnected! Sollte mit SessionID-Fix gefixt sein.
	          if(json.equalsIgnoreCase("null") || json == null || json.equalsIgnoreCase("") || json.isEmpty() || json == JSONObject.NULL)
	        	  return false;
	          
	          //if sessionId is expired, catch here and logg out
	          if(json.equalsIgnoreCase("[\"disconnected\"]")) {
	        	  Common.disconnectedFromSystem(context);
	        	  return false;
	          }
	          
	          //TEST-JSON:
	          //json = "{\"chatSession\":[{\"Connection\":{\"id\":\"2\",\"senderId\":\"5\",\"receiverId\":\"10\",\"alias\":\"tu001\",\"receiverPin\":\"1234\",\"pubKey\":\"g0kn4pg0kn4p23oge5ng0kn4p23oge5neqp5f73i1ghf5eqp5f73i1ghf523oge5neqp5f73i1ghf5\"}}]}";
	          JSONObject jsonobject = new JSONObject(json);

	          //Log.d(SCConstants.LOG, "Service: HTTP-Response: ServiceJSON: " + jsonobject.toString());
	          
	          //If there is no request...
	          if( jsonobject.toString().equalsIgnoreCase("{\"chatSession\":[]}") ) {
	        	  Log.d(SCConstants.LOG, "Service: waiting...");
	        	  return false;
	          }
	          else 
	        	  Log.d(SCConstants.LOG, "Service: we 're gettin something!");
	          
	          //if chatSessionId is expired, catch here and logg out
	          if(jsonobject.toString().equalsIgnoreCase("{chatDisconnected}")) {
	        	  SCState.setState(SCState.LOGGED_IN, context, false);
	        	  return false;
	          }

	          JSONArray cast = jsonobject.getJSONArray("chatSession");
	          for (int i=0; i<cast.length(); i++) 
	          {
	        	  //Log.d(SCConstants.LOG, "Service: HTTP-Response: ServiceJSON2: " + cast.getJSONObject(i).getJSONObject("Connection").toString());

	              JSONObject connection = cast.getJSONObject(i).getJSONObject("Connection");

	              String pubKey = connection.getString("pubKey");
	              int senderId = connection.getInt("senderId"); //partnerId
	              String receiverPin = connection.getString("receiverPin"); //partnerPin
	              String alias = connection.getString("alias");

	              //either be sender or id and pin should be valid
	              if( Partner.getType() == SCPartner.SENDER || PinHashGenerator.validatePin(context, receiverPin) )
	              {
		              Partner.setPartnerAlias(alias);
		              Partner.setPartnerId(senderId);
		              Partner.setPartnerPin(receiverPin);
		              Partner.setPartnerPubKey(pubKey);
		              
		              //TODO: if pubKey/json broken check
		              //http://stackoverflow.com/questions/8934412/mysql-to-php-to-json-string-cannot-be-converted-to-jsonobject
		              //http://stackoverflow.com/questions/3020094/how-should-i-escape-strings-in-json
		              //http://stackoverflow.com/questions/16574482/decoding-json-string-in-java
	              }
	              else
	            	  return false;
	          }
	          
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
  
  //serivceConnect
  public static Boolean serviceConnect(Context context) {
	String sessionId = SharedPrefs.getSessionId(context);
	String pubKey = PubPrivKeyGenerator.getOwnPublicKeyAsString(context);
	String alias = SharedPrefs.getAlias(context);
	String receiverID = Partner.getPartnerId()+"";
	
	final String url = SCConstants.REST_SERVICE_CONNECT;

    Uri uri = Uri.parse(url);
    HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
    HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
    HttpPost httppost = new HttpPost(url);
	
	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverId]", receiverID));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][alias]", alias));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][pubKey]", pubKey));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      HttpResponse response = httpclient.execute(host, httppost);
	      
	      Log.d(SCConstants.LOG, "ServiceConnect: connect to id "+receiverID);
	      
		  Log.d(SCConstants.LOG, "ServiceConnect: HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          
	          //if sessionId is expired, catch here and logg out
	          if(json.equalsIgnoreCase("[\"disconnected\"]")) {
	        	  Common.disconnectedFromSystem(context);
	        	  return false;
	          }
	          
	          JSONObject jsonobject = new JSONObject(json);
	          
        	  Log.d(SCConstants.LOG, "ServiceConnect: HTTP-Response: ServiceConnectJSON: " + json);
	          
	          int chatSessionId = jsonobject.getInt("chatSessionId");
	          
	          Log.d(SCConstants.LOG, "ServiceConnect: HTTP-Response: chatSessionId: " + chatSessionId);
	          
	          if(chatSessionId != 0)
	        	  SharedPrefs.saveChatSessionId(context, chatSessionId);
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
  public static boolean sendChatMessage(String message, Context context) {
	  String sessionId = SharedPrefs.getSessionId(context);
	  int chatSessionId = SharedPrefs.getChatSessionId(context);
	  final String url = SCConstants.REST_SEND_MSG;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
      boolean messageReceived = false;

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Message][connectionId]", Integer.toString(chatSessionId)));
	      nameValuePairs.add(new BasicNameValuePair("data[Message][message]", encryptChatMsg(message,context,Partner.getPartnerPubKey())));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
		  Log.d(SCConstants.LOG, "Send: HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          
	          //if sessionId is expired, catch here and logg out
	          if(json.equalsIgnoreCase("[\"disconnected\"]")) {
	        	  Common.disconnectedFromSystem(context);
	        	  return false;
	          }
	          
	          JSONObject jsonobject = new JSONObject(json);
	          
	          //if sessionId is expired, catch here and logg out
	          if(jsonobject.toString().equalsIgnoreCase("{\"chatDisconnected\":true}")) {
	        	  SCState.setState(SCState.LOGGED_IN, context, false);
	        	  return false;
	          }
	          
	          messageReceived = jsonobject .getString("received").equals("true");
	          
	          Log.d(SCConstants.LOG, "Send: HTTP-Response: messageReceived?: " + messageReceived);
	          
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
  public static boolean receiveChatMessage(Context context) {
	  String sessionId = SharedPrefs.getSessionId(context);
	  
	  final String url = SCConstants.REST_RECEIVE_MSG;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);

      String receivedMessage = "";

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
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
	          
        	  Log.d(SCConstants.LOG, "Receive: HTTP-Response: receiveChatMessageJSON: " + json);
	          
	          //if chatSessionId is destroyed, catch here and logg out
	          if(json.equalsIgnoreCase("[\"chatDisconnected\"]")) {
	        	  //TODO global variable true that user left chat -> change GUI
	        	  SCState.setState(SCState.LOGGED_IN, context, false);
	        	  return false;
	          }
	          
	          //if sessionId is expired, catch here and logg out
	          if(json.equalsIgnoreCase("[\"disconnected\"]")) {
	        	  Common.disconnectedFromSystem(context);
	        	  return false;
	          }

	          JSONObject jsonobject = new JSONObject(json);
	          
		      //If there is no message to receive...
	          if( jsonobject.toString().equalsIgnoreCase("{\"receivedMessages\":[]}") || jsonobject.toString().equalsIgnoreCase("{\"receivedMessages\": null}") )
	        	  return false;
	          
	          JSONArray cast = jsonobject.getJSONArray("receivedMessages");
	          for (int i=0; i<cast.length(); i++) 
	          {
	              JSONObject message = cast.getJSONObject(i).getJSONObject("Message");
	              
	              receivedMessage += message.getString("message");
	              //id
	              //conectionId
	              //senderId
	          }

	          String decryptedMessage = decryptChatMsg(receivedMessage,context,PubPrivKeyGenerator.getOwnPrivateKeyAsString(context));

        	  Log.d(SCConstants.LOG, "Receive: HTTP-Response: receiveChatMessageJSON2: " + decryptedMessage);

	          //for tests
	          //receivedMessage = encryptChatMsg("Hello, sub?",context,PubPrivKeyGenerator.getOwnPublicKeyAsString(context));
	          
	          //Log.d(SCConstants.LOG, "HTTP-Response: receiveMessage: " + receivedMessage);
	          //Log.d(SCConstants.LOG, "HTTP-Response: receiveMessage: " + decryptChatMsg(receivedMessage,context,PubPrivKeyGenerator.getOwnPrivateKeyAsString(context)));
	          
	          Partner.setPartnerNewMsg(decryptedMessage);
	          
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
  
  //Destroy Chat Session
  public static Boolean destroyChatSession(Context context, String chatSessionIdBackup) {

	  final String url = SCConstants.REST_DESTROY_CHAT_SESSION;
	  String sessionId = SharedPrefs.getSessionId(context);
	  int chatSessionId = SharedPrefs.getChatSessionId(context);
	  String chatSessionIdStr = "";
	  
	  //TODO: this is a workaround cuz of Common.resetClient - async request
	  //on chatSessionId (race conditions)
	  if(chatSessionId != 0)
		  chatSessionIdStr = Integer.toString(chatSessionId);
	  else
		  chatSessionIdStr = chatSessionIdBackup;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][id]", chatSessionIdStr));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);

		  Log.d(SCConstants.LOG, "Destroy: HTTP-Status: "+response.getStatusLine().getStatusCode());

	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	          String json = reader.readLine();
	          
	          Log.d(SCConstants.LOG, "DestroyChat: HTTP-Response: " + json);
	          
	          JSONObject jsonobject = new JSONObject(json);
	          
	          if(jsonobject.toString().equalsIgnoreCase("{\"chatDisconnected\":true}")) {
	        	  return true;
	          }

	          return false;
	        }
	        catch ( JSONException e )
	        {
	          e.printStackTrace();
	        }
	      }

	      //TODO: have to be false, json above should be null or something like that
	      return true;
	  }
	  catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  return false;
  }

	//ver... encrypt with partners public key
	private static String encryptChatMsg(String msg, Context context, String pubKeyStr) {
	
		return PubPrivKeyGenerator.encrypt(msg, context, pubKeyStr);
	}
	
	//ent... decrypt with my own private key
	private static String decryptChatMsg(String msg, Context context, String privKeyStr) {
    
	  return PubPrivKeyGenerator.decrypt(msg, context, privKeyStr);
	}
  
}
