/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.panels.data.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.gui.util.VNAFrequencyPair;
import krause.vna.resources.VNAMessages;

public class VNAEditableFrequencyPairTable extends JPanel implements ActionListener, MouseListener {
	private VNAFrequencyPairTable tblFrequencies;
	private JButton buttonDelete;
	private JButton buttonAdd;
	private JButton buttonUse;
	private EventListenerList listenerList = new EventListenerList();

	/**
	 * @param listModel
	 */
	public VNAEditableFrequencyPairTable() {
		TraceHelper.entry(this, "VNAEditableFrequencyPairTable");
		createComponents();
		TraceHelper.exit(this, "VNAEditableFrequencyPairTable");
	}

	private void createComponents() {
		TraceHelper.entry(this, "createComponents");
		JScrollPane tablePane;
		JPanel panel1;
		//
		setLayout(new BorderLayout());
		//
		tblFrequencies = new VNAFrequencyPairTable();
		tblFrequencies.addMouseListener(this);
		tblFrequencies.setToolTipText(VNAMessages.getString("Panel.Data.FrequencyList.Tooltip"));
		//
		tablePane = new JScrollPane(tblFrequencies);
		tablePane.setPreferredSize(new Dimension(150, 100));
		tablePane.setMinimumSize(tablePane.getPreferredSize());
		tablePane.setAlignmentX(LEFT_ALIGNMENT);
		//
		add(tablePane, BorderLayout.CENTER);
		//
		panel1 = new JPanel(new FlowLayout());
		buttonAdd = SwingUtil.createToolbarButton("Button.Icon.Add", this);
		buttonDelete = SwingUtil.createToolbarButton("Button.Icon.Delete", this);
		buttonUse = SwingUtil.createToolbarButton("Button.Icon.Use", this);
		panel1.add(buttonAdd);
		panel1.add(buttonDelete);
		panel1.add(buttonUse);
		add(panel1, BorderLayout.SOUTH);
		//
		buttonUse.setEnabled(false);
		buttonDelete.setEnabled(false);
		//
		TraceHelper.exit(this, "createComponents");
	}

	/**
	 * Adds a <code>ActionListener</code> to the list.
	 * 
	 * @param l
	 *            the listener to be added
	 */
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * Removes a listener from the list
	 * 
	 * @param l
	 *            the listener to be removed
	 */
	public void removeActionListener(ActionListener l) {
		listenerList.remove(ActionListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for notification on
	 * this event type. The event instance is lazily created.
	 * 
	 * @see EventListenerList
	 */
	protected void fireAction(String command, VNAFrequencyPair fp) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		if (fp != null) {
			command += ";";
			command += fp.getStartFrequency() + ";";
			command += fp.getStopFrequency() + ";";
		}
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				ActionEvent actionEvent = new ActionEvent(this, 123, command, 0);
				((ActionListener) listeners[i + 1]).actionPerformed(actionEvent);
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		int row = tblFrequencies.getSelectedRow();
		if (e.getSource() == buttonAdd) {
			fireAction("ADD", null);
		} else if (e.getSource() == buttonDelete) {
			if (row != -1) {
				VNAFrequencyPair fp = tblFrequencies.getModel().getData().get(row);

				tblFrequencies.getModel().getData().remove(row);
				tblFrequencies.getModel().fireTableDataChanged();
				fireAction("DEL", fp);
				tblFrequencies.getSelectionModel().setSelectionInterval(-1, -1);
				buttonDelete.setEnabled(false);
				buttonUse.setEnabled(false);
			}
		} else if (e.getSource() == buttonUse) {
			if (row != -1) {
				VNAFrequencyPair fp = tblFrequencies.getModel().getData().get(row);
				fireAction("USE", fp);
			}
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		TraceHelper.entry(this, "mouseClicked");
		VNAFrequencyPairTable tbl = (VNAFrequencyPairTable) e.getSource();
		if (tbl.isEnabled()) {
			int row = tbl.getSelectedRow();
			//
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getClickCount() == 1) {
					buttonDelete.setEnabled(row != -1);
					buttonUse.setEnabled(row != -1);
				} else if (e.getClickCount() > 1) {
					VNAFrequencyPair fp = tbl.getModel().getData().get(row);
					fireAction("USE", fp);
				}
			}
		}
		TraceHelper.exit(this, "mouseClicked");
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void addFrequency(VNAFrequencyPair pair) {
		tblFrequencies.addFrequencyPair(pair);
	}

	public void save(String string) {
		TraceHelper.entry(this, "save");
		tblFrequencies.save(string);
		TraceHelper.exit(this, "save");
	}

	public void load(String string) {
		TraceHelper.entry(this, "load");
		tblFrequencies.load(string);
		TraceHelper.exit(this, "load");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setEnabled(boolean)
	 */
	@Override
	public void setEnabled(boolean enabled) {
		// TraceHelper.entry(this, "setEnabled", ""+enabled);
		super.setEnabled(enabled);
		tblFrequencies.setEnabled(enabled);
		buttonAdd.setEnabled(enabled);
		buttonDelete.setEnabled(enabled);
		buttonUse.setEnabled(enabled);
		//
		// TraceHelper.exit(this, "setEnabled");
	}

	public JButton getButtonUse() {
		return buttonUse;
	}

	public List<VNAFrequencyPair> getFrequencyPairs() {
		return tblFrequencies.getModel().getData();
	}

}
