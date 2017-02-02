package com.github.stefantt.vdrcontroller.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.text.SimpleDateFormat;
import java.util.Collection;

import org.junit.Test;

import com.github.stefantt.vdrcontroller.entity.EpgsearchEvent;

/**
 * Unit tests for {@link EpgsearchEventParser}.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class EpgsearchEventParserTest
{
    private SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd:HHmm");

    @Test
    public void parse()
    {
        EpgsearchEventParser target = new EpgsearchEventParser();
        Collection<EpgsearchEvent> events = target.parse(
                "NEWT 1:21:2017-02-11:1158:1240:45:60:Wissen macht Ah!:\n" +
                "NEWT 1:23:2017-02-24:1608:1710:80:99:Horseland, die Pferderanch:");
        assertNotNull(events);
        assertEquals(2, events.size());
    }

    @Test
    public void parseLine()
    {
        EpgsearchEventParser target = new EpgsearchEventParser();
        EpgsearchEvent event = target.parseLine(
                "NEWT 1:21:2017-02-11:1158:1240:45:60:Wissen macht Ah!:");
        assertNotNull(event);
        assertEquals(21, event.getChannel());
        assertEquals("2017-02-11:1158", dateFmt.format(event.getStartTime()));
        assertEquals(42, event.getDuration());
        assertEquals("Wissen macht Ah!", event.getTitle());
    }

    @Test
    public void parseLineNull()
    {
        EpgsearchEventParser target = new EpgsearchEventParser();
        assertNull(target.parseLine(null));
    }
}
