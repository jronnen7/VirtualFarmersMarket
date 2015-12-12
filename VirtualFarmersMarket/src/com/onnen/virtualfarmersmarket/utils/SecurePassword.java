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
	
	private static final String MY_KEY = "a135f7c5-ddca-4b1f-8af0-2255be5b836a";
	private byte[] key;
	
	private SecurePassword() throws NoSuchAlgorithmException {
		byte[] keyStart = MY_KEY.getBytes();
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
		sr.setSeed(keyStart);
		kgen.init(256, sr); // 192 and 256 bits may not be available
		SecretKey skey = kgen.generateKey();
		key = skey.getEncoded();  
	}
	
	public static SecurePassword getInstance() {
		if(singleton == null) {
			try {
				singleton = new SecurePassword();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		} return singleton;
	}
	
	public String EncryptPassword(String password) throws Exception {
		byte[] hash =  Encrypt(key,password.getBytes());
		return Base64.encodeToString(hash, Base64.URL_SAFE);
	}
	
/*	public String EncryptPassword(String clearText) {
	    byte[] encryptedText = null;
	    try {
	        byte[] keyData = MY_KEY.getBytes();
	        SecretKey ks = new SecretKeySpec(keyData, "AES");
	        Cipher c = Cipher.getInstance("AES");
	        c.init(Cipher.ENCRYPT_MODE, ks);
	        encryptedText = c.doFinal(clearText.getBytes("UTF-8"));
	        return Base64.encodeToString(encryptedText, Base64.DEFAULT);
	    } catch (Exception e) {
	        return null;
	    }
	}*/
	
	public String DecryptPassword(String hash) throws Exception {
		byte[] tmp = Base64.decode(hash, Base64.URL_SAFE);
		return new String(Decrypt(key,tmp), "utf-8");
	}
/*	public String DecryptPassword(String hash) {
	   byte[] clearText = null;
	    try {
	        byte[] keyData = MY_KEY.getBytes();
	        SecretKey ks = new SecretKeySpec(keyData, "AES");
	        Cipher c = Cipher.getInstance("AES");
	        c.init(Cipher.DECRYPT_MODE, ks);
	        clearText = c.doFinal(Base64.decode(hash, Base64.DEFAULT));
	        return new String(clearText, "UTF-8");
	    } catch (Exception e) {
	        return null;
	    }
	} */
	
	private byte[] Encrypt(byte[] raw, byte[] clear) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
	    byte[] encrypted = cipher.doFinal(clear);
	    return encrypted;
	}
	private byte[] Decrypt(byte[] raw, byte[] encrypted) throws Exception {
	    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
	    Cipher cipher = Cipher.getInstance("AES");
	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] decrypted = cipher.doFinal(encrypted);
	    return decrypted;
	}
}
