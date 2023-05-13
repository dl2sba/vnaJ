package krause.common.exception;
/**
  *  Filderwetter - framework for weather aquisistion and analysis
  * 
  *  Copyright (C) 2003 Dietmar Krause, DL2SBA
  */
public class UserContextNotSetException extends ProcessingException {
	public UserContextNotSetException(String text) {
		super(text);
	}
}
