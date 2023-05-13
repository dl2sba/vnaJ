package krause.net.server.data;

public class ServerReportItem {
	private int intValue;
	private String stringValue;
	private String name;
	private Object key;

	public int getIntValue() {
		return intValue;
	}

	public void setIntValue(int intValue) {
		this.intValue = intValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void incIntValue() {
		++intValue;
	}

	public void decIntValue() {
		--intValue;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getKey() {
		return key;
	}
}
