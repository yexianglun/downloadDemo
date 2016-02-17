package com.download.db;

import java.util.List;

import com.download.enti.Threadinfo;

public interface ThreadDAO
{
	public void insertThread(Threadinfo threadinfo);
	
	public void deleteThread(String url,int thread_id);
	
	public void updateThread(String url,int thread_id,int finished);
	
	public List<Threadinfo> queryThread(String url);
	
	public boolean isExists(String url,int thread_id);
}
