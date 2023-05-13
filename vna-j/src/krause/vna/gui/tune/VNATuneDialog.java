package krause.vna.gui.tune;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;

import org.jfree.ui.FontChooserDialog;

import krause.common.TypedProperties;
import krause.common.gui.KrauseDialog;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.gui.format.VNAFormatFactory;
import krause.vna.gui.panels.marker.VNAMarker;
import krause.vna.resources.VNAMessages;
import net.miginfocom.swing.MigLayout;

public class VNATuneDialog extends KrauseDialog {
	private final static int DEF_WIDTH = 750;
	private final static int DEF_HEIGHT = 350;
	private final static int DEF_FNT_SIZE = 80;
	private final static String DEF_FNT_NAME = "MS UI Gothic";
	private Font initialFont = new Font(DEF_FNT_NAME, Font.PLAIN, DEF_FNT_SIZE);

	private JTextField txtGreenYellow;
	private JTextField txtYellowRed;

	private VNAMarker marker = null;
	private TypedProperties config = VNAConfig.getSingleton();
	private double limitYellow = 200;
	private double limitRed = 300;
	private JLabel lblSwrVal;
	private JLabel lblFrqVal;
	private JLabel lblSwrTxt;
	private JLabel lblFrqTxt;
	private JButton btFntSelect;

	@Override
	public void dispose() {
		TraceHelper.entry(this, "dispose");

		String confKey = "BigSWR." + marker.getName();
		config.storeWindowPosition(confKey, this);
		config.storeWindowSize(confKey, this);
		config.putDouble(confKey + ".Yellow", limitYellow);
		config.putDouble(confKey + ".Red", limitRed);

		//
		config.putInteger(confKey + ".FontSize", initialFont.getSize());
		config.put(confKey + ".FontName", initialFont.getFontName());

		super.dispose();
		TraceHelper.exit(this, "dispose");
	}

	public VNATuneDialog(VNAMarker pMarker) {
		super(false);
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		marker = pMarker;

		getContentPane().setLayout(new MigLayout("", "[left][grow,right]", "[grow][grow][]"));
		setTitle(MessageFormat.format(VNAMessages.getString("VNATuneDialog.title"), marker.getName()));

		//
		getContentPane().setBackground(Color.BLACK);
		//
		// frq
		lblFrqTxt = new JLabel(VNAMessages.getString("VNATuneDialog.frq"));
		lblFrqTxt.setForeground(Color.WHITE);
		getContentPane().add(lblFrqTxt, "");

		lblFrqVal = new JLabel("1");
		lblFrqVal.setForeground(Color.WHITE);
		getContentPane().add(lblFrqVal, "wrap");

		// swr
		lblSwrTxt = new JLabel(VNAMessages.getString("VNATuneDialog.swr"));
		lblSwrTxt.setForeground(Color.WHITE);
		getContentPane().add(lblSwrTxt, "");

		lblSwrVal = new JLabel("2");
		lblSwrVal.setForeground(Color.WHITE);
		getContentPane().add(lblSwrVal, "wrap");

		// settings
		JPanel panel_2 = new JPanel();
		getContentPane().add(panel_2, "span 2,grow,wrap");
		panel_2.setLayout(new FlowLayout());

		panel_2.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), VNAMessages.getString("VNATuneDialog.group"), TitledBorder.LEADING, TitledBorder.TOP, null,
				new Color(0, 0, 0)));

		JLabel lblSwrgreen = new JLabel(VNAMessages.getString("VNATuneDialog.gy"));
		panel_2.add(lblSwrgreen);

		txtGreenYellow = new JTextField();
		txtGreenYellow.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				fields2Limits();
			}
		});
		panel_2.add(txtGreenYellow);
		txtGreenYellow.setColumns(6);

		JLabel lblYellowred = new JLabel(VNAMessages.getString("VNATuneDialog.yr"));
		panel_2.add(lblYellowred);

		txtYellowRed = new JTextField();
		txtYellowRed.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				fields2Limits();
			}
		});
		panel_2.add(txtYellowRed);
		txtYellowRed.setColumns(6);

		btFntSelect = new JButton(VNAMessages.getString("VNATuneDialog.btFntSelect.text")); //$NON-NLS-1$
		btFntSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				doSelectFont();
			}
		});
		panel_2.add(btFntSelect);

		getContentPane().addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent arg0) {
				Component c = arg0.getComponent();
				doResizeFonts(c);

			}
		});

		doDialogInit();
	}

	protected void doResizeFonts(Component c) {
		TraceHelper.entry(this, "doResizeFonts");
		float relX = c.getWidth() / (DEF_WIDTH * 1.0f);
		float relY = c.getHeight() / (DEF_HEIGHT * 1.0f);

		float fact = Math.min(relX, relY);
		float newFntSize = DEF_FNT_SIZE * fact;
		lblFrqVal.setFont(initialFont.deriveFont(newFntSize));
		lblSwrVal.setFont(initialFont.deriveFont(newFntSize));
		lblFrqTxt.setFont(initialFont.deriveFont(newFntSize));
		lblSwrTxt.setFont(initialFont.deriveFont(newFntSize));
		TraceHelper.exit(this, "doResizeFonts");
	}

	protected void doSelectFont() {
		TraceHelper.entry(this, "doSelectFont");
		FontChooserDialog fcs = new FontChooserDialog(this, VNAMessages.getString("VNATuneDialog.fontSelDialog"), true, initialFont);
		fcs.pack();
		fcs.setVisible(true);
		initialFont = fcs.getSelectedFont();
		doResizeFonts(getContentPane());

		TraceHelper.exit(this, "doSelectFont");
	}

	protected void fields2Limits() {
		TraceHelper.entry(this, "fields2Limits");

		try {
			limitYellow = VNAFormatFactory.getSwrFormat().parse(txtGreenYellow.getText()).doubleValue();
			limitRed = VNAFormatFactory.getSwrFormat().parse(txtYellowRed.getText()).doubleValue();
			limits2Fields();
		} catch (ParseException e) {
			Toolkit.getDefaultToolkit().beep();
		}
		TraceHelper.exit(this, "fields2Limits");
	}

	@Override
	protected void doDialogCancel() {
		TraceHelper.entry(this, "doCANCEL");
		TraceHelper.exit(this, "doCANCEL");
	}

	@Override
	protected void doDialogInit() {
		TraceHelper.entry(this, "doInit");
		String confKey = "BigSWR." + marker.getName();

		//
		int fntSize = config.getInteger(confKey + ".FontSize", DEF_FNT_SIZE);
		String fntName = config.getProperty(confKey + ".FontName", DEF_FNT_NAME);
		initialFont = new Font(fntName, Font.PLAIN, fntSize);

		//
		config.restoreWindowPosition(confKey, this, new Point(10, 10));
		limitYellow = config.getDouble(confKey + ".Yellow", 2.0);
		limitRed = config.getDouble(confKey + ".Red", 3.0);
		limits2Fields();
		update(marker.getSample());

		pack();
		config.restoreWindowSize(confKey, this, new Dimension(810, 345));
		setVisible(true);
		TraceHelper.exit(this, "doInit");
	}

	private void limits2Fields() {
		TraceHelper.entry(this, "limits2Fields");
		txtGreenYellow.setText(VNAFormatFactory.getSwrFormat().format(limitYellow));
		txtYellowRed.setText(VNAFormatFactory.getSwrFormat().format(limitRed));
		TraceHelper.exit(this, "limits2Fields");
	}

	public void update(VNACalibratedSample markerSample) {
		TraceHelper.entry(this, "update");

		lblFrqVal.setText(VNAFormatFactory.getFrequencyFormat().format(markerSample.getFrequency()));

		double swr = markerSample.getSWR();
		lblSwrVal.setText(VNAFormatFactory.getSwrFormat().format(swr) + ":1");
		if (swr > limitRed) {
			lblSwrVal.setForeground(Color.RED);
			lblFrqVal.setForeground(Color.RED);
		} else if (swr > limitYellow) {
			lblSwrVal.setForeground(Color.YELLOW);
			lblFrqVal.setForeground(Color.YELLOW);
		} else {
			lblSwrVal.setForeground(Color.GREEN);
			lblFrqVal.setForeground(Color.GREEN);
		}

		TraceHelper.exit(this, "update");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * krause.common.gui.KrauseDialog#windowClosing(java.awt.event.WindowEvent)
	 */
	@Override
	public void windowClosing(WindowEvent e) {
		TraceHelper.entry(this, "windowClosing");
		marker.doClickOnBigSWRSymbol();
		// String title =
		// MessageFormat.format(VNAMessages.getString("VNATuneDialog.infoClose.title"),
		// marker.getName());
		// String msg =
		// MessageFormat.format(VNAMessages.getString("VNATuneDialog.infoClose.text"),
		// marker.getName());
		// JOptionPane.showMessageDialog(this, msg, title,
		// JOptionPane.INFORMATION_MESSAGE);
		TraceHelper.exit(this, "windowClosing");
	}
}
