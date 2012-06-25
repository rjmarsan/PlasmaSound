package com.rj.processing.plasmasoundhd.pd;

import java.net.SocketException;
import java.util.Date;

import android.util.Log;

import com.illposed.osc.OSCListener;
import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortIn;

public class OSCNoteInputSource implements NoteInputSource {
    NoteInputManager manager;
    OSCPortIn in;
    int port;
    
    public OSCNoteInputSource(int port) {
        this.port = port;
        try {
            setupOSC(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }
    
    public void setupOSC(int port) throws SocketException {
        Log.d("OSCNoteInput", "setting up osc!");
        in = new OSCPortIn(port);
        in.addListener(Note.PRIMARY_NOTE+"/note_on", noteOnHandler);
        in.addListener(Note.PRIMARY_NOTE+"/note_off", noteOffHandler);
        in.addListener(Note.PRIMARY_NOTE+"/note_updated", noteUpdatedHandler);
        in.startListening();
        Log.d("OSCNoteInput", "set up!");
    }
    

    @Override
    public void setManager(NoteInputManager manager) {
        this.manager = manager;
    }

    @Override
    public String getName() {
        return "OSCNoteInputSource";
    }

    @Override
    public boolean isConnected() {
        if (in != null)
            return in.isListening();
        else
            return false;
    }


    public OSCListener noteOnHandler = new OSCListener() {
        public void acceptMessage(Date time, OSCMessage message) {
            Log.d("OSCNoteInput", "Note on!");
            message.setAddress(message.getAddress().replace("/note_on", ""));
            Note note = Note.fromOSC(message, time, OSCNoteInputSource.this);
            //Log.d("OSCNoteInput", "Sending note: "+note);
            if (manager != null) manager.noteOn(note);
            //if (manager != null) manager.noteUpdated(note);
        }
    };
    
    public OSCListener noteOffHandler = new OSCListener() {
        public void acceptMessage(Date time, OSCMessage message) {
            Log.d("OSCNoteInput", "Note off!");
            message.setAddress(message.getAddress().replace("/note_off", ""));
            Note note = Note.fromOSC(message, time, OSCNoteInputSource.this);
            if (manager != null) manager.noteOff(note);
        }
    };
    
    public OSCListener noteUpdatedHandler = new OSCListener() {
        public void acceptMessage(Date time, OSCMessage message) {
            Log.d("OSCNoteInput", "Note updated!");
            message.setAddress(message.getAddress().replace("/note_updated", ""));
            Note note = Note.fromOSC(message, time, OSCNoteInputSource.this);
            if (manager != null) manager.noteUpdated(note);
        }
    };

 
    
}
