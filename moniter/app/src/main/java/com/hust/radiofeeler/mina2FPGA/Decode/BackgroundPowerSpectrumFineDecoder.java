package com.hust.radiofeeler.mina2FPGA.Decode;

import android.util.Log;

import com.hust.radiofeeler.Bean.BackgroundPowerSpectrum;
import com.hust.radiofeeler.Bean.ReceiveRight;
import com.hust.radiofeeler.Bean.ReceiveWrong;
import com.hust.radiofeeler.GlobalConstants.Constants;
import com.hust.radiofeeler.GlobalConstants.Context;
import com.hust.radiofeeler.compute.ComputePara;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.filter.codec.demux.MessageDecoder;
import org.apache.mina.filter.codec.demux.MessageDecoderResult;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by jinaghao on 15/12/24.
 */
public class BackgroundPowerSpectrumFineDecoder implements MessageDecoder {

    private final AttributeKey CONTEXT = new AttributeKey(getClass(),
            "context");
    private ComputePara computePara=new ComputePara();
    private ReceiveRight mReceiveRight = new ReceiveRight();
    private ReceiveWrong mReceiveWrong = new ReceiveWrong();


    @Override
    public MessageDecoderResult decodable(IoSession session, IoBuffer in) {
        Log.d("abcd", "尝试BackgroundPowerSpectrumFineDecoder谱解码器");
        if(Constants.flag ){
            Constants.flag=false;
            Constants.buffer.limit(Constants.positionValue);
            Constants.buffer.flip();
            byte headtail=Constants.buffer.get();
//            while (headtail != (byte) 0x55) {
//                i++;
//                headtail=Constants.buffer.get();
//                Log.d("abcd", "背景功率谱解码器找帧头Constants.buffer"+headtail);
//                if (i >= in.remaining()) {
//                    break;
//                }
//            }
            byte functionCode = Constants.buffer.get();
          //  Log.d("abcd", "Constants.buffer背景功率谱解码器functionCode"+functionCode);
            if (functionCode == 0x52) {
                Constants.Isstop=false;
                Constants.flag=false;
                return MessageDecoderResult.OK;
            } else {
                Constants.Isstop=true;
                return MessageDecoderResult.NOT_OK;
            }
        }else {
            if (in.remaining() < 2) {
                return MessageDecoderResult.NEED_DATA;
            } else {
                byte head = in.get();
               // Log.d("abcd", "背景功率谱解码器找帧头" + head);
//                while (head != (byte) 0x55) {
//                    i++;
//                    head = in.get();
//                    Log.d("abcd", "背景功率谱解码器找帧头" + head);
//                    if (i >= in.remaining()) {
//                        break;
//                    }
//                }
                byte functionCode = in.get();
               // Log.d("abcd", "背景功率谱解码器functionCode" + functionCode);

                if (functionCode == 0x52) {
                    Constants.Isstop=false;
                    return MessageDecoderResult.OK;

                } else {
                    Constants.Isstop=true;
                    return MessageDecoderResult.NOT_OK;
                }
            }
        }
    }
    @Override
    public MessageDecoderResult decode(IoSession session, IoBuffer in,
                                       final ProtocolDecoderOutput out) throws Exception {

        if(Constants.IsJump&&(!Constants.Backfail)){
            Constants.IsJump=false;
            Constants.flag = true;
            Constants.positionValue=in.limit();
            Constants.buffer.sweep();
            Constants.buffer.put(in);
            Log.d("back", "jump");
            return MessageDecoderResult.OK;
        }

        Constants.ctx = getContext(session);//获取session  的context
        long matchCount = Constants.ctx.getMatchLength();//目前已获取的数据
        long length = Constants.ctx.getLength();//数据总长度
        IoBuffer buffer = Constants.ctx.getBuffer();//数据存入buffer

///////////////////////////////////////////////////
        if(length==0){
            length=1561;
            Constants.ctx.setLength(length);
            matchCount= in.remaining();
        }else {
            matchCount += in.remaining();
        }
        Log.d("back", "共收到字节：" + String.valueOf(matchCount));
        Constants.ctx .setMatchLength(matchCount);
        if (in.hasRemaining()) {// 如果in中还有数据
            if(matchCount< length) {
                buffer.put(in);// 添加到保存数据的buffer中
            }
            if (matchCount >= length) {// 如果已经发送的数据的长度>=目标数据的长度,则进行解码
                final byte[] b = new byte[1561];
                byte[] temp = new byte[1561];
                in.get(temp,0, (int) (length-buffer.position()));//最后一次in的数据可能有多的
                buffer.put(temp);
                // 一定要添加以下这一段，否则不会有任何数据,因为，在执行in.put(buffer)时buffer的起始位置已经移动到最后，所有需要将buffer的起始位置移动到最开始
                buffer.flip();
                buffer.get(b);
                if (b[1] == (byte) 0x52 && b[1560] == (byte) 0xaa) {
                    BackgroundPowerSpectrum back = byte2Object(b);

                    if (back != null) {

                        TimerTask task = new TimerTask() {
                            public void run() {
                                Constants.FPGAsession.write(mReceiveRight);
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(task, 20);
                        out.write(back);
                        Log.d("back", "当前帧总共段数：" + back.getTotalBand());
                        Log.d("back", "当前帧所在序号：" + back.getNumN());
                        Constants.Backfail=false;
                    }
                }else {
                    Constants.FPGAsession.write(mReceiveWrong);
                }
                Constants.ctx.reset();
                return MessageDecoderResult.OK;
            } else {
                Constants.ctx.setBuffer(buffer);
                Constants.Backfail=true;
                return MessageDecoderResult.NEED_DATA;
            }
        }
        return MessageDecoderResult.OK;
    }

    @Override
    public void finishDecode(IoSession ioSession, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {

    }

    private BackgroundPowerSpectrum byte2Object(byte[] bytes) {
        BackgroundPowerSpectrum back = new BackgroundPowerSpectrum();
        back.setTotalBand((bytes[19]  & 0xff));
        back.setNumN((bytes[20] & 0xff));//扫频总段数的序号
        back.setPSbandNum((bytes[21] & 0xff));
        byte[] b2 = new byte[1536];
        System.arraycopy(bytes, 22, b2, 0, 1536);
         float[] f1=computePara.Bytes2Power(b2);
        back.setPSpower(f1);
        return back;
    }


    //获取session的context
    private Context getContext(IoSession session) {
        Context ctx = (Context) session.getAttribute(CONTEXT);
        if (ctx == null) {
            ctx = new Context();
            session.setAttribute(CONTEXT, ctx);
        }
        return ctx;
    }

}
