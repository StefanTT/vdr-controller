package com.github.stefantt.vdrcontroller.vdr;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jetty.http.HttpStatus;
import org.hampelratte.svdrp.Response;
import org.hampelratte.svdrp.commands.HITK;
import org.hampelratte.svdrp.commands.PLUG;
import org.hampelratte.svdrp.responses.R250;

import com.github.stefantt.vdrcontroller.entity.VdrOsdItem;
import com.github.stefantt.vdrcontroller.entity.VdrOsdItemType;

/**
 * A proxy for controlling VDR's OSD. Requires the VDR plugin svdrposd to work.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class VdrOsdProxy
{
    private static final String PLUGIN_NAME = "svdrposd";
    private static final int RC_NOT_ON_OSD = 930;
    private static final int RC_OK = 920;

    private final VdrConnection vdr;

    /**
     * Create a proxy for controlling VDR's OSD.
     *
     * @param vdr The VDR connection to use
     */
    public VdrOsdProxy(VdrConnection vdr)
    {
        this.vdr = vdr;
    }

    /**
     * Send a key press to VDR.
     *
     * SVDRP command "HITK" without arguments returns the list of supported key names.
     *
     * @param key The key to send
     */
    public void sendKey(String key)
    {
        Response res = vdr.query(new HITK(key));

        if (!(res instanceof R250))
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, res.getMessage());
    }

    /**
     * Get the items of what the OSD currently contains.
     *
     * @return The contents of the OSD
     */
    public List<VdrOsdItem> getItems()
    {
        List<VdrOsdItem> items = new ArrayList<>();

        Response res = vdr.query(new PLUG(PLUGIN_NAME, "lsto 15"));
        if (res.getCode() == RC_NOT_ON_OSD)
            return items;
        if (res.getCode() != RC_OK)
            throw new VdrRuntimeException(HttpStatus.BAD_REQUEST_400, res.getMessage());

        for (String line: res.getMessage().split("\n"))
        {
            if (line.length() < 3 || line.charAt(1) != ':')
                continue;

            VdrOsdItemType type = VdrOsdItemType.valueOf(line.charAt(0));
            if (type == null) continue;

            int idx = line.lastIndexOf(':');
            if (idx < 2 || idx >= line.length() - 1)
            {
                items.add(new VdrOsdItem(type, line.substring(2).trim(), null));
            }
            else
            {
                items.add(new VdrOsdItem(type, line.substring(2, idx).trim(),
                        line.substring(idx + 1).trim()));
            }
        }

        return items;
    }
}
