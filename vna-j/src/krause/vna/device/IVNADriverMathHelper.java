package krause.vna.device;

import krause.common.exception.ProcessingException;
import krause.vna.data.VNABaseSample;
import krause.vna.data.VNASampleBlock;
import krause.vna.data.calibrated.VNACalibratedSample;
import krause.vna.data.calibrated.VNACalibratedSampleBlock;
import krause.vna.data.calibrated.VNACalibrationBlock;
import krause.vna.data.calibrated.VNACalibrationContext;
import krause.vna.data.calibrated.VNACalibrationPoint;
import krause.vna.data.calibrationkit.VNACalibrationKit;

public interface IVNADriverMathHelper {

	/**
	 * 
	 * @return
	 */
	public IVNADriver getDriver();

	/**
	 * 
	 * @param driver
	 */
	public void setDriver(IVNADriver driver);

	/**
	 * 
	 * @param listOpen
	 * @param listShort
	 * @param listLoad
	 * @return
	 * @throws ProcessingException
	 */
	public VNACalibrationBlock createCalibrationBlockFromRaw(final VNACalibrationContext context, final VNASampleBlock listOpen, final VNASampleBlock listShort, final VNASampleBlock listLoad, final VNASampleBlock listLoop) throws ProcessingException;

	/**
	 * 
	 * @param context
	 * @param raw
	 * @param calib
	 * @return
	 */
	public VNACalibratedSampleBlock createCalibratedSamples(final VNACalibrationContext context, final VNASampleBlock raw);

	/**
	 * 
	 * @param context
	 * @param calBlock
	 */
	public void createCalibrationPoints(VNACalibrationContext context, VNACalibrationBlock calBlock);

	/**
	 * 
	 * @param context
	 * @param sample
	 * @param calib
	 * @return
	 */
	public VNACalibratedSample createCalibratedSample(final VNACalibrationContext context, final VNABaseSample sample, final VNACalibrationPoint calib);

	/**
	 * 
	 * @param context
	 * @param pOpen
	 * @param pShort
	 * @param pLoad
	 * @param pLoop
	 * @return
	 */
	public VNACalibrationPoint createCalibrationPoint(VNACalibrationContext context, VNABaseSample pOpen, VNABaseSample pShort, VNABaseSample pLoad, VNABaseSample pLoop);

	/**
	 * 
	 * @param calBlock
	 * @return
	 */
	public VNACalibrationContext createCalibrationContextForCalibrationPoints(VNACalibrationBlock calBlock, VNACalibrationKit calKit);

	/**
	 * 
	 * @param calBlock
	 * @return
	 */
	public VNACalibrationContext createCalibrationContextForCalibratedSamples(VNACalibrationBlock calBlock);

	/**
	 * 
	 * @param samples
	 */
	public void applyFilter(VNABaseSample[] samples);
}
