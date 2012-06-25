package com.rj.processing.plasmasoundhd.pd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NoteInputManager {
    
    
    public List<NoteInputSource> inputSources = new ArrayList<NoteInputSource>();
    public List<Note> currentNotes = new ArrayList<Note>();
    
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
    
    
    public void noteOn(Note note) {
        synchronized(currentNotes) {
            currentNotes.add(note);
        }
        sendNoteOn(note);
    }
    
    public void noteOff(Note note) {
        synchronized(currentNotes) {
            currentNotes.remove(note);
        }
        sendNoteOff(note);
    }
    
    public void noteUpdated(Note note) {
        synchronized(currentNotes) {
            currentNotes.remove(note);
            currentNotes.add(note);
        }
        sendNoteUpdated(note);
    }
    
    public void clear(NoteInputSource source) {
        ArrayList<Note> noteClone = new ArrayList<Note>(currentNotes);
        for (Note note : noteClone) {
            if (note.source.equals(source)) {
                noteOff(note);
            }
        }
    }
    
    
    private void sendNoteOn(Note note) {
        for (NoteInputListener l : listeners) {
            l.noteOn(this, note);
        }
    }
    private void sendNoteOff(Note note) {
        for (NoteInputListener l : listeners) {
            l.noteOff(this, note);
        }
    }
    private void sendNoteUpdated(Note note) {
        for (NoteInputListener l : listeners) {
            l.noteUpdated(this, note);
        }
    }
    
}
