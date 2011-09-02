package com.rj.processing.plasmasoundhd;

import com.rj.processing.mt.MTManager;
import com.rj.processing.plasmasoundhd.pd.instruments.Instrument;

public interface PlasmaActivity {
	public MTManager getMTManager();
	public Instrument getInst();
    

}
