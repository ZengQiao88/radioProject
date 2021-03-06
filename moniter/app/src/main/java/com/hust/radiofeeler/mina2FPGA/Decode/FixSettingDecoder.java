package com.hust.radiofeeler.mina2FPGA.Decode;

import com.hust.radiofeeler.Bean.FixSetting;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

/**
 * Created by jinaghao on 15/11/24.
 */
public class FixSettingDecoder implements MessageDecoder {
    @Override
    public MessageDecoderResult decodable(IoSession ioSession, IoBuffer in) {
        if (in.remaining() < 2) {
            return MessageDecoderResult.NEED_DATA;
        } else {
            byte frameHead = in.get();
            if ((frameHead==(byte)0x55)||(frameHead==(byte)0x66)) {
                byte functionCode = in.get();
                if (functionCode == 0x27) {
                    return MessageDecoderResult.OK;

                } else
                    return MessageDecoderResult.NOT_OK;

            } else
                return MessageDecoderResult.NOT_OK;
        }
    }

    @Override
    public MessageDecoderResult decode(IoSession ioSession, IoBuffer in, ProtocolDecoderOutput out) throws Exception {
        IoBuffer buffer = IoBuffer.allocate(17);
        FixSetting fixSetting = new FixSetting();
        int receiveCount = 0;

        while (in.hasRemaining()) {
            byte b = in.get();
            buffer.put(b);
            receiveCount++;
            if (receiveCount == 17) {
                buffer.flip();
                byte[] accept = new byte[17];
                buffer.get(accept);
                fixSetting.setPacketHead(accept[0]);
                fixSetting.setIQwidth(getIQwidth(accept));//带宽和数据率
                fixSetting.setBlockNum(accept[5]);
                fixSetting.setYear(((accept[6]&0xff) << 4) + ((accept[7] >> 4)&0x0f));
                fixSetting.setMonth(accept[7]&0x0f);
                fixSetting.setDay((accept[8]>>3)&0x1f);
                fixSetting.setHour(((accept[8] &0x07)<<2) + (accept[9]&0x03)+8);//UTC转北京时间
                fixSetting.setMinute((accept[9]>>2)&0x3f);
                fixSetting.setSecond(accept[10]&0xff);
                fixSetting.setContent(accept);

                out.write(fixSetting);
                return MessageDecoderResult.OK;
            }
        }
        return MessageDecoderResult.NOT_OK;
    }

    @Override
    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

    }


    /**
     * IQ复信号带宽和复信号数据率转换编码
     *
     * @param bytes
     * @return
     */

    private double getIQwidth(byte[] bytes) {
        double IQwidth = 0;
        switch (bytes[4]) {
            case 0x11:
                IQwidth = 5;
                break;
            case 0x22:
                IQwidth = 2.5;
                break;
            case 0x33:
                IQwidth = 1;
                break;
            case 0x44:
                IQwidth = 0.5;
                break;
            case 0x55:
                IQwidth = 0.1;
                break;
        }
        return IQwidth;

    }

}
