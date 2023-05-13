package krause.vna.gui.panels.marker;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.gui.HelpButton;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.marker.math.VNAMarkerMathHelper;
import krause.vna.marker.math.VNAMarkerMathInput;
import krause.vna.marker.math.VNAMarkerMathResult;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAMarkerMathDialog extends KrauseDialog {
	final transient private TypedProperties config = VNAConfig.getSingleton();
	final transient private VNADataPool datapool = VNADataPool.getSingleton();

	private String confPrefix;

	VNAMarker marker = null;
	private JTextField txtLoss;
	private JTextField txtFrq;
	private JTextField txtLeftLowFrq;
	private JTextField txtRightLowFrq;
	private JTextField txtLeftLowLoss;
	private JTextField txtRightLowLoss;
	private JTextField txtLossTarget;
	private JLabel lblFrequency;
	private JLabel lblTarget;
	private JLabel lblBandwidth;
	private JTextField txtBW;
	private JTextField txtQ;
	private JLabel lblLow;
	private JLabel lblMode;
	private JTextField txtMODE;

	NumberFormat returnLossFormat = VNAFormatFactory.getReflectionLossFormat();

	private JLabel lblC;
	private JLabel lblL;
	private JTextField txtCSer;
	private JTextField txtLSer;
	private JLabel lblRs;
	private JLabel lblRp;
	private JTextField txtRS;
	private JTextField txtRP;
	private JLabel lblXs;
	private JTextField txtXS;
	private JLabel lblXp;
	private JTextField txtXP;
	private JRadioButton rbTL;
	private JRadioButton rbRL;

	/**
	 * 
	 * @param pMainFrame
	 * @param pMarker
	 */
	public VNAMarkerMathDialog(VNAMarker pMarker) {
		super(null, false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setBounds(new Rectangle(0, 0, 440, 330));

		setTitle(MessageFormat.format(VNAMessages.getString("VNAMarkerMathDialog.title"), pMarker.getName()));
		marker = pMarker;
		confPrefix = "MarkerMath." + marker.getName();

		setResizable(true);
		getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel2 = new JPanel();
		panel2.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(panel2);
		panel2.setLayout(new MigLayout("", "", "grow"));

		// -----------------------
		panel2.add(new JLabel(), "");

		lblLow = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.low"));
		panel2.add(lblLow, "");
		JLabel lblLoss = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.marker"));
		panel2.add(lblLoss, "");
		JLabel lblFreq = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.high"));
		panel2.add(lblFreq, "wrap");
		lblFrequency = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.frequency"));
		panel2.add(lblFrequency, "");

		// -----------------------
		txtLeftLowFrq = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
		txtLeftLowFrq.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLeftLowFrq.setEditable(false);
		txtLeftLowFrq.setColumns(9);
		panel2.add(txtLeftLowFrq, "");

		txtFrq = new JTextField();
		txtFrq.setHorizontalAlignment(SwingConstants.RIGHT);
		panel2.add(txtFrq, "");
		txtFrq.setEditable(false);
		txtFrq.setColumns(9);

		txtRightLowFrq = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
		txtRightLowFrq.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRightLowFrq.setEditable(false);
		txtRightLowFrq.setColumns(9);
		panel2.add(txtRightLowFrq, "wrap");

		JLabel lblLossdb = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.loss"));
		panel2.add(lblLossdb);

		// -----------------------
		txtLeftLowLoss = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
		txtLeftLowLoss.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLeftLowLoss.setEditable(false);
		txtLeftLowLoss.setColumns(9);
		panel2.add(txtLeftLowLoss, "");

		txtLoss = new JTextField();
		txtLoss.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLoss.setEditable(false);
		txtLoss.setColumns(9);
		panel2.add(txtLoss, "");

		txtRightLowLoss = new JTextField(VNAMessages.getString("VNAMarkerMathDialog.empty"));
		txtRightLowLoss.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRightLowLoss.setEditable(false);
		txtRightLowLoss.setColumns(9);
		panel2.add(txtRightLowLoss, "wrap");

		lblTarget = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.target"));
		panel2.add(lblTarget);

		// -----------------------
		txtLossTarget = new JTextField();
		txtLossTarget.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLossTarget.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				try {
					txtLossTarget.setText(returnLossFormat.format(returnLossFormat.parse(txtLossTarget.getText())));
				} catch (ParseException e1) {
					Toolkit.getDefaultToolkit().beep();
				}
				update();
			}
		});
		txtLossTarget.setText("6,0");
		txtLossTarget.setColumns(4);
		panel2.add(txtLossTarget, "wrap");

		// -----------------------
		lblBandwidth = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.bandwidth"));
		panel2.add(lblBandwidth, "");

		txtBW = new JTextField();
		txtBW.setHorizontalAlignment(SwingConstants.RIGHT);
		txtBW.setEditable(false);
		txtBW.setColumns(9);
		panel2.add(txtBW, "");

		// -----------------------
		JLabel lblQ1 = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.q"));
		panel2.add(lblQ1, "right");

		txtQ = new JTextField();
		txtQ.setHorizontalAlignment(SwingConstants.RIGHT);
		txtQ.setEditable(false);
		txtQ.setColumns(9);
		panel2.add(txtQ, "wrap");

		lblC = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.c"));
		panel2.add(lblC, "");

		txtCSer = new JTextField();
		txtCSer.setHorizontalAlignment(SwingConstants.RIGHT);
		txtCSer.setEditable(false);
		txtCSer.setColumns(9);
		panel2.add(txtCSer, "");

		lblL = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.l"));
		panel2.add(lblL, "right");

		txtLSer = new JTextField();
		txtLSer.setHorizontalAlignment(SwingConstants.RIGHT);
		txtLSer.setEditable(false);
		txtLSer.setColumns(9);
		panel2.add(txtLSer, "wrap");

		// -----------------------
		lblRs = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblRs.text")); //$NON-NLS-1$
		panel2.add(lblRs, "");

		txtRS = new JTextField();
		txtRS.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRS.setEditable(false);
		txtRS.setColumns(9);
		panel2.add(txtRS, "");

		// -----------------------
		lblXs = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblXs.text")); //$NON-NLS-1$
		panel2.add(lblXs, "right");

		txtXS = new JTextField();
		txtXS.setHorizontalAlignment(SwingConstants.RIGHT);
		txtXS.setEditable(false);
		txtXS.setColumns(9);
		panel2.add(txtXS, "wrap");

		// -----------------------
		lblRp = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblRp.text")); //$NON-NLS-1$
		panel2.add(lblRp, "");

		txtRP = new JTextField();
		txtRP.setHorizontalAlignment(SwingConstants.RIGHT);
		txtRP.setEditable(false);
		txtRP.setColumns(9);
		panel2.add(txtRP, "");

		lblXp = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.lblXp.text")); //$NON-NLS-1$
		panel2.add(lblXp, "right");

		txtXP = new JTextField();
		txtXP.setHorizontalAlignment(SwingConstants.RIGHT);
		txtXP.setEditable(false);
		txtXP.setColumns(9);
		panel2.add(txtXP, "wrap");

		// -----------------------
		lblMode = new JLabel(VNAMessages.getString("VNAMarkerMathDialog.mode"));
		panel2.add(lblMode, "");
		txtMODE = new JTextField();
		txtMODE.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMODE.setEditable(false);
		txtMODE.setColumns(9);
		panel2.add(txtMODE, "wrap");

		panel2.add(new JLabel(VNAMessages.getString("VNAMarkerMathDialog.use")), "");
		panel2.add(rbRL = SwingUtil.createJRadioButton("VNAMarkerMathDialog.rl", null), "");
		panel2.add(rbTL = SwingUtil.createJRadioButton("VNAMarkerMathDialog.tl", null), "");
		panel2.add(new HelpButton(this, "VNAMarkerMathDialog"), "wrap");
		ButtonGroup bg = new ButtonGroup();
		bg.add(rbRL);
		bg.add(rbTL);
		//
		rbRL.setSelected(datapool.getScanMode().isReflectionMode());
		rbTL.setSelected(datapool.getScanMode().isTransmissionMode());
		//
		rbTL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
		});
		rbRL.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				update();
			}
		});
		//
		doDialogInit();
	}

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");
		config.storeWindowPosition(confPrefix, this);
		config.storeWindowSize(confPrefix, this);
		config.put(confPrefix + ".Loss", txtLossTarget.getText());
		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		txtLossTarget.setText(config.getProperty(confPrefix + ".Loss", "6"));
		TraceHelper.exit(this, "doInit");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Dialog#show()
	 */
	public void doDialogShow() {
		config.restoreWindowPosition(confPrefix, this, new Point(100, 100));
		pack();
		config.restoreWindowSize(confPrefix, this, new Dimension(440, 330));
		setVisible(true);
	}

	public void update() {
		TraceHelper.entry(this, "update");
		String lossString = txtLossTarget.getText();
		try {
			double delta = VNAFormatFactory.getReflectionLossFormat().parse(lossString).doubleValue();

			final VNACalibratedSample markerSample = marker.getSample();
			final VNACalibratedSampleBlock currentData = datapool.getCalibratedData();
			final VNACalibratedSample[] allSamples = currentData.getCalibratedSamples();
			int markerSampleIndex = -1;
			// suche den im marker selektierten datensatz in den daten
			for (int i = 0; i < allSamples.length; ++i) {
				if (allSamples[i] == markerSample) {
					markerSampleIndex = i;
				}
			}

			// determine which loss data to use
			boolean useRL = rbRL.isSelected();

			// selektierten datensatz gefunden?
			if (markerSampleIndex != -1) {
				// ja
				// search down
				// n dB lower than the marker
				double lowDelta = (useRL ? markerSample.getReflectionLoss() : markerSample.getTransmissionLoss()) - delta;
				// n dB higher than the marker
				double highDelta = (useRL ? markerSample.getReflectionLoss() : markerSample.getTransmissionLoss()) + delta;

				VNACalibratedSample leftLowDeltaSample = null;
				VNACalibratedSample leftHighDeltaSample = null;
				VNACalibratedSample rightLowDeltaSample = null;
				VNACalibratedSample rightHighDeltaSample = null;

				// now search in peak mode, means left and right lower values
				for (int i = markerSampleIndex - 1; i > 0; --i) {
					VNACalibratedSample sample = allSamples[i];
					double loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
					if (loss < lowDelta) {
						leftLowDeltaSample = sample;
						break;
					}
				}
				for (int i = markerSampleIndex + 1; i < allSamples.length; ++i) {
					VNACalibratedSample sample = allSamples[i];
					double loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
					if (loss < lowDelta) {
						rightLowDeltaSample = sample;
						break;
					}
				}
				boolean peakMode = (leftLowDeltaSample != null && rightLowDeltaSample != null);

				// now search in notch mode, means left and right higher values
				for (int i = markerSampleIndex - 1; i > 0; --i) {
					VNACalibratedSample sample = allSamples[i];
					double loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
					if (loss > highDelta) {
						leftHighDeltaSample = sample;
						break;
					}
				}
				for (int i = markerSampleIndex + 1; i < allSamples.length; ++i) {
					VNACalibratedSample sample = allSamples[i];
					double loss = useRL ? sample.getReflectionLoss() : sample.getTransmissionLoss();
					if (loss > highDelta) {
						rightHighDeltaSample = sample;
						break;
					}
				}
				boolean notchMode = (leftHighDeltaSample != null && rightHighDeltaSample != null);

				// dies kann immer gemacht werden
				txtFrq.setText(VNAFormatFactory.getFrequencyFormat().format(markerSample.getFrequency()));
				txtLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? markerSample.getReflectionLoss() : markerSample.getTransmissionLoss()));

				VNAMarkerMathInput mmInput = new VNAMarkerMathInput(markerSample);

				if (peakMode) {
					txtMODE.setText(VNAMessages.getString("VNAMarkerMathDialog.notch"));
					txtLeftLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(leftLowDeltaSample.getFrequency()));
					txtLeftLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? leftLowDeltaSample.getReflectionLoss() : leftLowDeltaSample.getTransmissionLoss()));
					txtRightLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(rightLowDeltaSample.getFrequency()));
					txtRightLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? rightLowDeltaSample.getReflectionLoss() : rightLowDeltaSample.getTransmissionLoss()));

					mmInput.setHighFrequency(rightLowDeltaSample.getFrequency());
					mmInput.setLowFrequency(leftLowDeltaSample.getFrequency());
				} else if (notchMode) {
					txtMODE.setText(VNAMessages.getString("VNAMarkerMathDialog.peak"));
					txtLeftLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(leftHighDeltaSample.getFrequency()));
					txtLeftLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? leftHighDeltaSample.getReflectionLoss() : leftHighDeltaSample.getTransmissionLoss()));
					txtRightLowFrq.setText(VNAFormatFactory.getFrequencyFormat().format(rightHighDeltaSample.getFrequency()));
					txtRightLowLoss.setText(VNAFormatFactory.getReflectionLossFormat().format(useRL ? rightHighDeltaSample.getReflectionLoss() : rightHighDeltaSample.getTransmissionLoss()));

					mmInput.setHighFrequency(rightHighDeltaSample.getFrequency());
					mmInput.setLowFrequency(leftHighDeltaSample.getFrequency());
				} else {
					txtLeftLowFrq.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtLeftLowLoss.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtRightLowFrq.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtRightLowLoss.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtBW.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtQ.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtMODE.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtCSer.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtLSer.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					//
					txtRS.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtRP.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtXS.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));
					txtXP.setText(VNAMessages.getString("VNAMarkerMathDialog.empty"));

				}
				final VNAMarkerMathResult mmResult = VNAMarkerMathHelper.execute(mmInput);
				//
				txtBW.setText(VNAFormatFactory.getFrequencyFormat().format(mmResult.getBandWidth()));
				txtQ.setText(VNAFormatFactory.getQFormat().format(mmResult.getQ()));
				txtCSer.setText(VNAFormatFactory.getCapacityFormat().format(mmResult.getSerialCapacity()));
				txtLSer.setText(VNAFormatFactory.getInductivityFormat().format(mmResult.getSerialInductance()));
				//
				txtRS.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getRs()));
				txtRP.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getRp()));
				txtXS.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getXs()));
				txtXP.setText(VNAFormatFactory.getResistanceFormat().format(mmResult.getXp()));
			}
		} catch (ParseException e) {
			ErrorLogHelper.exception(this, "update", e);
		}
		TraceHelper.exit(this, "update");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		TraceHelper.entry(this, "windowClosing");
		marker.doClickOnMathSymbol();
		// String title =
		// MessageFormat.format(VNAMessages.getString("VNAMarkerMathDialog.infoClose.title"),
		// marker.getName());
		// String msg =
		// MessageFormat.format(VNAMessages.getString("VNAMarkerMathDialog.infoClose.text"),
		// marker.getName());
		// JOptionPane.showMessageDialog(this, msg, title,
		// JOptionPane.INFORMATION_MESSAGE);
		TraceHelper.exit(this, "windowClosing");
	}
}
