package com.hc.app.client.test;

import com.hc.app.model.Head;

public class Md5_bin {

	public static void main(String[] args) {
		String hexString = "e3ceb5881a0a1fdaad01296d7554868d";
		
		//System.out.println(hexString.length());
		Head head = new Head();
		
		System.out.println(head.hexStringToBytes(hexString).length);
		
		System.out.println(head.bytesToHexString(head.hexStringToBytes(hexString)));
	}

}
