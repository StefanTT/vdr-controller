package com.github.stefantt.vdrcontroller.dto;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;

/**
 * A brief representation of the {@link Searchtimer}, for list views.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class BriefSearchtimer
{
    private int id;
    private boolean enabled;
    private String search;
    private String folder;

    /**
     * Create an empty brief search timer.
     */
    public BriefSearchtimer()
    {
    }

    /**
     * Create a brief representation of a search timer.
     *
     * @param timer The search timer to use
     */
    public BriefSearchtimer(Searchtimer timer)
    {
        this.id = timer.getId();
        this.enabled = timer.isEnabled();
        this.search = timer.getSearch();
        this.folder = timer.getFolder();
    }

    public int getId()
    {
        return id;
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public String getSearch()
    {
        return search;
    }

    public String getFolder()
    {
        return folder;
    }
}
