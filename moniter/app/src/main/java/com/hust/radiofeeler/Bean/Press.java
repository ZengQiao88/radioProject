package com.hust.radiofeeler.Bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinaghao on 15/11/25.
 */
public class Press implements Parcelable {
    private byte packetHead;
    private byte[] content;
    private int number;
    private double fix1;
    private double fix2;
    public Press(){}

    protected Press(Parcel in) {
        packetHead = in.readByte();
        content = in.createByteArray();
        number = in.readInt();
        fix1 = in.readDouble();
        fix2 = in.readDouble();
    }

    public static final Creator<Press> CREATOR = new Creator<Press>() {
        @Override
        public Press createFromParcel(Parcel in) {
            return new Press(in);
        }

        @Override
        public Press[] newArray(int size) {
            return new Press[size];
        }
    };

    public byte getPacketHead() {
        return packetHead;
    }

    public void setPacketHead(byte packetHead) {
        this.packetHead = packetHead;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public void setFix1(double fix1) {
        this.fix1 = fix1;
    }

    public void setFix2(double fix2) {
        this.fix2 = fix2;
    }

    public int getNumber() {

        return number;
    }

    public double getFix1() {
        return fix1;
    }

    public double getFix2() {
        return fix2;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(packetHead);
        dest.writeByteArray(content);
        dest.writeInt(number);
        dest.writeDouble(fix1);
        dest.writeDouble(fix2);
    }
}
