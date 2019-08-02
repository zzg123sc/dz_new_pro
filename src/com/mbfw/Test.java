package com.mbfw;


import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

import com.mbfw.util.MyNumberUtils;
import com.mbfw.util.MyStringUtils;

import sun.misc.BASE64Decoder;

public class Test {
	//密钥 (需要前端和后端保持一致)
    private static final String KEY = "gjsscodeeduspost";  
    //算法
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
    
    /** 
     * aes解密 
     * @param encrypt   内容 
     * @return 
     * @throws Exception 
     */  
    public static String aesDecrypt(String encrypt) {  
        try {
            return aesDecrypt(encrypt, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }  
    }  
      
    /** 
     * aes加密 
     * @param content 
     * @return 
     * @throws Exception 
     */  
    public static String aesEncrypt(String content) {  
        try {
            return aesEncrypt(content, KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }  
    }  
  
    /** 
     * 将byte[]转为各种进制的字符串 
     * @param bytes byte[] 
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制 
     * @return 转换后的字符串 
     */  
    public static String binary(byte[] bytes, int radix){  
        return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数  
    }  
  
    /** 
     * base 64 encode 
     * @param bytes 待编码的byte[] 
     * @return 编码后的base 64 code 
     */  
    public static String base64Encode(byte[] bytes){  
        return Base64.encodeBase64String(bytes);  
    }  
  
    /** 
     * base 64 decode 
     * @param base64Code 待解码的base 64 code 
     * @return 解码后的byte[] 
     * @throws Exception 
     */  
    public static byte[] base64Decode(String base64Code) throws Exception{  
        return MyStringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);  
    }  
  
      
    /** 
     * AES加密 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的byte[] 
     * @throws Exception 
     */  
    public static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128);  
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);  
        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));  
  
        return cipher.doFinal(content.getBytes("utf-8"));  
    }  
  
  
    /** 
     * AES加密为base 64 code 
     * @param content 待加密的内容 
     * @param encryptKey 加密密钥 
     * @return 加密后的base 64 code 
     * @throws Exception 
     */  
    public static String aesEncrypt(String content, String encryptKey) throws Exception {  
        return base64Encode(aesEncryptToBytes(content, encryptKey));  
    }  
  
    /** 
     * AES解密 
     * @param encryptBytes 待解密的byte[] 
     * @param decryptKey 解密密钥 
     * @return 解密后的String 
     * @throws Exception 
     */  
    public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {  
        KeyGenerator kgen = KeyGenerator.getInstance("AES");  
        kgen.init(128);  
  
        Cipher cipher = Cipher.getInstance(ALGORITHMSTR);  
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));  
        byte[] decryptBytes = cipher.doFinal(encryptBytes);  
        return new String(decryptBytes,"utf-8");  
    }  
  
  
    /** 
     * 将base 64 code AES解密 
     * @param encryptStr 待解密的base 64 code 
     * @param decryptKey 解密密钥 
     * @return 解密后的string 
     * @throws Exception 
     */  
    public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {  
        return MyStringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);  
    }  
    
    
    /**
     * 测试
     */
    public static void main(String[] args) throws Exception {  
        String content = "pageId=&pageName=测试三十多岁&tableId=&tableName=&page=1&limit=10"; 
        System.out.println("加密前：" + content);  
        System.out.println("加密密钥和解密密钥：" + KEY);  
        String encrypt = aesEncrypt(content); 
        System.out.println(encrypt.length()+"加密后：" + encrypt); 
        String decrypt = aesDecrypt(encrypt);
        
        System.out.println("解密后：" + decrypt);  
//        Map<String, Object> jsonMap = new HashMap<String, Object>();
//        Iterator<String> it = jsonMap.keySet().iterator();
//        StringBuffer whereSql = new StringBuffer();
//        String sql = "";
//        String fromSql = ","+(sql.substring(6,sql.indexOf(" from ")>0?sql.indexOf(" from "):sql.indexOf(" FROM ")));
//		fromSql = fromSql.replaceAll("\r\n", "").trim();
//        while(it.hasNext()) {
//        	String key = it.next();
//        	Object value = jsonMap.get(key);
//        	if (value==null || MyStringUtils.isBlank(value+"")) {
//        		continue;
//        	}
//        	String tmpKey = key.toUpperCase();
//        	String sqlAlias =  MyStringUtils.getSqlFieldByAliasName(fromSql, tmpKey, null);
//        	//开始日期
//        	if ("STARTDATE".equals(tmpKey) || tmpKey.endsWith("SD_")) {
//        		whereSql.append(" and "+sqlAlias+" >='"+value+"'");
//        	} else if ("ENDDATE".equals(tmpKey) || tmpKey.endsWith("ED_")) {//结束日期
//        		whereSql.append(" and "+sqlAlias+" <='"+value+"'");
//            } 
//        }
    } 

}
