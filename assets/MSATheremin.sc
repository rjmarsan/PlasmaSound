(		// stereo
		SynthDef(\rjFreeverb2x2, {| inbus=0, mix=1.25, red=0.15, green=0.5, fxlevel=1, level=0.5 | 
		var fx, sig; 
		sig = In.ar(inbus, 2); 
		fx = FreeVerb.ar(sig, mix, red, green); 
		ReplaceOut.ar(inbus, (fx*fxlevel) + (sig * level)); // level 
		},[0,0,0.1,0.1,0,0]).load(s); 
)

SynthDef(\stereosaw, {arg out=0, freq=333, amp=0.4, pan=0.0; // we add a new argument
	var signal;
	signal = LFSaw.ar(freq, 0, amp);
	signal = Pan2.ar(signal, pan);
	Out.ar(out, signal);
}).load(s) // we load the synthdef into the server
)
(
SynthDef(\stereosine, {arg out=0, freq=333, amp=0.4, pan=0.0; // we add a new argument
	var signal;
	signal = SinOsc.ar(freq, 0, amp);
	signal = Pan2.ar(signal, pan);
	Out.ar(out, signal);
}).load(s) // we load the synthdef into the server
)
(
SynthDef(\stereosquare, {arg out=0, freq=333, amp=0.4, pan=0.0; // we add a new argument
	var signal;
	signal = LFPulse.ar(freq, 0,0.5, amp);
	signal = Pan2.ar(signal, pan);
	Out.ar(out, signal);
}).load(s) // we load the synthdef into the server
)
(
SynthDef(\stereotriangle, {arg out=0, freq=333, amp=0.4, pan=0.0; // we add a new argument
	var signal;
	signal = LFTri.ar(freq, 0, amp);
	signal = Pan2.ar(signal, pan);
	Out.ar(out, signal);
}).load(s) // we load the synthdef into the server
)


(		// stereo
		SynthDef(\StereoPlaceholder, {arg inbus=0, outbus=0;
		 Out.ar(outbus,InFeedback.ar(inbus,2));
		}).store; 	
)


