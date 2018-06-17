package com.hc.app.client.test.polymorphism;

public class B extends A {
	public String show(B obj){  
        return ("B and B");  
	 }  
	 public String show(A obj){  
	    return ("B and A");  
	 }  
	 //验证测试
	 public String show(C obj){  
		    return ("C and A");  
		 }  
}
