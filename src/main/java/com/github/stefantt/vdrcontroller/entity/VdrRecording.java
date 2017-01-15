package com.github.stefantt.vdrcontroller.entity;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.hampelratte.svdrp.responses.highlevel.Genre;
import org.hampelratte.svdrp.responses.highlevel.Recording;
import org.hampelratte.svdrp.responses.highlevel.Stream;

import com.github.stefantt.vdrcontroller.util.VdrUtils;
import com.github.stefantt.vdrcontroller.vdr.VdrConstants;

/**
 * A VDR recording.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class VdrRecording implements Serializable, Named, VirtualFolderEntry
{
   private static final long serialVersionUID = 5023098905970862157L;

   private final Recording rec;
   private String name;
   private UUID id;

   public VdrRecording(Recording rec)
   {
      this.rec = rec;

      id = VdrUtils.createRecordingId(rec);
      initName();
   }

   /**
    * Set the name from the recording's title.
    */
   public void initName()
   {
      name = rec.getTitle();
      int idx = name.lastIndexOf(VdrConstants.PATH_SEPARATOR);
      if (idx >= 0)
      {
         name = name.substring(idx + 1);
      }
   }

   @Override
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
       this.name = name;
   }

   /**
    * Get the unique ID of the recording.
    *
    * @return The unique ID of the recording
    */
   public UUID getId()
   {
      return id;
   }

   /**
    * @return True if the recording contains details, false if it is only the short
    *         list representation.
    */
   public boolean isDetailed()
   {
      return rec.getEventID() != 0;
   }

   public String getChannelID()
   {
      return rec.getChannelID();
   }

   public String getChannelName()
   {
      return rec.getChannelName();
   }

   public int getNumber()
   {
      return rec.getNumber();
   }

   public String getDescription()
   {
      return rec.getDescription();
   }

   public Calendar getEndTime()
   {
      return rec.getEndTime();
   }

   public String getDisplayTitle()
   {
      return rec.getDisplayTitle();
   }

   public String getShortText()
   {
      return rec.getShortText();
   }

   public String getFolder()
   {
      return rec.getFolder();
   }

   public Calendar getStartTime()
   {
      return rec.getStartTime();
   }

   public int getTableID()
   {
      return rec.getTableID();
   }

   public boolean isNew()
   {
      return rec.isNew();
   }

   public String getTitle()
   {
      return rec.getTitle();
   }

   public boolean isCut()
   {
      return rec.isCut();
   }

   public Calendar getVpsTime()
   {
      return rec.getVpsTime();
   }

   public int getPriority()
   {
      return rec.getPriority();
   }

   public int getLifetime()
   {
      return rec.getLifetime();
   }

   /**
    * @return The duration in minutes
    */
   public int getDuration()
   {
      return rec.getDuration();
   }

   public long getEventID()
   {
      return rec.getEventID();
   }

   public int getVersion()
   {
      return rec.getVersion();
   }

   public List<Stream> getStreams()
   {
      return rec.getStreams();
   }

   public List<Genre> getGenres()
   {
      return rec.getGenres();
   }

   public List<Stream> getAudioStreams()
   {
      return rec.getAudioStreams();
   }

   public void setChannelID(String channelID)
   {
      rec.setChannelID(channelID);
   }

   public void setChannelName(String channelName)
   {
      rec.setChannelName(channelName);
   }

   public void setNumber(int number)
   {
      rec.setNumber(number);
   }

   public void setDescription(String description)
   {
      rec.setDescription(description);
   }

   public void setShortText(String shortText)
   {
      rec.setShortText(shortText);
   }

   public void setDisplayTitle(String display)
   {
      rec.setDisplayTitle(display);
   }

   public void setTitle(String title)
   {
      rec.setTitle(title);
   }

   public void setTableID(int tableId)
   {
      rec.setTableID(tableId);
   }

   public void setNew(boolean isNew)
   {
      rec.setNew(isNew);
   }

   public void setPriority(int priority)
   {
      rec.setPriority(priority);
   }

   public void setVpsTime(long vpsTime)
   {
      rec.setVpsTime(vpsTime);
   }

   public void setLifetime(int lifetime)
   {
      rec.setLifetime(lifetime);
   }

   public void setVpsTime(Calendar vpsTime)
   {
      rec.setVpsTime(vpsTime);
   }

   public void setDuration(int duration)
   {
      rec.setDuration(duration);
   }

   public void setEventID(long eventID)
   {
      rec.setEventID(eventID);
   }

   public void setVersion(int version)
   {
      rec.setVersion(version);
   }

   public void setStreams(List<Stream> streams)
   {
      rec.setStreams(streams);
   }

   public void setGenres(List<Genre> genres)
   {
      rec.setGenres(genres);
   }

   public Recording getRec()
   {
      return rec;
   }

   @Override
   public boolean equals(Object obj)
   {
      return rec.equals(obj);
   }

   @Override
   public int hashCode()
   {
      return rec.hashCode();
   }
}
