package com.hc.common.utils.hk;

import org.jpos.iso.ISOUtil;

public class test {

	public static void main(String[] args) {
		String hexString = "000E5358544A5F41433130303130000831323334353600043030";
		byte[] bb = ParseUtil.hexStringToBytes(hexString);
		
		String myLRC= ISOUtil.hexString(new byte[]{ParsePackage.getEOR(bb)});
		System.out.println(myLRC);

	}

}
