package com.github.stefantt.vdrcontroller.dto;

import java.util.UUID;

import org.hampelratte.svdrp.responses.highlevel.Recording;

import com.github.stefantt.vdrcontroller.entity.Named;
import com.github.stefantt.vdrcontroller.entity.VdrRecording;

/**
 * A brief representation of the {@link Recording}, for list views.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class BriefRecording implements Named
{
   private final UUID id;
   private final String name;
   private final long start;
   private final int duration;
   private final boolean isNew;

   /**
    * Create a brief recording from a recording.
    *
    * @param rec The recording to use
    */
   public BriefRecording(VdrRecording rec)
   {
      this.id = rec.getId();
      this.name = rec.getName();
      this.start = rec.getStartTime().getTimeInMillis();
      this.duration = rec.getDuration();
      this.isNew = rec.isNew();
   }

   public UUID getId()
   {
      return id;
   }

   @Override
   public String getName()
   {
      return name;
   }

   public long getStart()
   {
      return start;
   }

   public int getDuration()
   {
      return duration;
   }

   public boolean isNew()
   {
      return isNew;
   }
}
