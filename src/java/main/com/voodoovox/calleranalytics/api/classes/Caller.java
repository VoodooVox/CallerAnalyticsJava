/* @(#)Caller.java
 * 
 * Created: Oct 3, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.api.classes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.voodoovox.calleranalytics.api.CaApiBase;
import com.voodoovox.calleranalytics.api.CaApiException;

/**
 * Class to handle the Caller API command. 
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class Caller extends CaApiBase
{
   private static final String COMMAND = "Caller";
   
   /**
    * Perform Caller.Read on given phone number with optional request profile.
    * 
    * @param phoneNumber         The phone number to lookup
    * @param requestProfile      The request profile to use (pass null to use default)
    * @return Caller object
    * @throws JSONException Error processing JSON data
    * @throws IOException Error communicating with API
    * @throws CaApiException on "nack" result
    */
   public static Caller read( String phoneNumber, String requestProfile ) throws JSONException, IOException, CaApiException
   {
      JSONObject jsonIn = new JSONObject();
      jsonIn.put( "phoneNumber", phoneNumber );
      if ( requestProfile != null )
      {
         jsonIn.put( "requestProfile", requestProfile );
      }
      
      return new Caller( phoneNumber, send( COMMAND, CaApiMethod.Read, jsonIn ) );
   }
   
   private String phoneNumber;
   
   private Map<String, String> data;
   private List<Map<String, String>> members;
   
   /**
    * Create new empty Caller object
    * 
    * @param phoneNumber The phoneNumber of the caller
    */
   public Caller( String phoneNumber )
   {
      this.phoneNumber = phoneNumber;
      this.data = new HashMap<String, String>();
      this.members = new ArrayList<Map<String,String>>();
   }
   
   /**
    * Create new Caller object from JSON response.
    * 
    * @param response The JSON "response" object
    * @throws JSONException when error parsing JSON "response" object
    */
   public Caller( String phoneNumber, JSONObject response) throws JSONException
   {
      this.phoneNumber = phoneNumber;
      this.data = new HashMap<String, String>();
      this.members = new ArrayList<Map<String,String>>();
      
      Iterator<?> responseIt = response.keys();
      while ( responseIt.hasNext() )
      {
         String key = (String) responseIt.next();
         
         if ( "members".equals( key ) )
         {
            JSONArray membersJson = response.getJSONArray( key );
            
            for ( int i = 0; i < membersJson.length(); i++ )
            {
               HashMap<String, String> memberData = new HashMap<String, String>();
               
               JSONObject member = (JSONObject) membersJson.getJSONObject( i );
               Iterator<?> memberIt = member.keys();
               
               while ( memberIt.hasNext() )
               {
                  String memberKey = (String) memberIt.next();
                  Object memberValue = member.get( memberKey );
                  memberData.put( memberKey, memberValue.toString() );
               }
               
               members.add( memberData );
            }
         }
         else
         {
            Object value = response.get( key );
            data.put( key, value.toString() );
         }
      }
   }
   
   /**
    * @return the phoneNumber
    */
   public String getPhoneNumber()
   {
      return phoneNumber;
   }

   /**
    * @return the data (i.e. attributes of the caller)
    */
   public Map<String, String> getData()
   {
      return data;
   }
   
   /**
    * Add data to the caller.
    * 
    * @param name    name of the attribute to add
    * @param value   value of the attribute to add
    */
   public void addData(String name, String value)
   {
      data.put( name, value );
   }

   /**
    * @return the members
    */
   public List<Map<String, String>> getMembers()
   {
      return members;
   }
   
   /**
    * Update caller object for given phoneNumber with given data.
    * <br><br>
    * NOTE: currently omits members from update.
    * 
    * @throws JSONException
    * @throws IOException
    * @throws CaApiException
    */
   public void update() throws JSONException, IOException, CaApiException
   {
      JSONObject jsonIn = new JSONObject();
      jsonIn.put( "phoneNumber", phoneNumber );
      
      JSONObject callerObj = new JSONObject();
      
      if ( data != null )
      {
         for ( Entry<String, String> entry: data.entrySet() )
         {
            callerObj.put( entry.getKey(), entry.getValue() );
         }
      }
      
      jsonIn.put( "caller", callerObj );
      
      send( COMMAND, CaApiMethod.Update, jsonIn );
   }
}
