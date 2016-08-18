package com.example.administrator.zigbeeboxgatway;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.administrator.service.MyService;
import com.example.administrator.tools.Util;

/**
 * Created by Administrator on 2016/8/9.
 */
public class ControlWindowActivity extends Activity {
    private static final String tag = "ControlWindowActivity";
    private MyService.MyBind myBind = null;
    private RadioButton id_forward_rbtn, id_reversal_rbtn;
    private RadioButton id_open_beep_rbtn, id_close_beep_rbtn;
    private RadioButton id_on_relay_rbtn, id_off_relay_rbtn;
    private Spinner id_smg_spinner, id_led_spinner;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(tag, "onServiceConnected");
            myBind = (MyService.MyBind) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_window_layout);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initView();
    }

    private void initView() {
        /**
         * 数码管的控制 Spinner
         */
        id_smg_spinner = (Spinner) findViewById(R.id.id_smg_spinner);
        id_smg_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ControlWindowActivity.this, "你选择了:" + position, Toast.LENGTH_SHORT).show();
                byte[] controlCmd = new byte[10];
                controlCmd[0] = 0x7e;
                controlCmd[1] = 0x7e;//head
                controlCmd[2] = 0x22;//addr
                controlCmd[3] = 0x03;//commend
                controlCmd[4] = 0x02;//type
                controlCmd[5] = (byte) Integer.parseInt(id_smg_spinner.getItemAtPosition(position).toString(), 16);
                controlCmd[6] = 0x00;
                controlCmd[7] = 0x00;
                byte[] aa = new byte[6];
                System.arraycopy(controlCmd, 2, aa, 0, 6);
                byte[] bb = new byte[2];
                Util.get_crc16(aa, aa.length, bb);
                controlCmd[8] = bb[0];
                controlCmd[9] = bb[1];
                if (myBind != null) {
                    myBind.writeDataToSerial(controlCmd);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**
         * LED点阵的控制 Spinner
         */
        id_led_spinner = (Spinner) findViewById(R.id.id_led_spinner);
        id_led_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(ControlWindowActivity.this, "你选择了:" + position, Toast.LENGTH_SHORT).show();
                byte[] controlCmd = new byte[10];
                controlCmd[0] = 0x7e;
                controlCmd[1] = 0x7e;//head
                controlCmd[2] = 0x22;//addr
                controlCmd[3] = 0x03;//commend
                controlCmd[4] = 0x04;//type
                controlCmd[5] = (byte) Integer.parseInt(id_led_spinner.getItemAtPosition(position).toString(), 16);
                controlCmd[6] = 0x00;
                controlCmd[7] = 0x00;
                byte[] aa = new byte[6];
                System.arraycopy(controlCmd, 2, aa, 0, 6);
                byte[] bb = new byte[2];
                Util.get_crc16(aa, aa.length, bb);
                controlCmd[8] = bb[0];
                controlCmd[9] = bb[1];
                if (myBind != null) {
                    myBind.writeDataToSerial(controlCmd);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        /**
         * 电机正反转的控制 RadioButton
         */
        id_forward_rbtn = (RadioButton) findViewById(R.id.id_forward_rbtn);
        id_forward_rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byte[] controlCmd = new byte[10];
                    controlCmd[0] = 0x7e;
                    controlCmd[1] = 0x7e;//head
                    controlCmd[2] = 0x22;//addr
                    controlCmd[3] = 0x03;//commend
                    controlCmd[4] = 0x0;//type
                    controlCmd[5] = (byte) 0x00;
                    controlCmd[6] = 0x00;
                    controlCmd[7] = 0x00;
                    byte[] aa = new byte[6];
                    System.arraycopy(controlCmd, 2, aa, 0, 6);
                    byte[] bb = new byte[2];
                    Util.get_crc16(aa, aa.length, bb);
                    controlCmd[8] = bb[0];
                    controlCmd[9] = bb[1];
                    if (myBind != null) {
                        myBind.writeDataToSerial(controlCmd);
                    }
                }
                Toast.makeText(ControlWindowActivity.this, "正转:" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        id_reversal_rbtn = (RadioButton) findViewById(R.id.id_reversal_rbtn);
        id_reversal_rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byte[] controlCmd = new byte[10];
                    controlCmd[0] = 0x7e;
                    controlCmd[1] = 0x7e;//head
                    controlCmd[2] = 0x22;//addr
                    controlCmd[3] = 0x03;//commend
                    controlCmd[4] = 0x00;//type
                    controlCmd[5] = (byte) 0xff;
                    controlCmd[6] = 0x00;
                    controlCmd[7] = 0x00;
                    byte[] aa = new byte[6];
                    System.arraycopy(controlCmd, 2, aa, 0, 6);
                    byte[] bb = new byte[2];
                    Util.get_crc16(aa, aa.length, bb);
                    controlCmd[8] = bb[0];
                    controlCmd[9] = bb[1];
                    if (myBind != null) {
                        myBind.writeDataToSerial(controlCmd);
                    }
                }
                Toast.makeText(ControlWindowActivity.this, "反转:" + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        /**
         * 蜂鸣器的开关控制 RadioButton
         */
        id_open_beep_rbtn = (RadioButton) findViewById(R.id.id_open_beep_rbtn);
        id_open_beep_rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byte[] controlCmd = new byte[10];
                    controlCmd[0] = 0x7e;
                    controlCmd[1] = 0x7e;//head
                    controlCmd[2] = 0x22;//addr
                    controlCmd[3] = 0x03;//commend
                    controlCmd[4] = 0x01;//type
                    controlCmd[5] = (byte) 0x00;
                    controlCmd[6] = 0x00;
                    controlCmd[7] = 0x00;
                    byte[] aa = new byte[6];
                    System.arraycopy(controlCmd, 2, aa, 0, 6);
                    byte[] bb = new byte[2];
                    Util.get_crc16(aa, aa.length, bb);
                    controlCmd[8] = bb[0];
                    controlCmd[9] = bb[1];
                    if (myBind != null) {
                        myBind.writeDataToSerial(controlCmd);
                    }
                }
            }
        });
        id_close_beep_rbtn = (RadioButton) findViewById(R.id.id_close_beep_rbtn);
        id_close_beep_rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byte[] controlCmd = new byte[10];
                    controlCmd[0] = 0x7e;
                    controlCmd[1] = 0x7e;//head
                    controlCmd[2] = 0x22;//addr
                    controlCmd[3] = 0x03;//commend
                    controlCmd[4] = 0x01;//type
                    controlCmd[5] = (byte) 0xff;
                    controlCmd[6] = 0x00;
                    controlCmd[7] = 0x00;
                    byte[] aa = new byte[6];
                    System.arraycopy(controlCmd, 2, aa, 0, 6);
                    byte[] bb = new byte[2];
                    Util.get_crc16(aa, aa.length, bb);
                    controlCmd[8] = bb[0];
                    controlCmd[9] = bb[1];
                    if (myBind != null) {
                        myBind.writeDataToSerial(controlCmd);
                    }
                }
            }
        });
        /**
         * 继电器的通断控制 RadioButton
         *
         */
        id_on_relay_rbtn = (RadioButton) findViewById(R.id.id_on_relay_rbtn);
        id_on_relay_rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byte[] controlCmd = new byte[10];
                    controlCmd[0] = 0x7e;
                    controlCmd[1] = 0x7e;//head
                    controlCmd[2] = 0x22;//addr
                    controlCmd[3] = 0x03;//commend
                    controlCmd[4] = 0x03;//type
                    controlCmd[5] = (byte) 0x00;
                    controlCmd[6] = 0x00;
                    controlCmd[7] = 0x00;
                    byte[] aa = new byte[6];
                    System.arraycopy(controlCmd, 2, aa, 0, 6);
                    byte[] bb = new byte[2];
                    Util.get_crc16(aa, aa.length, bb);
                    controlCmd[8] = bb[0];
                    controlCmd[9] = bb[1];
                    if (myBind != null) {
                        myBind.writeDataToSerial(controlCmd);
                    }
                }
            }
        });
        id_off_relay_rbtn = (RadioButton) findViewById(R.id.id_off_relay_rbtn);
        id_off_relay_rbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    byte[] controlCmd = new byte[10];
                    controlCmd[0] = 0x7e;
                    controlCmd[1] = 0x7e;//head
                    controlCmd[2] = 0x22;//addr
                    controlCmd[3] = 0x03;//commend
                    controlCmd[4] = 0x03;//type
                    controlCmd[5] = (byte) 0xff;
                    controlCmd[6] = 0x00;
                    controlCmd[7] = 0x00;
                    byte[] aa = new byte[6];
                    System.arraycopy(controlCmd, 2, aa, 0, 6);
                    byte[] bb = new byte[2];
                    Util.get_crc16(aa, aa.length, bb);
                    controlCmd[8] = bb[0];
                    controlCmd[9] = bb[1];
                    if (myBind != null) {
                        myBind.writeDataToSerial(controlCmd);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, "onResume");
        Intent intent = new Intent();
        intent.setAction("com.example.administrator.MyService.localservice");
        bindService(intent, serviceConnection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(tag, "onPause");
        if (myBind != null) {
            myBind = null;
            unbindService(serviceConnection);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
//            overridePendingTransition(R.anim.activity_from_up_to_down_in, R.anim.activity_from_up_to_down_out);
        }
        return super.onKeyDown(keyCode, event);
    }
}
