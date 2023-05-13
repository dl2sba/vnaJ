/**
 * Copyright (C) 2010 Dietmar Krause, DL2SBA
 * 
 */
package krause.vna.data;

import java.util.List;

import krause.vna.background.VNABackgroundJob;

public interface IVNADataConsumer {
	public void consumeDataBlock(List<VNABackgroundJob> jobs);
}
