/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNAApplicationState.INNERSTATE;
import krause.vna.data.VNABandMap;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.helper.VNACalibratedSampleHelper;
import krause.vna.data.observer.VNAApplicationStateObserver;
import krause.vna.data.reference.VNAReferenceDataBlock;
import krause.vna.data.reference.VNAReferenceDataHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.gui.panels.marker.VNAMarkerPanel;
import krause.vna.gui.scale.VNAGenericScale;
import krause.vna.gui.util.VNAFrequencyPair;

/**
 * This component draws the measured samples into the center of the applications window.
 */
public class VNAImagePanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, ComponentListener, VNAApplicationStateObserver {
	private final VNAConfig config = VNAConfig.getSingleton();
	private final transient VNADataPool datapool = VNADataPool.getSingleton();

	private static final int SAMPLE_HISTORY_DEPTH = 8;
	private static final int RED_MASK = 255 << 16;
	private static final int GREEN_MASK = 255 << 8;
	private static final int BLUE_MASK = 255;
	private static final float BRIGHT_SCALE = 0.8f;

	private final Font textFont = new java.awt.Font("Dialog", java.awt.Font.PLAIN, 17);
	private final transient VNABandMap bandMap = new VNABandMap();

	private int lastMouseX = -1;
	private transient VNAMainFrame mainFrame;
	@SuppressWarnings("unused")
	private int lastMouseY = -1;
	private Cursor lastCursor = null;

	private List<VNACalibratedSample[]> sampleHistory = new ArrayList<>();
	private Color[] historyColorsLeft = new Color[SAMPLE_HISTORY_DEPTH];
	private Color[] historyColorsRight = new Color[SAMPLE_HISTORY_DEPTH];

	/**
	 * Create a new panel. Add a resize listener which automatically pushes the size change as a change in the number of samples to
	 * the Thread.
	 */
	public VNAImagePanel(VNAMainFrame pMainFrame) {
		super();
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		//
		mainFrame = pMainFrame;

		// add the mouse stuff
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		//
		addComponentListener(this);
		//
		TraceHelper.exit(this, "componentResized");
	}

	public void componentHidden(ComponentEvent arg0) {
		// not relevant
	}

	public void componentMoved(ComponentEvent arg0) {
		// not relevant
	}

	public void componentResized(ComponentEvent arg0) {
		Dimension dim = getSize();
		final int numberOfSamples = (int) dim.getWidth();

		// store the new sample size config
		VNAConfig.getSingleton().setNumberOfSamples(numberOfSamples);

		// clear the currently set calibration block
		datapool.clearResizedCalibrationBlock();

		// delete also the calibrated data
		datapool.clearCalibratedData();

		// check if all markers are still visible due to their absolute data
		// index
		VNAMarkerPanel markerPanel = mainFrame.getMarkerPanel();
		final VNAMarker[] markers = markerPanel.getMarkers();
		for (VNAMarker marker : markers) {
			if (marker.isVisible() && (marker.getDiagramX() > numberOfSamples)) {
				marker.setVisible(false);
				marker.clearFields();
			}
		}
		markerPanel.repaint();

		//
		mainFrame.getDiagramPanel().repaint();
	}

	public void componentShown(ComponentEvent arg0) {
		// not relevant
	}

	/**
	 * find the sample at the mouse position. Use the stored lastVNAData to map. If not set or the windows has been resized in the
	 * meantime, ignore this and return simply NULL
	 * 
	 * @param mouseX
	 *            where the mouse is
	 * 
	 * @return The found sample or null
	 */
	public VNACalibratedSample getSampleAtMousePosition(int mouseX) {
		VNACalibratedSample rc = null;
		if (datapool.getCalibratedData() != null) {
			try {

				rc = datapool.getCalibratedData().getCalibratedSamples()[mouseX];
				rc.setDiagramX(mouseX);
			} catch (IndexOutOfBoundsException e) {
				// not relevant
			}
		}
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		final String methodName = "mouseClicked";
		TraceHelper.entry(this, methodName);

		VNAMarker marker = mainFrame.getMarkerPanel().getMarkerForMouseEvent(e);

		// a valid button pressed?
		if (marker != null) {
			// update the marker with the sample at the click position
			marker.update(getSampleAtMousePosition(e.getX()));

			// redraw the window
			repaint();

			// marker is now visible
			VNAScaleSelectPanel ssp = mainFrame.getDiagramPanel().getScaleSelectPanel();
			// smith chart visible?
			if (ssp.getSmithDialog() != null) {
				// yes
				// redraw complete diagram
				ssp.getSmithDialog().consumeCalibratedData(datapool.getCalibratedData());
			}
		} else {
			if ((e.getButton() == MouseEvent.BUTTON3) && ((e.getModifiersEx() & (InputEvent.SHIFT_DOWN_MASK + InputEvent.CTRL_DOWN_MASK + InputEvent.ALT_DOWN_MASK)) == 0)) {
				TraceHelper.text(this, methodName, "right-button clicked");
				mainFrame.getMenuAndToolbarHandler().doExportJPGClipboard();
			}
		}
		TraceHelper.exit(this, methodName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent )
	 */
	public void mouseDragged(MouseEvent e) {
		// not relevant
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
		lastCursor = getCursor();
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
		lastMouseX = lastMouseY = -1;
		if (lastCursor != null) {
			setCursor(lastCursor);
		}
		mainFrame.getMarkerPanel().getMouseMarker().update(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	public void mouseMoved(MouseEvent e) {
		lastMouseX = e.getX();
		lastMouseY = e.getY();
		if (datapool.getCalibratedData() != null) {
			mainFrame.getMarkerPanel().getMouseMarker().update(getSampleAtMousePosition(e.getX()));
		}
	}

	public void mousePressed(MouseEvent e) {
		// not relevant
	}

	public void mouseReleased(MouseEvent e) {
		// not relevant
	}

	public void mouseWheelMoved(MouseWheelEvent e) {
		mainFrame.getMarkerPanel().consumeMouseWheelEvent(e);
	}

	@Override
	public void paint(Graphics g) {
		final String methodName = "paint";
		TraceHelper.entry(this, methodName);
		super.paint(g);
		//
		final int width = getWidth();
		final int height = getHeight();
		// clear panel
		g.setColor(config.getColorDiagram());
		g.fillRect(0, 0, width, height);

		final VNACalibratedSampleBlock calData = datapool.getCalibratedData();

		// is paintable data there?
		if (calData != null) {
			// yes
			//
			final Color colLeft = config.getColorScaleLeft();
			final Color colRight = config.getColorScaleRight();
			final Color colDiagramLine = config.getColorDiagramLines();

			final VNAMarker[] markers = mainFrame.getMarkerPanel().getMarkers();
			final VNADiagramPanel diagPanel = mainFrame.getDiagramPanel();

			final VNACalibratedSample[] samples = calData.getCalibratedSamples();
			final int numSamples = samples.length;

			// there are samples in the data ?
			if (numSamples > 1) {
				// yes
				TraceHelper.text(this, methodName, "#samples %d", numSamples);
				TraceHelper.text(this, methodName, "first    %d", samples[0].getFrequency());
				TraceHelper.text(this, methodName, "last     %d", samples[numSamples - 1].getFrequency());

				//
				VNAGenericScale scaleLeft = diagPanel.getScaleLeft().getScale();
				VNAGenericScale scaleRight = diagPanel.getScaleRight().getScale();

				VNACalibratedSample sample = samples[0];

				int lastY1 = scaleLeft.getScaledSampleValue(sample, height);
				int lastY2 = scaleRight.getScaledSampleValue(sample, height);
				int lastX = 0;

				drawBandMap(g, samples);

				// evaluate reference data
				final VNAReferenceDataBlock refBlock = datapool.getReferenceData();
				if (refBlock != null) {
					final long startFreq = samples[0].getFrequency();
					final long stopFreq = samples[numSamples - 1].getFrequency();
					refBlock.prepare(samples, startFreq, stopFreq);
					VNAReferenceDataHelper.paint(g, scaleLeft, scaleRight, numSamples, height, refBlock.getResizedSamples());
				}

				// is phosphor mode enabled?
				if (config.isPhosphor()) {
					// yes
					// check if historic samples are available?
					drawHistorySample(g, scaleLeft, scaleRight, height);

					// put the current scan at the first position in the history
					sampleHistory.add(0, samples);

					// too much samples
					if (sampleHistory.size() > SAMPLE_HISTORY_DEPTH) {
						// remove oldest from history
						sampleHistory.remove(SAMPLE_HISTORY_DEPTH);
					}
					setupPhosphorColors(colLeft, colRight);
				}

				Graphics2D g2 = (Graphics2D) g.create();

				// set the stroke of the copy, not the original
				final Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] {
						9
				}, 0);
				g2.setStroke(dashed);

				Double assVal = scaleLeft.getGuideLineValue();
				if (assVal != null) {
					final int rA1 = scaleLeft.getScaledSampleValue(assVal, height);
					g2.setColor(colLeft);
					g2.drawLine(0, rA1, width, rA1);
					final String valStr = scaleLeft.getFormattedValueAsStringWithUnit(assVal);
					g2.drawString(valStr, 5, rA1 - 2);
				}

				assVal = scaleRight.getGuideLineValue();
				if (assVal != null) {
					final int rA1 = scaleRight.getScaledSampleValue(assVal, height);
					g2.setColor(colRight);
					g2.drawLine(0, rA1, width, rA1);
					final String valStr = scaleRight.getFormattedValueAsStringWithUnit(assVal);
					g2.drawString(valStr, width - g2.getFontMetrics().stringWidth(valStr) - 5, rA1 - 2);
				}

				// now draw all the samples
				for (int x = 1; x < numSamples; ++x) {
					sample = samples[x];

					// remember where i painted it
					sample.setDiagramX(x);
					// scale down
					final int rY1 = scaleLeft.getScaledSampleValue(sample, height);
					final int rY2 = scaleRight.getScaledSampleValue(sample, height);
					// draw it
					g.setColor(colLeft);
					g.drawLine(lastX, lastY1, x, rY1);
					g.setColor(colRight);
					g.drawLine(lastX, lastY2, x, rY2);

					//
					lastX = x;
					lastY1 = rY1;
					lastY2 = rY2;

					//
					final int[] polyX = {
							x,
							x - 5,
							x + 5
					};

					// now check for every optional marker
					for (VNAMarker marker : markers) {
						// marker visible at current drawing position?
						if (marker.isVisible() && x == marker.getDiagramX()) {
							// yes
							marker.update(sample);

							// set color to marker color
							g.setColor(marker.getMarkerColor());

							// draw the markers as vertical lines?
							if (config.isMarkerModeLine()) {
								// yes
								g.drawLine(x, 1, x, height - 2);
								// marker at right border?
								if (x > width - 20) {
									// yes
									// draw text left of marker
									g.drawString(marker.getShortName(), x - 15, 20);
								} else {
									// no
									// draw text right of marker
									g.drawString(marker.getShortName(), x + 2, 20);
								}
							} else {
								// draw marker for left scale
								if (rY1 > 20) {
									final int[] polyY = {
											rY1,
											rY1 - 7,
											rY1 - 7
									};
									g.drawString(marker.getShortName(), x - 3, rY1 - 7);
									g.drawPolygon(polyX, polyY, 3);
								} else {
									final int[] polyY = {
											rY1,
											rY1 + 7,
											rY1 + 7
									};
									g.drawString(marker.getShortName(), x - 3, rY1 + 20);
									g.drawPolygon(polyX, polyY, 3);
								}

								// draw marker for right scale
								if (rY2 > 20) {
									//
									final int[] polyY = {
											rY2,
											rY2 - 7,
											rY2 - 7
									};
									g.drawString(marker.getShortName(), x - 3, rY2 - 7);
									g.drawPolygon(polyX, polyY, 3);
								} else {
									//
									final int[] polyY = {
											rY2,
											rY2 + 7,
											rY2 + 7
									};
									g.drawString(marker.getShortName(), x - 3, rY2 + 20);
									g.drawPolygon(polyX, polyY, 3);
								}
							}
						}
					}

					// is the draw position the mouse position of the mouse
					// marker?
					if (lastMouseX == x) {
						// yes
						// updates this markers data
						mainFrame.getMarkerPanel().getMouseMarker().update(sample);
					}

					// draw the horizontal dotted line
					final int[] ticks = scaleLeft.getTickCoordinates();

					// draw every 7th point a pixel
					if ((x & 7) == 1) {
						g.setColor(colDiagramLine);
						for (int t = 0; t < ticks.length; ++t) {
							g.drawLine(x, ticks[t], x, ticks[t]);
						}
					}
				}
				// markers 1 and 2 visible?
				if (markers[VNAMarkerPanel.MARKER_0].isVisible() && markers[VNAMarkerPanel.MARKER_1].isVisible()) {
					// yes
					// get samples of both markers
					final VNACalibratedSample s1 = mainFrame.getMarkerPanel().getMarker(VNAMarkerPanel.MARKER_0).getSample();
					final VNACalibratedSample s2 = mainFrame.getMarkerPanel().getMarker(VNAMarkerPanel.MARKER_1).getSample();

					// update the delta marker
					mainFrame.getMarkerPanel().getDeltaMarker().update(VNACalibratedSampleHelper.delta(s1, s2));
				} else {
					// no
					// clear the delta marker
					mainFrame.getMarkerPanel().getDeltaMarker().update(null);
				}
			}
		} else {
			// clear the sample history
			sampleHistory.clear();
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * Copy of the original JAVA Color operation with variable scaling factor
	 * 
	 * @param col
	 *            the color to darken
	 * @return the darkened color
	 */
	private static final Color getDarkerColor(Color col) {
		final int value = col.getRGB();
		final int r = (int) (((value & RED_MASK) >> 16) * BRIGHT_SCALE);
		final int g = (int) (((value & GREEN_MASK) >> 8) * BRIGHT_SCALE);
		final int b = (int) ((value & BLUE_MASK) * BRIGHT_SCALE);
		return new Color(r, g, b, 255);
	}

	/**
	 * Create the number of darker colors for the left and right scale first color is slightly darker then the scale color. Second
	 * color is darker then the first color and so on
	 * 
	 * @param colLeft
	 * @param colRight
	 */
	private void setupPhosphorColors(Color colLeft, Color colRight) {

		historyColorsLeft[0] = getDarkerColor(colLeft);
		historyColorsRight[0] = getDarkerColor(colRight);

		for (int i = 1; i < SAMPLE_HISTORY_DEPTH; ++i) {
			historyColorsLeft[i] = getDarkerColor(historyColorsLeft[i - 1]);
			historyColorsRight[i] = getDarkerColor(historyColorsRight[i - 1]);
		}
	}

	/**
	 * 
	 * @param g
	 * @param scaleLeft
	 * @param scaleRight
	 * @param height
	 */
	private void drawHistorySample(Graphics g, VNAGenericScale scaleLeft, VNAGenericScale scaleRight, int height) {
		final String methodName = "drawHistorySample";
		TraceHelper.entry(this, methodName);

		// now draw all historical samples
		final int historySize = sampleHistory.size();
		TraceHelper.text(this, methodName, "length=" + historySize);

		for (int historyIndex = historySize - 1; historyIndex >= 0; --historyIndex) {
			final VNACalibratedSample[] samples = sampleHistory.get(historyIndex);
			final int numSamples = samples.length;
			VNACalibratedSample sample = samples[0];

			int lastY1 = scaleLeft.getScaledSampleValue(sample, height);
			int lastY2 = scaleRight.getScaledSampleValue(sample, height);
			int lastX = 0;

			// now draw all the samples
			for (int x = 1; x < numSamples; ++x) {
				sample = samples[x];

				// remember where i painted it
				sample.setDiagramX(x);
				// scale down
				final int rY1 = scaleLeft.getScaledSampleValue(sample, height);
				final int rY2 = scaleRight.getScaledSampleValue(sample, height);

				// draw left scale
				g.setColor(historyColorsLeft[historyIndex]);
				g.drawLine(lastX, lastY1, x, rY1);

				// draw right
				g.setColor(historyColorsRight[historyIndex]);
				g.drawLine(lastX, lastY2, x, rY2);
				//
				lastX = x;
				lastY1 = rY1;
				lastY2 = rY2;
			}
		}
		TraceHelper.exit(this, methodName);
	}

	/**
	 * 
	 * @param g
	 * @param samples
	 */
	private void drawBandMap(Graphics g, final VNACalibratedSample[] samples) {
		// now draw the bandmap
		if (config.isShowBandmap()) {
			List<VNAFrequencyPair> map = bandMap.getList();
			final Color col = config.getColorBandmap();
			final int numSamples = samples.length;
			final int height = getHeight();

			for (int x = 1; x < numSamples; ++x) {
				final VNACalibratedSample sample = samples[x];
				final long frq = sample.getFrequency();

				for (final VNAFrequencyPair pair : map) {
					if (pair.isWithinPair(frq)) {
						g.setColor(col);
						g.drawLine(x, 1, x, height - 2);
						break;
					}
				}
			}
		}

	}

	@Override
	public void changeState(INNERSTATE oldState, INNERSTATE newState) {
		if (newState == INNERSTATE.DRIVERLOADED) {
			sampleHistory.clear();
		} else if (newState == INNERSTATE.CALIBRATED) {
			// not relevant
		} else if (newState == INNERSTATE.RUNNING) {
			// not relevant
		} else {
			// not relevant
		}
	}
}
