package com.voodoovox.calleranalytics.api;

import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;

import com.voodoovox.calleranalytics.api.CaApiUtil.CaApiMethod;

/**
 * Example usage of Caller Analytics API
 */
public class CaApiExample {
   public static void main(String[] args)
   {
      if ( args.length != 2 )
      {
         System.out.println("Usage: com.voodoovox.calleranalytics.api.CaApiExample API_KEY PHONE_NUMBER");
         System.exit(1);
      }
      
      String apiKey = args[0];
      String phoneNumber = args[1];
      
      System.out.println("================================");
      System.out.println(" Looking up " + phoneNumber );
      System.out.println("================================");
      
      try
      {
         JSONObject jsonIn = new JSONObject();
         jsonIn.put("phoneNumber", phoneNumber);
         
         JSONObject jsonOut = CaApiUtil.send(apiKey, "Lookup", CaApiMethod.Read, jsonIn);
         
         JSONObject response = jsonOut.getJSONObject( "response" );
         String result = jsonOut.getString( "result" );
         
         if ( !"ack".equals( result ) )
         {
            System.out.println( "Error processing response:" );
            System.out.println( response == null ? "UNKNOWN RESPONSE" : response.toString(3) );
            System.exit(1);
         }
         
         Iterator responseIt = response.keys();
         while ( responseIt.hasNext() )
         {
            String key = (String) responseIt.next();
            
            if ( "members".equals( key ) )
            {
               System.out.println("members : ");
               JSONArray members = response.getJSONArray( key );
               
               for ( int i = 0; i < members.length(); i++ )
               {
                  JSONObject member = (JSONObject) members.getJSONObject( i );
                  Iterator memberIt = member.keys();
                  
                  System.out.println("   member " + ( i + 1 ) );
                  
                  while ( memberIt.hasNext() )
                  {
                     String memberKey = (String) memberIt.next();
                     Object memberValue = member.get( memberKey );
                     System.out.println("      " + memberKey + " : " + memberValue );
                  }
               }
            }
            else
            {
               Object value = response.get( key );
               
               System.out.println(key + " : " + value);
            }
         }
      }
      catch ( Exception ex )
      {
         ex.printStackTrace();
         System.exit(1);
      }
   }
}
