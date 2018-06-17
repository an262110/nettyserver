package com.hc.app.action.hk;

import com.hc.app.action.BaseAction;
import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.hk.HKLogUtils;
import com.hc.common.utils.hk.ParsePackage;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.apache.log4j.Logger;
import org.jpos.iso.ISOUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component("HK5800")
public class HK5800 implements BaseAction {
	private  static Logger logger = Logger.getLogger("IDNHK_CONNECT");
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		
		RequestObject ob=(RequestObject)msg;
        Map data=ob.getData();
        String pileNo=(String)data.get("SN");
        
        HKLogUtils.info("info>>>>>>>>当前长连接"+ctx.channel().toString());
        
        
        
        /*************************保存长连接***************************/
        
        Channel oldSocket= NettyChannelMap.get(pileNo);
		if(oldSocket==null){//删除旧的连接
			NettyChannelMap.add(pileNo, (SocketChannel)ctx.channel());
			HKLogUtils.info("info>>>>>>>>保存了一个长连接"+ NettyChannelMap.get(pileNo).toString());
		}else{
			
			//if(!(oldSocket.isActive()||oldSocket.isOpen())){
				NettyChannelMap.remove(pileNo);
				//oldSocket.close();
				//HKLogUtils.info("info>>>>>>>>移除连接"+oldSocket.toString());
				NettyChannelMap.add(pileNo, (SocketChannel)ctx.channel());
				HKLogUtils.info("info>>>>>>>>保存了一个长连接"+ NettyChannelMap.get(pileNo).toString());
				logger.info("PILE_NO:"+pileNo+";CONNECT:"+ctx.channel());
			//}else{
				
			//	logger.info("PILE_NO:"+pileNo+";CONNECT:"+oldSocket);
			//}
		}
		data.put("LONG_LINK", NettyChannelMap.get(pileNo).toString());
		//添加心跳统计 成功率，粘包率，
		chargePileServiceImpl.addHeartBeat(data);
		
		
		
		/*****************************业务逻辑处理********************************/
		//逻辑处理
		String gunStatus=(String)data.get("GUN_STATUS");
		String chargeStatus= CommonUtil.toBinaryString((String)data.get("GUN_CHARGE"));
		String errCode=data.get("ERR_FLAG").toString();
		
		for(int i=1;i<=chargeStatus.length();i++){
			//更新每个枪的状态 0 未插枪 1 插枪
			//枪的状态 0待机 1充电 2充电完成 3故障 4 离线
			
			int gunNo=i;
			char c=chargeStatus.charAt(chargeStatus.length()-i);
			String gunCharge=(c=='1'?"1":"0");
		    
			String gunS=Integer.valueOf(gunStatus.substring(i*2-2,i*2),16).toString();
			
			//结合桩的状态的枪的最终显示状态
			String showStatus="03";
			if(!"0".equals(errCode)){
				showStatus="01";//故障
			}
			
			else if("0".equals(gunCharge)){
				showStatus="03";
			}else if("1".equals(gunCharge)&&"0".equals(gunS)){
				showStatus="07";
			}else if("1".equals(gunS)){
				showStatus="04";
			}else if("2".equals(gunS)){
				showStatus="05";
			}else if("3".equals(gunS)){
				showStatus="01";
			}else if("4".equals(gunS)){
				showStatus="00";
			}
			chargePileServiceImpl.updateGunStatus(pileNo,  gunCharge, gunS, showStatus,gunNo);
			
			if("2".equals(gunS)){//充电完成状态跟新订单
				Map gunInfo=chargePileServiceImpl.findByPileNoAndGunNo(pileNo, gunNo);
				String gunCode=(String)gunInfo.get("GUN_CODE");
				 Map rm =  chargeOrderServiceImpl.findLatestByGunCode(gunCode);
				 if(rm!=null){
					 String orderId=(String)rm.get("CHARGE_ORDER_ID");
					 Map<String,Object> params = new HashMap<String,Object>();
					 params.put("ORDER_STATE","03");
					 params.put("END_CHARGE_TIME", "1");
					 params.put("CHARGE_ORDER_ID",orderId);
					 
					 chargeOrderServiceImpl.updateInfo(params);
				 }
			}
		    
		}
		//跟新充电桩的状态 
		String pileStatus="03";
		int noUseGun=chargePileServiceImpl.countGun(pileNo,"0");
		if(!"0".equals(errCode)){
			pileStatus="01";//故障
		}else if(noUseGun==0){
			pileStatus="07";//等待，就是所有枪都被占用
		}
		
		chargePileServiceImpl.updateStatus(pileNo, pileStatus,errCode);
		
		
		
		
		
		/********************************响应数据*************************/
         byte[] sendData=buildData();
         byte[] ret= ParsePackage.buildHeader( 5801, sendData);
         
         HKLogUtils.info("=================================发送的数据=======================");
         HKLogUtils.info(ISOUtil.hexString(ret));
         
         ByteBuf resp= Unpooled.copiedBuffer(ret);
 		 ctx.writeAndFlush(resp);
		         
 		//处理离线桩，关闭channel
 /*		List list  = chargePileServiceImpl.searchPileList();
 		if(list!=null&&list.size()>0){
 			Iterator<Map> iter = list.iterator();
 			String pile_no=null;
 			 while(iter.hasNext()) {  
 	            Map map = iter.next();
 	            pile_no=map.get("CHARGE_PILE_SERI").toString();
 	            HKLogUtils.info("离线桩编号："+pile_no);
 	            if(NettyChannelMap.get(pile_no)!=null){
 	            	HKLogUtils.info("离线连接："+NettyChannelMap.get(pile_no).toString());
 	            	NettyChannelMap.get(pile_no).close();
 	            	NettyChannelMap.get(pile_no).disconnect();
 	            	HKLogUtils.info("重启离线桩："+pile_no);
 	            }
 		     }
 		}*/
				return null;
			}
			
			private byte[] buildData() throws UnsupportedEncodingException{
				
					
			SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
			String currentTime = formatter.format(new Date());
				
			byte[] val=currentTime.getBytes("ascii");
		    return val;
			
			}
  
}
