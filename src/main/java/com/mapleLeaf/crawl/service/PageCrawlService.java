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
		
		
		log.info("扒取开始");
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
		log.info("扒取完毕");
		Cache.cacheMap.get().clear();
		Cache.cacheMap.set(null);
	}
	
	private void execute(String curl,PageParamter param) {
		
		log.info("开始抓取："+curl);
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
			log.info("自动提取页面编码："+param.getCharset());
			
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
			log.info(e.getMessage()+" -- "+curl);
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
		log.info("开始提取CSS");
		for (int i = 0; i < cssEles.size(); i++) {
			Element ele = cssEles.get(i);
			//过滤掉 预加载解析的
			String rel = ele.attr("rel");
			if(!StringUtils.isBlank(rel)&&!rel.trim().equalsIgnoreCase("stylesheet")){
				if(rel.trim().equalsIgnoreCase("icon")||
						rel.trim().equalsIgnoreCase("shortcut icon")){//图标
					String url = ele.attr("abs:href");
					if(url.length() > 3){
						
						this.drawElement(ele, url, param,"href");
					}
				}
				continue;
			}
			
			String url = ele.attr("abs:href");
			if(url.length() > 3){
				//一种特殊情况 地址后??跟多个css文件，中间英文逗号分隔
				if(url.indexOf("??")!=-1){
					String[] tmpArr = url.split("\\?\\?");
					String[] cssPaths = tmpArr[1].split(",");
					for(int j=0;j<cssPaths.length;j++){
						String cssPath = tmpArr[0]+cssPaths[j].trim();
						//创建新的 link
						Element newEle = new Element("link");
						newEle.attr("rel", "stylesheet");
						
						ele.after(newEle);
						
						this.drawCss(newEle, null,cssPath, param);
					}
					//移除原来的link
					ele.remove();;
				}else{
					//下载 css
					this.drawCss(ele, null,url, param);
				}
				
			}
		}
		
		//取JS
		Elements jsEles = doc.getElementsByTag("script");
		log.info("开始提取JS");
		for (int i = 0; i < jsEles.size(); i++) {
			Element ele = jsEles.get(i);
			String url = ele.attr("abs:src");
			if(url.length() > 3){
				
				this.drawJs(ele, null,url, param);
				
			}else{//没有src ，页面脚本
				
				String jsContent = ele.html().trim();
				
				if(!StringUtils.isBlank(jsContent)){
					// '//'开头的地址加上 协议
					jsContent=UrlUtil.convertRawPath(param.getProtocol(), jsContent);
					
					//找出 js ,css，动态加载
					/*
					Pattern pattern = Pattern.compile(param.getProtocol()+"://.{1,500}?\\.(js|css)");
					Matcher matcher = pattern.matcher(jsContent);
					while (matcher.find()) {
						String src = matcher.group();
						//过滤不合格地址
						if(src.lastIndexOf("http://")>0||
								src.lastIndexOf("https://")>0){
							continue;
						}
						
						if(src.endsWith(".js")){
							jsContent=this.drawJs(ele,jsContent,src, param);
						}else if(src.endsWith(".css")){
							jsContent=this.drawCss(ele,jsContent,src, param);
						}
					}*/
					
					ele.html(jsContent);
				}
				
			}
		}
		
		
		//取img
		Elements imgEles = doc.getElementsByTag("img");
		
		log.info("开始提取IMG");
		for (int i = 0; i < imgEles.size(); i++) {
			Element ele = imgEles.get(i);
			String url = ele.attr("abs:src");
			if(url.length() > 3){
				
				this.drawElement(ele, url, param,"src");
				
			}
		}
		
		
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
	 * 扒取 css
	 * @param ele 标签
	 * @param content 标签内容(如果有的话)
	 * @param url 原始地址(标签中的src或标签内容中的一个地址)
	 * @param param
	 */
	private String drawCss(Element ele,String content,String url,PageParamter param){
		Resource res = ResouceFactory.createResouce(url, param);
		
		if(res == null){
			return "";
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
					FileUtil.write(res.getLocalUrl(), "/* not find url or connect timeout */", param.getCharset());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			res = Cache.cacheMap.get().get(res.getNetUrl());
		}
		
		if(StringUtils.isBlank(content)){//内容为空时
			//替换css文件路径
			ele.attr("href", "./"+res.getDir()+"/"+res.getLocalFile());
		}else{
			content.replace(url, "./"+res.getDir()+"/"+res.getLocalFile());
		}
		return content;
	}
	/**
	 * 扒取 js
	 * @param ele 标签
	 * @param content 标签内容(如果有的话)
	 * @param url 原始地址(标签中的src或标签内容中的一个地址)
	 * @param param
	 */
	private String drawJs(Element ele,String content,String url,PageParamter param){
		Resource res = ResouceFactory.createResouce(url, param);
		if(res==null){
			return "";
		}
		//判断是否已经下载过
		if(Cache.cacheMap.get().get(res.getNetUrl()) == null){
			
			//获取js文件 文本
			String jsText = UrlUtil.getContent(res.getNetUrl(),param.getCharset());
			
			if(!StringUtils.isBlank(jsText)){
				Cache.cacheMap.get().put(res.getNetUrl(), res);
				// '//'开头的地址加上 协议
				jsText=UrlUtil.convertRawPath(param.getProtocol(), jsText);
				
				//找出 js ,css，动态加载
				/*
				Pattern pattern = Pattern.compile(param.getProtocol()+"://.{1,500}?\\.(js|css)");
				Matcher matcher = pattern.matcher(jsText);
				while (matcher.find()) {
					String src = matcher.group();
					//过滤不合格地址
					if(src.lastIndexOf("http://")>0||
							src.lastIndexOf("https://")>0){
						continue;
					}
					
					if(src.endsWith(".js")){
						jsText=this.drawJs(ele,jsText,src, param);//递归
					}else if(src.endsWith(".css")){
						jsText=this.drawCss(ele,jsText,src, param);
					}
				}*/
				try {
					FileUtil.write(res.getLocalUrl(), jsText, param.getCharset());//下载
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else{
				try {
					FileUtil.write(res.getLocalUrl(), "/* not find url or connect timeout */", param.getCharset());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			
		}else{
			res = Cache.cacheMap.get().get(res.getNetUrl());
		}
		
		
		//res = Cache.addCache(res);
		//if(res.getResult() == GlobalConst.SUCCESS){
			
		if(StringUtils.isBlank(content)){//内容为空时
			//替换标签
			ele.attr("src", "./"+res.getDir()+"/"+res.getLocalFile());
		}else{
			content =content.replace(url, "./"+res.getDir()+"/"+res.getLocalFile());
		}
		//}
		return content;
	}
	/**
	 * 扒取页面中的特定元素 ，如图片
	 * @param ele
	 * @param url
	 * @param param
	 */
	private void drawElement(Element ele,String url,PageParamter param,String addr){
		Resource res = ResouceFactory.createResouce(url, param);
		if(res == null){
			return;
		}
		res = Cache.addCache(res);
		if(res.getResult()==GlobalConst.SUCCESS){
			//替换标签
			ele.attr(addr, "./"+res.getDir()+"/"+res.getLocalFile());
		}
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
			
				if(src.indexOf(GlobalConst.CACHE_STRING) == -1){//排除已下载过的资源路径
					
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
		String content="var fixReferer = '',"+
                "curReferer = document.referrer,"
                +"site = 'anjuke',"
                +"st = new SiteTracker();"
                +"st.setSite(site); st.setPage(\"Ershou_Web_Home_HomePage\");st.setPageName(\"Ershou_Web_Home_HomePage\");"
                +"st.setReferer(curReferer ? curReferer : fixReferer);"
                +"st.buildParams();var _trackUrl = st.getParams();"
                +"delete _trackUrl.sc; delete _trackUrl.cp;window._trackURL = JSON.stringify(_trackUrl);"
            	+"function loadTrackjs(){"
                +" var s = document.createElement('script');"
                 +"s.type = 'text/javascript'; s.async = true; s.src = '//tracklog.58.com/referrer_anjuke_pc.js?_=' + Math.random();";
		//Pattern pattern = Pattern.compile("('|\")\\s*//.+?('|\")");
		Pattern pattern = Pattern.compile("'\\s*//.+?'|\"\\s*//.+?\"");
		//Pattern pattern = Pattern.compile("https://.+?\\.(js|css)");
		Matcher matcher = pattern.matcher(content);
		while (matcher.find()) {
			String src = matcher.group();	//src的地址
			System.out.println(src);
		}
		
	}
}
