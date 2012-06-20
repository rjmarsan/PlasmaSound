package com.rj.processing.plasmasoundhd.pd;

public abstract class NoteInputSource {
    NoteInputManager manager;
    
    public void setManager(NoteInputManager manager) {
        this.manager = manager;
    }
    
    public abstract String getName();
    public abstract boolean isConnected();
 
}
