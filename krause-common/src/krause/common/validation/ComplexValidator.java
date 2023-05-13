package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.apache.commons.math3.complex.Complex;

import krause.common.resources.CommonMessages;
import krause.common.validation.ValidationResult.ValidationType;

public class ComplexValidator {
	public static double parseReal(String sReal, Complex min, Complex max, String context, ValidationResults results) {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		double real = 0;
		try {
			real = fmt.parse(sReal).doubleValue();

			if (real < min.getReal()) {
				String msg = CommonMessages.getString("ComplexValidator.realTooSmall");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, min.getReal()));
				res.setType(ValidationType.ERROR);
				res.setErrorObject(context);
				results.add(res);
			}

			if (real > max.getReal()) {
				String msg = CommonMessages.getString("ComplexValidator.realTooLarge");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, max.getReal()));
				res.setType(ValidationType.ERROR);
				res.setErrorObject(context);
				results.add(res);
			}

		} catch (ParseException e1) {
			ValidationResult res = new ValidationResult(e1, e1.getMessage());
			res.setType(ValidationType.ERROR);
			res.setErrorObject(context);
			results.add(res);
		}
		return real;
	}

	public static double parseImaginary(String sImaginary, Complex min, Complex max, String context, ValidationResults results) {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		double imaginary = 0;
		try {
			imaginary = fmt.parse(sImaginary).doubleValue();

			if (imaginary < min.getImaginary()) {
				String msg = CommonMessages.getString("ComplexValidator.imaginaryTooSmall");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, min.getImaginary()));
				res.setType(ValidationType.ERROR);
				res.setErrorObject(context);
				results.add(res);
			}

			if (imaginary > max.getImaginary()) {
				String msg = CommonMessages.getString("ComplexValidator.imaginaryTooLarge");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, max.getImaginary()));
				res.setType(ValidationType.ERROR);
				res.setErrorObject(context);
				results.add(res);
			}
		} catch (ParseException e1) {
			ValidationResult res = new ValidationResult(e1, e1.getMessage());
			res.setType(ValidationType.ERROR);
			res.setErrorObject(context);
			results.add(res);
		}
		return imaginary;
	}
}
