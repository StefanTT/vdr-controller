package com.github.stefantt.vdrcontroller.vdr.commands;

import org.hampelratte.svdrp.Command;

public class LSTRpath extends Command
{
    private static final long serialVersionUID = -2590497143439604860L;

    private final int number;

    /**
     * Command to get the path of a recording
     *
     * @param number The number of the recording
     */
    public LSTRpath(int number)
    {
        this.number = number;
    }

    @Override
    public String getCommand()
    {
        return "LSTR " + number + " path";
    }

    @Override
    public String toString()
    {
        return "LSTR";
    }

    /**
     * Returns the number of the recording
     *
     * @return The number of the recording
     */
    public int getNumber()
    {
        return number;
    }
}
