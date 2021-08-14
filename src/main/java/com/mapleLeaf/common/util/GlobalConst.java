package com.mapleLeaf.common.util;

public class GlobalConst {
	//缓存特殊字符。只要缓存过的文件，替换时会加上这个。以免进行多次缓存
	public final static String CACHE_STRING = "_*CACHE*_";
	public final static int SUCCESS=1;
	public final static int FAILURE=0;
	public final static String TEMPLATE_PATH="template";
	public final static String CONFIG="config.xml";
	//表单类型
	public final static String[] TAG_TYPES={"select","text","date","checkbox","radio","textarea",
			"hidden","file","email","mobilphone","telphone","money","number","idcard"};
	//持久层类型
	public final static String[] PERSISTENCE={"mybatis","hibernate","other"};
}
