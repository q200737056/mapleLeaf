package com.mapleLeaf.crawl.other;

import com.mapleLeaf.crawl.bean.PageParamter;
import com.mapleLeaf.crawl.service.PageCrawlService;

public class Test {

	public static void main(String[] args) {
		
		
		final String[] urls = {"https://www.guazi.com/hz/?ca_s=dh_jinshanmz&ca_n=jskz&scode=10104337812"};
		
		
		
		PageParamter param = new PageParamter("", "", "f:/test/");
		new PageCrawlService().startCrawl(urls,param);
	}

}
