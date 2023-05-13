package krause.common.validation;

import java.text.MessageFormat;

import krause.common.resources.CommonMessages;
import krause.common.validation.ValidationResult.ValidationType;

public class StringValidator {
	public static String parse(String value, long minLen, long maxLen, String context, ValidationResults results) {
		if (value.length() < minLen) {
			String msg = CommonMessages.getString("StringValidator.tooShort");
			ValidationResult res = new ValidationResult(MessageFormat.format(msg, minLen));
			res.setType(ValidationType.ERROR);
			res.setErrorObject(context);
			results.add(res);
		} else if (value.length() > maxLen) {
			String msg = CommonMessages.getString("StringValidator.tooLong");
			ValidationResult res = new ValidationResult(MessageFormat.format(msg, maxLen));
			res.setType(ValidationType.ERROR);
			res.setErrorObject(context);
			results.add(res);
		}
		return value;
	}
}
