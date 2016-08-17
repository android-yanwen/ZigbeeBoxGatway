package com.example.administrator.zigbeeboxgatway;

import android.app.AlertDialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.service.MyService;
import com.example.administrator.view.FixedSpeedScroller;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android_serialport_api.Application;
import android_serialport_api.SerialPortPreferences;

public class MainActivity extends AppCompatActivity {
    private static final String tag = "MainActivity";
    private ViewPager myViewPager;
    private FixedSpeedScroller mScroller;//控制ViewPage滑动速度
    private List<View> views = new ArrayList<>();
    private MyService.MyBind bind = null;
    private ImageButton id_right_ibtn, id_left_ibtn;
    private Timer timer;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            Log.d(tag, "onServiceConnected");
            bind = (MyService.MyBind) service;
            if (dataListener != null && dataListener2 != null) {
                // 没隔一段时间调用HexServiceLitener接口类的getServiceHexData()接口
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        byte[] b_data = bind.getSerialHexData();
                        Log.d(tag, ModebusParse.byteToHexString(b_data));
                        dataListener.getServiceHexData(bind.getSerialHexData());
                        dataListener2.getServiceHexData(bind.getSerialHexData());
                    }
                }, 0 , 500);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(tag, "onServiceDisconnected");
//            unbindService(connection);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initView();

    }

    /**
     * 定义接口用于传递串口数据
     */
    public interface HexDataListener {
        void getServiceHexData(byte[] data);
    }
    private HexDataListener dataListener, dataListener2;
    // 要获取串口数据必须要监听此接口
    public void setServiceDataListener(HexDataListener hexDataListener){
        dataListener = hexDataListener;

    }
    public void setServiceDataListener2(HexDataListener hexDataListener){
        dataListener2 = hexDataListener;
    }


    private float alpha = (float) 1; /*此变量设置左右箭头渐变的颜色值*/
    private Handler updateAlphaHandler;
    private Runnable myRunnable;
    private boolean isOnePeriod = false;/*判断渐变是否完成了一个周期*/

    /**
     * 初始化界面的各个组件
     */
    private void initView() {
        myViewPager = (ViewPager) findViewById(R.id.id_my_view_pager);
        views.add(getLayoutInflater().inflate(R.layout.one_fragment, null));
        views.add(getLayoutInflater().inflate(R.layout.two_fragment, null));
        myViewPager.setAdapter(new ViewPagerAdapter(views));
        myViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                Toast.makeText(MainActivity.this, "position="+position, Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        if (id_left_ibtn != null && id_right_ibtn != null) {
                            id_right_ibtn.setVisibility(View.VISIBLE);
                            id_left_ibtn.setVisibility(View.INVISIBLE);
                        }
                        break;
                    case 1:
                        if (id_left_ibtn != null && id_right_ibtn != null) {
                            id_right_ibtn.setVisibility(View.INVISIBLE);
                            id_left_ibtn.setVisibility(View.VISIBLE);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        try {
            /*
            改变ViewPage滑动速度关键代码
             */
            Field mField = ViewPager.class.getDeclaredField("mScroller");
            mField.setAccessible(true);
            mScroller = new FixedSpeedScroller(myViewPager.getContext(), new AccelerateInterpolator());
            mField.set(myViewPager, mScroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        mScroller.setmDuration(300);

        /**
         * 下面是左右两个ImageButton的操作
         */
        id_right_ibtn = (ImageButton) findViewById(R.id.id_right_ibtn);
        id_right_ibtn.setAlpha(alpha);
        id_right_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewPager.setCurrentItem(1);
            }
        });
        id_left_ibtn = (ImageButton) findViewById(R.id.id_left_ibtn);
        id_left_ibtn.setAlpha(alpha);
        id_left_ibtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myViewPager.setCurrentItem(0);
            }
        });
        updateAlphaHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                updateAlphaHandler.postDelayed(myRunnable, 100);
//                Log.d(tag, "handler post delay");
                if (!isOnePeriod) { //从亮逐渐变暗
                    alpha -= 0.1;
                    if (alpha <= 0.1) {
                        isOnePeriod = true;
                    }
                }
                if (isOnePeriod) { //从暗逐渐变亮
                    alpha += 0.1;
                    if (alpha >= 1.0) {
                        isOnePeriod = false;
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        id_right_ibtn.setAlpha(alpha);
                        id_left_ibtn.setAlpha(alpha);
                    }
                });
            }
        };
        updateAlphaHandler.postDelayed(myRunnable, 100);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(tag, "onResume");
        try {
            //这一步是检测串口是否配置
            ((Application) getApplication()).getSerialPort();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvalidParameterException e) {
            // 如果串口未配置会抛出InvalidParameterException异常
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("请先配置串口");
            builder.setCancelable(false);
            builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(MainActivity.this, SerialPortPreferences.class));
                }
            });
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
            return;
        }

        Intent intent = new Intent();
        intent.setAction("com.example.administrator.MyService.localservice");
//        intent.setClass(MainActivity.this, MyService.class);
        bindService(intent, connection, Service.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bind != null) {
            timer.cancel();
            unbindService(connection);
            bind = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SerialPortPreferences.class));
            overridePendingTransition(R.anim.activity_from_up_to_down_in, R.anim.activity_from_up_to_down_out);
        } else if (id == R.id.action_control) {
            startActivity(new Intent(MainActivity.this, ControlWindowActivity.class));
            overridePendingTransition(R.anim.activity_from_down_to_up_in, R.anim.activity_from_down_to_up_out);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
