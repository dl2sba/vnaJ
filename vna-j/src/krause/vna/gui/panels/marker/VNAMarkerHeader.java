/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAMarkerHeader.java
 *  Part of:   vna-j
 */

package krause.vna.gui.panels.marker;

import javax.swing.JPanel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNADataPool;
import krause.vna.data.VNAScanMode;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar
 * 
 */
public class VNAMarkerHeader implements VNAApplicationStateObserver {
	private final VNADataPool datapool = VNADataPool.getSingleton();

	private VNAMarkerLabel lblLoss;
	private VNAMarkerLabel lblPhase;
	private VNAMarkerLabel lblSwrGrpDelay;

	/**
	 * @param vnaMarkerPanel
	 * @param i
	 */
	public VNAMarkerHeader(JPanel panel, int line) {
		TraceHelper.entry(this, "VNAMarkerHeader");
		panel.add(new VNAMarkerLabel(""), "");
		panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.Frequency")), "");
		panel.add(lblLoss = new VNAMarkerLabel(VNAMessages.getString("Marker.RL")), "");
		panel.add(lblPhase = new VNAMarkerLabel(VNAMessages.getString("Marker.PhaseRL")), "");
		panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.Z")), "");
		panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.R")), "");
		panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.X")), "");
		panel.add(new VNAMarkerLabel(VNAMessages.getString("Marker.Theta")), "");
		panel.add(lblSwrGrpDelay = new VNAMarkerLabel(VNAMessages.getString("Marker.SWR")), "wrap");
		TraceHelper.exit(this, "VNAMarkerHeader");
	}

	@Override
	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
//		TraceHelper.entry(this, "changeState", "old=" + oldState + " new=" + newState);
		if ((oldState == INNERSTATE.CALIBRATED) && (newState == INNERSTATE.CALIBRATED)) {
			if (datapool.getScanMode() == VNAScanMode.MODE_REFLECTION) {
				lblLoss.setText(VNAMessages.getString("Marker.RL"));
				lblPhase.setText(VNAMessages.getString("Marker.PhaseRL"));
				lblSwrGrpDelay.setText(VNAMessages.getString("Marker.SWR"));
			} else if (datapool.getScanMode() == VNAScanMode.MODE_TRANSMISSION) {
				lblLoss.setText(VNAMessages.getString("Marker.TL"));
				lblPhase.setText(VNAMessages.getString("Marker.PhaseTL"));
				lblSwrGrpDelay.setText(VNAMessages.getString("Marker.GrpDelay"));
			}
		}
//		TraceHelper.exit(this, "changeState");
	}
}
