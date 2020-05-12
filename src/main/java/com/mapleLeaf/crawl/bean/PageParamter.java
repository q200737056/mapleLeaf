package com.mapleLeaf.crawl.bean;
/**
 * 扒取网页参数
 * @author Administrator
 *
 */
public class PageParamter {
	//k1=v1;k2=v2
	private String cookie;
	private String userAgent;
	//本地保存的路径
	private String localRootPath; 
	//页面编码,默认UTF-8
	private String charset; 
	//当前网址 域名
	private String domain; 
	//当前 网址 协议
	private String protocol;
	
	public PageParamter(String cookie, String userAgent, String localRootPath) {
		this.cookie = cookie;
		this.userAgent = userAgent;
		this.localRootPath = localRootPath;
		this.charset = "UTF-8";
	}
	public String getCookie() {
		return cookie;
	}
	public void setCookie(String cookie) {
		this.cookie = cookie;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	public String getLocalRootPath() {
		return localRootPath;
	}
	public void setLocalRootPath(String localRootPath) {
		this.localRootPath = localRootPath;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	
}
