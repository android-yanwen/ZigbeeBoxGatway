package com.example.administrator.zigbeeboxgatway;

/**
 * Created by Administrator on 2016/8/9.
 */
public class ModebusParse {
    private byte[] modebusData;
    public static final byte RAIN_DROP_SENSOR_ADDR = 0x11;  //雨滴传感器
    public static final byte TEMP_HUMI_SENSOR_ADDR = 0x12;  //温湿度传感器
    public static final byte DIP_SENSOR_ADDR = 0x13; //倾角传感器
    public static final byte ILLUMINTION_SENSOR_ADDR = 0x14;//光照度传感器
    public static final byte INFRARED_SENSOR_ADDR = 0x15;   //红外感应传感器
    public static final byte FIRE_SENSOR_ADDR = 0x16;     //火焰传感器
    public static final byte ULTRASONIC_SENSOR_ADDR = 0x17;//超声波传感器
    public static final byte COMBUSTIBLE_GAS_ADDR = 0x18;  //可燃气体传感器
    public static final byte DS1820_SENSOR_ADDR = 0x19; //ds18b20温度传感器
    public static final byte ALCOHOL_SENSOR_ADDR = 0x20; //酒精传感器
    public static final byte SMOKE_SENSOR_ADDR = 0x21; //烟雾传感器
    public static final byte CONTROL_MODULE_ADDR = 0x22; //控制模块

    public ModebusParse(byte[] data) {
        modebusData = data;
    }

    public String getAddressStr() {
        byte addr = modebusData[1];
        return Integer.toHexString(addr);
    }

    public String getResult() {
        byte addr = modebusData[1];
        switch (addr) {
            case RAIN_DROP_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "雨滴传感器\n无水滴";
                } else {
                    return "雨滴传感器\n有水滴";
                }
            case TEMP_HUMI_SENSOR_ADDR:
                byte b_humiH = modebusData[4];
                byte b_humiL = modebusData[5];
                String s_humi = "湿度：" + Integer.toString(b_humiH) + "." + Integer.toString(b_humiL);
                byte b_tempH = modebusData[6];
                byte b_tempL = modebusData[7];
                String s_temp = "湿度：" + Integer.toString(b_tempH) + "." + Integer.toString(b_tempL);
                return "温湿度传感器\n" + s_humi + "\n" + s_temp;
            case DIP_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "倾角传感器\n水平";
                } else {
                    return "倾角传感器\n倾斜";
                }
            case ILLUMINTION_SENSOR_ADDR:
                return "";
            case INFRARED_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "红外传感器\n无人";
                } else {
                    return "红外传感器\n有人";
                }
            case FIRE_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "火焰传感器\n无火焰";
                } else {
                    return "火焰传感器\n有火焰";
                }
            case ULTRASONIC_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "超声波传感器\n无遮挡";
                } else {
                    return "超声波传感器\n有遮挡";
                }
            case COMBUSTIBLE_GAS_ADDR:
                if (modebusData[4] == 0x00) {
                    return "可燃气体传感器\n无可燃气体";
                } else {
                    return "超声波传感器\n有可燃气体";
                }
            case DS1820_SENSOR_ADDR:
                int i_tempH = modebusData[4];
                int i_tempL = modebusData[5];
                String s_Temp = Integer.toString(i_tempH) + "." + Integer.toString(i_tempL);
                return "18B20温度数字温度传感器\n" + s_Temp;
            case SMOKE_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "烟雾传感器\n无烟雾";
                } else {
                    return "烟雾传感器\n有烟雾";
                }
            case ALCOHOL_SENSOR_ADDR:
                if (modebusData[4] == 0x00) {
                    return "酒精传感器\n无酒精";
                } else {
                    return "酒精传感器\n有酒精";
                }
        }
        return "";
    }

}
