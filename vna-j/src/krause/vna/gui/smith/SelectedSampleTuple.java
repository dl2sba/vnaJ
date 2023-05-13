package krause.vna.gui.smith;

import java.awt.Color;

public class SelectedSampleTuple {
	private int index;
	private Color color;
	private String name;

	public SelectedSampleTuple(int idx, Color col, String pName) {
		index = idx;
		color = col;
		name = pName;		
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
