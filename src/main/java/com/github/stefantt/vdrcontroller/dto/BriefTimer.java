package com.github.stefantt.vdrcontroller.dto;

import org.hampelratte.svdrp.responses.highlevel.Timer;

/**
 * A brief representation of the {@link Timer}, for list views.
 *
 * @author "Stefan Taferner <stefan.taferner@gmx.at>"
 */
public class BriefTimer
{
    enum State
    {
        ENABLED,
        DISABLED,
        RECORDING
    }

    private final int id;
    private final int channel;
    private final String title;
    private final String path;
    private final long startTime;
    private final int duration;
    private final State state;

    /**
     * Create a brief timer from a timer.
     *
     * @param timer The timer to use
     */
    public BriefTimer(Timer timer)
    {
        this.id = timer.getID();
        this.channel = timer.getChannelNumber();
        this.title = timer.getTitle();
        this.path = timer.getPath();
        this.startTime = timer.getStartTime().getTimeInMillis();
        this.duration = (int)((timer.getEndTime().getTimeInMillis() - this.startTime) / 60000);

        if (timer.hasState(Timer.RECORDING))
            this.state = State.RECORDING;
        else if (timer.hasState(Timer.ACTIVE))
            this.state = State.ENABLED;
        else this.state = State.DISABLED;
    }

    public State getState()
    {
        return state;
    }

    public int getId()
    {
        return id;
    }

    public int getChannel()
    {
        return channel;
    }

    public String getTitle()
    {
        return title;
    }

    public String getPath()
    {
        return path;
    }

    public long getStartTime()
    {
        return startTime;
    }

    public int getDuration()
    {
        return duration;
    }
}
