package com.github.stefantt.vdrcontroller.dto;

import org.hampelratte.svdrp.responses.highlevel.Channel;
import org.hampelratte.svdrp.responses.highlevel.DVBChannel;

/**
 * A brief representation of the {@link Channel}, for list views.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class BriefChannel
{
    private final String id;
    private final int number;
    private final String name;

    /**
     * Create a brief channel from a channel.
     *
     * @param channel The channel to use
     */
    public BriefChannel(Channel channel)
    {
        number = channel.getChannelNumber();
        name = channel.getName();

        if (channel instanceof DVBChannel)
            id = ((DVBChannel) channel).getID();
        else id = Integer.toString(number);
    }

    public String getId()
    {
        return id;
    }

    public int getNumber()
    {
        return number;
    }

    public String getName()
    {
        return name;
    }
}
