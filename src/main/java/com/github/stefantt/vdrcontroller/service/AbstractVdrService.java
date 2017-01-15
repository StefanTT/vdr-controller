package com.github.stefantt.vdrcontroller.service;

import java.io.Closeable;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Command;
import org.hampelratte.svdrp.Connection;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.responses.R221;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;

/**
 * Abstract base class for the service for accessing VDR data and doing VDR related tasks.
 * This base class contains the low level stuff.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public abstract class AbstractVdrService implements Closeable
{
   private static final Logger LOGGER = LoggerFactory.getLogger(AbstractVdrService.class);

   private Connection connection;
   private final String host;
   private final int port;

   /**
    * Create a service for accessing VDR data and doing VDR related tasks.
    *
    * @param host The name of the host that is running VDR
    * @param port The SVDRP port of vdr on the host
    */
   public AbstractVdrService(String host, int port)
   {
      this.host = host;
      this.port = port;
   }

   /**
    * Create a service for accessing VDR data and doing VDR related tasks.
    * This version is mainly meant for unit testing as it cannot handle automatic
    * reconnects.
    *
    * @param connection The VDR connection to use
    */
   public AbstractVdrService(Connection connection)
   {
      this.connection = connection;
      this.host = "-";
      this.port = 0;
   }

   /**
    * Get a connection to VDR.
    *
    * @return The connection to VDR.
    *
    * @throws VdrRuntimeException if connecting to VDR failed
    */
   protected Connection getConnection() throws UnknownHostException, IOException
   {
      if (connection == null)
      {
         try
         {
            LOGGER.debug("Opening VDR connection to {} port {}", host, port);
            connection = new Connection(host, port);
         }
         catch (Exception e)
         {
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, "Failed to connect to VDR", e);
         }
      }

      return connection;
   }

   /**
    * Close the connection to VDR. Silently handle errors.
    */
   @Override
   public void close()
   {
      if (connection != null)
      {
         try
         {
            LOGGER.debug("Closing VDR connection");
            connection.close();
         }
         catch (Exception e)
         {
         }
         connection = null;
      }
   }

   /**
    * Send a command to VDR, return the response.
    *
    * @param cmd The SVDRP command to send
    * @return The SVDRP response
    */
   public Response query(Command cmd)
   {
      for (int tries = 3; tries > 0; tries--)
      {
         try
         {
            connection = getConnection();

            LOGGER.debug("Send VDR command: {}", cmd.getCommand());
            Response res = connection.send(cmd);
            LOGGER.debug("Received from VDR: {}", res.getMessage());

            if (res instanceof R221)
            {
               LOGGER.debug("VDR closed connection, reconnecting");
               close();
               continue;
            }

            return res;
         }
         catch (SocketException e)
         {
            LOGGER.debug("VDR connection lost, reconnecting");
            close();
            continue;
         }
         catch (IOException e)
         {
            LOGGER.error("VDR request failed: {}", e.getMessage());
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, "VDR request failed", e);
         }
      }
      throw new VdrRuntimeException(HttpStatus.GATEWAY_TIMEOUT_504, "VDR request failed");
   }
}
