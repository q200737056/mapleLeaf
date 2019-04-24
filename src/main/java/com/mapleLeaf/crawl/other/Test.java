package com.mapleLeaf.crawl.other;

import com.mapleLeaf.crawl.bean.PageParamter;
import com.mapleLeaf.crawl.service.PageCrawlService;

public class Test {

	public static void main(String[] args) {
		
		
		final String[] urls = {"https://www.duba.com/?f=liebao"};
		
		
		
		PageParamter param = new PageParamter("", "", "f:/test/");
		new PageCrawlService().startCrawl(urls,param);
	}

}
