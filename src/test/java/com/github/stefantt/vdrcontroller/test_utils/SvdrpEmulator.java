package com.github.stefantt.vdrcontroller.test_utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import spark.utils.StringUtils;

/**
 * Opens a port and emulates VDR's SVDRP protocol on this port.
 *
 * @author Stefan Taferner
 */
public class SvdrpEmulator implements Runnable
{
    private static final Logger LOGGER = LoggerFactory.getLogger(SvdrpEmulator.class);
    public static String DEFAULT_REPLIES_DIR = "vdr-replies";

    private ServerSocket listener = null;
    private boolean running = false;
    private String repliesDir;
    private final int port;

    /**
     * Create a SVDRP emulator.
     *
     * @param port The TCP port to listen on
     * @param repliesDir The directory with the replies
     */
    public SvdrpEmulator(int port, String repliesDir)
    {
        this.repliesDir = repliesDir + '/';

        try
        {
            listener = new ServerSocket(port);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Cannot listen on port " + port, e);
        }

        this.port = listener.getLocalPort();
        LOGGER.info("Listening on port {}", port);

    }

    /**
     * @return The port on which the emulator is listening.
     */
    public int getPort()
    {
        return port;
    }

    /**
     * Stop the emulator.
     */
    public void close()
    {
        LOGGER.info("Stopping emulator");

        running = false;
        IOUtils.closeQuietly(listener);
    }

    @Override
    public void run()
    {
        running = true;

        try
        {
            while (running)
            {
                Socket socket = listener.accept();
                try
                {
                    LOGGER.info("{} connected", socket.getRemoteSocketAddress());
                    session(socket);
                    LOGGER.info("{} disconnected", socket.getRemoteSocketAddress());
                }
                catch(IOException e)
                {
                    LOGGER.warn("Session terminated", e);
                }
                finally
                {
                    IOUtils.closeQuietly(socket);
                }
            }
        }
        catch (IOException e)
        {
            if (!(e instanceof SocketException) || running)
                throw new RuntimeException(e);
        }
        finally
        {
            IOUtils.closeQuietly(listener);
        }
    }

    public void session(Socket socket) throws IOException
    {
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
        PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);

        out.println("220 SvdrpEmulator SVDRP VideoDiskRecorder 2.2.0; Sun Jan 15 14:38:41 2017; UTF-8");

        while (true)
        {
            String line = in.readLine();
            if (line == null)
                return;

            line = line.toLowerCase();
            if ("quit".equals(line) || line.startsWith("\04"))
            {
                out.println("221 SvdrpEmulator closing connection");
                return;
            }
            if ("x".equals(line))
            {
                out.println("221 SvdrpEmulator shutting down");
                close();
                return;
            }

            if (StringUtils.isEmpty(line))
                continue;

            String replyFile = repliesDir + line.trim().replaceAll("[.\\s]+", "_") + ".txt";
            InputStream replyStream = getClass().getClassLoader().getResourceAsStream(replyFile);
            if (replyStream == null)
            {
                LOGGER.warn("Unsupported request: {} (file {})", line, replyFile);
                out.println("500 file not found: " + replyFile);
            }
            else
            {
                try
                {
                    LOGGER.info("Sending reply {}", replyFile);
                    IOUtils.copy(replyStream, out, "utf-8");
                    out.flush();
                }
                finally
                {
                    IOUtils.closeQuietly(replyStream);
                }
            }
        }
    }
}
