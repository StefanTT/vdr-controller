package com.github.stefantt.vdrcontroller.vdr;

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
 * Connection to VDR.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class VdrConnection implements Closeable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(VdrConnection.class);

    private Connection connection;
    private String host;
    private int port;

    /**
     * Create a connection to VDR.
     *
     * @param host The name of the host that is running VDR
     * @param port The SVDRP port of vdr on the host
     */
    public VdrConnection(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Create a service for accessing VDR data and doing VDR related tasks. This version is mainly meant for unit
     * testing as it cannot handle automatic reconnects.
     *
     * @param connection The VDR connection to use
     */
    public VdrConnection(Connection connection)
    {
        this.connection = connection;
        this.host = "-";
        this.port = 0;
    }

    /**
     * Close the connection to VDR. Silently handle errors.
     */
    @Override
    public synchronized void close()
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
     * Execute a task within a SVDRP connection session. The SVDRP connection is
     * opened prior to the task and is closed when the task ends.
     *
     * @param task The task to execute
     * @return The return value from the task's execution
     */
    public synchronized <T> T execute(VdrSessionTask<T> task)
    {
        Connection con;

        try
        {
            con = new Connection(host, port);
        }
        catch (Exception e)
        {
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, "Failed to connect to VDR", e);
        }

        try
        {
            return task.execute(con);
        }
        catch (IOException e)
        {
            LOGGER.warn("VDR communication task failed", e);
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, e);
        }
        finally
        {
            try
            {
                con.close();
            }
            catch (IOException e)
            {
            }
        }
    }

    /**
     * Send a command to VDR, return the response.
     *
     * @param cmd The SVDRP command to send
     * @return The SVDRP response
     */
    public synchronized Response query(Command cmd)
    {
        for (int tries = 3; tries > 0; tries--)
        {
            try
            {
                connection = getConnection();

                LOGGER.debug("Send VDR command: {}", cmd.getCommand());
                Response res = connection.send(cmd);
                LOGGER.debug("Received from VDR {}: {}", res.getCode(), res.getMessage());

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
            finally
            {
                close();
            }
        }
        throw new VdrRuntimeException(HttpStatus.GATEWAY_TIMEOUT_504, "VDR request failed");
    }

    /**
     * Get a connection to VDR.
     *
     * @return The connection to VDR.
     *
     * @throws VdrRuntimeException if connecting to VDR failed
     */
    public Connection getConnection() throws UnknownHostException, IOException
    {
        if (connection != null)
            return connection;

        try
        {
            LOGGER.debug("Opening VDR connection to {} port {}", host, port);
            return new Connection(host, port);
        }
        catch (Exception e)
        {
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, "Failed to connect to VDR", e);
        }
    }

    /**
     * Set the name of the host that is running VDR.
     *
     * @param host The name of the VDR host
     */
    public void setHost(String host)
    {
        this.host = host;
    }

    /**
     * Set the name of the host that is running VDR.
     *
     * @param host The name of the VDR host
     * @param port The SVDRP port of vdr on the host
     */
    public void setHost(String host, int port)
    {
        this.host = host;
        this.port = port;
    }

    /**
     * Set the SVDRP port of VDR.
     *
     * @param port The SVDRP port of vdr on the host
     */
    public void setPort(int port)
    {
        this.port = port;
    }
}
