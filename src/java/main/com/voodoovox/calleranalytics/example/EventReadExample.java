/* @(#)EventReadExample.java
 * 
 * Created: Sep 20, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.example;

import java.text.SimpleDateFormat;

import org.json.JSONException;

import com.voodoovox.calleranalytics.api.CaApiException;
import com.voodoovox.calleranalytics.api.classes.Event;
import com.voodoovox.calleranalytics.api.classes.EventResponse;

/**
 * 
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class EventReadExample
{
   private static final String DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss aa";
   
   public static void main( String[] args )
   {
      boolean lookup = false;
      String requestProfile = null;
      Integer pageSize = null;
      String lastEvent = null;
      
      String dataBucket = null;
      String eventType = null;
      String start = null;
      String end = null;
      
      int finalArgIdx = 0;
      for ( int i = 0; i < args.length; i++ )
      {
         if ( "--key".equals( args[i] ) )
         {
            if ( ( i + 1 ) >= args.length ) printUsage();
            
            System.setProperty( "calleranalytics.apikey", args[++i] );
         }
         else if ( "--lookup".equals( args[i] ) )
         {
            lookup = true;
         }
         else if ( "--requestProfile".equals( args[i] ) )
         {
            if ( ( i + 1 ) >= args.length ) printUsage();
            
            requestProfile = args[++i];
         }
         else if ( "--requestProfile".equals( args[i] ) )
         {
            if ( ( i + 1 ) >= args.length ) printUsage();
            
            requestProfile = args[++i];
         }
         else if ( "--pageSize".equals( args[i] ) )
         {
            if ( ( i + 1 ) >= args.length ) printUsage();
            
            pageSize = new Integer(args[++i]);
         }
         else if ( "--lastEvent".equals( args[i] ) )
         {
            if ( ( i + 1 ) >= args.length ) printUsage();
            
            lastEvent = args[++i];
         }
         else
         {
            switch ( finalArgIdx )
            {
            case 0:
               dataBucket = args[i];
               break;
            case 1:
               eventType = args[i];
               break;
            case 2:
               start = args[i];
               break;
            case 3:
               end = args[i];
               break;
            }
            finalArgIdx++;
         }
      }
      
      if ( finalArgIdx < 4 )
      {
         printUsage();
      }
      
      try
      {
         SimpleDateFormat timeFormatter = new SimpleDateFormat( DATE_TIME_FORMAT );
         
         EventResponse eventResponse = Event.read( dataBucket, eventType, start, end, pageSize, lastEvent, lookup, requestProfile );
         System.out.println( "Last Event : " + eventResponse.getLastEvent() );
         System.out.println( "Events : (" + eventResponse.getEvents().size() + ")");
         for ( Event event: eventResponse.getEvents() )
         {
            System.out.print( "   " + timeFormatter.format( event.getTime() ) + " " );
            for ( String name: event.getAttributeNames() )
            {
               String value = event.getAttribute( name );
               System.out.print( name + " = '" + value + "' ");
            }
            System.out.println();
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
      System.out.println("Usage: com.voodoovox.calleranalytics.example.EventReadExample [--key API_KEY] [--lookup] [--requestProfile REQUEST_PROFILE] [--pageSize PAGE_SIZE] [--lastEvent LAST_EVENT] DATA_BUCKET EVENT_TYPE START END");
      System.exit(1);
   }
}
