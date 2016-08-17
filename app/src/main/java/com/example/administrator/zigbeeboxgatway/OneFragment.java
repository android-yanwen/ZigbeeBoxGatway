package com.example.administrator.zigbeeboxgatway;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2016/8/8.
 */
public class OneFragment extends Fragment {
    private static final String tag = "OneFragment1";
    private MainActivity activity;

    private TextView id_temp_humi_sensor;
    private TextView id_illumination_sensor;
    private TextView id_smoke_sensor;
    private TextView id_rain_drop_sensor;
    private TextView id_combustibles_gas_sensor;
    private TextView id_dip_sensor;
//    private HashMap<String, String> hashMap = new HashMap<>();
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
    public void onAttach(Context activity) {
        super.onAttach(activity);
        Log.d(tag, "onAttach");
        this.activity = (MainActivity) getActivity();
        // 监听服务当中读取串口后发来的数据
        this.activity.setServiceDataListener(new MainActivity.HexDataListener() {
            @Override
            public void getServiceHexData(byte[] data) {
                Log.d(tag, "hex = " + ModebusParse.byteToHexString(data) + ", length = " + data.length);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.one_layout, container, false);
        id_temp_humi_sensor = (TextView) view.findViewById(R.id.id_temp_humi_sensor);
        id_illumination_sensor = (TextView) view.findViewById(R.id.id_illumination_sensor);
        id_smoke_sensor = (TextView) view.findViewById(R.id.id_smoke_sensor);
        id_rain_drop_sensor = (TextView) view.findViewById(R.id.id_rain_drop_sensor);
        id_combustibles_gas_sensor = (TextView) view.findViewById(R.id.id_combustibles_gas_sensor);
        id_dip_sensor = (TextView) view.findViewById(R.id.id_dip_sensor);
        return view;
    }

    private void refreshUi(String addr, String data) {
        int i_addr = Integer.parseInt(addr, 16);
        switch (i_addr) {
            case ModebusParse.TEMP_HUMI_SENSOR_ADDR:
                id_temp_humi_sensor.setText(data);
                break;
            case ModebusParse.ILLUMINTION_SENSOR_ADDR:
                id_illumination_sensor.setText(data);
                break;
            case ModebusParse.COMBUSTIBLE_GAS_ADDR:
                id_combustibles_gas_sensor.setText(data);
                break;
            case ModebusParse.RAIN_DROP_SENSOR_ADDR:
                id_rain_drop_sensor.setText(data);
                break;
            case ModebusParse.SMOKE_SENSOR_ADDR:
                id_smoke_sensor.setText(data);
                break;
            case ModebusParse.DIP_SENSOR_ADDR:
                id_dip_sensor.setText(data);
                break;
        }
    }
}
