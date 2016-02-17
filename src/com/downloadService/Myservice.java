package com.downloadService;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import com.download.enti.filedownload;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class Myservice extends Service
{
	public static final String ACTION_START = "ACTION_START";
	public static final String ACTION_STOP = "ACTION_STOP";
	public static final String UPDATE_SERVICE = "UPDATE_SERVICE";
	public static final String FILENAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/download/";
	public static final int MSG_INI = 0;
	private DownloadTask mtask;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		if (ACTION_START.equals(intent.getAction()))
		{
			filedownload mfiledownload = (filedownload) intent.getExtras().getSerializable("fileInfo");
			//初始化操作，读取网络文件的长度,在本地创建文件
			new InitThread(mfiledownload).start();

		} else if (ACTION_STOP.equals(intent.getAction()))
		{
			filedownload mfiledownload = (filedownload) intent.getExtras().getSerializable("fileInfo");
			//Log.d("info", "stop" + mfiledownload.toString());
			if (mtask != null)
			{
				mtask.isPause = true;
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy()
	{

		super.onDestroy();
	}

	/*
	 * 初始化操作
	 * 
	 */
	Handler mHanler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_INI:
				filedownload fileDownload = (filedownload) msg.obj;
				// Log.d("Info","test"+fileDownload.getLength());
				mtask = new DownloadTask(fileDownload, Myservice.this);
				mtask.download();
				break;
			}
		}

	};

	/*
	 *初始化操作
	 *分为四步
	 *1.连接网络
	 *2.获取要下载的文件长度
	 *3.在本地SD卡根目录创建一个文件
	 *4.设置文件的长度
	 */
	class InitThread extends Thread
	{
		private filedownload fileDownload;

		public InitThread(filedownload file)
		{
			this.fileDownload = file;
		}

		// 1.连接网络(耗时操作)	
		public void run()
		{
			HttpURLConnection conn;
			RandomAccessFile randomAcessFile;
			try
			{
				URL url = new URL(fileDownload.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				//设置连接的属性
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				int length = -1;
				if (conn.getResponseCode() == HttpStatus.SC_OK)
				{
					// Log.d("ResponseCode", ""+conn.getResponseCode());
					// 获取文件的长度
					length = conn.getContentLength();
					if (length <= 0)
					{
						return;
					}
					// 在本地创建文件
					File dir = new File(FILENAME);
					if (!dir.exists())
					{
						dir.mkdir();
					}
					File file = new File(dir, fileDownload.getFilename());
					randomAcessFile = new RandomAccessFile(file, "rwd");
					randomAcessFile.setLength(length);
					fileDownload.setLength(length);
					Message msg = mHanler.obtainMessage();
					msg.obj = fileDownload;
					mHanler.sendMessage(msg);

				}
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}

		// 设置文件的长度
	}

}
