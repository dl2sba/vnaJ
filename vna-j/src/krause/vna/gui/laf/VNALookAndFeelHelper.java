package krause.vna.gui.laf;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.bulenkov.darcula.DarculaLaf;
import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import com.jgoodies.looks.plastic.theme.DarkStar;

import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;

/**
 * https://alvinalexander.com/java/java-uimanager-color-keys-list/
 * https://www.formdev.com/flatlaf/themes/
 * 
 * @author dietmar
 *
 */
public class VNALookAndFeelHelper {
	private static final VNAConfig config = VNAConfig.getSingleton();

	public void setThemeBasedOnConfig() throws UnsupportedLookAndFeelException {
		final String methodName = "setThemeBasedOnConfig";
		TraceHelper.entry(this, methodName);
		int theme = config.getThemeID();
		TraceHelper.text(this, methodName, "id=%d", theme);

		switch (theme) {
		case 0:
			PlasticLookAndFeel.setHighContrastFocusColorsEnabled(true);
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
			break;
		case 1:
			PlasticLookAndFeel.setPlasticTheme(new DarkStar());
			PlasticLookAndFeel.setHighContrastFocusColorsEnabled(true);
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
			break;
		case 2:
			UIManager.setLookAndFeel(new DarculaLaf());
			break;
		case 3:
			UIManager.setLookAndFeel(new FlatDarkLaf());
			break;
		case 4:
			UIManager.setLookAndFeel(new FlatLightLaf());
			break;
		case 5:
			UIManager.setLookAndFeel(new FlatIntelliJLaf());
			break;
		case 6:
			UIManager.setLookAndFeel(new FlatDarculaLaf());
			break;
		default:
			break;
		}
		TraceHelper.text(this, methodName, "L&F class=[%s]", UIManager.getLookAndFeel().getClass().getName());
	}

	public VNALookAndFeelEntry[] getThemeList() {
		return new VNALookAndFeelEntry[] {
				new VNALookAndFeelEntry(0, "Standard"),
				new VNALookAndFeelEntry(1, "DarkStar"),
				new VNALookAndFeelEntry(2, "DarculaLaf"),
				new VNALookAndFeelEntry(3, "FlatDarkLaf"),
				new VNALookAndFeelEntry(4, "FlatLightLaf"),
				new VNALookAndFeelEntry(5, "FlatIntelliJLaf"),
				new VNALookAndFeelEntry(6, "FlatDarculaLaf"),
		};
	} 
}
