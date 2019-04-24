package com.mapleLeaf.crawl.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mapleLeaf.common.util.GlobalConst;
import com.mapleLeaf.common.util.StrUtil;
import com.mapleLeaf.crawl.bean.PageParamter;
import com.mapleLeaf.crawl.bean.Resource;

import com.mapleLeaf.crawl.other.ResouceFactory;
import com.mapleLeaf.crawl.utils.FileUtil;
import com.mapleLeaf.crawl.utils.UrlUtil;



/**
 * 爬取抓取某个指定的页面
 * @author
 */
@Service
public class PageCrawlService {
	private Logger log =  LoggerFactory.getLogger(PageCrawlService.class);
	
	public void startCrawl(String[] curls,PageParamter param){
		
		//初始化缓存map
		if(Cache.cacheMap.get()==null){
			Map<String, Resource> map = new HashMap<>();
			Cache.cacheMap.set(map);
		}
		
		String rootPath = param.getLocalRootPath();
		
		
		System.out.println("扒取开始");
		for (int i = 0; i < curls.length; i++) {
			if(curls[i].length() > 6){
				
				String domain = UrlUtil.getDomain(curls[i]);//带端口的域名
				param.setDomain(domain);
				String protocol =UrlUtil.getProtocols(curls[i]);//协议
				param.setProtocol(protocol);
				
				String tmp = domain.split(":",-1)[0];//去掉端口,做成文件夹
				
				//创建文件夹,不管域名相不相同，每个抓取页面，都会创建独立一个文件夹，防止所有页面资源都放在一起
				String p = rootPath+File.separator+tmp+File.separator+(i+1)+File.separator;
				param.setLocalRootPath(p);
				FileUtil.createCacheFile(p);
				
				execute(curls[i],param);
				//抓取一个页面后 ，清空缓存，防止之后的页面中有资源被缓存，而不去下载
				Cache.cacheMap.get().clear();
			}
		}
		System.out.println("扒取完毕");
		Cache.cacheMap.get().clear();
		Cache.cacheMap.set(null);
	}
	
	private void execute(String curl,PageParamter param) {
		
		System.out.println("开始抓取："+curl);
		try {

			Map<String, String> headersMap = new HashMap<String, String>();
			
			if(!StringUtils.isBlank(param.getCookie())){
				headersMap.put("Cookie", param.getCookie());
			}
			if(!StringUtils.isBlank(param.getUserAgent())){
				headersMap.put("User-Agent",param.getUserAgent());
			}else{
				headersMap.put("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) "
						+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36");
			}
			
			
			Document doc = Jsoup.connect(curl).headers(headersMap).get();
			String en = UrlUtil.getCharset(doc);//获取 页面编码
			if(en != null && en.equalsIgnoreCase("UTF-8")){
				
				param.setCharset("UTF-8");
			}else{
				
				param.setCharset(en);
			}
			System.out.println("自动提取页面编码："+param.getCharset());
			
			String html = HandlHtml(curl,doc,param);
			
			//当前html文件名字
			int start = (param.getProtocol()+"://"+param.getDomain()).length();
			int end = curl.indexOf("?")>0?curl.indexOf("?"):curl.length();
			
			String htmlName = curl.substring(start,end);
			if("".equals(htmlName)||"/".equals(htmlName)){
				htmlName = "_index";
			}else{
				htmlName = htmlName.substring(htmlName.lastIndexOf("/")+1,htmlName.length());
				htmlName = UrlUtil.getFileBeforeName(htmlName);//如果有后缀，去掉后缀
			}
					
			
			
			String dynamicParam = "";	
			if(curl.indexOf("?") > 0){
			
				dynamicParam = curl.substring(curl.indexOf("?")+1,curl.length());
			}
			
			//将此变为html后缀的页面进行保存
			htmlName = htmlName+ (dynamicParam.length() > 0 ? "__"+dynamicParam:"") +".html";
			
			try {
				FileUtil.write(param.getLocalRootPath()+htmlName, html, param.getCharset());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage()+" -- "+curl);
			e.printStackTrace();
		}
		
		
	}

	/**
	 * 针对html文件，替换其源代码，将资源引用的相对路径改为绝对路径
	 * @throws IOException 
	 */
	private String HandlHtml(String curl,Document doc,PageParamter param){
		
		//取CSS
		Elements cssEles = doc.getElementsByTag("link");
		System.out.println("开始提取CSS:"+cssEles.size());
		for (int i = 0; i < cssEles.size(); i++) {
			Element ele = cssEles.get(i);
			//过滤掉 预加载解析的
			String rel = ele.attr("rel");
			if(!StringUtils.isBlank(rel)&&!rel.trim().equalsIgnoreCase("stylesheet")){
				continue;
			}
			String url = ele.attr("abs:href");
			if(url.length() > 3){
				//找到地址了，将其下载
				//Resource res = new Resource(url, curl,param.getLocalRootPath());
				Resource res = ResouceFactory.createResouce(url, param);
				
				if(res == null){
					continue;
				}
				//判断是否已经下载过
				if(Cache.cacheMap.get().get(res.getNetUrl()) == null){
					//未下载，则去下载
					String cssText = UrlUtil.getContent(res.getNetUrl(),param.getCharset());
					if(cssText != null){
						
						Cache.cacheMap.get().put(res.getNetUrl(), res);
						cssText = replaceContent(cssText, res.getNetUrl(), "../",param);//图片路径
						try {
							FileUtil.write(res.getLocalUrl(), cssText, param.getCharset());
						} catch (IOException e) {
							e.printStackTrace();
						}
					}else{
						try {
							FileUtil.write(res.getLocalUrl(), "/* not find url */", param.getCharset());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}else{
					res = Cache.cacheMap.get().get(res.getNetUrl());
				}
				
				//替换css文件路径
				ele.attr("href", "./css/"+res.getLocalFile());
			}
		}
		
		
		//取JS
		Elements jsEles = doc.getElementsByTag("script");
		System.out.println("开始提取JS:"+jsEles.size());
		for (int i = 0; i < jsEles.size(); i++) {
			Element ele = jsEles.get(i);
			String url = ele.attr("abs:src");
			if(url.length() > 3){
				//找到地址了，将其下载
				//Resource res = new Resource(url, curl, param.getLocalRootPath());
				Resource res = ResouceFactory.createResouce(url, param);
				if(res==null){
					continue;
				}
				res = Cache.addCache(res);
				if(res.getResult() == GlobalConst.SUCCESS){
					//替换标签
					ele.attr("src", "./"+res.getDir()+"/"+res.getLocalFile());
				}
			}
		}
		
		
		//取img
		Elements imgEles = doc.getElementsByTag("img");
		
		System.out.println("开始提取IMG:"+imgEles.size());
		for (int i = 0; i < imgEles.size(); i++) {
			Element ele = imgEles.get(i);
			String url = ele.attr("abs:src");
			if(url.length() > 3){
				//找到地址了，将其下载
				//Resource res = new Resource(url, curl, param.getLocalRootPath());
				Resource res = ResouceFactory.createResouce(url, param);
				if(res == null){
					continue;
				}
				res = Cache.addCache(res);
				if(res.getResult()==GlobalConst.SUCCESS){
					//替换标签
					ele.attr("src", "./"+res.getDir()+"/"+res.getLocalFile());
				}
			}
		}
		// 页面中的样式
		/*System.out.println("开始提取页面中的style");
		Elements styEles = doc.getElementsByTag("style");
		for (int i = 0; i < styEles.size(); i++) {
			Element ele = styEles.get(i);
			String styContent =  ele.html();
			if(!StringUtils.isBlank(styContent)){
				
				styContent = replaceContent(styContent, curl, "./images/");
				ele.html(styContent);
				
			}
		}*/
		
		
		//去掉base标签
		Elements baseEles = doc.getElementsByTag("base");
		if(baseEles != null && baseEles.size() > 0){
			for (int i = 0; i < baseEles.size(); i++) {
				baseEles.get(i).remove();
			}
		}
		
		//替换网页本身内写的css相关引用
		String html = replaceContent(doc.toString(), curl, "./",param);

		
		return html;
	}
	
	
	/**
	 * 替换地址
	 * @param cssText
	 * @param thisUrl
	 * @param relativePath  相对路径  ./ 本级目录下  ../ 上级目录下
	 * @return
	 */
	
	private  String replaceContent(String content, String url, String relativePath,PageParamter param){
	
		Pattern pattern = Pattern.compile("url\\('?\"?(.*?)'?\"?\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String src = matcher.group(1);	//src的地址
			src=src.replaceAll("\\\\/", "/");
			
			/*System.out.println("获取到的地址："+src);
			if(src != null && src.length() > 2){
				String srcUrl = "";
				if(UrlUtil.isAbsoluteUrl(src)){
					srcUrl=src;
				}else{
					if(src.indexOf("/") == 0){
						srcUrl=param.getProtocol()+":"+param.getDomain()+src;
					}else{
						while(src.indexOf("./") == 0 || src.indexOf("../") == 0){
							if(src.indexOf("./") == 0){
								//过滤前面的./
								src = src.substring(2, src.length());
							}else if (src.indexOf("../") == 0) {
								//过滤前面的../
								src = src.substring(3, src.length());
								
							}
						}
						srcUrl=param.getProtocol()+":"+param.getDomain()+"/"+src;
					}
					
					srcUrl=param.getProtocol()+":"+param.getDomain()+"/"+src;
				}*/
				
				
				if(src.indexOf(GlobalConst.CACHE_STRING) == -1){//排除已下载过的资源路径
					//Resource res = new Resource(srcUrl, url, param.getLocalRootPath());
					Resource res = ResouceFactory.createResouce(src, param);
					if(res == null){
						continue;
					}
					res = Cache.addCache(res);//没下载过 ，就下载
					if(res.getResult() == GlobalConst.SUCCESS){
						//将其进行替换为相对路径
						content = StrUtil.replaceAll(content, src, 
								GlobalConst.CACHE_STRING+relativePath+res.getDir()+"/"
								+res.getLocalFile());
					}
				}
			
		}
		
		//将 _*CACHE*_ 去除
		//content = StrUtil.replaceAll(content, GlobalConst.CACHE_STRING, "");
		content = content.replace(GlobalConst.CACHE_STRING, "");
		return content;
	}

	
	
	public static void main(String[] args) {
		/*String content="#head{padding-bottom:100px;text-align:center;*z-index:1}#ftCon{height:50px;"
				+ "position:absolute;bottom:47px;text-align:left;width:100%;margin:0 auto;z-index:0;"
				+ "overflow:hidden}.ftCon-Wrapper{overflow:hidden;margin:0 auto;text-align:center;*"
				+ "width:640px}#qrcode .qrcode-item-1 .qrcode-img{background:"
				+ "url(https://ss1.bdstatic.com/5eN1bjq8AAUYm2zgoY3K/r/www/cache"
				+ "/static/protocol/https/home/img/qrcode/zbios_efde696.png) 0 0 no-repeat}#qrcode "
				+ ".qrcode-item-2 .qrcode-img{background:url(https://ss1.bdstatic.com/5eN1bjq8AAUYm2z"
				+ "goY3K/r/www/cache/static/protocol/https/home/img/qrcode/nuomi_365eabd.png) 0 0 no-repeat}"
				;
		Pattern pattern = Pattern.compile("url\\('?\"?(.*?)'?\"?\\)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String src = matcher.group(1);	//src的地址
			System.out.println(src);
		}*/
		
	}
}
