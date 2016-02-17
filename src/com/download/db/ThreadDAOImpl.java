package com.download.db;

import java.util.ArrayList;
import java.util.List;

import com.download.enti.Threadinfo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ThreadDAOImpl implements ThreadDAO
{
	private Mydb mdb;
	private ContentValues values = null;

	public ThreadDAOImpl(Context context)
	{
		mdb = new Mydb(context);
		values = new ContentValues();
	}

	@Override
	public void insertThread(Threadinfo threadinfo)
	{
		// 在调用getWritableDatabase()是，再去调用SQLiteDatabase的onCreate()方法
		SQLiteDatabase Database = mdb.getWritableDatabase();
		// 添加数据的方法一

		values.put("thread_id", threadinfo.getId());
		values.put("url", threadinfo.getUrl());
		values.put("start", threadinfo.getStart());
		values.put("end", threadinfo.getEnd());
		values.put("finished", threadinfo.getFinished());
		Database.insert("thread_info", null, values);
		// 添加数据的方法二
		// Database.execSQL("insert into Thread_info(thread_id,url ,start ,end
		// ,finished)values(?,?,?,?,?)", new Object[]
		// { threadinfo.getId(), threadinfo.getUrl(), threadinfo.getStart(),
		// threadinfo.getEnd(),
		// threadinfo.getFinished() });
		values.clear();
		Database.close();

	}

	@Override
	public void deleteThread(String url, int thread_id)
	{
		SQLiteDatabase Database = mdb.getWritableDatabase();

		// 方式一

		Database.delete("Thread_info", "url= ? and thread_id= ?", new String[]
		{ url, String.valueOf(thread_id) });
		Database.close();
		// 方式二
		// Database.execSQL("delete from Thread_info where url= ? and thread_id=
		// ?", new Object[]
		// { url, thread_id });
		// Database.close();
	}

	@Override
	public void updateThread(String url, int thread_id, int finished)
	{
		SQLiteDatabase Database = mdb.getWritableDatabase();
		// 方式一

		 values.put("finished", finished);
		 Database.update("thread_info", values, "thread_id= ? and url= ?", new
		 String[]
		 { String.valueOf(thread_id), String.valueOf(finished) });
		 Database.close();
		 values.clear();

		// 方式二
		// Database.execSQL("update thread_info set finished= ? where thread_id=
		// ? and url= ?", new Object[]
		// { finished, thread_id, url });
		// Database.close();
	}

	@Override
	public List<Threadinfo> queryThread(String url)
	{
		// 方式一
		SQLiteDatabase Database = mdb.getWritableDatabase();
		List<Threadinfo> list = new ArrayList<Threadinfo>();
		Cursor cursor = Database.query("thread_info", null, "url= ?", new String[]
		{ url }, null, null, null);
		while (cursor.moveToNext())
		{
			Threadinfo mThreadinfo = new Threadinfo();
			mThreadinfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
			mThreadinfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
			mThreadinfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
			mThreadinfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
			mThreadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
			list.add(mThreadinfo);
		}
		Database.close();
		cursor.close();
		// 方式二
		// SQLiteDatabase Database = mdb.getWritableDatabase();
		// List<Threadinfo> list = new ArrayList<Threadinfo>();
		// Cursor cursor = Database.rawQuery("select * from thread_info where
		// url= ?", new String[]
		// { url });
		// while (cursor.moveToNext())
		// {
		// Threadinfo threadinfo = new Threadinfo();
		// threadinfo.setId(cursor.getInt(cursor.getColumnIndex("thread_id")));
		// threadinfo.setUrl(cursor.getString(cursor.getColumnIndex("url")));
		// threadinfo.setStart(cursor.getInt(cursor.getColumnIndex("start")));
		// threadinfo.setEnd(cursor.getInt(cursor.getColumnIndex("end")));
		// threadinfo.setFinished(cursor.getInt(cursor.getColumnIndex("finished")));
		// list.add(threadinfo);
		// }
		//
		// Database.close();
		// cursor.close();
		return list;
	}

	@Override
	public boolean isExists(String url, int thread_id)
	{
		SQLiteDatabase Database = mdb.getWritableDatabase();

		Cursor cursor = Database.rawQuery("select * from thread_info where url= ? and thread_id= ?", new String[]
		{ url, String.valueOf(thread_id) });
		boolean exists = cursor.moveToNext();
		Database.close();
		cursor.close();
		return exists;
	}

}
