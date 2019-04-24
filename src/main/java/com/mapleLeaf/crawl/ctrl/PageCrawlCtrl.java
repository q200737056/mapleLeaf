package com.mapleLeaf.crawl.ctrl;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mapleLeaf.common.bean.AjaxResult;
import com.mapleLeaf.crawl.bean.PageParamter;
import com.mapleLeaf.crawl.service.PageCrawlService;

@Controller
@RequestMapping("/pageCrawl")
public class PageCrawlCtrl {
	@Autowired
	PageCrawlService pageCrawlService;
	
	@RequestMapping(value = "/startCrab")
	@ResponseBody
	public AjaxResult<String> updateConfig(String rootpath,String urls,String cookie,String useragent){
		
		AjaxResult<String> rst = new AjaxResult<>();
		String[] urlarr = urls.trim().split("\r|\n");
		String urlStr="";
		for(String url:urlarr){
			if(url.indexOf("http://")>-1
					|| url.indexOf("https://")>-1){
				urlStr+=url.trim()+"|";
			}
		}
		PageParamter pageParam = new PageParamter(cookie.trim(), useragent.trim(), rootpath.trim());
		
		pageCrawlService.startCrawl(urlStr.split("\\|"), pageParam);
		rst.setCode("0");
		
		return rst;
	}
}
