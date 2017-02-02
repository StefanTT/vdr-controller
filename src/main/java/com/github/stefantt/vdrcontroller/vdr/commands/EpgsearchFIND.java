package com.github.stefantt.vdrcontroller.vdr.commands;

import org.hampelratte.svdrp.Command;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;
import com.github.stefantt.vdrcontroller.util.VdrEpgsearchUtils;

/**
 * An epgsearch plugin FIND command.
 * This command is used to find events that match the given search timer.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class EpgsearchFIND extends Command
{
    private static final long serialVersionUID = 3786509150148281891L;
    private final String cmd;

    /**
     * Create an epgsearch plugin FIND command.
     * This command is used to find events that match the given search timer.
     *
     * @param timer The searchtimer to use for finding
     */
    public EpgsearchFIND(Searchtimer timer)
    {
        cmd = "plug epgsearch find " + VdrEpgsearchUtils.toString(timer);
    }

    @Override
    public String getCommand()
    {
        return cmd;
    }

    @Override
    public String toString()
    {
        return "epgsearch-find";
    }

}
