package com.hc.netty.handler;

import com.hc.common.utils.hk.HKLogUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import org.jpos.iso.ISOUtil;

import java.nio.ByteOrder;
import java.util.List;

public class HKDecoder extends ByteToMessageDecoder {
	 private final ByteOrder byteOrder=ByteOrder.BIG_ENDIAN;
	 private final int lengthFieldLength=4;
	 private int lengthAdjustment=6;
	 private int lengthFieldOffset=0;
	 private int lengthFieldEndOffset=0;
	 public HKDecoder(){
		 
	 }
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// TODO Auto-generated method stub
		Object decoded = decode(ctx, in);
        if (decoded != null) {
            out.add(decoded);
        }
	}
	
	 protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
	     if(in.readableBytes()==0){
	    	 return null;
	     }
		 ByteBuf buf =extractFrame(in, in.readerIndex(),in.readableBytes());
         byte[] req=new byte[buf.readableBytes()];
         buf.readBytes(req);
         HKLogUtils.info("HKDECODER>>>>>>"+ ISOUtil.hexString(req));
         
		 //1.根据控制头计算长度属性的位置
		 ByteBuf head=extractFrame(in, in.readerIndex(),2);
		     
         byte[] h=new byte[head.readableBytes()];
         head.readBytes(h);
         String hexString= ISOUtil.hexString(h);
         if("7E68".equals(hexString)){
        	 this.lengthFieldOffset=4;
        	 this.lengthFieldEndOffset=4+4;
         }else if("7F79".equals(hexString)){
        	 this.lengthFieldOffset=11;
        	 this.lengthFieldEndOffset=11+4;
         }else if("7E70".equals(hexString)){
        	 this.lengthFieldOffset=4;
        	 this.lengthFieldEndOffset=4+4;
        	 this.lengthAdjustment=0;
         }else if("7E99".equals(hexString)){
        	 this.lengthFieldOffset=4;
        	 this.lengthFieldEndOffset=4+4;
        	 this.lengthAdjustment=0;
         }else{
        	 
        	 HKLogUtils.error("错误的报文头>>>>>"+hexString);
        	 
        	 //in.skipBytes(in.readableBytes());
        	 int index=in.readerIndex();
        	 int act=in.readableBytes();
        	 ByteBuf bf= extractFrame(in,index ,act);
        	 in.readerIndex(index +act);
        	 return bf; 
        	 //throw new CorruptedFrameException(
	                    //"wrong header: " + hexString);
         }
         
          //in.readerIndex(in.readerIndex()-2);//恢复读起始位置
          
          
	        if (in.readableBytes() < lengthFieldEndOffset) {
	            return null;
	        }

	        int actualLengthFieldOffset = in.readerIndex() + lengthFieldOffset;
	        long frameLength = getFrameLength(in, actualLengthFieldOffset);

	        if (frameLength < 0) {
	            in.skipBytes(lengthFieldEndOffset);
	            throw new CorruptedFrameException(
	                    "negative pre-adjustment length field: " + frameLength);
	        }

	        frameLength += lengthAdjustment + lengthFieldEndOffset;

	        if (frameLength < lengthFieldEndOffset) {
	            in.skipBytes(lengthFieldEndOffset);
	            throw new CorruptedFrameException(
	                    "Adjusted frame length (" + frameLength + ") is less " +
	                    "than lengthFieldEndOffset: " + lengthFieldEndOffset);
	        }

	      

	        // never overflows because it's less than maxFrameLength
	        int frameLengthInt = (int) frameLength;
	        if (in.readableBytes() < frameLengthInt) {
	            return null;
	        }

	       
	        // extract frame
	        int readerIndex = in.readerIndex();
	        int actualFrameLength = frameLengthInt;
	        ByteBuf frame = extractFrame(in, readerIndex, actualFrameLength);
	        in.readerIndex(readerIndex + actualFrameLength);
	        return frame;
	    }
	 protected ByteBuf extractFrame(ByteBuf buffer, int index, int length) {
	        ByteBuf frame = Unpooled.buffer(length);
	        frame.writeBytes(buffer, index, length);
	        return frame;
	    }
	private long getFrameLength(ByteBuf in, int actualLengthFieldOffset) {
        in = in.order(byteOrder);
        long frameLength;
        switch (lengthFieldLength) {
        case 1:
            frameLength = in.getUnsignedByte(actualLengthFieldOffset);
            break;
        case 2:
            frameLength = in.getUnsignedShort(actualLengthFieldOffset);
            break;
        case 3:
            frameLength = in.getUnsignedMedium(actualLengthFieldOffset);
            break;
        case 4:
            frameLength = in.getUnsignedInt(actualLengthFieldOffset);
            break;
        case 8:
            frameLength = in.getLong(actualLengthFieldOffset);
            break;
        default:
            throw new Error("should not reach here");
        }
        return frameLength;
    }

}
