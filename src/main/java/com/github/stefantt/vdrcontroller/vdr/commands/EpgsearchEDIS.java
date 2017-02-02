package com.github.stefantt.vdrcontroller.vdr.commands;

import org.hampelratte.svdrp.Command;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;
import com.github.stefantt.vdrcontroller.util.VdrEpgsearchUtils;

/**
 * An epgsearch plugin EDIS command.
 * This command is used to update (edit) an existing search timer.
 *
 * @author Stefan Taferner
 */
public class EpgsearchEDIS extends Command
{
    private static final long serialVersionUID = 7768752368145514956L;
    private final String cmd;

    /**
     * Create an epgsearch plugin EDIS command.
     * This command is used to update (edit) an existing search timer.
     *
     * @param timer The searchtimer
     */
    public EpgsearchEDIS(Searchtimer timer)
    {
        cmd = "plug epgsearch edis " + VdrEpgsearchUtils.toString(timer);
    }

    @Override
    public String getCommand()
    {
        return cmd;
    }

    @Override
    public String toString()
    {
        return "plug epgsearch edis";
    }
}
