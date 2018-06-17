package com.hc.app.model;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;
import java.util.Map;

/**
 * 报文头
 * 
 * @author liuh
 *
 */
public final class Head extends MegUtil implements BodyI {
	private byte[] head1_1;
	private byte[] head2_2;
	private byte[] head3_2;
	private byte[] head4_1;
	private byte[] head5_3;
	private byte[] head6_2;
	private byte[] head7_8;
	private byte[] head8_1;
	private byte[] head9_7;
	
	private String head_hexstr;
	private String head8_1_hexstr;
	private String head5_3_hexstr;

	// 报文头字节长度
	public final static int len = 27;

	public Head() {
		head1_1 = new byte[] { 0x68 };
		// head2_2 合并在meg的getbyte
		// head2_2 = intToBytes(bodylen, 2, 1);
		head3_2 = new byte[] { 0x01, 0x02 };
		head4_1 = new byte[] { 0x00 };
		head5_3 = new byte[] { 0x00, 0x00, 0x00 };
		head6_2 = new byte[] { 0x00, 0x00 };
		// 充电设备号
		head7_8 = hexStringToBytes("2017012914295801");
		// 上线用B0 测试用B1
		head8_1 = new byte[] { (byte) 0xB0 };
		// head.setHead9_7(head.hexStringToBytes("01291429580102"));
		// 流水号跟智网商量下采用yyyyMMddHHmmss
		head9_7 = hexStringToBytes(getSerialNum());

	}

	// 充电桩号 + 帧代码
	public Head(String head7_8, byte[] head8_1) {
		head1_1 = new byte[] { 0x68 };
		// head2_2 合并在meg的getbyte
		// head2_2 = intToBytes(bodylen, 2, 1);
		head3_2 = new byte[] { 0x01, 0x02 };
		head4_1 = new byte[] { 0x00 };
		head5_3 = new byte[] { 0x00, 0x00, 0x00 };
		head6_2 = new byte[] { 0x00, 0x00 };
		// 充电设备号
		this.head7_8 = hexStringToBytes(head7_8);
		// 上线用B0 测试用B1
		this.head8_1 = head8_1;// new byte[]{(byte) 0xB1};
		// head.setHead9_7(head.hexStringToBytes("01291429580102"));
		// 流水号跟智网商量下采用yyyyMMddHHmmss
		head9_7 = hexStringToBytes(getSerialNum());

	}
		

	public Head(byte[] meg) {
		head1_1 = copyBytes(meg, 0, 1);
		head2_2 = copyBytes(meg, 1, 2);
		head3_2 = copyBytes(meg, 3, 2);
		head4_1 = copyBytes(meg, 5, 1);
		head5_3 = copyBytes(meg, 6, 3);
		head6_2 = copyBytes(meg, 9, 2);
		head7_8 = copyBytes(meg, 11, 8);
		head8_1 = copyBytes(meg, 19, 1);
		head9_7 = copyBytes(meg, 20, 7);
		
		head_hexstr = bytesToHexString(this.getByte());
		head8_1_hexstr = bytesToHexString(head8_1).toUpperCase();
		head5_3_hexstr = bytesToHexString(head5_3);
	}

	public Head(byte[] head1_1, byte[] head2_2, byte[] head3_2, byte[] head4_1, byte[] head5_3, byte[] head6_2,
			byte[] head7_8, byte[] head8_1, byte[] head9_7) {

		this.head1_1 = fill0x00(head1_1, 1);
		this.head2_2 = fill0x00(head2_2, 2);
		this.head3_2 = fill0x00(head3_2, 2);
		this.head4_1 = fill0x00(head4_1, 1);
		this.head5_3 = fill0x00(head5_3, 3);
		this.head6_2 = fill0x00(head6_2, 2);
		this.head7_8 = fill0x00(head7_8, 8);
		this.head8_1 = fill0x00(head8_1, 1);
		this.head9_7 = fill0x00(head9_7, 7);
	}

	/**
	 * @return byte[]
	 * @roseuid 587C9ED803B2
	 */
	public byte[] getByte() {
		head1_1 = fill0x00(head1_1, 1);
		head2_2 = fill0x00(head2_2, 2);
		head3_2 = fill0x00(head3_2, 2);
		head4_1 = fill0x00(head4_1, 1);
		head5_3 = fill0x00(head5_3, 3);
		head6_2 = fill0x00(head6_2, 2);
		head7_8 = fill0x00(head7_8, 8);
		head8_1 = fill0x00(head8_1, 1);
		head9_7 = fill0x00(head9_7, 7);

		byte[] head = new byte[27];
		head[0] = head1_1[0];
		System.arraycopy(head2_2, 0, head, 1, 2);
		System.arraycopy(head3_2, 0, head, 3, 2);
		head[5] = head4_1[0];

		System.arraycopy(head5_3, 0, head, 6, 3);
		System.arraycopy(head6_2, 0, head, 9, 2);
		System.arraycopy(head7_8, 0, head, 11, 8);

		head[19] = head8_1[0];
		System.arraycopy(head9_7, 0, head, 20, 7);

		return head;
	}
	
	   //发送前合并byte[] 封装
	   public ByteBuf getSendBuf(){
		   return obtainSendBuf(getByte());
	   }

	public byte[] getHead1_1() {
		return head1_1;
	}

	public void setHead1_1(byte[] head1_1) {
		this.head1_1 = head1_1;
	}

	public byte[] getHead2_2() {
		return head2_2;
	}

	public void setHead2_2(byte[] head2_2) {
		this.head2_2 = head2_2;
	}

	public byte[] getHead3_2() {
		return head3_2;
	}

	public void setHead3_2(byte[] head3_2) {
		this.head3_2 = head3_2;
	}

	public byte[] getHead4_1() {
		return head4_1;
	}

	public void setHead4_1(byte[] head4_1) {
		this.head4_1 = head4_1;
	}

	public byte[] getHead5_3() {
		return head5_3;
	}

	public void setHead5_3(byte[] head5_3) {
		this.head5_3 = head5_3;
	}

	public byte[] getHead6_2() {
		return head6_2;
	}

	public void setHead6_2(byte[] head6_2) {
		this.head6_2 = head6_2;
	}

	public byte[] getHead7_8() {
		return head7_8;
	}

	public void setHead7_8(String pileNum) {
		this.head7_8 = hexStringToBytes(pileNum);
	}

	public void setHead7_8(byte[] head7_8) {
		this.head7_8 = head7_8;
	}

	public byte[] getHead8_1() {
		return head8_1;
	}

	public void setHead8_1(byte[] head8_1) {
		this.head8_1 = head8_1;
	}

	public byte[] getHead9_7() {
		return head9_7;
	}

	public void setHead9_7(byte[] head9_7) {
		this.head9_7 = head9_7;
	}

	
	
	public String getHead_hexstr() {
		return head_hexstr;
	}

	public void setHead_hexstr(String head_hexstr) {
		this.head_hexstr = head_hexstr;
	}

	
	
	public String getHead8_1_hexstr() {
		return head8_1_hexstr;
	}

	public void setHead8_1_hexstr(String head8_1_hexstr) {
		this.head8_1_hexstr = head8_1_hexstr;
	}

	public String getHead5_3_hexstr() {
		return head5_3_hexstr;
	}

	public void setHead5_3_hexstr(String head5_3_hexstr) {
		this.head5_3_hexstr = head5_3_hexstr;
	}

	public static void main(String[] args) {
		//

		Head head = new Head();
		// byte[] b = head.intStrToBCD("12");
		// System.out.println(head.BCDtointStr(b));
		// System.out.println(bcd2Str(b));
		//
		// byte[] b = head.intToBytes(65532,4,0);
		// System.out.println(head.BytesToint(b));

//		byte[] b = new byte[2];
//		b[0] = 0x01;
//		b[1] = 0x09;
//		System.out.println(head.bytesToHexString(b));
//
//		System.out.println(head.bytesToHexString(head.hexStringToBytes("20170119")));
//
//		String ipStr = "211.149.228.101";
//		System.out.println(head.bytesToHexString(head.ipToBytes(ipStr)));
//
//		System.out.println(head.BytesToip((head.ipToBytes(ipStr))));
//
//		System.out.println(head.bytesToHexString(head.readFileTobytes("WriteFile")));
	
		String oper_name_50 = "广州爱电牛互联网科技有限公司\r\n";
		System.out.println(head.bytesToGBK(head.fill0x00(head.gbkToBytes(oper_name_50),50)));
		System.out.println(head.bytesToHexString(head.fill0x00(head.gbkToBytes(oper_name_50),50)));
	}

	@Override
	public int getBodyLen() {

		return len;
	}

	public Map<String, String> bytesToMap() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("head1_1", bytesToHexString(head1_1));
		map.put("head2_2", bytesToHexString(head2_2) + "");
		map.put("head3_2", bytesToHexString(head3_2));
		map.put("head4_1", bytesToHexString(head4_1));
		map.put("head5_3", bytesToHexString(head5_3));
		map.put("head6_2", bytesToHexString(head6_2));
		map.put("head7_8", bytesToHexString(head7_8));
		map.put("head8_1", bytesToHexString(head8_1));
		map.put("head9_7", bytesToHexString(head9_7));
		return map;
	}

}
