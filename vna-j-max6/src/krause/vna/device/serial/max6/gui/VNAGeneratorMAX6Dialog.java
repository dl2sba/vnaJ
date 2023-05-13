package krause.vna.device.serial.max6.gui;

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
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
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
import krause.vna.device.serial.max6.VNADriverSerialMax6DIB;
import krause.vna.device.serial.max6.VNADriverSerialMax6Messages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.gui.generator.table.VNAEditableFrequencyTable;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAGeneratorMAX6Dialog extends KrauseDialog implements ChangeListener, ActionListener, ClipboardOwner, AdjustmentListener {
	public final static String GENERATOR_LIST_FILENAME = "vna.generator.xml";

	private VNAConfig cfg = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private JButton btOK;
	private JPanel contentPanel;
	private VNADigitTextFieldHandler mouseHandler = null;
	private JLabel lblOnAir;
	private boolean onAir = false;
	private IVNADriver driver = null;
	private VNAEditableFrequencyTable tblFrequencies = null;

	private JScrollBar sbLevel;
	private int outputLevel = VNADriverSerialMax6DIB.MAX_LEVEL;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNAGeneratorMAX6Dialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), true);

		driver = pDriver;

		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix("VNAGeneratorMAX6Dialog");
		setTitle(VNADriverSerialMax6Messages.getString("VNAGeneratorMAX6Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		// content pane
		getContentPane().setLayout(new BorderLayout(0, 0));
		contentPanel = new JPanel(new MigLayout("", "[][][][]", "[][]"));
		contentPanel.setBorder(new MatteBorder(1, 1, 1, 1, (Color) new Color(0, 0, 0)));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// 
		contentPanel.add(createFrequencyPanel(), "span 3,grow");

		//
		tblFrequencies = new VNAEditableFrequencyTable();
		tblFrequencies.addActionListener(this);
		contentPanel.add(tblFrequencies, "span 1 2,wrap");

		//
		contentPanel.add(new JLabel(VNADriverSerialMax6Messages.getString("VNAGeneratorMAX6Dialog.attn")), "");
		sbLevel = new JScrollBar(JScrollBar.HORIZONTAL, outputLevel, 1, 0, 16384);
		sbLevel.setBlockIncrement(1000);
		sbLevel.addAdjustmentListener(this);
		contentPanel.add(sbLevel, "span 2,grow x");

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

	/**
	 * @return
	 */
	private JPanel createFrequencyPanel() {
		JPanel rc;
		TraceHelper.entry(this, "createFrequencyPanel");

		// handler for digits
		VNADeviceInfoBlock dib = datapool.getDriver().getDeviceInfoBlock();
		mouseHandler = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());
		mouseHandler.addChangeListener(this);

		rc = new JPanel();
		rc.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		rc.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JLabel label;

		rc.add(mouseHandler.registerField(new VNADigitTextField(1000000000, 0)));

		label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
		label.setFont(new Font("Tahoma", Font.PLAIN, 54));
		rc.add(label);
		rc.add(mouseHandler.registerField(new VNADigitTextField(100000000, 0)));
		rc.add(mouseHandler.registerField(new VNADigitTextField(10000000, 0)));
		rc.add(mouseHandler.registerField(new VNADigitTextField(1000000, 0)));

		label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
		label.setFont(new Font("Tahoma", Font.PLAIN, 54));
		rc.add(label);
		rc.add(mouseHandler.registerField(new VNADigitTextField(100000, 0)));
		rc.add(mouseHandler.registerField(new VNADigitTextField(10000, 0)));
		rc.add(mouseHandler.registerField(new VNADigitTextField(1000, 0)));

		label = new JLabel(VNAMessages.getString("VNAGeneratorDialog.thousand"));
		label.setFont(new Font("Tahoma", Font.PLAIN, 54));
		rc.add(label);
		rc.add(mouseHandler.registerField(new VNADigitTextField(100, 0)));
		rc.add(mouseHandler.registerField(new VNADigitTextField(10, 0)));
		rc.add(mouseHandler.registerField(new VNADigitTextField(1, 0)));

		JLabel lblHz = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblHz.text"));
		lblHz.setFont(new Font("Tahoma", Font.PLAIN, 54));
		rc.add(lblHz);
		TraceHelper.exit(this, "createFrequencyPanel");
		return rc;

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
		TraceHelper.entry(this, "stateChanged", "" + e);
		if (e.getSource() == mouseHandler) {
			if (onAir) {
				startGenerator();
			}
		}
		TraceHelper.exit(this, "stateChanged");
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
					startGenerator();
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
				startGenerator();
				onAir = true;
			}
			updateOnAirField();
		}
		TraceHelper.exit(this, "doMouseClickedOnOnAirField");

	}

	/**
	 * 
	 */
	private void startGenerator() {
		TraceHelper.entry(this, "startGenerator");
		btOK.setEnabled(false);
		try {
			long frequency = mouseHandler.getValue();
			driver.startGenerator(frequency, 0, VNADriverSerialMax6DIB.MAX_LEVEL - outputLevel, 0, 0, 0);
		} catch (ProcessingException ex) {
			ErrorLogHelper.exception(this, "startGenerator", ex);
		}
		btOK.setEnabled(true);
		TraceHelper.exit(this, "startGenerator");
	}

	/**
	 * 
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.AdjustmentListener#adjustmentValueChanged(java.awt.event
	 * .AdjustmentEvent)
	 */
	public void adjustmentValueChanged(AdjustmentEvent e) {
		if (!e.getValueIsAdjusting()) {
			outputLevel = e.getValue();
			sbLevel.setToolTipText("" + outputLevel);
			if (onAir) {
				startGenerator();
			}
		}
	}
}
