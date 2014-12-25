package cs.hm.edu.sisy.chat.communication;

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
import cs.hm.edu.sisy.chat.enums.SCConstants;
import cs.hm.edu.sisy.chat.generators.PubPrivKeyGenerator;
import cs.hm.edu.sisy.chat.objects.Partner;
import cs.hm.edu.sisy.chat.storage.SharedPrefs;
import cs.hm.edu.sisy.chat.tools.EasySSLSocketFactory;

//alternative: rest lib: http://java.dzone.com/articles/android-%E2%80%93-volley-library

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
		
		      Log.d(SCConstants.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
		      
		      if(response.getStatusLine().getStatusCode() < 300 && 
		          response.getStatusLine().getStatusCode() > 199)
		      {
		        try
		        {
		          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
		          
		          String json = reader.readLine();
		          JSONObject jsonobject = new JSONObject(json);
		          
		          String sessionId = jsonobject.getString("sessionId");
		          
		          Log.d(SCConstants.LOG, "HTTP-Response: SessionID: " + sessionId);	
		
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

		  Log.d(SCConstants.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
	      if(response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199)
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));

	          String json = reader.readLine();
	          JSONObject jsonobject = new JSONObject(json);
	          
	          int userId = jsonobject.getInt("userId");
	          
	          Log.d(SCConstants.LOG, "HTTP-Response: ID: " + userId);
	
	          SharedPrefs.saveID(context, userId);
	          
	          //httpclient.getConnectionManager().shutdown(); //TODO ??
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
	  
	  //httpclient.getConnectionManager().shutdown(); //TODO ??
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
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverID ]", receiverID));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][alias]", alias));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][receiverPin]", receiverPin));
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][pubKey]", pubKey));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
		  Log.d(SCConstants.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
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
	          
	          Log.d(SCConstants.LOG, "HTTP-Response: chatSessionId: " + chatSessionId);
	          
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
	      nameValuePairs.add(new BasicNameValuePair("data[Connection][sessionID]", sessionId));
	      httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	
	      // Execute HTTP Post Request
	      HttpResponse response = httpclient.execute(host, httppost);
	      
		    Log.d(SCConstants.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
		    //TODO TEST 1==1
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

	          Log.d(SCConstants.LOG, "HTTP-Response: ServiceJSON1: " + jsonobject.toString());
	          
	          if(jsonobject.toString() == "{\"chatSession\":[]}")
	        	  return false;
	          
	          //TODO: if sessionId is expired, catch here through "special json response" which leads to ...
	          //if(jsonobject.toString() == "BLABLA") {
	          // new RestThreadTask(SCTypes.LOGIN, (Context) Login.this).execute()
	          // return false;
	          //}

	          JSONArray cast = jsonobject.getJSONArray("chatSession");
	          for (int i=0; i<cast.length(); i++) 
	          {
	        	  Log.d(SCConstants.LOG, "HTTP-Response: ServiceJSON2: " + cast.getJSONObject(i).getJSONObject("Connection").toString());
	        	  
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
	              
	              SharedPrefs.saveChatSessionId(context, id);
	          }
	          
	          //httpclient.getConnectionManager().shutdown(); //TODO ??
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
	  
	  //httpclient.getConnectionManager().shutdown(); //TODO ??
      return false;
  }
  
  //TODO serivceConnect
  public static Boolean serviceConnect(Context context, String receiverID) {
	String sessionId = SharedPrefs.getSessionId(context);
	String pubKey = PubPrivKeyGenerator.getOwnPublicKeyAsString(context);
	String alias = SharedPrefs.getAlias(context);
	
	final String url = SCConstants.REST_CONNECT_FRIEND;

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
	      
		  Log.d(SCConstants.LOG, "HTTP-Status: "+response.getStatusLine().getStatusCode());
	
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
	          
	          Log.d(SCConstants.LOG, "HTTP-Response: chatSessionId: " + chatSessionId);
	          
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
  public static boolean sendChatMessage(int receiverId, String message, Context context) {
	  String sessionId = SharedPrefs.getSessionId(context);
	  final String url = SCConstants.REST_SEND_MSG;

      Uri uri = Uri.parse(url);
      HttpClient httpclient = EasySSLSocketFactory.getNewHttpClient();
      HttpHost host = new HttpHost(uri.getHost(), 443, uri.getScheme());
      HttpPost httppost = new HttpPost(url);
	
      boolean messageReceived = false;

	  try {
	      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	      nameValuePairs.add(new BasicNameValuePair("data[User][sessionId]", sessionId));
	      nameValuePairs.add(new BasicNameValuePair("data[Message][connectionId]", Integer.toString(receiverId)));
	      nameValuePairs.add(new BasicNameValuePair("data[Message][message]", encryptChatMsg(message,context,Partner.getPartnerPubKey())));
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
	          
	          Log.d(SCConstants.LOG, "HTTP-Response: messageReceived?: " + messageReceived);
	          
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
	  int chatSessionId = SharedPrefs.getChatSessionId(context);
	  
	  final String url = SCConstants.REST_SEND_MSG;

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

	       //TODO TEST 1==1
	      if(1==1 || (response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199))
	      {

	          //TODO: for test outcommented
	          /*BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          JSONTokener tokener = new JSONTokener(json);
	          JSONArray jsonarray = new JSONArray(tokener);
	
	          JSONObject jsonobject = jsonarray.getJSONObject(0);
	          receivedMessage = jsonobject .getString("receivedMessage");*/
	          
	          //TODO for tests
	          receivedMessage = encryptChatMsg("Hello, sub?",context,PubPrivKeyGenerator.getOwnPublicKeyAsString(context));
	          
	          //Log.d(SCConstants.LOG, "HTTP-Response: receiveMessage: " + receivedMessage);
	          //Log.d(SCConstants.LOG, "HTTP-Response: receiveMessage: " + decryptChatMsg(receivedMessage,context,PubPrivKeyGenerator.getOwnPrivateKeyAsString(context)));
	          
	          Partner.setPartnerNewMsg(decryptChatMsg(receivedMessage,context,PubPrivKeyGenerator.getOwnPrivateKeyAsString(context)));
	          
	          return true;
	      }
	  } catch (ClientProtocolException e) {
	  } catch (IOException e) {
	  }
	  
   return false;
 }
	      
	      /*
	       //TODO TEST 1==1
	      if(1==1 || (response.getStatusLine().getStatusCode() < 300 && 
	          response.getStatusLine().getStatusCode() > 199))
	      {
	        try
	        {
	          BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
	          String json = reader.readLine();
	          JSONTokener tokener = new JSONTokener(json);
	          JSONArray jsonarray = new JSONArray(tokener);
	
	          JSONObject jsonobject = jsonarray.getJSONObject(0);
	          receivedMessage = jsonobject .getString("receivedMessage");
	          
	          //TODO for tests
	          receivedMessage = encryptChatMsg("Hello, sub?",context,Partner.getPartnerPubKey());
	          
	          Log.d(SCConstants.LOG, "HTTP-Response: receiveMessage: " + decryptChatMsg(receivedMessage,context));
	          
        	  Partner.setPartnerNewMsg(decryptChatMsg(receivedMessage,context));
	          
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
  }*/
  
	//ver... encrypt with partners public key
	private static String encryptChatMsg(String msg, Context context, String pubKeyStr) {
	
		return PubPrivKeyGenerator.encrypt(msg, context, pubKeyStr);
	}
	
	//ent... decrypt with my own private key
	private static String decryptChatMsg(String msg, Context context, String privKeyStr) {
    
	  return PubPrivKeyGenerator.decrypt(msg, context, privKeyStr);
	}
  
}
