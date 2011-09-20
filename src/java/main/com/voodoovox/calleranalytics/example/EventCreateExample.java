/* @(#)EventCreateExample.java
 * 
 * Created: Sep 20, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.voodoovox.calleranalytics.api.CaApiException;
import com.voodoovox.calleranalytics.api.classes.Event;

/**
 * 
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class EventCreateExample
{
   public static void main(String[] args)
   {
      String dataBucket = null;
      String eventType = null;
      Date time = null;
      Map<String, String> attributes = new HashMap<String, String>();
      
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
               dataBucket = args[i];
               break;
            case 1:
               eventType = args[i];
               break;
            case 2:
               try
               {
                  time = parseIsoDate( args[i] );
               }
               catch ( ParseException ex )
               {
                  System.err.println("Error parsing time: " + args[i] );
                  ex.printStackTrace();
                  System.exit(1);
               }
               break;
            default:
               String[] parts = args[i].split( "=" );
               if ( parts.length != 2 )
               {
                  System.err.println("Invalid attribute '" + args[i] + "', expecting syntax name=value" );
                  System.exit(1);
               }
               
               attributes.put( parts[0], parts[1] );
            }
            finalArgIdx++;
         }
      }
      
      if ( finalArgIdx < 2 )
      {
         printUsage();
      }
      
      try
      {
         Event event = new Event( dataBucket, eventType, time, attributes );
         event.create();
      }
      catch ( CaApiException ex )
      {
         System.err.println("API Error: ");
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
      System.out.println("Usage: com.voodoovox.calleranalytics.example.EventCreateExample [--key API_KEY] DATA_BUCKET EVENT_TYPE TIME [name=value]...");
      System.exit(1);
   }
   
   
   /*
    * Some stuff for handling dates 
    */
   
   private static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
   private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
   private static final String ISO_DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";

   /**
    * Parses date of formats: 
    * <ul>
    *    <li>yyyy-MM-dd</li>
    *    <li>yyyy-MM-dd HH:mm:ss</li> 
    *    <li>yyyy-MM-dd HH:mm:ss.SSS</li>
    * </ul>
    * @param str
    * @return
    * @throws ParseException
    */
   public static Date parseIsoDate(String str) throws ParseException {
      String fmt = ISO_DATE_FORMAT;
      if ( str.length() == ISO_DATE_TIME_FORMAT.length() ) {
         fmt = ISO_DATE_TIME_FORMAT;
      } else if ( str.length() == ISO_DATE_TIME_MS_FORMAT.length() ) {
         fmt = ISO_DATE_TIME_MS_FORMAT;
      }
      return new SimpleDateFormat( fmt ).parse( str );
   }
}
