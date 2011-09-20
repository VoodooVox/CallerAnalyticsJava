/* @(#)LookupResponse.java
 * 
 * Created: Sep 19, 2011
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.voodoovox.calleranalytics.api.CaApiBase;
import com.voodoovox.calleranalytics.api.CaApiException;

/**
 * Class to handle the Lookup API command. 
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class Lookup extends CaApiBase
{
   private static final String COMMAND = "Lookup";
   
   /**
    * Perform Lookup.Read on given phone number with optional request profile.
    * 
    * @param phoneNumber         The phone number to lookup
    * @param requestProfile      The request profile to use (pass null to use default)
    * @return Lookup object
    * @throws JSONException Error processing JSON data
    * @throws IOException Error communicating with API
    * @throws CaApiException on "nack" result
    */
   public static Lookup read( String phoneNumber, String requestProfile ) throws JSONException, IOException, CaApiException
   {
      JSONObject jsonIn = new JSONObject();
      jsonIn.put( "phoneNumber", phoneNumber );
      if ( requestProfile != null )
      {
         jsonIn.put( "requestProfile", requestProfile );
      }
      
      return new Lookup( send( COMMAND, CaApiMethod.Read, jsonIn ) );
   }
   
   private Map<String, String> data;
   private List<Map<String, String>> members;
   
   /**
    * Create new Lookup object from JSON response.
    * 
    * @param response The JSON "response" object
    * @throws JSONException when error parsing JSON "response" object
    */
   public Lookup(JSONObject response) throws JSONException
   {
      data = new HashMap<String, String>();
      members = new ArrayList<Map<String,String>>();
      
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
    * @return the data (i.e. attributes of the caller)
    */
   public Map<String, String> getData()
   {
      return data;
   }

   /**
    * @return the members
    */
   public List<Map<String, String>> getMembers()
   {
      return members;
   }
}
