package com.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import android.database.sqlite.SQLiteOpenHelper;

public class Mydb extends SQLiteOpenHelper
{
	public static final String CREATE_TABLE = "Create Table Thread_info(_id integer primary key autoincrement,"
			+ "thread_id integer,url text,start integer,end integer,finished integer)";

	public static final String DROP_TABLE = "Drop Table if exists Thread_info";

	public Mydb(Context context)
	{
		super(context, "dbname", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL(DROP_TABLE);
		db.execSQL(CREATE_TABLE);

	}

}
