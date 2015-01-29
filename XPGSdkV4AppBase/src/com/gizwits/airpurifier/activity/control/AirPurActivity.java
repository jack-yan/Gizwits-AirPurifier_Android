package com.gizwits.airpurifier.activity.control;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gizwits.aircondition.R;
import com.gizwits.airpurifier.activity.slipbar.SlipBarActivity;
import com.gizwits.framework.Interface.OnDialogOkClickListenner;
import com.gizwits.framework.activity.BaseActivity;
import com.gizwits.framework.config.JsonKeys;
import com.gizwits.framework.entity.DeviceAlarm;
import com.gizwits.framework.utils.DialogManager;
import com.gizwits.framework.utils.PxUtil;
import com.xtremeprog.xpgconnect.XPGWifiDevice;

public class AirPurActivity extends BaseActivity implements OnClickListener,OnTouchListener {
	private final String TAG = "YouAoControlFrament";
	
	private LinearLayout timingOn_layout;
	private ImageView timingOn_iv;
	private TextView timingOn_tv;
	private Dialog timeDialog;
	private RelativeLayout disconnected_layout;
	private Button reConn_btn;
	private ImageView push_iv;//底部箭头
	private RelativeLayout alarmTips_layout;
	private TextView alarmCounts_tv;
	private ImageView ivTitleRight;//左上角菜单按钮
	private ImageView ivTitleLeft;//右上角菜单按钮
	private TextView tvTitle;//顶部设备名称显示
	private TextView homeQualityResult_tv;
	private ImageView homeQualityResult_iv;
	private ImageView setTimeOff_iv;
	private LinearLayout functions_layout;
	private ImageView plasma_iv;//离子开关显示iv
	private ImageView childLock_iv;//童锁开关显示iv
	private ImageView qualityLight_iv;//空气灯开关显示iv
	private TextView outdoorQuality_tv;//室外空气质量指数
	private TextView pm25_tv;//展示pm2.5
	private TextView pm10_tv;//展示pm10
	private ImageView palasmaO_iv;//离子功能按钮
	private ImageView childLockO_iv;//童锁功能按钮
	private ImageView qualityLightO_iv;//空气灯功能按钮
	private ImageView silent_iv;//睡眠
	private ImageView standar_iv;//标准
	private ImageView strong_iv;//强力
	private ImageView auto_iv;//自动
	private RelativeLayout turnOff_layout;//关机界面
	private ImageView turnOn_iv;//开机按钮
	private RelativeLayout back_layout;//打开底部隐藏菜单后，半透明黑色遮罩层
	private Button back_btn;//灰色遮罩层按钮，可点击，退出底部菜单
	private ImageView timingOff_iv;
	private ImageView homeQualityTip_iv;//空气质量显示横条中标志
	private static View mView;//侧拉菜单拉出后右边显示本界面
	
	float mL = 0;
	float mR = 0;
	float mW = 0;
	float mW100 = 0;
	private float mBgW = 0;
	private int select_id;//选中的id
	/** The is click. */
	private boolean isClick;
	/** The device data map. */
	private ConcurrentHashMap<String, Object> deviceDataMap;
	/** The m fault dialog. */
	private Dialog mFaultDialog;
	/** The statu map. */
	private ConcurrentHashMap<String, Object> statuMap;
	/** The alarm list. */
	private ArrayList<DeviceAlarm> alarmList;
	
	private enum handler_key {

		/** The update ui. */
		UPDATE_UI,

		/** The alarm. */
		ALARM,

		/** The disconnected. */
		DISCONNECTED,

		/** The received. */
		RECEIVED,

		/** The get statue. */
		GET_STATUE,
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.youao_control);
		initUI();
		statuMap = new ConcurrentHashMap<String, Object>();
		alarmList = new ArrayList<DeviceAlarm>();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		handler.sendEmptyMessage(handler_key.GET_STATUE.ordinal());
		mXpgWifiDevice.setListener(deviceListener);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	private void initUI(){
		turnOff_layout=(RelativeLayout) findViewById(R.id.turnOff_layout);
		turnOn_iv=(ImageView) findViewById(R.id.turnOn_iv);
		turnOn_iv.setOnClickListener(this);
		mView = findViewById(R.id.main_layout);
		homeQualityTip_iv = (ImageView) findViewById(R.id.homeQualityTipArrow_iv);
		back_btn = (Button) findViewById(R.id.back_btn);
		back_btn.setOnTouchListener(this);
//		timmingOff_tv = (TextView) findViewById(R.id.timmingOff_tv);
//		timmingOff_tv.setOnClickListener(this);
//		alarmCounts_tv = (TextView) findViewById(R.id.alarmCounts_tv);
//		alarmTips_layout = (RelativeLayout) findViewById(R.id.alarmTips_layout);

		back_layout = (RelativeLayout) findViewById(R.id.back_layout);
//		push_iv = (ImageView) findViewById(R.id.push_iv);
//		push_iv.setOnClickListener(this);
		// connectType_tv = (TextView) findViewById(R.id.connectType_tv);
//		timingOff_iv = (ImageView) findViewById(R.id.timingOff_iv);
//		timingOff_iv.setOnClickListener(this);
		homeQualityResult_tv = (TextView) findViewById(R.id.homeQualityResult_tv);
		homeQualityResult_iv = (ImageView) findViewById(R.id.homeQualityResult_iv);
		setTimeOff_iv = (ImageView) findViewById(R.id.setTimeOff_iv);
		setTimeOff_iv.setOnTouchListener(onTouchListener);
		functions_layout = (LinearLayout) findViewById(R.id.functions_layout);
		//********************效果显示********************
		plasma_iv = (ImageView) findViewById(R.id.plasama_iv);
		childLock_iv = (ImageView) findViewById(R.id.childLock_iv);
		qualityLight_iv = (ImageView) findViewById(R.id.qualityLight_iv);
		outdoorQuality_tv = (TextView) findViewById(R.id.outdoorQuality_tv);
		//********************pm25 10的值********************
		pm25_tv = (TextView) findViewById(R.id.pm25_tv);
		pm10_tv = (TextView) findViewById(R.id.pm10_tv);
		//********************隐藏的功能键********************
		setTimeOff_iv = (ImageView) findViewById(R.id.setTimeOff_iv);
		setTimeOff_iv.setOnClickListener(this);
		palasmaO_iv = (ImageView) findViewById(R.id.plasmaO_iv);
		palasmaO_iv.setOnClickListener(this);
		childLockO_iv = (ImageView) findViewById(R.id.childLockO_iv);
		childLockO_iv.setOnClickListener(this);
		qualityLightO_iv = (ImageView) findViewById(R.id.qualityLightO_iv);
		qualityLightO_iv.setOnClickListener(this);
		//********************风速档位********************
		auto_iv = (ImageView) findViewById(R.id.auto_iv);
		auto_iv.setOnClickListener(this);
		standar_iv = (ImageView) findViewById(R.id.standar_iv);
		standar_iv.setOnClickListener(this);
		strong_iv = (ImageView) findViewById(R.id.strong_iv);
		strong_iv.setOnClickListener(this);
		silent_iv = (ImageView) findViewById(R.id.silent_iv);
		silent_iv.setOnClickListener(this);
		//********************顶部按钮********************
		ivTitleRight = (ImageView) findViewById(R.id.ivPower);
		ivTitleRight.setOnClickListener(this);
		ivTitleLeft = (ImageView) findViewById(R.id.ivMenu);
		ivTitleLeft.setOnClickListener(this);
		tvTitle = (TextView) findViewById(R.id.tvTitle);
	}

	/**
	 * 初始化 空气质量指示条
	 */
	private void initQualityTips() {

		ViewTreeObserver vto = homeQualityResult_iv.getViewTreeObserver();

		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				float w = homeQualityResult_iv.getWidth();
				float l = homeQualityResult_iv.getLeft();
				float r = homeQualityResult_iv.getRight();

				// 提示条分为100份
				mL = l + w / 50;
				mR = r - w / 10;
				mW = mR - mL;
				mW100 = mW / 100;
			}
		});

	}

	/**
	 * 更新空气质量指示条 指示位置
	 * 
	 * @param position
	 */
	private void updateTips(final float position) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				homeQualityTip_iv.setX(mL + position);
			}
		});

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.reConn_btn:
			break;
		case R.id.childLockO_iv:
			if (childLockO_iv.getTag().toString() == "0") {
				mCenter.cChildLock(mXpgWifiDevice, false);
			}else{
				mCenter.cChildLock(mXpgWifiDevice, true);
			}
			break;
		case R.id.plasmaO_iv:
			if (palasmaO_iv.getTag().toString() == "0") {
				mCenter.cSwitchPlasma(mXpgWifiDevice, false);
			}else{
				mCenter.cSwitchPlasma(mXpgWifiDevice, true);
			}
			break;
		case R.id.qualityLightO_iv:
			if (qualityLightO_iv.getTag().toString() == "0") {
				mCenter.cLED(mXpgWifiDevice, false);
			}else{
				mCenter.cLED(mXpgWifiDevice, true);
			}
			break;
		case R.id.turnOn_iv:
			mCenter.cSwitchOn(mXpgWifiDevice, true);
			break;
		case R.id.timingOn_layout:
		case R.id.timingOn_iv:
		case R.id.timingOn_tv:
			break;
		case R.id.ivMenu:
			if (!isClick) {
				isClick = true;
				startActivityForResult(new Intent(AirPurActivity.this,
						SlipBarActivity.class), Activity.RESULT_FIRST_USER);
				overridePendingTransition(0, 0);
			}
			break;
		case R.id.ivPower:
			mCenter.cSwitchOn(mXpgWifiDevice, false);
			break;
		case R.id.auto_iv:
			mCenter.cSetSpeed(mXpgWifiDevice, "3");
			break;
		case R.id.silent_iv:
			mCenter.cSetSpeed(mXpgWifiDevice, "0");
			break;
		case R.id.standar_iv:
			mCenter.cSetSpeed(mXpgWifiDevice, "1");
			break;
		case R.id.strong_iv:
			mCenter.cSetSpeed(mXpgWifiDevice, "2");
			break;
//		case R.id.push_iv:
//			troggleBottom();
//			break;
		default:
			break;
		}
	}

	/**
	 * bottom 功能的显示和隐藏
	 */
	public void troggleBottom() {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) functions_layout
				.getLayoutParams();
		int bM = params.bottomMargin;
		if (bM == 0) {
			params.bottomMargin = PxUtil.dip2px(this, -76);
			back_layout.setVisibility(View.GONE);
		} else {
			params.bottomMargin = 0;
			back_layout.setVisibility(View.VISIBLE);
		}
		functions_layout.setLayoutParams(params);
	}

	public void bottomFucTrogg() {
		push_iv.setVisibility(View.VISIBLE);
	}

	private int currentOn = 0;
	private OnDialogOkClickListenner okTimingOnClickListenner = new OnDialogOkClickListenner() {

		@Override
		public void onClick(View arg0) {
			timeDialog.dismiss();
			if (currentOn == 0) {
				setTimingOn(false, currentOn);
			} else {
				setTimingOn(true, currentOn);
			}

		}

		@Override
		public void callBack(Object obj) {
			currentOn = (Integer) obj;
		}
	};
	private int currentOff = 0;
	private OnDialogOkClickListenner okTimingOffClickListenner = new OnDialogOkClickListenner() {

		@Override
		public void onClick(View arg0) {
			timeDialog.dismiss();
			// cmd.setTimingOff()
			if (currentOff == 0) {
				setTimingOff(false, currentOff);
			} else {
				setTimingOff(true, currentOff);
			}
		}

		@Override
		public void callBack(Object obj) {
			currentOff = (Integer) obj;
		}
	};

	/**
	 * 设置开关状态
	 * 
	 * @param isOn
	 */
	public void setSwitch(boolean isOn) {
		if (isOn) {
			turnOff_layout.setVisibility(View.GONE);
		} else {
			turnOff_layout.setVisibility(View.VISIBLE);
			reAll();
		}

	}

	/**
	 * 运行模式
	 * 
	 * @param speed
	 */
	public void changeRUNmodeBg(int speed) {
		reAll();
		if (speed == 0) {
			setSilentAnimation();
			select_id=4;
		} 
		if (speed == 1) {
			setStandarAnimation();
			select_id=2;
		} 

		if (speed == 2) {
			setStrongAnimation();
			select_id=1;
		} 

		if (speed == 3) {
			setAutoAnimation();
			select_id=5;
		} 

	}

	/**
	 * 定时开机
	 * 
	 * @param isOn
	 */
	public void setTimingOn(boolean isOn, int time) {
		if (isOn) {
			timingOn_tv.setText(time + "小时后开机");
			timingOn_layout.setBackgroundResource(R.drawable.alarm_select);
			timingOn_iv.setTag("0");
		} else {
			timingOn_tv.setText("定时开机");
			timingOn_layout.setBackgroundResource(R.drawable.alarm);
			timingOn_iv.setTag("1");
		}
	}

	/**
	 * 定时关机
	 * 
	 * @param isOn
	 */
	public void setTimingOff(boolean isOn, int time) {
//		if (isOn) {
//			timmingOff_tv.setText(time + "小时后关机");
//			// timingOff_layout.setImageResource(R.drawable.alarm_select);
//			timingOff_iv.setTag("0");
//		} else {
//			timmingOff_tv.setText("定时关机")a;s
//			// timingOff_layout.setImageResource(R.drawable.alarm);
//			timingOff_iv.setTag("1");
//		}
	}

	public void setChildLock(boolean isOn) {
		if (isOn) {
			childLockO_iv.setImageResource(R.drawable.icon9_2);
			childLockO_iv.setTag("0");
			childLock_iv.setImageResource(R.drawable.lock_select);
		} else {
			childLockO_iv.setImageResource(R.drawable.icon9);
			childLockO_iv.setTag("1");
			childLock_iv.setImageResource(R.drawable.lock_not_select);
		}
	}

	public void setPlasma(boolean isOn) {
		if (isOn) {
			palasmaO_iv.setImageResource(R.drawable.icon8_2);
			palasmaO_iv.setTag("0");
			plasma_iv.setImageResource(R.drawable.anion_select);
		} else {
			palasmaO_iv.setImageResource(R.drawable.icon8);
			palasmaO_iv.setTag("1");
			plasma_iv.setImageResource(R.drawable.anion_not_select);
		}
	}

	public void setIndicatorLight(boolean isOn) {
		if (isOn) {
			qualityLightO_iv.setImageResource(R.drawable.icon10_2);
			qualityLightO_iv.setTag("0");
			qualityLight_iv.setImageResource(R.drawable.quality_select);
		} else {
			qualityLightO_iv.setImageResource(R.drawable.icon10);
			qualityLightO_iv.setTag("1");
			qualityLight_iv.setImageResource(R.drawable.quality_not_select);
		}
	}

	private OnTouchListener onTouchListener = new OnTouchListener() {

		private double yDown;
		private double yCurrent;
		private boolean isProgressing = false;

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				isProgressing = false;
				yDown = event.getY();

				break;

			case MotionEvent.ACTION_MOVE:
				yCurrent = event.getY();
				if (Math.abs(yDown - yCurrent) > 40) {
					if (!isProgressing) {
						troggleBottom();
						isProgressing = true;
					}
				}
				break;
			}
			return true;
		}
	};

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.back_btn) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				troggleBottom();
			}
		}
		return true;
	}
	
	/**
	 * 设置silent的帧动画
	 */
	public void setSilentAnimation(){
		silent_iv.setBackgroundResource(R.drawable.icon_sleep_select);
	}
	
	/**
	 * 设置standar的帧动画
	 */
	public void setStandarAnimation(){
		standar_iv.setBackgroundResource(R.drawable.icon_standard_select);
	}
	
	/**
	 * 设置strong背景
	 */
	public void setStrongAnimation(){
		strong_iv.setBackgroundResource(R.drawable.icon_strong_select);
	}
	
	/**
	 * 设置auto背景
	 */
	public void setAutoAnimation(){
		auto_iv.setBackgroundResource(R.drawable.icon_intelligence_select);
	}
	
	/**
	 * 设置重设全部功能iv
	 */
	public void reAll(){
		silent_iv.setBackgroundResource(R.drawable.icon_sleep_not_select);
		standar_iv.setBackgroundResource(R.drawable.icon_standard_not_select);
		strong_iv.setBackgroundResource(R.drawable.icon_strong_not_select);
		auto_iv.setBackgroundResource(R.drawable.icon_intelligence_not_select);
	}

	/**
	 * Gets the view.
	 * 
	 * @return the view
	 */
	public static Bitmap getView() {
		// 用指定大小生成一张透明的32位位图，并用它构建一张canvas画布
		Bitmap mBitmap = Bitmap.createBitmap(mView.getWidth(),
				mView.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(mBitmap);
		// 将指定的view包括其子view渲染到这种画布上，在这就是上一个activity布局的一个快照，现在这个bitmap上就是上一个activity的快照
		mView.draw(canvas);
		return mBitmap;
	}
	
	@Override
	protected void didDisconnected(XPGWifiDevice device) {
		super.didDisconnected(device);
	}
	
	@Override
	protected void didReceiveData(XPGWifiDevice device,
			ConcurrentHashMap<String, Object> dataMap, int result) {
		Log.e(TAG, "didReceiveData");
		this.deviceDataMap = dataMap;
		handler.sendEmptyMessage(handler_key.RECEIVED.ordinal());
	}
	

	/**
	 * The handler.
	 */
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			handler_key key = handler_key.values()[msg.what];
			switch (key) {
			case RECEIVED:
				try {
					if (deviceDataMap.get("data") != null) {
						Log.i("info", (String) deviceDataMap.get("data"));
						inputDataToMaps(statuMap,
								(String) deviceDataMap.get("data"));

					}
					alarmList.clear();
					if (deviceDataMap.get("alters") != null) {
						Log.i("info", (String) deviceDataMap.get("alters"));
						// 返回主线程处理报警数据刷新
//						inputAlarmToList((String) deviceDataMap.get("alters"));
					}
					if (deviceDataMap.get("faults") != null) {
						Log.i("info", (String) deviceDataMap.get("faults"));
						// 返回主线程处理错误数据刷新
//						inputAlarmToList((String) deviceDataMap.get("faults"));
					}
					// 返回主线程处理P0数据刷新
					handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
					handler.sendEmptyMessage(handler_key.ALARM.ordinal());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			case UPDATE_UI:
				if (statuMap != null && statuMap.size() > 0) {
					changeRUNmodeBg(Integer.parseInt(statuMap.get(JsonKeys.FAN_SPEED).toString()));
					setChildLock((Boolean)statuMap.get(JsonKeys.Child_Lock));
					setIndicatorLight((Boolean)statuMap.get(JsonKeys.LED));
					setPlasma((Boolean)statuMap.get(JsonKeys.Plasma));
					setSwitch((Boolean)statuMap.get(JsonKeys.ON_OFF));
				}
				break;
			case ALARM:
				if (alarmList != null && alarmList.size() > 0) {
					if (mFaultDialog == null) {
						mFaultDialog = DialogManager.getDeviceErrirDialog(
								AirPurActivity.this, "设备故障",
								new OnClickListener() {

									@Override
									public void onClick(View v) {
										Intent intent = new Intent(
												Intent.ACTION_CALL, Uri
														.parse("tel:10086"));
										startActivity(intent);
										mFaultDialog.dismiss();
										mFaultDialog = null;
									}
								});

					}
					mFaultDialog.show();
//					setTipsLayoutVisiblity(true, alarmList.size());
				} else {
//					setTipsLayoutVisiblity(false, 0);
				}
				break;
			case DISCONNECTED:
                mCenter.cDisconnect(mXpgWifiDevice);
				break;
			case GET_STATUE:
				mCenter.cGetStatus(mXpgWifiDevice);
				break;
			}
		}
	};
	
	/**
	 * Input data to maps.
	 * 
	 * @param map
	 *            the map
	 * @param json
	 *            the json
	 * @throws JSONException
	 *             the JSON exception
	 */
	private void inputDataToMaps(ConcurrentHashMap<String, Object> map,
			String json) throws JSONException {
		Log.i("revjson", json);
		JSONObject receive = new JSONObject(json);
		Iterator actions = receive.keys();
		while (actions.hasNext()) {

			String action = actions.next().toString();
			Log.i("revjson", "action=" + action);
			// 忽略特殊部分
			if (action.equals("cmd") || action.equals("qos")
					|| action.equals("seq") || action.equals("version")) {
				continue;
			}
			JSONObject params = receive.getJSONObject(action);
			Log.i("revjson", "params=" + params);
			Iterator it_params = params.keys();
			while (it_params.hasNext()) {
				String param = it_params.next().toString();
				Object value = params.get(param);
				map.put(param, value);
				Log.i(TAG, "Key:" + param + ";value" + value);
			}
		}
		handler.sendEmptyMessage(handler_key.UPDATE_UI.ordinal());
	}
}