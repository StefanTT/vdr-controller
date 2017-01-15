package com.github.stefantt.vdrcontroller.entity;

/**
 * An item of VDR's OSD.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 */
public class VdrOsdItem
{
    private final VdrOsdItemType type;
    private final String label;
    private final String value;

    public VdrOsdItem(VdrOsdItemType type, String label, String value)
    {
        this.type = type;
        this.label = label;
        this.value = value;
    }

    public VdrOsdItemType getType()
    {
        return type;
    }

    public String getLabel()
    {
        return label;
    }

    public String getValue()
    {
        return value;
    }
}
