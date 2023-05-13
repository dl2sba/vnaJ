/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels.marker;

import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import krause.util.GlobalSymbols;
import krause.util.StringHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarkerSearchMode.MARKERFIELDTYPE;
import krause.vna.gui.tune.VNATuneDialog;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNAMarker implements ClipboardOwner, MouseWheelListener, MouseListener, ActionListener, VNAApplicationStateObserver {
	private VNATuneDialog bigSWRDialog;
	private JCheckBox cbVisible = null;
	private final VNADataPool datapool = VNADataPool.getSingleton();
	private int diagramX = -1;
	private IMarkerEventEvaluator eventEvaluator = null;
	private boolean iAmVisible = false;
	private boolean isDynamicMarker = false;
	private boolean isMouseMarker = false;
	private JToggleButton labelBIGSWR = null;
	private JToggleButton labelMath = null;
	private JLabel lblName = null;
	private VNAMainFrame mainFrame;

	private Color markerColor = null;

	private VNAMarkerMathDialog mathDialog = null;
	private String name = null;
	private VNACalibratedSample sample = null;
	private String shortName;

	private VNAMarkerTextField txtFRQ = null;
	private VNAMarkerTextField txtLoss = null;
	private VNAMarkerTextField txtPhase = null;
	private VNAMarkerTextField txtR = null;
	private VNAMarkerTextField txtSwrGrpDelay = null;
	private VNAMarkerTextField txtTheta = null;

	private VNAMarkerTextField txtX = null;
	private VNAMarkerTextField txtZ = null;

	/**
	 * @param marker0
	 * @param pMF
	 * @param pMP
	 * @param listener
	 * @param pLine
	 * @param pStartCol
	 * @param pColor
	 */
	public VNAMarker(int id, VNAMainFrame pMF, VNAMarkerPanel pMP, ActionListener pLis, int pLine, int pStartCol, Color pColor) {
		this("" + id, pMF, pMP, pLis, pLine, pStartCol, pColor);
	}

	/**
	 * 
	 * @param pKey
	 * @param pMainFrame
	 * @param panel
	 * @param listener
	 * @param line
	 * @param startCol
	 * @param color
	 */
	public VNAMarker(String pKey, VNAMainFrame pMainFrame, JPanel panel, ActionListener listener, int line, int startCol, Color color) {
		TraceHelper.entry(this, "VNAMarker", "" + pKey);
		//
		mainFrame = pMainFrame;
		isDynamicMarker = (line == 4);
		isMouseMarker = (line == 2);
		markerColor = color;
		//
		name = VNAMessages.getString("Marker." + pKey);
		shortName = VNAMessages.getString("Marker." + pKey + ".short");
		lblName = new VNAMarkerLabel(name);
		lblName.setToolTipText(VNAMessages.getString("Marker.Name.Tooltip"));
		//
		panel.add(lblName, "");
		panel.add(txtFRQ = new VNAMarkerTextField(8), "");
		panel.add(txtLoss = new VNAMarkerTextField(4), "");
		panel.add(txtPhase = new VNAMarkerTextField(4), "");
		panel.add(txtZ = new VNAMarkerTextField(4), "");
		panel.add(txtR = new VNAMarkerTextField(4), "");
		panel.add(txtX = new VNAMarkerTextField(4), "");
		panel.add(txtTheta = new VNAMarkerTextField(4), "");
		panel.add(txtSwrGrpDelay = new VNAMarkerTextField(4), "");

		panel.add(cbVisible = new JCheckBox("", false), "");
		cbVisible.addActionListener(listener);
		cbVisible.setActionCommand(name);
		cbVisible.setToolTipText(VNAMessages.getString("Marker.Checkbox.Tooltip"));

		panel.add(labelMath = SwingUtil.createToggleButton("Marker.Math", this), "");
		panel.add(labelBIGSWR = SwingUtil.createToggleButton("Marker.BigSWR", this), "wrap");

		labelMath.setBorder(null);
		labelBIGSWR.setBorder(null);

		cbVisible.setBorder(null);
		lblName.setBorder(null);

		//
		if (!isMouseMarker && !isDynamicMarker) {
			lblName.addMouseListener(this);

			txtPhase.setMarkerSearchMode(new VNAMarkerSearchMode(MARKERFIELDTYPE.PHASE));
			txtPhase.addMouseListener(this);
			txtPhase.setToolTipText(VNAMessages.getString("Marker.Phase.Tooltip"));

			txtLoss.setMarkerSearchMode(new VNAMarkerSearchMode(MARKERFIELDTYPE.RL));
			txtLoss.addMouseListener(this);
			txtLoss.setToolTipText(VNAMessages.getString("Marker.Loss.Tooltip"));

			txtSwrGrpDelay.setMarkerSearchMode(new VNAMarkerSearchMode(MARKERFIELDTYPE.SWR));
			txtSwrGrpDelay.addMouseListener(this);
			txtSwrGrpDelay.setToolTipText(VNAMessages.getString("Marker.SWR.Tooltip"));
		}

		if (isMouseMarker) {
			cbVisible.setVisible(false);
			labelMath.setVisible(false);
			labelBIGSWR.setVisible(false);
		}
		if (isDynamicMarker) {
			txtSwrGrpDelay.setVisible(false);
			cbVisible.setVisible(false);
			labelMath.setVisible(false);
			labelBIGSWR.setVisible(false);
		}

		//
		setVisible(false);
		//
		TraceHelper.exit(this, "VNAMarker");
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		if (e.getSource() == labelMath) {
			doClickOnMathSymbol();
		} else if (e.getSource() == labelBIGSWR) {
			doClickOnBigSWRSymbol();
		}

		TraceHelper.exit(this, "actionPerformed");

	}

	@Override
	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		// TraceHelper.entry(this, "changeState", "old=" + oldState + " new=" +
		// newState);
		// TraceHelper.exit(this, "changeState");
	}

	/**
	 * 
	 */
	public void clearFields() {
		// TraceHelper.entry(this, "clearFields");
		txtFRQ.setText("");
		txtLoss.setText("");
		txtPhase.setText("");
		txtZ.setText("");
		txtR.setText("");
		txtX.setText("");
		txtTheta.setText("");
		txtSwrGrpDelay.setText("");
		//
		setVisible(false);
		//
		// TraceHelper.exit(this, "clearFields");
	}

	protected void copyMarkerData2Clipboard(MouseEvent e) {
		TraceHelper.entry(this, "copyMarkerData2Clipboard");
		String rc = new String();

		if (e.getButton() == MouseEvent.BUTTON3) {
			String names[] = new String[] {
					VNAMessages.getString("Marker.Frequency"),
					VNAMessages.getString("Marker.RL"),
					VNAMessages.getString("Marker.TL"),
					VNAMessages.getString("Marker.Phase"),
					VNAMessages.getString("Marker.Z"),
					VNAMessages.getString("Marker.R"),
					VNAMessages.getString("Marker.X"),
					VNAMessages.getString("Marker.SWR"),
					VNAMessages.getString("Marker.Theta"),
					VNAMessages.getString("Marker.Magnitude")
			};
			rc = StringHelper.array2String(names, "\t");
			rc += GlobalSymbols.LINE_SEPARATOR;
		}
		if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON3) {
			String values[] = new String[] {
					VNAFormatFactory.getFrequencyCalibrationFormat().format(getSample().getFrequency()),
					VNAFormatFactory.getReflectionLossFormat().format(getSample().getReflectionLoss()),
					VNAFormatFactory.getReflectionLossFormat().format(getSample().getTransmissionLoss()),
					VNAFormatFactory.getPhaseFormat().format(getSample().getReflectionPhase()),
					VNAFormatFactory.getZFormat().format(getSample().getZ()),
					VNAFormatFactory.getRsFormat().format(getSample().getR()),
					VNAFormatFactory.getXsFormat().format(getSample().getX()),
					VNAFormatFactory.getSwrFormat().format(getSample().getSWR()),
					VNAFormatFactory.getThetaFormat().format(getSample().getTheta()),
					VNAFormatFactory.getMagFormat().format(getSample().getMag()),
			};
			rc += StringHelper.array2String(values, "\t");
			StringSelection str = new StringSelection(rc);

			Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
			cb.setContents(str, this);
		}

		TraceHelper.exit(this, "copyMarkerData2Clipboard");
	}

	/**
	 * 
	 */
	public void doClickOnBigSWRSymbol() {
		TraceHelper.entry(this, "doClickOnBigSWRSymbol");
		if (bigSWRDialog == null) {
			bigSWRDialog = new VNATuneDialog(this);
			labelBIGSWR.setSelected(true);
		} else {
			bigSWRDialog.setVisible(false);
			bigSWRDialog.dispose();
			bigSWRDialog = null;
			labelBIGSWR.setSelected(false);
		}
		TraceHelper.exit(this, "doClickOnBigSWRSymbol");
	}

	/*
	 * 
	 * 
	 */
	public void doClickOnMathSymbol() {
		TraceHelper.entry(this, "doClickOnMathSymbol");
		if (mathDialog == null) {
			mathDialog = new VNAMarkerMathDialog(this);
			mathDialog.doDialogShow();
			mathDialog.update();
			labelMath.setSelected(true);
		} else {
			mathDialog.setVisible(false);
			mathDialog.dispose();
			mathDialog = null;
			labelMath.setSelected(false);
		}
		TraceHelper.exit(this, "doClickOnMathSymbol");
	}

	public int getDiagramX() {
		return diagramX;
	}

	/**
	 * 
	 * @return
	 */
	public long getFrequency() {
		return sample.getFrequency();
	}

	public Color getMarkerColor() {
		return markerColor;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the sample
	 */
	public VNACalibratedSample getSample() {
		return sample;
	}

	public String getShortName() {
		return shortName;
	}

	/**
	 * @return Returns the txtFrequence.
	 */
	public VNAMarkerTextField getTxtFrequency() {
		return txtFRQ;
	}

	public VNAMarkerTextField getTxtFRQ() {
		return txtFRQ;
	}

	public VNAMarkerTextField getTxtLoss() {
		return txtLoss;
	}

	public VNAMarkerTextField getTxtPhase() {
		return txtPhase;
	}

	public VNAMarkerTextField getTxtR() {
		return txtR;
	}

	/**
	 * @return Returns the txtRs.
	 */
	public VNAMarkerTextField getTxtRs() {
		return txtR;
	}

	public VNAMarkerTextField getTxtSwrGrpDelay() {
		return txtSwrGrpDelay;
	}

	public VNAMarkerTextField getTxtTheta() {
		return txtTheta;
	}

	public VNAMarkerTextField getTxtX() {
		return txtX;
	}

	/**
	 * @return Returns the txtXsAbsolute.
	 */
	public VNAMarkerTextField getTxtXsAbsolute() {
		return txtX;
	}

	public VNAMarkerTextField getTxtZ() {
		return txtZ;
	}

	/**
	 * @return Returns the txtZAbsolute.
	 */
	public VNAMarkerTextField getTxtZAbsolute() {
		return txtZ;
	}

	public boolean isMyMouseEvent(MouseEvent e) {
		boolean rc = false;
		if (eventEvaluator != null) {
			rc = eventEvaluator.isMyMouseEvent(e);
		}

		return rc;
	}

	/**
	 * @param e
	 * @return
	 */
	public boolean isMyMouseWheelEvent(MouseWheelEvent e) {
		boolean rc = false;
		if (eventEvaluator != null) {
			rc = eventEvaluator.isMyMouseWheelEvent(e);
		}
		return rc;
	}

	/**
	 * Use the internal variable because the state of the checkbox can change after the paint of the diagram because of the event
	 * queue handling
	 * 
	 * @return true if the marker should be painted in the diagram
	 * 
	 */
	public boolean isVisible() {
		return iAmVisible;
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		TraceHelper.entry(this, "mouseClicked");
		if (!isVisible()) {
			Toolkit.getDefaultToolkit().beep();
			return;
		}

		if (e.getSource() == lblName) {
			copyMarkerData2Clipboard(e);

		} else if (e.getSource() == txtPhase) {
			if (txtPhase.toggleSearchMode()) {
				txtSwrGrpDelay.clearSearchMode();
				txtLoss.clearSearchMode();
			}
			moveMarkerToData(datapool.getCalibratedData());
			mainFrame.getDiagramPanel().getImagePanel().repaint();

		} else if (e.getSource() == txtSwrGrpDelay) {
			if (txtSwrGrpDelay.toggleSearchMode()) {
				txtPhase.clearSearchMode();
				txtLoss.clearSearchMode();
			}
			moveMarkerToData(datapool.getCalibratedData());
			mainFrame.getDiagramPanel().getImagePanel().repaint();

		} else if (e.getSource() == txtLoss) {
			if (txtLoss.toggleSearchMode()) {
				txtPhase.clearSearchMode();
				txtSwrGrpDelay.clearSearchMode();
			}
			moveMarkerToData(datapool.getCalibratedData());
			mainFrame.getDiagramPanel().getImagePanel().repaint();
		}
		TraceHelper.exit(this, "mouseClicked");
	}

	public void mouseEntered(MouseEvent e) {
		// TraceHelper.entry(this, "mouseEntered");
		// TraceHelper.exit(this, "mouseEntered");
	}

	public void mouseExited(MouseEvent e) {
		// TraceHelper.entry(this, "mouseExited");
		// TraceHelper.exit(this, "mouseExited");
	}

	public void mousePressed(MouseEvent e) {
		// TraceHelper.entry(this, "mousePressed");
		// TraceHelper.exit(this, "mousePressed");
	}

	public void mouseReleased(MouseEvent e) {
		// TraceHelper.entry(this, "mouseReleased");
		// TraceHelper.exit(this, "mouseReleased");
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		// TraceHelper.entry(this, "mouseWheelMoved");
		if (isVisible()) {
			int delta = 0;
			if (e.getWheelRotation() < 0) {
				delta = -1;
			} else if (e.getWheelRotation() > 0) {
				delta = 1;
			}
			VNACalibratedSample sample = null;
			sample = mainFrame.getDiagramPanel().getImagePanel().getSampleAtMousePosition(getSample().getDiagramX() + delta);
			if (sample != null) {
				update(sample);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
			mainFrame.getDiagramPanel().getImagePanel().repaint();
		}
		// TraceHelper.exit(this, "mouseWheelMoved");
	}

	/**
	 * Move the marker to the data identified by one of the search modes
	 * 
	 * If no
	 * 
	 * @param data
	 */
	public void moveMarkerToData(VNACalibratedSampleBlock data) {
		// TraceHelper.entry(this, "moveMarkerToData", "m=" + getShortName());
		VNACalibratedSample foundSample = null;
		int idx = -1;

		// find the data index which matches the search criteria
		if (txtLoss.getMarkerSearchMode().isMaximum()) {
			if (datapool.getScanMode().isReflectionMode()) {
				idx = data.getMmRL().getMaxIndex();
			} else {
				idx = data.getMmTL().getMaxIndex();
			}
		} else if (txtLoss.getMarkerSearchMode().isMinimum()) {
			if (datapool.getScanMode().isReflectionMode()) {
				idx = data.getMmRL().getMinIndex();
			} else {
				idx = data.getMmTL().getMinIndex();
			}
		} else if (txtPhase.getMarkerSearchMode().isMaximum()) {
			if (datapool.getScanMode().isReflectionMode()) {
				idx = data.getMmRP().getMaxIndex();
			} else {
				idx = data.getMmTP().getMaxIndex();
			}
		} else if (txtPhase.getMarkerSearchMode().isMinimum()) {
			if (datapool.getScanMode().isReflectionMode()) {
				idx = data.getMmRP().getMinIndex();
			} else {
				idx = data.getMmTP().getMinIndex();
			}
		} else if (txtSwrGrpDelay.getMarkerSearchMode().isMaximum()) {
			if (datapool.getScanMode().isReflectionMode()) {
				idx = data.getMmSWR().getMaxIndex();
			} else {
				idx = data.getMmGRPDLY().getMaxIndex();
			}
		} else if (txtSwrGrpDelay.getMarkerSearchMode().isMinimum()) {
			if (datapool.getScanMode().isReflectionMode()) {
				idx = data.getMmSWR().getMinIndex();
			} else {
				idx = data.getMmGRPDLY().getMinIndex();
			}
		}

		// any search criteria found?
		if (idx != -1) {
			// yes
			// get sample by index
			foundSample = data.getCalibratedSamples()[idx];

			// TraceHelper.text(this, "moveMarkerToData", "found at " +
			// foundSample.getFrequency());
			// TraceHelper.text(this, "moveMarkerToData", "pos " +
			// foundSample.getDiagramX());
			foundSample.setDiagramX(idx);

			// update the marker to the found data
			update(foundSample);
		}
		// TraceHelper.exit(this, "moveMarkerToData");
	}

	/**
	 * move the marker to the sample that matches the given frequency
	 * 
	 * @param targetFrq
	 */
	public void moveMarkerToFrequency(long targetFrq) {
		TraceHelper.entry(this, "moveMarkerToFrequency");
		VNACalibratedSampleBlock cd = VNADataPool.getSingleton().getCalibratedData();
		if (cd != null) {
			for (VNACalibratedSample cs : cd.getCalibratedSamples()) {
				if (cs.getFrequency() >= targetFrq) {
					update(cs);
					break;
				}
			}
		}
		TraceHelper.exit(this, "moveMarkerToFrequency");
	}

	public void setDiagramX(int diagramX) {
		this.diagramX = diagramX;
	}

	public void setEventEvaluator(IMarkerEventEvaluator eventEvaluator) {
		this.eventEvaluator = eventEvaluator;
	}

	public void setMarkerColor(Color markerColor) {
		this.markerColor = markerColor;
	}

	/**
	 * 
	 * @param v
	 */
	public void setVisible(boolean v) {
		iAmVisible = v;
		cbVisible.setSelected(v);
		cbVisible.setEnabled(v);
		labelMath.setEnabled(v);
		labelBIGSWR.setEnabled(v);
	}

	/**
	 * 
	 * @param sample
	 */
	public void update(VNACalibratedSample s) {
		// store in internal sample
		sample = s;
		// a sample set ?
		if (s != null) {
			// yes
			// marker already enabled?
			if (!isVisible()) {
				// no
				// enable it
				setVisible(true);
			}

			// set index on diagram
			setDiagramX(s.getDiagramX());

			// update marker fields
			txtFRQ.setText(VNAFormatFactory.formatFrequency(s.getFrequency()));

			if (datapool.getScanMode().isReflectionMode()) {
				txtLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(s.getReflectionLoss()));
				txtPhase.setText(VNAFormatFactory.getPhaseFormat().format(s.getReflectionPhase()));
				txtSwrGrpDelay.setText(VNAFormatFactory.getSwrFormat().format(s.getSWR()) + ":1");
			} else if (datapool.getScanMode().isTransmissionMode()) {
				txtLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(s.getTransmissionLoss()));
				txtPhase.setText(VNAFormatFactory.getPhaseFormat().format(s.getTransmissionPhase()));
				txtSwrGrpDelay.setText(VNAFormatFactory.getGroupDelayFormat().format(s.getGroupDelay()));
			}

			txtTheta.setText(VNAFormatFactory.getThetaFormat().format(s.getTheta()));
			txtZ.setText(VNAFormatFactory.getZFormat().format(s.getZ()));
			txtR.setText(VNAFormatFactory.getRsFormat().format(s.getR()));
			txtX.setText(VNAFormatFactory.getXsFormat().format(s.getX()));

			// math dialog opened?
			if (mathDialog != null) {
				// yes
				// update also this dialog
				mathDialog.update();
			}

			// big swr dialog open
			if (bigSWRDialog != null) {
				// yes
				// update also this dialog
				bigSWRDialog.update(s);
			}
		} else {
			// no
			// clear all text fields
			clearFields();
		}
	}
}
