/**
 * ********************************************************************************** 
 * Copyright (C) 2009 Dietmar Krause, DL2SBA
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package krause.vna.export;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import krause.vna.gui.scale.VNAMeasurementScale;

public class JFSeries {
	private XYSeries mSeries;
	private VNAMeasurementScale mScale;
	private XYSeriesCollection mDataset = new XYSeriesCollection();

	public JFSeries(VNAMeasurementScale scale) {
		super();
		mScale = scale;
	}

	public JFSeries() {
	}

	public XYSeries getSeries() {
		return mSeries;
	}

	public void setSeries(XYSeries series) {
		this.mSeries = series;
		mDataset.addSeries(series);
	}

	public VNAMeasurementScale getScale() {
		return mScale;
	}

	public void setScale(VNAMeasurementScale scale) {
		this.mScale = scale;
	}

	public XYSeriesCollection getDataset() {
		return mDataset;
	}

	public void setDataset(XYSeriesCollection dataset) {
		this.mDataset = dataset;
	}
}
