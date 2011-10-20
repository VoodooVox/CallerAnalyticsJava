/* @(#)CaApiBase.java
 * 
 * Created: Sep 19, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Base class for Caller Analytics API commands.
 * <br><br>
 * Uses Java system property "calleranalytics.apikey" as the
 * API Key for Caller Analytics, which must be set before using any Caller
 * Analytics commands. Call 
 * <br><br>&nbsp;&nbsp;&nbsp;
 * <code>System.setProperty( "calleranalytics.apikey", apiKey );</code>
 * <br><br>
 * Or include 
 * <br><br>&nbsp;&nbsp;&nbsp;
 * <code>calleranalytics.apikey=YOUR_API_KEY</code>
 * <br><br>
 * in a Java properties file called "calleranalytics.properties" included in 
 * your classpath.
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class CaApiBase
{
   /**
    * Java system property name for Caller Analytics API Key.
    */
   public static String API_KEY_PROPERTY_NAME = "calleranalytics.apikey";
   
   private static final String URL_BASE = "https://api.calleranalytics.com/";
   
   /**
    * Enumeration of valid API Methods
    */
   public static enum CaApiMethod { Create, Read, Update, Delete }
   
   static 
   {
      try
      {
         InputStream is = CaApiBase.class.getResourceAsStream( "/calleranalytics.properties" );
         if ( is != null )
         {
            Properties p = new Properties(System.getProperties());
            p.load(is);
            System.setProperties(p);
         }
      } catch ( Exception ex ) { }
   }
   
   /**
    * Send API command to CallerAnalytics.
    * 
    * @param command       The API command/class name
    * @param method        The method on the command
    * @param jsonIn        The JSON object to pass to the API command
    * @return              The "response" JSON Object when "result" is "ack" (i.e. successful API call)
    * @throws IOException Usually network error communicating with Caller Analytics
    * @throws JSONException Error parsing JSON response
    * @throws CaApiException On "nack" result.
    */
   public static JSONObject send(String command, CaApiMethod method, JSONObject jsonIn) throws IOException, JSONException, CaApiException
   {
      String apiKey = System.getProperty( API_KEY_PROPERTY_NAME );
      
      if ( apiKey == null )
      {
         throw new NullPointerException( "Java system property '" + API_KEY_PROPERTY_NAME + "' not set" );
      }
      
      String url = URL_BASE + command + "." + method + ".do";
      Map<String, String> data = new HashMap<String, String>();
      data.put( "json", jsonIn.toString() );
      data.put( "key", apiKey );
      
      JSONObject out = CaApiUtil.sendGenericHttpJson( url, data );
      
      JSONObject response = out.optJSONObject( "response" );
      if ( !"ack".equals( out.optString( "result" ) ) )
      {
         throw new CaApiException( command, method.toString(), response );
      }
      
      return response;
   }
}
