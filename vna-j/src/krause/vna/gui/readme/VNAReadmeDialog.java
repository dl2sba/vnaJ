/**
 * Copyright © 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.readme;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.vna.gui.VNAMainFrame;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAReadmeDialog extends KrauseDialog implements ActionListener {
	public VNAReadmeDialog(VNAMainFrame f) {
		super(f.getJFrame(), true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JEditorPane htmlPane;
		JScrollPane scrollPane;
		StringBuffer t = new StringBuffer(32000);
		try {
			htmlPane = new JTextPane();
			htmlPane.setEditorKit(new HTMLEditorKit());
			//
			InputStream fr = getClass().getResourceAsStream("/krause/vna/resources/readme.html");
			// Convert our input stream to a
			// DataInputStream
			BufferedReader d = new BufferedReader(new InputStreamReader(fr));

			String line;
			while ((line = d.readLine()) != null) {
				t.append(line);
			}
			d.close();
			fr.close();
			//
			t.append("<h1>System properties</h1>");
			t.append("<div style=\"margin-left: 30px;\"><table>");
			Properties props = System.getProperties();
			Set<Object> keys = props.keySet();
			for (Object key : keys) {
				t.append("<tr><td>");
				t.append(key);
				t.append("=");
				t.append(props.getProperty((String) key));
				t.append("</td></tr>");
			}
			t.append("</table></div>");
			t.append("</body></html>");
			htmlPane.setText(t.toString());

			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			//
			JButton button = new JButton(VNAMessages.getString("Button.Close"));
			panel.add(button);
			button.addActionListener(this);
			getRootPane().setDefaultButton(button);
			htmlPane.setEditable(false);
			scrollPane = new JScrollPane(htmlPane);
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			setTitle(VNAMessages.getString("Dlg.Readme.1"));
			//
			htmlPane.setSelectionStart(0);
			htmlPane.setSelectionEnd(0);
			//
			doDialogInit();
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "VNAReadmeDialog", e);
			setVisible(false);
			dispose();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		doDialogCancel();
	}

	@Override
	protected void doDialogCancel() {
		setVisible(false);
		dispose();
	}

	@Override
	protected void doDialogInit() {
		addEscapeKey();
		showCentered(800, 480);
	}
}