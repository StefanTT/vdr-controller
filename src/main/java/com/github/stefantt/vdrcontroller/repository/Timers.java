package com.github.stefantt.vdrcontroller.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.LSTT;
import org.hampelratte.svdrp.commands.UPDT;
import org.hampelratte.svdrp.parsers.TimerParser;
import org.hampelratte.svdrp.responses.R250;
import org.hampelratte.svdrp.responses.highlevel.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;

/**
 * Repository for VDR timers.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class Timers
{
    private static final Logger LOGGER = LoggerFactory.getLogger(Timers.class);

    // The maximum age of the list of timers before they are re-read from VDR
    private static final int DATA_MAX_AGE_MSEC = 60000;

    private final VdrConnection vdr;

    private Map<Integer, Timer> timers;
    private long lastUpdated = 0;

    /**
     * Create a repository that holds the VDR timers.
     *
     * @param vdr The VDR connection to use
     */
    public Timers(VdrConnection vdr)
    {
        this.vdr = vdr;
    }

    /**
     * @return The timers.
     */
    public Collection<Timer> getAll()
    {
        ensureUpdated();
        return timers.values();
    }

    /**
     * Enable a timer.
     *
     * @param id The ID of the timer
     */
    public void enable(int id)
    {
        LOGGER.info("Enabling timer #{}", id);
        ensureUpdated();
        Timer timer = timers.get(id);
        setState(timer, timer.getState() | Timer.ACTIVE);
    }

    /**
     * Disable a timer.
     *
     * @param id The ID of the timer
     */
    public void disable(int id)
    {
        LOGGER.info("Disabling timer #{}", id);
        ensureUpdated();
        Timer timer = timers.get(id);
        setState(timer, timer.getState() & ~Timer.ACTIVE);
    }

    /**
     * Change the state of a timer.
     *
     * @param timer The timer
     * @param state The new state
     */
    private void setState(Timer timer, int state)
    {
        int oldState = timer.getState();
        timer.setState(state);

        Response res = vdr.query(new UPDT(timer));
        if (res instanceof R250)
            return;

        timer.setState(oldState);
        throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, res.getMessage());
    }

    /**
     * Clear cached data.
     */
    public void clearCache()
    {
        lastUpdated = 0;
    }

    /**
     * Ensure that the EPG data is up to date.
     */
    private void ensureUpdated()
    {
        if (System.currentTimeMillis() - DATA_MAX_AGE_MSEC > lastUpdated)
            update();
    }

    /**
     * Update the EPG data.
     */
    private synchronized void update()
    {
        Map<Integer, Timer> newTimers = new HashMap<>(256);
        Response res = vdr.query(new LSTT());

        if (!(res instanceof R250))
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, res.getMessage());

        for (Timer timer : TimerParser.parse(res.getMessage()))
            newTimers.put(timer.getID(), timer);

        timers = newTimers;
    }
}
