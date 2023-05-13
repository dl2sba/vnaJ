package krause.vna.data;

public class VNABaseSampleHelper {
	private VNABaseSampleHelper() {

	}

	/**
	 * create a new sample that contain in each attribute the delta between sample2 and sample1
	 * 
	 * @param sample1
	 * @param sample2
	 * @return the delta sample
	 */
	public static VNABaseSample createDeltaSample(final VNABaseSample sample1, final VNABaseSample sample2, int numberIntermediateSamples) {
		final VNABaseSample rc = new VNABaseSample();

		rc.setHasPData(sample1.hasPData());

		rc.setFrequency((sample2.getFrequency() - sample1.getFrequency()) / numberIntermediateSamples);
		rc.setAngle((sample2.getAngle() - sample1.getAngle()) / numberIntermediateSamples);
		rc.setLoss((sample2.getLoss() - sample1.getLoss()) / numberIntermediateSamples);

		rc.setRss1((sample2.getRss1() - sample1.getRss1()) / numberIntermediateSamples);
		rc.setRss2((sample2.getRss2() - sample1.getRss2()) / numberIntermediateSamples);
		rc.setRss3((sample2.getRss3() - sample1.getRss3()) / numberIntermediateSamples);

		if (rc.hasPData()) {
			rc.setP1((sample2.getP1() - sample1.getP1()) / numberIntermediateSamples);
			rc.setP2((sample2.getP2() - sample1.getP2()) / numberIntermediateSamples);
			rc.setP3((sample2.getP3() - sample1.getP3()) / numberIntermediateSamples);
			rc.setP4((sample2.getP4() - sample1.getP4()) / numberIntermediateSamples);

			rc.setP1Ref((sample2.getP1Ref() - sample1.getP1Ref()) / numberIntermediateSamples);
			rc.setP2Ref((sample2.getP2Ref() - sample1.getP2Ref()) / numberIntermediateSamples);
			rc.setP3Ref((sample2.getP3Ref() - sample1.getP3Ref()) / numberIntermediateSamples);
			rc.setP4Ref((sample2.getP4Ref() - sample1.getP4Ref()) / numberIntermediateSamples);

		}
		return rc;
	}

	/**
	 * Create a new sample by adding all values from sampleDelta to the values of sampleBase
	 * 
	 * @param sampleBase
	 * @param sampleDelta
	 * @return
	 */
	public static VNABaseSample createNewSampleWithDelta(final VNABaseSample sampleBase, final VNABaseSample sampleDelta) {
		final VNABaseSample rc = new VNABaseSample();

		rc.setHasPData(sampleBase.hasPData());

		rc.setFrequency(sampleBase.getFrequency() + sampleDelta.getFrequency());
		rc.setAngle(sampleBase.getAngle() + sampleDelta.getAngle());
		rc.setLoss(sampleBase.getLoss() + sampleDelta.getLoss());

		rc.setRss1(sampleBase.getRss1() + sampleDelta.getRss1());
		rc.setRss2(sampleBase.getRss2() + sampleDelta.getRss2());
		rc.setRss3(sampleBase.getRss3() + sampleDelta.getRss3());

		if (rc.hasPData()) {
			rc.setP1(sampleBase.getP1() + sampleDelta.getP1());
			rc.setP2(sampleBase.getP2() + sampleDelta.getP2());
			rc.setP3(sampleBase.getP3() + sampleDelta.getP3());
			rc.setP4(sampleBase.getP4() + sampleDelta.getP4());

			rc.setP1Ref(sampleBase.getP1Ref() + sampleDelta.getP1Ref());
			rc.setP2Ref(sampleBase.getP2Ref() + sampleDelta.getP2Ref());
			rc.setP3Ref(sampleBase.getP3Ref() + sampleDelta.getP3Ref());
			rc.setP4Ref(sampleBase.getP4Ref() + sampleDelta.getP4Ref());
		}
		return rc;
	}

}
