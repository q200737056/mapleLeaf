package com.mapleLeaf.code.utils;

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
    public static String convertToFirstLetterLowerCaseCamelCase(String str) {  
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
    /* 
     * 将数据库的数据类型转换为java的数据类型 
     */  
    public static String convertType(String databaseType) {  
        String javaType = "";  
          
        String databaseTypeStr = databaseType.trim().toLowerCase();
        if(databaseTypeStr.startsWith("int")
        		||databaseTypeStr.equals("smallint")
        		|| databaseTypeStr.equals("tinyint")
        		) {  
            javaType = "Integer";  
        } else if(databaseTypeStr.equals("char")) {  
            javaType = "String";  
        } else if(databaseTypeStr.equals("number") 
        		|| databaseTypeStr.equals("numeric")
        		) {  
            javaType = "Integer";  
        } else if(databaseTypeStr.indexOf("varchar")!=-1) {  
            javaType = "String";  
        } else if(databaseTypeStr.equals("blob")) {  
            javaType = "Byte[]";  
        } else if(databaseTypeStr.equals("float")) {  
            javaType = "Float";  
        } else if(databaseTypeStr.equals("double")) {  
            javaType = "Double";  
        } else if(databaseTypeStr.equals("decimal")) {  
            //javaType = "java.math.BigDecimal";
            javaType = "BigDecimal";
        } else if(databaseTypeStr.startsWith("bigint")) {  
            javaType = "Long";  
        } else if(databaseTypeStr.equals("date")) {  
            javaType = "Date";  
        } else if(databaseTypeStr.equals("time")) {  
            javaType = "Date";  
        } else if(databaseTypeStr.equals("datetime")) {  
            javaType = "Date";  
        } else if(databaseTypeStr.startsWith("timestamp")) {  
            javaType = "Date";  
        } else if(databaseTypeStr.equals("year")) {  
            //javaType = "java.util.Date"; 
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
     * 转换jdbc的类型，主要用于mybatis中的数据字段类型
     * @param type
     * @return
     */
    public static String convertJdbcType(String type) {
    	type=type.replace(" UNSIGNED","");
    	if (type.equals("INT")) {
        	type="INTEGER";
        } else if (type.equals("TEXT")){
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
}
