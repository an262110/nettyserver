package com.hc.app.client.test.polymorphism;
/**
 * 继承和多态的经典事例
 * @author liuh
 *
 */
public class A {
	public String show(D obj){  
        return ("A and D");  
	} 
	
	public String show(A obj){  
	    return ("A and A");  
	} 
	
	public static void main(String[] args) {
		A a1 = new A();  
        A a2 = new B();  
        B b = new B();  
        C c = new C();   
        D d = new D();   
        System.out.println(a1.show(b));   //① A and A new A() 的show方法   b继承a 所以匹配A()中的show（a）
        System.out.println(a1.show(c));   //②  c继承b b继承a 还是show（a）
        System.out.println(a1.show(d));   //③ A and D A() 中show（d）方法 
        System.out.println(a2.show(b));   //④  new B() B and A
        System.out.println(a2.show(c));   //⑤  B and A
        System.out.println(a2.show(d));   //⑥  A and D
        System.out.println(b.show(b));    //⑦  
        System.out.println(b.show(c));    //⑧  
        System.out.println(b.show(d));    //⑨
        /*
        A and A
        A and A
        A and D
        B and A
        B and A
        A and D
        B and B
        B and B
        A and D
        
        指向子类的父类引用由于向上转型了，
        它只能访问父类中拥有的方法和属性，
        而对于子类中存在而父类中不存在的方法，
        该引用是不能使用的，尽管是重载该方法。
        若子类重写了父类中的某些方法，
        在调用该些方法的时候，
        必定是使用子类中定义的这些方法（动态连接、动态调用）。
        
        Java实现多态有三个必要条件：继承、重写、向上转型。

         继承：在多态中必须存在有继承关系的子类和父类。

         重写：子类对父类中某些方法进行重新定义，在调用这些方法时就会调用子类的方法。

         向上转型：在多态中需要将子类的引用赋给父类对象，只有这样该引用才能够具备技能调用父类的方法和子类的方法。

         只有满足了上述三个条件，我们才能够在同一个继承结构中使用统一的逻辑实现代码处理不同的对象，从而达到执行不同的行为。

      对于Java而言，它多态的实现机制遵循一个原则：当超类对象引用变量引用子类对象时，
      被引用对象的类型而不是引用变量的类型决定了调用谁的成员方法，但是这个被调用的方法必须是在超类中定义过的，也就是说被子类覆盖的方法。
        */
	}
}
