package com.mapleLeaf.common.util;




public class StrUtil {
	
	
	/**
	 * 将空格等字符去除，用于字符比对
	 * @param text
	 * @return
	 */
	public static String purification(String text){
		return text.replaceAll("\\s*", "");
	}
	
	/**
	 * 判断传入的字符串是否全部都是汉字、英文、数字。如果有某个字符不是，那么都会返回false
	 * @param str 要判断的字符串
	 * @return true：字符串的字符全部都在汉字、英文、数字中
	 */
	public static boolean isChinese(String str){
		return str.matches("[\\u4e00-\\u9fa5A-Za-z0-9]+");
	}
	
	/**
	 * 正则替换
	 * @param text 操作的内容源，主体
	 * @param regex 替换掉的
	 * @param replacement 替换成新的，取而代之的
	 * @return 提花好的内容
	 */
	public static String replaceAll(String text, String regex, String replacement){
		String s[] = {"?","(",")"}; 
		for (int i = 0; i < s.length; i++) {
			regex = regex.replaceAll("\\"+s[i], "\\\\"+s[i]);
		}
		text = text.replaceAll(regex, replacement);
		
		return text;
	}
	
	
	
	/**
	 * 从给定的字符串中截取想要的指定字符
	 * @param sourceString 源字符串，要切割的字符串
	 * @param startString 匹配的开始点字符
	 * 				<li>若为null或者""表示从头开始匹配
	 * 				<li>若是没找到开始点字符串，默认为从最开始匹配
	 * @param endString 匹配的结束点字符
	 * 				<li>若为null或者""表示匹配到末尾
	 * 				<li>若是没找到结束点字符串，默认为匹配到最末尾
	 * @param matchType 此项是针对结尾的匹配,可传入：
	 * 				<li>1:开始匹配找到的第一个，结束匹配找到的最后一个。
	 * 				<li>2:开始匹配找到的第一个，结束匹配：找到的开始位置往后的第一个。
	 * 				<li>3.开始匹配找到的最后一个，结束匹配找到的最后一个。
	 * 				<li>4:开始匹配找到的最后一个，结束匹配：找到的开始位置往后的第一个。
	 * @return 截取的字符串,若是传入了但是没找到开始或者结束字符则返回null
	 */
	public static String subString(String sourceString,String startString,String endString,int matchType){
		//开始点
		int start=0;
		if(!(startString==null||startString.length()==0)){
			if(matchType==1||matchType==2){
				start=sourceString.indexOf(startString);
			}else{
				start=sourceString.lastIndexOf(startString);
			}
			
			if(start<0){
				//没有找到，则定为0，从最开始处截取
				start=0;
			}else{
				//不截取传入的字符，从其后开始截取
				start=start+startString.length();
			}
		}
		
		//结束点
		int end=0;
		if(!(endString==null||endString.length()==0)){
			if(matchType==1||matchType==3){
				end=sourceString.lastIndexOf(endString);
				if(end<0){
					//没有找到，则定为－1，方法返回null
					end=-1;
				}
			}else{
				String xnx3_string;
				if(start>-1){
					xnx3_string=sourceString.substring(start);
				}else{
					xnx3_string = sourceString;
				}
				
				end=xnx3_string.indexOf(endString);
				if(end<0){
					end=0;
				}
				end=end+start;
			}
		}else{
			end=sourceString.length();
		}

		if(start==-1||end==-1){
			return null;
		}else{
			return sourceString.substring(start,end);
		}
	}
	public static String subString(String sourceString,String startString,String endString){
		return subString(sourceString, startString, endString, 1);
	}
	
	/**
	 * 字符型转换为整数型
	 * @param param 待转换的字符串
	 * @param defaultValue 异常后的返回值，默认值
	 * @return 整数
	 */
	public static int stringToInt(String param,int defaultValue){
		int xnx3_result=0;
		
		//首先判断字符串不能为空
		if(param==null||param.equalsIgnoreCase("null")){
			xnx3_result=defaultValue;
		}else{
			try {
				xnx3_result=Integer.parseInt(param);
			} catch (Exception e) {
				xnx3_result=defaultValue;
			}
		}
		
		return xnx3_result;
	}
	/**
	 * 字符型转换为Float型
	 * @param param 待转换的字符串
	 * @param defaultValue 异常后的返回值
	 * @return float
	 */
	public static float stringToFloat(String param,float defaultValue){
		float xnx3_result=0.0f;
		
		//首先判断字符串不能为空
		if(param==null||param.equalsIgnoreCase("null")){
			xnx3_result=defaultValue;
		}else{
			try {
				xnx3_result=Float.parseFloat(param);
			} catch (Exception e) {
				xnx3_result=defaultValue;
			}
		}
		
		return xnx3_result;
	}
}
