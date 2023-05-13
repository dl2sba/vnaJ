package krause.vna.gui.generator.digit;

import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

import krause.util.ras.logging.TraceHelper;

/**
 * 
 * @author de049434
 * 
 */
public class VNADigitTextFieldHandler implements MouseWheelListener, MouseListener {

	private long minValue;
	private long maxValue;
	private long value;
	private long oldValue;

	private HashMap<Long, VNADigitTextField> fieldMap = new HashMap<Long, VNADigitTextField>(11);
	protected EventListenerList listenerList = new EventListenerList();
	/**
	 * Only one <code>ChangeEvent</code> is needed per handler instance since
	 * the event's only state is the source property. The source of events
	 * generated is always "this".
	 */
	private transient ChangeEvent changeEvent;

	public VNADigitTextFieldHandler(long minVal, long maxVal) {
		super();
		this.minValue = minVal;
		this.maxValue = maxVal;
	}

	/**
	 * 
	 * @param fld
	 * @param counts
	 */
	private void changeFrequencyByField(VNADigitTextField fld, int counts) {
		long locFreq = getValue();

		locFreq += (fld.getFactor() * counts);

		if (locFreq > getMaxValue()) {
			// setFrequency(getMaxFreq());
			Toolkit.getDefaultToolkit().beep();
		} else if (locFreq < getMinValue()) {
			// setFrequency(getMinFreq());
			Toolkit.getDefaultToolkit().beep();
		} else {
			setOldValue(getValue());
			setValue(locFreq);
		}
		// notify all listeners
		fireStateChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
	 * MouseWheelEvent)
	 */
	public void mouseWheelMoved(MouseWheelEvent e) {
		// TraceHelper.entry(this, "mouseWheelMoved", "" + e.getScrollAmount());
		// TraceHelper.text(this, "mouseWheelMoved", "" + e.getWheelRotation());
		// TraceHelper.text(this, "mouseWheelMoved", "" + e.getUnitsToScroll());
		VNADigitTextField fld = (VNADigitTextField) e.getSource();
		int amt = e.getScrollAmount();
		if (e.getUnitsToScroll() != 0) {
			amt /= e.getUnitsToScroll();
		}
		changeFrequencyByField(fld, amt);
		// TraceHelper.exit(this, "mouseWheelMoved");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		TraceHelper.entry(this, "mouseClicked", "" + e.getButton());
		TraceHelper.text(this, "mouseClicked", "" + e.getClickCount());
		VNADigitTextField fld = (VNADigitTextField) e.getSource();
		if (e.getClickCount() > 0) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				changeFrequencyByField(fld, 1);
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				changeFrequencyByField(fld, -1);
			}
		}
		TraceHelper.exit(this, "mouseClicked");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent e) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent e) {
	}

	/**
	 * 
	 * @param textField
	 */
	public VNADigitTextField registerField(VNADigitTextField textField) {
		fieldMap.put(Long.valueOf(textField.getFactor()), textField);
		textField.addMouseListener(this);
		textField.addMouseWheelListener(this);
		return textField;
	}

	/**
	 * @return the minFreq
	 */
	public long getMinValue() {
		return minValue;
	}

	/**
	 * @param minFreq
	 *            the minFreq to set
	 */
	public void setMinValue(long minFreq) {
		this.minValue = minFreq;
	}

	/**
	 * @return the maxFreq
	 */
	public long getMaxValue() {
		return maxValue;
	}

	/**
	 * @param maxFreq
	 *            the maxFreq to set
	 */
	public void setMaxValue(long maxFreq) {
		this.maxValue = maxFreq;
	}

	/**
	 * @param val
	 *            the frequency to set
	 */
	public void setValue(long val) {
//		TraceHelper.entry(this, "setValue", "val = " + val);
		if (val < minValue) {
			oldValue = value;
			value = minValue;
			Toolkit.getDefaultToolkit().beep();
		} else if (val > maxValue) {
			oldValue = value;
			value = maxValue;
			Toolkit.getDefaultToolkit().beep();
		} else {
			oldValue = value;
			this.value = val;
		}
		updateFields();
//		TraceHelper.exit(this, "setValue");
	}

	/***
	 * Write the frequency value to the various registered fields
	 */
	private void updateFields() {
//		TraceHelper.entry(this, "updateFields");

		int decades = Double.valueOf(Math.log10(getMaxValue() * 1.0)).intValue() + 1;
		long j = 1;
		long frq = getValue();
		for (int i = 0; i < decades; ++i) {
			long currDigit = (frq / j) % 10;
			// TraceHelper.text(this, "updateFields", "" + currDigit);
			VNADigitTextField currField = fieldMap.get(Long.valueOf(j));
			currField.setValue(currDigit);

			j *= 10;
		}
//		TraceHelper.exit(this, "updateFields");
	}

	/**
	 * @return the frequency
	 */
	public long getValue() {
		return value;
	}

	/**
	 * Adds a <code>ChangeListener</code> to the button.
	 * 
	 * @param l
	 *            the listener to be added
	 */
	public void addChangeListener(ChangeListener l) {
		listenerList.add(ChangeListener.class, l);
	}

	/**
	 * Removes a ChangeListener from the button.
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void removeChangeListener(ChangeListener l) {
		listenerList.remove(ChangeListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created.
	 * 
	 * @see EventListenerList
	 */
	protected void fireStateChanged() {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ChangeListener.class) {
				// Lazily create the event:
				if (changeEvent == null)
					changeEvent = new ChangeEvent(this);
				((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
			}
		}
	}

	public void setOldValue(long oldValue) {
		this.oldValue = oldValue;
	}

	public long getOldValue() {
		return oldValue;
	}
}
