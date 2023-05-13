/**
 * Copyright (C) 2011 Dietmar Krause, DL2SBA
 *
 *	This file: VNAPadCalculatorTest.java
 *  Part of:   vna-j
 */

package krause.vna.gui.padcalc;

import junit.framework.TestCase;

/**
 * @author Dietmar
 * 
 */
public class VNAPadCalculatorTest extends TestCase {

	public void test0() {

		VNAPadCalculator pc = new VNAPadCalculator();
		double[] fullSeries = pc.createFullSeries(VNAPadConstants.E48Factors, 7);

		assertNotNull(fullSeries);
	}

	public void test1() {

		VNAPadCalculator pc = new VNAPadCalculator();
		double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);

		for (int i = 0; i < 10; ++i) {
			double resistanceX = Math.round(Math.random() * 1000000);

			System.out.println("target=" + resistanceX);
			System.out.println("   " + pc.calculateSeriesCircuit(fullSeries, resistanceX, 3, 0.001));
			System.out.println("   " + pc.calculateSeriesCircuit(fullSeries, resistanceX, 2, 0.010));
			System.out.println("   " + pc.calculateSeriesCircuit(fullSeries, resistanceX, 2, 0.100));
		}
	}

	public void test2() {
		VNAPadCalculator pc = new VNAPadCalculator();
		double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
		System.out.println("207,4=" + pc.calculateSeriesCircuit(fullSeries, 207.4, 4, 0.01));
		System.out.println(" 87,1=" + pc.calculateSeriesCircuit(fullSeries, 87.1, 4, 0.01));
		System.out.println(" 77,1=" + pc.calculateSeriesCircuit(fullSeries, 77.1, 4, 0.01));
	}

	public void test3() {
		VNAPadCalculator pc = new VNAPadCalculator();
		double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
		System.out.println("103,30=" + pc.calculateSeriesCircuit(fullSeries, 103.3, 4, 0.01));
		System.out.println("246,30=" + pc.calculateSeriesCircuit(fullSeries, 246.30, 4, 0.01));
		System.out.println(" 60,43=" + pc.calculateSeriesCircuit(fullSeries, 60.43, 4, 0.01));
	}

	public void test4() {
		VNAPadCalculator pc = new VNAPadCalculator();
		double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
		System.out.println("207,4=" + pc.calculateSeriesCircuit(fullSeries, 207.4, 4, 0.001));
		System.out.println(" 87,1=" + pc.calculateSeriesCircuit(fullSeries, 87.1, 4, 0.001));
		System.out.println(" 77,1=" + pc.calculateSeriesCircuit(fullSeries, 77.1, 4, 0.001));
	}

	public void test5() {
		VNAPadCalculator pc = new VNAPadCalculator();
		double[] fullSeries = pc.createFullSeries(VNAPadConstants.E24Factors, 7);
		System.out.println("103,30=" + pc.calculateSeriesCircuit(fullSeries, 103.3, 4, 0.001));
		System.out.println("246,30=" + pc.calculateSeriesCircuit(fullSeries, 246.30, 4, 0.001));
		System.out.println(" 60,43=" + pc.calculateSeriesCircuit(fullSeries, 60.43, 4, 0.001));
	}

}
