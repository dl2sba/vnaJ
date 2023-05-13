package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import krause.common.resources.CommonMessages;
import krause.common.validation.ValidationResult.ValidationType;

public class DoubleValidator {
	public static double parse(String numAsString, Double min, Double max, String context, ValidationResults results) {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		double rc = 0;
		try {
			rc = fmt.parse(numAsString).doubleValue();
			if (rc < min) {
				String msg = CommonMessages.getString("DoubleValidator.tooSmall");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, min));
				res.setType(ValidationType.ERROR);
				res.setErrorObject(context);
				results.add(res);
			}
			if (rc > max) {
				String msg = CommonMessages.getString("DoubleValidator.tooLarge");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, max));
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
		return rc;
	}
}
