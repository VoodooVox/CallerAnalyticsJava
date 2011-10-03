/* @(#)Event.java
 * 
 * Created: Sep 19, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.api.classes;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.voodoovox.calleranalytics.api.CaApiBase;
import com.voodoovox.calleranalytics.api.CaApiException;

/**
 * Class to handle the Event command.
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class Event extends CaApiBase
{
   private static final String ISO_DATE_TIME_MS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
   
   private static final String COMMAND = "Event";
   
   /**
    * Perform an Event.Read.
    * 
    * @param dataBucket       The data bucket of the events
    * @param eventType        The type of events
    * @param start            The start time of the events (inclusive)
    * @param end              The end time of the events (inclusive)
    * @param pageSize         The maximum number of events to return in a 
    *                         single response
    * @param lastEvent        The lastEvent from the last Event.Read in order
    *                         to query the next page of events.
    * @param lookup           Whether to perform Lookup.Read on all phone 
    *                         numbers from all returned events.
    * @param requestProfile   The request profile to use on lookups
    * @return The event response
    * @throws JSONException Error parsing JSON
    * @throws ParseException Error parsing the time of returned events
    * @throws IOException Error communicating with Caller Analytics API
    * @throws CaApiException Error processing API command
    */
   public static EventResponse read( String dataBucket, String eventType, String start, String end, Integer pageSize, String lastEvent, boolean lookup, String requestProfile) throws JSONException, ParseException, IOException, CaApiException
   {
      JSONObject jsonIn = new JSONObject();
      jsonIn.put( "dataBucket", dataBucket );
      jsonIn.put( "eventType", eventType );
      
      JSONObject period = new JSONObject();
      period.put( "start", start );
      period.put( "end", end );
      jsonIn.put( "period", period );
      
      if ( pageSize != null ) jsonIn.put( "pageSize", pageSize );
      if ( lastEvent != null ) jsonIn.put( "lastEvent", lastEvent );
      
      jsonIn.put( "lookup", lookup );
      if ( requestProfile != null )
      {
         jsonIn.put( "requestProfile", requestProfile );
      }
      
      return new EventResponse( dataBucket, eventType, send( COMMAND, CaApiMethod.Read, jsonIn ) );
   }
   
   private String dataBucket;
   private String eventType;
   private Date time;
   private Map<String, String> attributes;
   
   /**
    * Create a new Event object.
    * 
    * @param dataBucket       The data bucket of the event
    * @param eventType        The type of the event
    * @param time             The time of the event (null to use time of create)
    * @param attributes       The attributes of the event, which are dependent
    *                         upon the type of the event.
    */
   public Event( String dataBucket, String eventType, Date time, Map<String, String> attributes )
   {
      this.dataBucket = dataBucket;
      this.eventType = eventType;
      this.time = time;
      this.attributes = attributes;
   }
   
   /**
    * Create a new Event object from an Event.Read response.
    * 
    * @param dataBucket       The data bucket of the event
    * @param eventType        The type of the event
    * @param eventJson        The JSON of the event attributes
    * @throws JSONException Error parsing JSON
    * @throws ParseException Error parsing time of event
    */
   public Event( String dataBucket, String eventType, JSONObject eventJson ) throws JSONException, ParseException
   {
      this.dataBucket = dataBucket;
      this.eventType = eventType;
      
      attributes = new HashMap<String, String>();
      
      SimpleDateFormat timeFormatter = new SimpleDateFormat( ISO_DATE_TIME_MS_FORMAT );
      
      Iterator<?> eventIt = eventJson.keys();
      while ( eventIt.hasNext() )
      {
         String key = (String) eventIt.next();
         String value = eventJson.getString( key );
         
         if ( "time".equals( key ) )
         {
            time = timeFormatter.parse( value );
         }
         else
         {
            attributes.put( key, value );
         }
      }
   }

   /**
    * @return the time
    */
   public Date getTime()
   {
      return time;
   }
   
   /**
    * Retrieve an event type specific attribute by name
    * 
    * @param name    The name of the attribute
    * @return the value of the attribute, or null if not set
    */
   public String getAttribute( String name )
   {
      if ( this.attributes == null ) return null;
      
      return this.attributes.get( name );
   }
   
   /**
    * Return the set of event type specific attribute names.
    * 
    * @return the set of attribute names assigned to this event.
    */
   public Set<String> getAttributeNames()
   {
      if ( this.attributes == null ) return new HashSet<String>(0);
      
      return this.attributes.keySet();
   }
   
   /**
    * Add a new (or replace an old) attribute.
    * 
    * @param name    The name of the attribute.
    * @param value   The value of the attribute.
    */
   public void addAttribute( String name, String value )
   {
      if ( this.attributes == null ) 
      {
         this.attributes = new HashMap<String, String>();
      }
      
      this.attributes.put( name, value );
   }
   
   /**
    * @return the dataBucket
    */
   public String getDataBucket()
   {
      return dataBucket;
   }

   /**
    * @return the eventType
    */
   public String getEventType()
   {
      return eventType;
   }
   
   /**
    * Create event.
    * 
    * @throws JSONException
    * @throws IOException
    * @throws CaApiException
    */
   public void create() throws JSONException, IOException, CaApiException
   {
      SimpleDateFormat timeFormatter = new SimpleDateFormat( ISO_DATE_TIME_MS_FORMAT );
      
      JSONObject jsonIn = new JSONObject();
      jsonIn.put( "dataBucket", dataBucket );
      jsonIn.put( "eventType", eventType );
      
      if ( time != null )
      {
         jsonIn.put( "time", timeFormatter.format( time ) );
      }
      
      if ( attributes != null )
      {
         for ( Entry<String, String> entry: attributes.entrySet() )
         {
            jsonIn.put( entry.getKey(), entry.getValue() );
         }
      }
      
      send( COMMAND, CaApiMethod.Create, jsonIn );
   }
}
