package com.hust.radiofeeler.bean2Transmit.FPGA2server;

/**
 * Created by Administrator on 2015/12/1.
 */
public class Reply_IsTerminalOnline {
    private byte[] content;

    public void setContent(byte[] content) {
        this.content = content;
    }

    public byte[] getContent() {

        return content;
    }
}
