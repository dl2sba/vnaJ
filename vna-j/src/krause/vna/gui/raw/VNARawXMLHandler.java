package krause.vna.gui.raw;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Locale;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.data.VNADataPool;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.resources.VNAMessages;

public class VNARawXMLHandler {

	private void createAdditionalData(Element root) {
		// add the version element
		root.addContent(new Element("version").setText("1"));
		Element userInfo = new Element("user-info");

		VNADataPool datapool = VNADataPool.getSingleton();

		userInfo.addContent(new Element("timestamp").setText(new Timestamp(System.currentTimeMillis()).toString()));
		userInfo.addContent(new Element("user").setText(System.getProperty("user.name")));

		userInfo.addContent(new Element("country").setText(Locale.getDefault().getCountry()));
		userInfo.addContent(new Element("language").setText(Locale.getDefault().getLanguage()));
		userInfo.addContent(new Element("display-variant").setText(Locale.getDefault().getDisplayVariant()));
		userInfo.addContent(new Element("display-country").setText(Locale.getDefault().getDisplayCountry()));
		userInfo.addContent(new Element("display-language").setText(Locale.getDefault().getDisplayLanguage()));
		userInfo.addContent(new Element("display-variant").setText(Locale.getDefault().getDisplayVariant()));
		userInfo.addContent(new Element("application-version").setText(VNAMessages.getString("Application.version")));
		userInfo.addContent(new Element("application-date").setText(VNAMessages.getString("Application.date")));
		userInfo.addContent(new Element("java-version").setText(System.getProperty("java.version")));
		userInfo.addContent(new Element("java-runtime-version").setText(System.getProperty("java.runtime.version")));
		userInfo.addContent(new Element("java-vm-version").setText(System.getProperty("java.vm.version")));
		userInfo.addContent(new Element("java-vm-vendor").setText(System.getProperty("java.vm.vendor")));
		userInfo.addContent(new Element("os-arch").setText(System.getProperty("os.arch")));
		userInfo.addContent(new Element("os-name").setText(System.getProperty("os.name")));
		userInfo.addContent(new Element("os-version").setText(System.getProperty("os.version")));
		userInfo.addContent(new Element("sun-cpu-endian").setText(System.getProperty("sun.cpu.endian")));
		userInfo.addContent(new Element("sun-desktop").setText(System.getProperty("sun.desktop")));
		userInfo.addContent(new Element("scanmode").setText(datapool.getScanMode().key()));
		userInfo.addContent(new Element("device").setText(datapool.getDriver().getDeviceInfoBlock().getLongName()));
		if (datapool.getMainCalibrationBlock().getFile() != null) {
			userInfo.addContent(new Element("calibration-filename").setText(datapool.getMainCalibrationBlock().getFile().getName()));
		}

		//
		root.addContent(userInfo);
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws ProcessingException
	 * @throws ProcessingException
	 */
	public String readXMLCommentFromFile(File file) throws ProcessingException {
		TraceHelper.entry(this, "readXMLCommentFromFile", file.getAbsolutePath());
		String rc = "";
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(file);
			Element root = doc.getRootElement();
			if (VNACalibratedSampleBlock.XML_NAME_ROOT.equals(root.getName())) {
				rc = root.getChildText(VNACalibratedSampleBlock.XML_NAME_COMMENT);
			}
		} catch (Exception e) {
			throw new ProcessingException(e);
		}
		TraceHelper.exitWithRC(this, "readXMLCommentFromFile", rc);
		return rc;
	}

	/**
	 * 
	 * @param file
	 * @return
	 * @throws ProcessingException
	 */
	public VNACalibratedSampleBlock readXMLFromFile(File file) throws ProcessingException {
		TraceHelper.entry(this, "readXMLFromFile");
		VNACalibratedSampleBlock rc = null;
		SAXBuilder builder = new SAXBuilder();
		Document doc;
		try {
			doc = builder.build(file);
			Element root = doc.getRootElement();
			if (VNACalibratedSampleBlock.XML_NAME_ROOT.equals(root.getName())) {
				rc = VNACalibratedSampleBlock.fromElement(root);
				if (rc == null) {
					ErrorLogHelper.text(this, "readXMLFromFile", "error reading root element");
					throw new ProcessingException(VNAMessages.getString("Message.Import.3"));
				}
				rc.setFile(file);
			} else {
				ErrorLogHelper.text(this, "readXMLFromFile", "wrong root element found");
				throw new ProcessingException(VNAMessages.getString("Message.Import.3"));
			}
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "readXMLFromFile", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exit(this, "readXMLFromFile");
		return rc;
	}

	/**
	 * 
	 * @param data
	 * @param file
	 * @throws ProcessingException
	 */
	public void writeXMLFile(VNACalibratedSampleBlock data, File file) throws ProcessingException {
		FileOutputStream fos = null;
		try {
			// create the block as xml structure
			Element root = data.asElement();

			// write additional data
			createAdditionalData(root);

			// build a document from it
			Document doc = new Document(root);

			// build the outputter
			XMLOutputter outp = new XMLOutputter();
			outp.setFormat(Format.getPrettyFormat());

			// and write it out
			fos = new FileOutputStream(file);
			outp.output(doc, fos);
		} catch (FileNotFoundException e) {
			ErrorLogHelper.exception(this, "writeXMLFile", e);
			throw new ProcessingException(e);
		} catch (IOException e) {
			ErrorLogHelper.exception(this, "writeXMLFile", e);
			throw new ProcessingException(e);
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
