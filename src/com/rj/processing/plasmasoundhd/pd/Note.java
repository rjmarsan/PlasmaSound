package com.rj.processing.plasmasoundhd.pd;

public class Note {
    public NoteInputSource source;
    public int id;
    public int index;
    /**
     * For backwards compatability, [0..127]
     */
    public float midivalue;
    /**
     * for my sanity [0..1]
     */
    public float velocity;
    
    public long lastUpdate;

    public Object data;
    
    
    public Note(int id, float midival, float velocity, NoteInputSource source) {
        this.id = id;
        this.midivalue = midival;
        this.velocity = velocity;
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
        float a1 = n1.midivalue/127f;
        float a2 = n2.midivalue/127f;
        float b1 = n1.velocity;
        float b2 = n2.velocity;
        return (a1-a2)*(a1-a2)+(b1-b2)*(b1-b2);
    }
    

}
