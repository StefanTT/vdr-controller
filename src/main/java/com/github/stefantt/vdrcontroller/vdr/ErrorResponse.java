package com.github.stefantt.vdrcontroller.vdr;

import org.hampelratte.svdrp.Response;

/**
 * A VDR response object for returning an error condition.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class ErrorResponse extends Response
{
   private static final long serialVersionUID = 7247730436142281749L;
   private final int httpStatus;

   /**
    * Create a VDR response object for returning an error condition.
    *
    * @param httpStatus The HTTP status to use
    * @param message The error message
    */
   public ErrorResponse(int httpStatus, String message)
   {
      super(VdrStatus.INTERNAL_ERROR, message);
      this.httpStatus = httpStatus;
   }

   /**
    * @return The HTTP status to use
    */
   public int getHttpStatus()
   {
      return httpStatus;
   }

   @Override
   public String toString()
   {
      return message;
   }
}
