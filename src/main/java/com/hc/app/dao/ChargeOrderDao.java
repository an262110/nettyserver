package com.hc.app.dao;

import com.hc.common.database.BaseJdbcDao;
import com.hc.common.utils.hk.HKLogUtils;
import org.springframework.stereotype.Repository;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ChargeOrderDao extends BaseJdbcDao {
   public void updateInfo(Map params){
	   List<Object> args=new LinkedList<Object>();
	   
	   StringBuilder sqlStr=new StringBuilder("update HC_CHARGE_ORDER set UPDATE_TIME = sysdate ");
	     if(params.get("CURRENT_V")!=null){
	   		 sqlStr.append(",CURRENT_V = ?");
	   		 args.add(params.get("CURRENT_V"));
	     }
	     if(params.get("CURRENT_A")!=null){
	   		sqlStr.append(",CURRENT_A = ?");
	   		args.add(params.get("CURRENT_A"));
	     }
	     if(params.get("TOTAL_CHARGE_QUANTITY")!=null){
	    	 sqlStr.append(",TOTAL_CHARGE_QUANTITY = ?");
	    	 args.add(params.get("TOTAL_CHARGE_QUANTITY"));
	     }
	     if(params.get("TOTAL_CHARGE_TIMES")!=null){
	    	 sqlStr.append(",TOTAL_CHARGE_TIMES = ?");
	    	 args.add(params.get("TOTAL_CHARGE_TIMES"));
	     }
	     
	     if(params.get("TOTAL_CHARGE_MONEY")!=null){
	    	 sqlStr.append(",TOTAL_CHARGE_MONEY = ?");
	    	 args.add(params.get("TOTAL_CHARGE_MONEY"));
	     }
	   		
	     if(params.get("TOTAL_SERVICE_MONEY")!=null){
	    	 sqlStr.append(",TOTAL_SERVICE_MONEY =?");
	    	 args.add(params.get("TOTAL_SERVICE_MONEY"));
	     }	
	     
	     if(params.get("ORI_CHARGE_MONEY")!=null){
	    	 sqlStr.append(",ORI_CHARGE_MONEY = ?");
	    	 args.add(params.get("ORI_CHARGE_MONEY"));
	     }
	   		
	     if(params.get("ORI_SERVICE_MONEY")!=null){
	    	 sqlStr.append(",ORI_SERVICE_MONEY =?");
	    	 args.add(params.get("ORI_SERVICE_MONEY"));
	     }	
	   	 
	     if(params.get("CHARGE_GUN")!=null){
	    	 sqlStr.append(",CHARGE_GUN = ?");
	    	 args.add(params.get("CHARGE_GUN"));
	     }	
	     if(params.get("ORDER_STATE")!=null){
	    	 sqlStr.append(",ORDER_STATE = ?");
	    	 args.add(params.get("ORDER_STATE"));
	     }	
	     
	     if(params.get("CHARGE_RET_39")!=null){
	    	 sqlStr.append(",CHARGE_RET_39 = ?");
	    	 args.add(params.get("CHARGE_RET_39"));
	     }		     
	     if(params.get("CHARGE_RET_39_DESC")!=null){
	    	 sqlStr.append(",CHARGE_RET_39_DESC = ?");
	    	 args.add(params.get("CHARGE_RET_39_DESC"));
	     }
	   	 
	     if(params.get("CHARGE_USERID_9")!=null){
	    	 sqlStr.append(",CHARGE_USERID_9 = ?");
	    	 args.add(params.get("CHARGE_USERID_9"));
	     }
	     //开始充电
	     if(params.get("START_CHARGE_TIME")!=null){
	    	 sqlStr.append(",START_CHARGE_TIME = sysdate");
	    	 //args.add(params.get("START_CHARGE_TIME"));
	     }
	     //结束充电
	     if(params.get("END_CHARGE_TIME")!=null){
	    	 sqlStr.append(",END_CHARGE_TIME = sysdate");
	    	 //args.add(params.get("END_CHARGE_TIME"));
	     }
	   //SOC
	     if(params.get("SOC")!=null){
	    	 sqlStr.append(",SOC=?");
	    	 args.add(params.get("SOC"));
	     }
	   //BMS_TYPE 电池类型
	     if(params.get("BMS_TYPE")!=null){
	    	 sqlStr.append(",BMS_TYPE=?");
	    	 args.add(params.get("BMS_TYPE"));
	     }
	     //POLICY_ID 资费策略
	     if(params.get("POLICY_ID")!=null){
	    	 sqlStr.append(",POLICY_ID=?");
	    	 args.add(params.get("POLICY_ID"));
	     }
	   		sqlStr.append(" where CHARGE_ORDER_ID=?");
	   		args.add(params.get("CHARGE_ORDER_ID"));
	   	Object[] argsArray=args.toArray();
	   	try {
			this.update(sqlStr.toString(),argsArray);
			//System.out.println("开始充电结束充电sql："+sqlStr.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   }
   
   public Map findLatest(String pileNo) throws Exception{
	   String sqlStr="select CHARGE_ORDER_ID from ("
	   		+ "select CHARGE_ORDER_ID from HC_CHARGE_ORDER where CHARGE_PILE_SERI = ? and ORDER_STATE = '02'"
	   		+ "order by CREATE_TIME desc) where rownum=1";
	   return this.querySingleRow(sqlStr,new String[]{pileNo});
	  
   }
   
   public Map findLatestByGunCode(String gunCode) throws Exception{
	   String sqlStr="select CHARGE_ORDER_ID from ("
	   		+ "select CHARGE_ORDER_ID from HC_CHARGE_ORDER where GUN_CODE = ? and ORDER_STATE = '02' "
	   		+ "order by CREATE_TIME desc) where rownum=1";
	   return this.querySingleRow(sqlStr,new String[]{gunCode});
	  
   }
   
   public Map findByOrderSeril(String orderSeril) throws Exception{
	   String sqlStr="select GUN_CODE,CHARGE_USERID_9,CHARGE_ORDER_ID,POLICY_ID,USER_ID,NVL( TOTAL_CHARGE_QUANTITY,'0') TOTAL_CHARGE_QUANTITY,"
	   		+ "NVL(TOTAL_CHARGE_TIMES,'0') TOTAL_CHARGE_TIMES,"
	   		+ "NVL(TOTAL_CHARGE_MONEY,0) TOTAL_CHARGE_MONEY,"
	   		+ "NVL(TOTAL_SERVICE_MONEY,0) TOTAL_SERVICE_MONEY,"
	   		+ "ORDER_TYPE,NVL(PAY_MONEY,0) PAY_MONEY,ORDER_STATE,"
	   		+ "NVL(ELE_DISCOUNT,100) ELE_DISCOUNT,NVL(SERVICE_DISCOUNT,100) SERVICE_DISCOUNT,"
	   		+ "NVL(DISCOUNT_FLAG,'0') DISCOUNT_FLAG,"
	   		+ "NVL(ORI_CHARGE_MONEY,0) ORI_CHARGE_MONEY,NVL(ORI_SERVICE_MONEY,0) ORI_SERVICE_MONEY "
	   		+ " from HC_CHARGE_ORDER where CHARGE_USERID_9 = ? ";
	   		
	   return this.querySingleRow(sqlStr,new String[]{orderSeril});
	  
   }
   
   public Map findByOrderId(String orderId) throws Exception{
	   String sqlStr="select CHARGE_ORDER_ID,POLICY_ID,USER_ID,NVL( TOTAL_CHARGE_QUANTITY,'0') TOTAL_CHARGE_QUANTITY,"
	   		+ "NVL(TOTAL_CHARGE_TIMES,'0') TOTAL_CHARGE_TIMES,"
	   		+ "NVL(TOTAL_CHARGE_MONEY,0) TOTAL_CHARGE_MONEY,"
	   		+ "NVL(TOTAL_SERVICE_MONEY,0) TOTAL_SERVICE_MONEY,"
	   		+ "ORDER_TYPE,NVL(PAY_MONEY,0) PAY_MONEY,ORDER_STATE ,"
	   		+ "NVL(ELE_DISCOUNT,100) ELE_DISCOUNT,"
	   		+ "NVL(SERVICE_DISCOUNT,100) SERVICE_DISCOUNT,"
	   		+ "NVL(DISCOUNT_FLAG,'0'),"
	   		+ "NVL(ORI_CHARGE_MONEY,0) ORI_CHARGE_MONEY,NVL(ORI_SERVICE_MONEY,0) ORI_SERVICE_MONEY,"
	   		+ "GUN_CODE,CHARGE_USERID_9  "
	   		+ " from HC_CHARGE_ORDER where  CHARGE_ORDER_ID = ? ";
	   		
	   return this.querySingleRow(sqlStr,new String[]{orderId});
	  
   }
   
   public Map searchOrderDetail(String business_no,String status) throws Exception{
	  // String sqlStr="select * from HC_charge_order co where co.charge_order_id = ? and co.order_state='01'";
	   String sqlStr="select * from HC_charge_order co where co.charge_order_id = ? and co.order_state=?";
	   
	   return this.querySingleRow(sqlStr,new String[]{business_no,status});
	  
   }
   
   public Map searchOrderDetailHk(String business_no) throws Exception{
		  // String sqlStr="select * from HC_charge_order co where co.charge_order_id = ? and co.order_state='01'";
		   String sqlStr="select * from HC_charge_order co where co.charge_userid_9=?";
		   
		   return this.querySingleRow(sqlStr,new String[]{business_no});
		  
	   }
   
   public String obtainUserSeq(String HC_charge_userid) throws Exception{
	   return this.querySequence(HC_charge_userid);
	  
   }

	public String addHCCharge(Map paramMap) throws Exception {
		//String charge_id = this.querySequence("HC_charge").toString();
		//paramMap.put("CHARGE_ID", charge_id);
		super.add("HC_charge", paramMap);
		return paramMap.get("CHARGE_USERID_9").toString();
	}

	public Map searchHCChargeDetail(String charge_userid) throws Exception {
		String sqlStr="select * from HC_charge dc where dc.charge_userid_9=? order by UPDATE_TIME desc";
		return this.querySingleRow(sqlStr,new String[]{charge_userid});
	}

	public String updateHCCharge(Map paramMap) throws Exception {
		String sql = "update HC_charge dc set ";
		if("02".equals(paramMap.get("CHARGE_TYPE_4"))){
			sql+="dc.stop_status=?,";
		}else{
			sql+="dc.charge_ret_39=?,";
		}
				sql +="dc.charge_ret_userid_9=?,dc.charge_ret_pileno_41=?,dc.charge_ret_mac_63=?,dc.update_time=sysdate where dc.charge_userid_9=?  ";
		List<Object> args=new LinkedList<Object>();
		args.add(paramMap.get("CHARGE_RET_39"));
		args.add(paramMap.get("CHARGE_RET_USERID_9"));
		args.add(paramMap.get("CHARGE_RET_PILENO_41"));
		args.add(paramMap.get("CHARGE_RET_PILENO_63"));
		//args.add(paramMap.get("CHARGE_TYPE_4"));
		args.add(paramMap.get("CHARGE_USERID_9"));
		
		this.update(sql,args.toArray());
		return paramMap.get("CHARGE_USERID_9").toString();
	}
	
	//更新
	public String updateHCChargeUserid(Map paramMap) throws Exception {
		String sql = "update HC_charge dc set dc.charge_ret_39=?,dc.charge_ret_userid_9=?,dc.charge_ret_pileno_41=?,dc.charge_ret_mac_63=?,dc.update_time=sysdate where dc.charge_userid_9=?";
		List<Object> args=new LinkedList<Object>();
		args.add(paramMap.get("CHARGE_RET_39"));
		args.add(paramMap.get("CHARGE_RET_USERID_9"));
		args.add(paramMap.get("CHARGE_RET_PILENO_41"));
		args.add(paramMap.get("CHARGE_RET_PILENO_63"));
		args.add(paramMap.get("CHARGE_USERID_9"));
		this.update(sql,args.toArray());
		return paramMap.get("CHARGE_USERID_9").toString();
	}
	
	public void updateHCChargeType(String userFlagNo,String type) throws Exception {
		String sql = "update HC_charge dc set dc.CHARGE_TYPE_4=?,dc.update_time=sysdate where dc.charge_userid_9=?";
		List<Object> args=new LinkedList<Object>();
		args.add(type);
		args.add(userFlagNo);
		
		this.update(sql,args.toArray());
		
	}
   
	 public void updateInfoHk(Map params){
		   List<Object> args=new LinkedList<Object>();
		   
		   StringBuilder sqlStr=new StringBuilder("update HC_CHARGE_ORDER set UPDATE_TIME = sysdate ");

		     if(params.get("ORDER_STATE")!=null){
		    	 sqlStr.append(",ORDER_STATE = ?");
		    	 args.add(params.get("ORDER_STATE"));
		     }			    		     
		   
		     //结束充电
		     if(params.get("END_CHARGE_TIME")!=null){
		    	 sqlStr.append(",END_CHARGE_TIME = sysdate");
		    	 //args.add(params.get("END_CHARGE_TIME"));
		     }
		     
		     if(params.get("TOTAL_CHARGE_QUANTITY")!=null){
		    	 sqlStr.append(",TOTAL_CHARGE_QUANTITY = ?");
		    	 args.add(params.get("TOTAL_CHARGE_QUANTITY"));
		     }
		     if(params.get("TOTAL_CHARGE_TIMES")!=null){
		    	 sqlStr.append(",TOTAL_CHARGE_TIMES = ?");
		    	 args.add(params.get("TOTAL_CHARGE_TIMES"));
		     }
		     
		     if(params.get("TOTAL_CHARGE_MONEY")!=null){
		    	 sqlStr.append(",TOTAL_CHARGE_MONEY = ?");
		    	 args.add(params.get("TOTAL_CHARGE_MONEY"));
		     }
		   		
		     if(params.get("TOTAL_SERVICE_MONEY")!=null){
		    	 sqlStr.append(",TOTAL_SERVICE_MONEY =?");
		    	 args.add(params.get("TOTAL_SERVICE_MONEY"));
		     }	
		     
		     if(params.get("ORI_CHARGE_MONEY")!=null){
		    	 sqlStr.append(",ORI_CHARGE_MONEY = ?");
		    	 args.add(params.get("ORI_CHARGE_MONEY"));
		     }
		   		
		     if(params.get("ORI_SERVICE_MONEY")!=null){
		    	 sqlStr.append(",ORI_SERVICE_MONEY =?");
		    	 args.add(params.get("ORI_SERVICE_MONEY"));
		     }	
		   		sqlStr.append(" where CHARGE_USERID_9=? ");
		   		args.add(params.get("CHARGE_USERID_9"));
		   	Object[] argsArray=args.toArray();
		   	try {
				this.update(sqlStr.toString(),argsArray);
				System.out.println("开始充电结束充电sql："+sqlStr.toString());
			} catch (Exception e) {
				
				e.printStackTrace();
			}
	   }
	 
	 public void singleCheckBill(String orderId) throws Exception {
		 
		String sql="select do.user_id,do.order_state,round((nvl(to_number(do.total_charge_money),0)+nvl(to_number(do.total_service_money),0))) AMOUNT,do.user_name,to_number(nvl(do.total_charge_quantity,0)) quantity,do.pay_money pay,do.charge_order_id order_id,do.user_name real_name,do.order_type,do.PAY_AGENT_ID from HC_charge_order do where do.CHARGE_ORDER_ID=?";
		Map map = this.querySingleRow(sql,new String[]{orderId});
		
		
		if(map==null){
			
			HKLogUtils.info("对账订单不存在");
			return;
		}
						
		if("04".equals(map.get("ORDER_STATE").toString())||"05".equals(map.get("ORDER_STATE").toString())){
			HKLogUtils.info("重复结算");
			return;
		}
		
		String sql_refund="select * from HC_refund dr where dr.order_id=?";
		Map map_refund = this.querySingleRow(sql_refund,new String[]{orderId});
		
		if(map_refund!=null&&orderId.equals(map_refund.get("ORDER_ID"))){
			HKLogUtils.info("自动任务已经结算");
			return;
		}
		
		String user_id = map.get("USER_ID").toString();
		String user_name = map.get("USER_NAME").toString();
		
		String quantity = map.get("QUANTITY").toString();
		
		Double amountDouble=Double.valueOf(map.get("AMOUNT").toString())+0.5;//四舍五入
		int amount =amountDouble.intValue();
		
		String pay = map.get("PAY").toString();//支付金额
		String accountBalance=pay;//账户余额
		String order_id = map.get("ORDER_ID").toString();
		
		String real_name = map.get("REAL_NAME").toString();
		String order_type = map.get("ORDER_TYPE").toString();
		Map paramMap = new HashMap();
		if("2".equals(order_type)){
			
			if(Integer.valueOf(pay)<=amount){//欠费不需要退款
				paramMap.put("STATUS", "01");
			}else{
				paramMap.put("STATUS", "00");
			}
			
			paramMap.put("detail_data", "单笔微信公众号退款");
			
		}else if("5".equals(order_type)){//代付处理
			
			String agentId = map.get("PAY_AGENT_ID").toString();
			String sqlAccount = "select frozen_money,TOTAL_MONEY from HC_acct_info where user_id=?";
			Map mapAccount = this.querySingleRow(sqlAccount,new String[]{user_id});
			pay=mapAccount.get("FROZEN_MONEY").toString();
			
			//查找代付人的账户余额
			String sqlAccount2 = "select TOTAL_MONEY from HC_acct_info where user_id=?";
			Map mapAccount2 = this.querySingleRow(sqlAccount2,new String[]{agentId});
			accountBalance=mapAccount2.get("TOTAL_MONEY").toString();
			
			String sqlupAcc = "update HC_acct_info dc set dc.update_time=sysdate,dc.frozen_money=0,dc.status=0 where dc.user_id=?";
			this.update(sqlupAcc, new Object[]{user_id});	
			paramMap.put("STATUS", "01");
			paramMap.put("detail_data", "单笔app退款");
			
			//将钱退回到主账户
			String refund=String.valueOf((Integer.valueOf(pay)-Integer.valueOf(amount)));
			String sqlupAcc2 = "update HC_acct_info dc set dc.update_time=sysdate,dc.total_money =to_number(dc.total_money)+? where dc.user_id=?";
			this.update(sqlupAcc2, new Object[]{refund,agentId});
			
			
		}else{
			String sqlAccount = "select frozen_money,TOTAL_MONEY from HC_acct_info where user_id=?";
			Map mapAccount = this.querySingleRow(sqlAccount,new String[]{user_id});
			
			pay=mapAccount.get("FROZEN_MONEY").toString();
			accountBalance=mapAccount.get("TOTAL_MONEY").toString();
			
			String sqlupAcc = "update HC_acct_info dc set dc.total_money =to_number(dc.total_money)-?,dc.update_time=sysdate,dc.frozen_money=0,dc.status=0 where dc.user_id=?";
			this.update(sqlupAcc, new Object[]{amount,user_id});	
			paramMap.put("STATUS", "01");
			paramMap.put("detail_data", "单笔app退款");
		}
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String batch_no = this.querySequence("HC_refund").toString();
		paramMap.put("batch_no", df.format(new Date())+batch_no);
		paramMap.put("batch_num", "1");
		paramMap.put("USER_ID", user_id);
		paramMap.put("user_name", user_name);
		paramMap.put("quantity", quantity);
		paramMap.put("amount", amount);
		paramMap.put("pay", pay);
		paramMap.put("order_id", order_id);
		paramMap.put("msg", "对账单笔记录");
		paramMap.put("real_name", real_name);
		paramMap.put("order_type", order_type);
		paramMap.put("account_balance",accountBalance);
		paramMap.put("pay_agent_id",map.get("PAY_AGENT_ID")==null?"":map.get("PAY_AGENT_ID").toString());
		
		super.add("HC_refund", paramMap);
		
		String orderSql="update HC_charge_order set order_state = '04',update_time=sysdate where CHARGE_ORDER_ID=?";
		this.update(orderSql, new String[]{orderId});
		HKLogUtils.info("结算完毕");
	}
	 
	 public Map findByOrderStatus(String pileno_zw, int gun_no,String order_status) throws Exception{
		   String sqlStr="select GUN_CODE,CHARGE_USERID_9,CHARGE_ORDER_ID,POLICY_ID,USER_ID,"+
			        "NVL( TOTAL_CHARGE_QUANTITY,'0') TOTAL_CHARGE_QUANTITY,"+
				   	"	NVL(TOTAL_CHARGE_TIMES,'0') TOTAL_CHARGE_TIMES,"+
				   	"	NVL(TOTAL_CHARGE_MONEY,0) TOTAL_CHARGE_MONEY,"+
				   	"	NVL(TOTAL_SERVICE_MONEY,0) TOTAL_SERVICE_MONEY,"+
				   	"	ORDER_TYPE,NVL(PAY_MONEY,0) PAY_MONEY,ORDER_STATE,"+
				   	"	NVL(ELE_DISCOUNT,100) ELE_DISCOUNT,NVL(SERVICE_DISCOUNT,100) SERVICE_DISCOUNT,"+
				   	"	NVL(DISCOUNT_FLAG,'0') DISCOUNT_FLAG,"+
				   	"	NVL(ORI_CHARGE_MONEY,0) ORI_CHARGE_MONEY,NVL(ORI_SERVICE_MONEY,0) ORI_SERVICE_MONEY "+
				   	"	 from HC_CHARGE_ORDER do,HC_charge_gun dg"+
			        " where do.charge_pile_seri=dg.pile_seri"+
			        "  and do.charge_gun=dg.gun_no         "+
			        "  and do.charge_pile_seri=?"+
			        "  and do.charge_gun=?"+
			        "  and do.order_state=?";
		   		
		   return this.querySingleRow(sqlStr,new Object[]{pileno_zw,gun_no,order_status});
		  
	   }
}
