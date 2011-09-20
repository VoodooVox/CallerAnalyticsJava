/* @(#)EventResponse.java
 * 
 * Created: Sep 19, 2011
 * 
 * Copyright(c) 2011 VoodooVox, Inc. All Rights Reserved.
 */
package com.voodoovox.calleranalytics.api.classes;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Processes and packages Event.Read responses.
 * 
 * @author Joseph Monti <jmonti@voodoovox.com>
 * @version 1.0
 */
public class EventResponse
{
   private String lastEvent;
   private List<Event> events;
   
   /**
    * Create new EventResponse object from Event.Read response.
    * 
    * @param dataBucket       The data bucket used to query events
    * @param eventType        The event type used to query events
    * @param response         The JSON "response" object from the Event.Read
    * @throws JSONException Error parsing JSON
    * @throws ParseException Error parsing time of event
    */
   public EventResponse( String dataBucket, String eventType, JSONObject response ) throws JSONException, ParseException 
   {
      if ( response.has( "lastEvent" ) )
      {
         lastEvent = response.getString( "lastEvent" );
      }
      else
      {
         lastEvent = null;
      }
      
      events = new ArrayList<Event>();
      
      JSONArray eventsJson = response.getJSONArray( "events" );
      
      for ( int i = 0; i < eventsJson.length(); i++ )
      {
         JSONObject eventJson = (JSONObject) eventsJson.getJSONObject( i );
         
         events.add( new Event( dataBucket, eventType, eventJson ) );
      }
   }

   /**
    * Use this lastEvent value to pass to subsequent Event.Read calls in order
    * to request next page of events.
    * 
    * @return the lastEvent
    */
   public String getLastEvent()
   {
      return lastEvent;
   }

   /**
    * @return the list of Event objects in this
    */
   public List<Event> getEvents()
   {
      return events;
   }
}
