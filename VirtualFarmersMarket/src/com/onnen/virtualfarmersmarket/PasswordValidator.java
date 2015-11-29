package com.onnen.virtualfarmersmarket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
		
	private String passwordErrorStr = "";
	private Pattern pattern;
	private Matcher matcher;
	
	 private static final String PASSWORD_PATTERN = 
             "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,20})";
	        
	  public PasswordValidator(){
		  pattern = Pattern.compile(PASSWORD_PATTERN);
	  }
	
	public boolean IsPasswordValid(String password, String verifiedPassword) {
		boolean ret = true;
		
		matcher = pattern.matcher(password);
		
		if(!verifiedPassword.equals(password)) {
			passwordErrorStr = "Passwords do not match";
			ret = false;
		} else if(!matcher.matches()) {
			passwordErrorStr = "Password does not meet requirements";
			ret = false;
		}
		  
		return ret;
	}
	
	public String GetErrorString() {
		return passwordErrorStr;
	}
	

}
