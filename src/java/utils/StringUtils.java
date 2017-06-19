package utils;

import java.awt.AlphaComposite;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class StringUtils {

	private static String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	
	public static boolean canParseToNumber(String valueAsString){
		try{
			new BigDecimal(valueAsString);
			return true;
		}
		catch(Exception e){
			return false;
		}
		
	}
	
	public static char getNextChar(char thisChar){
		char[] alph = alphabet.toCharArray();
		int indexOfThisChar = 0;
		char next = alph[0];
		for(int i=0; i<alph.length-1; i++){
			if(alph[i]==Character.toUpperCase(thisChar)){
				next = (char) alph[i + 1];
				i=alph.length;
			}
		}
		
		if(Character.isLowerCase(thisChar)){
			next = Character.toLowerCase(thisChar);
		}
		return next;
	}
	
	public static String returnMd5String(String plaintext){
		try{
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.reset();
			m.update(plaintext.getBytes());
			byte[] digest = m.digest();
			BigInteger bigInt = new BigInteger(1,digest);
			String hashtext = bigInt.toString(16);

			while(hashtext.length() < 32 ){
			  hashtext = "0"+hashtext;
			}
			
			return hashtext;

		}
		catch(NoSuchAlgorithmException e){
			return null;
		}
	}
}
