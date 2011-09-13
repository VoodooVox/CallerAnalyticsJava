package com.voodoovox.calleranalytics.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to interact with Caller Analytics API.
 */
public class CaApiUtil {
   private static final String URL_BASE = "http://ca-api.voodoovox.com/";
   
   /**
    * Enumeration of valid API Methods
    */
   public static enum CaApiMethod { Create, Read, Update, Delete }
   
   /**
    * Send API command to CallerAnalytics.
    * 
    * @param apiKey        The API key for the application
    * @param command       The API command name
    * @param method        The method on the command
    * @param jsonIn        The JSON object to pass into API command
    * @return              The JSON object returned by API command
    * @throws IOException Usually network error communicating with Caller Analytics
    * @throws JSONException Error parsing JSON response
    */
   public static JSONObject send(String apiKey, String command, CaApiMethod method, JSONObject jsonIn) throws IOException, JSONException
   {
      String urlStr = URL_BASE + command + "." + method + ".do?json=" + URLEncoder.encode( jsonIn.toString(), "UTF-8" ) + "&key=" + apiKey;
      
      URL url = new URL( urlStr );
      
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoInput( true );
      
      int responseCode = conn.getResponseCode();
      if ( responseCode == HttpURLConnection.HTTP_OK )
      {
         StringBuilder sb = new StringBuilder();
         final char[] buff = new char[1024];
         int len;
         InputStreamReader in = new InputStreamReader( conn.getInputStream() );
         while ( ( len = in.read( buff, 0, 1024 ) ) >= 0 )
         {
            sb.append( buff, 0, len );
         }
         
         return new JSONObject( sb.toString() );
      }
      else
      {
         throw new IOException( "Invalid response code: " + responseCode );
      }
   }

}
