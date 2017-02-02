package com.github.stefantt.vdrcontroller.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.stefantt.vdrcontroller.entity.Searchtimer;

/**
 * Unit tests for {@link EpgsearchTimerParser}.
 *
 * @author Stefan Taferner
 */
public class EpgsearchTimerParserTest
{
    @Test
    public void parseLine_1()
    {
        String line =
            "1:Felix und die wilden Tiere:0:::0:0:0:0:1:0:0:0:::0:1:3:1:Felix und die wilden Tiere:::2:10:0:0:0::1:0:1:1:0:0:0:0:20:0:0:0::1:0:0:0:0:0:0:0:0:0:90::0";
        EpgsearchTimerParser target = new EpgsearchTimerParser();

        Searchtimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertEquals(1, timer.getId());
        assertEquals("Felix und die wilden Tiere", timer.getSearch());
        assertEquals(0, timer.getStartTimeMax());
        assertEquals(0, timer.getDurationMax());
        assertFalse(timer.isCaseSensitive());
        assertEquals(Searchtimer.DEFAULT_PRIORITY, timer.getPriority());
        assertEquals(Searchtimer.DEFAULT_LIFETIME, timer.getLifetime());
        assertEquals(8, timer.getWeekdays());
    }

    @Test
    public void parseLine_4_v2()
    {
        String line =
            "4:Kommissar Beck:0:::2:OeffRecht:1:0:1:0:0:0:::1:1:0:1:%Category%~%Genre%:52:98:10:60:0:0:0::1:0:1:1:0:0:0:0:0";
        EpgsearchTimerParser target = new EpgsearchTimerParser();

        Searchtimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertEquals(4, timer.getId());
        assertEquals("Kommissar Beck", timer.getSearch());
        assertEquals(0, timer.getStartTimeMin());
        assertEquals(0, timer.getStartTimeMax());
        assertEquals(0, timer.getDurationMin());
        assertEquals(0, timer.getDurationMax());
        assertTrue(timer.isCaseSensitive());
        assertEquals(52, timer.getPriority());
        assertEquals(98, timer.getLifetime());
        assertEquals(1, timer.getWeekdays());
    }

    @Test
    public void parseLine_19()
    {
        String line =
            "19:Dragons - die Reiter von Berg:1:0805:2109:0:0:0:0:1:0:0:0:::1:0:0:0:Dragons:50:99:2:10:0:0:0::1:0:1:1:1:0:0:0:0:0:0:0::1:0:0:0:0:0:0:0:0:0:90::0";
        EpgsearchTimerParser target = new EpgsearchTimerParser();

        Searchtimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertEquals(19, timer.getId());
        assertEquals("Dragons - die Reiter von Berg", timer.getSearch());
        assertEquals(8 * 60 + 5, timer.getStartTimeMin());
        assertEquals(21 * 60 + 9, timer.getStartTimeMax());
        assertTrue(timer.isAvoidRepeats());
        assertTrue(timer.isRepeatsCompareTitle());
        assertTrue(timer.isRepeatsCompareSubtitle());
        assertTrue(timer.isRepeatsCompareDescription());
        assertEquals(90, timer.getRepeatsMatchDescriptionPercent());
        assertEquals(0, timer.getWeekdays());
    }

    @Test
    public void parseLine_42()
    {
        String line =
            "42:Armans Geheimnis:0:::0:0:0:0:1:1:1:0:::0:1:-6:1:Reiten:50:50:2:15:0:0:0::1:0:1:0:1:0:0:0:0:0:0:0::1:0:0:0:0:0:0:0:0:0:88::0";
        EpgsearchTimerParser target = new EpgsearchTimerParser();

        Searchtimer timer = target.parseLine(line);
        assertNotNull(timer);
        assertFalse(timer.isEnabled());
        assertEquals(88, timer.getRepeatsMatchDescriptionPercent());
        assertEquals(6, timer.getWeekdays());
    }
}
