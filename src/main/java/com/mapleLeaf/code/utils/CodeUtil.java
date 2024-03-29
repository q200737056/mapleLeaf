package com.mapleLeaf.code.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CodeUtil {
	
	
	/**
	 *  下划线 转换为首字母大写的驼峰命名 ,
	 * @param str
	 * @return
	 */
    public static String convertToCamelCase(String str) {  
        String result = "";
        if (str==null) {
        	return "";
        }
        String[] strArr = str.trim().split("_");  
        for(String s : strArr) {  
            if(s.length()>1) {  
                result += s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();  
            } else {  
                result += s.toUpperCase();  
            }  
        }  
  
        return result;  
    }
     /**
      *  下划线 转换为首字母小写的驼峰命名 
      * @param str
      * @return
      */
    public static String convertToFstLowerCamelCase(String str) {  
        String resultCamelCase = convertToCamelCase(str);  
  
        String result = "";
        if(resultCamelCase.length()>1) {  
            result = resultCamelCase.substring(0, 1).toLowerCase() + resultCamelCase.substring(1);  
        } else {  
            result = resultCamelCase.toLowerCase();  
        }  
          
        return result;  
    }
    /**
     * 首字母大写
     * @param 
     * @return
     */
    public static String converFirstUpper(String str){
    	if(isEmpty(str)){
    		return str;
    	}
    	return str.substring(0, 1).toUpperCase()+str.substring(1);
    }
    /**
     * 首字母小写
     * @param 
     * @return
     */
    public static String converFirstLower(String str){
    	if(isEmpty(str)){
    		return str;
    	}
    	return str.substring(0, 1).toLowerCase()+str.substring(1);
    }
    /**
     * 将数据库的数据类型转换为java的数据类型 
     * @param databaseType
     * @return
     */
    public static String convertType(String databaseType,boolean wrap) {  
        
       String databaseTypeStr = databaseType.toLowerCase().replace("unsigned","").trim();
       if(databaseTypeStr.startsWith("int")
       		||databaseTypeStr.equals("smallint")
       		|| databaseTypeStr.equals("tinyint")) {
       	if(wrap){
       		return "Integer";
       	}
           return "int";  
       } else if(databaseTypeStr.equals("char")||databaseTypeStr.indexOf("varchar")!=-1) {  
           return "String";  
       } else if(databaseTypeStr.equals("number") 
       		|| databaseTypeStr.equals("numeric")) {  
           return "BigDecimal";  
       } else if(databaseTypeStr.equals("blob")) {
       	if(wrap){
       		return "Byte[]";
       	}
           return "byte[]";  
       } else if(databaseTypeStr.equals("float")) { 
       	if(wrap){
       		return "Float";
       	}
           return "float";  
       } else if(databaseTypeStr.equals("double")) { 
       	if(wrap){
       		return "Double";
       	}
           return "double";  
       } else if(databaseTypeStr.equals("decimal")) {  
           return "BigDecimal";
       } else if(databaseTypeStr.startsWith("bigint")) {
       	if(wrap){
       		return "Long";
       	}
           return "long";  
       } else if(databaseTypeStr.equals("date")||databaseTypeStr.equals("time")
       		||databaseTypeStr.equals("datetime")||databaseTypeStr.startsWith("timestamp")
       		||databaseTypeStr.equals("year")) {  
           return "Date";  
       }  
         
       return "String";  
   }
    public static String convertClassType(String type){
    	String renType="";
    	if("Date".equals(type)){
    		renType="java.util.Date";
    	}else if("BigDecimal".equals(type)){
    		renType="java.math.BigDecimal";
    	}
    	return renType;
    }
    
    /**
     * 转换(mybatis的jdbcType,hibernate的映射)
     * @param type 数据库字段类型
     * @return
     */
    public static String convertJdbcType(String type,String ormType) {
    	ormType = ormType.toLowerCase();
    	if("other".equals(ormType)){
    		return type.toLowerCase();
    	}
    	if("mybatis".equals(ormType)){
    		type=type.toUpperCase().replace("UNSIGNED","").trim();
    		
        	if (type.equals("INT")) {
            	type="INTEGER";
            } else if (type.equals("TEXT")||type.startsWith("LONG VARCHAR")){
        		type="LONGVARCHAR";
        	} else if (type.equals("DATETIME")) {
        		type="DATE";
        	} else if (type.equals("VARCHAR2")) {
        		type="VARCHAR";
        	} else if (type.equals("NUMBER")) {
        		type="NUMERIC";
        	} else if (type.equals("NVARCHAR")) {
        		type="VARCHAR";
        	}
    	}else if("hibernate".equals(ormType)){
    		type=type.toLowerCase().replace("unsigned","").trim();
    		if(type.equals("tinyint")){
    			type="byte";
    		}else if(type.equals("smallint")){
    			type="short";
    		}else if(type.equals("bigint")){
    			type="long";
    		}else if(type.equals("numeric")){
    			type="big_decimal";
    		}else if(type.equals("datetime")){
    			type="date";
    		}else if(type.equals("varchar2")||type.equals("varchar")||type.equals("char")){
    			type="string";
    		}else if(type.equals("bit")){
    			type="boolean";
    		}else if(type.equals("blob")){
    			type="binary";
    		}else if(type.equals("clob")){
    			type="text";
    		}
    	}
    	//数据库字段类型 对应框架的字段类型名一样的话， 就原样输出
    	return type;
    }
    
    public static boolean isEmpty(String str) {
    	if (str==null) {
    		return true;
    	}
    	if (str.trim().length()==0) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean existsType(List<String> list , String type) {
    	return list.contains(convertClassType(type));
    }
    /**
     * 判断是否是主键
     * @param priCols 主键列表
     * @param columnName 要判断的列名
     * @return
     */
    public static boolean isPrimaryKey(List<String> priCols,String columnName){
    	for (String pri : priCols) {
    		if (pri.equalsIgnoreCase(columnName)) {
    			return true;
    		}
    	}
    	return false;
    }
    /**
     * 对 k1 separ v1,k2 separ v2形式的字符串进行 切割
     * @param data 比如k1=v1,k2=v2
     * @param separ 分隔符
     * @return
     */
    public static Map<String,String> splitKeyVal(String data,String separ){
    	if(isEmpty(data)){
    		return null;
    	}
    	Map<String,String> dataMap = new LinkedHashMap<>();
    	String[] dataArr = data.trim().replace("，", ",").split(",");
    	for(String item : dataArr){
    		String[] itemArr = item.split(separ);
    		if(itemArr.length!=2){
    			return null;
    		}
    		dataMap.put(itemArr[0].trim(), itemArr[1].trim());
    	}
    	return dataMap;
    }
    /**
     * 判断 字符串数组中 是否存在 指定字符串，不区分大小写
     * @param per
     * @return
     */
    public static boolean checkStrArray(String[] arr,String str){
    	for(String s: arr){
    		if(s.equalsIgnoreCase(str)){
    			return true;
    		}
    	}
    	return false;
    }
    /**
     * 判断 字符串数组中 是否存在 指定字符串，不区分大小写
     * @param per
     * @return 索引
     */
    public static int checkStrArrayIdx(String[] arr,String str){
    	for(int i=0;i<arr.length;i++){
    		String s = arr[i];
    		if(s!=null && s.trim().equalsIgnoreCase(str)){
    			return i;
    		}
    	}
    	return -1;
    }
    /**
     * map中的value 放入到 list集合中
     * @param map
     * @param list
     */
    public static <T> void  mapvalToList(Map<String,T> map,List<T> list){
    	for(Map.Entry<String, T> entry : map.entrySet() ){
			 list.add(entry.getValue());
		}
    }
}
