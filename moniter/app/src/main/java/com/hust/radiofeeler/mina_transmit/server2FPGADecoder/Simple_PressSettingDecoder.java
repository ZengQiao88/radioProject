package com.hust.radiofeeler.mina_transmit.server2FPGADecoder;

import android.util.Log;

import com.hust.radiofeeler.bean2Transmit.server2FPGASetting.Simple_PressSetting;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import java.util.Arrays;

/**
 * Created by Administrator on 2015/12/1.
 */
public class Simple_PressSettingDecoder implements MessageDecoder {
    @Override
    public MessageDecoderResult decodable(IoSession ioSession, IoBuffer in) {
        if(in.remaining()<2){
            return MessageDecoderResult.NEED_DATA;
        }else {
            byte b1=in.get();
            byte b2=in.get();
            if((b1==0x66)&&(b2==0x08)){
                return MessageDecoderResult.OK;
            }else
                return MessageDecoderResult.NOT_OK;
        }

    }

    @Override
    public MessageDecoderResult decode(IoSession ioSession, IoBuffer in,
                                       ProtocolDecoderOutput out) throws Exception {

        int hasCount=0;
        while (in.hasRemaining()){
            hasCount++;
           if(hasCount==17){
               byte[] bytes=new byte[17];
               in.get(bytes);
               Simple_PressSetting pressSetting=new Simple_PressSetting();
               pressSetting.setContent(bytes);
               out.write(pressSetting);
               Log.d("xyz", Arrays.toString(bytes));
               return MessageDecoderResult.OK;
           }
        }
        return MessageDecoderResult.NEED_DATA;
    }

    @Override
    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

    }
}
