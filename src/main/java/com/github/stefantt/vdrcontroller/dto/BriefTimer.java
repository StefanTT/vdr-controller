package com.github.stefantt.vdrcontroller.dto;

import org.hampelratte.svdrp.responses.highlevel.Timer;

/**
 * A brief representation of the {@link Timer}, for list views.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class BriefTimer
{
   private int id;
   private int channel;
   private String title;
   private String path;

   /**
    * Create a brief timer from a timer.
    *
    * @param timer The timer to use
    */
   public BriefTimer(Timer timer)
   {
      this.id = timer.getID();
      this.channel = timer.getChannelNumber();
      this.title = timer.getTitle();
      this.path = timer.getPath();
   }

   public int getId()
   {
      return id;
   }

   public int getChannel()
   {
      return channel;
   }

   public String getTitle()
   {
      return title;
   }

   public String getPath()
   {
      return path;
   }
}
