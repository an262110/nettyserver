package com.hc.common.utils;

import java.util.Random;

public class RandomString {
  private final static String allString="1234567890abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
  
  public static String getRandomString(int length){
	  Random r=new Random();
	  char[] c=new char[length];
	  for(int i=0;i<c.length;i++){
		  c[i]=allString.charAt(r.nextInt(62));
	  }
	  
	  return new String(c);
  }
}
