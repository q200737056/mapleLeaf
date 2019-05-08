package com.mapleLeaf.crawl.other;

import org.apache.commons.lang.StringUtils;

import com.mapleLeaf.common.util.StrUtil;
import com.mapleLeaf.crawl.bean.PageParamter;
import com.mapleLeaf.crawl.bean.Resource;
import com.mapleLeaf.crawl.utils.UrlUtil;

public class ResouceFactory {
	public static Resource createResouce(String url,PageParamter param){
		System.out.println("原地址："+url);
		
		if(url.indexOf("+")!=-1){//不是有效地址，拼接的，暂不处理
			return null;
		}
		
		String srcUrl = UrlUtil.getFullPath(url, param);
		
		//System.out.println("绝对地址："+srcUrl);
		if(srcUrl.length() > 500){
			return null;
		}
		
		
		
		String filename="";
		String localUrl="";
		String dir="";
		//文件名
		filename = UrlUtil.getFileName(srcUrl);
		if(StringUtils.isBlank(filename)){
			return null;
		}
		
		//判断资源类型，是html、css、js、image
		//获取资源的后缀名＋后面？的一堆
		String suffix = StrUtil.subString(filename, ".", null, 4);//获取文件后缀
		
		if(suffix.equals(filename)){//不是文件
			return null;
		}
		
		//图片文件后缀
		String[] imgs = {"jpg","jpeg","gif","png","bmp","ico","cur"};
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
			if(suffix.equalsIgnoreCase("css")){
				type = "css";
			}
		}
		if(type == null){
			//判断是否是css后缀
			String[] fonts = {"woff2","woff","eot","ttf","otf","svg"};
			for (int i = 0; i < fonts.length; i++) {
				if(suffix.equalsIgnoreCase(fonts[i])){
					type = "font";
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
					filename.replace(htmls[i], "html");	//文件后缀改为html
					break;
				}
			}
		}
		if(type == null){
			System.err.println("未发现什么类型。后缀:"+suffix+",--"+srcUrl);
			
		}else{
			switch (type) {
			case "image":
				localUrl = param.getLocalRootPath()+"images/";
				dir="images";
				break;
			case "js":
				localUrl = param.getLocalRootPath()+"js/";
				dir="js";
				break;
			case "css":
				localUrl = param.getLocalRootPath()+"css/";
				dir="css";
				break;
			case "font":
				localUrl = param.getLocalRootPath()+"fonts/";
				dir="fonts";
				break;
			case "html":
				localUrl = param.getLocalRootPath();
				
				break;
			default:
				localUrl =param.getLocalRootPath();
				break;
			}
		}

		//判断磁盘上这个文件是否存在
		/*if(FileUtil.exists(this.localUrl + localFile)){
			//已经存在了，那么要重新命名，不能覆盖
			this.localFile = StringUtil.subString(this.localFile, null, ".")+"_"+StringUtil.uuid()+"."+suffix;
		}*/
		
		Resource r = new Resource();
		
		r.setNetUrl(srcUrl);
		r.setLocalUrl(localUrl+filename);
		r.setLocalFile(filename);
		r.setDir(dir);
		
		return r;
	}
}
