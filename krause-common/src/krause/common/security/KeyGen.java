package krause.common.security;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;

public class KeyGen {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			KeyPair kp = kpg.generateKeyPair();
			Key pub = kp.getPublic();
			Key pvt = kp.getPrivate();

			FileOutputStream out = new FileOutputStream("../vna-j-build/vnaJ-sec.key");
			out.write(pvt.getEncoded());
			out.close();

			out = new FileOutputStream("../vna-j/src/krause/vna/resources/vnaJ-sec.pub");
			out.write(pub.getEncoded());
			out.close();

			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initSign(kp.getPrivate());

		} catch (NoSuchAlgorithmException | IOException | InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}
