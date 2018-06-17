package com.hc.app.action;

import com.hc.app.service.ChargePileService;
import com.hc.app.service.TariffPolicyService;
import com.hc.app.utils.MAC;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 电价请求处理类
 * @Title:TX1040.java
 * @Package:com.hc.app.action
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年6月22日 下午5:54:58
 * @Version V1.0
 */
@Component("TX1040")
public class TX1040 implements BaseAction {
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private TariffPolicyService tariffPolicyServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		byte[] req=(byte[])msg;
		ISOMsg request= CommonUtil.parsePackage(req);
		
		String pileNo=request.getString(41);
		String status="00";//00 成功 03无效终端序列号 04无资费策略
		String serviceType="03";//01按次收费02按时间收费（单位 每分/10分钟）03按电量
		String serviceFee="50";//服务费 单位分
		String commonEleFee="160";//非分时电价
		String divisionEleFee="016001600160";//分时电价
		String divisionCount="1";//时段数量
		String divisionFeeInfo= CommonUtil.buildStringRight("00001",60);//默认只有一个时段
		
		//1.查找充电桩信息
		Map pileInfo=chargePileServiceImpl.findByPileNo(pileNo);
		
		if(pileInfo==null){
			status="03";
		}else{
			String policyId=pileInfo.get("TARIFF_POLICY_ID").toString();
			if("".equals(policyId)){
				status="04";
			}else{
				Map policyInfo=tariffPolicyServiceImpl.findTariffPolicy(policyId);
				if(policyInfo==null){
					status="04";
				}else{
					List priceInfoList=tariffPolicyServiceImpl.findDivisionPrice(policyId);
					serviceFee=policyInfo.get("CHARGE_SERVICE_FEE").toString();
					serviceType=(String)policyInfo.get("SERVICE_FEE_METHOD");
					 commonEleFee=policyInfo.get("COMMON_ELEC_PRICE").toString();
					if(!priceInfoList.isEmpty()){
						divisionCount=String.valueOf(priceInfoList.size());
						
						//1.创建电价数组
						Map<String,String> eleFeeArray=this.elePriceArray();//只支持4中电价
						
						//2.组成电价参数字符串
						
						String[] p=new String[4];
						p[0]=policyInfo.get("MAX_PRICE").toString();
						p[1]=policyInfo.get("HIGH_PRICE").toString();
						p[2]=policyInfo.get("AVG_PRICE").toString();
						p[3]=policyInfo.get("LOW_PRICE").toString();
						
						divisionEleFee="";
						for(int i=0;i<4;i++){
							divisionEleFee+= CommonUtil.buildString(p[i],4);
						}	
						
						//3.构造时段电价对应参数字符串
							divisionFeeInfo="";
							for(int i=0;i<priceInfoList.size();i++){
								Map priceInfo=(Map)priceInfoList.get(i);

								String startTime=priceInfo.get("DIVISION_START_TIME").toString();
								
								String index=eleFeeArray.get(priceInfo.get("DIVISION_TYPE"));
								divisionFeeInfo+=(CommonUtil.buildString(startTime,4)+index);
								
							}
						
							
					}
				}
			}
		}
		
		
		ISOMsg sendMsg=new ISOMsg();
		//String header="0039303030303030";//57
		String header="303030303030";
		sendMsg.setHeader(ISOUtil.hex2byte(header));
		
		sendMsg.set("39",status);
		sendMsg.set("4",serviceType);
		sendMsg.set("5",serviceFee);
		sendMsg.set("6",commonEleFee);
		sendMsg.set("7",divisionEleFee);
		sendMsg.set("8",divisionCount);
		
		sendMsg.set("9", CommonUtil.buildStringRight(divisionFeeInfo,60));
		sendMsg.set("63",request.getString("63"));
		sendMsg.set("0","1041");
		 ISOPackager packager = new GenericPackager(CommonUtil.getConfigPath(request.getString(0),"OUT"));
		 sendMsg.setPackager(packager);
		
		
		
		byte[] res=sendMsg.pack();
		byte[] both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		byte[] mb= new byte[both.length-16];
				System.arraycopy(both,0,mb,0, both.length-16);
		String mac= new MAC().encrypt(mb);
		sendMsg.set("63",mac);
		 res=sendMsg.pack();
		 both = (byte[]) ArrayUtils.addAll(ISOUtil.hex2byte(header),res);
		 both= CommonUtil.addLength(both);
		 
		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(TX1040:电价数据 )响应的报文--------------------------");
		LogUtils.info(ISOUtil.hexString(both));
		
		ByteBuf resp= Unpooled.copiedBuffer(both);
		ctx.writeAndFlush(resp);
		
		CommonUtil.dumpInfo(sendMsg);
		
		return both;
		
	}
    
	private  Map<String,String> elePriceArray(){
	
	  Map<String,String> priceArray=new HashMap<String,String>();
	  priceArray.put("max_price","1");
	  priceArray.put("high_price","2");
	  priceArray.put("avg_price","3");
	  priceArray.put("low_price","4");
	  
	  return priceArray;
	}
	
	private String indexArray(String s,String[] array){
		String index="0";
		for(int i=0;i<array.length;i++){
			if(s.equals(array[i])){
				index=String.valueOf(i);
				break;
			}
		}
		
		return index;
	}
}
