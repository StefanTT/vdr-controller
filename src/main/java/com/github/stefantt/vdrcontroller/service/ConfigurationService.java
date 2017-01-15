package com.github.stefantt.vdrcontroller.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.entity.Configuration;
import com.google.gson.Gson;

/**
 * This service manages the application configuration.
 *
 * @author Stefan Taferner
 */
public class ConfigurationService
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationService.class);
    private static final ConfigurationService INSTANCE = newInstance();

    private Gson gson = new Gson();
    private Configuration config = new Configuration();
    private String lastFileName;

    // Hide constructor
    protected ConfigurationService()
    {
    }

    /**
     * @return The singleton instance of the service.
     */
    public static ConfigurationService getInstance()
    {
        return INSTANCE;
    }

    /**
     * Create a new configuration service. Used in those cases where the global configuration service
     * instance is not suitable, e.g. in tests.
     *
     * @return The new configuration service
     */
    public static ConfigurationService newInstance()
    {
        return new ConfigurationService();
    }

    /**
     * @return The configuration
     */
    public Configuration getConfig()
    {
        return config;
    }

    /**
     * Set the configuration.
     *
     * @param config The new configuration
     */
    public void setConfig(Configuration config)
    {
        this.config = config;
    }

    /**
     * Load the configuration from a file.
     *
     * @param fileName The name of the file to load
     */
    public void load(String fileName)
    {
        try
        {
            load(new FileInputStream(fileName));
        }
        catch (FileNotFoundException e)
        {
            LOGGER.info("Configuration file not found: {}", fileName);
            config.clear();
        }

        lastFileName = fileName;
    }

    /**
     * Load the configuration from an input stream.
     *
     * @param in The input stream to read
     */
    public void load(InputStream in)
    {
        load(in, "<stream>");
    }

    protected void load(InputStream in, String fileName)
    {
        try
        {
            config = gson.fromJson(new InputStreamReader(in, "UTF-8"), Configuration.class);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to load configuration from " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Save the configuration to a file.
     *
     * @param fileName The name of the file to write
     */
    public void save(String fileName)
    {
        try
        {
            save(new FileOutputStream(fileName), fileName);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to write configuration to " + fileName + ": " + e.getMessage());
        }
    }

    /**
     * Save the configuration to the last file it was loaded from.
     */
    public void save()
    {
        Validate.notEmpty(lastFileName, "Internal error: last file name is unset");
        save(lastFileName);
    }

    /**
     * Save the configuration to an output stream.
     *
     * @param out The output stream to write
     */
    public void save(OutputStream out)
    {
        save(out, "<stream>");
    }

    protected void save(OutputStream out, String fileName)
    {
        Writer writer = null;
        try
        {
            writer = new OutputStreamWriter(out);
            writer.write(gson.toJson(config));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed to write configuration to " + fileName + ": " + e.getMessage());
        }
        finally
        {
            IOUtils.closeQuietly(writer);
        }
    }
}
