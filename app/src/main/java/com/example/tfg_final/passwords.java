package com.example.tfg_final;

import java.util.ArrayList;

class passwords {
    private static final passwords ourInstance = new passwords();
    public ArrayList<byte[]> customKeys = new ArrayList<byte[]>();
    public ArrayList<byte[]> obtainedKeys = new ArrayList<byte[]>();
    public ArrayList<byte[]> data = new ArrayList<byte[]>();
    public ArrayList<byte[]> modifiedData = new ArrayList<byte[]>();
    static passwords getInstance() {
        return ourInstance;
    }

    private passwords() {
        customKeys.add( new byte[]{(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff});
        customKeys.add( new byte[]{(byte)0xfa,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff});
        customKeys.add( new byte[]{(byte)0xfa,(byte)0xfa,(byte)0xff,(byte)0xff,(byte)0xff,(byte)0xff});
        customKeys.add( new byte[]{(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xff});
        /*customKeys.add( new byte[]{(byte)0x04,(byte)0x04,(byte)0x04,(byte)0x04,(byte)0x04,(byte)0x04});
        customKeys.add( new byte[]{(byte)0xa0,(byte)0xa1,(byte)0xa2,(byte)0xa3,(byte)0xa4,(byte)0xa5});*/
    }
}
