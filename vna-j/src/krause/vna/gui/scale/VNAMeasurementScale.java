/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import krause.util.ResourceLoader;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAMeasurementScale extends VNADiagramScale implements MouseListener, MouseMotionListener {

	private transient VNAGenericScale scale = VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_NONE);
	protected static VNAConfig config = VNAConfig.getSingleton();
	private boolean leftScale = false;
	private Frame owner;

	private boolean dragging = false;
	private int lastDragPos = 0;
	private boolean dragModeRange = false;
	private Cursor lastMouseCursor;
	private Cursor cursorRange;
	private Cursor cursorMove;
	private Cursor cursorScale;

	/**
	 * 
	 * @param type
	 * @param isLeftScale
	 */
	public VNAMeasurementScale(VNAGenericScale pScale, boolean isLeftScale, Frame pOwner) {
		owner = pOwner;
		addMouseListener(this);
		addMouseMotionListener(this);
		scale = pScale;
		leftScale = isLeftScale;
		//
		byte[] iconBytes = null;
		try {
			iconBytes = ResourceLoader.getResourceAsByteArray("images/zoomIn16.gif");
		} catch (Exception e) {
			// not used
		}
		ImageIcon icon = new ImageIcon(iconBytes, "zoom");
		cursorRange = Toolkit.getDefaultToolkit().createCustomCursor(icon.getImage(), new Point(0, 0), "img");
		cursorMove = Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
		cursorScale = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);

		//
		setMinimumSize(new Dimension(40, 30));
		setPreferredSize(getMinimumSize());
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//
		getScale().paintScale(getWidth(), getHeight(), g);
	}

	/**
	 * @return Returns the scaleType.
	 */
	public VNAGenericScale getScale() {
		return scale;
	}

	/**
	 * @param scaleType
	 *            The scaleType to set.
	 */
	public void setScale(VNAGenericScale scaleType) {
		this.scale = scaleType;
	}

	/**
	 * @return Returns the leftScale.
	 */
	public boolean isLeftScale() {
		return leftScale;
	}

	/**
	 * @param leftScale
	 *            The leftScale to set.
	 */
	public void setLeftScale(boolean leftScale) {
		this.leftScale = leftScale;
	}

	public void mouseClicked(MouseEvent e) {
		TraceHelper.entry(this, "mouseClicked");

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!getScale().supportsCustomScaling()) {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.NotSupported"), VNAMessages.getString("Scale.Customscale"), JOptionPane.WARNING_MESSAGE);
			} else {
				if (config.isAutoscaleEnabled()) {
					JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.Remove"), VNAMessages.getString("Scale.Customscale.Autoscale"), JOptionPane.WARNING_MESSAGE);
				} else {
					VNAScaleConfigDialog dlg = new VNAScaleConfigDialog(owner, this);
					if (dlg.isExitWithOK()) {
						getScale().rescale();
						owner.repaint();
					}
				}
			}
		}
		TraceHelper.exit(this, "mouseClicked");
	}

	public void mouseEntered(MouseEvent e) {
		lastMouseCursor = getCursor();
		setCursor(cursorScale);
	}

	public void mouseExited(MouseEvent e) {
		setCursor(lastMouseCursor);
	}

	public void mousePressed(MouseEvent e) {
		if (!dragging) {
			if (!getScale().supportsCustomScaling()) {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.NotSupported"), VNAMessages.getString("Scale.Customscale"), JOptionPane.WARNING_MESSAGE);
			} else if (config.isAutoscaleEnabled()) {
				JOptionPane.showMessageDialog(this, VNAMessages.getString("Scale.Customscale.Remove"), VNAMessages.getString("Scale.Customscale.Autoscale"), JOptionPane.WARNING_MESSAGE);
			} else {
				dragging = true;
				lastDragPos = e.getY();
				dragModeRange = (e.getButton() == MouseEvent.BUTTON3);
				if (dragModeRange) {
					setCursor(cursorMove);
				} else {
					setCursor(cursorRange);
				}
			}
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (dragging) {
			dragging = false;
			setCursor(cursorScale);
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (dragging) {
			boolean dragUp = (lastDragPos > e.getY());
			lastDragPos = e.getY();
			if (dragModeRange) {
				double min = getScale().getCurrentMinValue();
				double max = getScale().getCurrentMaxValue();

				double delta = getScale().getRange() / 40;
				if (dragUp) {
					min -= delta;
					max -= delta;
				} else {
					min += delta;
					max += delta;
				}
				if ((min >= getScale().getDefaultMinValue()) && (max <= getScale().getDefaultMaxValue())) {
					getScale().setCurrentMinValue(min);
					getScale().setCurrentMaxValue(max);
					getScale().rescale();
					owner.repaint();
				}
			} else {
				double max = getScale().getCurrentMaxValue();
				double delta = getScale().getRange() / 30;
				if (dragUp) {
					max += delta;
				} else {
					max -= delta;
				}
				getScale().setCurrentMaxValue(max);
				getScale().rescale();
				owner.repaint();
			}
		}
	}

	public void mouseMoved(MouseEvent e) {
		// not used
	}

	public void setupColors() {
		TraceHelper.entry(this, "setupColors");
		TraceHelper.exit(this, "setupColors");
	}

}
