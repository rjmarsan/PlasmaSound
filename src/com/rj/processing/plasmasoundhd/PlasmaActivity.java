package com.rj.processing.plasmasoundhd;

import com.rj.processing.mt.MTManager;
import com.rj.processing.plasmasounddonate.R;
import com.rj.processing.plasmasoundhd.pd.PDManager;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;

public interface PlasmaActivity {
	public MTManager getMTManager();
	public Instrument getInst();
	public PDManager getPD();
    

}
