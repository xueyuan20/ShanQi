package com.example.chensanqi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.update.UmengDialogButtonListener;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class AboutActivity extends Activity implements View.OnClickListener{
	TextView tvFeedback, tvAppDesc, tvUpdate;
	FeedbackAgent agent;
	UpdateResponse updateResp;
	Context mContext;
	boolean mHasNewVersion = false;
	Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				if (mHasNewVersion) {
					UmengUpdateAgent.showUpdateDialog(mContext, updateResp);
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.activity_about);
		tvFeedback = (TextView)findViewById(R.id.feedback);
		tvFeedback.setOnClickListener(this);
		tvAppDesc = (TextView)findViewById(R.id.app_desc);
		tvAppDesc.setOnClickListener(this);
		tvUpdate = (TextView)findViewById(R.id.update);
		tvUpdate.setOnClickListener(this);
		agent = new FeedbackAgent(mContext);
		agent.sync();

		// 版本更新
	       
        /**
         * [true(default) update only wifi,
         *  false we'll try to update during 2G or 3G ,
         *  if you have no better idea,just gore this params]
         *  
         **/
        UmengUpdateAgent.setUpdateOnlyWifi(true);

        /**
         *  MobclickAgent.enableCacheInUpdate 
         *     [true(default) enable cached apk to be installed 
         *     			which has been downloaded before]
         *     [false we'll always download the latest version from the server. 
         *     			if you have no better idea,just igore this params.]
         * 
         */

        /**
         *  set false if you want to handle the update result by yourself.
         *  and add a UmengUpdateListener() like below
         **/
        UmengUpdateAgent.setUpdateAutoPopup(false);
        UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {

			@Override
			public void onUpdateReturned(int updateState, UpdateResponse arg1) {
				// TODO Auto-generated method stub
				updateResp = arg1;

				switch(updateState){
				case UpdateStatus.Yes: 				//has update
					tvUpdate.setText(getResources().getString(R.string.update_new));
					mHasNewVersion = true;
					break;
				case UpdateStatus.No:					//has no update
					break;
				case UpdateStatus.NoneWifi:				//none wifi
					break;				
				case UpdateStatus.Timeout:				//time out
					break;
				}
			} 
        });

        UmengUpdateAgent.setDialogListener(new UmengDialogButtonListener() {
			
			@Override
			public void onClick(int state) {
				// TODO Auto-generated method stub
				switch (state) {
				case UpdateStatus.Update:
					// 现在就更新
					Toast.makeText(mContext, "现在开始更新",
							Toast.LENGTH_LONG).show();
					tvUpdate.setText(getResources().getString(R.string.update));
					mHasNewVersion = false;
					break;

				case UpdateStatus.NotNow:
					Toast.makeText(mContext, "现在开始更新",
							Toast.LENGTH_LONG).show();
					break;

				default:
					break;
				}
			}
		});

        /**
         * Listen to the update events.
         */
        UmengUpdateAgent.update(mContext);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.feedback:
		    agent.startFeedbackActivity();
			break;

		case R.id.app_desc:
			startActivity(new Intent(AboutActivity.this, AppDescActivity.class));
			break;

		case R.id.update:
			UmengUpdateAgent.update(mContext);
			int delay = mHasNewVersion ? 0 : 1000;
			mHandler.sendEmptyMessageDelayed(0, delay);
			break;

		default:
			break;
		}
	}
}
