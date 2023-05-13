package krause.vna.gui.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.gui.generator.table.VNAEditableFrequencyTable;
import krause.vna.resources.VNAMessages;

public class VNAGeneratorDialog extends KrauseDialog implements ChangeListener, ActionListener, ClipboardOwner {
	public final static String GENERATOR_LIST_FILENAME = "vna.generator.xml";

	private VNAConfig cfg = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private JButton btOK;
	private JPanel contentPanel;
	private VNADigitTextFieldHandler mouseHandler = null;
	private JPanel panel_1;
	private JLabel lblOnAir;
	private boolean onAir = false;
	private IVNADriver driver = null;
	private VNAEditableFrequencyTable tblFrequencies = null;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNAGeneratorDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), true);

		driver = pDriver;

		setResizable(false);
		setTitle(VNAMessages.getString("VNAGeneratorDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		// content pane

		// handler for digits
		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
		mouseHandler = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());
		mouseHandler.addChangeListener(this);
		getContentPane().setLayout(new BorderLayout(0, 0));

		// content panel
		contentPanel = new JPanel();
		contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		FlowLayout fl_contentPanel = new FlowLayout(FlowLayout.CENTER, 5, 5);
		contentPanel.setLayout(fl_contentPanel);

		panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		contentPanel.add(panel_1);
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		VNADigitTextField textField_9 = new VNADigitTextField(1000000000, 0);
		mouseHandler.registerField(textField_9);
		panel_1.add(textField_9);

		JLabel label_1 = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
		panel_1.add(label_1);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 54));

		VNADigitTextField textField = new VNADigitTextField(100000000, 0);
		mouseHandler.registerField(textField);
		panel_1.add(textField);

		VNADigitTextField textField_7 = new VNADigitTextField(10000000, 0);
		mouseHandler.registerField(textField_7);
		panel_1.add(textField_7);

		VNADigitTextField textField_6 = new VNADigitTextField(1000000, 0);
		mouseHandler.registerField(textField_6);
		panel_1.add(textField_6);

		label_1 = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
		panel_1.add(label_1);
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 54));

		VNADigitTextField textField_5 = new VNADigitTextField(100000, 0);
		mouseHandler.registerField(textField_5);
		panel_1.add(textField_5);

		VNADigitTextField textField_3 = new VNADigitTextField(10000, 0);
		mouseHandler.registerField(textField_3);
		panel_1.add(textField_3);

		VNADigitTextField textField_4 = new VNADigitTextField(1000, 0);
		mouseHandler.registerField(textField_4);
		panel_1.add(textField_4);

		JLabel label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
		panel_1.add(label);
		label.setFont(new Font("Tahoma", Font.PLAIN, 54));

		VNADigitTextField textField_2 = new VNADigitTextField(100, 0);
		mouseHandler.registerField(textField_2);
		panel_1.add(textField_2);

		VNADigitTextField textField_1 = new VNADigitTextField(10, 0);
		mouseHandler.registerField(textField_1);
		panel_1.add(textField_1);

		VNADigitTextField textField_8 = new VNADigitTextField(1, 0);
		mouseHandler.registerField(textField_8);
		panel_1.add(textField_8);

		JLabel lblHz = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblHz.text"));
		lblHz.setFont(new Font("Tahoma", Font.PLAIN, 54));
		panel_1.add(lblHz);

		//
		tblFrequencies = new VNAEditableFrequencyTable();
		contentPanel.add(tblFrequencies);
		tblFrequencies.addActionListener(this);

		// button panel
		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		lblOnAir = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.text")); //$NON-NLS-1$
		lblOnAir.setToolTipText(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.toolTipText")); //$NON-NLS-1$
		lblOnAir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblOnAir.setOpaque(true);

		lblOnAir.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doMouseClickedOnOnAirField(e);
			}
		});
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		lblOnAir.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblOnAir.setFont(new Font("Courier New", Font.PLAIN, 17));
		lblOnAir.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		buttonPane.add(lblOnAir);

		JLabel lblTuneTheFrequency = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblTuneTheFrequency.text")); //$NON-NLS-1$
		buttonPane.add(lblTuneTheFrequency);

		btOK = new JButton(VNAMessages.getString("Button.Close"));
		btOK.addActionListener(this);
		buttonPane.add(btOK);

		//
		doDialogInit();
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		//
		cfg.setGeneratorFrequency(mouseHandler.getValue());
		//
		tblFrequencies.save(cfg.getVNAConfigDirectory() + "/" + GENERATOR_LIST_FILENAME);

		stopGenerator();

		// and hide myself
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		// now do the rest
		mouseHandler.setValue(cfg.getGeneratorFrequency());
		//
		tblFrequencies.load(cfg.getVNAConfigDirectory() + "/" + GENERATOR_LIST_FILENAME);
		updateOnAirField();

		// add escape key to window
		addEscapeKey();
		showCentered(getOwner());
		TraceHelper.exit(this, "doInit");
	}

	private void updateOnAirField() {
		if (onAir) {
			lblOnAir.setForeground(Color.BLACK);
			lblOnAir.setBackground(Color.RED);
		} else {
			lblOnAir.setForeground(Color.RED);
			lblOnAir.setBackground(Color.BLACK);
		}
	}

	public void stateChanged(ChangeEvent e) {
		// TraceHelper.entry(this, "stateChanged", "" + e);
		if (e.getSource() == mouseHandler) {
			if (onAir) {
				try {
					driver.startGenerator(mouseHandler.getValue(), 0, 0, 0, 0, 0);
				} catch (ProcessingException ex) {
					ErrorLogHelper.exception(this, "stateChanged", ex);
				}
			}
		}
		// TraceHelper.exit(this, "stateChanged");
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		TraceHelper.text(this, "actionPerformed", e.toString());
		if (e.getSource() == btOK) {
			doDialogCancel();
		} else if (e.getSource() == tblFrequencies) {
			if ("ADD".equals(e.getActionCommand())) {
				tblFrequencies.addFrequency(Long.valueOf(mouseHandler.getValue()));
			} else if ("DEL".equals(e.getActionCommand())) {
			} else if (("USE".equals(e.getActionCommand())) || ("FRQ".equals(e.getActionCommand()))) {
				long freq = e.getWhen();
				mouseHandler.setValue(freq);
				if (onAir) {
					startGenerator(freq);
				}
			}
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		TraceHelper.entry(this, "lostOwnership");
		TraceHelper.exit(this, "lostOwnership");
	}

	private void doMouseClickedOnOnAirField(MouseEvent e) {
		TraceHelper.entry(this, "doMouseClickedOnOnAirField");
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (onAir) {
				stopGenerator();
				onAir = false;
			} else {
				startGenerator(mouseHandler.getValue());
				onAir = true;
			}
			updateOnAirField();
		}
		TraceHelper.exit(this, "doMouseClickedOnOnAirField");

	}

	private void startGenerator(long frequency) {
		TraceHelper.entry(this, "startGenerator");
		btOK.setEnabled(false);
		try {
			driver.startGenerator(frequency, 0, 0, 0, 0, 0);
		} catch (ProcessingException ex) {
			ErrorLogHelper.exception(this, "startGenerator", ex);
		}
		btOK.setEnabled(true);
		TraceHelper.exit(this, "startGenerator");
	}

	private void stopGenerator() {
		TraceHelper.entry(this, "stopGenerator");
		btOK.setEnabled(false);
		try {
			driver.stopGenerator();
		} catch (ProcessingException ex) {
			ErrorLogHelper.exception(this, "startGenerator", ex);
		}
		btOK.setEnabled(true);
		TraceHelper.exit(this, "stopGenerator");
	}
}
