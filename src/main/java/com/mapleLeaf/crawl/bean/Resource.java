package com.mapleLeaf.crawl.bean;

/**
 * 某个资源对象
 * @author 
 */
public class Resource {
	private String netUrl;		//资源在网络上的绝对路径，绝对地址Url

	private String localUrl;	//存到本地磁盘绝对的地址 ，包含文件名
	
	private String dir;
	
	private String localFile;		//保存到本地的文件文件名，格式如 a.jpg 
	//private String localRelativePath;	//存储在本地的相对路径，如 images/   而html的，则为空字符串
	
	private int result;//处理结果  1成功 0失败
	
	
	public String getNetUrl() {
		return netUrl;
	}
	public void setNetUrl(String netUrl) {
		this.netUrl = netUrl;
	}
	public String getLocalUrl() {
		return localUrl;
	}
	public void setLocalUrl(String localUrl) {
		this.localUrl = localUrl;
	}

	public String getLocalFile() {
		return localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public int getResult() {
		return result;
	}

	public void setResult(int result) {
		this.result = result;
	}

	
	
}
