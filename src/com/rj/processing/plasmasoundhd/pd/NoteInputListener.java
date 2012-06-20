package com.rj.processing.plasmasoundhd.pd;

public interface NoteInputListener {
    
    public void interfaceConnected(NoteInputManager manager, NoteInputSource input);
    
    public void interfaceDisconnected(NoteInputManager manager, NoteInputSource input);
    
    public void noteOn(NoteInputManager manager, Note note);
    
    public void noteUpdated(NoteInputManager manager, Note note);
    
    public void noteOff(NoteInputManager manager, Note note);
 
}
