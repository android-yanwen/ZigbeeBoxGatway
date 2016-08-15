package com.example.administrator.zigbeeboxgatway;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/8/8.
 */
public class TwoFragment extends Fragment {
    private static final String tag = "TwoFragment";
    private MainActivity activity;
    private TextView id_ultrasonic_sensor, id_fire_sensor, id_alcohol_sensor, id_infrared_sensor, id_temp_1820_sensor;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String addr = bundle.getString("addr");
            String data = bundle.getString("data");
            refreshUi(addr, data);
        }
    };
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) getActivity();
        activity.setServiceDataListener(new MainActivity.HexDataListener() {
            @Override
            public void getServiceHexData(byte[] data) {
                Log.d(tag, "hex = " + new String(data) + ", length = " + data.length);
                if (data.length > 0) {
                    int i = 0;
                    int count = 0;
                    byte[][] allData = new byte[128][128];
                    while (true) {
                        if (data[i] == 0x7E) {
                            int index = 0;
                            while (true) {
                                allData[count][index++] = data[i++];
                                if (i >= data.length) {
                                    break;
                                }
                                if (data[i] == 0x7E) {
                                    break;
                                }
                            }
                        } else {
                            break;
                        }
                        count++;
                        if (i >= data.length) {
                            break;
                        }
                    }
                    for (int j = 0;  j < count; j++) {
                        ModebusParse modebusParse = new ModebusParse(allData[j]);
                        Message msg = handler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putString("addr", modebusParse.getAddressStr());
                        bundle.putString("data", modebusParse.getResult());
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            }
        });

    }

    private void refreshUi(String addr, String data) {
        int i_addr = Integer.parseInt(addr, 16);
        switch (i_addr) {
            case ModebusParse.ULTRASONIC_SENSOR_ADDR:
                id_ultrasonic_sensor.setText(data);
                break;
            case ModebusParse.FIRE_SENSOR_ADDR:
                id_fire_sensor.setText(data);
                break;
            case ModebusParse.ALCOHOL_SENSOR_ADDR:
                id_alcohol_sensor.setText(data);
                break;
            case ModebusParse.INFRARED_SENSOR_ADDR:
                id_infrared_sensor.setText(data);
                break;
            case ModebusParse.DS1820_SENSOR_ADDR:
                id_temp_1820_sensor.setText(data);
                break;
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.two_layout, container, false);
        id_ultrasonic_sensor = (TextView) view.findViewById(R.id.id_ultrasonic_sensor);
        id_fire_sensor = (TextView) view.findViewById(R.id.id_fire_sensor);
        id_alcohol_sensor = (TextView) view.findViewById(R.id.id_alcohol_sensor);
        id_infrared_sensor = (TextView) view.findViewById(R.id.id_infrared_sensor);
        id_temp_1820_sensor = (TextView) view.findViewById(R.id.id_temp_1820_sensor);
        return view;
    }
}
