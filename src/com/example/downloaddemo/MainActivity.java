package com.example.downloaddemo;

import java.io.Serializable;

import com.download.enti.filedownload;
import com.downloadService.Myservice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener
{

	private ProgressBar mprogressBar;
	private Button start;
	private Button pause;
	private filedownload mfile;
	private TextView text;
	private TextView percent;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		WifiManager wifi = (WifiManager) getSystemService(WIFI_SERVICE);
		wifi.setWifiEnabled(true);

		mprogressBar = (ProgressBar) findViewById(R.id.id_progress);
		mprogressBar.setMax(100);
		start = (Button) findViewById(R.id.id_start);
		start.setOnClickListener(this);
		pause = (Button) findViewById(R.id.id_pause);
		pause.setOnClickListener(this);
		text = (TextView) findViewById(R.id.id_filename);
		percent = (TextView) findViewById(R.id.percent);
		//创建filedownload的实例
		mfile = new filedownload("http://www.imooc.com/mobile/mukewang.apk", "mukewang.apk", 0, 0, 0);
		text.setText(mfile.getFilename());
		//广播的注册
		IntentFilter filter = new IntentFilter(Myservice.UPDATE_SERVICE);
		registerReceiver(mBroadcastReceiver, filter);
	}

	@Override
	protected void onDestroy()
	{
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.id_start:
			// 开启服务
			Intent intent1 = new Intent(MainActivity.this, Myservice.class);
			Bundle bundle1 = new Bundle();
			bundle1.putSerializable("fileInfo", (Serializable) mfile);
			intent1.putExtras(bundle1);
			intent1.setAction(Myservice.ACTION_START);
			startService(intent1);
			break;
		case R.id.id_pause:
			Intent intent2 = new Intent(MainActivity.this, Myservice.class);
			Bundle bundle2 = new Bundle();
			bundle2.putSerializable("fileInfo", (Serializable) mfile);
			intent2.putExtras(bundle2);
			intent2.setAction(Myservice.ACTION_STOP);
			startService(intent2);
			break;

		}

	}

	//匿名内部类
	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver()
	{
		long time = System.currentTimeMillis();

		@Override
		public void onReceive(Context context, Intent intent)
		{
			if (Myservice.UPDATE_SERVICE.equals(intent.getAction()))

			{
				int finished = intent.getIntExtra("finished", 0);
				if ((System.currentTimeMillis() - time) > 500)
				{
					time = System.currentTimeMillis();
					mprogressBar.setProgress(finished);
					percent.setText(finished + "%");
				}
				if (finished == 100)
				{
					mprogressBar.setProgress(finished);
					percent.setText(finished + "%");
				}
			}

		}

	};

}
