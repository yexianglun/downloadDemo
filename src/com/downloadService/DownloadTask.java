package com.downloadService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.http.HttpStatus;

import com.download.db.ThreadDAO;
import com.download.db.ThreadDAOImpl;
import com.download.enti.Threadinfo;
import com.download.enti.filedownload;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadTask
{
	private filedownload fileinfo;
	private Context mContext;
	private ThreadDAO mThreadDAO;
	private int finished = 0;
	public boolean isPause = false;

	public DownloadTask(filedownload fileinfo, Context mContext)
	{
		this.fileinfo = fileinfo;
		this.mContext = mContext;
		mThreadDAO = new ThreadDAOImpl(mContext);
	}

	public void download()
	{
		List<Threadinfo> threadinfos = mThreadDAO.queryThread(fileinfo.getUrl());
		Threadinfo info = null;
		if (threadinfos.size() == 0)
		{
			info = new Threadinfo(0, fileinfo.getUrl(), 0, fileinfo.getLength(), 0);
		} else
		{
			info = threadinfos.get(0);
		}
		new DownloadThread(info).start();
	}

	//开启线程
	class DownloadThread extends Thread
	{
		private Threadinfo mThreadinfo;

		public DownloadThread(Threadinfo mThreadinfo)
		{
			this.mThreadinfo = mThreadinfo;
		}

		@Override
		public void run()
		{
			// 向数据库插入线程信息
			// 设置下载位置
			// 设置文件写入位置
			// 开始下载
			if (!mThreadDAO.isExists(mThreadinfo.getUrl(), mThreadinfo.getId()))
			{
				mThreadDAO.insertThread(mThreadinfo);
			}
			HttpURLConnection conn = null;
			RandomAccessFile raf = null;
			InputStream inputStream = null;
			try
			{
				URL url = new URL(mThreadinfo.getUrl());
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(3000);
				conn.setRequestMethod("GET");
				//
				int start = mThreadinfo.getFinished() + mThreadinfo.getStart();
				conn.setRequestProperty("Range", "bytes=" + start + "-" + mThreadinfo.getEnd());
				File file = new File(Myservice.FILENAME, fileinfo.getFilename());
				raf = new RandomAccessFile(file, "rwd");
				//发送广播
				Intent intent = new Intent(Myservice.UPDATE_SERVICE);
				raf.seek(start);

				finished += mThreadinfo.getFinished();

				if (conn.getResponseCode() == HttpStatus.SC_PARTIAL_CONTENT)
				{
					Log.d("Code", "" + conn.getResponseCode());
					inputStream = conn.getInputStream();
					byte[] bytes = new byte[1024 * 4];
					int length = -1;

					while ((length = inputStream.read(bytes)) != -1)
					{
						raf.write(bytes, 0, length);
						finished += length;
						intent.putExtra("finished", finished * 100 / fileinfo.getLength());
						//发送广播
						mContext.sendBroadcast(intent);

						if (isPause)
						{
							mThreadDAO.updateThread(mThreadinfo.getUrl(), mThreadinfo.getId(), finished);
							return;
						}
					}
					mThreadDAO.deleteThread(mThreadinfo.getUrl(), mThreadinfo.getId());
				}

			} catch (Exception e)
			{

				e.printStackTrace();
			} finally
			{
				try
				{
					raf.close();
					inputStream.close();
					conn.disconnect();
				} catch (IOException e)
				{

					e.printStackTrace();
				}

			}
		}

	}
}
