/* @(#)CaApiException.java
 * 
 * Created: Sep 19, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.api;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Exception class to represent Caller Analytics error. Thrown on "nack" 
 * result from API.
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class CaApiException extends Exception
{
   private JSONObject response;
   
   /**
    * Create new CaApiException instance.
    * 
    * @param command    The API command/class name
    * @param method     The API method
    * @param response   The JSON "response" object
    * @throws JSONException when there is an error processing JSON "response" object
    */
   public CaApiException( String command, String method, JSONObject response ) throws JSONException
   {
      super( getExceptionMessage( command, method, response ) );
      
      this.response = response;
   }
   
   private static String getExceptionMessage( String command, String method, JSONObject response ) throws JSONException
   {
      String errorKey = "UNKNOWN";
      String errorMessage = "Unknown Error";
      if ( response != null )
      {
         errorKey = response.optString( "errorKey", errorKey );
         errorMessage = response.optString( "message", errorMessage );
      }
      return "Api Error in " + command + "." + method + ".do: " + errorMessage + " [" + errorKey + "]";
   }
   
   /**
    * @return the JSON "response" object
    */
   public JSONObject getResponse()
   {
      return response;
   }
}
