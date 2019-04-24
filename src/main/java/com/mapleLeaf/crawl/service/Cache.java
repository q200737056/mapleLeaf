package com.mapleLeaf.crawl.service;

import java.io.FileNotFoundException;
import java.util.Map;

import com.mapleLeaf.common.util.GlobalConst;
import com.mapleLeaf.crawl.bean.Resource;
import com.mapleLeaf.crawl.utils.FileUtil;



/**
 * 
 * @author
 */
public class Cache {
	
	public static ThreadLocal<Map<String, Resource>> cacheMap = new ThreadLocal<Map<String, Resource>>();
	
	public static  Resource addCache(Resource resource){
		
		if(resource.getNetUrl() == null
			|| resource.getNetUrl().length() > 255){
			//无远程资源，或不正确，退出不缓存
			resource.setResult(GlobalConst.FAILURE);
			return resource;
		}
		
		//找cacheMap中是否缓存了
		Resource cacheResource = cacheMap.get().get(resource.getNetUrl());
		
		//如果缓存中有，那么要将之前的缓存返回
		if(cacheResource != null){
			//已经下载过了
			
		}else{
			//如果缓存中没有，那么进行缓存，将其下载下来
			downFile(resource);
			//将绝对路径其加入缓存
			cacheMap.get().put(resource.getNetUrl(), resource);
			//将其原始引用路径加入缓存
			//cacheMap.put(resource.getOriginalUrl(), resource);
			
		}
		resource.setResult(GlobalConst.SUCCESS);
		return resource;
	}
	
	/**
	 *  缓存到本地磁盘，文件下载
	 */
	public static void downFile(Resource resource){
		try {
			FileUtil.downFiles(resource.getNetUrl(), resource.getLocalUrl());
		}catch(FileNotFoundException notFind){
			System.err.println("404 File Not Found !  "+resource.getNetUrl());
			notFind.printStackTrace();
		}catch(NullPointerException nullE){
			System.err.println("downFile NULL----- "+resource.getNetUrl());
			nullE.printStackTrace();
		}catch (Exception e) {
			System.err.println(e.getMessage()+" --- "+resource.getNetUrl());
			e.printStackTrace();
		}
	}
	
}
