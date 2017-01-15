package com.github.stefantt.vdrcontroller.entity;

import java.util.HashSet;
import java.util.Set;

/**
 * A folder that can contain other folders and entities of a specific type.
 *
 * @author Stefan Taferner <stefan.taferner@gmx.at>
 *
 * @param <T> The type of the entity
 */
public class VirtualFolder<T extends Named> implements Named, VirtualFolderEntry
{
    private final Set<VirtualFolder<T>> folders = new HashSet<>();
    private final Set<T> files = new HashSet<>();
    private VirtualFolder<T> parent;
    private int numRecordings = 0;
    private String name;

    public VirtualFolder()
    {
    }

    public VirtualFolder(String name, VirtualFolder<T> parent)
    {
        this.name = name;
        this.parent = parent;
    }

    public void add(VirtualFolder<T> folder)
    {
        this.folders.add(folder);
    }

    public void add(T file)
    {
        this.files.add(file);
    }

    public void remove(VirtualFolder<T> folder)
    {
        this.folders.remove(folder);
    }

    public void remove(T file)
    {
        this.files.remove(file);
    }

    @Override
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Find a child folder by it's name.
     *
     * @param name The name of the folder to find
     * @return The folder, null if not found
     */
    public VirtualFolder<T> getFolder(String name)
    {
        for (VirtualFolder<T> folder : folders)
        {
            if (name.equals(folder.getName()))
                return folder;
        }
        return null;
    }

    /**
     * @return All child folders.
     */
    public Set<VirtualFolder<T>> getFolders()
    {
        return folders;
    }

    /**
     * Find a file by it's name.
     *
     * @param name The name of the file to find
     * @return The file, null if not found
     */
    public T getFile(String name)
    {
        for (T file : files)
        {
            if (name.equals(file.getName()))
                return file;
        }
        return null;
    }

    /**
     * @return All files in the folder.
     */
    public Set<T> getFiles()
    {
        return files;
    }

    public VirtualFolder<T> getParent()
    {
        return parent;
    }

    public void setParent(VirtualFolder<T> parent)
    {
        this.parent = parent;
    }

    public int getNumRecordings()
    {
        return numRecordings;
    }

    public void setNumRecordings(int numRecordings)
    {
        this.numRecordings = numRecordings;
    }

    public void incNumRecordings()
    {
        this.numRecordings++;
    }
}