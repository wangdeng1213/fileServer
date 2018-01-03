package com.jmw.filesite.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

public class AesCBC {
	public String Encrypt(String sSrc)throws Exception{
		return this.Encrypt(sSrc, "ap$*25639vnt, @h", "18T6]y194#5/3!3>");
	}
	public String Decrypt(String sSrc)throws Exception{
		return this.Decrypt(sSrc, "ap$*25639vnt, @h", "18T6]y194#5/3!3>");
	}
	/**
	 * ASE加密算法(采用CBC工作模式,PKCS5Padding填充方式)
	 * @param 加密源
	 * @param 加密密钥
	 * @param 初始向量
	 * @return 加密后的字符串
	 * @throws Exception
	 */
    protected String Encrypt(String sSrc, String sKey, String sIv) throws Exception {  
        if (sKey == null) {  
            System.out.print("Key为空null");  
            return null;  
        }  
        // 判断Key是否为16位  
        if (sKey.length() != 16) {  
            System.out.print("Key长度不是16位");  
            return null;  
        }  
        byte[] raw = sKey.getBytes();  
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");//"算法/模式/补码方式"  
        IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度  
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,iv);  
        byte[] encrypted = cipher.doFinal(sSrc.getBytes());  
        return new Base64().encodeBase64String(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。  
    }
  
    /**
     * ASE解密算法(采用CBC工作模式,PKCS5Padding填充方式)
     * @param 加密源
     * @param 加密密码
     * @param 初始向量
     * @return 加密后的字符串
     * @throws Exception
     */
    protected String Decrypt(String sSrc, String sKey, String sIv) throws Exception {   
            // 判断Key是否正确  
            if (sKey == null) {  
                System.out.print("Key为空null");  
                return null;  
            }  
            // 判断Key是否为16位  
            if (sKey.length() != 16) {  
                System.out.print("Key长度不是16位");  
                return null;  
            }
            byte[] raw = sKey.getBytes("utf-8");  
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
            IvParameterSpec iv = new IvParameterSpec(sIv.getBytes());  
            cipher.init(Cipher.DECRYPT_MODE, skeySpec,iv);  
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密   
            byte[] original = cipher.doFinal(encrypted1);  
            String originalString = new String(original);  
            return originalString;     
    }
}
