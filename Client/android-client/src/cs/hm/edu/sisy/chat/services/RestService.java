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

import cs.hm.edu.sisy.chat.tools.CONSTANTS;

public class RestService
{

  //example for register user
  public void postData() {
    HttpClient httpclient = new DefaultHttpClient();
    HttpPost httppost = new HttpPost(CONSTANTS.REST_REGISTER);

  try {
      List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
      nameValuePairs.add(new BasicNameValuePair("data[User][alias]", "Hase")); //alias
      nameValuePairs.add(new BasicNameValuePair("data[User][password]", "123")); //hash value
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
          String userId = jsonobject .getString("userId");
          //TODO: storage user id
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
  
}
