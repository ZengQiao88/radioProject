package com.hust.radiofeeler.mina2FPGA.Encode;

import com.hust.radiofeeler.Bean.FixSetting;
import com.hust.radiofeeler.GlobalConstants.Constants;
import com.hust.radiofeeler.compute.ComputePara;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.apache.mina.filter.codec.demux.MessageEncoder;

/**
 * Created by jinaghao on 15/11/24.
 */
public class FixSettingEncoder implements MessageEncoder<FixSetting> {
    private ComputePara computePara=new ComputePara();
    @Override
    public void encode(IoSession ioSession, FixSetting fixsetting, ProtocolEncoderOutput out) throws Exception {

        if(fixsetting!=null) {
            IoBuffer buffer = IoBuffer.allocate(17);
            byte[] bytes=GetBytes(fixsetting);
            buffer.put(bytes);
            buffer.flip();
            out.write(buffer);
            Constants.sequenceID=0;
        }
    }
    private  byte[] GetBytes(FixSetting fixsetting){
        byte[] data=new byte[17];
        data[0]=0x55;
        data[1]=0x07;
        data[2]= (byte) (Constants.ID&0xff);//设备ID号
        data[3]= (byte) ((Constants.ID>>8)&0xff);
        data[4]=IQtoCode((int) (fixsetting.getIQwidth()*1000));//带宽和数据率
        data[5]= (byte) fixsetting.getBlockNum();//数据块
        byte[] data1=computePara.Time2BytesUTC(fixsetting.getTimeString());//时间
        System.arraycopy(data1, 0, data, 6, 5);
        data[16]= (byte) 0xAA;
        return data;

    }
    private byte IQtoCode(int index){
        byte code=0;
        switch(index){
            case 5000:
                code=0x11;
                break;
            case 2500:
                code=0x22;
                break;
            case 1250:
                code=0x33;
                break;
            case 625:
                code=0x44;
                break;
            case 125:
                code=0x55;
                break;
            default:
                break;
        }
        return code;

    }
}
