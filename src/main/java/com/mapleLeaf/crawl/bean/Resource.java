package com.mapleLeaf.crawl.bean;


/**
 * 某个资源对象
 * @author 
 */
public class Resource {
	private String netUrl;		//资源在网络上的绝对路径，绝对地址Url
	//private String originalUrl;	//原始url，资源在当前html中被引用的url =netUrl
	private String localUrl;	//存到本地磁盘绝对的地址 ，包含文件名
	
	private String dir;
	
	private String localFile;		//保存到本地的文件文件名，格式如 a.jpg 
	//private String localRelativePath;	//存储在本地的相对路径，如 images/   而html的，则为空字符串
	
	private int result;//处理结果  1成功 0失败
	
	
	public Resource() {
		
	}
	
	/**
	 * 传入绝对路径
	 * @param url 网络资源的绝对路径
	 * @param referrerUrl 来源的URL，即上级url，使用此资源的url,只用来获取Protocol
	 * @param rootPath 本地保存的根路径
	 */
	/*public Resource(String url, String referrerUrl, String rootPath) {
		if(url.length() < 3 || url.length() > 500){
			return;
		}
		
		if(url.indexOf("//") == 0){
			//如果这个url是 //www.baidu.com这样的网址，是以//开头的，那么补全协议
			System.out.println("补齐协议。原路径："+url);
			url = UrlUtil.getProtocols(referrerUrl)+":"+url;
		}
		//如果url中包含空格，要将其变为 url 编码
		if(url.indexOf(" ") > 0){
			url = url.replaceAll(" ", "%20");
		}
		
		this.netUrl = url;
		//this.originalUrl = url;
		createLocalFile(rootPath);//创建本地储存文件
		
		System.out.println("地址："+this.netUrl);
	}*/
	
	
	/**
	 * @param refererPagePath 来源页面的路径所在。如 www.baidu.com/a/b/c.html 那么这里便是 www.baidu.com/a/b/ 此项的作用，是当 originalUrl 是相对路径时，进行自动补齐其路径
	 */
	/*public Resource(String baseUri, String originalUrl) {
		this.originalUrl = originalUrl;
		if(baseUri.lastIndexOf("/") < baseUri.length() - 1){
			this.baseUri = StringUtil.subString(baseUri, "/", null, 3);
		}else{
			this.baseUri = baseUri;
		}
		
		//原始url
		if(originalUrl == null || originalUrl.length() < 2){
			return;
		}
		
		//资源在网络的绝对地址
		if(originalUrl.indexOf("http://") == 0 || originalUrl.indexOf("https://") == 0 || originalUrl.indexOf("//") == 0){
			//传入的是绝对url
			this.netUrl = originalUrl;
		}else{
			//传入的是相对路径，计算出绝对路径。
			if(originalUrl.indexOf("./") == 0){
				//是资源文件饮用
				this.netUrl = this.baseUri + originalUrl.substring(2, originalUrl.length());
			}else if (originalUrl.indexOf("../") == 0) {
				//上级文件，针对此项目，那应该就是html文件了
//				this.netUrl = Lang.subString(this.baseUri.substring(0, this.baseUri.length() - 1), "/", null, 3) + originalUrl.substring(3, originalUrl.length());
				//需要找baseUri的上一级目录
				this.netUrl = this.baseUri.substring(0, this.baseUri.substring(0, this.baseUri.length()-1).lastIndexOf("/")+1) + originalUrl.substring(3, originalUrl.length());
			}else if (originalUrl.indexOf("/") == 0) {
				//是根目录
				System.out.println("根目录");
			}else{
				//什么也没有，直接跟在后面即可
				this.netUrl = this.baseUri + originalUrl;
			}
		}
		
		createLocalFile(); //创建本地储存文件
	}*/
	
	/**
	 * 根据this.netUrl 网上的绝对路径，来计算存到本地的路径
	 */
	/*private void createLocalFile(String rootPath){
		

		if(this.netUrl.lastIndexOf("/") < 5){
			System.out.println("debug netUrl.lastIndexOf < 5, netUrl:"+this.netUrl);
			return;
		}else{
			//文件名
			this.localFile = UrlUtil.getFileName(this.netUrl);

		}

		//判断资源类型，是html、css、js、image
		//获取资源的后缀名＋后面？的一堆
		String suffix = StrUtil.subString(this.localFile, ".", null, 4);//获取文件后缀
		//图片文件后缀
		String[] imgs = {"jpg","jpeg","gif","png","bmp","ico"};
		String type = null;
		for (int i = 0; i < imgs.length; i++) {
			if(suffix.equalsIgnoreCase(imgs[i])){
				type = "image";
				break;
			}
		}
		if(type == null){
			//判断是否是js后缀
			if(suffix.equalsIgnoreCase("js")){
				type = "js";
			}
		}
		if(type == null){
			//判断是否是css后缀
			String[] csss = {"css","woff2","woff","eot","ttf","otf","svg"};
			for (int i = 0; i < csss.length; i++) {
				if(suffix.equalsIgnoreCase(csss[i])){
					type = "css";
					break;
				}
			}
		}
		if(type == null){
			//判断是否是html文件
			String[] htmls = {"htm","html","shtml","jsp","action","do","asp","aspx","php"};
			for (int i = 0; i < htmls.length; i++) {
				if(suffix.equalsIgnoreCase(htmls[i])){
					type = "html";
					suffix = "html";	//后缀名变为html
					this.localFile.replace(htmls[i], "html");	//文件后缀改为html
					break;
				}
			}
		}
		if(type == null){
			System.out.println("未发现什么类型。后缀:"+suffix+",--"+netUrl);
			this.localUrl = rootPath;
			this.localRelativePath = "/";
		}else{
			switch (type) {
			case "image":
				this.localUrl = rootPath+"images/";
				this.localRelativePath = "./images/";
				break;
			case "js":
				this.localUrl = rootPath+"js/";
				this.localRelativePath = "./js/";
				break;
			case "css":
				this.localUrl = rootPath+"css/";
				this.localRelativePath = "./css/";
				break;
			case "html":
				this.localUrl = rootPath;
				this.localRelativePath = "/";
				break;
			default:
				this.localUrl = rootPath;
				this.localRelativePath = "/";
				break;
			}
		}

		//判断磁盘上这个文件是否存在
		if(FileUtil.exists(this.localUrl + localFile)){
			//已经存在了，那么要重新命名，不能覆盖
			this.localFile = StringUtil.subString(this.localFile, null, ".")+"_"+StringUtil.uuid()+"."+suffix;
		}
		
		this.localUrl = this.localUrl + this.localFile;
	}*/
	
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

	/*public String getOriginalUrl() {
		return originalUrl;
	}*/
	
	
}
