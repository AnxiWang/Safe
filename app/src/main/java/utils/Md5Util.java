package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {

//	用MD5加密
	public static String encoder(String psd) {
		try {
			psd = psd +"com.wang.anxi.safe";
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] bs = digest.digest(psd.getBytes());
			StringBuffer stringBuffer = new StringBuffer();
			for ( byte b : bs) {
				int i = b &0xff;
				String hexstring = Integer.toHexString(i);
				if (hexstring.length()<2) {
					hexstring = "0"+hexstring;					
				}
				stringBuffer.append(hexstring);
			}
//			System.out.println( stringBuffer.toString());
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}


}
