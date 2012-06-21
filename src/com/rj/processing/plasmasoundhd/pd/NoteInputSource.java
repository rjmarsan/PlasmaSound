package com.rj.processing.plasmasoundhd.pd;

public interface NoteInputSource {
    
    public void setManager(NoteInputManager manager);
    public abstract String getName();
    public abstract boolean isConnected();
 
    
}
