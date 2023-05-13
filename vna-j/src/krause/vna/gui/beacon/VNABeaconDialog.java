package krause.vna.gui.beacon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormatSymbols;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import krause.common.gui.KrauseDialog;
import krause.common.validation.IntegerValidator;
import krause.common.validation.ValidationResults;
import krause.common.validation.ValidationResultsDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.device.IVNADriver;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.firmware.IVNABackgroundFlashBurnerConsumer;
import krause.vna.firmware.SimpleStringListbox;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNABeaconDialog extends KrauseDialog implements ActionListener, IVNABackgroundFlashBurnerConsumer {
	private static final String PROPERTIES_PREFIX = "VNABeaconDialog";
	public final static int FONT_SIZE = 30;

	private static Font symbolFont = new Font("Tahoma", Font.PLAIN, FONT_SIZE);
	private String groupSeparator = new String("" + DecimalFormatSymbols.getInstance().getGroupingSeparator());

	private VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();
	private IVNADriver driver = datapool.getDriver();
	private VNADeviceInfoBlock dib = driver.getDeviceInfoBlock();

	private JButton btOK;
	private JPanel contentPanel;
	private JLabel lblOnAir;
	private boolean onAir = false;
	private VNADigitTextFieldHandler handlerFrequencyI;
	private JTextArea txtText;
	private JTextField txtInterval;
	private JTextField txtBPM;

	private SimpleStringListbox messageList;
	private VnaBackgroundBeacon backgroundBeacon;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNABeaconDialog(Window wnd) {
		super(wnd, true);

		setConfigurationPrefix(PROPERTIES_PREFIX);
		setProperties(config);

		setTitle(VNAMessages.getString("VNABeaconDialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(900, 500));
		addWindowListener(this);

		//
		handlerFrequencyI = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());

		// content pane
		getContentPane().setLayout(new BorderLayout(5, 5));

		// content panel
		contentPanel = new JPanel(new MigLayout("", "[left]0[grow, fill]0[right]", "[][grow][][][]"));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		contentPanel.add(createFREQPanel(), "span 3, center, grow, wrap");
		contentPanel.add(createTEXTPanel(), "span 3, center, grow, wrap");

		lblOnAir = new JLabel(VNAMessages.getString("VNAGeneratorDialog.lblOnAir.text")); //$NON-NLS-1$
		lblOnAir.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		lblOnAir.setOpaque(true);
		lblOnAir.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				doClickOnAirField(e);
			}
		});
		lblOnAir.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblOnAir.setFont(new Font("Courier New", Font.PLAIN, 17));
		lblOnAir.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		lblOnAir.setToolTipText(VNAMessages.getString("VNABeaconDialog.OnAir"));
		contentPanel.add(lblOnAir, "span 3, center, grow, wrap");

		//
		messageList = new SimpleStringListbox(VNAMessages.getString("VNABeaconDialog.Nachrichten"));
		JScrollPane listScroller = new JScrollPane(messageList);
		listScroller.setPreferredSize(new Dimension(600, 100));
		contentPanel.add(listScroller, "span 3, center, grow, wrap");

		// button panel
		btOK = new JButton(VNAMessages.getString("Button.Close"));
		btOK.addActionListener(this);
		contentPanel.add(btOK, "span 3, right");

		doDialogInit();
	}

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		TraceHelper.text(this, "actionPerformed", e.toString());
		if (e.getSource() == btOK) {
			doDialogCancel();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		//
		config.putInteger(PROPERTIES_PREFIX + ".Interval", Integer.parseInt(txtInterval.getText()));
		config.putInteger(PROPERTIES_PREFIX + ".BPM", Integer.parseInt(txtBPM.getText()));
		config.putLong(PROPERTIES_PREFIX + ".handlerFrequencyI", handlerFrequencyI.getValue());
		config.setProperty(PROPERTIES_PREFIX + ".text", txtText.getText());

		//
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		//
		if (backgroundBeacon != null) {
			backgroundBeacon.cancel(false);

			while (!backgroundBeacon.isDone()) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			backgroundBeacon = null;
		}

		// and hide myself
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	private void doClickOnAirField(MouseEvent e) {
		TraceHelper.entry(this, "doMouseClickedOnFrequency");
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (onAir) {
				beaconOff();
			} else {
				beaconOn();
			}
			updateOnAirField();
		}
		TraceHelper.exit(this, "doMouseClickedOnFrequency");
	}

	private void beaconOn() {
		TraceHelper.entry(this, "beaconOn");

		ValidationResults results = new ValidationResults();

		int interval = IntegerValidator.parse(txtInterval.getText(), 1, 999999999, VNAMessages.getString("VNABeaconDialog.Interval"), results);
		int bpm = IntegerValidator.parse(txtBPM.getText(), 1, 80, VNAMessages.getString("VNABeaconDialog.BPM"), results);

		if (results.isEmpty()) {
			//
			txtBPM.setEnabled(false);
			txtInterval.setEnabled(false);
			txtText.setEnabled(false);
			btOK.setEnabled(false);

			//
			backgroundBeacon = new VnaBackgroundBeacon();
			backgroundBeacon.setDriver(driver);
			backgroundBeacon.setDataConsumer(this);
			backgroundBeacon.setListbox(messageList);
			backgroundBeacon.setFrequency(handlerFrequencyI.getValue());
			backgroundBeacon.setPause(interval);
			backgroundBeacon.setBpm(bpm);
			backgroundBeacon.setMessage(txtText.getText());
			backgroundBeacon.execute();
			
			//
			onAir = true;
		} else {
			new ValidationResultsDialog(getOwner(), results, getTitle());
		}

		TraceHelper.exit(this, "beaconOn");
	}

	private void beaconOff() {
		TraceHelper.entry(this, "beaconOff");
		backgroundBeacon.cancel(false);
		backgroundBeacon = null;
		onAir = false;
		TraceHelper.exit(this, "beaconOff");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		//
		handlerFrequencyI.setValue(config.getInteger(PROPERTIES_PREFIX + ".handlerFrequencyI", dib.getMinFrequency()));
		txtInterval.setText("" + config.getInteger(PROPERTIES_PREFIX + ".Interval", 60));
		txtBPM.setText("" + config.getInteger(PROPERTIES_PREFIX + ".BPM", 60));
		txtText.setText(config.getProperty(PROPERTIES_PREFIX + ".text", "Test"));

		//
		updateOnAirField();

		//
		addEscapeKey();
		doDialogShow();
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

	private JPanel createTEXTPanel() {
		JPanel panelTEXT = new JPanel(new MigLayout("", "[center,fill,grow]", "[]"));
		panelTEXT.setBorder(new TitledBorder(null, VNAMessages.getString("VNABeaconDialog.Panel"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		//
		panelTEXT.add(new JLabel(VNAMessages.getString("VNABeaconDialog.Text")), "");
		txtText = new JTextArea("", 3, 40);
		txtText.setFont(new Font("Courier New", Font.PLAIN, 12));
		txtText.setLineWrap(true);
		txtText.setWrapStyleWord(true);
		JScrollPane sp = new JScrollPane(txtText);
		panelTEXT.add(sp, "wrap");

		panelTEXT.add(new JLabel(VNAMessages.getString("VNABeaconDialog.Interval")), "");
		txtInterval = new JTextField(10);
		panelTEXT.add(txtInterval, "wrap");

		panelTEXT.add(new JLabel(VNAMessages.getString("VNABeaconDialog.BPM")), "");
		txtBPM = new JTextField(10);
		panelTEXT.add(txtBPM, "wrap");

		return panelTEXT;
	}

	private JPanel createFREQPanel() {
		JPanel panelFRQ = new JPanel(new MigLayout("", "[center,fill,grow]", ""));
		panelFRQ.setBorder(new TitledBorder(null, VNAMessages.getString("VNABeaconDialog.Frequenz"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(1000000000, 0, FONT_SIZE)));
		panelFRQ.add(createNewLabel(groupSeparator));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(100000000, 0, FONT_SIZE)));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(10000000, 0, FONT_SIZE)));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(1000000, 0, FONT_SIZE)));
		panelFRQ.add(createNewLabel(groupSeparator));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(100000, 0, FONT_SIZE)));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(10000, 0, FONT_SIZE)));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(1000, 0, FONT_SIZE)));
		panelFRQ.add(createNewLabel(groupSeparator));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(100, 0, FONT_SIZE)));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(10, 0, FONT_SIZE)));
		panelFRQ.add(handlerFrequencyI.registerField(new VNADigitTextField(1, 0, FONT_SIZE)));
		panelFRQ.add(createNewLabel("Hz"));

		return panelFRQ;
	}

	/**
	 * create a label with the default symbol font for this dialog
	 * 
	 * @param str
	 * @return
	 */
	private JLabel createNewLabel(String str) {
		JLabel rc = new JLabel(str);
		rc.setFont(symbolFont);
		return rc;
	}

	@Override
	public void consumeReturnCode(Integer rc) {
		TraceHelper.entry(this, "consumeReturnCode");

		messageList.addMessage("... beacon mode ended");

		//
		txtBPM.setEnabled(true);
		txtInterval.setEnabled(true);
		txtText.setEnabled(true);
		btOK.setEnabled(true);

		TraceHelper.exit(this, "consumeReturnCode");
	}
}
