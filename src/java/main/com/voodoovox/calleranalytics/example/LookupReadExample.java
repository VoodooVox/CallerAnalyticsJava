package com.voodoovox.calleranalytics.example;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;

import com.voodoovox.calleranalytics.api.CaApiException;
import com.voodoovox.calleranalytics.api.classes.Lookup;

/**
 * Example usage of Caller Analytics API
 */
public class LookupReadExample 
{
   public static void main(String[] args)
   {
      String phoneNumber = null;
      
      int finalArgIdx = 0;
      for ( int i = 0; i < args.length; i++ )
      {
         if ( "--key".equals( args[i] ) )
         {
            if ( ( i + 1 ) >= args.length ) printUsage();
            
            System.setProperty( "calleranalytics.apikey", args[++i] );
         }
         else
         {
            switch ( finalArgIdx )
            {
            case 0:
               phoneNumber = args[i];
               break;
            }
            finalArgIdx++;
         }
      }
      
      if ( finalArgIdx < 1 )
      {
         printUsage();
      }
      
      System.out.println("================================");
      System.out.println(" Looking up " + phoneNumber );
      System.out.println("================================");
      
      try
      {
         Lookup lookup = Lookup.read( phoneNumber, null );
         
         for ( Entry<String, String> dataEntry: lookup.getData().entrySet() )
         {
            System.out.println( dataEntry.getKey() + " : " +dataEntry.getValue() );
         }
         
         System.out.println( "members (" + lookup.getMembers().size() + ") : " );
         
         int i = 1;
         for ( Map<String, String> memberItem: lookup.getMembers() )
         {
            System.out.println("   member " + i );
            
            for ( Entry<String, String> memberDataEntry: memberItem.entrySet() )
            {
               System.out.println("      " + memberDataEntry.getKey() + " : " + memberDataEntry.getValue() );
            }
            
            i++;
         }
      }
      catch ( CaApiException ex )
      {
         System.err.println("Error: ");
         try
         {
            System.err.println( ex.getResponse().toString( 3 ) );
         } 
         catch ( JSONException ex2 )
         {
            ex2.printStackTrace();
         }
         ex.printStackTrace();
         System.exit(1);
      }
      catch ( Exception ex )
      {
         ex.printStackTrace();
         System.exit(1);
      }
   }

   private static void printUsage()
   {
      System.out.println("Usage: com.voodoovox.calleranalytics.example.LookupReadExample [--key API_KEY] PHONE_NUMBER");
      System.exit(1);
   }
}
