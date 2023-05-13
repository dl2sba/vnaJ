package krause.vna.update;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import krause.common.exception.ProcessingException;
import krause.util.ras.logging.ErrorLogHelper;
import krause.util.ras.logging.TraceHelper;
import krause.vna.config.VNASystemConfig;
import krause.vna.config.VNASystemConfig.OS_PLATFORM;

public class UpdateChecker {

	public enum FILE_TYPE {
		JAR_FILE, README, OTHER
	};

	public boolean isNewVersionAvailable(String currentVersion, String versionFilePath) throws ProcessingException {
		boolean rc = false;

		UpdateInfoBlock uib;
		uib = readUpdateInfoFile(versionFilePath, false);
		if (uib != null) {
			String remoteVersion = uib.getVersion();
			if (remoteVersion != null) {
				if (currentVersion != null) {
					rc = remoteVersion.compareTo(currentVersion) > 0;
				}
			}
		}
		return rc;
	}

	/**
	 * 
	 * @param path
	 *            pathname of XML file
	 * @param readForAllOS
	 *            passed to readXMLFileEntries()
	 * @return
	 * @throws ProcessingException
	 */
	public UpdateInfoBlock readUpdateInfoFile(String path, boolean readForAllOS) throws ProcessingException {
		TraceHelper.entry(this, "readUpdateInfoFile", path);

		UpdateInfoBlock rc = null;

		try {
			URL url = new URL(path);
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(url);
			Element root = doc.getRootElement();

			String version = readVersion(root);
			List<DownloadFile> files = readXMLFileEntries(root, readForAllOS);
			String comment = readComment(root);

			rc = new UpdateInfoBlock();
			rc.setFiles(files);
			rc.setVersion(version);
			rc.setComment(comment);
		} catch (Exception e) {
			ErrorLogHelper.exception(this, "readUpdateInfoFile", e);
			throw new ProcessingException(e);
		}
		TraceHelper.exitWithRC(this, "readUpdateInfoFile", rc);
		return rc;
	}

	/**
	 * 
	 * @param root
	 *            of XML tree
	 * @param readForAllOS
	 *            true=read all files independent of current OS, false=read only
	 *            entries matching current OS
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<DownloadFile> readXMLFileEntries(Element root, boolean readForAllOS) {
		List<DownloadFile> rc = new ArrayList<DownloadFile>();

		OS_PLATFORM myOs = VNASystemConfig.getPlatform();

		// Get a list of all child elements
		Element eFiles = root.getChild("files");
		List<Element> lstFiles = eFiles.getChildren();
		for (Element file : lstFiles) {
			DownloadFile ent = readXMLFile(file);
			if (readForAllOS) {
				rc.add(ent);
			} else {
				if ((myOs == ent.getPlattform()) || (ent.getPlattform() == OS_PLATFORM.ALL)) {
					rc.add(ent);
				}
			}
		}
		return rc;
	}

	private DownloadFile readXMLFile(Element elem) {
		DownloadFile rc = new DownloadFile();
		rc.setLocalFileName(elem.getChildText("local"));
		rc.setRemoteFileName(elem.getChildText("remote"));
		rc.setHash(elem.getChildText("md5"));

		//
		String type = elem.getChildText("type");
		if (type != null) {
			if (type.equalsIgnoreCase("JAR")) {
				rc.setType(FILE_TYPE.JAR_FILE);
			} else if (type.equalsIgnoreCase("README")) {
				rc.setType(FILE_TYPE.README);
			} else {
				rc.setType(FILE_TYPE.OTHER);
			}
		} else {
			rc.setType(FILE_TYPE.OTHER);
		}

		//
		String plattform = elem.getChildText("platform");
		if (plattform != null) {
			if (plattform.equalsIgnoreCase("WINDOWS")) {
				rc.setPlattform(OS_PLATFORM.WINDOWS);
			} else if (plattform.equalsIgnoreCase("MAC")) {
				rc.setPlattform(OS_PLATFORM.MAC);
			} else if (plattform.equalsIgnoreCase("UNIX")) {
				rc.setPlattform(OS_PLATFORM.UNIX);
			} else {
				rc.setPlattform(OS_PLATFORM.ALL);
			}
		} else {
			rc.setPlattform(OS_PLATFORM.ALL);
		}
		return rc;
	}

	private String readComment(Element root) {
		String rc = null;
		Element eVersion = root.getChild("comment");
		if (eVersion != null) {
			rc = eVersion.getText();
		}
		return rc;
	}

	private String readVersion(Element root) {
		String rc = null;
		Element eVersion = root.getChild("version");
		rc = eVersion.getText();
		return rc;
	}
}
