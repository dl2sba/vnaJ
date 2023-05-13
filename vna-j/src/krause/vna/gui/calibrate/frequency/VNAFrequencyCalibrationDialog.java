package krause.vna.gui.calibrate.frequency;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.DecimalFormatSymbols;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import krause.common.exception.ProcessingException;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.gui.input.FrequencyInputField;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAFrequencyCalibrationDialog extends KrauseDialog implements ActionListener, ChangeListener, FocusListener {
	private JButton btCancel;
	private JPanel contentPanel;
	private VNADigitTextFieldHandler handlerTicks = null;
	private FrequencyInputField frequencyField;
	private VNADeviceInfoBlock dib;
	private IVNADriver driver = null;
	private long orgTicks;

	private JButton btOK;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNAFrequencyCalibrationDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), true);

		setConfigurationPrefix("VNAFrequencyCalibrationDialog");
		setProperties(VNAConfig.getSingleton());

		String comma = new String("" + DecimalFormatSymbols.getInstance().getGroupingSeparator());

		JLabel label;

		driver = pDriver;
		dib = driver.getDeviceInfoBlock();
		orgTicks = dib.getDdsTicksPerMHz();

		setTitle(VNAMessages.getString("VNAFrequencyCalibrationDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(320, 340));
		addWindowListener(this);

		// content pane

		// handler for digits
		getContentPane().setLayout(new BorderLayout());

		// content panel
		contentPanel = new JPanel();
		contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("", "fill", "[][fill]"));

		JTextArea ta = new JTextArea(VNAMessages.getString("VNAFrequencyCalibrationDialog.helptext"));
		ta.setForeground(new Color(0, 0, 255));
		ta.setWrapStyleWord(true);
		ta.setLineWrap(true);
		ta.setBackground(contentPanel.getBackground());
		ta.setEditable(false);
		contentPanel.add(ta, "wrap");

		JPanel panelFRQ = new JPanel();
		panelFRQ.setBorder(new TitledBorder(null, VNAMessages.getString("VNAFrequencyCalibrationDialog.Frequency"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panelFRQ, "wrap");
		panelFRQ.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panelFRQ.add(frequencyField = new FrequencyInputField("", 1, dib.getMinFrequency(), dib.getMaxFrequency()));
		panelFRQ.add(new JLabel("Hz"));
		frequencyField.addFocusListener(this);

		JPanel panelTicks = new JPanel();
		panelTicks.setBorder(new TitledBorder(null, VNAMessages.getString("VNAFrequencyCalibrationDialog.Ticks"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		contentPanel.add(panelTicks, "wrap");

		panelTicks.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		handlerTicks = new VNADigitTextFieldHandler(0, 99999999);
		handlerTicks.addChangeListener(this);
		label = new JLabel(comma);
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(10000000, 0, 40)));
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(1000000, 0, 40)));
		label.setFont(new Font("Tahoma", Font.PLAIN, 40));
		panelTicks.add(label);
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(100000, 0, 40)));
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(10000, 0, 40)));
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(1000, 0, 40)));
		label = new JLabel(comma);
		label.setFont(new Font("Tahoma", Font.PLAIN, 40));
		panelTicks.add(label);
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(100, 0, 40)));
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(10, 0, 40)));
		panelTicks.add(handlerTicks.registerField(new VNADigitTextField(1, 0, 40)));

		// button panel
		JPanel buttonPane = new JPanel();
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		buttonPane.add(new HelpButton(this, "VNAFrequencyCalibrationDialog"));

		btCancel = new JButton(VNAMessages.getString("Button.Cancel"));
		btCancel.addActionListener(this);
		buttonPane.add(btCancel);

		btOK = new JButton(VNAMessages.getString("Button.Save"));
		btOK.addActionListener(this);
		buttonPane.add(btOK);

		//
		doDialogInit();
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		if (e.getSource() == btCancel) {
			doDialogCancel();
		} else if (e.getSource() == btOK) {
			doSAVE();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	/**
	 * 
	 */
	private void doSAVE() {
		TraceHelper.entry(this, "doSAVE");

		// use the ticks selected in fields
		dib.setDdsTicksPerMHz(handlerTicks.getValue());
		dib.store(getProperties(), driver.getDriverConfigPrefix());

		//
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doSAVE");
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");

		// turn output off
		stopGenerator();

		// store the values
		dib.store(getProperties(), driver.getDriverConfigPrefix());

		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");

		// restore the value from dialog open
		dib.setDdsTicksPerMHz(orgTicks);

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
		handlerTicks.setValue(dib.getDdsTicksPerMHz());
		//
		long frq = ((dib.getMaxFrequency() / 2) / 1000000) * 1000000;
		frequencyField.setFrequency(frq);

		// turn output on
		startGenerator();

		// add escape key to window
		addEscapeKey();

		//
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.datatransfer.ClipboardOwner#lostOwnership(java.awt.datatransfer
	 * .Clipboard, java.awt.datatransfer.Transferable)
	 */
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		TraceHelper.entry(this, "lostOwnership");
		TraceHelper.exit(this, "lostOwnership");
	}

	private void startGenerator() {
		TraceHelper.entry(this, "startGenerator");

		dib.setDdsTicksPerMHz(handlerTicks.getValue());

		long frequencyI = frequencyField.getFrequency();
		long frequencyQ = 0;
		int attentuationI = 0;
		int attenuationQ = 0;
		int phase = 0;
		int mainAttenuation = 0;

		try {
			driver.startGenerator(frequencyI, frequencyQ, attentuationI, attenuationQ, phase, mainAttenuation);
		} catch (ProcessingException e) {
			ErrorLogHelper.exception(this, "startGenerator", e);
		}
		TraceHelper.exit(this, "startGenerator");
	}

	private void stopGenerator() {
		TraceHelper.entry(this, "stopGenerator");
		btCancel.setEnabled(false);
		try {
			driver.stopGenerator();
		} catch (ProcessingException ex) {
			ErrorLogHelper.exception(this, "stopGenerator", ex);
		}
		btCancel.setEnabled(true);
		TraceHelper.exit(this, "stopGenerator");
	}

	/**
	 * 
	 */
	public void stateChanged(ChangeEvent arg0) {
		TraceHelper.entry(this, "stateChanged");
		startGenerator();
		TraceHelper.exit(this, "stateChanged");
	}

	@Override
	public void focusGained(FocusEvent e) {
		TraceHelper.entry(this, "focusGained");
		startGenerator();
		TraceHelper.exit(this, "focusGained");
	}

	@Override
	public void focusLost(FocusEvent e) {
		TraceHelper.entry(this, "focusLost");
		startGenerator();
		TraceHelper.exit(this, "focusLost");
	}
}
