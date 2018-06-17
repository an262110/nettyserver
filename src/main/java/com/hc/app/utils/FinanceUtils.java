package com.hc.app.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计算还款工具类(计算回款)
 * 
 * @author Zed
 *
 */
public class FinanceUtils {
	
	/**
	 * 计算(按月计数) ,返回每个月的还款金额，还款本金，还款利息
	 * 
	 * @param calMoney 计算金融 (借款金额，投资金额)
	 * 
	 * @param expire 还款期限(天数)，除以30就是月数
	 * 
	 * @param interest_rate 年利率，除以12就是月利率
	 * 
	 * @param patmentType 还款方式：A等额本金，B等额本息，C先息后本
	 * 
	 * @throws Exception
	 * 
	 * 返回字段：
	 * 还款日期 month_date 
	 * 还款期数 month_expire
	 * 月还款总额 month_amount
	 * 月还款本金 month_capital
	 * 月还款利息 month_interest
	 * 
	 */
	public static List calculate(double calMoney,int expire_p,double interest_rate_p,String paymentType,String startDate) throws Exception {
		
		if(null == paymentType || "".equals(paymentType)){
			return null;
		}
		
		List dataList = new ArrayList();
		Map dataMap = null;
		
        double borrowMoney = calMoney;
		
		int expire= expire_p;//还款期限，天数 ,，除以30就是月数
		
		double interest_rate = interest_rate_p;//年利率，，除以12就是月利率
		
		//A等额本金，B等额本息，C先息后本
		
		//还款总金额，每月还款金额(每月还款本金，每月还款利息)
		int month = expire/30;//还款月数
		
		if(paymentType.equals("C")){//C先息后本

			//--- 针对借款人
			//应还款总额
			double allInterest = borrowMoney * interest_rate/12 * expire/30;//全部应还利息
			double allPaymentMoney = borrowMoney + allInterest;
			//System.out.println("每月应还款总额＝"+allPaymentMoney);
			
			//每月应还款
			double interest = 0;
			for(int i=1;i<=month;i++){
				
				dataMap = new HashMap();
				dataMap.put("month_expire", i);//还款期数
				dataMap.put("month_date", TimeUtils.getDate(startDate, i, "yyyyMMdd"));//还款日期
				
				interest = allInterest/month ;
				//System.out.println("第"+i+"个月");
				if(i<month){
					//System.out.println("每月应还款本金＝"+0);
					//System.out.println("每月应还款利息＝"+interest);
					
					interest = formatAmount(interest);
					
					dataMap.put("month_capital", 0);//月还款本金
					dataMap.put("month_interest", interest);//月还款利息
					dataMap.put("month_amount", 0+interest);//月还款总额
					
				}else if(i==month){//最后一个月
					//System.out.println("应还款本金＝"+borrowMoney);
					//System.out.println("每月应还款利息＝"+interest);
					
					borrowMoney = formatAmount(borrowMoney);
					interest = formatAmount(interest);
					allPaymentMoney = formatAmount(allPaymentMoney);
					
					dataMap.put("month_capital", borrowMoney);//月还款本金
					dataMap.put("month_interest", interest);//月还款利息
					dataMap.put("month_amount", allPaymentMoney);//月还款总额
				}
				
				dataList.add(dataMap);
			}

		}//C先息后本 end
		
		else if(paymentType.equals("B")){//B等额本息
		
			double month_interest_rate = interest_rate/12;//月利率
			//每月应还款

//			等额本息还款法:
//				每月月供额=〔贷款本金×月利率×(1＋月利率)＾还款月数〕÷〔(1＋月利率)＾还款月数-1〕
//				每月应还利息=贷款本金×月利率×〔(1+月利率)^还款月数-(1+月利率)^(还款月序号-1)〕÷〔(1+月利率)^还款月数-1〕
//				每月应还本金=贷款本金×月利率×(1+月利率)^(还款月序号-1)÷〔(1+月利率)^还款月数-1〕
//				总利息=还款月数×每月月供额-贷款本金
			
			double monthPaymentMoney = 0;//每月应还款
			for(int i=1;i<=month;i++){
				
				double interest_rate_start = 1+month_interest_rate;
				
				double interest_rate_after = 1;//(1＋月利率)＾还款月数
				
				for(int j=1;j<=month;j++){//(1+月利率)^还款月数
					interest_rate_after = interest_rate_after *interest_rate_start;
				}
				
				monthPaymentMoney = borrowMoney * month_interest_rate *interest_rate_after / (interest_rate_after-1);
				//System.out.println("第"+i+"个月");
				//System.out.println("月应还款金额＝"+monthPaymentMoney);
				
				//每月应还本金
				//每月应还本金=贷款本金×月利率×(1+月利率)^(还款月序号-1)÷〔(1+月利率)^还款月数-1〕
				double interest_rate_after1 = 1;//(1+月利率)^(还款月序号-1)
				double monthPaymentFund = 0;//每月应还本金
				for(int a=0;a<i-1;a++){//(1+月利率)^(还款月序号-1)
					interest_rate_after1 = interest_rate_after1 * interest_rate_start;
				}
				monthPaymentFund = borrowMoney*month_interest_rate*interest_rate_after1 / (interest_rate_after-1);
				//System.out.println("每月应还本金＝"+monthPaymentFund);
				
				//每月应还利息
				//每月应还利息=贷款本金×月利率×〔(1+月利率)^还款月数-(1+月利率)^(还款月序号-1)〕÷〔(1+月利率)^还款月数-1〕
				double monthPaymentInterest = 0;
				monthPaymentInterest=borrowMoney*month_interest_rate*(interest_rate_after - interest_rate_after1)/(interest_rate_after-1);
				//System.out.println("每月应还利息＝"+monthPaymentInterest);
				
				monthPaymentMoney = formatAmount(monthPaymentMoney);
				monthPaymentFund = formatAmount(monthPaymentFund);
				monthPaymentInterest = formatAmount(monthPaymentInterest);
				
				dataMap = new HashMap();
				dataMap.put("month_expire", i);//还款期数
				dataMap.put("month_date", TimeUtils.getDate(startDate, i, "yyyyMMdd"));//还款日期
				dataMap.put("month_capital", monthPaymentFund);//月还款本金
				dataMap.put("month_interest", monthPaymentInterest);//月还款利息
				dataMap.put("month_amount", monthPaymentMoney);//月还款总额
				dataList.add(dataMap);
			}
			
		}//B等额本息 end
		
        else if(paymentType.equals("A")){//A等额本金
			
//        	等额本金还款法:
//        		每月月供额=(贷款本金÷还款月数)+(贷款本金-已归还本金累计额)×月利率
//        		每月应还本金=贷款本金÷还款月数
//        		每月应还利息=剩余本金×月利率=(贷款本金-已归还本金累计额)×月利率
//        		每月月供递减额=每月应还本金×月利率=贷款本金÷还款月数×月利率
//        		总利息=还款月数×(总贷款额×月利率-月利率×(总贷款额÷还款月数)*(还款月数-1)÷2+总贷款额÷还款月数)
//        		月利率=年利率÷12      
        		
    		double month_interest_rate = interest_rate/12;//月利率
			
    		double leftPaymentMoney = borrowMoney;//剩余本金
    					
            for(int i=1;i<=month;i++){
				
				//System.out.println("第"+i+"个月");
				
				//每月应还本金
				//每月应还本金=贷款本金÷还款月数
				double monthPaymentFund = 0;
				monthPaymentFund = borrowMoney / month;
				//System.out.println("每月应还本金＝"+monthPaymentFund);
				
				//每月应还利息
				//每月应还利息=剩余本金×月利率=(贷款本金-已归还本金累计额)×月利率
				double monthPaymentInterest = 0;
				monthPaymentInterest = leftPaymentMoney *month_interest_rate;
				//System.out.println("每月应还利息＝"+monthPaymentInterest);
				leftPaymentMoney = leftPaymentMoney-monthPaymentFund;
				
				//每月应还款
				//每月应还款=每月应还本金+每月应还利息
				double monthPaymentMoney = 0;
				monthPaymentMoney = monthPaymentFund + monthPaymentInterest;
				//System.out.println("每月应还款＝"+monthPaymentMoney);
				
				monthPaymentMoney = formatAmount(monthPaymentMoney);
				monthPaymentFund = formatAmount(monthPaymentFund);
				monthPaymentInterest = formatAmount(monthPaymentInterest);
				
				dataMap = new HashMap();
				dataMap.put("month_expire", i);//还款期数
				dataMap.put("month_date", TimeUtils.getDate(startDate, i, "yyyyMMdd"));//还款日期
				dataMap.put("month_capital", monthPaymentFund);//月还款本金
				dataMap.put("month_interest", monthPaymentInterest);//月还款利息
				dataMap.put("month_amount", monthPaymentMoney);//月还款总额
				dataList.add(dataMap);
            }
			
		}//A等额本金 end
		
		return dataList;
	}
	
	/**
	 * 保留两位小数(四舍五入)
	 * 
	 * @param d
	 * @return
	 * @throws Exception
	 */
	public static double formatAmount(double d) throws Exception {

		BigDecimal b = new BigDecimal(d);
		double f = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

		return f;
	}

	/**
	 * 保留两位小数(不进行四舍五入)
	 * 
	 * @param d
	 * @return
	 * @throws Exception
	 */
	public static double formatAmount1(double d) throws Exception {

		//java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		//String s = df.format(d);
		//double dd =Double.valueOf(s);
		
		double dd = ((int)(d*100))/100.0;
		
		return dd;
	}
	
	/**
	 * 测试
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		double borrowMoney = 1000;
		
		int expire= 90;//还款期限，天数 ,，除以30就是月数
		
		double interest_rate = 0.18;//年利率，，除以12就是月利率
		
		String paymentMethod = "B";//A等额本金，B等额本息，C先息后本
		
		String startDate = "20150602";
		
		List dataList = calculate(borrowMoney,expire,interest_rate,paymentMethod,startDate) ;
		System.out.println(dataList.size());
		System.out.println(dataList);
		
		System.out.println(formatAmount(1111.225145));
		
		System.out.println(formatAmount1(1111.225145));
	}
	
}
