package me.zeroeightsix.kami.util;

import java.io.UnsupportedEncodingException;
import java.security.*;
import java.util.ArrayList;

/**
 * Created by Hexeption on 16/01/2017.
 * Modified by FINZ0
 */
public final class HwidStuff {

    /**
     * Gives a HWID I.E (350-30a-3ae-30e-304-3d6-37d-359-371-3e0-3d8-3e1-369-3b2-34a-314) - Hexeption
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static String getHWID() throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String s = "";
        final String main = System.getenv("COMPUTERNAME") + System.getProperty("os.name") + System.getProperty("os.version").trim();
        final byte[] bytes = main.getBytes("UTF-8");
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        final byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        for (final byte b : md5) {
            s += Integer.toHexString((b & 0xFF) | 0x300).substring(0, 3);
            if (i != md5.length - 1) {
                s += "-";
            }
            i++;
        }
        return s;
    }
    
    public static ArrayList<String> validHwids = new ArrayList<String>();
    
    public static void initValidHwids() {
    	validHwids.add("362-306-37c-38b-3c5-33f-357-360-3b8-3f6-340-3bc-3c7-32a-31a-302"); // FINZ0
    	validHwids.add("3c6-38b-341-307-340-30a-34a-30f-3d4-32a-317-3c8-38d-346-3e8-382"); //kettu
    	validHwids.add("334-33f-3d6-3e6-357-38a-36f-34c-33b-3e5-39b-393-3d0-3a7-3de-33e"); //Plox
    	validHwids.add("364-323-315-397-3f9-374-359-381-3f3-392-3ba-37d-3e7-30b-3b6-362"); //Distaf
    	validHwids.add("33e-330-361-3ae-33a-3df-322-3ca-376-30f-3aa-333-30c-3b9-363-3e0"); //Sushi
    }
    
    public static ArrayList<String> getValidHwids() {
        return validHwids;
    }

}