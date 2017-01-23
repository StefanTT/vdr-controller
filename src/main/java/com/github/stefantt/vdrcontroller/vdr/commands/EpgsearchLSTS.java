package com.github.stefantt.vdrcontroller.vdr.commands;

import org.hampelratte.svdrp.Command;

/**
 * An epgsearch plugin LSTS command.
 *
 * @author Stefan Taferner
 */
public class EpgsearchLSTS extends Command
{
    private static final long serialVersionUID = -1205499025262420758L;
    private final String cmd;

    /**
     * Create an epgsearch plugin LSTS command.
     */
    public EpgsearchLSTS()
    {
        cmd = "plug epgsearch lsts";
    }

    @Override
    public String getCommand()
    {
        return cmd;
    }

    @Override
    public String toString()
    {
        return "epgsearch-lsts";
    }

}
