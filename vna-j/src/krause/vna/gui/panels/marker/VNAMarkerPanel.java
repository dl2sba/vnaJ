/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels.marker;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JPanel;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNADataPool;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.VNAScaleSelectPanel;
import net.miginfocom.swing.MigLayout;

public class VNAMarkerPanel extends JPanel implements ActionListener, VNAApplicationStateObserver, WindowListener {
	private final VNAConfig config = VNAConfig.getSingleton();
	private final VNADataPool datapool = VNADataPool.getSingleton();

	private VNAMainFrame mainFrame;

	public final static int MARKER_0 = 0;
	public final static int MARKER_1 = 1;
	public final static int MARKER_2 = 2;
	public final static int MARKER_3 = 3;
	public final static int NUM_MARKERS = 4;

	private VNAMarker markers[] = new VNAMarker[NUM_MARKERS];
	private VNAMarker mouseMarker;
	private VNAMarker deltaMarker;
	private VNAMarkerHeader markerHeader;

	public VNAMarkerPanel(VNAMainFrame pMainFrame) {
		// super(null, VNAMessages.getString("VNAMarkerPanel.title"));
		// setBounds(new Rectangle(0, 0, 700, 170));
		TraceHelper.entry(this, "VNAMarkerPanel");

		setFont(new Font("Tahoma", Font.PLAIN, 8));
		//
		mainFrame = pMainFrame;

		setLayout(new MigLayout("", "[][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][grow,fill][][][]", "[]"));

		// headline
		int line = 0;
		markerHeader = new VNAMarkerHeader(this, ++line);
		mouseMarker = new VNAMarker("Maus", mainFrame, this, this, ++line, 1, Color.BLACK);
		markers[MARKER_0] = new VNAMarker(MARKER_0, mainFrame, this, this, ++line, 1, config.getColorMarker(MARKER_0));
		deltaMarker = new VNAMarker("Delta", mainFrame, this, this, ++line, 1, Color.BLACK);
		markers[MARKER_1] = new VNAMarker(MARKER_1, mainFrame, this, this, ++line, 1, config.getColorMarker(MARKER_1));
		markers[MARKER_2] = new VNAMarker(MARKER_2, mainFrame, this, this, ++line, 1, config.getColorMarker(MARKER_2));
		markers[MARKER_3] = new VNAMarker(MARKER_3, mainFrame, this, this, ++line, 1, config.getColorMarker(MARKER_3));

		markers[MARKER_0].setEventEvaluator(new Marker0EventEvaluator());
		markers[MARKER_1].setEventEvaluator(new Marker1EventEvaluator());
		markers[MARKER_2].setEventEvaluator(new Marker2EventEvaluator());
		markers[MARKER_3].setEventEvaluator(new Marker3EventEvaluator());

		TraceHelper.exit(this, "VNAMarkerPanel");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TraceHelper.entry(this, "actionPerformed", cmd);
		VNAMarker marker = null;

		for (int i = 0; i < NUM_MARKERS; ++i) {
			if (markers[i].getName().equals(cmd)) {
				marker = markers[i];
				break;
			}
		}

		if (marker != null) {
			if (marker.isVisible()) {
				marker.clearFields();
				mainFrame.getDiagramPanel().repaint();
				VNAScaleSelectPanel ssp = mainFrame.getDiagramPanel().getScaleSelectPanel();
				if (ssp.getSmithDialog() != null) {
					ssp.getSmithDialog().consumeCalibratedData(datapool.getCalibratedData());
				}
			}
		}
		TraceHelper.entry(this, "actionPerformed");
	}

	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		// TraceHelper.entry(this, "changeState", "old=" + oldState + " new=" + newState);
		for (VNAMarker oneMarker : markers) {
			oneMarker.changeState(oldState, newState);
		}
		markerHeader.changeState(oldState, newState);
		mouseMarker.changeState(oldState, newState);
		deltaMarker.changeState(oldState, newState);

		// TraceHelper.exit(this, "changeState");
	}

	public void consumeMouseWheelEvent(MouseWheelEvent e) {
		// TraceHelper.entry(this, "consumeMouseWheelEvent", "" + e.getModifiers());

		boolean oneMarkerVisible = false;

		for (int i = 0; i < NUM_MARKERS; ++i) {
			if (markers[i].isMyMouseWheelEvent(e)) {
				oneMarkerVisible = true;
				markers[i].mouseWheelMoved(e);
			}
		}

		if (oneMarkerVisible) {
			VNAScaleSelectPanel ssp = mainFrame.getDiagramPanel().getScaleSelectPanel();
			if (ssp.getSmithDialog() != null) {
				ssp.getSmithDialog().consumeCalibratedData(datapool.getCalibratedData());
			}
		}
		// TraceHelper.exit(this, "consumeMouseWheelEvent");
	}

	public VNAMainFrame getMainFrame() {
		return mainFrame;
	}

	public VNAMarker getMarker(int i) {
		return markers[i];
	}

	/**
	 * @param e
	 * @return
	 */
	public VNAMarker getMarkerForMouseEvent(MouseEvent e) {
		VNAMarker rc = null;
		TraceHelper.entry(this, "getMarkerForMouseEvent");
		for (int i = 0; i < NUM_MARKERS; ++i) {
			if (markers[i].isMyMouseEvent(e)) {
				rc = markers[i];
				break;
			}
		}
		TraceHelper.exit(this, "getMarkerForMouseEvent");
		return rc;
	}

	public void setupColors() {
		TraceHelper.entry(this, "setupColors");
		for (int i = 0; i < NUM_MARKERS; ++i) {
			markers[i].setMarkerColor(config.getColorMarker(i));
		}
		TraceHelper.exit(this, "setupColors");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent e) {
		TraceHelper.entry(this, "windowActivated");
		TraceHelper.exit(this, "windowActivated");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent e) {
		TraceHelper.entry(this, "windowClosed");
		TraceHelper.exit(this, "windowClosed");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent e) {
		TraceHelper.entry(this, "windowClosing");
		TraceHelper.exit(this, "windowClosing");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent )
	 */
	public void windowDeactivated(WindowEvent e) {
		TraceHelper.entry(this, "windowDeactivated");
		TraceHelper.exit(this, "windowDeactivated");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent )
	 */
	public void windowDeiconified(WindowEvent e) {
		TraceHelper.entry(this, "windowDeiconified");
		TraceHelper.exit(this, "windowDeiconified");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent e) {
		TraceHelper.entry(this, "windowIconified");
		TraceHelper.exit(this, "windowIconified");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent e) {
		TraceHelper.entry(this, "windowOpened");
		TraceHelper.exit(this, "windowOpened");

	}

	public VNAMarker[] getMarkers() {
		return markers;
	}

	public VNAMarker getMouseMarker() {
		return mouseMarker;
	}

	public VNAMarker getDeltaMarker() {
		return deltaMarker;
	}
}
