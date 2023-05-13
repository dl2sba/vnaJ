package krause.common.validation;

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import krause.common.resources.CommonMessages;
import krause.common.validation.ValidationResult.ValidationType;

public class LongValidator {
	public static long parse(String value, long min, long max, String context, ValidationResults results) {
		NumberFormat fmt = NumberFormat.getNumberInstance();
		long rc = 0;
		try {
			rc = fmt.parse(value).longValue();
			if (rc < min) {
				String msg = CommonMessages.getString("LongValidator.tooSmall");
				ValidationResult res = new ValidationResult(MessageFormat.format(msg, min));
				res.setType(ValidationType.ERROR);
				res.setErrorObject(context);
				results.add(res);
			}
			if (rc > max) {
				String msg = CommonMessages.getString("LongValidator.tooLarge");
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
