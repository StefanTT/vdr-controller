package com.github.stefantt.vdrcontroller.dto;

import com.github.stefantt.vdrcontroller.entity.Named;
import com.github.stefantt.vdrcontroller.entity.VdrRecording;
import com.github.stefantt.vdrcontroller.entity.VirtualFolder;

/**
 * A folder in the list of recordings.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class RecordingsFolder implements Named
{
   private final String name;
   private final int childs;

   /**
    * Create a recordings entry from a virtual folder.
    *
    * @param folder The folder for creating the object
    */
   public RecordingsFolder(VirtualFolder<VdrRecording> folder)
   {
      this.name = folder.getName();
      this.childs = folder.getNumRecordings();
   }

   /**
    * @return The name of the folder
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @return The number of children the folder contains - both recordings and other folders.
    */
   public int getChilds()
   {
      return childs;
   }
}
