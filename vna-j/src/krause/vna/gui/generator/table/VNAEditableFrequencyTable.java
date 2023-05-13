package krause.vna.gui.generator.table;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;

import krause.util.ras.logging.TraceHelper;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;

public class VNAEditableFrequencyTable extends JPanel implements ActionListener, MouseListener {
	private VNAFrequencyTable tblFrequencies;
	private JButton buttonDelete;
	private JButton buttonAdd;
	private JButton buttonUse;
	protected EventListenerList listenerList = new EventListenerList();

	public VNAEditableFrequencyTable() {
		TraceHelper.exit(this, "VNAEditableFrequencyTable");
		createComponents();
		TraceHelper.exit(this, "VNAEditableFrequencyTable");

	}

	private void createComponents() {
		TraceHelper.entry(this, "createComponents");
		JScrollPane tablePane;
		JPanel panel1;
		//
		setLayout(new BorderLayout());
		//
		tblFrequencies = new VNAFrequencyTable();
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

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		TraceHelper.text(this, "actionPerformed", e.toString());
		int row = tblFrequencies.getSelectedRow();
		if (e.getSource() == buttonAdd) {
			fireAction("ADD", 0);
		} else if (e.getSource() == buttonDelete) {
			if (row != -1) {
				tblFrequencies.getModel().getData().remove(row);
				tblFrequencies.getModel().fireTableDataChanged();
				fireAction("DEL", 0);
				tblFrequencies.getSelectionModel().setSelectionInterval(-1,-1);
				buttonDelete.setEnabled(false);
				buttonUse.setEnabled(false);
			}
		} else if (e.getSource() == buttonUse) {
			if (row != -1) {
				fireAction("USE", tblFrequencies.getModel().getData().get(row));
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
		VNAFrequencyTable tbl = (VNAFrequencyTable) e.getSource();
		int row = tbl.getSelectedRow();
		//
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (e.getClickCount() == 1) {
				buttonDelete.setEnabled(row != -1);
				buttonUse.setEnabled(row != -1);
			} else if (e.getClickCount() > 1) {
				Long freq = tbl.getModel().getData().get(row);
				fireAction("FRQ", freq);
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
	protected void fireAction(String command, long value) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length - 2; i >= 0; i -= 2) {
			if (listeners[i] == ActionListener.class) {
				ActionEvent actionEvent = new ActionEvent(this, -1, command, value, 0);
				((ActionListener) listeners[i + 1]).actionPerformed(actionEvent);
			}
		}
	}

	public void addFrequency(Long pair) {
		tblFrequencies.addFrequency(pair);
	}

	public void load(String fn) {
		tblFrequencies.load(fn);
	}

	public void save(String fn) {
		tblFrequencies.save(fn);
	}
}
