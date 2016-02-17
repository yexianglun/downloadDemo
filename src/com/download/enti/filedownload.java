package com.download.enti;

import java.io.Serializable;

public class filedownload implements Serializable
{
	private String url;
	private String filename;
	private int id;
	private int length;
	private int finished;
	
	public filedownload()
	{
	}
	public filedownload(String url, String filename, int id, int length, int finished)
	{
		this.url = url;
		this.filename = filename;
		this.id = id;
		this.length = length;
		this.finished = finished;
	}

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

	public String getFilename()
	{
		return filename;
	}

	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	public int getId()
	{
		return id;
	}

	public void setId(int id)
	{
		this.id = id;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}

	public int getFinished()
	{
		return finished;
	}

	public void setFinished(int finished)
	{
		this.finished = finished;
	}
	@Override
	public String toString()
	{
		return "filedownload [url=" + url + ", filename=" + filename + ", id=" + id + ", length=" + length
				+ ", finished=" + finished + "]";
	}
	

}
