package krause.vna.data;

import krause.common.TypedProperties;

public class VNAFrequencyRange {

	public static final long NO_FREQ = Long.MIN_VALUE;

	private long start;
	private long stop;

	public VNAFrequencyRange() {
		start = NO_FREQ;
		stop = NO_FREQ;
	}

	public VNAFrequencyRange(long start, long stop) {
		super();
		this.start = start;
		this.stop = stop;
	}

	public VNAFrequencyRange(VNAFrequencyRange range) {
		start = range.getStart();
		stop = range.getStop();
	}

	public long getStart() {
		return start;
	}

	public long getStop() {
		return stop;
	}

	@Override
	public String toString() {
		return "VNAFrequencyRange [start=" + start + ", stop=" + stop + "]";
	}

	public void setStart(long start) {
		this.start = start;
	}

	public void setStop(long stop) {
		this.stop = stop;
	}

	public void saveToProperties(TypedProperties props) {
		props.putLong("Range.start", start);
		props.putLong("Range.stop", stop);
	}

	public void restoreFromProperties(TypedProperties props) {
		start = props.getInteger("Range.start", NO_FREQ);
		stop = props.getInteger("Range.stop", NO_FREQ);
	}

	/**
	 * if start and stop are not equal to NO_FREQ
	 * 
	 * @return
	 */
	public boolean isValid() {
		return (start != NO_FREQ) && (stop != NO_FREQ);
	}
}
