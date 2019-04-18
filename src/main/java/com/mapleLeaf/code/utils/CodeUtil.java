package com.mapleLeaf.code.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

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
    	return str.substring(0, 1).toUpperCase()+str.substring(1);
    }
    
    /**
     * 将数据库的数据类型转换为java的数据类型 
     * @param databaseType
     * @return
     */
    public static String convertType(String databaseType) {  
        String javaType = "";  
          
        String databaseTypeStr = databaseType.toLowerCase().replace("unsigned","").trim();
        if(databaseTypeStr.startsWith("int")
        		||databaseTypeStr.equals("smallint")
        		|| databaseTypeStr.equals("tinyint")) {  
            javaType = "Integer";  
        } else if(databaseTypeStr.equals("char")||databaseTypeStr.indexOf("varchar")!=-1) {  
            javaType = "String";  
        } else if(databaseTypeStr.equals("number") 
        		|| databaseTypeStr.equals("numeric")) {  
            javaType = "BigDecimal";  
        } else if(databaseTypeStr.equals("blob")) {  
            javaType = "Byte[]";  
        } else if(databaseTypeStr.equals("float")) {  
            javaType = "Float";  
        } else if(databaseTypeStr.equals("double")) {  
            javaType = "Double";  
        } else if(databaseTypeStr.equals("decimal")) {  
            javaType = "BigDecimal";
        } else if(databaseTypeStr.startsWith("bigint")) {  
            javaType = "Long";  
        } else if(databaseTypeStr.equals("date")||databaseTypeStr.equals("time")
        		||databaseTypeStr.equals("datetime")||databaseTypeStr.startsWith("timestamp")
        		||databaseTypeStr.equals("year")) {  
            javaType = "Date";  
        } else {
            javaType = "String";  
        }  
          
        return javaType;  
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
     * @param type
     * @return
     */
    public static String convertJdbcType(String type,String ormType) {
    	if("mybatis".equals(ormType.toLowerCase())){
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
    	}else if("hibernate".equals(ormType.toLowerCase())){
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
     * 读取文件内容到一个字符串中
     * @param file
     * @return
     */
	private static String readFile(File file) {
		StringBuffer result = new StringBuffer();
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result.append(s);
				result.append("\n");
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.toString();
	}
}
