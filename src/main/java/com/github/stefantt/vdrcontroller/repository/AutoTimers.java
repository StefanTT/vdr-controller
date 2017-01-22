package com.github.stefantt.vdrcontroller.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.vdr.VdrConnection;

/**
 * A repository that holds the auto timers.
 *
 * @author Stefan Taferner
 */
public class AutoTimers
{
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoTimers.class);
    private final VdrConnection vdr;
    private long lastUpdated = 0;

    /**
     * Create a repository that holds the auto timers.
     *
     * @param vdr The VDR connection to use
     */
    public AutoTimers(VdrConnection vdr)
    {
        this.vdr = vdr;
    }

    /**
     * Clear cached data.
     */
    public void clearCache()
    {
        lastUpdated = 0;
    }

}
