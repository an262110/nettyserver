package com.hc.app.client.test;

public class JavaB {

	public static void main(String[] args) {
		int a = 10;
		for (int i=0;i<32;i++){
			int t = (a&0x80000000>>>i)>>>(31-i);
			System.out.print(t);
		}
		System.out.println();
		System.out.println(Runtime.getRuntime().maxMemory()/1000/1000);
	}

}
