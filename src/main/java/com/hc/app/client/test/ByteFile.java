package com.hc.app.client.test;

import com.hc.app.model.Head;

import java.io.FileOutputStream;
import java.io.IOException;

public class ByteFile {

	public static void main(String[] args) {
		try {
			System.out.print("输入要保存文件的内容：");
			//int count, n = 512;
			//byte buffer[] = new byte[n];
			// 读取标准输入流
			// count = System.in.read(buffer);
			//byte[] data = new byte[] { 0x01, 0x02, 0x03, 0x04, 0x06, 0x02, 0x03, 0x04, 0x06, 0x02, 0x03, 0x04, 0x06, 0x02, 0x03, 0x04, 0x06, 0x02, 0x03, 0x04, 0x06 };
			Head head = new Head();
			
			//启动字符
			byte[] head1 = new byte[1];
			head1[0] = (byte) 0x68;			
			head.setHead1_1(head1);
			
			//报文长度
//			int bodyLen = 16;
//			byte[] head2 = new byte[2];
//			head2[0] = (byte) ((bodyLen >>> 0) & 0xff);
//			head2[1] = (byte) ((bodyLen >>> 8) & 0xff);
			byte[] head2 = head.intToBytes(1,2,1);
			head.setHead2_2(head2);
			
			//协议版本号
//			byte[] head3 = new byte[2];
//			head3[0] = (byte) 0x01;	
//			head3[1] = (byte) 0x02;	
			byte[] head3 = head.intStrToBCD("23");
			head.setHead3_2(head3);
			
			//安全控制码 无需验证，固定
			byte[] head4 = new byte[1];
			head4[0] = (byte) 0x00;			
			head.setHead4_1(head4);
			
			//head5 默认自动填充 3个 0x00
			//byte[] head5 = new byte[3];
						
			byte[] data =head.getByte();
			System.out.println(new String(data));
			// 创建文件输出流对象
			FileOutputStream os = new FileOutputStream("pkg/WriteFile.txt");
			// 写入输出流
			os.write( data,0,data.length);
			// 关闭输出流
			os.close();
			System.out.println("已保存到WriteFile.txt!");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
