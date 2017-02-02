package com.github.stefantt.vdrcontroller.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import com.github.stefantt.vdrcontroller.entity.EpgsearchEvent;

/**
 * A parser for results of an epgsearch search.
 *
 * @author Stefan Taferner
 */
public class EpgsearchEventParser
{
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-ddHHmm");

    /**
     * Parse multiple lines.
     *
     * @param lines The lines to parse
     * @return The list of events
     */
    public List<EpgsearchEvent> parse(String lines)
    {
        List<EpgsearchEvent> events = new ArrayList<>(128);

        StringTokenizer tokenizer = new StringTokenizer(lines, "\n");
        while (tokenizer.hasMoreTokens())
        {
            String line = tokenizer.nextToken();
            if (line.startsWith("End")) break;

            EpgsearchEvent event = parseLine(line);
            if (event != null)
                events.add(event);
        }

        return events;
    }

    /**
     * Parse a single event line.
     *
     * @param line The line to parse
     * @return The parsed event, null if the line was null
     */
    public EpgsearchEvent parseLine(String line)
    {
        if (line == null)
            return null;

        // Example line:
        // NEWT 1:21:2017-02-11:1158:1240:50:50:Wissen macht Ah!:

        String[] fields = line.split(":");
        if (fields.length < 8) return null;

        EpgsearchEvent event = new EpgsearchEvent();

        event.setChannel(Integer.parseInt(fields[1]));
        event.setTitle(fields[7]);

        long startTime = createTime(fields[2], fields[3]);

        long endTime = createTime(fields[2], fields[4]);
        if (endTime <= startTime) endTime += 24 * 3600000;

        event.setStartTime(startTime);
        event.setDuration((int)((endTime - startTime) / 60000));

        return event;
    }

    private long createTime(String dateStr, String timeStr)
    {
        try
        {
            Date date = dateFmt.parse(dateStr + timeStr);
            if (date == null)
                throw new IllegalArgumentException("failed to parse date");
            return date.getTime();
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException("failed to parse date", e);
        }
    }
}
