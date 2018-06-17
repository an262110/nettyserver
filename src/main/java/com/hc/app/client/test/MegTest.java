package com.hc.app.client.test;

//java中 多个类操作同一个对象
public final class MegTest {
		
		private static MegTest megTest =null;
		
		private MegTest(){}//让该类不能被外部实例化
		
		static{//静态块优先执行
			//megTest=new Configuration().configure().buildSessionFactory();//一个得到对象
		}
		
		public static MegTest getMegTest(){
			return megTest;//返回该对象
		}
	
	public static void main(String[] args) {

	}

}
