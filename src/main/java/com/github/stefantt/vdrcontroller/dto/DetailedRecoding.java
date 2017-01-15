package com.github.stefantt.vdrcontroller.dto;

import com.github.stefantt.vdrcontroller.entity.VdrRecording;

/**
 * A DTO for a detailed recording.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class DetailedRecoding extends BriefRecording
{
   private final String shortText;
   private final String description;
   private final String channel;
   private final long eventId;
   private final int lifetime;

   /**
    * Create a DTO for a detailed recording.
    *
    * @param rec The recording to use
    */
   public DetailedRecoding(VdrRecording rec)
   {
      super(rec);

      this.shortText = rec.getShortText();
      this.description = rec.getDescription().replace('\n', '|');
      this.eventId = rec.getEventID();
      this.lifetime = rec.getLifetime();
      this.channel = rec.getChannelName();
   }

   public String getShortText()
   {
      return shortText;
   }

   public String getDescription()
   {
      return description;
   }

   public long getEventId()
   {
      return eventId;
   }

   public int getLifetime()
   {
      return lifetime;
   }

   public String getChannel()
   {
      return channel;
   }
}
