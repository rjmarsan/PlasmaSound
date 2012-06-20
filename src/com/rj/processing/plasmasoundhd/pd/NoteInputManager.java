package com.rj.processing.plasmasoundhd.pd;

import java.util.ArrayList;
import java.util.List;

public class NoteInputManager {
    
    
    public List<NoteInputSource> inputSources = new ArrayList<NoteInputSource>();
    
    public void addInputSource(NoteInputSource source) {
        source.setManager(this);
        inputSources.add(source);
    }
    
    
    private ArrayList<NoteInputListener> listeners = new ArrayList<NoteInputListener>();
    
    public void registerListener(NoteInputListener listener) {
        listeners.add(listener);
    }
    
    public void unregisterListener(NoteInputListener listener) {
        listeners.remove(listener);
    }
    
}
