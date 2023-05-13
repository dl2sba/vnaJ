package krause.vna.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import krause.util.ras.logging.TraceHelper;

public class SimpleProgressPopup extends JDialog implements PropertyChangeListener {
	private SwingWorker<Void, Void> task = null;
	private JProgressBar progressBar;

	/**
	 * 
	 */
	public SimpleProgressPopup(JFrame owner, String title) {
		super(owner, ModalityType.APPLICATION_MODAL);
		TraceHelper.entry(this, "SimpleProgressPopup");
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		setUndecorated(true);
		add(createContentPane(title));
		centerOnComponent(owner);
		pack();
		TraceHelper.exit(this, "SimpleProgressPopup");
	}

	protected void centerOnComponent(Component root) {
		Dimension dimRoot = root.getSize();
		Point locRoot = root.getLocation();
		int x = ((int) locRoot.getX() + (dimRoot.width / 2) - (getSize().width / 2));
		int y = ((int) locRoot.getY() + (dimRoot.height / 2) - (getSize().height / 2));

		if (x < 0) {
			x = 0;
		}

		if (y < 0) {
			y = 0;
		}
		// center on component
		setLocation(x, y);
	}

	/**
	 * 
	 * @param title
	 * @return
	 */
	public JPanel createContentPane(String title) {
		JPanel rc = new JPanel(new BorderLayout());
		rc.setOpaque(true);

		JLabel lbl = new JLabel(title);
		rc.add(lbl, BorderLayout.PAGE_START);

		progressBar = new JProgressBar(0, 100);
		progressBar.setValue(0);
		progressBar.setStringPainted(true);

		rc.add(progressBar, BorderLayout.PAGE_END);

		rc.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		return rc;
	}

	public SwingWorker<Void, Void> getTask() {
		return task;
	}

	/**
	 * Invoked when task's progress property changes.
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if ("progress".equals(evt.getPropertyName())) {
			int progress = (Integer) evt.getNewValue();
			progressBar.setIndeterminate(false);
			progressBar.setValue(progress);
		}
	}

	public void setTask(SwingWorker<Void, Void> task) {
		this.task = task;
	}

	public void run() {
		TraceHelper.entry(this, "run");
		if (task != null) {
			task.addPropertyChangeListener(this);
			task.execute();
		}
		setVisible(true);
		TraceHelper.exit(this, "run");
	}
}