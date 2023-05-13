package krause.vna.device.serial.pro.gui;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import krause.util.ras.logging.TraceHelper;
import krause.vna.device.VNADriverDialog;
import krause.vna.device.serial.pro.VNADriverSerialProMessages;
import krause.vna.gui.VNAMainFrame;

public class VNADriverSerialProDialog2 extends VNADriverDialog {

	public VNADriverSerialProDialog2(JFrame frame, VNAMainFrame pMainFrame) {
		super(frame, pMainFrame);
		TraceHelper.exit(this, "VNADriverSerialProDialog2");

		setBounds(new Rectangle(100, 100, 410, 400));
		setTitle(VNADriverSerialProMessages.getString("Dialog2.title"));
		setResizable(false);

		JPanel panel = new JPanel();
		getContentPane().add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0, 0));

		JPanel panelButtons = new JPanel();
		panel.add(panelButtons, BorderLayout.SOUTH);

		JButton buttonOK = new JButton(VNADriverSerialProMessages.getString("Button.OK"));
		buttonOK.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doDialogCancel();
			}
		});
		panelButtons.add(buttonOK);

		JLabel labelIMG = new JLabel("");
		panel.add(labelIMG, BorderLayout.CENTER);
		labelIMG.setIcon(new ImageIcon(VNADriverSerialProDialog2.class.getResource("/krause/vna/device/serial/pro/gui/ProLabel.jpg")));

		JTextArea txtrAsAStarting = new JTextArea();
		txtrAsAStarting.setEditable(false);
		txtrAsAStarting.setWrapStyleWord(true);
		panel.add(txtrAsAStarting, BorderLayout.NORTH);
		txtrAsAStarting.setLineWrap(true);
		txtrAsAStarting.setBackground(UIManager.getColor("Label.background"));
		txtrAsAStarting.setFont(UIManager.getFont("Label.font"));
		txtrAsAStarting.setText(VNADriverSerialProMessages.getString("Dialog2.info"));

		doDialogInit();
		TraceHelper.exit(this, "VNADriverSerialProDialog2");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		addEscapeKey();
		showCentered(getWidth(), getHeight());
		TraceHelper.exit(this, "doInit");
	}
}
