package com.github.stefantt.vdrcontroller.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;
import com.github.stefantt.vdrcontroller.parser.EpgsearchTimerParser;
import com.github.stefantt.vdrcontroller.vdr.VdrConnection;
import com.github.stefantt.vdrcontroller.vdr.VdrRuntimeException;
import com.github.stefantt.vdrcontroller.vdr.commands.EpgsearchEDIS;
import com.github.stefantt.vdrcontroller.vdr.commands.EpgsearchLSTS;

/**
 * A repository that holds the search timers.
 *
 * @author Stefan Taferner
 */
public class SearchtimerRepository extends AbstractCachingRepository
{
    private final VdrConnection vdr;
    private Map<Integer, Searchtimer> timers = new HashMap<>();

    /**
     * Create a repository that holds the auto timers.
     *
     * @param vdr The VDR connection to use
     */
    public SearchtimerRepository(VdrConnection vdr)
    {
        this.vdr = vdr;
    }

    /**
     * Get a specific search timer.
     *
     * @param id The ID of the search timer
     * @return The search timer, null if not found
     */
    public Searchtimer get(int id)
    {
        ensureUpdated();
        return timers.get(id);
    }

    /**
     * @return The search timers.
     */
    public Collection<Searchtimer> getAll()
    {
        ensureUpdated();
        return timers.values();
    }

    /**
     * Store a modified search timer.
     *
     * @param timer The search timer to store
     */
    public void modify(Searchtimer timer)
    {
        Response res = vdr.query(new EpgsearchEDIS(timer));
        if (res.getCode() != 900)
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, res.getMessage());
    }

    @Override
    protected synchronized void update()
    {
        Response res = vdr.query(new EpgsearchLSTS());
        if (res.getCode() != 900)
            throw new VdrRuntimeException(HttpStatus.BAD_GATEWAY_502, res.getMessage());

        Map<Integer, Searchtimer> newTimers = new HashMap<>(256);

        EpgsearchTimerParser parser = new EpgsearchTimerParser();
        for (Searchtimer timer : parser.parse(res.getMessage()))
        {
            newTimers.put(timer.getId(), timer);
        }

        this.timers = newTimers;
    }
}
