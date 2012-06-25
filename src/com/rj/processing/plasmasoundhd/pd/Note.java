package com.rj.processing.plasmasoundhd.pd;

import java.util.Date;

import com.illposed.osc.OSCMessage;

/*
 * 
 * Things that a Note object would like to keep track of
 * Source
 * Note On, Updated, Off
 * Extra Data
 * 
 */

/**
 * This is the internal note passing data object.
 * it's similar to OSC, but it's intended to be persistent across the lifetime of the note
 * 
 * @author rj
 *
 */
public class Note {
    public final static String PRIMARY_NOTE         = "/primary_note";
    
    
    public NoteInputSource source;
    public String path;
    public int id;
    public int index;
    /**
     * For backwards compatability, [0..127]
     */
    public float notevalue;
    /**
     * for my sanity [0..1]
     */
    public float controlvalue;
    
    public long lastUpdate;

    public Object data;
    
    
    public Note(int id, String path, float midival, float velocity, NoteInputSource source) {
        this.path = path;
        this.id = id;
        this.notevalue = midival;
        this.controlvalue = velocity;
        this.source = source;
    }

    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((source == null) ? 0 : source.hashCode());
        return result;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Note other = (Note) obj;
        if (id != other.id)
            return false;
        if (source == null) {
            if (other.source != null)
                return false;
        } else if (!source.equals(other.source))
            return false;
        return true;
    }


    public static float distance(Note n1, Note n2) {
        float a1 = n1.notevalue/127f;
        float a2 = n2.notevalue/127f;
        float b1 = n1.controlvalue;
        float b2 = n2.controlvalue;
        return (a1-a2)*(a1-a2)+(b1-b2)*(b1-b2);
    }
    
    /** Format for fromOSC:
     * 1) id, 2) noteval, 3) controlval
     * @param message
     * @param time
     * @return
     */
    public static Note fromOSC(OSCMessage message, Date time, NoteInputSource source) {
        String path = message.getAddress();
        int id = (Integer)message.getArguments()[0];
        float noteval = (Integer)message.getArguments()[1];
        float controlval = (Integer)message.getArguments()[2];
        return new Note(id, path, noteval, controlval, source);
    }
    
    
    
    @Override
    public String toString() {
        return "Note [source=" + source + ", path=" + path + ", id=" + id + ", index=" + index
                + ", notevalue=" + notevalue + ", controlvalue=" + controlvalue + ", lastUpdate="
                + lastUpdate + ", data=" + data + "]";
    }


}
