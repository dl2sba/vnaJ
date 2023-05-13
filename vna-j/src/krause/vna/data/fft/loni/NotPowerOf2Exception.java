package krause.vna.data.fft.loni;

/*
 * @(#)NotPowerOf2Exception.java		1.10 05/02/24
 *
 * ChargedFluid released package
 *
 * COPYRIGHT NOTICE
 * Copyright (c) 2005
 * Laboratory of Neuro Imaging, Neurology, UCLA.
 */


/** 
 * This exception will be thrown by the {@link FastFourierTransform} when the size of the 
 * input arrays is not an integer power of 2.
 *
 * @version	1.10, 05/02/24
 * @author	Herbert H.H. Chang and Daniel J. Valentino
 * @since	JDK1.4
 */

public class NotPowerOf2Exception extends Exception
{
	private final int _numOfPoint;
	
	NotPowerOf2Exception(int index) {
		_numOfPoint = index;
	}
	
	@Override
	public String toString()
	{
		return "NotPowerOf2Exception[ " + _numOfPoint + " is not an integer power of 2 ]";
	}
}
    

