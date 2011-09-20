package com.voodoovox.calleranalytics.api;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Utility class to interact with JSON-based API (i.e. Caller Analytics).
 */
public class CaApiUtil 
{
   /**
    * Generic method for retrieving JSON documents via HTTP.
    * 
    * @param urlStr  The URL string.
    * @param data    A Map of the HTTP POST data (null to send none)
    * @return the HTTP response in a JSONObject
    * @throws IOException Usually network error communicating with remote server
    * @throws JSONException Error parsing JSON response
    */
   public static JSONObject sendGenericHttpJson(String urlStr, Map<String,String> data) throws IOException, JSONException
   {
      URL url = new URL( urlStr );
      
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoInput( true );
      
      if ( data != null && data.size() > 0 )
      {
         StringBuilder outBuilder = new StringBuilder();
         for ( Entry<String, String> entry: data.entrySet() ) 
         {
            if ( outBuilder.length() > 0 )
            {
               outBuilder.append( '&' );
            }
            
            outBuilder.append( URLEncoder.encode( entry.getKey(), "UTF-8" ) );
            outBuilder.append( '=' );
            outBuilder.append( URLEncoder.encode( entry.getValue(), "UTF-8" ) );
         }
         
         String outStr = outBuilder.toString();
         
         conn.setDoOutput( true );
         conn.setRequestMethod( "POST" );
         conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded" );
         conn.setRequestProperty( "Content-Length", Integer.toString( outStr.length() ) );
         
         OutputStreamWriter out = new OutputStreamWriter( conn.getOutputStream() );
         out.write( outStr );
         out.flush();
      }
      
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
