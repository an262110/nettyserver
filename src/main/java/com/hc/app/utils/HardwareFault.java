package com.hc.app.utils;

import java.util.Arrays;
import java.util.Random;

public class HardwareFault {

    private static int useNo = 1;


    /***
     * 硬件遥信故障判定
     * y  代表硬件故障判断
     * r  代表软件故障判断
     *
     * @param binCode
     * @param fauleKinds
     * @return
     */
    public static String analyzer(byte[] binCode,String fauleKinds){
        byte[] b1 = Arrays.copyOfRange(binCode, 0, 1);
        byte[] b2 = Arrays.copyOfRange(binCode, 1, 2);
        byte[] b3 = Arrays.copyOfRange(binCode, 2, 3);
        byte[] b4 = Arrays.copyOfRange(binCode, 3, 4);
        String b_yx = ToolUtil.bytesToBits(b4) + ToolUtil.bytesToBits(b3) + ToolUtil.bytesToBits(b2) + ToolUtil.bytesToBits(b1);
        char[] chars = b_yx.toCharArray();
        ToolUtil.arrayReverseSelf(chars);
        StringBuffer sbu = new StringBuffer();
        /**
         * y: 代表硬件故障判断
         */
        if("y".equals(fauleKinds)){
            for (int i = 0; i < chars.length; i++) {
                char aChar = chars[i];
                if("1".equals(aChar)){
                    sbu.append("Y-"+"BIT"+i);
                }
            }
            return sbu.toString();
        }else{
            for (int i = 0; i < chars.length; i++) {
                char aChar = chars[i];
                if("1".equals(aChar)){
                    sbu.append("R-"+"BIT"+i);
                }
            }
            return sbu.toString();
        }

    }

    /**
     * 获取随机数 0-65534之间
     * @return
     */
    public static byte[] getSuiJiNo(){
        Random r = new Random();
        int n = r.nextInt(65534);// 范围是[0,65534)
        return ToolUtil.hexStringToBytes(ToolUtil.bin2HexString(Integer.toBinaryString(n)));
    }

    /**
     * 获取流水号
     * @return
     */
    public synchronized static byte[] getValue(){
        System.out.println("当前userNo数值为: " + useNo);
        String result = Integer.toBinaryString(useNo);
        if(result.length() == 1){
            result = "0000" + result;
        }else if(result.length() == 2){
            result = "000" + result;
        }else if(result.length() == 3){
            result = "00" + result;
        }else if(result.length() == 4){
            result = "0" + result;
        }
        System.out.println("result " + result);
        String bin2HexString = ToolUtil.bin2HexString("000" + result);
        if(useNo == 31){
            useNo = 0;
        }else{
            useNo++;
        }
        if(bin2HexString.length() == 1){
            bin2HexString = "0"+bin2HexString;
        }
        byte[] bytes = ToolUtil.hexStringToBytes(bin2HexString);
        return bytes;
    }



    public static byte[] toBytes(String str) {
        if(str == null || str.trim().equals("")) {
            return new byte[0];
        }

        byte[] bytes = new byte[str.length() / 2];
        for(int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }

    public static void subContent(String content,int length){
        int leng = content.length() / 2;
        for(int i =0; i < leng; i++){ // 0 1 2
            int t = i * length;
            String substring = content.substring(t, length + (t * length));
            System.out.println(substring);

        }
    }

    /**
     * 获取信息点数据
     */
    public static byte[] getInfoPoint(int t){
        /***
         * 1 1   1
         * 10 2  2
         * 100 4  4
         * 1000 8  8
         * 10000 10  16
         * 100000 20
         * 10000000 40
         * 100000000 80
         */

//        int t  = 15;

        int i1 = t % 8; //

        int i = t / 8;

        String row  = "00";
        if(i1 == 0){
            String hexString = ToolUtil.dec2HexString(i + "");
            if(hexString.length() == 1){
                row = "0" + hexString;
            }
            row = hexString;
        }else{
            String hexString = ToolUtil.dec2HexString((i+1) + "");
            row = "0" + hexString;
        }
        String s = takeNo(i1);
        byte[] bytes = ToolUtil.hexStringToBytes(row + s);
        return bytes;
    }

    /**
     * 根据枪序号获取对应的位置码
     * @param t
     * @return
     */
    public static String takeNo(int t){
        String no = "01";
        switch (t){
            case 1 : no = "01";
                break;
            case 2 : no = "02";
                break;
            case 3: no = "04";
                break;
            case 4 : no = "08";
                break;
            case 5 : no = "10";
                break;
            case 6 : no = "20";
                break;
            case 7 : no = "40";
            default: no = "80";
                break;
        }
        return no;
    }

    /**
     * 递归添加0
     * @param value
     * @param length
     * @return
     */
    public static String diGui(String value, int length){
        if(value.length() < length){
            value = "0" + value;
            return diGui(value,length);
        }else{
            return value;
        }
    }

    public static void main(String[] args) {
//        for(int i = 0;  i <= 34; i++){
//            System.out.println(getValue());
//        }
//
//        byte[] bytes = toBytes("1F");
//        System.out.println(Arrays.toString(bytes));
//        byte[] ret_length = new byte[]{(byte) 0xC1};
//
//        System.out.println("====================" + Arrays.toString(ret_length));

        System.out.println(1%8);

    }




}
