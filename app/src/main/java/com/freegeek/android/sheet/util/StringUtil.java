package com.freegeek.android.sheet.util;

import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;

import java.security.MessageDigest;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/1/29.
 */
public class StringUtil {
    public static class TwoLine{

        private String line1,line2;
        private int textSize1 = 30,textSize2=20;
        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public void setText(String line1,String line2){
            setLine1(line1);
            setLine2(line2);
        }

        public void setTextSize1(int textSize1) {
            this.textSize1 = textSize1;
        }

        public void setTextSize2(int textSize2) {
            this.textSize2 = textSize2;
        }

        public SpannableString getContent(){
            SpannableString spannableString;
            if(line2.length() == 0){
                spannableString = new SpannableString(line1);
            }else{
                spannableString = new SpannableString(line1+"\n"+line2);
                spannableString.setSpan(new AbsoluteSizeSpan(textSize2),line1.length() + 1,spannableString.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            spannableString.setSpan(new AbsoluteSizeSpan(textSize1),0,line1.length(),SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
            return spannableString;
        }

    }

    /**
     * 判断是不是一个合法的电子邮件地址
     * @param email
     * @return
     */
    public static boolean isEmail(String email){
        Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
        if(email == null || email.equals("")) return false;
        email = email.toLowerCase();
        return emailer.matcher(email).matches();
    }

    /**
     * MD5佳铭
     * @param str
     * @return
     */
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];
        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }



}
