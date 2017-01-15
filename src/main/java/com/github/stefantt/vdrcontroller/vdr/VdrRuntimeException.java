package com.github.stefantt.vdrcontroller.vdr;

/**
 * A runtime exception for VDR related problems.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class VdrRuntimeException extends RuntimeException
{
   private static final long serialVersionUID = -8613213004189748343L;

   private final int httpStatus;

   /**
    * Create a runtime exception for VDR related problems.
    *
    * @param httpStatus The HTTP status for reporting problems to the web app
    * @param msg A text message describing the problem
    */
   public VdrRuntimeException(int httpStatus, String msg)
   {
      super(msg);
      this.httpStatus = httpStatus;
   }

   /**
    * Create a runtime exception for VDR related problems.
    *
    * @param httpStatus The HTTP status for reporting problems to the web app
    * @param t The throwable that caused the problem
    */
   public VdrRuntimeException(int httpStatus, Throwable t)
   {
      super(t);
      this.httpStatus = httpStatus;
   }

   /**
    * Create a runtime exception for VDR related problems.
    *
    * @param httpStatus The HTTP status for reporting problems to the web app
    * @param msg A text message describing the problem
    * @param t The throwable that caused the problem
    */
   public VdrRuntimeException(int httpStatus, String msg, Throwable t)
   {
      super(msg, t);
      this.httpStatus = httpStatus;
   }

   public int getHttpStatus()
   {
      return httpStatus;
   }
}
