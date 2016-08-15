package com.example.administrator.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;

import android_serialport_api.Application;
import android_serialport_api.SerialPort;

/**
 * Created by Administrator on 2016/8/8.
 */
public class MyService extends Service {
    private static final String tag = "MyService";
    private Application mApplication;
    private SerialPort mSerialPort;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private boolean isRun = false;
    private MyBind bind = new MyBind();
    private byte[] availHexData;
    public class MyBind extends Binder {
        public byte[] getSerialHexData() {
            return availHexData == null ? new byte[]{} : availHexData;
        }
        public void writeDataToSerial(byte[] data) {
            try {
                mOutputStream.write(data);
                mOutputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(tag, "onBind");
        return bind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(tag, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(tag, "onCreate");
        isRun = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                mApplication = (Application) getApplication();
                try {
                    mSerialPort = mApplication.getSerialPort();
                    mInputStream = mSerialPort.getInputStream();
                    mOutputStream = mSerialPort.getOutputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InvalidParameterException e) {
                }
                while (isRun) {
                    byte[] hexData = new byte[256];
                    try {
                        if (mInputStream.available() > 0 != false) {  //有数据到来
                            int size = mInputStream.read(hexData);
                            if (size > 0) {
                                availHexData = Arrays.copyOf(hexData, size);
//                                System.arraycopy(hexData, 0, availHexData, 0, size);
                            }
                        }
                        Thread.sleep(100);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(tag, "onDestroy");
        isRun = false;
        mApplication.closeSerialPort();
    }



}
