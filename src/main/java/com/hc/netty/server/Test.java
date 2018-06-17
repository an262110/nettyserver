package com.hc.netty.server;

import java.util.UUID;

public class Test {


    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    /**
     * Convert char to byte
     * @param c char
     * @return byte
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    public static void main(String[] args) {
       /* byte[] bytes = hexStringToBytes("68 26 22 22 11 11 11 C1 00 68 98 12 00 02 0B 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 E5 16");
        for (int i = 0; i < bytes.length; i++) {
            byte aByte = bytes[i];
            System.out.println(aByte);
        }
        System.out.println(90);*/
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr=str.replace("-", "");
        System.out.println(uuidStr);

    }
}
