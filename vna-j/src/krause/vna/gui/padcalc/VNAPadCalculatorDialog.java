/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAPadCalculator.java
 *  Part of:   vna-j
 */

package krause.vna.gui.padcalc;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

/**
 * @author Dietmar
 * 
 */
public class VNAPadCalculatorDialog extends KrauseDialog {
	private JTextField txtR1;
	private JTextField txtR2;
	private JTextField txtR3;
	private JTextField txtR4;
	private JTextField txtR5;
	private JTextField txtAtten;
	private JTextField txtR3_E24;
	private JTextField txtR4_E24;
	private JTextField txtR5_E24;
	private JTextField txtR3_E48;
	private JTextField txtR4_E48;
	private JTextField txtR5_E48;
	private JTextField txtR1_E24;
	private JTextField txtR2_E24;
	private JTextField txtR1_E48;
	private JTextField txtR2_E48;
	private JTextField txtR1_E12;
	private JTextField txtR2_E12;
	private JTextField txtR3_E12;
	private JTextField txtR4_E12;
	private JTextField txtR5_E12;
	private JTextField txtNumRes;
	private JRadioButton rbPi;
	private JRadioButton rbT;
	private JLabel lblImage;
	NumberFormat realNumberFormat = NumberFormat.getNumberInstance();
	NumberFormat intNumberFormat = NumberFormat.getNumberInstance();

	/**
	 * @param aFrame
	 * @param modal
	 */
	public VNAPadCalculatorDialog(Frame aFrame) {
		super(aFrame, true);
		TraceHelper.entry(this, "VNAPadCalculatorDialog");

		setTitle(VNAMessages.getString("VNAPadCalculatorDialog.title"));
		setProperties(VNAConfig.getSingleton());
		setConfigurationPrefix("VNAPadCalculatorDialog");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

		setPreferredSize(new Dimension(600, 600));

		//
		realNumberFormat.setGroupingUsed(false);
		realNumberFormat.setMaximumFractionDigits(2);
		realNumberFormat.setMinimumFractionDigits(2);
		realNumberFormat.setMaximumIntegerDigits(4);
		realNumberFormat.setMinimumIntegerDigits(1);

		intNumberFormat.setGroupingUsed(false);
		intNumberFormat.setMaximumFractionDigits(0);
		intNumberFormat.setMinimumFractionDigits(0);
		intNumberFormat.setMaximumIntegerDigits(1);
		intNumberFormat.setMinimumIntegerDigits(1);

		JPanel pnlButtons = new JPanel();
		getContentPane().add(pnlButtons, BorderLayout.SOUTH);

		pnlButtons.add(new HelpButton(this, "VNAPadCalculatorDialog"));

		rbPi = new JRadioButton(VNAMessages.getString("VNAPadCalculatorDialog.piPad"));
		rbPi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				doSwitchPadType();
				TraceHelper.exit(this, "actionPerformed");
			}
		});
		rbT = new JRadioButton(VNAMessages.getString("VNAPadCalculatorDialog.tPad"));
		rbT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				TraceHelper.entry(this, "actionPerformed");
				doSwitchPadType();
				TraceHelper.exit(this, "actionPerformed");
			}
		});

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbPi);
		bg.add(rbT);
		pnlButtons.add(rbPi);
		pnlButtons.add(rbT);

		pnlButtons.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.numRes")), "");
		txtNumRes = new JTextField("2");
		txtNumRes.setColumns(3);
		txtNumRes.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				doCalculate();
			}

			public void focusGained(FocusEvent e) {
				txtNumRes.select(0, 99);
			}
		});
		pnlButtons.add(txtNumRes, "");

		JButton btOK = SwingUtil.createJButton("Button.Close", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doDialogCancel();
			}
		});
		pnlButtons.add(btOK);

		JPanel pnlCenter = new JPanel();
		getContentPane().add(pnlCenter, BorderLayout.CENTER);
		pnlCenter.setLayout(new MigLayout("", "[]", "[][][]"));

		//
		lblImage = new JLabel("");
		lblImage.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlCenter.add(lblImage, "spanx 6, spany 1, center, wrap");

		//
		pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.atten")), "");
		pnlCenter.add(new JLabel("R1"), "");
		pnlCenter.add(new JLabel("R2"), "");
		pnlCenter.add(new JLabel("R3"), "");
		pnlCenter.add(new JLabel("R4"), "");
		pnlCenter.add(new JLabel("R5"), "wrap");

		//
		int colWidth = 7;
		//
		txtAtten = new JTextField("6");
		txtAtten.setColumns(colWidth);
		txtAtten.setHorizontalAlignment(JTextField.RIGHT);
		txtAtten.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				doCalculate();
			}

			public void focusGained(FocusEvent e) {
				txtAtten.select(0, 99);
			}
		});
		pnlCenter.add(txtAtten, "");
		txtR1 = new JTextField("50");
		txtR1.setColumns(colWidth);
		txtR1.setHorizontalAlignment(JTextField.RIGHT);
		txtR1.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				doCalculate();
			}

			public void focusGained(FocusEvent e) {
				txtR1.select(0, 99);
			}
		});
		pnlCenter.add(txtR1, "");
		txtR2 = new JTextField("50");
		txtR2.setColumns(colWidth);
		txtR2.setHorizontalAlignment(JTextField.RIGHT);
		txtR2.addFocusListener(new FocusListener() {
			public void focusLost(FocusEvent e) {
				doCalculate();
			}

			public void focusGained(FocusEvent e) {
				txtR2.select(0, 99);
			}
		});
		pnlCenter.add(txtR2, "");
		txtR3 = new JTextField();
		txtR3.setColumns(colWidth);
		txtR3.setEditable(false);
		txtR3.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR3, "");
		txtR4 = new JTextField();
		txtR4.setEditable(false);
		txtR4.setColumns(colWidth);
		txtR4.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR4, "");
		txtR5 = new JTextField();
		txtR5.setEditable(false);
		txtR5.setColumns(colWidth);
		txtR5.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR5, "grow,wrap");

		//
		pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.E12")), "right");
		txtR1_E12 = new JTextField();
		txtR1_E12.setColumns(colWidth);
		txtR1_E12.setEditable(false);
		txtR1_E12.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR1_E12, "");
		txtR2_E12 = new JTextField();
		txtR2_E12.setColumns(colWidth);
		txtR2_E12.setEditable(false);
		txtR2_E12.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR2_E12, "");
		txtR3_E12 = new JTextField();
		txtR3_E12.setColumns(colWidth);
		txtR3_E12.setEditable(false);
		txtR3_E12.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR3_E12, "");
		txtR4_E12 = new JTextField();
		txtR4_E12.setEditable(false);
		txtR4_E12.setColumns(colWidth);
		txtR4_E12.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR4_E12, "");
		txtR5_E12 = new JTextField();
		txtR5_E12.setEditable(false);
		txtR5_E12.setColumns(colWidth);
		txtR5_E12.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR5_E12, "grow,wrap");

		//
		pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.E24")), "right");
		txtR1_E24 = new JTextField();
		txtR1_E24.setColumns(colWidth);
		txtR1_E24.setEditable(false);
		txtR1_E24.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR1_E24, "");
		txtR2_E24 = new JTextField();
		txtR2_E24.setColumns(colWidth);
		txtR2_E24.setEditable(false);
		txtR2_E24.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR2_E24, "");
		txtR3_E24 = new JTextField();
		txtR3_E24.setColumns(colWidth);
		txtR3_E24.setEditable(false);
		txtR3_E24.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR3_E24, "");
		txtR4_E24 = new JTextField();
		txtR4_E24.setEditable(false);
		txtR4_E24.setColumns(colWidth);
		txtR4_E24.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR4_E24, "");
		txtR5_E24 = new JTextField();
		txtR5_E24.setEditable(false);
		txtR5_E24.setColumns(colWidth);
		txtR5_E24.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR5_E24, "grow,wrap");

		//
		pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.E48")), "right");
		txtR1_E48 = new JTextField();
		txtR1_E48.setColumns(colWidth);
		txtR1_E48.setEditable(false);
		txtR1_E48.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR1_E48, "");
		txtR2_E48 = new JTextField();
		txtR2_E48.setColumns(colWidth);
		txtR2_E48.setEditable(false);
		txtR2_E48.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR2_E48, "");
		txtR3_E48 = new JTextField();
		txtR3_E48.setColumns(colWidth);
		txtR3_E48.setEditable(false);
		txtR3_E48.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR3_E48, "");
		txtR4_E48 = new JTextField();
		txtR4_E48.setEditable(false);
		txtR4_E48.setColumns(colWidth);
		txtR4_E48.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR4_E48, "");
		txtR5_E48 = new JTextField();
		txtR5_E48.setEditable(false);
		txtR5_E48.setColumns(colWidth);
		txtR5_E48.setHorizontalAlignment(JTextField.RIGHT);
		pnlCenter.add(txtR5_E48, "grow,wrap");

		pnlCenter.add(new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.hint1")), "right");
		JLabel lblHint = new JLabel(VNAMessages.getString("VNAPadCalculatorDialog.hint2"));
		lblHint.setForeground(Color.BLUE);
		pnlCenter.add(lblHint, "center,span 5, grow, wrap");

		//
		doDialogInit();
		TraceHelper.exit(this, "VNAPadCalculatorDialog");
	}

	/**
	 * 
	 */
	protected void doSwitchPadType() {
		TraceHelper.entry(this, "doSwitchPadType");
		if (rbPi.isSelected()) {
			lblImage.setIcon(new ImageIcon(VNAPadCalculatorDialog.class.getResource("/images/PiGlied.gif")));
		} else {
			lblImage.setIcon(new ImageIcon(VNAPadCalculatorDialog.class.getResource("/images/TGlied.gif")));
		}
		doCalculate();
		TraceHelper.exit(this, "doSwitchPadType");
	}

	/**
	 * 
	 */
	protected void doCalculate() {
		TraceHelper.entry(this, "doCalculate");

		double r1;
		double r2;
		double atten;
		int numResistors;

		try {
			r1 = realNumberFormat.parse(txtR1.getText()).doubleValue();
			r2 = realNumberFormat.parse(txtR2.getText()).doubleValue();
			atten = realNumberFormat.parse(txtAtten.getText()).doubleValue();
			numResistors = intNumberFormat.parse(txtNumRes.getText()).intValue();

			txtAtten.setText(realNumberFormat.format(atten));
			txtR1.setText(realNumberFormat.format(r1));
			txtR2.setText(realNumberFormat.format(r2));
			txtNumRes.setText(intNumberFormat.format(numResistors));

		} catch (Exception e) {
			txtR3.setText("");
			txtR4.setText("");
			txtR5.setText("");

			txtR1_E12.setText("");
			txtR2_E12.setText("");
			txtR3_E12.setText("");
			txtR4_E12.setText("");
			txtR5_E12.setText("");

			txtR1_E24.setText("");
			txtR2_E24.setText("");
			txtR3_E24.setText("");
			txtR4_E24.setText("");
			txtR5_E24.setText("");

			txtR1_E48.setText("");
			txtR2_E48.setText("");
			txtR3_E48.setText("");
			txtR4_E48.setText("");
			txtR5_E48.setText("");

			return;
		}

		VNAGenericPad pad;

		if (rbPi.isSelected())
			pad = new VNAPiPad();
		else
			pad = new VNATPad();

		pad.setR1(r1);
		pad.setR2(r2);

		VNAPadCalculator pc = new VNAPadCalculator();
		pc.setPad(pad);

		//
		pc.calculatePad(atten);

		double r3 = pad.getR3();
		double r4 = pad.getR4();
		double r5 = pad.getR5();

		txtR3.setText(VNAFormatFactory.getResistanceFormat().format(r3));
		txtR4.setText(VNAFormatFactory.getResistanceFormat().format(r4));
		txtR5.setText(VNAFormatFactory.getResistanceFormat().format(r5));

		if (r3 > 0 && r4 > 0 && r5 > 0) {
			// E12 series
			double[] fsE12 = pc.createFullSeries(VNAPadConstants.E12Factors, 6);
			List<Double> r3s = pc.calculateSeriesCircuit(fsE12, r3, numResistors, 0.01);
			List<Double> r4s = pc.calculateSeriesCircuit(fsE12, r4, numResistors, 0.01);
			List<Double> r5s = pc.calculateSeriesCircuit(fsE12, r5, numResistors, 0.01);

			VNAPiPad pp = new VNAPiPad();
			pp.setR1(r1);
			pp.setR2(r2);
			pp.setR3(getResistorSum(r3s));
			pp.setR4(getResistorSum(r4s));
			pp.setR5(getResistorSum(r5s));
			pc.setPad(pp);
			pc.reverseCalcPad(atten);

			txtR1_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR1()));
			txtR2_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR2()));
			txtR3_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR3()));
			txtR4_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR4()));
			txtR5_E12.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR5()));
			txtR3_E12.setToolTipText(formatResistorList(r3s));
			txtR4_E12.setToolTipText(formatResistorList(r4s));
			txtR5_E12.setToolTipText(formatResistorList(r5s));

			// E24 series
			double[] fsE24 = pc.createFullSeries(VNAPadConstants.E24Factors, 6);
			r3s = pc.calculateSeriesCircuit(fsE24, r3, numResistors, 0.01);
			r4s = pc.calculateSeriesCircuit(fsE24, r4, numResistors, 0.01);
			r5s = pc.calculateSeriesCircuit(fsE24, r5, numResistors, 0.01);

			pp = new VNAPiPad();
			pp.setR1(r1);
			pp.setR2(r2);
			pp.setR3(getResistorSum(r3s));
			pp.setR4(getResistorSum(r4s));
			pp.setR5(getResistorSum(r5s));
			pc.setPad(pp);
			pc.reverseCalcPad(atten);

			txtR1_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR1()));
			txtR2_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR2()));
			txtR3_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR3()));
			txtR4_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR4()));
			txtR5_E24.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR5()));
			txtR3_E24.setToolTipText(formatResistorList(r3s));
			txtR4_E24.setToolTipText(formatResistorList(r4s));
			txtR5_E24.setToolTipText(formatResistorList(r5s));

			// E48 series
			double[] fsE48 = pc.createFullSeries(VNAPadConstants.E48Factors, 6);
			r3s = pc.calculateSeriesCircuit(fsE48, r3, numResistors, 0.001);
			r4s = pc.calculateSeriesCircuit(fsE48, r4, numResistors, 0.001);
			r5s = pc.calculateSeriesCircuit(fsE48, r5, numResistors, 0.001);

			pp = new VNAPiPad();
			pp.setR1(r1);
			pp.setR2(r2);
			pp.setR3(getResistorSum(r3s));
			pp.setR4(getResistorSum(r4s));
			pp.setR5(getResistorSum(r5s));
			pc.setPad(pp);
			pc.reverseCalcPad(atten);

			txtR1_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR1()));
			txtR2_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR2()));
			txtR3_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR3()));
			txtR4_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR4()));
			txtR5_E48.setText(VNAFormatFactory.getResistanceFormat().format(pp.getR5()));
			txtR3_E48.setToolTipText(formatResistorList(r3s));
			txtR4_E48.setToolTipText(formatResistorList(r4s));
			txtR5_E48.setToolTipText(formatResistorList(r5s));
		} else {
			txtR1_E12.setText("");
			txtR2_E12.setText("");
			txtR3_E12.setText("");
			txtR4_E12.setText("");
			txtR5_E12.setText("");

			txtR1_E24.setText("");
			txtR2_E24.setText("");
			txtR3_E24.setText("");
			txtR4_E24.setText("");
			txtR5_E24.setText("");

			txtR1_E48.setText("");
			txtR2_E48.setText("");
			txtR3_E48.setText("");
			txtR4_E48.setText("");
			txtR5_E48.setText("");
		}
		TraceHelper.exit(this, "doCalculate");
	}

	/**
	 * @param r3s
	 * @return
	 */
	private String formatResistorList(List<Double> rs) {
		String rc = "";
		for (Iterator<Double> it = rs.iterator(); it.hasNext();) {
			Double r = it.next();
			rc += VNAFormatFactory.getResistanceFormat().format(r.doubleValue());
			if (it.hasNext()) {
				rc += " + ";
			}
		}
		return rc;
	}

	/**
	 * @param r3s
	 * @return
	 */
	private double getResistorSum(List<Double> rs) {
		double rc = 0;
		TraceHelper.entry(this, "getResistorSum");
		for (Iterator<Double> it = rs.iterator(); it.hasNext();) {
			Double r = it.next();

			rc += r.doubleValue();
		}

		TraceHelper.exit(this, "getResistorSum");
		return rc;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doCANCEL()
	 */
	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doCANCEL");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		rbPi.setSelected(true);
		doSwitchPadType();
		addEscapeKey();
		doDialogShow();
		TraceHelper.exit(this, "doInit");
	}
}
