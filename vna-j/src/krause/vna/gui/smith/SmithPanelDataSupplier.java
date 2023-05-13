package krause.vna.gui.smith;

import krause.vna.gui.smith.data.SmithDataCurve;

public interface SmithPanelDataSupplier {

	SmithDataCurve getDataCurve();

	SelectedSampleTuple[] getSelectedTuples();

}
