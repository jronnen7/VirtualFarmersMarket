package com.onnen.virtualfarmersmarket.utils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class SecurePassword {

	private static SecurePassword singleton;
	
	private SecurePassword() {

	}
	
	public static SecurePassword getInstance() {
		if(singleton == null) {
			singleton = new SecurePassword();	
		} return singleton;
	}
	
	public String Hash(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
	public boolean Matches(String password, String hashed) {
		return BCrypt.checkpw(password, hashed);
	}
	
}
