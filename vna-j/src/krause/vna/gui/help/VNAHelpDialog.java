/**
 * Copyright © 2010 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.help;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.resources.VNAMessages;

/**
 * @author Dietmar Krause
 * 
 */
public class VNAHelpDialog extends JDialog implements HyperlinkListener {
	public final static String HELP_HOME = "krause/vna/resources/help/";
	public final static String HELP_IMAGES = "krause/vna/resources/help/images";

	private JEditorPane htmlPane;

	public VNAHelpDialog(Dialog owner, String helpID) {
		super(owner);
		TraceHelper.entry(this, "VNAHelpDialog", helpID);
		internal(helpID);
		TraceHelper.exit(this, "VNAHelpDialog");
	}

	/**
	 * 
	 * @param f
	 */
	public VNAHelpDialog(Frame owner, String helpID) {
		super(owner);
		TraceHelper.entry(this, "VNAHelpDialog", helpID);
		internal(helpID);
		TraceHelper.exit(this, "VNAHelpDialog");
	}

	/**
	 * 
	 */
	private void addEscapeKey() {
		// add escape key to window
		Action actionListener = new AbstractAction() {
			public void actionPerformed(ActionEvent actionEvent) {
				dispose();
			}
		};
		KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(stroke, "ESCAPE");
		rootPane.getActionMap().put("ESCAPE", actionListener);
	}

	/**
	 * 
	 * @param helpID
	 * @return
	 */
	private String buildDefaultResourceName(String helpID) {
		String rc = HELP_HOME;
		TraceHelper.entry(this, "buildDefaultResourceName", helpID);
		String language = Locale.ENGLISH.getLanguage();
		//
		rc += language;
		rc += "/";
		rc += helpID;
		rc += ".html";
		TraceHelper.exitWithRC(this, "buildDefaultResourceName", rc);
		return rc;
	}

	/**
	 * 
	 * @param helpID
	 * @return
	 */
	private String buildResourceName(String helpID) {
		String rc = HELP_HOME;
		TraceHelper.entry(this, "buildResourceName", helpID);
		String language = Locale.getDefault().getLanguage();
		//
		rc += language;
		rc += "/";
		rc += helpID;
		rc += ".html";
		TraceHelper.exitWithRC(this, "buildResourceName", rc);
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Window#dispose()
	 */
	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		VNAConfig.getSingleton().storeWindowPosition("VNAHelpDialog", this);
		VNAConfig.getSingleton().storeWindowSize("VNAHelpDialog", this);
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	/**
	 * 
	 * @param helpID
	 */
	private void internal(String helpID) {
		TraceHelper.entry(this, "internal", helpID);

		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JScrollPane scrollPane;

		try {
			String resourceName = buildResourceName(helpID);
			TraceHelper.text(this, "internal", "resourcename=[" + resourceName + "]");
			URL url = ClassLoader.getSystemResource(resourceName);
			if (url != null) {
				TraceHelper.text(this, "internal", "URL build=[" + url.toString() + "]");
				InputStream s = ClassLoader.getSystemResourceAsStream(resourceName);
				if (s == null) {
					TraceHelper.text(this, "internal", "language resource not found");
					resourceName = buildDefaultResourceName(helpID);
					url = ClassLoader.getSystemResource(resourceName);
				} else {
					s.close();
				}
			} else {
				resourceName = buildDefaultResourceName(helpID);
				url = ClassLoader.getSystemResource(resourceName);
			}

			TraceHelper.text(this, "internal", "try to load from [" + url + "]");
			htmlPane = new JEditorPane(url);
			htmlPane.setContentType("text/html;charset=iso8859-1");
			htmlPane.putClientProperty(BasicHTML.documentBaseKey, ClassLoader.getSystemResource("/"));
			htmlPane.addHyperlinkListener(this);

			HTMLEditorKit kit = new HTMLEditorKit();
			StyleSheet styleSheet = kit.getStyleSheet();
			styleSheet.addRule("h1 {margin-bottom: 0px; margin-top: 5px;}");
			styleSheet.addRule("h2 {margin-bottom: 0px; margin-top: 5px;}");
			styleSheet.addRule("h3 {margin-bottom: 0px; margin-top: 5px; }");
			styleSheet.addRule("p  {margin-top: 5px; margin-left: 10px; }");
			styleSheet.addRule("ol {font-weight: bold; font-size:larger; }");
			styleSheet.addRule("ol p {font-weight: normal; font-size:smaller; }");

			JPanel panel = new JPanel();
			getContentPane().add(panel, BorderLayout.SOUTH);
			htmlPane.setEditable(false);
			scrollPane = new JScrollPane(htmlPane);
			getContentPane().add(scrollPane, BorderLayout.CENTER);
			setTitle(VNAMessages.getString("Dlg.Help.title"));
			//
			htmlPane.setSelectionStart(0);
			htmlPane.setSelectionEnd(0);
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "internal", e);
		}

		//
		addEscapeKey();

		//
		VNAConfig.getSingleton().restoreWindowPosition("VNAHelpDialog", this, new Point(100, 100));
		pack();
		VNAConfig.getSingleton().restoreWindowSize("VNAHelpDialog", this, new Dimension(400, 400));
		setVisible(true);
		TraceHelper.exit(this, "internal");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.swing.event.HyperlinkListener#hyperlinkUpdate(javax.swing.event
	 * .HyperlinkEvent)
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {
		TraceHelper.entry(this, "hyperlinkUpdate");
		if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			try {
				htmlPane.setPage(event.getURL());
			} catch (IOException e) {
				ErrorLogHelper.exception(this, "hyperlinkUpdate", e);
			}
		}
		TraceHelper.exit(this, "hyperlinkUpdate");
	}
}