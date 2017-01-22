package com.github.stefantt.vdrcontroller.parser;

import static org.junit.Assert.*;

import org.apache.commons.lang3.Range;
import org.junit.Test;

import com.github.stefantt.vdrcontroller.entity.AutoTimer;

/**
 * Unit tests for {@link EpgsearchAutoTimerParser}.
 *
 * @author Stefan Taferner
 */
public class EpgsearchAutoTimerParserTest
{
    @Test
    public void parseLine_1()
    {
        String line =
            "1:Felix und die wilden Tiere:0:::0:0:0:0:1:0:0:0:::0:0:0:1:Felix und die wilden Tiere:::2:10:0:0:0::1:0:1:1:0:0:0:0:20:0:0:0::1:0:0:0:0:0:0:0:0:0:90::0";
        EpgsearchAutoTimerParser target = new EpgsearchAutoTimerParser();

        AutoTimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertEquals(1, timer.getId());
        assertEquals("Felix und die wilden Tiere", timer.getSearch());
        assertNull(timer.getStartTimeRange());
        assertNull(timer.getDurationRange());
        assertFalse(timer.isCaseSensitive());
        assertEquals(AutoTimer.DEFAULT_PRIORITY, timer.getPriority());
        assertEquals(AutoTimer.DEFAULT_LIFETIME, timer.getLifetime());
    }

    @Test
    public void parseLine_4_v2()
    {
        String line =
            "4:Kommissar Beck:0:::2:OeffRecht:1:0:1:0:0:0:::1:0:0:1:%Category%~%Genre%:52:98:10:60:0:0:0::1:0:1:1:0:0:0:0:0";
        EpgsearchAutoTimerParser target = new EpgsearchAutoTimerParser();

        AutoTimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertEquals(4, timer.getId());
        assertEquals("Kommissar Beck", timer.getSearch());
        assertNull(timer.getStartTimeRange());
        assertNull(timer.getDurationRange());
        assertTrue(timer.isCaseSensitive());
        assertEquals(52, timer.getPriority());
        assertEquals(98, timer.getLifetime());
    }

    @Test
    public void parseLine_19()
    {
        String line =
            "19:Dragons - die Reiter von Berg:1:0805:2109:0:0:0:0:1:0:0:0:::1:0:0:0:Dragons:50:99:2:10:0:0:0::1:0:1:1:1:0:0:0:0:0:0:0::1:0:0:0:0:0:0:0:0:0:90::0";
        EpgsearchAutoTimerParser target = new EpgsearchAutoTimerParser();

        AutoTimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertEquals(19, timer.getId());
        assertEquals("Dragons - die Reiter von Berg", timer.getSearch());
        assertEquals(Range.between(8 * 60 + 5, 21 * 60 + 9), timer.getStartTimeRange());

        assertNull(timer.getDurationRange());

        assertTrue(timer.isAvoidRepeats());
        assertTrue(timer.isRepeatsCompareTitle());
        assertTrue(timer.isRepeatsCompareSubtitle());
        assertTrue(timer.isRepeatsCompareDescription());
        assertEquals(90, timer.getRepeatsMatchDescriptionPercent());
    }
}
