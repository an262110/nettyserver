package com.hc.app.action.kl;


//@Component
//@Lazy(false)
public class TaskTest implements Task {

//    @Scheduled(cron = "0/1 * * * * ?")
    @Override
    public void aTask() {
        System.out.println(Thread.currentThread().getName()+"*********A任务每1秒执行一次进入测试");
    }
//    @PostConstruct
    public void init(){
        System.out.println("-----> staticCronTask is init...");
    }

}
