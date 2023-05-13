package krause.net.server.data;

import java.util.HashMap;
import java.util.Map;

public class ServerReport {
	private Map<String, ServerReportItem> items = new HashMap<String, ServerReportItem>();

	public ServerReportItem getItem(String name) {
		ServerReportItem lItem = items.get(name);
		if (lItem == null) {
			lItem = new ServerReportItem();
			lItem.setName(name);
			items.put(name, lItem);
		}
		return lItem;
	}

	public void updateItem(ServerReportItem item) {
		ServerReportItem lItem = items.get(item.getName());
		if (lItem != null) {
			lItem.setIntValue(item.getIntValue());
			lItem.setStringValue(item.getStringValue());
		}
	}

	public void incItemValue(String name) {
		ServerReportItem lItem = items.get(name);
		if (lItem == null) {
			lItem = new ServerReportItem();
			lItem.setName(name);
			items.put(name, lItem);
		}
		lItem.incIntValue();
	}

	public void decItemValue(String name) {
		ServerReportItem lItem = items.get(name);
		if (lItem == null) {
			lItem = new ServerReportItem();
			lItem.setName(name);
			items.put(name, lItem);
		}
		lItem.decIntValue();
	}

	public void updateItem(String name, int value) {
		ServerReportItem lItem = items.get(name);
		if (lItem == null) {
			lItem = new ServerReportItem();
			lItem.setName(name);
			lItem.setIntValue(value);
			items.put(name, lItem);
		}
		lItem.setIntValue(value);
	}

	public void updateItem(String name, String value) {
		ServerReportItem lItem = items.get(name);
		if (lItem != null) {
			lItem.setStringValue(value);
		}
	}

	public void setItems(Map<String, ServerReportItem> items) {
		this.items = items;
	}

	public Map<String, ServerReportItem> getItems() {
		return items;
	}
}
