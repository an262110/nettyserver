package com.hc.common.utils.hk;

import com.hc.app.action.hk.RequestObject;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jpos.iso.ISOUtil;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/***
 * XML���ý���������
 * @author DELL
 *
 */
public class DomUtil{

	/**
	 * �����ֽ����
	 * 
	 * @param data
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public  static RequestObject parseByteToObject(byte[] req) throws UnsupportedEncodingException {
		
		byte[] data;
		String LRC="";
		RequestObject request=new RequestObject();
		Map dataMap=new LinkedHashMap();
		Map controlHeader=new LinkedHashMap();
		Map header=new LinkedHashMap();
		
		String txcode="";//������         
        String headHex="";//��ʼ�ֽ� 0x7E68����׼��Ϣͷ 0x7F79:�����ͷ        
        //��ȡ��ʼ�ֽ�
        byte[] headArray=new byte[2];
        System.arraycopy(req,0,headArray,0,2);
        headHex= ISOUtil.hexString(headArray);
        
       //1.��ȡ������
        byte[] txcodeArray=new byte[2];
        
        try{
        	if("7E68".equals(headHex)){
   	         System.arraycopy(req,8,txcodeArray,0,2);
   	         
   	         txcode=Integer.toString(ParseUtil.bytesToInt(txcodeArray));
   	         Map h= ParsePackage.parseHeader(req);
   	         header=(LinkedHashMap)h.get("head");
   	         LRC=(String)h.get("LRC");
   	         data=(byte[])h.get("data");
   	         
           }else if("7F79".equals(headHex)){
                  System.arraycopy(req,15,txcodeArray,0,2);
   	           txcode=Integer.toString(ParseUtil.bytesToInt(txcodeArray));
   	           Map c= ParsePackage.parseControlHeader(req);
   	           controlHeader=(LinkedHashMap)c.get("control_head");
   	           
   	           Map d= ParsePackage.parseHeader((byte[])c.get("ordi_data"));
   	           
   	           header=(LinkedHashMap)d.get("head");
   		       data=(byte[])d.get("data");
   		       LRC=(String)d.get("LRC");
   	           
           }else if("7E70".equals(headHex)){//�ͻ����·�������
          	       System.arraycopy(req,2,txcodeArray,0,2);
   	         
   	           txcode=Integer.toString(ParseUtil.bytesToInt(txcodeArray));
   	           
   	           byte[] codeArray=new byte[20];
   	           System.arraycopy(req,8,codeArray,0,20);
   	           String business_no=new String(codeArray,"ascii");
   	           
   	           request.setOrderId(business_no);
   	           request.setTxcode(txcode);
   	           return request;
   	           
           }else if("7E99".equals(headHex)){//�ͻ����·�������
       	       System.arraycopy(req,2,txcodeArray,0,2);
     	         
   	           txcode=Integer.toString(ParseUtil.bytesToInt(txcodeArray));
   	           
   	           byte[] codeArray=new byte[12];
   	           System.arraycopy(req,8,codeArray,0,12);
   	           String business_no=new String(codeArray,"ascii");
   	           
   	           request.setOrderId(business_no);
   	           request.setTxcode(txcode);
           	
   	           return request;
   	           
           }
           else{
          	 HKLogUtils.error("��ʼ�ֽڴ���:headbyte===="+headHex);
          	 return null;
           }
        }catch (Exception e) {
        	 HKLogUtils.error("������쳣����:headbyte===="+headHex);
           	 return null;
		}
        
        
        request.setControlHeader(controlHeader);
		request.setHeader(header);
		request.setData(dataMap);
        request.setTxcode(txcode);
        
        //���У��
        String myLRC= ISOUtil.hexString(new byte[]{ParsePackage.getEOR(data)});
        
        if(!LRC.equals(myLRC)){
       	  HKLogUtils.error("���У�����:RECIEVE LRC=="+LRC+";MY LRC===="+myLRC);
       	  return null;
        }
        
			// ��ݹ������ȡXML document������н���
			Document xmlDocument = ConfigLoader.getInstance()
					.getXMLDocumentsByCode("HK"+txcode);
			if (xmlDocument == null) {
				
				HKLogUtils.info("�޷������ñ���,�Ҳ��������ļ��������룺" +txcode+"; �����Ϣ:");
				HKLogUtils.info(request);
				return null;
			}
			
			Element root = xmlDocument.getRootElement();
			List<Element> attrs = root.element("body").elements();

			int count = 0;
			for (Element e : attrs) {

				String filedName = e.attribute("name").getValue();//������
				
				int subLen = Integer.parseInt(e.attribute("length").getValue());//�ֽڳ���
				String type = e.attribute("type").getValue();//��������
				
				
				// �����Ը�ֵ
				byte[] temp = Arrays.copyOfRange(data, count, count + subLen);
				if ("hex".equalsIgnoreCase(type)) {
											
						dataMap.put(filedName, ISOUtil.hexString(temp));
					
				} else if ("String".equalsIgnoreCase(type)) {
					String value=new String(temp,"ascii");
					dataMap.put(filedName,value);
				}else if("int".equalsIgnoreCase(type)){
					dataMap.put(filedName,Integer.valueOf(ISOUtil.hexString(temp),16));
				}
				
				//��������
				count = count + subLen;
					
		}
			
		
		return request;
	}

	public static void main(String[] args) {


	}
}
