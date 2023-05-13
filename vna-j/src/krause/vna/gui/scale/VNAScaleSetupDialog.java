/**
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 */
package krause.vna.gui.scale;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.gui.HelpButton;
import krause.vna.gui.VNAMainFrame;
import krause.vna.gui.scale.VNAScaleSymbols.SCALE_TYPE;
import krause.vna.gui.util.SwingUtil;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNAScaleSetupDialog extends KrauseDialog implements ActionListener {
	private final JPanel contentPanel;

	private VNAConfig config = VNAConfig.getSingleton();
	private JButton btnSave;
	private JButton btnCancel;

	private transient ArrayList<VNAGenericScale> lstScales = new ArrayList<>();

	/**
	 * Create the dialog.
	 */
	public VNAScaleSetupDialog(VNAMainFrame pMainFrame) {
		super(pMainFrame.getJFrame(), true);
		final String methodName = "VNAScaleSetupDialog";
		TraceHelper.entry(this, methodName);

		setTitle(VNAMessages.getString("VNAScaleSetupDialog.Title")); //$NON-NLS-1$
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setModal(true);

		setProperties(config);
		setConfigurationPrefix(methodName);
		setMinimumSize(new Dimension(470, 210));
		setPreferredSize(new Dimension(470, 260));

		contentPanel = new JPanel();
		contentPanel.setLayout(new MigLayout("", "[grow,fill][30%][][30%][]", "[grow,fill][][]"));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);

		JLabel lbl = new JLabel(VNAMessages.getString("VNAScaleSetupDialog.Scale"));
		contentPanel.add(lbl, "span 5, wrap");

		lbl = new JLabel();
		contentPanel.add(lbl, "");

		lbl = new JLabel(VNAMessages.getString("VNAScaleSetupDialog.Min"));
		contentPanel.add(lbl, "span 2");

		lbl = new JLabel(VNAMessages.getString("VNAScaleSetupDialog.Max"));
		contentPanel.add(lbl, "span 2, wrap");

		lstScales.add(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RS));
		lstScales.add(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_XS));
		lstScales.add(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_Z_ABS));
		lstScales.add(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_RSS));
		lstScales.add(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_SWR));
		lstScales.add(VNAScaleSymbols.MAP_SCALE_TYPES.get(SCALE_TYPE.SCALE_THETA));

		for (VNAGenericScale aScale : lstScales) {
			createScaleLine(aScale);
		}
		//
		contentPanel.add(new HelpButton(this, methodName), "grow");
		btnSave = SwingUtil.createJButton("Button.Save", this);
		btnCancel = SwingUtil.createJButton("Button.Cancel", this);

		contentPanel.add(btnCancel, "span 2, grow");
		btnSave.setActionCommand("OK");
		contentPanel.add(btnSave, "span 2, grow");
		getRootPane().setDefaultButton(btnSave);
		doDialogInit();
		TraceHelper.exit(this, methodName);
	}

	private void createScaleLine(VNAGenericScale aScale) {
		NumberFormat fmt = aScale.getFormat();
		JLabel lbl = new JLabel(aScale.getName());
		contentPanel.add(lbl, "");

		VNAScaleTextField txtMin = new VNAScaleTextField(fmt.format(aScale.getDefaultMinValue()), aScale);
		txtMin.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMin.setBorder(new LineBorder(new Color(171, 173, 179)));
		txtMin.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				VNAScaleTextField fld = (VNAScaleTextField) arg0.getSource();
				VNAGenericScale s = fld.getScale();
				String txt = fld.getText();
				NumberFormat ft = s.getFormat();
				try {
					Number val = ft.parse(txt);
					double d = val.doubleValue();
					if (d < s.getAbsolutMinValue()) {
						d = s.getAbsolutMinValue();
					}
					s.setDefaultMinValue(d);
				} catch (ParseException e) {
					// nfa
				}
				fld.setText(ft.format(s.getDefaultMinValue()));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// nfa
			}
		});
		contentPanel.add(txtMin, "grow");

		lbl = new JLabel("(" + fmt.format(aScale.getAbsolutMinValue()) + ")");
		contentPanel.add(lbl, "right");

		VNAScaleTextField txtMax = new VNAScaleTextField(fmt.format(aScale.getDefaultMaxValue()), aScale);
		txtMax.setHorizontalAlignment(SwingConstants.RIGHT);
		txtMax.setBorder(new LineBorder(new Color(171, 173, 179)));
		txtMax.addFocusListener(new FocusListener() {

			@Override
			public void focusLost(FocusEvent arg0) {
				VNAScaleTextField fld = (VNAScaleTextField) arg0.getSource();
				VNAGenericScale s = fld.getScale();
				String txt = fld.getText();
				NumberFormat ft = s.getFormat();
				try {
					Number val = ft.parse(txt);
					double d = val.doubleValue();
					if (d > s.getAbsolutMaxValue()) {
						d = s.getAbsolutMaxValue();
					}
					s.setDefaultMaxValue(d);
				} catch (ParseException e) {
					// nfa
				}
				fld.setText(ft.format(s.getDefaultMaxValue()));
			}

			@Override
			public void focusGained(FocusEvent arg0) {
				// nfa
			}
		});
		contentPanel.add(txtMax, "grow");

		lbl = new JLabel("(" + fmt.format(aScale.getAbsolutMaxValue()) + ")");
		contentPanel.add(lbl, "right, wrap");

		txtMax.setMinField(txtMin);
		txtMin.setMinField(txtMax);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see krause.common.gui.KrauseDialog#doInit()
	 */
	protected void doDialogInit() {
		addEscapeKey();
		doDialogShow();
	}

	/*
	 * 
	 */
	protected void doSave() {
		TraceHelper.entry(this, "doSave");
		//
		for (VNAGenericScale aScale : lstScales) {
			config.putDouble(aScale.getClass().getSimpleName() + ".defaultMinValue", aScale.getDefaultMinValue());
			config.putDouble(aScale.getClass().getSimpleName() + ".defaultMaxValue", aScale.getDefaultMaxValue());
		}

		//
		setVisible(false);
		dispose();
		TraceHelper.exit(this, "doSave");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		TraceHelper.entry(this, "actionPerformed");
		if (e.getSource() == btnCancel) {
			doDialogCancel();
		} else if (e.getSource() == btnSave) {
			doSave();
		}
		TraceHelper.exit(this, "actionPerformed");
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

}
