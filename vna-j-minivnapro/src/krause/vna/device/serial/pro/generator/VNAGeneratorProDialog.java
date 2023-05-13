package krause.vna.device.serial.pro.generator;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import krause.common.TypedProperties;
import krause.common.exception.ProcessingException;
import krause.common.gui.ILocationAwareDialog;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.device.IVNADriver;
import krause.vna.device.serial.pro.VNADriverSerialProDIB;
import krause.vna.device.serial.pro.VNADriverSerialProMessages;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.generator.digit.VNADigitTextField;
import krause.vna.gui.generator.digit.VNADigitTextFieldHandler;
import net.miginfocom.swing.MigLayout;

public class VNAGeneratorProDialog extends KrauseDialog implements ChangeListener, ActionListener, ClipboardOwner, AdjustmentListener, ILocationAwareDialog {
	private static final String PROPERTIES_PREFIX = "VNAGeneratorProDialog";
	public final static int FONT_SIZE = 30;
	public final static int MIN_PHASE = 0;
	public final static int MAX_PHASE = 360 * 100;

	private static Font symbolFont = new Font("Tahoma", Font.PLAIN, FONT_SIZE);
	private String groupSeparator = new String("" + DecimalFormatSymbols.getInstance().getGroupingSeparator());

	private JButton btOK;
	private JPanel contentPanel;
	private VNADigitTextFieldHandler handlerFrequencyQ = null;
	private VNADigitTextFieldHandler handlerFrequencyI = null;
	private VNADigitTextFieldHandler handlerAttenuationI = null;
	private VNADigitTextFieldHandler handlerAttenuationQ = null;
	private VNADigitTextFieldHandler handlerPhase = null;
	private JPanel panelFRQQ;
	private TypedProperties cfg = VNAConfig.getSingleton();
	private VNADriverSerialProDIB dib;
	private JLabel lblOnAir;
	private boolean onAir = false;
	private IVNADriver driver = null;
	private JTextField txtVALUE;
	JScrollBar sbPhase;
	private JToggleButton btLinkFrq;
	private JToggleButton btLinkAtt;

	/**
	 * 
	 * @param pMainFrame
	 */
	public VNAGeneratorProDialog(VNAMainFrame pMainFrame, IVNADriver pDriver) {
		super(pMainFrame.getJFrame(), true);

		driver = pDriver;
		dib = (VNADriverSerialProDIB) driver.getDeviceInfoBlock();

		setTitle(VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.title"));
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setPreferredSize(new Dimension(900, 500));
		addWindowListener(this);

		// content pane
		getContentPane().setLayout(new BorderLayout(5, 5));

		// handler for digits
		createDigitHandlers();

		// content panel
		contentPanel = new JPanel(new MigLayout("", "[left]0[grow, fill]0[right]", "0[]0[]0[]0[fill]0"));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		// frequency stuff
		contentPanel.add(createFREQPanel(), "span 3, center, grow, wrap");

		// attenuation stuff
		contentPanel.add(createATTPanel(), "span 3, center, grow, wrap");

		// phase stuff
		contentPanel.add(createPHASEPanel(), "growy");
		contentPanel.add(createHELPPanel(), "growy");
		contentPanel.add(createNUMPanel(), "growy,wrap");

		btOK = new JButton(VNADriverSerialProMessages.getString("Button.Close"));
		btOK.addActionListener(this);
		contentPanel.add(btOK, "span 3, right");

		doDialogInit();
	}

	private JPanel createNUMPanel() {
		JPanel panelNUM = new JPanel(new MigLayout("", "", ""));
		panelNUM.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.control"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelNUM.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel panel_1 = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_1.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelNUM.add(panel_1);

		JLabel lblValue = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblValue.text"));
		panel_1.add(lblValue);

		txtVALUE = new JTextField();
		txtVALUE.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				// TraceHelper.text(this, "keyReleased", e.toString());
				if (e.getKeyCode() >= 115 && e.getKeyCode() <= 120) {
					doProcessFKey(e);
				}
			}
		});
		panel_1.add(txtVALUE);
		txtVALUE.setColumns(10);

		JPanel panel_3 = new JPanel();
		panelNUM.add(panel_3);

		JLabel lblEndWithFor = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblEndWithFor.text")); //$NON-NLS-1$
		panel_3.add(lblEndWithFor);

		JPanel panel_2 = new JPanel();
		panelNUM.add(panel_2);

		JLabel lblTuneTheFrequency = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblTuneTheFrequency.text"));
		panel_2.add(lblTuneTheFrequency);

		lblOnAir = new JLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblOnAir.text")); //$NON-NLS-1$
		panel_2.add(lblOnAir);
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

		return panelNUM;
	}

	private JPanel createHELPPanel() {
		JPanel panelHLP = new JPanel(new MigLayout("", "[grow,center]", "[]"));
		panelHLP.setBorder(new TitledBorder(null, VNADriverSerialProMessages.getString("VNAGeneratorDialog.help"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JTextArea txtrFEnterI = new JTextArea();
		txtrFEnterI.setTabSize(3);
		txtrFEnterI.setEditable(false);
		txtrFEnterI.setBackground(UIManager.getColor("Label.background"));
		txtrFEnterI.setFont(UIManager.getFont("Label.font"));
		txtrFEnterI.setText(VNADriverSerialProMessages.getString("VNAGeneratorDialog.txtrFEnterI.text")); //$NON-NLS-1$
		panelHLP.add(txtrFEnterI, "");
		return panelHLP;
	}

	private JPanel createPHASEPanel() {
		JPanel panelPhase = new JPanel(new MigLayout("", "[][][][][][][]", "[][]"));
		panelPhase.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.phaseDiff"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		panelPhase.add(handlerPhase.registerField(new VNADigitTextField(10000, 0, FONT_SIZE)), "");
		panelPhase.add(handlerPhase.registerField(new VNADigitTextField(1000, 0, FONT_SIZE)), "");
		panelPhase.add(handlerPhase.registerField(new VNADigitTextField(100, 0, FONT_SIZE)), "");
		panelPhase.add(createNewLabel(groupSeparator), "");
		panelPhase.add(handlerPhase.registerField(new VNADigitTextField(10, 0, FONT_SIZE)), "");
		panelPhase.add(handlerPhase.registerField(new VNADigitTextField(1, 0, FONT_SIZE)), "");
		panelPhase.add(createNewLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.label_7.text")), "wrap");

		sbPhase = new JScrollBar(JScrollBar.HORIZONTAL, 0, 1, MIN_PHASE, MAX_PHASE);
		sbPhase.setBlockIncrement(50);
		sbPhase.addAdjustmentListener(this);
		panelPhase.add(sbPhase, "grow, span 7");
		return panelPhase;
	}

	private JPanel createFREQPanel() {
		JPanel panelFRQ = new JPanel(new MigLayout("", "0[left]0[center,fill,grow]0[right]0", "0[]0"));
		panelFRQ.setBorder(new TitledBorder(null, VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.Frequency"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panelFRQI = new JPanel(new MigLayout("", "0", "0"));
		panelFRQ.add(panelFRQI, "");
		panelFRQI.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.frq.i"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(1000000000, 0, FONT_SIZE)));
		panelFRQI.add(createNewLabel(groupSeparator));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(100000000, 0, FONT_SIZE)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(10000000, 0, FONT_SIZE)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(1000000, 0, FONT_SIZE)));
		panelFRQI.add(createNewLabel(groupSeparator));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(100000, 0, FONT_SIZE)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(10000, 0, FONT_SIZE)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(1000, 0, FONT_SIZE)));
		panelFRQI.add(createNewLabel(groupSeparator));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(100, 0, FONT_SIZE)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(10, 0, FONT_SIZE)));
		panelFRQI.add(handlerFrequencyI.registerField(new VNADigitTextField(1, 0, FONT_SIZE)));
		panelFRQI.add(createNewLabel("Hz"));

		JPanel panelIQLink = new JPanel(new MigLayout("", "", ""));
		panelFRQ.add(panelIQLink, "");

		JButton btFrqRight2Left = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button.text"));
		btFrqRight2Left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handlerFrequencyI.setValue(handlerFrequencyQ.getValue());
				startGenerator();
			}
		});
		panelIQLink.add(btFrqRight2Left, "wrap");

		btLinkFrq = new JToggleButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.toggleButton.text")); //$NON-NLS-1$
		btLinkFrq.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelIQLink.add(btLinkFrq, "wrap");

		JButton btFrqLeft2Right = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button_1.text")); //$NON-NLS-1$
		btFrqLeft2Right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handlerFrequencyQ.setValue(handlerFrequencyI.getValue());
				startGenerator();
			}
		});
		panelIQLink.add(btFrqLeft2Right, "");

		panelFRQQ = new JPanel(new MigLayout("", "", ""));
		panelFRQ.add(panelFRQQ);
		panelFRQQ.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.frq.q"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));

		/**
		 * handlerFrequencyQ
		 */
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(1000000000, 0, FONT_SIZE)));
		panelFRQQ.add(createNewLabel(groupSeparator));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(100000000, 0, FONT_SIZE)));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(10000000, 0, FONT_SIZE)));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(1000000, 0, FONT_SIZE)));
		panelFRQQ.add(createNewLabel(groupSeparator));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(100000, 0, FONT_SIZE)));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(10000, 0, FONT_SIZE)));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(1000, 0, FONT_SIZE)));
		panelFRQQ.add(createNewLabel(groupSeparator));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(100, 0, FONT_SIZE)));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(10, 0, FONT_SIZE)));
		panelFRQQ.add(handlerFrequencyQ.registerField(new VNADigitTextField(1, 0, FONT_SIZE)));
		panelFRQQ.add(createNewLabel("Hz"));

		return panelFRQ;
	}

	private JPanel createATTPanel() {
		JPanel panelATT = new JPanel(new MigLayout("", "0[left]0[center,fill,grow]0[right]0", "0[]0"));
		panelATT.setBorder(new TitledBorder(null, VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.att"), TitledBorder.LEADING, TitledBorder.TOP, null, null));

		JPanel panelATTI = new JPanel(new MigLayout("", "", ""));
		panelATTI.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.att.i"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelATT.add(panelATTI, "");

		panelATTI.add(createNewLabel("-"), "");
		panelATTI.add(handlerAttenuationI.registerField(new VNADigitTextField(1000, 0, FONT_SIZE)), "");
		panelATTI.add(handlerAttenuationI.registerField(new VNADigitTextField(100, 0, FONT_SIZE)), "");
		panelATTI.add(createNewLabel(groupSeparator), "");
		panelATTI.add(handlerAttenuationI.registerField(new VNADigitTextField(10, 0, FONT_SIZE)), "");
		panelATTI.add(handlerAttenuationI.registerField(new VNADigitTextField(1, 0, FONT_SIZE)), "");
		panelATTI.add(createNewLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblDb.text")), "");

		JPanel panelATTCenter = new JPanel(new MigLayout("", "", ""));
		panelATT.add(panelATTCenter, "");
		JButton btAttRight2Left = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button_2.text")); //$NON-NLS-1$
		btAttRight2Left.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handlerAttenuationI.setValue(handlerAttenuationQ.getValue());
				startGenerator();
			}
		});
		panelATTCenter.add(btAttRight2Left, "");

		btLinkAtt = new JToggleButton("\u221E");
		btLinkAtt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		panelATTCenter.add(btLinkAtt, "");

		JButton btAttLeft2Right = new JButton(VNADriverSerialProMessages.getString("VNAGeneratorDialog.button_3.text")); //$NON-NLS-1$
		btAttLeft2Right.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				handlerAttenuationQ.setValue(handlerAttenuationI.getValue());
				startGenerator();
			}
		});
		panelATTCenter.add(btAttLeft2Right, "");

		JPanel panelATTQ = new JPanel(new MigLayout("", "", ""));
		panelATTQ.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNADriverSerialProMessages.getString("VNAGeneratorPro.Dialog.att.q"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panelATT.add(panelATTQ, "");

		panelATTQ.add(createNewLabel("-"), "");
		panelATTQ.add(handlerAttenuationQ.registerField(new VNADigitTextField(1000, 0, FONT_SIZE)), "");
		panelATTQ.add(handlerAttenuationQ.registerField(new VNADigitTextField(100, 0, FONT_SIZE)), "");
		panelATTQ.add(createNewLabel(groupSeparator), "");
		panelATTQ.add(handlerAttenuationQ.registerField(new VNADigitTextField(10, 0, FONT_SIZE)), "");
		panelATTQ.add(handlerAttenuationQ.registerField(new VNADigitTextField(1, 0, FONT_SIZE)), "");
		panelATTQ.add(createNewLabel(VNADriverSerialProMessages.getString("VNAGeneratorDialog.lblDb.text")), "");

		//
		return panelATT;
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

	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		TraceHelper.text(this, "actionPerformed", e.toString());
		if (e.getSource() == btOK) {
			doDialogCancel();
		}
		TraceHelper.exit(this, "actionPerformed");
	}

	public void adjustmentValueChanged(AdjustmentEvent e) {
		// TraceHelper.entry(this, "adjustmentValueChanged");
		handlerPhase.setValue(sbPhase.getValue());
		if (!e.getValueIsAdjusting()) {
			startGenerator();
		}
		// TraceHelper.exit(this, "adjustmentValueChanged");

	}

	private void createDigitHandlers() {
		TraceHelper.entry(this, "createDigitHandlers");
		handlerFrequencyI = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());
		handlerFrequencyQ = new VNADigitTextFieldHandler(dib.getMinFrequency(), dib.getMaxFrequency());

		handlerAttenuationI = new VNADigitTextFieldHandler(0, 6020);
		handlerAttenuationQ = new VNADigitTextFieldHandler(0, 6020);

		handlerPhase = new VNADigitTextFieldHandler(MIN_PHASE, MAX_PHASE);

		handlerAttenuationI.addChangeListener(this);
		handlerAttenuationQ.addChangeListener(this);
		handlerFrequencyI.addChangeListener(this);
		handlerFrequencyQ.addChangeListener(this);
		handlerPhase.addChangeListener(this);

		TraceHelper.exit(this, "createDigitHandlers");

	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		//
		storeWindowPosition();
		storeWindowSize();
		//
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		//
		cfg.putLong(PROPERTIES_PREFIX + ".handlerFrequencyQ", handlerFrequencyQ.getValue());
		cfg.putLong(PROPERTIES_PREFIX + ".handlerFrequencyI", handlerFrequencyI.getValue());
		cfg.putInteger(PROPERTIES_PREFIX + ".handlerAttenuationI", (int) handlerAttenuationI.getValue());
		cfg.putInteger(PROPERTIES_PREFIX + ".handlerAttenuationQ", (int) handlerAttenuationQ.getValue());
		cfg.putInteger(PROPERTIES_PREFIX + ".handlerPhase", (int) handlerPhase.getValue());
		cfg.putBoolean(PROPERTIES_PREFIX + ".btLinkAtt", btLinkAtt.isSelected());
		cfg.putBoolean(PROPERTIES_PREFIX + ".btLinkFrq", btLinkFrq.isSelected());

		stopGenerator();

		// and hide myself
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	private void doClickOnAirField(MouseEvent e) {
		TraceHelper.entry(this, "doMouseClickedOnFrequency");
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (onAir) {
				stopGenerator();
				onAir = false;
			} else {
				onAir = true;
				startGenerator();
			}
			updateOnAirField();
		}
		TraceHelper.exit(this, "doMouseClickedOnFrequency");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		// now do the rest
		handlerFrequencyI.setValue(cfg.getInteger(PROPERTIES_PREFIX + ".handlerFrequencyI", dib.getMinFrequency()));
		handlerFrequencyQ.setValue(cfg.getInteger(PROPERTIES_PREFIX + ".handlerFrequencyQ", dib.getMinFrequency()));

		handlerAttenuationI.setValue(cfg.getInteger(PROPERTIES_PREFIX + ".handlerAttenuationI", 0));
		handlerAttenuationQ.setValue(cfg.getInteger(PROPERTIES_PREFIX + ".handlerAttenuationQ", 0));
		handlerPhase.setValue(cfg.getInteger(PROPERTIES_PREFIX + ".handlerPhase", 0));
		sbPhase.setValue((int) handlerPhase.getValue());

		btLinkAtt.setSelected(cfg.getBoolean(PROPERTIES_PREFIX + ".btLinkAtt", false));
		btLinkFrq.setSelected(cfg.getBoolean(PROPERTIES_PREFIX + ".btLinkFrq", false));

		updateOnAirField();

		// add escape key to window
		addEscapeKey();
		TraceHelper.exit(this, "doInit");
	}

	protected void doProcessFKey(KeyEvent e) {
		TraceHelper.entry(this, "doProcessFKey");
		boolean ok = false;
		if (e.getKeyCode() == 115) {
			if (onAir) {
				stopGenerator();
				onAir = false;
			} else {
				onAir = true;
				startGenerator();
			}
			updateOnAirField();
			ok = true;
		} else {
			String sVal = txtVALUE.getText();
			if (sVal != null) {
				sVal = sVal.trim();
				if (sVal.length() > 0) {
					sVal = sVal.toUpperCase();
					int factor;
					if (sVal.endsWith("K")) {
						factor = 1000;
						sVal = sVal.substring(0, sVal.length() - 1);
					} else if (sVal.endsWith("M")) {
						factor = 1000000;
						sVal = sVal.substring(0, sVal.length() - 1);
					} else {
						factor = 1;
					}
					try {
						double dVal = NumberFormat.getInstance().parse(sVal).doubleValue();
						dVal *= factor;
						switch (e.getKeyCode()) {
						case 116:
							handlerFrequencyI.setValue((int) dVal);
							ok = true;
							break;
						case 117:
							handlerFrequencyQ.setValue((int) dVal);
							ok = true;
							break;
						case 118:
							handlerAttenuationI.setValue((int) (dVal * 100));
							ok = true;
							break;
						case 119:
							handlerAttenuationQ.setValue((int) (dVal * 100));
							ok = true;
							break;
						case 120:
							int val = (int) (dVal * 100);
							handlerPhase.setValue(val);
							sbPhase.setValue(val);
							ok = true;
							break;
						}
					} catch (Exception ex) {
					}
				}
			}
			if (ok) {
				startGenerator();
				txtVALUE.select(0, 999);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
		TraceHelper.exit(this, "doProcessFKey");
	}

	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		TraceHelper.entry(this, "lostOwnership");
		TraceHelper.exit(this, "lostOwnership");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.LocationAwareDialog#restoreWindowPosition()
	 */
	public void restoreWindowPosition() {
		TraceHelper.entry(this, "restoreWindowPosition");
		cfg.restoreWindowPosition(PROPERTIES_PREFIX, this, new Point(10, 10));
		TraceHelper.exit(this, "restoreWindowPosition");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.LocationAwareDialog#restoreWindowSize()
	 */
	public void restoreWindowSize() {
		TraceHelper.entry(this, "restoreWindowSize");
		cfg.restoreWindowSize(PROPERTIES_PREFIX, this, new Dimension(880, 450));
		TraceHelper.exit(this, "restoreWindowSize");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.LocationAwareDialog#showInPlace()
	 */
	public void showInPlace() {
		TraceHelper.entry(this, "showInPlace");
		restoreWindowPosition();
		pack();
		restoreWindowSize();
		setVisible(true);
		TraceHelper.exit(this, "showInPlace");
	}

	private void startGenerator() {
		TraceHelper.entry(this, "startGenerator");
		if (onAir) {
			btOK.setEnabled(false);
			long frequencyI = handlerFrequencyI.getValue();
			long frequencyQ = handlerFrequencyQ.getValue();
			int attentuationI = (int) handlerAttenuationI.getValue();
			int attenuationQ = (int) handlerAttenuationQ.getValue();
			int phase = (int) handlerPhase.getValue();
			int mainAttenuation = 0;

			try {
				driver.startGenerator(frequencyI, frequencyQ, attentuationI, attenuationQ, phase, mainAttenuation);
			} catch (ProcessingException e) {
				ErrorLogHelper.exception(this, "startGenerator", e);
			}
			btOK.setEnabled(true);
		}
		TraceHelper.exit(this, "startGenerator");
	}

	public void stateChanged(ChangeEvent e) {
		TraceHelper.entry(this, "stateChanged", "" + e);
		VNADigitTextFieldHandler handler = (VNADigitTextFieldHandler) e.getSource();
		long delta = handler.getValue() - handler.getOldValue();

		if (handler == handlerFrequencyQ) {
			if (btLinkFrq.isSelected()) {
				handlerFrequencyI.setValue(handlerFrequencyI.getValue() + delta);
			}
			startGenerator();
		} else if (handler == handlerFrequencyI) {
			if (btLinkFrq.isSelected()) {
				handlerFrequencyQ.setValue(handlerFrequencyQ.getValue() + delta);
			}
			startGenerator();
		} else if (handler == handlerAttenuationI) {
			if (btLinkAtt.isSelected()) {
				handlerAttenuationQ.setValue(handlerAttenuationQ.getValue() + delta);
			}
			startGenerator();
		} else if (handler == handlerAttenuationQ) {
			if (btLinkAtt.isSelected()) {
				handlerAttenuationI.setValue(handlerAttenuationI.getValue() + delta);
			}
			startGenerator();
		} else if (handler == handlerPhase) {
			startGenerator();
		}
		TraceHelper.exit(this, "stateChanged");
	}

	private void stopGenerator() {
		TraceHelper.entry(this, "stopGenerator");
		btOK.setEnabled(false);
		try {
			driver.stopGenerator();
		} catch (ProcessingException ex) {
			ErrorLogHelper.exception(this, "stopGenerator", ex);
		}
		btOK.setEnabled(true);
		TraceHelper.exit(this, "stopGenerator");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.LocationAwareDialog#storeWindowPosition()
	 */
	public void storeWindowPosition() {
		TraceHelper.entry(this, "storeWindowPosition");
		cfg.storeWindowPosition(PROPERTIES_PREFIX, this);
		TraceHelper.exit(this, "storeWindowPosition");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.LocationAwareDialog#storeWindowSize()
	 */
	public void storeWindowSize() {
		TraceHelper.entry(this, "storeWindowSize");
		cfg.storeWindowSize(PROPERTIES_PREFIX, this);
		TraceHelper.exit(this, "storeWindowSize");
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

	@Override
	public void windowOpened(WindowEvent e) {
		TraceHelper.entry(this, "windowOpened");
		super.windowOpened(e);
		txtVALUE.requestFocus();
		TraceHelper.exit(this, "windowOpened");
	}
}
