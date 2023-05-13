package krause.vna.gui;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.MessageFormat;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.jfree.chart.JFreeChart;
import org.jfree.ui.ExtensionFileFilter;

import krause.common.exception.ProcessingException;
import krause.util.GlobalSymbols;
import krause.util.StringHelper;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNAConfig;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrationkit.VNACalibrationKit;
import krause.vna.data.helper.VNACalibrationBlockHelper;
import krause.vna.data.presets.VNAPresetHelper;
import krause.vna.device.IVNADriver;
import krause.vna.device.IVNAFlashableDevice;
import krause.vna.device.VNADeviceInfoBlock;
import krause.vna.device.VNADriverFactory;
import krause.vna.export.CSVExporter;
import krause.vna.export.JpegExporter;
import krause.vna.export.PDFExporter;
import krause.vna.export.SnPExporter;
import krause.vna.export.VNAExporter;
import krause.vna.export.XLSExporter;
import krause.vna.export.ZPlotsExporter;
import krause.vna.firmware.VNAFirmwareUpdateDialog;
import krause.vna.gui.about.VNAAboutDialog;
import krause.vna.gui.analyse.VNADataAnalysisDialog;
import krause.vna.gui.beacon.VNABeaconDialog;
import krause.vna.gui.cable.VNACableLengthDialog;
import krause.vna.gui.cable.VNACableLossDialog;
import krause.vna.gui.calibrate.VNACalibrationDialog;
import krause.vna.gui.calibrate.VNACalibrationLoadDialog;
import krause.vna.gui.calibrate.calibrationkit.VNACalibrationKitDialog;
import krause.vna.gui.calibrate.frequency.VNAFrequencyCalibrationDialog;
import krause.vna.gui.config.VNAColorConfigDialog;
import krause.vna.gui.config.VNAConfigEditDialog;
import krause.vna.gui.config.VNAConfigLanguageDialog;
import krause.vna.gui.driver.VNADriverConfigDialog;
import krause.vna.gui.export.VNAAutoExportSettingsDialog;
import krause.vna.gui.export.VNAExportCommentDialog;
import krause.vna.gui.export.VNAExportSettingsDialog;
import krause.vna.gui.fft.VNAFFTDataDetailsDialog;
import krause.vna.gui.filter.VNAGaussianFilterCreatorDialog;
import krause.vna.gui.menu.VNAMenuBar;
import krause.vna.gui.multiscan.VNAMultiScanWindow;
import krause.vna.gui.padcalc.VNAPadCalculatorDialog;
import krause.vna.gui.raw.VNARawHandler;
import krause.vna.gui.readme.VNALicenseDialog;
import krause.vna.gui.readme.VNAReadmeDialog;
import krause.vna.gui.scale.VNAScaleSetupDialog;
import krause.vna.gui.scheduler.VNASchedulerDialog;
import krause.vna.gui.scollector.VNASCollectorDialog;
import krause.vna.gui.toolbar.VNAToolbar;
import krause.vna.gui.update.VNAUpdateDialog;
import krause.vna.resources.VNAMessages;

public class VNAMenuAndToolbarHandler implements ActionListener {
	private static VNAConfig config = VNAConfig.getSingleton();
	private VNADataPool datapool = VNADataPool.getSingleton();

	private VNAMainFrame mainFrame = null;
	private VNAMenuBar menubar = null;
	private VNAToolbar toolbar = null;

	/**
	 * @wbp.parser.entryPoint
	 */
	public VNAMenuAndToolbarHandler(VNAMainFrame pMainFrame) {
		mainFrame = pMainFrame;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		TraceHelper.entry(this, "actionPerformed", cmd); //$NON-NLS-1$

		if (VNAMessages.getString("Menu.File.Exit.Command").equals(cmd)) { //$NON-NLS-1$
			mainFrame.doShutdown();
		} else if (VNAMessages.getString("Menu.Tools.Generator.Command").equals(cmd)) { //$NON-NLS-1$
			doShowGeneratorDialog();
		} else if (VNAMessages.getString("Menu.Tools.Padcalc.Command").equals(cmd)) { //$NON-NLS-1$
			doShowPadCalcDialog();
		} else if (VNAMessages.getString("Menu.Tools.CableLoss.Command").equals(cmd)) { //$NON-NLS-1$
			if (datapool.getMainCalibrationBlock().getScanMode().isReflectionMode()) {
				new VNACableLossDialog(mainFrame.getJFrame());
			} else {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.OnlyInReflectionMode.msg"), VNAMessages.getString("Message.OnlyInReflectionMode.title"), JOptionPane.WARNING_MESSAGE);
			}
		} else if (VNAMessages.getString("Menu.Help.About.Command").equals(cmd)) { //$NON-NLS-1$
			doAboutDialog();
		} else if (VNAMessages.getString("Menu.Help.Readme.Command").equals(cmd)) { //$NON-NLS-1$
			new VNAReadmeDialog(mainFrame);
		} else if (VNAMessages.getString("Menu.Help.License.Command").equals(cmd)) { //$NON-NLS-1$
			new VNALicenseDialog(mainFrame);
		} else if (VNAMessages.getString("Menu.Help.Support.Command").equals(cmd)) { //$NON-NLS-1$
			doCopySupportInformation2Clipboard();
		} else if (VNAMessages.getString("Menu.Export.Setting.Command").equals(cmd)) { //$NON-NLS-1$
			new VNAExportSettingsDialog(mainFrame);
		} else if (VNAMessages.getString("Menu.Export.AutoSetting.Command").equals(cmd)) { //$NON-NLS-1$
			new VNAAutoExportSettingsDialog(mainFrame);
		} else if (VNAMessages.getString("Menu.Analyser.Setup.Command").equals(cmd)) { //$NON-NLS-1$
			doAnalyserSetup();
		} else if (VNAMessages.getString("Menu.Analyser.Info.Command").equals(cmd)) { //$NON-NLS-1$
			doShowDriverInfo();
		} else if (VNAMessages.getString("Menu.Analyser.Reconnect.Command").equals(cmd)) { //$NON-NLS-1$
			doDriverReconnect();
		} else if (VNAMessages.getString("Menu.Tools.Gaussian.Command").equals(cmd)) { //$NON-NLS-1$
			doShowGaussianCalculator();
		} else if (VNAMessages.getString("Menu.Tools.FFT.Command").equals(cmd)) { //$NON-NLS-1$
			if (datapool.getMainCalibrationBlock().getScanMode().isReflectionMode()) {
				new VNAFFTDataDetailsDialog(mainFrame.getJFrame());
			} else {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.OnlyInReflectionMode.msg"), VNAMessages.getString("Message.OnlyInReflectionMode.title"), JOptionPane.WARNING_MESSAGE);
			}
		} else if (VNAMessages.getString("Menu.Tools.Beacon.Command").equals(cmd)) { //$NON-NLS-1$
			doBeacon();
		} else if (VNAMessages.getString("Menu.Schedule.Execute.Command").equals(cmd)) { //$NON-NLS-1$
			doScheduler();
		} else if (VNAMessages.getString("Menu.Export.S2P.Command").equals(cmd)) { //$NON-NLS-1$
			doExportSnP();
		} else if (VNAMessages.getString("Menu.Export.S2PCollector.Command").equals(cmd)) { //$NON-NLS-1$
			doShowS2PCollector();
		} else if (VNAMessages.getString("Menu.Export.XLS.Command").equals(cmd)) { //$NON-NLS-1$
			doExportXLS(e);
		} else if (VNAMessages.getString("Menu.Export.XML.Command").equals(cmd)) { //$NON-NLS-1$
			doExportXML();
		} else if (VNAMessages.getString("Menu.Raw.Write.Command").equals(cmd)) { //$NON-NLS-1$
			doExportXML();
		} else if (VNAMessages.getString("Menu.Export.CSV.Command").equals(cmd)) { //$NON-NLS-1$
			doExportCSV();
		} else if (VNAMessages.getString("Menu.Export.ZPlot.Command").equals(cmd)) { //$NON-NLS-1$
			doExportZPlots();
		} else if (VNAMessages.getString("Menu.Export.JPG.Command").equals(cmd)) { //$NON-NLS-1$
			doExportJPG(e);
		} else if (VNAMessages.getString("Menu.Export.PDF.Command").equals(cmd)) { //$NON-NLS-1$
			doExportPDF(e);
		} else if (VNAMessages.getString("Menu.File.Settings.Command").equals(cmd)) { //$NON-NLS-1$
			doConfigDialog();
		} else if (VNAMessages.getString("Menu.Tools.Cablelength.Command").equals(cmd)) { //$NON-NLS-1$
			if (datapool.getMainCalibrationBlock().getScanMode().isReflectionMode()) {
				new VNACableLengthDialog(mainFrame);
			} else {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.OnlyInReflectionMode.msg"), VNAMessages.getString("Message.OnlyInReflectionMode.title"), JOptionPane.WARNING_MESSAGE);
			}
		} else if (VNAMessages.getString("Menu.Tools.Firmware.Command").equals(cmd)) { //$NON-NLS-1$
			doUploadFirmware();
		} else if (VNAMessages.getString("Menu.Calibration.Calibrate.Command").equals(cmd)) {
			doCalibrationDialog();
		} else if (VNAMessages.getString("Menu.Calibration.CalibrationSet.Command").equals(cmd)) {
			new VNACalibrationKitDialog(mainFrame);
		} else if (VNAMessages.getString("Menu.Calibration.Load.Command").equals(cmd)) {
			doCalibrationLoad();
		} else if (VNAMessages.getString("Menu.Calibration.Import.Command").equals(cmd)) {
			doCalibrationImport();
		} else if (VNAMessages.getString("Menu.Calibration.Export.Command").equals(cmd)) {
			doCalibrationExport();
		} else if (VNAMessages.getString("Menu.Calibration.Frequency.Command").equals(cmd)) {
			doFrequencyCalibration();
		} else if (VNAMessages.getString("Menu.File.Color.Command").equals(cmd)) {
			if ((e.getModifiers() & ActionEvent.CTRL_MASK) > 0) {
				mainFrame.getMenubar().add(mainFrame.getMenubar().createExperimentalMenu());
				mainFrame.getMenubar().revalidate();
			} else {
				doColorConfig();
			}
		} else if (VNAMessages.getString("Menu.File.Language.Command").equals(cmd)) {
			doLanguageConfig();
		} else if (VNAMessages.getString("Menu.Analysis.Command").equals(cmd)) { //$NON-NLS-1$
			doAnalysis();
		} else if (VNAMessages.getString("Menu.Multitune.Command").equals(cmd)) { //$NON-NLS-1$
			doMultiScan();
		} else if (VNAMessages.getString("Menu.Update.Command").equals(cmd)) { //$NON-NLS-1$
			docCheckForUpdates();
		} else if (VNAMessages.getString("Menu.Presets.Load.Command").equals(cmd)) { //$NON-NLS-1$
			new VNAPresetHelper(mainFrame).doLoadPresets();
		} else if (VNAMessages.getString("Menu.Presets.Save.Command").equals(cmd)) { //$NON-NLS-1$
			new VNAPresetHelper(mainFrame).doSavePresets();
		} else if (VNAMessages.getString("Menu.Analyser.Single.Command").equals(cmd)) { //$NON-NLS-1$
			mainFrame.getDataPanel().startSingleScan();
		} else if (VNAMessages.getString("Menu.Analyser.Free.Command").equals(cmd)) { //$NON-NLS-1$
			mainFrame.getDataPanel().startFreeRun();
		} else if (VNAMessages.getString("Menu.File.SettingsScales.Command").equals(cmd)) { //$NON-NLS-1$
			new VNAScaleSetupDialog(mainFrame);
		} else if (VNAMessages.getString("Menu.Experimental.A.Command").equals(cmd)) { //$NON-NLS-1$
		} else if (VNAMessages.getString("Menu.Experimental.B.Command").equals(cmd)) { //$NON-NLS-1$
		}

		TraceHelper.exit(this, "actionPerformed"); //$NON-NLS-1$
	}

	public void doShowGaussianCalculator() {
		new VNAGaussianFilterCreatorDialog(mainFrame.getJFrame());
	}

	public void doAboutDialog() {
		new VNAAboutDialog(mainFrame);
	}

	private void doAnalyserSetup() {
		TraceHelper.entry(this, "doAnalyserSetup");
		new VNADriverConfigDialog(mainFrame);
		TraceHelper.exit(this, "doAnalyserSetup");
	}

	private void doAnalysis() {
		TraceHelper.entry(this, "doAnalysis");
		new VNADataAnalysisDialog(mainFrame);
		TraceHelper.exit(this, "doAnalysis");
	}

	/**
	 * 
	 */
	public void doCalibrationDialog() {
		TraceHelper.entry(this, "doCalibrationDialog"); //$NON-NLS-1$

		// is a scanmode selected ?
		if (datapool.getScanMode() != null) {
			// yes
			// show dialog
			final VNACalibrationDialog sDlg = new VNACalibrationDialog(mainFrame);

			// dialog with valid data exited ?
			if (sDlg.isDataValid()) {
				// yes
				// calculate cal temp, as this is usually done during cal block
				// loading
				sDlg.getCalibration().calculateCalibrationTemperature();

				// make it the main calblock
				mainFrame.setMainCalibrationBlock(sDlg.getCalibration());
			}

			// remove the dialog
			sDlg.dispose();
		} else {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), JOptionPane.WARNING_MESSAGE);

		}
		TraceHelper.exit(this, "doCalibrationDialog"); //$NON-NLS-1$
	}

	private void doCalibrationExport() {
		TraceHelper.entry(this, "doCalibrationExport");
		if (datapool.getScanMode() != null) {
			final VNACalibrationBlock blk = datapool.getMainCalibrationBlock();

			if (blk != null) {
				String fn = null;
				if (blk.getFile() != null) {
					fn = blk.getFile().getName();
				}
				if (fn == null) {
					final StringBuilder sb = new StringBuilder();
					sb.append(blk.getScanMode().shortText());
					sb.append("_");
					sb.append(datapool.getDriver().getDeviceInfoBlock().getShortName());
					sb.append(".cal");
					fn = sb.toString();
				}

				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.setFileFilter(new ExtensionFileFilter("vna/J calibration files (*.cal)", ".cal"));
				fc.setSelectedFile(new File(config.getCalibrationExportDirectory() + System.getProperty("file.separator") + fn));
				int returnVal = fc.showSaveDialog(mainFrame.getJFrame());

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = fc.getSelectedFile();
					if (file.exists()) {
						String msg = MessageFormat.format(VNAMessages.getString("VNACalibrationSaveDialog.fileExists.2"), config.getCalibrationExportDirectory(), fn);
						Object[] options = {
								VNAMessages.getString("Button.Overwrite"),
								VNAMessages.getString("Button.Cancel"),
						};
						int n = JOptionPane.showOptionDialog(mainFrame.getJFrame(), msg, VNAMessages.getString("VNACalibrationSaveDialog.fileExists.1"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						if (n == 0) {
							VNACalibrationBlockHelper.save(blk, file.getAbsolutePath());
						}
					} else {
						VNACalibrationBlockHelper.save(blk, file.getAbsolutePath());
					}
					config.setCalibrationExportDirectory(file.getParentFile().getAbsolutePath());
				}
			} else {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.NoCalPresent.1"), VNAMessages.getString("Message.NoCalPresent.2"), JOptionPane.WARNING_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), JOptionPane.WARNING_MESSAGE);
		}

		TraceHelper.exit(this, "doCalibrationExport");
	}

	private void doCalibrationImport() {
		TraceHelper.entry(this, "doCalibrationImport");
		if (datapool.getScanMode() != null) {
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new ExtensionFileFilter("vna/J calibration files (*.cal)", ".cal"));
			fc.setSelectedFile(new File(config.getCalibrationExportDirectory() + System.getProperty("file.separator") + "."));
			int returnVal = fc.showOpenDialog(mainFrame.getJFrame());

			boolean doImport = false;

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File importFile = fc.getSelectedFile();
				String importPath = importFile.getParentFile().getAbsolutePath();
				String importFilename = importFile.getName();

				// check if same files exists in calibration directory
				File targetFile = new File(config.getVNACalibrationDirectory() + System.getProperty("file.separator") + importFilename);
				if (targetFile.exists()) {
					String msg = MessageFormat.format(VNAMessages.getString("VNACalibrationSaveDialog.fileExists.2"), config.getVNACalibrationDirectory(), importFilename);
					Object[] options = {
							VNAMessages.getString("Button.Overwrite"),
							VNAMessages.getString("Button.Cancel"),
					};
					int n = JOptionPane.showOptionDialog(mainFrame.getJFrame(), msg, VNAMessages.getString("VNACalibrationSaveDialog.fileExists.1"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
					if (n == 0) {
						doImport = true;
					}
				} else {
					doImport = true;
				}

				// we have a valid source and target file
				if (doImport) {
					// now check, whether this is a good calibration block
					final IVNADriver drv = datapool.getDriver();
					final VNADeviceInfoBlock dib = drv.getDeviceInfoBlock();
					final VNACalibrationKit kit = datapool.getCalibrationKit();

					VNACalibrationBlock blk = null;
					FileChannel channelIn = null;
					FileChannel channelOut = null;

					try {
						// load the source
						blk = VNACalibrationBlockHelper.load(importFile, drv, kit);

						// block matches the analyzer and mode?
						if (blk.blockMatches(dib, datapool.getScanMode())) {
							// yes
							// copy the external file into the calibration
							// directory
							channelIn = new FileInputStream(importFile).getChannel();
							channelOut = new FileOutputStream(targetFile).getChannel();
							channelOut.transferFrom(channelIn, 0, channelIn.size());
							mainFrame.setMainCalibrationBlock(blk);
						}
					} catch (ProcessingException e) {
						JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.ImportCal.1"), VNAMessages.getString("Message.ImportCal.2"), JOptionPane.WARNING_MESSAGE);
					} catch (IOException e) {
						JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.ImportCal.3"), VNAMessages.getString("Message.ImportCal.2"), JOptionPane.WARNING_MESSAGE);
					} finally {
						if (channelIn != null) {
							try {
								channelIn.close();
							} catch (IOException e) {
								// not relevant
							}
						}
						if (channelOut != null) {
							try {
								channelOut.close();
							} catch (IOException e) {
								// not relevant
							}
						}
					}
				}
				config.setCalibrationExportDirectory(importPath);
			}
		} else {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), JOptionPane.WARNING_MESSAGE);
		}
		TraceHelper.exit(this, "doCalibrationImport");
	}

	public void doCalibrationLoad() {
		TraceHelper.entry(this, "doCalibrationLoad");
		if (datapool.getScanMode() != null) {
			final VNACalibrationLoadDialog sDlg = new VNACalibrationLoadDialog(mainFrame.getJFrame());
			final VNACalibrationBlock blk = sDlg.getSelectedCalibrationBlock();
			// we have selected a calibration block?
			if (blk != null) {
				// yes
				mainFrame.setMainCalibrationBlock(blk);
			}
			sDlg.dispose();
		} else {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.NoScanMode.1"), VNAMessages.getString("Message.NoScanMode.2"), JOptionPane.WARNING_MESSAGE);
		}

		TraceHelper.exit(this, "doCalibrationLoad");
	}

	private void docCheckForUpdates() {
		TraceHelper.entry(this, "docCheckForUpdates");
		new VNAUpdateDialog(mainFrame.getJFrame());
		TraceHelper.exit(this, "docCheckForUpdates");
	}

	private void doColorConfig() {
		TraceHelper.entry(this, "doColorConfig");
		VNAColorConfigDialog sDlg;

		sDlg = new VNAColorConfigDialog(mainFrame, mainFrame.getJFrame());
		mainFrame.getDiagramPanel().setupColors();
		mainFrame.getMarkerPanel().setupColors();
		sDlg.dispose();

		TraceHelper.exit(this, "doColorConfig");
	}

	public void doConfigDialog() {
		new VNAConfigEditDialog(mainFrame);
	}

	/**
	 * @param mainFrame2
	 */
	private void doCopySupportInformation2Clipboard() {
		TraceHelper.entry(this, "doCopySupportInformation2Clipboard");
		String rc;
		String[] values = new String[] {
				"Application version....[" + VNAMessages.getString("Application.version") + " " + VNAMessages.getString("Application.date") + "]",
				"Java version...........[" + System.getProperty("java.version") + "]",
				"Java runtime.version...[" + System.getProperty("java.runtime.version") + "]",
				"Java vm.version........[" + System.getProperty("java.vm.version") + "]",
				"Java vm.vendor.........[" + System.getProperty("java.vm.vendor") + "]",
				"OS.....................[" + System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + "]",
				"Country/Language.......[" + Locale.getDefault().getCountry() + "/" + Locale.getDefault().getLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]",
				"                       [" + Locale.getDefault().getDisplayCountry() + "/" + Locale.getDefault().getDisplayLanguage() + "/" + Locale.getDefault().getDisplayVariant() + "]",
				"Analyser ..............[" + datapool.getDriver().getDeviceInfoBlock().getLongName() + "]",
				"User ..................[" + System.getProperty("user.name") + "]",
				"Home ..................[" + System.getProperty("user.home") + "]",
				"Installation directory.[" + config.getInstallationDirectory() + "]",
				"Configuration directory[" + config.getVNAConfigDirectory() + "]",
		};
		rc = StringHelper.array2String(values, GlobalSymbols.LINE_SEPARATOR);
		StringSelection str = new StringSelection(rc);

		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(str, mainFrame);

		JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.Support.text"), VNAMessages.getString("Message.Support.title"), JOptionPane.INFORMATION_MESSAGE);

		TraceHelper.exit(this, "doCopySupportInformation2Clipboard");
	}

	private void doDriverReconnect() {
		TraceHelper.entry(this, "doDriverReconnect");

		// Create and set up the content pane.
		final SimpleProgressPopup spp = new SimpleProgressPopup(mainFrame.getJFrame(), VNAMessages.getString("Message.Reconnect"));
		spp.setTask(new SwingWorker<Void, Void>() {
			/*
			 * Main task. Executed in background thread.
			 */
			@Override
			protected Void doInBackground() throws Exception {
				mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				mainFrame.unloadDriver();
				for (int i = 0; i < 50; i += 10) {
					setProgress(i);
					Thread.sleep(200);
				}
				mainFrame.loadDriver();
				for (int i = 50; i < 100; i += 10) {
					setProgress(i);
					Thread.sleep(200);
				}
				mainFrame.getJFrame().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
				return null;
			}

			@Override
			protected void done() {
				spp.dispose();
			}
		});
		spp.run();

		TraceHelper.exit(this, "doDriverReconnect");
	}

	private void doBeacon() {
		new VNABeaconDialog(mainFrame.getJFrame());
	}

	private void doExportCSV() {
		TraceHelper.entry(this, "doExportCSV");
		doExportJPGFileInternal(new CSVExporter(mainFrame), true);
		TraceHelper.exit(this, "doExportCSV");
	}

	private void doExportJPG(ActionEvent e) {
		TraceHelper.entry(this, "doExportJPG");
		boolean toClipboard = ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
		boolean autoOpen = ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0);

		if (toClipboard) {
			// yes
			// export to clipbooard
			doExportJPGClipboard();
		} else {
			// no
			// simply export to file
			String fileName = doExportJPGFileInternal(new JpegExporter(mainFrame), !autoOpen);
			if (autoOpen && (fileName != null)) {
				try {
					File file = new File(fileName);
					java.awt.Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					ErrorLogHelper.exception(this, "doExportJPG", e1);
				}
			}
		}
		TraceHelper.exit(this, "doExportJPG");
	}

	/**
	 * Export the chart to the clipboard
	 */
	public void doExportJPGClipboard() {
		TraceHelper.entry(this, "doExportJPGClipboard");

		class MyImageSelection implements Transferable {
			private Image img;
			DataFlavor myFlavor = java.awt.datatransfer.DataFlavor.imageFlavor;

			public MyImageSelection(Image awtImg) {
				img = awtImg;
			}

			public synchronized Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
				if (!myFlavor.equals(flavor)) {
					throw new UnsupportedFlavorException(flavor);
				}
				return img;
			}

			public synchronized DataFlavor[] getTransferDataFlavors() {
				return new DataFlavor[] {
						myFlavor
				};
			}

			public boolean isDataFlavorSupported(DataFlavor flavor) {
				return myFlavor.equals(flavor);
			}
		}

		// create a dummy exporter to use functions of base class
		VNAExporter exporter = new VNAExporter(mainFrame) {
			@Override
			public String export(String fnp, boolean overwrite) throws ProcessingException {
				return null;
			}

			@Override
			public String getExtension() {
				return null;
			}
		};

		final VNACalibratedSampleBlock blk = datapool.getCalibratedData();
		final VNACalibratedSample[] samples = blk.getCalibratedSamples();
		final JFreeChart chart = exporter.createChart(samples);
		final Image awtImg = chart.createBufferedImage(config.getExportDiagramWidth(), config.getExportDiagramHeight());

		final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		final MyImageSelection select = new MyImageSelection(awtImg);
		clipboard.setContents(select, mainFrame);

		TraceHelper.exit(this, "doExportJPGClipboard");
	}

	private String doExportJPGFileInternal(VNAExporter exp, boolean showSuccess) {
		TraceHelper.entry(this, "doExportJPGFileInternal");
		String filename = null;
		if (datapool.getCalibratedData() != null) {
			try {
				String fnp = config.getExportDirectory() + System.getProperty("file.separator") + config.getExportFilename();
				filename = exp.export(fnp, config.isExportOverwrite());
				if (showSuccess) {
					JOptionPane.showMessageDialog(mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.3"), filename), VNAMessages.getString("Message.Export.4"), JOptionPane.INFORMATION_MESSAGE);
				}
			} catch (ProcessingException e) {
				JOptionPane.showMessageDialog(mainFrame.getJFrame(), MessageFormat.format(VNAMessages.getString("Message.Export.6"), e.getMessage()), VNAMessages.getString("Message.Export.5"), JOptionPane.ERROR_MESSAGE);
			}
		} else {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.Export.8"), VNAMessages.getString("Message.Export.5"), JOptionPane.ERROR_MESSAGE);
		}
		TraceHelper.exit(this, "doExportJPGFileInternal");
		return filename;
	}

	private void doExportPDF(ActionEvent e) {
		TraceHelper.entry(this, "doExportPDF");
		boolean autoOpen = ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
		boolean autoEdit = ((e.getModifiers() & ActionEvent.CTRL_MASK) != 0);
		boolean goOn = true;
		if (autoEdit) {
			VNAExportCommentDialog dlg = new VNAExportCommentDialog(mainFrame);
			goOn = !dlg.isDialogCancelled();
		}
		if (goOn) {
			String filename = doExportJPGFileInternal(new PDFExporter(mainFrame), !autoOpen);
			if (autoOpen && (filename != null)) {
				try {
					File file = new File(filename);
					java.awt.Desktop.getDesktop().open(file);
				} catch (IOException e1) {
					ErrorLogHelper.exception(this, "doExportPDF", e1);
				}
			}
		}
		TraceHelper.exit(this, "doExportPDF");
	}

	private void doExportSnP() {
		TraceHelper.entry(this, "doExportSnP");
		doExportJPGFileInternal(new SnPExporter(mainFrame), true);
		TraceHelper.exit(this, "doExportSnP");
	}

	private void doExportXLS(ActionEvent e) {
		TraceHelper.entry(this, "doExportXLS");
		boolean autoOpen = ((e.getModifiers() & ActionEvent.SHIFT_MASK) != 0);
		String filename = doExportJPGFileInternal(new XLSExporter(mainFrame), !autoOpen);
		if (autoOpen && (filename != null)) {
			try {
				File file = new File(filename);
				java.awt.Desktop.getDesktop().open(file);
			} catch (IOException e1) {
				ErrorLogHelper.exception(this, "doExportPDF", e1);
			}
		}
		TraceHelper.exit(this, "doExportXLS");
	}

	private void doExportXML() {
		TraceHelper.entry(this, "doExportXML");
		if (datapool.getCalibratedData() != null) {
			new VNARawHandler(mainFrame.getJFrame()).exportMainDiagram();
		} else {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Message.Export.8"), VNAMessages.getString("Message.Export.5"), JOptionPane.ERROR_MESSAGE);
		}
		TraceHelper.exit(this, "doExportXML");
	}

	private void doExportZPlots() {
		TraceHelper.entry(this, "doExportZPlots");
		doExportJPGFileInternal(new ZPlotsExporter(mainFrame), true);
		TraceHelper.exit(this, "doExportZPlots");
	}

	private void doFrequencyCalibration() {
		TraceHelper.entry(this, "doFrequencyCalibration");
		new VNAFrequencyCalibrationDialog(mainFrame, datapool.getDriver());
		TraceHelper.exit(this, "doFrequencyCalibration");
	}

	/**
	 * 
	 */
	private void doLanguageConfig() {
		TraceHelper.entry(this, "doLanguaageConfig");
		new VNAConfigLanguageDialog(mainFrame.getJFrame());
		TraceHelper.exit(this, "doLanguaageConfig");
	}

	private void doMultiScan() {
		TraceHelper.entry(this, "doMultiScan");
		new VNAMultiScanWindow(mainFrame.getJFrame(), mainFrame, mainFrame.getDiagramPanel().getScaleLeft());
		TraceHelper.exit(this, "doMultiScan");
	}

	private void doScheduler() {
		TraceHelper.entry(this, "doScheduler");
		new VNASchedulerDialog(mainFrame.getJFrame(), mainFrame);
		TraceHelper.exit(this, "doScheduler");
	}

	private void doShowDriverInfo() {
		TraceHelper.entry(this, "doShowDriverInfo");
		datapool.getDriver().showDriverDialog(mainFrame);
		TraceHelper.exit(this, "doShowDriverInfo");
	}

	/**
	 * 
	 */
	private void doShowGeneratorDialog() {
		TraceHelper.entry(this, "doShowGeneratorDialog");
		IVNADriver drv = null;
		try {
			drv = VNADriverFactory.getSingleton().getDriverForType(datapool.getDriver().getDeviceInfoBlock().getType());
			drv.showGeneratorDialog(mainFrame);
		} catch (ProcessingException e) {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("NoGeneratorAvailable.2"), VNAMessages.getString("VNAFrequencyCalibrationDialog.title"), JOptionPane.ERROR_MESSAGE);
		}
		TraceHelper.exit(this, "doShowGeneratorDialog");
	}

	/**
	 *  
	 */
	private void doShowPadCalcDialog() {
		TraceHelper.entry(this, "doShowPadCalcDialog");
		new VNAPadCalculatorDialog(mainFrame.getJFrame());
		TraceHelper.exit(this, "doShowPadCalcDialog");
	}

	/**
	 * 
	 */
	private void doShowS2PCollector() {
		TraceHelper.entry(this, "doShowS2PCollector");
		new VNASCollectorDialog();
		TraceHelper.exit(this, "doShowS2PCollector");
	}

	private void doUploadFirmware() {
		IVNADriver driver = datapool.getDriver();
		if (!(driver instanceof IVNAFlashableDevice)) {
			JOptionPane.showMessageDialog(mainFrame.getJFrame(), VNAMessages.getString("Firmware.Update.NotPossible.2"), VNAMessages.getString("Firmware.Update.NotPossible.1"), JOptionPane.ERROR_MESSAGE);
		} else {
			new VNAFirmwareUpdateDialog(mainFrame.getJFrame());
		}
	}

	/**
	 * @return
	 */
	public JFrame getMainFrame() {
		return mainFrame.getJFrame();
	}

	/**
	 * @return the menubar
	 */
	public VNAMenuBar getMenubar() {
		return menubar;
	}

	/**
	 * @return the toolbar
	 */
	public VNAToolbar getToolbar() {
		return toolbar;
	}

	/**
	 * @param menubar
	 *            the menubar to set
	 */
	public void setMenubar(VNAMenuBar menubar) {
		this.menubar = menubar;
	}

	/**
	 * @param toolbar
	 *            the toolbar to set
	 */
	public void setToolbar(VNAToolbar toolbar) {
		this.toolbar = toolbar;
	}
}
