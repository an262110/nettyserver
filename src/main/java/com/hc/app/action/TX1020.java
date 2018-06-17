package com.hc.app.action;

import com.hc.app.service.ChargeOrderService;
import com.hc.app.service.ChargePileService;
import com.hc.app.service.HeartbeatService;
import com.hc.app.utils.MAC;
import com.hc.app.utils.ParsePileData;
import com.hc.app.utils.TimeUtils;
import com.hc.common.utils.CommonUtil;
import com.hc.common.utils.LogUtils;
import com.yao.NettyChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import org.apache.commons.lang.ArrayUtils;
import org.jpos.iso.ISOMsg;
import org.jpos.iso.ISOPackager;
import org.jpos.iso.ISOUtil;
import org.jpos.iso.packager.GenericPackager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 心跳数据
 * @Title:TX1020.java
 * @Package:com.hc.app.action
 * @Description:TODO
 * @author zhifanglong
 * @Date 2016年6月20日 下午7:53:21
 * @Version V1.0
 */
@Component("TX1020")
public class TX1020 implements BaseAction{
	@Autowired
	private ChargePileService chargePileServiceImpl;
	@Autowired
	private HeartbeatService heartbeatServiceImpl;
	@Autowired
	private ChargeOrderService chargeOrderServiceImpl;
	@Override
	public byte[] createSendInfo(ChannelHandlerContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		byte[] req=(byte[])msg;
		ISOMsg request=CommonUtil.parsePackage(req);
		String pileNo=request.getString(41);

		Channel oldSocket=NettyChannelMap.get(pileNo);
		if(oldSocket==null){//删除旧的连接
			NettyChannelMap.add(pileNo, (SocketChannel)ctx.channel());
			LogUtils.info("info>>>>>>>>保存了一个长连接"+NettyChannelMap.get(pileNo).toString());
		}else{
			if(!(oldSocket.isActive()||oldSocket.isOpen())){
				NettyChannelMap.remove(pileNo);
				oldSocket.close();
				LogUtils.info("info>>>>>>>>关闭了不活动的连接"+oldSocket.toString());
				NettyChannelMap.add(pileNo, (SocketChannel)ctx.channel());
				LogUtils.info("info>>>>>>>>保存了一个长连接"+NettyChannelMap.get(pileNo).toString());
			}
		}




		ISOMsg sendMsg=new ISOMsg();
		//String header="0050303030303030";
		String header="303030303030";

		sendMsg.setHeader(ISOUtil.hex2byte(header));
		String status="";
		String OUTPOWER="00070007";//最大和平均功率 4位平均+4位最大
		String feeVersion="00007";//资费版本号
		String softVersion="00000";//软件版本号
		String blackVersion="00007";//黑名单版本号
		String downloadVersion="00007";//下载地址版本号

		Map pileInfo=chargePileServiceImpl.findByPileNo(pileNo);
		if(pileInfo!=null){
			status="00";
			String maxPower=(String)pileInfo.get("CHARGE_PILE_POWER_MAX");
			String avgPower=(String)pileInfo.get("CHARGE_PILE_POWER_AVG");
			if(maxPower!=null&&avgPower!=null)
				if(!"".equals(maxPower)&&!"".equals(avgPower)){
					OUTPOWER=CommonUtil.buildString(avgPower,4)+CommonUtil.buildString(maxPower,4);

				}
			String fv=(String)pileInfo.get("FEE_VERSION");
			String sv=(String)pileInfo.get("CHARGE_PILE_SOFTW_VERSION");
			feeVersion=("".equals(fv)||fv==null)?feeVersion:CommonUtil.buildString(fv,5);
			softVersion=("".equals(sv)||sv==null)?feeVersion:CommonUtil.buildString(sv,5);

		}else{
			status="03";
		}
		sendMsg.set("39",status);//响应状态
		sendMsg.set("44",OUTPOWER);//平均与最大功率
		/*
		 * 充电桩收到服务器的心跳响应，会根据52域中的APP下载连接版本号进行判断
		 * 如果与保存的版本号不一致，就会重新发送签到
		 * 充电桩保存APP版本号的逻辑：先判断签到中的下载地址是否正确，正确的话再
		 * 判断心跳送的APP版本号与保存的是否一样，不一样则保存
		 * 默认版本号是99999，签到中未送下载连接，导致版本号不会保存，这样我们又
		 * 送了一个版本号，和默认版本号不一致，就会重新发签到请求下载地址
		 *
		 *
		 * 发送签到，签到上传下载版本地址--》交换密钥--》心跳上传下载地址版本号--》首次判断下载地址版本号与默认版本号不同
		 * --》保存下载地址版本号--》重现签到获取下载地址；
		 * 首次联网签到到心跳因此会重复执行一下
		 */
		sendMsg.set("52",softVersion+downloadVersion+feeVersion+blackVersion);//黑名单、资费、app下载版本、充电桩软件版本号
		sendMsg.set("61",TimeUtils.getCurrentTime());

		sendMsg.set("63",request.getString("63"));
		sendMsg.set("0","1021");
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
		both=CommonUtil.addLength(both);

		sendMsg.dump(System.out, "");
		LogUtils.info("--------------------------(TX1020:心跳 )响应的报文--------------------------");

		LogUtils.info(ISOUtil.hexString(both));

		ByteBuf resp= Unpooled.copiedBuffer(both);
		ctx.writeAndFlush(resp);

		CommonUtil.dumpInfo(sendMsg);

		//保存心跳数据
		Map<String,String> values=new HashMap<String,String>();
		values.put("MSG_TYPE","1020");
		values.put("PILE_TYPE33",request.getString(33));
		values.put("PILE_HEARTBEAT_DATA35",request.getString(35));
		values.put("ELEC_QUANTITY36",request.getString(36));
		values.put("SERVICE_FEE37",request.getString(37));
		values.put("PILE_MOUTH38",request.getString(38));
		values.put("PILE_SEQ41",request.getString(41));
		values.put("PILE_HEART_TIME62",request.getString(62));
		values.put("PILE_MAC63",request.getString(63));
		values.put("MSG_RET39",status);
		values.put("MSG_OUTPOWER44", OUTPOWER);

		/*
		 * 33域 充电桩类型：01 单枪直流充电桩 02 单相交流充电桩 03 三相交流充电桩
		 * 04 双枪轮流充直流充电桩 05 双枪同时充直流充电桩
		 */
		Map pileData=ParsePileData.parsePileData(request.getString(35), request.getString(33));

		if(pileData!=null){
			values.putAll(pileData);
			String workStatus=(String)pileData.get("PILE_DATA_2");
			chargePileServiceImpl.updateStatus(pileNo, workStatus);

			String gunChargeStatus="0";//充电枪的插枪状态 0 未插枪 1 插枪
			String gunStatus="0";//充电枪工作状态0待机 4离线 3 故障 1 充电 2充电完成
			int gunNo=1;
			if("01".equals(workStatus)){
				gunStatus="3";
			}
			else if("07".equals(workStatus)){
				gunChargeStatus="1";
			}else if("04".equals(workStatus)){
				gunChargeStatus="1";
				gunStatus="1";
			}else if("05".equals(workStatus)){
				gunChargeStatus="1";
				gunStatus="2";
			}
			chargePileServiceImpl.updateGunStatus(pileNo, gunChargeStatus, gunStatus, workStatus,gunNo);

			/*
			 * 如果心跳数据中的状态是充电中或充电完成，实时更新充电订单数据
			 */
			if("04".equals(workStatus)||"05".equals(workStatus)){
				Map rm =  chargeOrderServiceImpl.findLatest(pileNo);

				String businessNo = "";
				if(rm!=null){
					businessNo=(String)rm.get("CHARGE_ORDER_ID");
					Map<String,Object> params = new HashMap<String,Object>();
					params.put("CURRENT_V",pileData.get("PILE_DATA_10"));
					params.put("CURRENT_A",pileData.get("PILE_DATA_11"));
					params.put("TOTAL_CHARGE_TIMES",pileData.get("PILE_DATA_18"));
					params.put("TOTAL_CHARGE_QUANTITY",pileData.get("PILE_DATA_17"));
					params.put("TOTAL_CHARGE_MONEY",request.getString(36));
					params.put("TOTAL_SERVICE_MONEY",request.getString(37));
					params.put("CHARGE_GUN",request.getString(38));
					if("04".equals(workStatus))
						params.put("ORDER_STATE","02");
					else
						params.put("ORDER_STATE","03");
					params.put("CHARGE_ORDER_ID",businessNo);

					chargeOrderServiceImpl.updateInfo(params);
				}
			}
		}else{
			LogUtils.error("---心跳充电桩数据解析失败；pileData="+request.getString(35));
		}

		heartbeatServiceImpl.save(values);


		return both;
	}

}
