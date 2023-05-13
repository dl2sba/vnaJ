package krause.vna.data.fft.loni;

/*
 * @(#)FastFourierTransform.java		1.10 05/02/24
 *
 * ChargedFluid released package
 *
 * COPYRIGHT NOTICE
 * Copyright (c) 2005
 * Laboratory of Neuro Imaging, Neurology, UCLA.
 */

/**
 * The class <code>FastFourierTransform</code> contains methods for performing basic numerical computations of discrete Fourier
 * transform (DFT) in 1-D, 2-D and 3-D. The dimension of the input matrix must be the interger power of 2 due to the algorithm of
 * the fast Fourier transform (FFT).
 *
 * @version 1.10, 05/02/24
 * @author Herbert H.H. Chang and Daniel J. Valentino
 * @since JDK1.4
 */
// NOSONAR
public final class FastFourierTransform {

	private FastFourierTransform() {
		// not used
	}

	/**
	 * Computes the discrete Fourier transform in 3-D with the specified arrays and direction using the fast Fourier transform (FFT)
	 * algorithm. Two double type arrays representing the real part and imaginary part of data in the spatial domain are required
	 * for the computation. Notice that the dimension of the arrays must be the same and be an integer power of 2, otherwise, an
	 * error will be catched and an exception will be thrown without any computation. If the boolean value of the direction is true,
	 * the forward FFT will be performed, or the inverse FFT if false. The computation results are returned in the corresponding
	 * real and imaginary input arrays. Therefore, the original data in the two arrays will be lost after the computation.
	 *
	 * @param realArray
	 *            a double array representing the real part of complex input data before the computation. It is also the real part
	 *            of the complex output data after the computation.
	 * @param imagArray
	 *            a double array representing the imaginary part of complex input data before the computation. It is also the
	 *            imaginary part of the complex output data after the computation.
	 * @param direction
	 *            <code>true</code> if the forward 3-D FFT is executed, false inverse.
	 */
	public static void fastFT(double[][][] realArray, double[][][] imagArray, boolean direction) {
		int numOfFrame = 0;
		int numOfRow = 0;
		int numOfCol = 0;
		if (realArray != null) {
			numOfFrame = realArray.length; // z-axis
			numOfRow = realArray[0].length; // y-axis
			numOfCol = realArray[0][0].length; // x-axis
		}

		int numOfFrameImag = 0;
		int numOfRowImag = 0;
		int numOfColImag = 0;
		if (imagArray != null) {
			numOfFrameImag = imagArray.length; // z-axis
			numOfRowImag = imagArray[0].length; // y-axis
			numOfColImag = imagArray[0][0].length; // x-axis
		}

		/* Allocate and initialize memories to double variables */
		// Temporary computation vectors.
		double[] realRow = new double[numOfRow];
		double[] imagRow = new double[numOfRow];
		double[] realFrame = new double[numOfFrame];
		double[] imagFrame = new double[numOfFrame];

		try {
			// check if the dimensions of both arrays are the same or not!
			_checkSizeOfArray(numOfFrame, numOfRow, numOfCol, numOfFrameImag, numOfRowImag, numOfColImag);
			int numOfPowerInFrame = _checkPowerOf2(numOfFrame); // check if numOfFrame is an integer power of 2 or not!
			int numOfPowerInRow = _checkPowerOf2(numOfRow); // check if numOfRow is an integer power of 2 or not!
			int numOfPowerInCol = _checkPowerOf2(numOfCol); // check if numOfCol is an integer power of 2 or not!

			/* Transform array in the column direction (x-axis) */
			for (int frame = 0; frame < numOfFrame; frame++) {
				for (int row = 0; row < numOfRow; row++) {
					/* Compute 1D FFT via function _fastFT1D() */
					_fastFT1D(realArray[frame][row], imagArray[frame][row], numOfPowerInCol, direction);
				}
			}
			/* Transform array in the row direction (y-axis) */
			for (int frame = 0; frame < numOfFrame; frame++) {
				for (int col = 0; col < numOfCol; col++) {
					// Extract one vector from array for real and imaginary parts
					for (int row = 0; row < numOfRow; row++) {
						realRow[row] = realArray[frame][row][col];
						imagRow[row] = imagArray[frame][row][col];
					}
					/* Compute 1D FFT via function _fastFT1D() */
					_fastFT1D(realRow, imagRow, numOfPowerInRow, direction);
					// Store one vector into array after FFT for real and imaginary parts
					for (int row = 0; row < numOfRow; row++) {
						realArray[frame][row][col] = realRow[row];
						imagArray[frame][row][col] = imagRow[row];
					}
				}
			}
			/* Transform array in the frame direction (z-axis) */
			for (int row = 0; row < numOfRow; row++) {
				for (int col = 0; col < numOfCol; col++) {
					// Extract one vector from array for real and imaginary parts
					for (int frame = 0; frame < numOfFrame; frame++) {
						realFrame[frame] = realArray[frame][row][col];
						imagFrame[frame] = imagArray[frame][row][col];
					}
					/* Compute 1D FFT via function _fastFT1D() */
					_fastFT1D(realFrame, imagFrame, numOfPowerInFrame, direction);
					// Store one vector into array after FFT for real and imaginary parts
					for (int frame = 0; frame < numOfFrame; frame++) {
						realArray[frame][row][col] = realFrame[frame];
						imagArray[frame][row][col] = imagFrame[frame];
					}
				}
			}
		} catch (NotSameArraySizeException e) {
			System.out.println("\n" + e);
			System.out.println("Warning: Please assign real and imaginary arrays with the same dimension");
		} catch (NotPowerOf2Exception e) {
			System.out.println("\n" + e);
			System.out.println("Warning: Please assign an array with the length equal to an integer power of 2");
		}
	} // End of fastFT

	/**
	 * Computes the discrete Fourier transform in 2-D with the specified arrays and direction using the fast Fourier transform (FFT)
	 * algorithm. Two double type arrays representing the real part and imaginary part of data in the spatial domain are required
	 * for the computation. Notice that the dimension of the arrays must be the same and be an integer power of 2, otherwise, an
	 * error will be catched and an exception will be thrown without any computation. If the boolean value of the direction is true,
	 * the forward FFT will be performed, or the inverse FFT if false. The computation results are returned in the corresponding
	 * real and imaginary input arrays. Therefore, the original data in the two arrays will be lost after the computation.
	 *
	 * @param realArray
	 *            a double array representing the real part of complex input data before the computation. It is also the real part
	 *            of the complex output data after the computation.
	 * @param imagArray
	 *            a double array representing the imaginary part of complex input data before the computation. It is also the
	 *            imaginary part of the complex output data after the computation.
	 * @param direction
	 *            <code>true</code> if the forward 2-D FFT is executed, false inverse.
	 */
	public static void fastFT(double[][] realArray, double[][] imagArray, boolean direction) {
		int numOfRow = 0;
		int numOfCol = 0;
		if (realArray != null) {
			numOfRow = realArray.length; // y-axis
			numOfCol = realArray[0].length; // x-axis
		}

		int numOfRowImag = 0;
		int numOfColImag = 0;
		if (imagArray != null) {
			numOfRowImag = imagArray.length; // y-axis
			numOfColImag = imagArray[0].length; // x-axis
		}

		/* Allocate and initialize memories to double variables */
		double[] realRow = new double[numOfRow];
		double[] imagRow = new double[numOfRow];

		try {
			// check if the dimensions of both arrays are the same or not!
			_checkSizeOfArray(numOfRow, numOfCol, numOfRowImag, numOfColImag);
			int numOfPowerInRow = _checkPowerOf2(numOfRow); // check if numOfRow is an integer power of 2 or not!
			int numOfPowerInCol = _checkPowerOf2(numOfCol); // check if numOfCol is an integer power of 2 or not!

			/* Transform array in the column direction */
			for (int row = 0; row < numOfRow; row++) {
				/* Compute 1D FFT via function _fastFT1D() */
				_fastFT1D(realArray[row], imagArray[row], numOfPowerInCol, direction);
			}

			/* Transform array in the row direction */
			for (int col = 0; col < numOfCol; col++) {
				// Extract one vector from array for real and imaginary parts
				for (int row = 0; row < numOfRow; row++) {
					realRow[row] = realArray[row][col];
					imagRow[row] = imagArray[row][col];
				}

				/* Compute 1D FFT via function _fastFT1D() */
				_fastFT1D(realRow, imagRow, numOfPowerInRow, direction);

				// Store one vector into array after FFT for real and imaginary parts
				for (int row = 0; row < numOfRow; row++) {
					realArray[row][col] = realRow[row];
					imagArray[row][col] = imagRow[row];
				}
			}
		} catch (NotSameArraySizeException e) {
			System.out.println("\n" + e);
			System.out.println("Warning: Please assign real and imaginary arrays with the same dimension");
		} catch (NotPowerOf2Exception e) {
			System.out.println("\n" + e);
			System.out.println("Warning: Please assign an array with the length equal to an integer power of 2");
		}
	} // End of fastFT

	/**
	 * Computes the discrete Fourier transform in 1-D with the specified arrays and direction using the fast Fourier transform (FFT)
	 * algorithm. Two double type arrays representing the real part and imaginary part of data in the spatial domain are required
	 * for the computation. Notice that the dimension of the arrays must be the same and be an integer power of 2, otherwise, an
	 * error will be catched and an exception will be thrown without any computation. If the boolean value of the direction is true,
	 * the forward FFT will be performed, or the inverse FFT if false. The computation results are returned in the corresponding
	 * real and imaginary input arrays. Therefore, the original data in the two arrays will be lost after the computation.
	 *
	 * @param realArray
	 *            a double array representing the real part of complex input data before the computation. It is also the real part
	 *            of the complex output data after the computation.
	 * @param imagArray
	 *            a double array representing the imaginary part of complex input data before the computation. It is also the
	 *            imaginary part of the complex output data after the computation.
	 * @param direction
	 *            <code>true</code> if the forward 1-D FFT is executed, false inverse.
	 */
	public static void fastFT(double[] realArray, double[] imagArray, boolean direction) {
		int numOfReal = 0;
		if (realArray != null) {
			numOfReal = realArray.length;
		}

		int numOfImag = 0;
		if (imagArray != null) {
			numOfImag = imagArray.length;
		}

		try {
			// check if the dimensions of both arrays are the same or not!
			_checkSizeOfArray(numOfReal, numOfImag);
			// check if the length is an integer power of 2 or not!
			int numOfPower = _checkPowerOf2(numOfReal);

			/* Compute 1D FFT via function _fastFT1D() */
			_fastFT1D(realArray, imagArray, numOfPower, direction);
		} catch (NotSameArraySizeException e) {
			System.out.println("\n" + e);
			System.out.println("Warning: Please assign real and imaginary arrays with the same dimension");
		} catch (NotPowerOf2Exception e) {
			System.out.println("\n" + e);
			System.out.println("Warning: Please assign an array with the length equal to an integer power of 2");
		}
	}

	/**
	 * Accepts six integers representing the sizes of two different arrays. If they don't have the same dimension, it throws a
	 * NotSameArraySizeException.
	 *
	 * @param frameReal
	 *            Input integer representing the number of frame of a real part array.
	 * @param rowReal
	 *            Input integer representing the number of row of a real part array.
	 * @param colReal
	 *            Input integer representing the number of column of a real part array.
	 * @param frameImag
	 *            Input integer representing the number of frame of a imaginary part array.
	 * @param rowImag
	 *            Input integer representing the number of row of a imaginary part array.
	 * @param colImag
	 *            Input integer representing the number of column of a imaginary part array.
	 */
	private static void _checkSizeOfArray(int frameReal, int rowReal, int colReal, int frameImag, int rowImag, int colImag) throws NotSameArraySizeException {
		if ((frameReal != frameImag) || (rowReal != rowImag) || (colReal != colImag))
			throw new NotSameArraySizeException();
	} // End of _checkSizeOfArray

	/*
	 * _checkSizeOfArray function accepts four integers representing the sizes of two different arrays. If they don't have the same
	 * dimension, it throws a NotSameArraySizeException.
	 *
	 * @param rowReal Input integer representing the number of row of a real part array.
	 * 
	 * @param colReal Input integer representing the number of column of a real part array.
	 * 
	 * @param rowImag Input integer representing the number of row of a imaginary part array.
	 * 
	 * @param colImag Input integer representing the number of column of a imaginary part array.
	 */
	private static void _checkSizeOfArray(int rowReal, int colReal, int rowImag, int colImag) throws NotSameArraySizeException {
		if ((rowReal != rowImag) || (colReal != colImag))
			throw new NotSameArraySizeException();
	} // End of _checkSizeOfArray

	/*
	 * _checkSizeOfArray function accepts four integers representing the sizes of two different arrays. If they don't have the same
	 * dimension, it throws a NotSameArraySizeException.
	 *
	 * @param numOfReal Input integer representing the number of row of a real part array.
	 * 
	 * @param numOfImag Input integer representing the number of column of a real part array.
	 */
	private static void _checkSizeOfArray(int numOfReal, int numOfImag) throws NotSameArraySizeException {
		if (numOfReal != numOfImag)
			throw new NotSameArraySizeException();
	}

	/*
	 * _checkPowerOf2 function accepts a integer and check if this value is an integer power of 2 or not. If it is an integer power
	 * of 2, this function returns the number of power of 2. If it is not an integer power of 2, this function throws a
	 * NotPowerOf2Exception.
	 *
	 * @param index Input integer value for examination.
	 * 
	 * @return int Output integer value representing the number of power of 2.
	 */
	private static int _checkPowerOf2(int index) throws NotPowerOf2Exception {
		// check if index is an integer power of 2
		if (index < 2)
			throw new NotPowerOf2Exception(index);
		if ((index & (index - 1)) != 0)
			throw new NotPowerOf2Exception(index);
		// calculate the number of bits needed
		for (int i = 0;; i++)
			if ((index & (1 << i)) != 0)
				return i;
	} // End of _checkPowerOf2

	/**
	 * _fastFT1D function accepts two double type vectors of length of integer power of 2, an integer indicating the number of power
	 * of 2, two boolean values indicating row computation and forward FFT or not.
	 * 
	 * @param real
	 *            Input double vector indicating the real part of the array.
	 * @param imag
	 *            Input double vector indicating the imaginary part of the array.
	 * @param numOfPower
	 *            An integer indicating the number of power of 2 by the given vector.
	 * @param direct
	 *            Boolean value, true indicates forward 2D FFT; false inverse.
	 */
	private static void _fastFT1D(double[] real, double[] imag, int numOfPower, boolean direct) {
		int numOfPoint, i1, i2, j2, k2, l1, l2;
		double tr, ti, c1, c2, u1, u2, t1, t2, z;

		numOfPoint = real.length;

		/* Do the bit reversal */
		i2 = numOfPoint >> 1;
		j2 = 0;
		for (int i = 0; i < numOfPoint - 1; i++) {
			if (i < j2) {
				tr = real[i];
				ti = imag[i];
				real[i] = real[j2];
				imag[i] = imag[j2];
				real[j2] = tr;
				imag[j2] = ti;
			}
			k2 = i2;
			while (k2 <= j2) {
				j2 -= k2;
				k2 >>= 1;
			}
			j2 += k2;
		}

		/* Compute 1D FFT */
		c1 = -1.0;
		c2 = 0.0;
		l2 = 1;
		for (int l = 0; l < numOfPower; l++) // Loops through stages
		{
			l1 = l2;
			l2 <<= 1;
			u1 = 1.0;
			u2 = 0.0;

			for (int j = 0; j < l1; j++) {
				for (int i = j; i < numOfPoint; i += l2) {
					i1 = i + l1;
					t1 = u1 * real[i1] - u2 * imag[i1];
					t2 = u1 * imag[i1] + u2 * real[i1];
					real[i1] = real[i] - t1;
					imag[i1] = imag[i] - t2;
					real[i] += t1;
					imag[i] += t2;
				}
				z = u1 * c1 - u2 * c2;
				u2 = u1 * c2 + u2 * c1;
				u1 = z;
			}
			c2 = Math.sqrt((1.0 - c1) / 2.0);
			if (direct)
				c2 = -c2;
			c1 = Math.sqrt((1.0 + c1) / 2.0);
		}

		/* Scaling for forward FFT */
		if (direct)
			for (int i = 0; i < numOfPoint; i++) {
				real[i] /= (double) numOfPoint;
				imag[i] /= (double) numOfPoint;
			}
	} // End of _fastFT1D
}
