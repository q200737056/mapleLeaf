package com.mapleLeaf.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;


public class CacheUtil {
	
	private static ThreadLocal<Map<String,Map<String ,Object>>> cacheMap = 
			new ThreadLocal<Map<String,Map<String ,Object>>>(){
		public Map<String,Map<String ,Object>> initialValue() {  
            return new HashMap<>();  
        }
	};
	
	/**
	 * 加入 缓存
	 * @param name 缓存名
	 * @param key 键
	 * @param object 值
	 */
	public static void addCache(String name,String key,Object object){
		Map<String,Object> cacheName = cacheMap.get().get(name);
		if(cacheName==null){
			cacheName = new HashMap<>();
			cacheName.put(key, object);
			cacheMap.get().put(name, cacheName);
		}else{
			cacheName.put(key, object);
		}
		
	}
	/**
	 * 得到 指定缓存值
	 * @param name
	 * @param key
	 * @return
	 */
	public static Object getValue(String name,String key){
		Map<String,Object> cacheName = cacheMap.get().get(name);
		if(cacheName==null){
			return null;
		}
		return cacheName.get(key);
	}
	/**
	 * 移除 指定缓存值 并 返回值
	 * @param name
	 * @param key
	 * @return
	 */
	public static Object removeValue(String name,String key){
		Map<String,Object> cacheName = cacheMap.get().get(name);
		if(cacheName==null){
			return null;
		}
		return cacheName.remove(key);
	}
	/**
	 * 移除 指定 缓存
	 * @param name
	 */
	public static void removeCache(String name){
		Map<String,Object> cacheName = cacheMap.get().get(name);
		if(cacheName==null){
			return;
		}
		cacheName.clear();
		cacheMap.get().remove(name);
	}
	/**
	 * 移除 所有 缓存
	 */
	public static void removeAll(){
		if(cacheMap.get().size()==0){
			return;
		}
		Set<Entry<String, Map<String,Object>>> set = cacheMap.get().entrySet();
		for(Map.Entry<String, Map<String,Object>> entry : set){
			entry.getValue().clear();
		}
		cacheMap.get().clear();
	}
} 
