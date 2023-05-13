package krause.common.math;

/*
 * 	http://introcs.cs.princeton.edu/java/97data/
 * 	http://introcs.cs.princeton.edu/java/97data/FFT.java.html
 */
public class DFFT {

	// compute the FFT of x[], assuming its length is a power of 2
	public static double[] execute(double[] v) {
		int N = v.length;
		double twoPikOnN;
		double twoPijkOnN;
		double twoPiOnN = 2 * Math.PI / N;
		double r_data[] = new double[N];
		double i_data[] = new double[N];
		double psd[] = new double[N];

		for (int k = 0; k < N; k++) {
			twoPikOnN = twoPiOnN * k;
			for (int j = 0; j < N; j++) {
				twoPijkOnN = twoPikOnN * j;
				r_data[k] += v[j] * Math.cos(twoPijkOnN);
				i_data[k] -= v[j] * Math.sin(twoPijkOnN);
			}
			r_data[k] /= N;
			i_data[k] /= N;
			psd[k] = r_data[k] * r_data[k] + i_data[k] * i_data[k];
		}

		return psd;
	}
}
