package com.github.stefantt.vdrcontroller.entity;

/**
 * An event that is the result of a epgsearch timer search.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class EpgsearchEvent
{
    private int channel;
    private long startTime;
    private int duration;
    private String title;

    public int getChannel()
    {
        return channel;
    }

    public void setChannel(int channel)
    {
        this.channel = channel;
    }

    /**
     * @return The start time in milliseconds since 1.1.1970
     */
    public long getStartTime()
    {
        return startTime;
    }

    public void setStartTime(long startTime)
    {
        this.startTime = startTime;
    }

    /**
     * @return The duration in minutes
     */
    public int getDuration()
    {
        return duration;
    }

    public void setDuration(int minutes)
    {
        this.duration = minutes;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
}
