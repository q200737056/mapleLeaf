package com.mapleLeaf.crawl.utils;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mapleLeaf.crawl.bean.PageParamter;

/**
 * url网址相关操作
 * @author 
 */
public class UrlUtil {
	
	
	
	/**
	 * 判断url是否是绝对路径网址
	 * @param url 要判断的url
	 * @return true:是绝对路径的
	 */
	public static boolean isAbsoluteUrl(String url){
		if(url.indexOf("http://") > -1 || url.indexOf("https://") > -1 
				|| url.indexOf("//") > -1){
			return true;
		}
		return false;
	}
	
	/**
	 * 获取当前url地址所在的远程文件路径
	 * @param url 目标url地址，绝对路径，如 http://www.wscso.com/test/a.jsp
	 * @return url所在地址的路径，返回如 http://www.wscso.com/test/
	 */
	public static String getPath(String url){
		//排除问号及其后的字符串
		int wenhao = url.lastIndexOf("?");
		if(wenhao > -1){
			//发现问号的存在，那么将问号后面的字符删除掉
			url = url.substring(0, wenhao);
		}
		
		//排除 # 及其后的字符串
		int jinghao = url.lastIndexOf("#");
		if(jinghao > -1){
			//发现问号的存在，那么将问号后面的字符删除掉
			url = url.substring(0, jinghao);
		}
		
		
		int last = url.lastIndexOf("/");
		if(last > 8){
			url = url.substring(0, last+1);
		}
		
		return url;
	}
	
	/**
	 * 根据url地址，获取其访问的文件名字
	 * @param url 目标url，如 http://wang.market/images/a.jpg?a=123
	 * @return 返回访问的文件名，如以上url返回： a.jpg
	 */
	public static String getFileName(String url){
		
		//排除问号及其后的字符串
		int wenhao = url.indexOf("?");
		if(wenhao > -1){
			//发现问号的存在，那么将问号后面的字符删除掉
			url = url.substring(0, wenhao);
		}
		
		//排除 # 及其后的字符串
		int jinghao = url.indexOf("#");
		if(jinghao > -1){
			//发现问号的存在，那么将问号后面的字符删除掉
			url = url.substring(0, jinghao);
		}
		
		int last = url.lastIndexOf("/");
		if(last > 8){
			url = url.substring(last+1, url.length());

			return url;
		}else{
			//不是正常的url地址
			return "";
		}
	}
	
	

	/**
	 * 根据url地址，获取其访问的域名。若么有发现，返回空字符
	 * @param url 目标url，如 http://wang.market/images/a.jpg
	 * @return 返回wang.market
	 */
	public static String getDomain(String url){
		int start = url.indexOf("//");
		if(start > 0){
			url = url.substring(start+2, url.length());
			
			int end = url.indexOf("/");
			if(end > 0){
				url = url.substring(0, end);
			}
			return url;
		}
		
		return "";
	}
	
	/**
	 * 获取文件前缀名字。
	 * @param fileName 传入的文件名字。如传入 test.jpg ，则会返回不带后缀的名字： test
	 * @return 若传入null，则返回空字符串""
	 */
	public static String getFileBeforeName(String fileName){
		if(fileName == null){
			return "";
		}
		
		String htmlFile = "";
		if(fileName.indexOf(".") > 0){
			String s[] = fileName.split("\\.");
			htmlFile = s[0];
		}else{
			htmlFile = fileName;
		}
		return htmlFile;
	}
	
	/**
	 * 获取当前url的协议，返回如 http 、 https 、 ftp 、 file 等
	 * @param url 绝对路径，必须是全的，包含协议的，如 http://www.xnx3.com
	 * @return 自动截取的协议，如 http 。若取不到，则返回null
	 */
	public static String getProtocols(String url){
		if(url == null){
			return null;
		}
		
		if(url.indexOf("//") > 1){
			return url.substring(0, url.indexOf("//") - 1);
		}
		return null;
	}
	/**
	 * 若使用的utf-8编码，则统一返回 “UTF-8” ，若是其他编码，则会返回charset中设置的编码
	 * @param doc
	 * @return
	 */
	public static String getCharset(Document doc){
		String defaultEncode = "UTF-8";
		
		Elements es = doc.getElementsByTag("meta");
		for (int i = 0; i < es.size(); i++) {
			Element ele = es.get(i);
			String equiv = ele.attr("http-equiv");
			if(equiv != null && equiv.equalsIgnoreCase("content-type")){
				String content = ele.attr("content");
				if(content == null){
					return defaultEncode;
				}
				String cs[] = content.split(";");
				for (int j = 0; j < cs.length; j++) {
					String kvs[] = cs[j].split("=");
					if(kvs[0].trim().equalsIgnoreCase("charset")){
						return kvs[1].trim();
					}
				}
			}
		}
		return defaultEncode;
	}
	
	
	/**
	 * 获取网页源代码，自动根据url的协议获取
	 * @param url 绝对路径
	 * @return 若失败返回null，成功返回源代码
	 */
	public static String getContent(String url,String charset){
		if(url == null){
			return null;
		}
		
		//获取协议，是http，还是https
		String protocol = getProtocols(url);
		if(protocol == null){
			protocol = "http";
		}
		HttpResponse hr = null;
		if(protocol.equalsIgnoreCase("https")){
			HttpsUtil https = new HttpsUtil(charset);
			hr = https.get(url);
		}else{
			//只要不是https的，这里暂时都认为是http
			HttpUtil http = new HttpUtil(charset);
			hr = http.get(url);
		}
		
		if(hr == null || hr.getContent() == null){
			return null;
		}else{
			return hr.getContent();
		}
	}
	
	/**
	 * 将Map参数转变为 URL后的字符组合形势 
	 * @param parameters Map 
	 * @return key=value&key=value
	 */
	public static String mapToQueryString(Map<String,String> parameters){
    	String data = "";
    	StringBuffer param = new StringBuffer(); 
        if(parameters != null){
        	int i = 0; 
            for (Map.Entry<String, String> entry : parameters.entrySet()) {  
            	if (i > 0){
                	param.append("&");
                }
                param.append(entry.getKey()).append("=").append(entry.getValue()); 
                i++; 
            }
        }
        if(param.length()>0){
        	data = param.toString();
        }
        return data;
	}
	/**
	 * 将Map转换为URL的请求GET参数
	 * @param url URL路径，如：http://www.xnx3.com/test.php
	 * @param parameters	请求参数Map集合
	 * @return 完整的GET方式网址
	 */
	public static String mapToUrl(String url,Map<String, String> parameters){
		int i = 0; 
		if(url.indexOf("?") > 0){
			i = 1;
		}
		
		StringBuffer param = new StringBuffer(); 
        
        for (String key : parameters.keySet()) { 
            if (i == 0){
            	param.append("?");
            }else{
            	param.append("&");
            }
            param.append(key).append("=").append(parameters.get(key)); 
            i++; 
        } 
        url += param; 
        return url;
	}

	/**
	 * 
	 * @param url  原地址
	 * @param param
	 * @return  完整地址http开头，对原地址不全的 进行补全
	 */
	public static String getFullPath(String url,PageParamter param){
		String srcUrl = "";
		if(isAbsoluteUrl(url)){
			srcUrl=url;
			if(srcUrl.indexOf("//") == 0){
				srcUrl=param.getProtocol()+":"+url;
			}
		}else{
			if(url.indexOf("/") == 0){
				srcUrl=param.getProtocol()+":"+param.getDomain()+url;
			}else{
				while(url.indexOf("./") == 0 || url.indexOf("../") == 0){
					if(url.indexOf("./") == 0){
						//过滤前面的./
						url = url.substring(2, url.length());
					}else if (url.indexOf("../") == 0) {
						//过滤前面的../
						url = url.substring(3, url.length());
						
					}
				}
				srcUrl=param.getProtocol()+":"+param.getDomain()+"/"+url;
			}
			
			srcUrl=param.getProtocol()+":"+param.getDomain()+"/"+url;
		}
		srcUrl =srcUrl.trim();
		//如果url中包含空格，要将其变为 url 编码
		/*if(srcUrl.indexOf(" ") > 0){
			srcUrl = srcUrl.replaceAll(" ", "%20");
		}*/
		return srcUrl;
	}
	/**
	 * 把 //开头的地址 加上协议
	 * @param protocol 协议 
	 * @param content 标签的内容
	 * @return 替换后的内容
	 */
	public static String convertRawPath(String protocol,String content){
		//Pattern pattern2 = Pattern.compile("'\\s*//.+?'|\"\\s*//.+?\"");
		Pattern p = Pattern.compile("'//.+?'|\"//.+?\"");
		Matcher m = p.matcher(content);
		while (m.find()) {
			String src = m.group();
			//排除不是地址的
			if(src.indexOf("||")!=-1||
					src.indexOf("+")!=-1){
				continue;
			}
			System.out.println("js中需要补全的地址： "+src);
			
			//src = src.substring(1, src.length()-1).trim();//去掉 引号
			String f = src.substring(0, 1);//引号
			
			//引号包裹的一起替换
			content=content.replace(src, f+protocol+":"+src.substring(1, src.length()-1).trim()+f);
		}
		return content;
	}
}
